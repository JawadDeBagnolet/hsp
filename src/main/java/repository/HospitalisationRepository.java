package repository;

import database.Database;
import modele.Hospitalisation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HospitalisationRepository {

    public boolean ajouterHospitalisation(Hospitalisation hospitalisation) {
        String sql = "INSERT INTO hospitalisation (dateDebut, dateFin, desc_maladie) VALUES (?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, hospitalisation.getDateDebut());
            stmt.setInt(2, hospitalisation.getDateFin());
            stmt.setString(3, hospitalisation.getDesc_maladie());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'hospitalisation : " + e.getMessage());
            return false;
        }
    }
    
    public Hospitalisation trouverHospitalisationParId(int id) {
        String sql = "SELECT * FROM hospitalisation WHERE idHospitalisation = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Hospitalisation(
                    rs.getInt("idHospitalisation"),
                    rs.getInt("dateDebut"),
                    rs.getInt("dateFin"),
                    rs.getString("desc_maladie")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'hospitalisation: " + e.getMessage());
        }
        return null;
    }
    
    public List<Hospitalisation> trouverHospitalisationsParDateDebut(int dateDebut) {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisation WHERE dateDebut = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, dateDebut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                hospitalisations.add(new Hospitalisation(
                    rs.getInt("idHospitalisation"),
                    rs.getInt("dateDebut"),
                    rs.getInt("dateFin"),
                    rs.getString("desc_maladie")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par date de début: " + e.getMessage());
        }
        return hospitalisations;
    }
    
    public List<Hospitalisation> getAllHospitalisations() {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisation";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                hospitalisations.add(new Hospitalisation(
                    rs.getInt("idHospitalisation"),
                    rs.getInt("dateDebut"),
                    rs.getInt("dateFin"),
                    rs.getString("desc_maladie")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des hospitalisations: " + e.getMessage());
        }
        return hospitalisations;
    }
    
    public List<Hospitalisation> getHospitalisationsEnCours() {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisation WHERE dateFin > ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                hospitalisations.add(new Hospitalisation(
                    rs.getInt("idHospitalisation"),
                    rs.getInt("dateDebut"),
                    rs.getInt("dateFin"),
                    rs.getString("desc_maladie")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des hospitalisations en cours: " + e.getMessage());
        }
        return hospitalisations;
    }
    
    public boolean modifierHospitalisation(Hospitalisation hospitalisation) {
        String sql = "UPDATE hospitalisation SET dateDebut = ?, dateFin = ?, desc_maladie = ? WHERE idHospitalisation = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, hospitalisation.getDateDebut());
            stmt.setInt(2, hospitalisation.getDateFin());
            stmt.setString(3, hospitalisation.getDesc_maladie());
            stmt.setInt(4, hospitalisation.getIdHospitalisation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'hospitalisation: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerHospitalisation(int id) {
        String sql = "DELETE FROM hospitalisation WHERE idHospitalisation = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'hospitalisation: " + e.getMessage());
            return false;
        }
    }
}
