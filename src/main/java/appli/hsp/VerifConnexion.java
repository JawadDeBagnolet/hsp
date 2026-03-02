package appli.hsp;
import database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import appli.util.PasswordUtils;
public class VerifConnexion {

    public static boolean verifierConnexion(String email, String motDePasse) {

        Connection cnx = Database.getConnexion();

        if (cnx == null) {
            System.out.println("Connexion à la base de données échouée");
            return false;
        }

        String query = "SELECT mdp FROM user WHERE email = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String hashedPassword = rs.getString("mdp");

                boolean connexionReussie =
                        PasswordUtils.checkPassword(motDePasse, hashedPassword);

                rs.close();
                stmt.close();
                cnx.close();

                return connexionReussie;
            }

            rs.close();
            stmt.close();
            cnx.close();

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}