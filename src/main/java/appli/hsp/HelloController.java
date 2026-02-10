package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modele.User;
import repository.UserRepository;

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
        
        // Validation simple des champs
        if (email.isEmpty() || motDePasse.isEmpty()) {
            erreurLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        // Vérification des identifiants en base de données
        if (VerifConnexion.verifierConnexion(email, motDePasse)) {
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
                    StartApplication.changeScene("pageAccueil");
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
