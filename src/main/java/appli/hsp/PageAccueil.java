package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import appli.hsp.exception.ErrorCode;
import appli.hsp.exception.LPRSException;
import appli.hsp.utils.ErrorHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import modele.User;

public class PageAccueil {

    // ── Navbar ────────────────────────────────────────────────────
    @FXML private Button btnSecretariat;
    @FXML private Button btnDossiers;
    @FXML private Button btnTickets;
    @FXML private Button btnInfirmerie;
    @FXML private Button btnCommandes;
    @FXML private Button btnPlanning;
    @FXML private Button btnCatalogue;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnUtilisateurs;

    // ── Cards ─────────────────────────────────────────────────────
    @FXML private VBox cardSecretariat;
    @FXML private VBox cardPlanning;
    @FXML private VBox cardInfirmerie;
    @FXML private VBox cardCommandes;
    @FXML private VBox cardFournisseurs;
    @FXML private VBox cardCatalogue;
    @FXML private VBox cardTickets;
    @FXML private VBox cardDossiers;

    // ── Info utilisateur ──────────────────────────────────────────
    @FXML private Label labelBienvenue;
    @FXML private Label labelRole;

    @FXML
    public void initialize() {
        System.out.println("Page d'accueil initialisée");

        if (!SessionManager.estConnecte()) return;

        User user = SessionManager.getUtilisateurConnecte();
        String role = user.getRole();

        if (labelBienvenue != null) {
            labelBienvenue.setText(user.getPrenom() + " " + user.getNom());
        }
        if (labelRole != null) {
            labelRole.setText(getRoleLibelle(role));
        }

        appliquerDroitsRole(role);
    }

    private void appliquerDroitsRole(String role) {
        boolean isAdmin        = "ADMIN".equals(role);
        boolean isSecretaire   = "SECRETAIRE".equals(role);
        boolean isInfirmier    = "INFIRMIER".equals(role);
        boolean isGestionnaire = "GESTIONNAIRE_DE_STOCK".equals(role);

        // Secrétariat : ADMIN, SECRETAIRE
        afficher(btnSecretariat, isAdmin || isSecretaire);
        afficher(cardSecretariat, isAdmin || isSecretaire);

        // Dossiers/Rapports : ADMIN, INFIRMIER, SECRETAIRE
        afficher(btnDossiers, isAdmin || isInfirmier || isSecretaire);
        afficher(cardDossiers, isAdmin || isInfirmier || isSecretaire);

        // Tickets : ADMIN, INFIRMIER, SECRETAIRE
        afficher(btnTickets, isAdmin || isInfirmier || isSecretaire);
        afficher(cardTickets, isAdmin || isInfirmier || isSecretaire);

        // Infirmerie (visites) : ADMIN, INFIRMIER, SECRETAIRE
        afficher(btnInfirmerie, isAdmin || isInfirmier || isSecretaire);
        afficher(cardInfirmerie, isAdmin || isInfirmier || isSecretaire);

        // Planning : ADMIN, SECRETAIRE
        afficher(btnPlanning, isAdmin || isSecretaire);
        afficher(cardPlanning, isAdmin || isSecretaire);

        // Commandes : ADMIN, GESTIONNAIRE_DE_STOCK
        afficher(btnCommandes, isAdmin || isGestionnaire);
        afficher(cardCommandes, isAdmin || isGestionnaire);

        // Catalogue produits : ADMIN, GESTIONNAIRE_DE_STOCK
        afficher(btnCatalogue, isAdmin || isGestionnaire);
        afficher(cardCatalogue, isAdmin || isGestionnaire);

        // Fournisseurs : ADMIN, GESTIONNAIRE_DE_STOCK
        afficher(btnFournisseurs, isAdmin || isGestionnaire);
        afficher(cardFournisseurs, isAdmin || isGestionnaire);

        // Utilisateurs : ADMIN uniquement
        afficher(btnUtilisateurs, isAdmin);
    }

    private void afficher(Node node, boolean visible) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private String getRoleLibelle(String role) {
        return switch (role) {
            case "ADMIN"                -> "Administrateur";
            case "SECRETAIRE"           -> "Secrétaire";
            case "INFIRMIER"            -> "Infirmier(ère)";
            case "GESTIONNAIRE_DE_STOCK"-> "Gestionnaire de stock";
            case "PROF"                 -> "Professeur";
            default                     -> role;
        };
    }

    // ── Navigation ────────────────────────────────────────────────

    @FXML
    public void versAccueil(ActionEvent event) {
        System.out.println("Déjà sur la page d'accueil");
    }

    @FXML
    public void versPatients(ActionEvent event) {
        try {
            StartApplication.changeScene("patientsView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des patients", e),
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
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des dossiers", e),
                "Navigation vers Dossiers"
            );
        }
    }

    @FXML
    public void versTickets(ActionEvent event) {
        try {
            StartApplication.changeScene("pageTickets");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder aux tickets", e),
                "Navigation vers Tickets"
            );
        }
    }

    @FXML
    public void versVisiteInfirmerie(ActionEvent event) {
        try {
            StartApplication.changeScene("visiteInfirmerieView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder aux visites infirmerie", e),
                "Navigation vers Visites Infirmerie"
            );
        }
    }

    @FXML
    public void versCommandes(ActionEvent event) {
        try {
            String role = SessionManager.estConnecte()
                    ? SessionManager.getUtilisateurConnecte().getRole()
                    : "";
            String page = "GESTIONNAIRE_DE_STOCK".equals(role) ? "pageCommandes" : "commandeView";
            StartApplication.changeScene(page);
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des commandes", e),
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
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des utilisateurs", e),
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
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au planning", e),
                "Navigation vers Planning"
            );
        }
    }

    @FXML
    public void versFicheProduit(ActionEvent event) {
        try {
            StartApplication.changeScene("ficheProduitView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au catalogue produits", e),
                "Navigation vers Catalogue Produits"
            );
        }
    }

    @FXML
    public void versFournisseursProduits(ActionEvent event) {
        try {
            StartApplication.changeScene("fournisseursProduitsView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des fournisseurs et produits", e),
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
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à votre espace personnel", e),
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
                SessionManager.deconnecter();
                StartApplication.changeScene("helloView");
                ErrorHandler.showInfoAlert("Déconnexion", "Vous avez été déconnecté avec succès");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Erreur lors de la déconnexion", e),
                "Déconnexion"
            );
        }
    }
}
