package repository;

import database.Database;
import modele.Demande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DemandeRepository {

    public boolean ajouterDemande(Demande demande) {
        String sql = "INSERT INTO demande (id_user, date_demande, quantite) VALUES (?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, demande.getIdUser());
            stmt.setObject(2, demande.getDateDemande());
            stmt.setInt(3, demande.getQuantite());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la demande : " + e.getMessage());
            return false;
        }
    }
    
    public Demande trouverDemandeParId(int id) {
        String sql = "SELECT * FROM demande WHERE id_demande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Demande(
                    rs.getInt("id_demande"),
                    rs.getInt("id_user"),
                    rs.getObject("date_demande", LocalDateTime.class),
                    rs.getInt("quantite")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la demande: " + e.getMessage());
        }
        return null;
    }
    
    public List<Demande> getAllDemandes() {
        List<Demande> demandes = new ArrayList<>();
        String sql = "SELECT * FROM demande";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                demandes.add(new Demande(
                    rs.getInt("id_demande"),
                    rs.getInt("id_user"),
                    rs.getObject("date_demande", LocalDateTime.class),
                    rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
        return demandes;
    }
    
    public boolean modifierDemande(Demande demande) {
        String sql = "UPDATE demande SET id_user = ?, date_demande = ?, quantite = ? WHERE id_demande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, demande.getIdUser());
            stmt.setObject(2, demande.getDateDemande());
            stmt.setInt(3, demande.getQuantite());
            stmt.setInt(4, demande.getIdDemande());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la demande: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerDemande(int id) {
        String sql = "DELETE FROM demande WHERE id_demande = ?";
        
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
