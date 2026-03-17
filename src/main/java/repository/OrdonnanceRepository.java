package repository;

import database.Database;
import modele.Ordonnance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepository {

    public boolean ajouterOrdonnance(Ordonnance ordonnance) {
        String sql = "INSERT INTO ordonnance (id_dossier, date_ordonnance, contenu) VALUES (?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, ordonnance.getIdDossier());
            stmt.setObject(2, ordonnance.getDateOrdonnance());
            stmt.setString(3, ordonnance.getContenu());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'ordonnance : " + e.getMessage());
            return false;
        }
    }

    public Ordonnance trouverOrdonnanceParId(int id) {
        String sql = "SELECT * FROM ordonnance WHERE id_ordonnance = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'ordonnance: " + e.getMessage());
        }
        return null;
    }

    public List<Ordonnance> getOrdonnancesParDossier(int idDossier) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance WHERE id_dossier = ? ORDER BY date_ordonnance DESC";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, idDossier);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ordonnances.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ordonnances du dossier: " + e.getMessage());
        }
        return ordonnances;
    }

    public List<Ordonnance> getAllOrdonnances() {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance ORDER BY date_ordonnance DESC";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ordonnances.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ordonnances: " + e.getMessage());
        }
        return ordonnances;
    }

    public boolean modifierOrdonnance(Ordonnance ordonnance) {
        String sql = "UPDATE ordonnance SET id_dossier = ?, date_ordonnance = ?, contenu = ? WHERE id_ordonnance = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, ordonnance.getIdDossier());
            stmt.setObject(2, ordonnance.getDateOrdonnance());
            stmt.setString(3, ordonnance.getContenu());
            stmt.setInt(4, ordonnance.getIdOrdonnance());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'ordonnance: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerOrdonnance(int id) {
        String sql = "DELETE FROM ordonnance WHERE id_ordonnance = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'ordonnance: " + e.getMessage());
            return false;
        }
    }

    private Ordonnance mapResultSet(ResultSet rs) throws SQLException {
        return new Ordonnance(
            rs.getInt("id_ordonnance"),
            rs.getInt("id_dossier"),
            rs.getObject("date_ordonnance", LocalDateTime.class),
            rs.getString("contenu")
        );
    }
}
