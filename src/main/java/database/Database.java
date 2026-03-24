package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String SERVEUR = "localhost";
    private static final String NOM_BDD = "hsp";
    private static final String UTILISATEUR = "root";
    private static boolean migrationDone = false;

    // {port, motDePasse, description}
    private static final String[][] CONFIGS = {
            { "3306", "", "WAMP" },      // WAMP - priorité
            { "8889", "root", "MAMP" }    // MAMP
    };

    /** Ajoute les colonnes manquantes si elles n'existent pas encore. */
    private static void runMigrations(Connection cnx) {
        try {
            DatabaseMetaData meta = cnx.getMetaData();
            try (Statement stmt = cnx.createStatement()) {

                // demande.statut
                try (ResultSet rs = meta.getColumns(null, null, "demande", "statut")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `demande` ADD COLUMN `statut` VARCHAR(50) NOT NULL DEFAULT 'En attente'");
                        System.out.println("Migration : colonne demande.statut ajoutée.");
                    }
                }

                // commande.id_fournisseur
                try (ResultSet rs = meta.getColumns(null, null, "commande", "id_fournisseur")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `commande` ADD COLUMN `id_fournisseur` INT NOT NULL DEFAULT 0");
                        System.out.println("Migration : colonne commande.id_fournisseur ajoutée.");
                    }
                }

                // commande.date_commande
                try (ResultSet rs = meta.getColumns(null, null, "commande", "date_commande")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `commande` ADD COLUMN `date_commande` DATETIME NOT NULL DEFAULT NOW()");
                        System.out.println("Migration : colonne commande.date_commande ajoutée.");
                    }
                }

                // commande.statut
                try (ResultSet rs = meta.getColumns(null, null, "commande", "statut")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `commande` ADD COLUMN `statut` VARCHAR(50) NOT NULL DEFAULT 'En attente'");
                        System.out.println("Migration : colonne commande.statut ajoutée.");
                    }
                }

                // commande.id_demande
                try (ResultSet rs = meta.getColumns(null, null, "commande", "id_demande")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `commande` ADD COLUMN `id_demande` INT NOT NULL DEFAULT 0");
                        System.out.println("Migration : colonne commande.id_demande ajoutée.");
                    }
                }

                // table ticket
                try (ResultSet rs = meta.getTables(null, null, "ticket", null)) {
                    if (!rs.next()) {
                        stmt.executeUpdate(
                            "CREATE TABLE `ticket` (" +
                            "  `id_ticket` INT NOT NULL AUTO_INCREMENT," +
                            "  `id_eleve` INT NOT NULL," +
                            "  `id_secretaire` INT NOT NULL," +
                            "  `date_creation` DATETIME NOT NULL DEFAULT NOW()," +
                            "  `motif` VARCHAR(500) DEFAULT NULL," +
                            "  `statut` VARCHAR(50) NOT NULL DEFAULT 'En attente infirmerie'," +
                            "  `prescription` TEXT DEFAULT NULL," +
                            "  PRIMARY KEY (`id_ticket`)," +
                            "  KEY `fk_ticket_eleve` (`id_eleve`)," +
                            "  KEY `fk_ticket_user` (`id_secretaire`)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
                        );
                        System.out.println("Migration : table ticket créée.");
                    }
                }

                // ticket.prescription
                try (ResultSet rs = meta.getColumns(null, null, "ticket", "prescription")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `ticket` ADD COLUMN `prescription` TEXT DEFAULT NULL");
                        System.out.println("Migration : colonne ticket.prescription ajoutée.");
                    }
                }

                // visite_infirmerie.traitement (colonne manquante dans le schema initial)
                try (ResultSet rs = meta.getColumns(null, null, "visite_infirmerie", "traitement")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `visite_infirmerie` ADD COLUMN `traitement` TEXT DEFAULT NULL");
                        System.out.println("Migration : colonne visite_infirmerie.traitement ajoutée.");
                    }
                }

                // visite_infirmerie.statut (colonne manquante dans le schema initial)
                try (ResultSet rs = meta.getColumns(null, null, "visite_infirmerie", "statut")) {
                    if (!rs.next()) {
                        stmt.executeUpdate("ALTER TABLE `visite_infirmerie` ADD COLUMN `statut` VARCHAR(50) NOT NULL DEFAULT 'Terminée'");
                        System.out.println("Migration : colonne visite_infirmerie.statut ajoutée.");
                    }
                }

                // table dossier_medical
                try (ResultSet rs = meta.getTables(null, null, "dossier_medical", null)) {
                    if (!rs.next()) {
                        stmt.executeUpdate(
                            "CREATE TABLE `dossier_medical` (" +
                            "  `id_dossier` INT NOT NULL AUTO_INCREMENT," +
                            "  `id_eleve` INT NOT NULL UNIQUE," +
                            "  `antecedents` TEXT DEFAULT NULL," +
                            "  `allergies` TEXT DEFAULT NULL," +
                            "  `traitements_chroniques` TEXT DEFAULT NULL," +
                            "  `date_creation` DATE NOT NULL," +
                            "  `date_modification` DATETIME DEFAULT NULL," +
                            "  PRIMARY KEY (`id_dossier`)," +
                            "  KEY `fk_dossier_eleve` (`id_eleve`)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
                        );
                        System.out.println("Migration : table dossier_medical créée.");
                    }
                }
            }
            migrationDone = true;
        } catch (SQLException e) {
            System.err.println("Erreur migration auto : " + e.getMessage());
        }
    }

    public static Connection getConnexion() {

        for (String[] config : CONFIGS) {
            String port = config[0];
            String mdp  = config[1];
            String type = config.length > 2 ? config[2] : "";

            try {
                String url = "jdbc:mysql://" + SERVEUR + ":" + port + "/" + NOM_BDD
                        + "?useSSL=false&serverTimezone=UTC";

                Connection cnx = DriverManager.getConnection(
                        url, UTILISATEUR, mdp
                );

                System.out.println("Connexion réussie - " + type + " (port " + port + ")");
                if (!migrationDone) runMigrations(cnx);
                return cnx;

            } catch (SQLException e) {
                System.out.println("Échec connexion - " + type + " (port " + port + ")");
            }
        }

        System.out.println("❌ Impossible de se connecter à la base de données.");
        return null;
    }
}
