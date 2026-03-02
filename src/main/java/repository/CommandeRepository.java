package repository;

import database.Database;
import modele.Commande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandeRepository {

    public boolean ajouterCommande(Commande commande) {
        String sql = "INSERT INTO commande (id_user, numCommande, libelle) VALUES (?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, commande.getIdUser());
            stmt.setInt(2, commande.getNumCommande());
            stmt.setString(3, commande.getLibelle());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
            return false;
        }
    }
    
    public Commande trouverCommandeParId(int id) {
        String sql = "SELECT * FROM commande WHERE id_commande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Commande(
                    rs.getInt("id_commande"),
                    rs.getInt("id_user"),
                    rs.getInt("numCommande"),
                    rs.getString("libelle")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la commande: " + e.getMessage());
        }
        return null;
    }
    
    public Commande trouverCommandeParNumero(int numero) {
        String sql = "SELECT * FROM commande WHERE numCommande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Commande(
                    rs.getInt("id_commande"),
                    rs.getInt("id_user"),
                    rs.getInt("numCommande"),
                    rs.getString("libelle")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par numéro: " + e.getMessage());
        }
        return null;
    }
    
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                commandes.add(new Commande(
                    rs.getInt("id_commande"),
                    rs.getInt("id_user"),
                    rs.getInt("numCommande"),
                    rs.getString("libelle")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
        return commandes;
    }
    
    public boolean modifierCommande(Commande commande) {
        String sql = "UPDATE commande SET id_user = ?, numCommande = ?, libelle = ? WHERE id_commande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, commande.getIdUser());
            stmt.setInt(2, commande.getNumCommande());
            stmt.setString(3, commande.getLibelle());
            stmt.setInt(4, commande.getIdCommande());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la commande: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerCommande(int id) {
        String sql = "DELETE FROM commande WHERE id_commande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la commande: " + e.getMessage());
            return false;
        }
    }
}
