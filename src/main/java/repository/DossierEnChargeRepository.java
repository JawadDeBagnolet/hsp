package repository;

import database.Database;
import modele.DossierEnCharge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DossierEnChargeRepository {

    public boolean ajouterDossier(DossierEnCharge dossier) {
        String sql = "INSERT INTO dossierencharge (dateArrivee, heureArrivee, symptomes, nivGravite) VALUES (?, ?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, dossier.getDateArrivee());
            stmt.setInt(2, dossier.getHeureArrivee());
            stmt.setString(3, dossier.getSymptomes());
            stmt.setInt(4, dossier.getNivGravite());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du dossier: " + e.getMessage());
            return false;
        }
    }

    public DossierEnCharge trouverDossierParId(int id) {
        String sql = "SELECT * FROM dossierencharge WHERE idDossier = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new DossierEnCharge(
                    rs.getInt("idDossier"),
                    rs.getInt("dateArrivee"),
                    rs.getInt("heureArrivee"),
                    rs.getString("symptomes"),
                    rs.getInt("nivGravite")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du dossier: " + e.getMessage());
        }
        return null;
    }

    public List<DossierEnCharge> getAllDossiers() {
        List<DossierEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossierencharge";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dossiers.add(new DossierEnCharge(
                    rs.getInt("idDossier"),
                    rs.getInt("dateArrivee"),
                    rs.getInt("heureArrivee"),
                    rs.getString("symptomes"),
                    rs.getInt("nivGravite")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des dossiers: " + e.getMessage());
        }
        return dossiers;
    }

    public boolean modifierDossier(DossierEnCharge dossier) {
        String sql = "UPDATE dossierencharge SET dateArrivee = ?, heureArrivee = ?, symptomes = ?, nivGravite = ? WHERE idDossier = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, dossier.getDateArrivee());
            stmt.setInt(2, dossier.getHeureArrivee());
            stmt.setString(3, dossier.getSymptomes());
            stmt.setInt(4, dossier.getNivGravite());
            stmt.setInt(5, dossier.getIdDossier());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du dossier: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerDossier(int id) {
        String sql = "DELETE FROM dossierencharge WHERE idDossier = ?";

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
