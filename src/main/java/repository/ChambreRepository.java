package repository;

import database.Database;
import modele.Chambre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChambreRepository {

    public boolean ajouterChambre(Chambre chambre) {
        String sql = "INSERT INTO chambre (numeroChambre, disponible) VALUES (?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, chambre.getNumeroChambre());
            stmt.setBoolean(2, chambre.isDisponible());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la chambre : " + e.getMessage());
            return false;
        }
    }
    
    public Chambre trouverChambreParId(int id) {
        String sql = "SELECT * FROM chambre WHERE idChambre = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Chambre(
                    rs.getInt("idChambre"),
                    rs.getInt("numeroChambre"),
                    rs.getBoolean("disponible")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la chambre: " + e.getMessage());
        }
        return null;
    }
    
    public Chambre trouverChambreParNumero(int numero) {
        String sql = "SELECT * FROM chambre WHERE numeroChambre = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Chambre(
                    rs.getInt("idChambre"),
                    rs.getInt("numeroChambre"),
                    rs.getBoolean("disponible")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par numéro: " + e.getMessage());
        }
        return null;
    }
    
    public List<Chambre> getAllChambres() {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambre";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                chambres.add(new Chambre(
                    rs.getInt("idChambre"),
                    rs.getInt("numeroChambre"),
                    rs.getBoolean("disponible")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des chambres: " + e.getMessage());
        }
        return chambres;
    }
    
    public List<Chambre> getChambresDisponibles() {
        List<Chambre> chambres = new ArrayList<>();
        String sql = "SELECT * FROM chambre WHERE disponible = true";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                chambres.add(new Chambre(
                    rs.getInt("idChambre"),
                    rs.getInt("numeroChambre"),
                    rs.getBoolean("disponible")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des chambres disponibles: " + e.getMessage());
        }
        return chambres;
    }
    
    public boolean modifierChambre(Chambre chambre) {
        String sql = "UPDATE chambre SET numeroChambre = ?, disponible = ? WHERE idChambre = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, chambre.getNumeroChambre());
            stmt.setBoolean(2, chambre.isDisponible());
            stmt.setInt(3, chambre.getIdChambre());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la chambre: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerChambre(int id) {
        String sql = "DELETE FROM chambre WHERE idChambre = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la chambre: " + e.getMessage());
            return false;
        }
    }
}
