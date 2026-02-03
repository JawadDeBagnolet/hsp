package repository;

import database.Database;
import modele.FicheProduit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FicheProduitRepository {

    public boolean ajouterFicheProduit(FicheProduit produit) {
        String sql = "INSERT INTO ficheproduit (libelle, description, nivDangerosite, stockActuel) VALUES (?, ?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, produit.getLibelle());
            stmt.setString(2, produit.getDescription());
            stmt.setInt(3, produit.getNivDangerosite());
            stmt.setInt(4, produit.getStockActuel());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la fiche produit : " + e.getMessage());
            return false;
        }
    }

    public FicheProduit trouverFicheProduitParId(int id) {
        String sql = "SELECT * FROM ficheproduit WHERE idProduit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FicheProduit(
                    rs.getInt("idProduit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getInt("nivDangerosite"),
                    rs.getInt("stockActuel")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la fiche produit: " + e.getMessage());
        }
        return null;
    }

    public FicheProduit trouverFicheProduitParLibelle(String libelle) {
        String sql = "SELECT * FROM ficheproduit WHERE libelle = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, libelle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FicheProduit(
                    rs.getInt("idProduit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getInt("nivDangerosite"),
                    rs.getInt("stockActuel")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par libellé: " + e.getMessage());
        }
        return null;
    }

    public List<FicheProduit> getAllFicheProduits() {
        List<FicheProduit> produits = new ArrayList<>();
        String sql = "SELECT * FROM ficheproduit";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(new FicheProduit(
                    rs.getInt("idProduit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getInt("nivDangerosite"),
                    rs.getInt("stockActuel")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fiches produit: " + e.getMessage());
        }
        return produits;
    }

    public boolean modifierFicheProduit(FicheProduit produit) {
        String sql = "UPDATE ficheproduit SET libelle = ?, description = ?, nivDangerosite = ?, stockActuel = ? WHERE idProduit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, produit.getLibelle());
            stmt.setString(2, produit.getDescription());
            stmt.setInt(3, produit.getNivDangerosite());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getIdProduit());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la fiche produit: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerFicheProduit(int id) {
        String sql = "DELETE FROM ficheproduit WHERE idProduit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la fiche produit: " + e.getMessage());
            return false;
        }
    }
}
