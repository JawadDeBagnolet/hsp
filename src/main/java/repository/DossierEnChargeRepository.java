package repository;

import database.Database;
import modele.DossierEnCharge;
import appli.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DossierEnChargeRepository {

    public boolean ajouterDossier(DossierEnCharge dossier) {
        System.out.println("=== DÉBUT AJOUT DOSSIER REPOSITORY ===");
        System.out.println("Dossier à ajouter: " + dossier.toString());
        
        // Ne pas inclure l'ID dans l'INSERT si c'est auto-incrémenté
        String sql = "INSERT INTO dossier_charge (date_arrivee, heure_arrivee, symptomes, niveau_gravite, id_patient, id_user) VALUES (?, ?, ?, ?, ?, ?)";
        System.out.println("SQL: " + sql);

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (cnx == null) {
                System.err.println("ERREUR: Connexion à la base de données est null !");
                return false;
            }
            System.out.println("Connexion à la base établie");

            // Conversion explicite pour éviter les problèmes de type
            java.sql.Date sqlDate = java.sql.Date.valueOf(dossier.getDateArrivee());
            // Combiner date et heure en un Timestamp
            java.time.LocalDateTime dateTime = dossier.getDateArrivee().atTime(dossier.getHeureArrivee());
            java.sql.Timestamp sqlDateTime = java.sql.Timestamp.valueOf(dateTime);
            
            stmt.setDate(1, sqlDate);
            stmt.setTimestamp(2, sqlDateTime);
            stmt.setString(3, dossier.getSymptomes());
            stmt.setInt(4, dossier.getNiveauGravite());
            stmt.setInt(5, dossier.getRefUser()); // id_patient
            stmt.setInt(6, SessionManager.getUtilisateurConnecte().getIdUser()); // id_user (utilisateur connecté)
            
            System.out.println("Paramètres préparés:");
            System.out.println("  1. Date (SQL Date): " + sqlDate + " -> " + sqlDate.getClass().getSimpleName());
            System.out.println("  2. Date/Heure (SQL Timestamp): " + sqlDateTime + " -> " + sqlDateTime.getClass().getSimpleName());
            System.out.println("  3. Symptômes: " + dossier.getSymptomes());
            System.out.println("  4. Niveau gravité: " + dossier.getNiveauGravite());
            System.out.println("  5. ID Patient: " + dossier.getRefUser());
            System.out.println("  6. ID User (connecté): " + SessionManager.getUtilisateurConnecte().getIdUser());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Nombre de lignes affectées: " + rowsAffected);

            boolean succes = rowsAffected > 0;
            System.out.println("Succès: " + succes);
            
            if (succes) {
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("ID généré: " + generatedId);
                    }
                }
            }
            
            System.out.println("=== FIN AJOUT DOSSIER REPOSITORY ===");
            return succes;
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout du dossier: " + e.getMessage());
            System.err.println("Code d'erreur: " + e.getErrorCode());
            System.err.println("État SQL: " + e.getSQLState());
            
            // Message d'erreur spécifique pour la contrainte d'unicité
            if (e.getErrorCode() == 1062 && e.getMessage().contains("ref_user")) {
                System.err.println("PROBLÈME IDENTIFIÉ: L'utilisateur ne peut avoir qu'un seul dossier (contrainte d'unicité sur ref_user)");
                System.err.println("SOLUTION: Modifier la table pour supprimer la contrainte d'unicité sur ref_user");
                System.err.println("SQL: ALTER TABLE dossier_charge DROP INDEX ref_user;");
            }
            
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Erreur générale lors de l'ajout du dossier: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public DossierEnCharge trouverDossierParId(int id) {
        String sql = "SELECT * FROM dossier_charge WHERE id_dossier = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new DossierEnCharge(
                    rs.getInt("id_dossier"),
                    rs.getDate("date_arrivee").toLocalDate(),
                    rs.getTimestamp("heure_arrivee").toLocalDateTime().toLocalTime(),
                    rs.getString("symptomes"),
                    rs.getInt("niveau_gravite"),
                    rs.getInt("id_patient")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du dossier: " + e.getMessage());
        }
        return null;
    }

    public List<DossierEnCharge> getAllDossiers() {
        List<DossierEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossier_charge";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dossiers.add(new DossierEnCharge(
                    rs.getInt("id_dossier"),
                    rs.getDate("date_arrivee").toLocalDate(),
                    rs.getTimestamp("heure_arrivee").toLocalDateTime().toLocalTime(),
                    rs.getString("symptomes"),
                    rs.getInt("niveau_gravite"),
                    rs.getInt("id_patient")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des dossiers: " + e.getMessage());
        }
        return dossiers;
    }

    public boolean modifierDossier(DossierEnCharge dossier) {
        String sql = "UPDATE dossier_charge SET date_arrivee = ?, heure_arrivee = ?, symptomes = ?, niveau_gravite = ?, id_patient = ?, id_user = ? WHERE id_dossier = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(dossier.getDateArrivee()));
            // Combiner date et heure en un Timestamp
            java.time.LocalDateTime dateTime = dossier.getDateArrivee().atTime(dossier.getHeureArrivee());
            java.sql.Timestamp sqlDateTime = java.sql.Timestamp.valueOf(dateTime);
            stmt.setTimestamp(2, sqlDateTime);
            stmt.setString(3, dossier.getSymptomes());
            stmt.setInt(4, dossier.getNiveauGravite());
            stmt.setInt(5, dossier.getRefUser()); // id_patient
            stmt.setInt(6, SessionManager.getUtilisateurConnecte().getIdUser()); // id_user (utilisateur connecté)
            stmt.setInt(7, dossier.getIdDossier());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du dossier: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerDossier(int id) {
        String sql = "DELETE FROM dossier_charge WHERE id_dossier = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du dossier: " + e.getMessage());
            return false;
        }
    }
}
