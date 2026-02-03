package repository;

import database.Database;
import modele.Ordonnance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepository {

    public boolean ajouterOrdonnance(Ordonnance ordonnance) {
        String sql = "INSERT INTO ordonnance (dateOrdonnance, contenuOrdonnance) VALUES (?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, ordonnance.getDateOrdonnance());
            stmt.setString(2, ordonnance.getContenuOrdonnance());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'ordonnance : " + e.getMessage());
            return false;
        }
    }

    public Ordonnance trouverOrdonnanceParId(int id) {
        String sql = "SELECT * FROM ordonnance WHERE idOrdonnance = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Ordonnance(
                    rs.getInt("idOrdonnance"),
                    rs.getInt("dateOrdonnance"),
                    rs.getString("contenuOrdonnance")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'ordonnance: " + e.getMessage());
        }
        return null;
    }

    public List<Ordonnance> getAllOrdonnances() {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT * FROM ordonnance";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ordonnances.add(new Ordonnance(
                    rs.getInt("idOrdonnance"),
                    rs.getInt("dateOrdonnance"),
                    rs.getString("contenuOrdonnance")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ordonnances: " + e.getMessage());
        }
        return ordonnances;
    }

    public boolean modifierOrdonnance(Ordonnance ordonnance) {
        String sql = "UPDATE ordonnance SET dateOrdonnance = ?, contenuOrdonnance = ? WHERE idOrdonnance = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, ordonnance.getDateOrdonnance());
            stmt.setString(2, ordonnance.getContenuOrdonnance());
            stmt.setInt(3, ordonnance.getIdOrdonnance());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'ordonnance: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerOrdonnance(int id) {
        String sql = "DELETE FROM ordonnance WHERE idOrdonnance = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'ordonnance: " + e.getMessage());
            return false;
        }
    }
}
