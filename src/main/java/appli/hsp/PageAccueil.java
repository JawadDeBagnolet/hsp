package appli.hsp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PageAccueil {

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {
        // Initialisation du contrôleur
        System.out.println("Page d'accueil initialisée");
    }

    @FXML
    void handleCommandes(ActionEvent event) {
        try {
            // Charger la vue des commandes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/appli/hsp/commandeView.fxml"));
            VBox commandeView = loader.load();
            
            // Créer une nouvelle scène
            Scene scene = new Scene(commandeView);
            
            // Obtenir la stage actuelle et changer la scène
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion des Commandes - HSP");
            
            System.out.println("Navigation vers la page des commandes");
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page des commandes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
