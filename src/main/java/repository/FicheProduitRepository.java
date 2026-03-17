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
        String sql = "INSERT INTO fiche_produit (libelle, description, niveau_dangerosite, stock) VALUES (?, ?, ?, ?)";

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
        String sql = "SELECT * FROM fiche_produit WHERE id_produit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FicheProduit(
                    rs.getInt("id_produit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getInt("niveau_dangerosite"),
                    rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la fiche produit: " + e.getMessage());
        }
        return null;
    }

    public FicheProduit trouverFicheProduitParLibelle(String libelle) {
        String sql = "SELECT * FROM fiche_produit WHERE libelle = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, libelle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new FicheProduit(
                    rs.getInt("id_produit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getInt("niveau_dangerosite"),
                    rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par libellé: " + e.getMessage());
        }
        return null;
    }

    public List<FicheProduit> getAllFicheProduits() {
        List<FicheProduit> produits = new ArrayList<>();
        String sql = "SELECT * FROM fiche_produit";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(new FicheProduit(
                    rs.getInt("id_produit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getInt("niveau_dangerosite"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fiches produit: " + e.getMessage());
        }
        return produits;
    }

    public boolean modifierFicheProduit(FicheProduit produit) {
        String sql = "UPDATE fiche_produit SET libelle = ?, description = ?, niveau_dangerosite = ? WHERE id_produit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, produit.getLibelle());
            stmt.setString(2, produit.getDescription());
            stmt.setInt(3, produit.getNivDangerosite());
            stmt.setInt(4, produit.getIdProduit());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la fiche produit: " + e.getMessage());
            return false;
        }
    }

    public boolean decrementerStock(int idProduit, int quantite) {
        String sql = "UPDATE fiche_produit SET stock = stock - ? WHERE id_produit = ? AND stock >= ?";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, idProduit);
            stmt.setInt(3, quantite);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur decrementerStock: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerFicheProduit(int id) {
        String sql = "DELETE FROM fiche_produit WHERE id_produit = ?";

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
