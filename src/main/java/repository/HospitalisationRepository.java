package repository;

import database.Database;
import modele.Hospitalisation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HospitalisationRepository {

    public boolean ajouterHospitalisation(Hospitalisation hospitalisation) {
        String sql = "INSERT INTO hospitalisation (id_dossier, id_chambre, date_debut, date_fin, desc_maladie) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, hospitalisation.getIdDossier());
            stmt.setInt(2, hospitalisation.getIdChambre());
            stmt.setObject(3, hospitalisation.getDateDebut());
            stmt.setObject(4, hospitalisation.getDateFin());
            stmt.setString(5, hospitalisation.getDesc_maladie());
            
            int result = stmt.executeUpdate();
            
            // Récupérer l'ID auto-généré
            if (result > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        hospitalisation.setIdHospitalisation(generatedId);
                        System.out.println("ID généré pour l'hospitalisation: " + generatedId);
                    }
                }
            }
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'hospitalisation : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Hospitalisation trouverHospitalisationParId(int id) {
        String sql = "SELECT * FROM hospitalisation WHERE id_hospitalisation = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Hospitalisation(
                    rs.getInt("id_hospitalisation"),
                    rs.getInt("id_dossier"),
                    rs.getInt("id_chambre"),
                    rs.getObject("date_debut", LocalDateTime.class),
                    rs.getObject("date_fin", LocalDateTime.class),
                    rs.getString("desc_maladie")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'hospitalisation: " + e.getMessage());
        }
        return null;
    }
    
    public List<Hospitalisation> trouverHospitalisationsParDateDebut(LocalDateTime dateDebut) {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisation WHERE date_debut = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setObject(1, dateDebut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                hospitalisations.add(new Hospitalisation(
                    rs.getInt("id_hospitalisation"),
                    rs.getInt("id_dossier"),
                    rs.getInt("id_chambre"),
                    rs.getObject("date_debut", LocalDateTime.class),
                    rs.getObject("date_fin", LocalDateTime.class),
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
        
        System.out.println("Requête SQL pour toutes les hospitalisations: " + sql);
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("Exécution de la requête hospitalisations...");
            
            while (rs.next()) {
                int idHospitalisation = rs.getInt("id_hospitalisation");
                int idDossier = rs.getInt("id_dossier");
                int idChambre = rs.getInt("id_chambre");
                LocalDateTime dateDebut = rs.getObject("date_debut", LocalDateTime.class);
                LocalDateTime dateFin = rs.getObject("date_fin", LocalDateTime.class);
                String descMaladie = rs.getString("desc_maladie");
                
                System.out.println("Hospitalisation trouvée - ID: " + idHospitalisation + 
                    ", Dossier: " + idDossier + 
                    ", Chambre: " + idChambre + 
                    ", Début: " + dateDebut + 
                    ", Fin: " + dateFin + 
                    ", Description: " + descMaladie);
                
                hospitalisations.add(new Hospitalisation(idHospitalisation, idDossier, idChambre, dateDebut, dateFin, descMaladie));
            }
            
            System.out.println("Total hospitalisations trouvées: " + hospitalisations.size());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des hospitalisations: " + e.getMessage());
            e.printStackTrace();
        }
        return hospitalisations;
    }
    
    public List<Hospitalisation> getHospitalisationsEnCours() {
        List<Hospitalisation> hospitalisations = new ArrayList<>();
        String sql = "SELECT * FROM hospitalisation WHERE date_fin IS NULL OR date_fin > ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setObject(1, LocalDateTime.now());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                hospitalisations.add(new Hospitalisation(
                    rs.getInt("id_hospitalisation"),
                    rs.getInt("id_dossier"),
                    rs.getInt("id_chambre"),
                    rs.getObject("date_debut", LocalDateTime.class),
                    rs.getObject("date_fin", LocalDateTime.class),
                    rs.getString("desc_maladie")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des hospitalisations en cours: " + e.getMessage());
        }
        return hospitalisations;
    }
    
    public boolean modifierHospitalisation(Hospitalisation hospitalisation) {
        String sql = "UPDATE hospitalisation SET id_dossier = ?, id_chambre = ?, date_debut = ?, date_fin = ?, desc_maladie = ? WHERE id_hospitalisation = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, hospitalisation.getIdDossier());
            stmt.setInt(2, hospitalisation.getIdChambre());
            stmt.setObject(3, hospitalisation.getDateDebut());
            stmt.setObject(4, hospitalisation.getDateFin());
            stmt.setString(5, hospitalisation.getDesc_maladie());
            stmt.setInt(6, hospitalisation.getIdHospitalisation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'hospitalisation: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerHospitalisation(int id) {
        String sql = "DELETE FROM hospitalisation WHERE id_hospitalisation = ?";
        
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
