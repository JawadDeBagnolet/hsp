package appli.hsp.utils;

import appli.SessionManager;
import javafx.scene.control.Button;

public class NavbarHelper {

    /**
     * Applique la visibilité des boutons de navigation selon le rôle de l'utilisateur connecté.
     * Passer null pour les boutons absents de la navbar courante.
     */
    public static void appliquerNavbar(
            Button btnSecretariat,
            Button btnDossiers,
            Button btnTickets,
            Button btnInfirmerie,
            Button btnCommandes,
            Button btnPlanning,
            Button btnCatalogue,
            Button btnFournisseurs,
            Button btnUtilisateurs,
            Button btnDemandes
    ) {
        if (!SessionManager.estConnecte()) return;

        String role = SessionManager.getUtilisateurConnecte().getRole();

        boolean isAdmin        = "ADMIN".equals(role);
        boolean isSecretaire   = "SECRETAIRE".equals(role);
        boolean isInfirmier    = "INFIRMIER".equals(role);
        boolean isGestionnaire = "GESTIONNAIRE_DE_STOCK".equals(role);

        setVisible(btnSecretariat,  isAdmin || isSecretaire);
        setVisible(btnDossiers,     isAdmin || isInfirmier || isSecretaire);
        setVisible(btnTickets,      isAdmin || isInfirmier || isSecretaire);
        setVisible(btnInfirmerie,   isAdmin || isInfirmier || isSecretaire);
        setVisible(btnCommandes,    isAdmin || isGestionnaire);
        setVisible(btnPlanning,     isAdmin || isSecretaire);
        setVisible(btnCatalogue,    isAdmin || isGestionnaire);
        setVisible(btnFournisseurs, isAdmin || isGestionnaire);
        setVisible(btnUtilisateurs, isAdmin);
        setVisible(btnDemandes,     isAdmin || isInfirmier);
    }

    private static void setVisible(Button btn, boolean visible) {
        if (btn == null) return;
        btn.setVisible(visible);
        btn.setManaged(visible);
    }
}
