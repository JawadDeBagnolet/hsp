package appli;

import modele.User;

public class SessionManager {
    private static User utilisateurConnecte;
    
    public static void setUtilisateurConnecte(User user) {
        utilisateurConnecte = user;
        System.out.println("Session: Utilisateur connecté - " + user.getNom() + " " + user.getPrenom());
    }
    
    public static User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }
    
    public static void deconnecter() {
        System.out.println("Session: Déconnexion de " + (utilisateurConnecte != null ? utilisateurConnecte.getNom() : "aucun utilisateur"));
        utilisateurConnecte = null;
    }
}
