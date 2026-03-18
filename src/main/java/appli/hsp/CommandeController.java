package appli.hsp;

import appli.StartApplication;
import appli.SessionManager;
import appli.hsp.utils.NavbarHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import modele.Demande;
import modele.FicheProduit;
import modele.User;
import repository.DemandeProduitRepository;
import repository.DemandeRepository;
import repository.FicheProduitRepository;
import repository.UserRepository;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeController {

    public static class LigneProduitUI {
        private final FicheProduit produit;
        private final int quantite;

        public LigneProduitUI(FicheProduit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
        }

        public FicheProduit getProduit() {
            return produit;
        }

        public int getQuantite() {
            return quantite;
        }

        public String getProduitLibelle() {
            return produit != null ? produit.getLibelle() : "";
        }

        public int getProduitId() {
            return produit != null ? produit.getIdProduit() : 0;
        }
    }

    @FXML
    private TextField rechercheField;

    @FXML
    private TableView<Demande> commandesTable;

    @FXML
    private TableColumn<Demande, Integer> idColumn;

    @FXML
    private TableColumn<Demande, String> dateColumn;

    @FXML
    private TableColumn<Demande, String> patientColumn;

    @FXML
    private TableColumn<Demande, String> roleColumn;


    @FXML
    private TableColumn<Demande, String> statutColumn;

    @FXML
    private TableColumn<Demande, Void> actionsColumn;

    @FXML
    private VBox detailsPane;

    @FXML
    private Label numeroLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label patientLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label medecinLabel;

    @FXML
    private Label statutLabel;

    @FXML
    private TableView<repository.DemandeProduitRepository.ProduitCommande> produitsTable;

    @FXML
    private TableColumn<repository.DemandeProduitRepository.ProduitCommande, String> produitNomColumn;


    @FXML
    private TableColumn<repository.DemandeProduitRepository.ProduitCommande, Integer> produitQuantiteDemandeeColumn;

    private final DemandeRepository demandeRepository = new DemandeRepository();
    private final FicheProduitRepository ficheProduitRepository = new FicheProduitRepository();
    private final DemandeProduitRepository demandeProduitRepository = new DemandeProduitRepository();
    private final UserRepository userRepository = new UserRepository();

    private final Map<Integer, String> userLabelCache = new HashMap<>();
    private final Map<Integer, String> userRoleCache = new HashMap<>();
    private final Map<Integer, Integer> nbProduitsCache = new HashMap<>();

    private final ObservableList<Demande> demandesObservable = FXCollections.observableArrayList();
    private Demande demandeSelectionnee;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavTickets;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavUtilisateurs;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, btnNavTickets, null, null, btnNavPlanning, btnNavCatalogue, null, btnNavUtilisateurs, null);
        // Initialisation du contrôleur
        System.out.println("Page commande initialisée");
        
        // Test du contenu de la base de données pour diagnostiquer
        demandeProduitRepository.testDatabaseContent();
        
        // Configuration initiale du tableau
        setupTable();
        
        // Masquer le panneau de détails au démarrage
        detailsPane.setVisible(false);

        commandesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            demandeSelectionnee = newVal;
            if (newVal != null) {
                System.out.println("Ligne sélectionnée: " + newVal.getIdDemande());
                afficherDetailsCommande(newVal);
            }
        });
        
        // Forcer un premier chargement
        System.out.println("Premier chargement des commandes...");
        loadCommandes();
    }

    private void setupTable() {
        System.out.println("Configuration du tableau...");
        
        // Vérifier que le TableView est bien initialisé
        if (commandesTable == null) {
            System.err.println("ERREUR: commandesTable est null!");
            return;
        }
        
        // Vérifier que toutes les colonnes sont bien présentes
        System.out.println("Colonnes du TableView:");
        for (TableColumn<Demande, ?> col : commandesTable.getColumns()) {
            System.out.println("  - " + col.getId() + " (" + col.getText() + ")");
        }
        
        // Vérifier que les champs FXML sont bien injectés
        System.out.println("Vérification des champs FXML:");
        System.out.println("  idColumn: " + (idColumn != null ? "OK" : "NULL"));
        System.out.println("  dateColumn: " + (dateColumn != null ? "OK" : "NULL"));
        
        // Configuration des colonnes du tableau
        idColumn.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        dateColumn.setStyle("-fx-alignment: CENTER;");
        patientColumn.setStyle("-fx-alignment: CENTER;");
        roleColumn.setStyle("-fx-alignment: CENTER;");
        statutColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.setStyle("-fx-alignment: CENTER;");

        idColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null) {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
            return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdDemande()).asObject();
        });
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null || cellData.getValue().getDateDemande() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        });
        // Afficher les informations réelles des demandes
        patientColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            return new javafx.beans.property.SimpleStringProperty(getUserLabel(cellData.getValue().getIdUser()));
        });
        roleColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            return new javafx.beans.property.SimpleStringProperty(getUserRole(cellData.getValue().getIdUser()));
        });
        statutColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue() != null ? cellData.getValue().getStatut() : ""
            )
        );

        idColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(item));
                setTextFill(Color.web("#111827"));
            }
        });

        dateColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(Color.web("#111827"));
            }
        });

        patientColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(Color.web("#111827"));
            }
        });

        roleColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(Color.web("#111827"));
            }
        });


        statutColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(Color.web("#111827"));
            }
        });

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button voirBtn = new Button("👁️ Voir");

            {
                voirBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 15; -fx-border-radius: 20; -fx-border-color: transparent;");
                voirBtn.setOnMouseEntered(e -> voirBtn.setStyle("-fx-background-color: linear-gradient(to right, #764ba2, #667eea); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 15; -fx-border-radius: 20; -fx-border-color: transparent; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
                voirBtn.setOnMouseExited(e -> voirBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 15; -fx-border-radius: 20; -fx-border-color: transparent;"));
                voirBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    afficherDetailsCommande(d);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : voirBtn);
            }
        });

        commandesTable.setItems(demandesObservable);

        // Style simple et lisible pour garantir l'affichage (évite les styles/rowFactory trop intrusifs)
        commandesTable.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        // Configuration du tableau des produits dans les détails
        produitNomColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null || cellData.getValue().getProduit() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduit().getLibelle());
        });
        
        
        produitQuantiteDemandeeColumn.setCellValueFactory(cellData -> {
            System.out.println("produitQuantiteDemandeeColumn cellValueFactory appelé");
            if (cellData.getValue() == null) {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
            int quantite = cellData.getValue().getQuantite();
            System.out.println("  -> Qté demandée: " + quantite);
            return new javafx.beans.property.SimpleIntegerProperty(quantite).asObject();
        });

        // Forcer le rafraîchissement du tableau
        commandesTable.refresh();
        
        loadCommandes();
    }

    private void loadCommandes() {
        System.out.println("Chargement des demandes...");
        try {
            List<Demande> demandes = demandeRepository.getAllDemandes();
            System.out.println("Nombre de demandes trouvées: " + demandes.size());
            
            // Afficher les détails de chaque demande pour debug
            for (int i = 0; i < demandes.size(); i++) {
                Demande d = demandes.get(i);
                System.out.println("Demande " + i + ": ID=" + d.getIdDemande() + ", User=" + d.getIdUser() + ", Date=" + d.getDateDemande() + ", Qté=" + d.getQuantite());
            }
            
            demandesObservable.clear();
            demandesObservable.addAll(demandes);
            
            System.out.println("ObservableList size after loading: " + demandesObservable.size());
            System.out.println("TableView items size: " + commandesTable.getItems().size());
            
            // Forcer le rafraîchissement complet
            commandesTable.refresh();
            commandesTable.layout();
            commandesTable.autosize();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des demandes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleRetour(ActionEvent event) {
        try {
            StartApplication.changeScene("pageAccueil");
            System.out.println("Retour à la page d'accueil");
            
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à la page d'accueil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleRecherche(ActionEvent event) {
        String rechercheText = rechercheField.getText();
        System.out.println("Recherche: " + rechercheText);

        if (rechercheText == null || rechercheText.trim().isEmpty()) {
            commandesTable.setItems(demandesObservable);
            return;
        }

        String r = rechercheText.trim();
        ObservableList<Demande> filtered = FXCollections.observableArrayList();
        for (Demande d : demandesObservable) {
            if (String.valueOf(d.getIdDemande()).contains(r) || String.valueOf(d.getIdUser()).contains(r)) {
                filtered.add(d);
            }
        }
        commandesTable.setItems(filtered);
    }

    @FXML
    void handleNouvelleCommande(ActionEvent event) {
        System.out.println("Création d'une nouvelle demande");

        try {
            List<FicheProduit> produits = ficheProduitRepository.getAllFicheProduits();
            if (produits.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun produit");
                alert.setHeaderText(null);
                alert.setContentText("Aucun produit n'est disponible dans fiche_produit.");
                alert.showAndWait();
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Nouvelle demande de produit");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            ObservableList<LigneProduitUI> lignes = FXCollections.observableArrayList();

            TableView<LigneProduitUI> lignesTable = new TableView<>(lignes);
            lignesTable.setPrefHeight(220);

            TableColumn<LigneProduitUI, Integer> prodIdCol = new TableColumn<>("ID Produit");
            prodIdCol.setPrefWidth(90);
            prodIdCol.setCellValueFactory(new PropertyValueFactory<>("produitId"));

            TableColumn<LigneProduitUI, String> prodLibCol = new TableColumn<>("Produit");
            prodLibCol.setPrefWidth(260);
            prodLibCol.setCellValueFactory(new PropertyValueFactory<>("produitLibelle"));

            TableColumn<LigneProduitUI, Integer> qteCol = new TableColumn<>("Quantité");
            qteCol.setPrefWidth(100);
            qteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));

            lignesTable.getColumns().addAll(prodIdCol, prodLibCol, qteCol);

            Button ajouterLigneBtn = new Button("Ajouter produit");
            Button supprimerLigneBtn = new Button("Supprimer ligne");
            supprimerLigneBtn.disableProperty().bind(lignesTable.getSelectionModel().selectedItemProperty().isNull());

            ajouterLigneBtn.setOnAction(e -> {
                Dialog<ButtonType> add = new Dialog<>();
                add.setTitle("Ajouter un produit");
                add.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                ComboBox<FicheProduit> produitCombo = new ComboBox<>(FXCollections.observableArrayList(produits));
                produitCombo.setValue(produits.get(0));
                produitCombo.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(FicheProduit item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : (item.getLibelle() + " (ID: " + item.getIdProduit() + ")"));
                    }
                });
                produitCombo.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(FicheProduit item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.getLibelle());
                    }
                });

                Spinner<Integer> quantiteSpinner = new Spinner<>(1, 10000, 1);
                quantiteSpinner.setEditable(true);

                GridPane g = new GridPane();
                g.setHgap(10);
                g.setVgap(10);
                g.add(new Label("Produit:"), 0, 0);
                g.add(produitCombo, 1, 0);
                g.add(new Label("Quantité:"), 0, 1);
                g.add(quantiteSpinner, 1, 1);
                add.getDialogPane().setContent(g);

                add.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.OK) {
                        FicheProduit p = produitCombo.getValue();
                        Integer q = quantiteSpinner.getValue();
                        if (p != null && q != null && q > 0) {
                            lignes.add(new LigneProduitUI(p, q));
                        }
                    }
                });
            });

            supprimerLigneBtn.setOnAction(e -> {
                LigneProduitUI selected = lignesTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    lignes.remove(selected);
                }
            });

            HBox actions = new HBox(10, ajouterLigneBtn, supprimerLigneBtn);
            VBox root = new VBox(10, new Label("Produits demandés:"), lignesTable, actions);
            VBox.setVgrow(lignesTable, Priority.ALWAYS);
            HBox.setHgrow(actions, Priority.ALWAYS);

            dialog.getDialogPane().setContent(root);

            dialog.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    int idUser = (SessionManager.estConnecte() ? SessionManager.getUtilisateurConnecte().getIdUser() : 1);

                    if (lignes.isEmpty()) {
                        Alert ko = new Alert(Alert.AlertType.WARNING);
                        ko.setTitle("Aucun produit");
                        ko.setHeaderText(null);
                        ko.setContentText("Ajoute au moins un produit à la demande.");
                        ko.showAndWait();
                        return;
                    }

                    List<DemandeProduitRepository.LigneDemandeProduit> aInserer = new java.util.ArrayList<>();
                    for (LigneProduitUI l : lignes) {
                        if (l != null && l.getProduit() != null && l.getQuantite() > 0) {
                            aInserer.add(new DemandeProduitRepository.LigneDemandeProduit(l.getProduit().getIdProduit(), l.getQuantite()));
                        }
                    }

                    int idDemande = demandeProduitRepository.creerDemandeAvecProduits(idUser, aInserer);
                    if (idDemande > 0) {
                        Alert ok = new Alert(Alert.AlertType.INFORMATION);
                        ok.setTitle("Demande envoyée");
                        ok.setHeaderText(null);
                        ok.setContentText("Demande créée avec l'ID: " + idDemande);
                        ok.showAndWait();
                        loadCommandes();
                    } else {
                        Alert ko = new Alert(Alert.AlertType.ERROR);
                        ko.setTitle("Erreur");
                        ko.setHeaderText(null);
                        ko.setContentText("Impossible de créer la demande.");
                        ko.showAndWait();
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("Erreur création demande: " + e.getMessage());
        }
    }

    @FXML
    void handleRafraichir(ActionEvent event) {
        // TODO: Rafraîchir la liste des commandes
        System.out.println("Rafraîchissement de la liste");
        loadCommandes();
    }

    @FXML
    void handleModifier(ActionEvent event) {
        // La quantité totale d'une demande est calculée depuis les lignes demande_produit.
        // La modifier directement ici créerait une incohérence avec la table demande_produit.
        // Pour modifier une demande, il faut la supprimer et en créer une nouvelle.
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Modification non disponible");
        info.setHeaderText(null);
        info.setContentText("Pour modifier une demande, supprimez-la et créez-en une nouvelle.");
        info.showAndWait();
    }

    @FXML
    void handleSupprimer(ActionEvent event) {
        System.out.println("Suppression de la demande");

        if (demandeSelectionnee == null) {
            return;
        }
        
        // Demander une confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette demande ?");
        alert.setContentText("Cette action est irréversible.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (demandeRepository.supprimerDemande(demandeSelectionnee.getIdDemande())) {
                    detailsPane.setVisible(false);
                    loadCommandes();
                }
            }
        });
    }

    @FXML
    public void versAccueil(ActionEvent event) {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers l'accueil: " + e.getMessage());
        }
    }

    @FXML
    public void versPatients(ActionEvent event) {
        try {
            StartApplication.changeScene("patientsView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers patients: " + e.getMessage());
        }
    }

    @FXML
    public void versDossiers(ActionEvent event) {
        try {
            StartApplication.changeScene("dossierEnChargeView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers dossiers: " + e.getMessage());
        }
    }

    @FXML
    public void versCommandes(ActionEvent event) {
        System.out.println("Déjà sur la page commandes");
    }

    @FXML
    public void versTickets(ActionEvent event) {
        appli.hsp.utils.NavigationHelper.versTickets();
    }

    @FXML
    public void versPlanning(ActionEvent event) {
        try {
            StartApplication.changeScene("planningView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers planning: " + e.getMessage());
        }
    }

    @FXML
    public void versFicheProduit(ActionEvent event) {
        try {
            StartApplication.changeScene("ficheProduitView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers catalogue: " + e.getMessage());
        }
    }

    @FXML
    public void versUtilisateurs(ActionEvent event) {
        try {
            StartApplication.changeScene("pageUtilisateurs");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    public void versMonEspace(ActionEvent event) {
        try {
            StartApplication.changeScene("pageMonEspace");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers mon espace: " + e.getMessage());
        }
    }

    @FXML
    public void deconnexion(ActionEvent event) {
        try {
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    /**
     * Affiche les détails d'une commande sélectionnée
     */
    public void afficherDetailsCommande(Object commande) {
        if (!(commande instanceof Demande d)) {
            return;
        }
        System.out.println("Affichage détails pour demande ID: " + d.getIdDemande());
        detailsPane.setVisible(true);

        numeroLabel.setText(String.valueOf(d.getIdDemande()));
        dateLabel.setText(d.getDateDemande() != null ? d.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        patientLabel.setText(getUserLabel(d.getIdUser()));
        roleLabel.setText(getUserRole(d.getIdUser()));
        medecinLabel.setText("Total: " + d.getQuantite() + " unités");
        statutLabel.setText(d.getStatut());

        // Charger et afficher les produits de la commande
        System.out.println("Chargement des produits pour la demande " + d.getIdDemande());
        List<repository.DemandeProduitRepository.ProduitCommande> produits = demandeProduitRepository.getProduitsByDemande(d.getIdDemande());
        System.out.println("Produits récupérés: " + produits.size());
        
        ObservableList<repository.DemandeProduitRepository.ProduitCommande> produitsObservable = FXCollections.observableArrayList(produits);
        produitsTable.setItems(produitsObservable);
        produitsTable.refresh();
        
        System.out.println("Tableau des produits mis à jour avec " + produitsTable.getItems().size() + " éléments");
    }

    private String getUserLabel(int idUser) {
        if (idUser <= 0) {
            return "";
        }

        String cached = userLabelCache.get(idUser);
        if (cached != null) {
            return cached;
        }

        try {
            User u = userRepository.trouverUtilisateurParId(idUser);
            String label;
            if (u == null) {
                label = "Utilisateur #" + idUser;
            } else {
                String nom = (u.getNom() == null ? "" : u.getNom().trim());
                String prenom = (u.getPrenom() == null ? "" : u.getPrenom().trim());
                label = (nom + " " + prenom).trim();
                if (label.isEmpty()) {
                    label = "Utilisateur #" + idUser;
                }
            }
            userLabelCache.put(idUser, label);
            return label;
        } catch (Exception e) {
            String label = "Utilisateur #" + idUser;
            userLabelCache.put(idUser, label);
            return label;
        }
    }

    private String getUserRole(int idUser) {
        if (idUser <= 0) {
            return "";
        }

        String cached = userRoleCache.get(idUser);
        if (cached != null) {
            return cached;
        }

        try {
            User u = userRepository.trouverUtilisateurParId(idUser);
            String role = (u == null || u.getRole() == null) ? "" : u.getRole().trim();
            if (role.isEmpty()) {
                role = "-";
            }
            userRoleCache.put(idUser, role);
            return role;
        } catch (Exception e) {
            String role = "-";
            userRoleCache.put(idUser, role);
            return role;
        }
    }

    private int getNbProduits(int idDemande) {
        if (idDemande <= 0) {
            return 0;
        }

        Integer cached = nbProduitsCache.get(idDemande);
        if (cached != null) {
            System.out.println("Nb produits pour demande " + idDemande + " (cache): " + cached);
            return cached;
        }

        try {
            System.out.println("Calcul nb produits pour demande " + idDemande);
            List<repository.DemandeProduitRepository.ProduitCommande> produits = demandeProduitRepository.getProduitsByDemande(idDemande);
            int nb = produits.size();
            System.out.println("Nb produits calculé pour demande " + idDemande + ": " + nb);
            nbProduitsCache.put(idDemande, nb);
            return nb;
        } catch (Exception e) {
            System.err.println("Erreur getNbProduits pour demande " + idDemande + ": " + e.getMessage());
            e.printStackTrace();
            nbProduitsCache.put(idDemande, 0);
            return 0;
        }
    }
}
