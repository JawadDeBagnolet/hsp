package appli.hsp;

import appli.StartApplication;
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
    public void versPlanning(ActionEvent event) {
        try {
            StartApplication.changeScene("planningView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers planning: " + e.getMessage());
        }
    }

    @FXML
    public void versHospitalisations(ActionEvent event) {
        try {
            StartApplication.changeScene("hospitalisationsView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers hospitalisations: " + e.getMessage());
        }
    }
    
    @FXML
    public void versFournisseursProduits(ActionEvent event) {
        System.out.println("=== CLIC SUR BOUTON FOURNISSEURS ===");
        try {
            System.out.println("Tentative de redirection vers fournisseurs produits...");
            StartApplication.changeScene("fournisseursProduitsView");
            System.out.println("Redirection vers fournisseurs produits réussie !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers fournisseurs produits: " + e.getMessage());
            e.printStackTrace();
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
}
