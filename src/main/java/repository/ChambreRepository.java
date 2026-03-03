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
        String sql = "SELECT * FROM chambre WHERE id_chambre = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Chambre(
                    rs.getInt("id_chambre"),
                    rs.getInt("numero_chambre"),
                    rs.getBoolean("disponible")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la chambre: " + e.getMessage());
        }
        return null;
    }
    
    public Chambre trouverChambreParNumero(int numero) {
        String sql = "SELECT * FROM chambre WHERE numero_chambre = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Chambre(
                    rs.getInt("id_chambre"),
                    rs.getInt("numero_chambre"),
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
                    rs.getInt("id_chambre"),
                    rs.getInt("numero_chambre"),
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
        String sql = "SELECT * FROM chambre WHERE disponible = 1";
        
        System.out.println("Requête SQL pour chambres disponibles: " + sql);
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("Exécution de la requête...");
            
            while (rs.next()) {
                int idChambre = rs.getInt("id_chambre");
                int numeroChambre = rs.getInt("numero_chambre");
                boolean disponible = rs.getBoolean("disponible");
                
                System.out.println("Chambre trouvée - ID: " + idChambre + ", Numéro: " + numeroChambre + ", Disponible: " + disponible);
                
                chambres.add(new Chambre(idChambre, numeroChambre, disponible));
            }
            
            System.out.println("Total chambres disponibles trouvées: " + chambres.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des chambres disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        return chambres;
    }
    
    public void testerToutesLesChambres() {
        System.out.println("=== TEST DE TOUTES LES CHAMBRES ===");
        String sql = "SELECT * FROM chambre";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int idChambre = rs.getInt("id_chambre");
                int numeroChambre = rs.getInt("numero_chambre");
                boolean disponible = rs.getBoolean("disponible");
                int disponibleInt = rs.getInt("disponible");
                
                System.out.println("Chambre " + numeroChambre + " (ID: " + idChambre + ") - disponible (boolean): " + disponible + " - disponible (int): " + disponibleInt);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du test des chambres: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== FIN DU TEST ===");
    }
    
    public boolean modifierChambre(Chambre chambre) {
        String sql = "UPDATE chambre SET numero_chambre = ?, disponible = ? WHERE id_chambre = ?";
        
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
        String sql = "DELETE FROM chambre WHERE id_chambre = ?";
        
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
