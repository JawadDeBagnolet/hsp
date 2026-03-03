package repository;

import database.Database;
import modele.FournisseurProduit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FournisseurProduitRepository {

    public boolean ajouterFournisseurProduit(FournisseurProduit fournisseurProduit) {
        String sql = "INSERT INTO fournisseur_produit (id_fournisseur, id_produit, prix) VALUES (?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, fournisseurProduit.getIdFournisseur());
            stmt.setInt(2, fournisseurProduit.getIdProduit());
            stmt.setDouble(3, fournisseurProduit.getPrix());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du fournisseur produit : " + e.getMessage());
            return false;
        }
    }
    
    public List<FournisseurProduit> getFournisseursParProduit(int idProduit) {
        List<FournisseurProduit> fournisseursProduits = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur_produit WHERE id_produit = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, idProduit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                fournisseursProduits.add(new FournisseurProduit(
                    rs.getInt("id_fournisseur_produit"),
                    rs.getInt("id_fournisseur"),
                    rs.getInt("id_produit"),
                    rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fournisseurs pour le produit: " + e.getMessage());
        }
        return fournisseursProduits;
    }
    
    public List<FournisseurProduit> getProduitsParFournisseur(int idFournisseur) {
        List<FournisseurProduit> fournisseursProduits = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur_produit WHERE id_fournisseur = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, idFournisseur);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                fournisseursProduits.add(new FournisseurProduit(
                    rs.getInt("id_fournisseur_produit"),
                    rs.getInt("id_fournisseur"),
                    rs.getInt("id_produit"),
                    rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits pour le fournisseur: " + e.getMessage());
        }
        return fournisseursProduits;
    }
    
    public boolean modifierFournisseurProduit(FournisseurProduit fournisseurProduit) {
        String sql = "UPDATE fournisseur_produit SET id_fournisseur = ?, id_produit = ?, prix = ? WHERE id_fournisseur_produit = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, fournisseurProduit.getIdFournisseur());
            stmt.setInt(2, fournisseurProduit.getIdProduit());
            stmt.setDouble(3, fournisseurProduit.getPrix());
            stmt.setInt(4, fournisseurProduit.getIdFournisseurProduit());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du fournisseur produit: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerFournisseurProduit(int id) {
        String sql = "DELETE FROM fournisseur_produit WHERE id_fournisseur_produit = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur produit: " + e.getMessage());
            return false;
        }
    }
    
    public List<FournisseurProduit> getAllFournisseursProduits() {
        List<FournisseurProduit> fournisseursProduits = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur_produit";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                fournisseursProduits.add(new FournisseurProduit(
                    rs.getInt("id_fournisseur_produit"),
                    rs.getInt("id_fournisseur"),
                    rs.getInt("id_produit"),
                    rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les fournisseurs produits: " + e.getMessage());
        }
        return fournisseursProduits;
    }
}
