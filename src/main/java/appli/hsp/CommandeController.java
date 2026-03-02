package appli.hsp;

import appli.StartApplication;
import appli.SessionManager;
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
import modele.Demande;
import modele.FicheProduit;
import repository.DemandeProduitRepository;
import repository.DemandeRepository;
import repository.FicheProduitRepository;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private TableColumn<Demande, Integer> patientColumn;

    @FXML
    private TableColumn<Demande, Integer> medecinColumn;

    @FXML
    private TableColumn<Demande, Integer> statutColumn;

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
    private Label medecinLabel;

    @FXML
    private Label statutLabel;

    private final DemandeRepository demandeRepository = new DemandeRepository();
    private final FicheProduitRepository ficheProduitRepository = new FicheProduitRepository();
    private final DemandeProduitRepository demandeProduitRepository = new DemandeProduitRepository();

    private final ObservableList<Demande> demandesObservable = FXCollections.observableArrayList();
    private Demande demandeSelectionnee;

    @FXML
    public void initialize() {
        // Initialisation du contrôleur
        System.out.println("Page commande initialisée");
        
        // Configuration initiale du tableau
        setupTable();
        
        // Masquer le panneau de détails au démarrage
        detailsPane.setVisible(false);

        commandesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            demandeSelectionnee = newVal;
            if (newVal != null) {
                afficherDetailsCommande(newVal);
            }
        });
    }

    private void setupTable() {
        // Configuration des colonnes du tableau
        idColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setStyle("-fx-alignment: CENTER;");
        patientColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        medecinColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        statutColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.setStyle("-fx-alignment: CENTER;");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idDemande"));
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null || cellData.getValue().getDateDemande() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        });
        // `commandeView.fxml` est générique (patient/médecin/statut), on l'utilise pour afficher des infos utiles
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        medecinColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("idDemande"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button voirBtn = new Button("Voir");

            {
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

        loadCommandes();
    }

    private void loadCommandes() {
        System.out.println("Chargement des demandes...");
        try {
            List<Demande> demandes = demandeRepository.getAllDemandes();
            demandesObservable.clear();
            demandesObservable.addAll(demandes);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des demandes: " + e.getMessage());
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
        if (demandeSelectionnee == null) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(demandeSelectionnee.getQuantite()));
        dialog.setTitle("Modifier quantité");
        dialog.setHeaderText(null);
        dialog.setContentText("Nouvelle quantité:");
        dialog.showAndWait().ifPresent(val -> {
            try {
                int qte = Integer.parseInt(val.trim());
                demandeSelectionnee.setQuantite(qte);
                if (demandeRepository.modifierDemande(demandeSelectionnee)) {
                    loadCommandes();
                }
            } catch (NumberFormatException ignored) {
            }
        });
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
        // Déjà sur la page commandes
        System.out.println("Déjà sur la page commandes");
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
        detailsPane.setVisible(true);

        numeroLabel.setText(String.valueOf(d.getIdDemande()));
        dateLabel.setText(d.getDateDemande() != null ? d.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        patientLabel.setText(String.valueOf(d.getIdUser()));
        medecinLabel.setText(String.valueOf(d.getQuantite()));
        statutLabel.setText("Demande produit");
    }
}
