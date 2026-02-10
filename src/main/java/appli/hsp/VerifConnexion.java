package appli.hsp;

import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerifConnexion {
    
    public static boolean verifierConnexion(String email, String motDePasse) {
        Connection cnx = Database.getConnexion();
        if (cnx == null) {
            System.out.println("Connexion à la base de données échouée");
            return false;
        }
        
        String query = "SELECT * FROM user WHERE email = ? AND mdp = ?";
        
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);
            
            ResultSet rs = stmt.executeQuery();
            boolean connexionReussie = rs.next();
            
            rs.close();
            stmt.close();
            cnx.close();
            
            return connexionReussie;
            
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification des identifiants: " + e.getMessage());
            try {
                cnx.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
