package repository;

import database.Database;
import modele.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduitRepository {

    public boolean ajouterProduit(Produit produit) {
        String sql = "INSERT INTO produit (nom, description, prix, quantite, id_fournisseur) VALUES (?, ?, ?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantite());
            stmt.setInt(5, produit.getIdFournisseur());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
            return false;
        }
    }

    public Produit trouverProduitParId(int id) {
        String sql = "SELECT * FROM produit WHERE idProduit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Produit(
                    rs.getInt("idProduit"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite"),
                    rs.getInt("id_fournisseur")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit: " + e.getMessage());
        }
        return null;
    }

    public List<Produit> getProduitsParFournisseur(int idFournisseur) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE id_fournisseur = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, idFournisseur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produits.add(new Produit(
                    rs.getInt("idProduit"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite"),
                    rs.getInt("id_fournisseur")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits du fournisseur: " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(new Produit(
                    rs.getInt("idProduit"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getInt("quantite"),
                    rs.getInt("id_fournisseur")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits: " + e.getMessage());
        }
        return produits;
    }

    public boolean modifierProduit(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, description = ?, prix = ?, quantite = ?, id_fournisseur = ? WHERE idProduit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantite());
            stmt.setInt(5, produit.getIdFournisseur());
            stmt.setInt(6, produit.getIdProduit());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du produit: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerProduit(int id) {
        String sql = "DELETE FROM produit WHERE idProduit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
            return false;
        }
    }
}
