package appli.hsp.utils;

import appli.SessionManager;
import appli.StartApplication;

import java.io.IOException;

public class NavigationHelper {

    /**
     * Navigue vers la page de commandes adaptée au rôle de l'utilisateur connecté.
     * - GESTIONNAIRE_DE_STOCK → pageCommandes (vue gestionnaire : demandes + commandes fournisseurs)
     * - Autres rôles → commandeView (vue infirmier : créer/voir ses demandes)
     */
    public static void versTickets() {
        try { StartApplication.changeScene("pageTickets"); } catch (Exception e) { e.printStackTrace(); }
    }

    public static void versCommandes() throws IOException {
        String role = SessionManager.estConnecte()
                ? SessionManager.getUtilisateurConnecte().getRole()
                : "";
        String page = "GESTIONNAIRE_DE_STOCK".equals(role) ? "pageCommandes" : "commandeView";
        StartApplication.changeScene(page);
    }
}
