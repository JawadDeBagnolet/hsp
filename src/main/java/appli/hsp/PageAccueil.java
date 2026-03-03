package appli.hsp;

import appli.StartApplication;
import appli.hsp.exception.ErrorCode;
import appli.hsp.exception.HSPException;
import appli.hsp.utils.ErrorHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PageAccueil {

    @FXML
    private Button fournisseursButton;

    @FXML
    public void initialize() {
        // Initialisation du contrôleur
        System.out.println("Page d'accueil initialisée");
        
        // Vérifier si le bouton fournisseurs est bien injecté
        if (fournisseursButton != null) {
            System.out.println("✅ Bouton fournisseurs injecté avec succès !");
        } else {
            System.err.println("❌ ERREUR: Bouton fournisseurs non injecté !");
        }
    }
    
    @FXML
    public void versAccueil(ActionEvent event) {
        // Déjà sur la page d'accueil
        System.out.println("Déjà sur la page d'accueil");
    }
    
    @FXML
    public void versPatients(ActionEvent event) {
        try {
            StartApplication.changeScene("patientsView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des patients", e),
                "Navigation vers Patients"
            );
        }
    }
    
    @FXML
    public void versDossiers(ActionEvent event) {
        try {
            StartApplication.changeScene("dossierEnChargeView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des dossiers", e),
                "Navigation vers Dossiers"
            );
        }
    }
    
    @FXML
    public void versCommandes(ActionEvent event) {
        try {
            StartApplication.changeScene("commandeView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des commandes", e),
                "Navigation vers Commandes"
            );
        }
    }
    
    @FXML
    public void versUtilisateurs(ActionEvent event) {
        try {
            StartApplication.changeScene("pageUtilisateurs");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des utilisateurs", e),
                "Navigation vers Utilisateurs"
            );
        }
    }
    
    @FXML
    public void versPlanning(ActionEvent event) {
        try {
            StartApplication.changeScene("planningView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au planning", e),
                "Navigation vers Planning"
            );
        }
    }

    @FXML
    public void versHospitalisations(ActionEvent event) {
        try {
            StartApplication.changeScene("hospitalisationsView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des hospitalisations", e),
                "Navigation vers Hospitalisations"
            );
        }
    }
    
    @FXML
    public void versFournisseursProduits(ActionEvent event) {
        try {
            StartApplication.changeScene("fournisseursProduitsView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des fournisseurs et produits", e),
                "Navigation vers Fournisseurs/Produits"
            );
        }
    }
    
    @FXML
    public void versMonEspace(ActionEvent event) {
        try {
            StartApplication.changeScene("pageMonEspace");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à votre espace personnel", e),
                "Navigation vers Mon Espace"
            );
        }
    }
    
    @FXML
    public void deconnexion(ActionEvent event) {
        try {
            boolean confirmed = ErrorHandler.showConfirmationAlert(
                "Déconnexion", 
                "Êtes-vous sûr de vouloir vous déconnecter ?"
            );
            if (confirmed) {
                StartApplication.changeScene("helloView");
                ErrorHandler.showInfoAlert("Déconnexion", "Vous avez été déconnecté avec succès");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(
                new HSPException(ErrorCode.NAVIGATION_ERROR, "Erreur lors de la déconnexion", e),
                "Déconnexion"
            );
        }
    }
}
