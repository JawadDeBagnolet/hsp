package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PageCommandes implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField rechercheField;

    @FXML
    private ComboBox<String> filtreStatut;

    @FXML
    private TableView<Commande> commandesTable;

    @FXML
    private TableColumn<Commande, String> idColumn;

    @FXML
    private TableColumn<Commande, String> dateColumn;

    @FXML
    private TableColumn<Commande, String> fournisseurColumn;

    @FXML
    private TableColumn<Commande, String> typeColumn;

    @FXML
    private TableColumn<Commande, String> montantColumn;

    @FXML
    private TableColumn<Commande, String> statutColumn;

    @FXML
    private TableColumn<Commande, Void> actionsColumn;

    @FXML
    private VBox detailsPane;

    @FXML
    private Label numeroLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label fournisseurLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label montantLabel;

    @FXML
    private Label statutLabel;

    private ObservableList<Commande> commandesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Page Commandes initialisée");
        
        // Initialisation des données
        commandesList = FXCollections.observableArrayList();
        chargerDonneesExemple();
        
        // Configuration du filtre
        filtreStatut.setItems(FXCollections.observableArrayList("Tous", "En attente", "Validée", "Livrée", "Annulée"));
        filtreStatut.setValue("Tous");
        
        // Configuration du tableau
        configurerTableau();
        
        // Configuration des listeners
        configurerListeners();
    }

    private void chargerDonneesExemple() {
        commandesList.addAll(
            new Commande("CMD001", LocalDate.now().minusDays(5), "PharmaPlus", "Médicaments", "1250.00", "Livrée"),
            new Commande("CMD002", LocalDate.now().minusDays(3), "MediSupply", "Matériel", "3500.00", "En attente"),
            new Commande("CMD003", LocalDate.now().minusDays(1), "BioLab", "Réactifs", "890.00", "Validée"),
            new Commande("CMD004", LocalDate.now(), "HealthTech", "Équipement", "15000.00", "En attente"),
            new Commande("CMD005", LocalDate.now().plusDays(1), "PharmaPlus", "Médicaments", "2100.00", "Validée")
        );
    }

    private void configurerTableau() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        fournisseurColumn.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        // Configuration de la colonne actions
        actionsColumn.setCellFactory(param -> new TableCell<Commande, Void>() {
            private final Button voirButton = new Button("👁️");
            private final HBox hbox = new HBox(5, voirButton);
            
            {
                voirButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                voirButton.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    afficherDetails(commande);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
        
        commandesTable.setItems(commandesList);
    }

    private void configurerListeners() {
        // Listener pour la sélection dans le tableau
        commandesTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    afficherDetails(newValue);
                }
            }
        );
        
        // Listener pour le filtre
        filtreStatut.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> filtrerCommandes()
        );
    }

    private void afficherDetails(Commande commande) {
        numeroLabel.setText(commande.getId());
        dateLabel.setText(commande.getDate());
        fournisseurLabel.setText(commande.getFournisseur());
        typeLabel.setText(commande.getType());
        montantLabel.setText(commande.getMontant() + " €");
        statutLabel.setText(commande.getStatut());
        
        detailsPane.setVisible(true);
    }

    private void filtrerCommandes() {
        String filtre = filtreStatut.getValue();
        ObservableList<Commande> filteredList = FXCollections.observableArrayList();
        
        for (Commande commande : commandesList) {
            if (filtre.equals("Tous") || commande.getStatut().equals(filtre)) {
                filteredList.add(commande);
            }
        }
        
        commandesTable.setItems(filteredList);
    }

    @FXML
    public void handleRecherche(ActionEvent event) {
        String recherche = rechercheField.getText().toLowerCase();
        ObservableList<Commande> filteredList = FXCollections.observableArrayList();
        
        for (Commande commande : commandesList) {
            if (commande.getId().toLowerCase().contains(recherche) ||
                commande.getFournisseur().toLowerCase().contains(recherche) ||
                commande.getType().toLowerCase().contains(recherche)) {
                filteredList.add(commande);
            }
        }
        
        commandesTable.setItems(filteredList);
    }

    @FXML
    public void handleNouvelleCommande(ActionEvent event) {
        // TODO: Ouvrir une fenêtre de dialogue pour créer une nouvelle commande
        System.out.println("Nouvelle commande");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nouvelle commande");
        alert.setHeaderText("Fonctionnalité à développer");
        alert.setContentText("La création de nouvelles commandes sera disponible prochainement.");
        alert.showAndWait();
    }

    @FXML
    public void handleModifier(ActionEvent event) {
        // TODO: Ouvrir une fenêtre de dialogue pour modifier la commande
        System.out.println("Modifier commande");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modifier commande");
        alert.setHeaderText("Fonctionnalité à développer");
        alert.setContentText("La modification des commandes sera disponible prochainement.");
        alert.showAndWait();
    }

    @FXML
    public void handleSupprimer(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer commande");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer cette commande ?");
        confirmation.setContentText("Cette action est irréversible.");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            // TODO: Supprimer la commande
            System.out.println("Commande supprimée");
            detailsPane.setVisible(false);
        }
    }

    @FXML
    public void handleImprimer(ActionEvent event) {
        // TODO: Imprimer la commande
        System.out.println("Imprimer commande");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Imprimer commande");
        alert.setHeaderText("Fonctionnalité à développer");
        alert.setContentText("L'impression des commandes sera disponible prochainement.");
        alert.showAndWait();
    }

    // Méthodes de navigation
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

    // Classe interne pour représenter une commande
    public static class Commande {
        private String id;
        private LocalDate date;
        private String fournisseur;
        private String type;
        private String montant;
        private String statut;

        public Commande(String id, LocalDate date, String fournisseur, String type, String montant, String statut) {
            this.id = id;
            this.date = date;
            this.fournisseur = fournisseur;
            this.type = type;
            this.montant = montant;
            this.statut = statut;
        }

        // Getters
        public String getId() { return id; }
        public String getDate() { return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
        public String getFournisseur() { return fournisseur; }
        public String getType() { return type; }
        public String getMontant() { return montant; }
        public String getStatut() { return statut; }
    }
}
