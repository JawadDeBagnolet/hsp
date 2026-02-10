package appli.hsp;

import appli.StartApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private Label erreurLabel;

    @FXML
    private TextField identifiantField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    void handleConnexion(ActionEvent event) {
        String identifiant = identifiantField.getText();
        String motDePasse = motDePasseField.getText();
        
        // Validation simple des champs
        if (identifiant.isEmpty() || motDePasse.isEmpty()) {
            erreurLabel.setText("Veuillez remplir tous les champs");
            return;
        }
        
        // Vérification des identifiants en base de données
        if (VerifConnexion.verifierConnexion(identifiantField.getText(), motDePasseField.getText())) {
            try {
                // Redirection vers la page d'accueil
                StartApplication.changeScene("pageAccueil");
            } catch (Exception e) {
                erreurLabel.setText("Erreur lors de la redirection");
                e.printStackTrace();
            }
        } else {
            erreurLabel.setText("Identifiant ou mot de passe incorrect");
        }
    }

}
