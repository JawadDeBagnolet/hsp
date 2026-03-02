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

public class PageMonEspace {

    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField mdpField;
    
    @FXML
    private PasswordField confirmMdpField;
    
    @FXML
    private Label messageLabel;

    private User utilisateurConnecte;
    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = new UserRepository();
        chargerUtilisateurConnecte();
    }

    private void chargerUtilisateurConnecte() {
        // Récupérer l'utilisateur connecté depuis SessionManager
        utilisateurConnecte = SessionManager.getUtilisateurConnecte();
        
        if (utilisateurConnecte != null) {
            afficherInformationsUtilisateur();
            System.out.println("MonEspace: Utilisateur chargé - " + utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());
        } else {
            afficherMessage("Erreur: Aucun utilisateur connecté", "#e74c3c");
            System.err.println("MonEspace: Aucun utilisateur connecté trouvé dans la session");
        }
    }

    private void afficherInformationsUtilisateur() {
        nomField.setText(utilisateurConnecte.getNom());
        prenomField.setText(utilisateurConnecte.getPrenom());
        emailField.setText(utilisateurConnecte.getEmail());
        
        // Les champs de mot de passe restent vides par défaut
        mdpField.setText("");
        confirmMdpField.setText("");
    }

    @FXML
    public void enregistrerModifications(ActionEvent event) {
        String nouvelEmail = emailField.getText();
        String nouveauMdp = mdpField.getText();
        String confirmMdp = confirmMdpField.getText();
        
        // Validation des champs
        if (nouvelEmail.trim().isEmpty()) {
            afficherMessage("L'email ne peut pas être vide", "#e74c3c");
            return;
        }
        
        boolean modificationsEffectuees = false;
        
        // Vérifier si l'email a changé
        if (!nouvelEmail.equals(utilisateurConnecte.getEmail())) {
            utilisateurConnecte.setEmail(nouvelEmail);
            modificationsEffectuees = true;
        }
        
        // Vérifier si le mot de passe doit être changé
        if (!nouveauMdp.trim().isEmpty()) {
            if (!nouveauMdp.equals(confirmMdp)) {
                afficherMessage("Les mots de passe ne correspondent pas", "#e74c3c");
                return;
            }
            
            if (nouveauMdp.length() < 4) {
                afficherMessage("Le mot de passe doit contenir au moins 4 caractères", "#e74c3c");
                return;
            }
            
            utilisateurConnecte.setMdp(nouveauMdp);
            modificationsEffectuees = true;
        }
        
        // Sauvegarder les modifications
        if (modificationsEffectuees) {
            if (userRepository.modifierUtilisateur(utilisateurConnecte)) {
                afficherMessage("Modifications enregistrées avec succès !", "#2ecc71");
                // Mettre à jour la session avec les nouvelles informations
                SessionManager.setUtilisateurConnecte(utilisateurConnecte);
                // Vider les champs de mot de passe après succès
                mdpField.setText("");
                confirmMdpField.setText("");
            } else {
                afficherMessage("Erreur lors de l'enregistrement des modifications", "#e74c3c");
                // Restaurer les valeurs originales
                chargerUtilisateurConnecte();
            }
        } else {
            afficherMessage("Aucune modification à enregistrer", "#f39c12");
        }
    }

    @FXML
    public void annulerModifications(ActionEvent event) {
        afficherInformationsUtilisateur();
        afficherMessage("Modifications annulées", "#95a5a6");
    }

    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-size: 14px; -fx-wrap-text: true; -fx-font-weight: bold;");
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
        try {
            StartApplication.changeScene("commandeView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers commandes: " + e.getMessage());
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
        // Déjà sur la page mon espace
        System.out.println("Déjà sur la page mon espace");
    }

    @FXML
    public void deconnexion(ActionEvent event) {
        SessionManager.deconnecter();
        try {
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
