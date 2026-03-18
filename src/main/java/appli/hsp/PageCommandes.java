package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modele.Commande;
import modele.Demande;
import modele.Fournisseur;
import repository.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PageCommandes implements Initializable {

    // --- Onglet Demandes ---
    @FXML private TableView<Demande> demandesTable;
    @FXML private TableColumn<Demande, Integer> demIdCol;
    @FXML private TableColumn<Demande, String> demDateCol;
    @FXML private TableColumn<Demande, String> demInfirmierCol;
    @FXML private TableColumn<Demande, Integer> demQuantiteCol;
    @FXML private TableColumn<Demande, String> demStatutCol;
    @FXML private TableColumn<Demande, Void> demActionsCol;

    // --- Onglet Commandes ---
    @FXML private TableView<Commande> commandesTable;
    @FXML private TableColumn<Commande, Integer> cmdIdCol;
    @FXML private TableColumn<Commande, String> cmdDateCol;
    @FXML private TableColumn<Commande, String> cmdFournisseurCol;
    @FXML private TableColumn<Commande, String> cmdLibelleCol;
    @FXML private TableColumn<Commande, String> cmdStatutCol;
    @FXML private TableColumn<Commande, Void> cmdActionsCol;

    private final DemandeRepository demandeRepo = new DemandeRepository();
    private final CommandeRepository commandeRepo = new CommandeRepository();
    private final FournisseurRepository fournisseurRepo = new FournisseurRepository();
    private final DemandeProduitRepository demandeProduitRepo = new DemandeProduitRepository();
    private final CommandeProduitRepository commandeProduitRepo = new CommandeProduitRepository();
    private final UserRepository userRepo = new UserRepository();

    private final ObservableList<Demande> demandesData = FXCollections.observableArrayList();
    private final ObservableList<Commande> commandesData = FXCollections.observableArrayList();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavUtilisateurs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, null, null, null, btnNavPlanning, btnNavCatalogue, null, btnNavUtilisateurs, null);
        setupDemandesTable();
        setupCommandesTable();
        chargerDemandes();
        chargerCommandes();
    }

    // ======================== SETUP TABLES ========================

    private void setupDemandesTable() {
        demIdCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdDemande()).asObject());
        demDateCol.setCellValueFactory(c -> {
            if (c.getValue().getDateDemande() == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(c.getValue().getDateDemande().format(FMT));
        });
        demInfirmierCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getUserLabel(c.getValue().getIdUser())));
        demQuantiteCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        demStatutCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut()));

        demActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button creerBtn = new Button("Créer commande");

            {
                creerBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 6 12;");
                creerBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    ouvrirDialogCreerCommande(d);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Demande d = getTableView().getItems().get(getIndex());
                    boolean traitee = "Traitée".equals(d.getStatut());
                    creerBtn.setDisable(traitee);
                    creerBtn.setOpacity(traitee ? 0.4 : 1.0);
                    setGraphic(creerBtn);
                }
            }
        });

        demandesTable.setItems(demandesData);
    }

    private void setupCommandesTable() {
        cmdIdCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdCommande()).asObject());
        cmdDateCol.setCellValueFactory(c -> {
            if (c.getValue().getDateCommande() == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(c.getValue().getDateCommande().format(FMT));
        });
        cmdFournisseurCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getFournisseurNom(c.getValue().getIdFournisseur())));
        cmdLibelleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLibelle()));
        cmdStatutCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut()));

        cmdActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button voirBtn = new Button("Voir");
            private final Button statutBtn = new Button("Statut");
            private final HBox box = new HBox(6, voirBtn, statutBtn);

            {
                voirBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");
                statutBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");

                voirBtn.setOnAction(e -> {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    afficherDetailsCommande(cmd);
                });
                statutBtn.setOnAction(e -> {
                    Commande cmd = getTableView().getItems().get(getIndex());
                    changerStatutCommande(cmd);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        commandesTable.setItems(commandesData);
    }

    // ======================== CHARGEMENT ========================

    private void chargerDemandes() {
        demandesData.clear();
        demandesData.addAll(demandeRepo.getAllDemandes());
    }

    private void chargerCommandes() {
        commandesData.clear();
        commandesData.addAll(commandeRepo.getAllCommandes());
    }

    // ======================== ACTIONS ========================

    private void ouvrirDialogCreerCommande(Demande demande) {
        List<Fournisseur> fournisseurs = fournisseurRepo.getAllFournisseurs();
        if (fournisseurs.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Aucun fournisseur enregistré dans la base de données.", ButtonType.OK).showAndWait();
            return;
        }

        // Charger les produits de la demande
        List<DemandeProduitRepository.ProduitCommande> produitsDemande = demandeProduitRepo.getProduitsByDemande(demande.getIdDemande());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Créer une commande fournisseur");
        dialog.setHeaderText("Commande basée sur la demande #" + demande.getIdDemande()
                + " de " + getUserLabel(demande.getIdUser()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Sélection du fournisseur
        ComboBox<Fournisseur> fournisseurCombo = new ComboBox<>(FXCollections.observableArrayList(fournisseurs));
        fournisseurCombo.setCellFactory(p -> new ListCell<>() {
            @Override protected void updateItem(Fournisseur f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getNom() + " (" + f.getEmail() + ")");
            }
        });
        fournisseurCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Fournisseur f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getNom());
            }
        });
        fournisseurCombo.setValue(fournisseurs.get(0));
        fournisseurCombo.setPrefWidth(300);

        // Libellé
        TextField libelleField = new TextField("Commande pour demande #" + demande.getIdDemande());
        libelleField.setPrefWidth(300);

        // Tableau des produits (lecture seule)
        TableView<DemandeProduitRepository.ProduitCommande> produitsTable = new TableView<>();
        TableColumn<DemandeProduitRepository.ProduitCommande, String> nomCol = new TableColumn<>("Produit");
        nomCol.setPrefWidth(220);
        nomCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProduit() != null ? c.getValue().getProduit().getLibelle() : ""));
        TableColumn<DemandeProduitRepository.ProduitCommande, Integer> qteCol = new TableColumn<>("Quantité");
        qteCol.setPrefWidth(90);
        qteCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        produitsTable.getColumns().addAll(nomCol, qteCol);
        produitsTable.setItems(FXCollections.observableArrayList(produitsDemande));
        produitsTable.setPrefHeight(150);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.add(new Label("Fournisseur :"), 0, 0);
        grid.add(fournisseurCombo, 1, 0);
        grid.add(new Label("Libellé :"), 0, 1);
        grid.add(libelleField, 1, 1);
        grid.add(new Label("Produits demandés :"), 0, 2);
        grid.add(produitsTable, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(450);

        // Désactiver OK si aucun produit
        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setDisable(produitsDemande.isEmpty());

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            Fournisseur fournisseur = fournisseurCombo.getValue();
            String libelle = libelleField.getText().trim();
            if (fournisseur == null || libelle.isEmpty()) return;

            int idUser = SessionManager.estConnecte() ? SessionManager.getUtilisateurConnecte().getIdUser() : 1;

            // Convertir les lignes demande → lignes commande
            List<CommandeProduitRepository.LigneCommandeProduit> lignes = new ArrayList<>();
            for (DemandeProduitRepository.ProduitCommande pc : produitsDemande) {
                lignes.add(new CommandeProduitRepository.LigneCommandeProduit(
                        pc.getProduit().getIdProduit(), pc.getQuantite()));
            }

            int idCommande = commandeProduitRepo.creerCommandeAvecProduits(
                    idUser, fournisseur.getIdFournisseur(), libelle, lignes, demande.getIdDemande());

            if (idCommande > 0) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Commande créée");
                ok.setHeaderText(null);
                ok.setContentText("Commande #" + idCommande + " envoyée à " + fournisseur.getNom() + ".\nLa demande a été marquée comme Traitée.");
                ok.showAndWait();
                chargerDemandes();
                chargerCommandes();
            } else {
                String errDetail = commandeProduitRepo.getLastError();
                new Alert(Alert.AlertType.ERROR,
                        "Impossible de créer la commande.\n\nErreur : " + (errDetail.isEmpty() ? "inconnue" : errDetail),
                        ButtonType.OK).showAndWait();
            }
        });
    }

    private void afficherDetailsCommande(Commande cmd) {
        List<CommandeProduitRepository.ProduitCommande> produits = commandeProduitRepo.getProduitsByCommande(cmd.getIdCommande());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails commande #" + cmd.getIdCommande());
        alert.setHeaderText("Commande #" + cmd.getNumCommande() + " — " + cmd.getStatut());

        StringBuilder sb = new StringBuilder();
        sb.append("Libellé : ").append(cmd.getLibelle()).append("\n");
        sb.append("Fournisseur : ").append(getFournisseurNom(cmd.getIdFournisseur())).append("\n");
        sb.append("Date : ").append(cmd.getDateCommande() != null ? cmd.getDateCommande().format(FMT) : "N/A").append("\n\n");
        sb.append("Produits :\n");
        if (produits.isEmpty()) {
            sb.append("  (aucun produit enregistré)");
        } else {
            for (CommandeProduitRepository.ProduitCommande pc : produits) {
                sb.append("  • ").append(pc.getProduit().getLibelle()).append(" × ").append(pc.getQuantite()).append("\n");
            }
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void changerStatutCommande(Commande cmd) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                cmd.getStatut(),
                "En attente", "Validée", "Livrée", "Annulée");
        dialog.setTitle("Changer le statut");
        dialog.setHeaderText("Commande #" + cmd.getIdCommande());
        dialog.setContentText("Nouveau statut :");
        dialog.showAndWait().ifPresent(statut -> {
            if (commandeRepo.updateStatut(cmd.getIdCommande(), statut)) {
                chargerCommandes();
                if ("Livrée".equals(statut)) {
                    new Alert(Alert.AlertType.INFORMATION, "Commande marquée comme Livrée. Le stock a été mis à jour.", ButtonType.OK).showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Impossible de modifier le statut.", ButtonType.OK).showAndWait();
            }
        });
    }

    // ======================== HANDLERS FXML ========================

    @FXML
    public void handleRafraichirDemandes(ActionEvent event) {
        chargerDemandes();
    }

    @FXML
    public void handleRafraichirCommandes(ActionEvent event) {
        chargerCommandes();
    }

    // ======================== NAVIGATION ========================

    @FXML public void versAccueil(ActionEvent event) {
        try { StartApplication.changeScene("pageAccueil"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versPatients(ActionEvent event) {
        try { StartApplication.changeScene("patientsView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versDossiers(ActionEvent event) {
        try { StartApplication.changeScene("dossierEnChargeView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versCommandes(ActionEvent event) {
        System.out.println("Déjà sur la page commandes");
    }
    @FXML public void versPlanning(ActionEvent event) {
        try { StartApplication.changeScene("planningView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versFicheProduit(ActionEvent event) {
        try { StartApplication.changeScene("ficheProduitView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versUtilisateurs(ActionEvent event) {
        try { StartApplication.changeScene("pageUtilisateurs"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versMonEspace(ActionEvent event) {
        try { StartApplication.changeScene("pageMonEspace"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void deconnexion(ActionEvent event) {
        try { StartApplication.changeScene("helloView"); } catch (Exception e) { e.printStackTrace(); }
    }

    // ======================== HELPERS ========================

    private String getUserLabel(int idUser) {
        if (idUser <= 0) return "";
        try {
            modele.User u = userRepo.trouverUtilisateurParId(idUser);
            if (u == null) return "Utilisateur #" + idUser;
            String label = (u.getNom() + " " + u.getPrenom()).trim();
            return label.isEmpty() ? "Utilisateur #" + idUser : label;
        } catch (Exception e) {
            return "Utilisateur #" + idUser;
        }
    }

    private String getFournisseurNom(int idFournisseur) {
        if (idFournisseur <= 0) return "-";
        try {
            Fournisseur f = fournisseurRepo.trouverFournisseurParId(idFournisseur);
            return f != null ? f.getNom() : "Fournisseur #" + idFournisseur;
        } catch (Exception e) {
            return "Fournisseur #" + idFournisseur;
        }
    }
}
