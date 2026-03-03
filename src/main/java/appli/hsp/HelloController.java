package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modele.User;
import repository.UserRepository;

import java.net.URL;

public class HelloController {

    @FXML
    private Label erreurLabel;

    @FXML
    private TextField identifiantField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    void handleConnexion(ActionEvent event) {
        String email = identifiantField.getText();
        String motDePasse = motDePasseField.getText();

        if (email != null) {
            email = email.trim();
        }
        if (motDePasse != null) {
            motDePasse = motDePasse.trim();
        }
        
        // Validation simple des champs
        if (email == null || motDePasse == null || email.isEmpty() || motDePasse.isEmpty()) {
            erreurLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        // Vérification des identifiants en base de données
        boolean ok = VerifConnexion.verifierConnexion(email, motDePasse);
        System.out.println("Résultat verifierConnexion(" + email + "): " + ok);

        if (ok) {
            erreurLabel.setText("Connexion OK (chargement...)");
            try {
                // Récupérer l'utilisateur connecté
                UserRepository userRepository = new UserRepository();
                User utilisateur = userRepository.trouverUtilisateurParEmail(email);
                
                System.out.println("Recherche utilisateur pour email: " + email);
                
                if (utilisateur != null) {
                    System.out.println("Utilisateur trouvé: " + utilisateur.getNom() + " " + utilisateur.getPrenom());
                    // Stocker l'utilisateur dans la session
                    SessionManager.setUtilisateurConnecte(utilisateur);
                    
                    // Redirection vers la page d'accueil
                    System.out.println("Redirection vers pageAccueil...");
                    Platform.runLater(() -> {
                        try {
                            Stage stage = (Stage) identifiantField.getScene().getWindow();
                            String resourcePath = "/appli/hsp/pageAccueil.fxml";
                            URL resourceUrl = StartApplication.class.getResource(resourcePath);

                            System.out.println("[HelloController] Loading FXML: " + resourcePath + " -> " + resourceUrl);
                            if (resourceUrl == null) {
                                throw new IllegalStateException("FXML introuvable: " + resourcePath);
                            }

                            FXMLLoader loader = new FXMLLoader(resourceUrl);
                            Scene scene = new Scene(loader.load(), stage.getWidth(), stage.getHeight());
                            stage.setScene(scene);
                            stage.setTitle("HSP - pageAccueil");
                            stage.show();

                            System.out.println("[HelloController] Scene changée vers pageAccueil.");
                        } catch (Exception e) {
                            System.err.println("Erreur changeScene(pageAccueil): " + e.getMessage());
                            e.printStackTrace();
                            erreurLabel.setText("Erreur changeScene: " + e.getMessage());

                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur de redirection");
                            alert.setHeaderText("Impossible d'ouvrir la page d'accueil");
                            alert.setContentText(String.valueOf(e.getMessage()));
                            alert.showAndWait();
                        }
                    });
                } else {
                    System.err.println("Utilisateur non trouvé pour email: " + email);
                    erreurLabel.setText("Erreur: Utilisateur non trouvé après vérification");
                }
            } catch (Exception e) {
                System.err.println("Exception lors de la redirection: " + e.getMessage());
                e.printStackTrace();
                erreurLabel.setText("Erreur lors de la redirection: " + e.getMessage());
            }
        } else {
            erreurLabel.setText("Identifiant ou mot de passe incorrect");
        }
    }
}
