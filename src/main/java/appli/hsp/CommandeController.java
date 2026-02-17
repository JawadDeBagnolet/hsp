package appli.hsp;

import appli.StartApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CommandeController {

    @FXML
    private TextField rechercheField;

    @FXML
    private TableView<?> commandesTable;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> dateColumn;

    @FXML
    private TableColumn<?, ?> patientColumn;

    @FXML
    private TableColumn<?, ?> medecinColumn;

    @FXML
    private TableColumn<?, ?> statutColumn;

    @FXML
    private TableColumn<?, ?> actionsColumn;

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

    @FXML
    public void initialize() {
        // Initialisation du contrôleur
        System.out.println("Page commande initialisée");
        
        // Configuration initiale du tableau
        setupTable();
        
        // Masquer le panneau de détails au démarrage
        detailsPane.setVisible(false);
    }

    private void setupTable() {
        // Configuration des colonnes du tableau
        idColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setStyle("-fx-alignment: CENTER;");
        patientColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        medecinColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        statutColumn.setStyle("-fx-alignment: CENTER;");
        actionsColumn.setStyle("-fx-alignment: CENTER;");
        
        // TODO: Charger les données des commandes depuis la base de données
        loadCommandes();
    }

    private void loadCommandes() {
        // TODO: Implémenter le chargement des commandes depuis la base de données
        // Pour l'instant, le tableau reste vide
        System.out.println("Chargement des commandes...");
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
        
        // TODO: Implémenter la logique de recherche
        // Filtrer les commandes selon le texte de recherche
    }

    @FXML
    void handleNouvelleCommande(ActionEvent event) {
        // TODO: Ouvrir une fenêtre ou un dialogue pour créer une nouvelle commande
        System.out.println("Création d'une nouvelle commande");
        // Ouvrir une nouvelle vue ou un dialogue pour la saisie
    }

    @FXML
    void handleRafraichir(ActionEvent event) {
        // TODO: Rafraîchir la liste des commandes
        System.out.println("Rafraîchissement de la liste");
        loadCommandes();
    }

    @FXML
    void handleModifier(ActionEvent event) {
        // TODO: Modifier la commande sélectionnée
        System.out.println("Modification de la commande");
        // Ouvrir un dialogue de modification
    }

    @FXML
    void handleSupprimer(ActionEvent event) {
        // TODO: Supprimer la commande sélectionnée avec confirmation
        System.out.println("Suppression de la commande");
        
        // Demander une confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette commande ?");
        alert.setContentText("Cette action est irréversible.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Supprimer la commande de la base de données
                System.out.println("Commande supprimée");
                detailsPane.setVisible(false);
                loadCommandes(); // Recharger la liste
            }
        });
    }

    /**
     * Affiche les détails d'une commande sélectionnée
     */
    public void afficherDetailsCommande(Object commande) {
        // TODO: Remplir les champs avec les détails de la commande
        detailsPane.setVisible(true);
        
        // Exemple de données (à remplacer avec les vraies données)
        numeroLabel.setText("CMD-001");
        dateLabel.setText("10/02/2026");
        patientLabel.setText("Jean Dupont");
        medecinLabel.setText("Dr. Martin");
        statutLabel.setText("En cours");
    }
}
