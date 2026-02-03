package repository;

import database.Database;
import modele.FichePatient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FichePatientRepository {

    public boolean ajouterFichePatient(FichePatient fichePatient) {
        String sql = "INSERT INTO fichePatient (nom, prenom, numSecu, email, tel, rue, cp, ville) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setString(1, fichePatient.getNom());
            stmt.setString(2, fichePatient.getPrenom());
            stmt.setInt(3, fichePatient.getNumSecu());
            stmt.setString(4, fichePatient.getEmail());
            stmt.setInt(5, fichePatient.getTel());
            stmt.setString(6, fichePatient.getRue());
            stmt.setInt(7, fichePatient.getCp());
            stmt.setString(8, fichePatient.getVille());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la fiche patient : " + e.getMessage());
            return false;
        }
    }
    
    public FichePatient trouverFichePatientParId(int id) {
        String sql = "SELECT * FROM fichePatient WHERE idFichePatient = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new FichePatient(
                    rs.getInt("idFichePatient"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("numSecu"),
                    rs.getString("email"),
                    rs.getInt("tel"),
                    rs.getString("rue"),
                    rs.getInt("cp"),
                    rs.getString("ville")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la fiche patient: " + e.getMessage());
        }
        return null;
    }
    
    public FichePatient trouverFichePatientParNumSecu(int numSecu) {
        String sql = "SELECT * FROM fichePatient WHERE numSecu = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, numSecu);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new FichePatient(
                    rs.getInt("idFichePatient"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("numSecu"),
                    rs.getString("email"),
                    rs.getInt("tel"),
                    rs.getString("rue"),
                    rs.getInt("cp"),
                    rs.getString("ville")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par numéro de sécurité sociale: " + e.getMessage());
        }
        return null;
    }
    
    public List<FichePatient> getAllFichePatients() {
        List<FichePatient> fichePatients = new ArrayList<>();
        String sql = "SELECT * FROM fichePatient";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                fichePatients.add(new FichePatient(
                    rs.getInt("idFichePatient"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("numSecu"),
                    rs.getString("email"),
                    rs.getInt("tel"),
                    rs.getString("rue"),
                    rs.getInt("cp"),
                    rs.getString("ville")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fiches patients: " + e.getMessage());
        }
        return fichePatients;
    }
    
    public boolean modifierFichePatient(FichePatient fichePatient) {
        String sql = "UPDATE fichePatient SET nom = ?, prenom = ?, numSecu = ?, email = ?, tel = ?, rue = ?, cp = ?, ville = ? WHERE idFichePatient = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setString(1, fichePatient.getNom());
            stmt.setString(2, fichePatient.getPrenom());
            stmt.setInt(3, fichePatient.getNumSecu());
            stmt.setString(4, fichePatient.getEmail());
            stmt.setInt(5, fichePatient.getTel());
            stmt.setString(6, fichePatient.getRue());
            stmt.setInt(7, fichePatient.getCp());
            stmt.setString(8, fichePatient.getVille());
            stmt.setInt(9, fichePatient.getIdFichePatient());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la fiche patient: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerFichePatient(int id) {
        String sql = "DELETE FROM fichePatient WHERE idFichePatient = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la fiche patient: " + e.getMessage());
            return false;
        }
    }
}
