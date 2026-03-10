package repository;

import database.Database;
import modele.VisiteInfirmerie;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VisiteInfirmerieRepository {

    public boolean ajouterVisite(VisiteInfirmerie visite) {
        String sql = "INSERT INTO visite_infirmerie (id_eleve, date_visite, heure_visite, motif, id_infirmier) VALUES (?, ?, ?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, visite.getIdEleve());
            stmt.setDate(2, Date.valueOf(visite.getDateVisite()));
            stmt.setTime(3, Time.valueOf(visite.getHeureVisite()));
            stmt.setString(4, visite.getMotif());
            if (visite.getIdInfirmier() == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, visite.getIdInfirmier());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la visite : " + e.getMessage());
            return false;
        }
    }

    public List<VisiteInfirmerie> getAllVisites() {
        List<VisiteInfirmerie> visites = new ArrayList<>();
        String sql = "SELECT v.*, e.nom, e.prenom FROM visite_infirmerie v " +
                     "LEFT JOIN fiche_eleve e ON v.id_eleve = e.id_eleve " +
                     "ORDER BY v.date_visite DESC, v.heure_visite DESC";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VisiteInfirmerie visite = mapResultSet(rs);
                try { visite.setNomEleve(rs.getString("nom")); } catch (Exception ignored) {}
                try { visite.setPrenomEleve(rs.getString("prenom")); } catch (Exception ignored) {}
                visites.add(visite);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des visites : " + e.getMessage());
        }
        System.out.println("Total visites récupérées: " + visites.size());
        return visites;
    }

    public List<VisiteInfirmerie> getVisitesParEleve(int idEleve) {
        List<VisiteInfirmerie> visites = new ArrayList<>();
        String sql = "SELECT v.*, e.nom, e.prenom FROM visite_infirmerie v " +
                     "LEFT JOIN fiche_eleve e ON v.id_eleve = e.id_eleve " +
                     "WHERE v.id_eleve = ? ORDER BY v.date_visite DESC, v.heure_visite DESC";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, idEleve);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                VisiteInfirmerie visite = mapResultSet(rs);
                try { visite.setNomEleve(rs.getString("nom")); } catch (Exception ignored) {}
                try { visite.setPrenomEleve(rs.getString("prenom")); } catch (Exception ignored) {}
                visites.add(visite);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des visites de l'élève : " + e.getMessage());
        }
        return visites;
    }

    public List<VisiteInfirmerie> getVisitesParDate(LocalDate date) {
        List<VisiteInfirmerie> visites = new ArrayList<>();
        String sql = "SELECT v.*, e.nom, e.prenom FROM visite_infirmerie v " +
                     "LEFT JOIN fiche_eleve e ON v.id_eleve = e.id_eleve " +
                     "WHERE v.date_visite = ? ORDER BY v.heure_visite";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                VisiteInfirmerie visite = mapResultSet(rs);
                try { visite.setNomEleve(rs.getString("nom")); } catch (Exception ignored) {}
                try { visite.setPrenomEleve(rs.getString("prenom")); } catch (Exception ignored) {}
                visites.add(visite);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des visites par date : " + e.getMessage());
        }
        return visites;
    }

    public boolean modifierVisite(VisiteInfirmerie visite) {
        String sql = "UPDATE visite_infirmerie SET id_eleve = ?, date_visite = ?, heure_visite = ?, motif = ?, id_infirmier = ? WHERE id_visite = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, visite.getIdEleve());
            stmt.setDate(2, Date.valueOf(visite.getDateVisite()));
            stmt.setTime(3, Time.valueOf(visite.getHeureVisite()));
            stmt.setString(4, visite.getMotif());
            if (visite.getIdInfirmier() == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, visite.getIdInfirmier());
            }
            stmt.setInt(6, visite.getIdVisite());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la visite : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerVisite(int idVisite) {
        String sql = "DELETE FROM visite_infirmerie WHERE id_visite = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, idVisite);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la visite : " + e.getMessage());
            return false;
        }
    }

    private VisiteInfirmerie mapResultSet(ResultSet rs) throws SQLException {
        VisiteInfirmerie visite = new VisiteInfirmerie();
        visite.setIdVisite(rs.getInt("id_visite"));
        visite.setIdEleve(rs.getInt("id_eleve"));

        Date d = rs.getDate("date_visite");
        if (d != null) visite.setDateVisite(d.toLocalDate());

        Time t = rs.getTime("heure_visite");
        if (t != null) visite.setHeureVisite(t.toLocalTime());

        visite.setMotif(rs.getString("motif"));

        int idInf = rs.getInt("id_infirmier");
        visite.setIdInfirmier(rs.wasNull() ? null : idInf);

        return visite;
    }
}
