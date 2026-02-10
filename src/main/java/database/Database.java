package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String SERVEUR = "localhost";
    private static final String NOM_BDD = "hsp";
    private static final String UTILISATEUR = "root";

    // {port, motDePasse}
    private static final String[][] CONFIGS = {
            { "8889", "root" }, // MAMP reda
            { "3306", "" }      // WAMP vous
    };

    public static Connection getConnexion() {

        for (String[] config : CONFIGS) {
            String port = config[0];
            String mdp  = config[1];

            try {
                String url = "jdbc:mysql://" + SERVEUR + ":" + port + "/" + NOM_BDD
                        + "?useSSL=false&serverTimezone=UTC";

                Connection cnx = DriverManager.getConnection(
                        url, UTILISATEUR, mdp
                );

                System.out.println("Connexion réussie (port " + port + ")");
                return cnx;

            } catch (SQLException e) {
                System.out.println("Échec connexion (port " + port + ")");
            }
        }

        System.out.println("❌ Impossible de se connecter à la base de données.");
        return null;
    }
}