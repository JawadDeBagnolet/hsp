import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestUsers {
    public static void main(String[] args) {
        // Paramètres de connexion (à adapter selon ta configuration)
        String url = "jdbc:mysql://localhost:3306/hsp_db";
        String user = "root";
        String password = "";
        
        try {
            // Charger le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion réussie à la base de données");
            
            // Requête pour récupérer tous les utilisateurs
            String sql = "SELECT * FROM user";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            List<String> utilisateurs = new ArrayList<>();
            
            System.out.println("\n=== LISTE DES UTILISATEURS ENREGISTRÉS ===");
            System.out.println("ID\tNOM\tPRÉNOM\tEMAIL\tRÔLE");
            System.out.println("----------------------------------------");
            
            while (rs.next()) {
                int id = rs.getInt("id_user");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String email = rs.getString("email");
                String role = rs.getString("role");
                
                System.out.println(id + "\t" + nom + "\t" + prenom + "\t" + email + "\t" + role);
                utilisateurs.add(id + " - " + nom + " " + prenom + " (" + role + ")");
            }
            
            System.out.println("\nTotal utilisateurs: " + utilisateurs.size());
            
            // Afficher les rôles disponibles
            System.out.println("\n=== RÔLES DISPONIBLES ===");
            System.out.println("ADMIN");
            System.out.println("MEDECIN");
            System.out.println("INFIRMIER");
            System.out.println("SECRETAIRE");
            System.out.println("GESTIONNAIRE_DE_STOCK");
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL non trouvé: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            System.out.println("\n=== SOLUTION ===");
            System.out.println("1. Vérifie que MySQL est installé et démarré");
            System.out.println("2. Vérifie que la base de données 'hsp_db' existe");
            System.out.println("3. Vérifie les paramètres de connexion dans le code");
            System.out.println("4. Ajoute le driver MySQL au classpath si nécessaire");
        }
    }
}
