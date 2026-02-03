package repository;

import database.Database;
import modele.Demande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DemandeRepository {

    public boolean ajouterDemande(Demande demande) {
        String sql = "INSERT INTO demande (dateDemande, statut, quantite) VALUES (?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, demande.getDateDemande());
            stmt.setBoolean(2, demande.isStatut());
            stmt.setInt(3, demande.getQuantite());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la demande : " + e.getMessage());
            return false;
        }
    }
    
    public Demande trouverDemandeParId(int id) {
        String sql = "SELECT * FROM demande WHERE idDemande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Demande(
                    rs.getInt("idDemande"),
                    rs.getInt("dateDemande"),
                    rs.getBoolean("statut"),
                    rs.getInt("quantite")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la demande: " + e.getMessage());
        }
        return null;
    }
    
    public List<Demande> trouverDemandesParStatut(boolean statut) {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande WHERE statut = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setBoolean(1, statut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                demandes.add(new Demande(
                    rs.getInt("idDemande"),
                    rs.getInt("dateDemande"),
                    rs.getBoolean("statut"),
                    rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par statut: " + e.getMessage());
        }
        return demandes;
    }
    
    public List<Demande> getAllDemandes() {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                demandes.add(new Demande(
                    rs.getInt("idDemande"),
                    rs.getInt("dateDemande"),
                    rs.getBoolean("statut"),
                    rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
        return demandes;
    }
    
    public List<Demande> getDemandesEnAttente() {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande WHERE statut = false";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                demandes.add(new Demande(
                    rs.getInt("idDemande"),
                    rs.getInt("dateDemande"),
                    rs.getBoolean("statut"),
                    rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes en attente: " + e.getMessage());
        }
        return demandes;
    }
    
    public boolean modifierDemande(Demande demande) {
        String sql = "UPDATE demande SET dateDemande = ?, statut = ?, quantite = ? WHERE idDemande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, demande.getDateDemande());
            stmt.setBoolean(2, demande.isStatut());
            stmt.setInt(3, demande.getQuantite());
            stmt.setInt(4, demande.getIdDemande());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la demande: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerDemande(int id) {
        String sql = "DELETE FROM demande WHERE idDemande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la demande: " + e.getMessage());
            return false;
        }
    }
}
