package repository;

import database.Database;
import modele.DossierEnCharge;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DossierEnChargeRepository {

    public DossierEnCharge getDossierByEleve(int idEleve) {
        String sql = "SELECT * FROM dossier_medical WHERE id_eleve = ?";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, idEleve);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du dossier: " + e.getMessage());
        }
        return null;
    }

    public List<DossierEnCharge> getAllDossiers() {
        List<DossierEnCharge> dossiers = new ArrayList<>();
        String sql = "SELECT * FROM dossier_medical ORDER BY date_creation DESC";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dossiers.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des dossiers: " + e.getMessage());
        }
        return dossiers;
    }

    public boolean creerDossier(DossierEnCharge dossier) {
        String sql = "INSERT INTO dossier_medical (id_eleve, antecedents, allergies, traitements_chroniques, date_creation) VALUES (?, ?, ?, ?, ?)";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, dossier.getIdEleve());
            stmt.setString(2, dossier.getAntecedents());
            stmt.setString(3, dossier.getAllergies());
            stmt.setString(4, dossier.getTraitementsChroniques());
            stmt.setDate(5, Date.valueOf(dossier.getDateCreation()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du dossier: " + e.getMessage());
            return false;
        }
    }

    public boolean modifierDossier(DossierEnCharge dossier) {
        String sql = "UPDATE dossier_medical SET antecedents = ?, allergies = ?, traitements_chroniques = ? WHERE id_eleve = ?";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, dossier.getAntecedents());
            stmt.setString(2, dossier.getAllergies());
            stmt.setString(3, dossier.getTraitementsChroniques());
            stmt.setInt(4, dossier.getIdEleve());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du dossier: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerDossier(int idEleve) {
        String sql = "DELETE FROM dossier_medical WHERE id_eleve = ?";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, idEleve);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du dossier: " + e.getMessage());
            return false;
        }
    }

    private DossierEnCharge mapResultSet(ResultSet rs) throws SQLException {
        Date d = rs.getDate("date_creation");
        return new DossierEnCharge(
            rs.getInt("id_dossier"),
            rs.getInt("id_eleve"),
            rs.getString("antecedents"),
            rs.getString("allergies"),
            rs.getString("traitements_chroniques"),
            d != null ? d.toLocalDate() : java.time.LocalDate.now()
        );
    }
}
