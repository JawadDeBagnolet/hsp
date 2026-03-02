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
        String sql = "INSERT INTO fiche_patient (nom, prenom, numSecu, email, tel, rue, cp, ville) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setString(1, fichePatient.getNom());
            stmt.setString(2, fichePatient.getPrenom());
            stmt.setLong(3, fichePatient.getNum_secu());
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
        String sql = "SELECT * FROM fiche_patient WHERE id_patient = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                FichePatient patient = new FichePatient(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getLong("num_secu"),
                    rs.getString("email"),
                    rs.getInt("tel"),
                    rs.getString("rue"),
                    rs.getInt("cp"),
                    rs.getString("ville")
                );
                patient.setIdFichePatient(rs.getInt("id_patient"));
                return patient;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la fiche patient: " + e.getMessage());
        }
        return null;
    }
    
    public FichePatient trouverFichePatientParNumSecu(int numSecu) {
        String sql = "SELECT * FROM fiche_patient WHERE num_secu = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, numSecu);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                FichePatient patient = new FichePatient(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getLong("num_secu"),
                    rs.getString("email"),
                    rs.getInt("tel"),
                    rs.getString("rue"),
                    rs.getInt("cp"),
                    rs.getString("ville")
                );
                patient.setIdFichePatient(rs.getInt("id_patient"));
                return patient;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par numéro de sécurité sociale: " + e.getMessage());
        }
        return null;
    }
    
    public List<FichePatient> getAllFichePatients() {
        List<FichePatient> fichePatients = new ArrayList<>();
        String sql = "SELECT * FROM fiche_patient";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Afficher les métadonnées des colonnes
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Colonnes dans la table fiche_patient:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("  Colonne " + i + ": " + metaData.getColumnName(i) + " (Type: " + metaData.getColumnTypeName(i) + ")");
            }
            
            while (rs.next()) {
                System.out.println("\nPatient trouvé - ID: " + rs.getString("id_patient"));
                
                // Récupérer les données en tant que String pour éviter les erreurs de conversion
                String idStr = rs.getString("id_patient");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String numSecuStr = rs.getString("num_secu");
                String email = rs.getString("email");
                String telStr = rs.getString("tel");
                String rue = rs.getString("rue");
                String cpStr = rs.getString("cp");
                String ville = rs.getString("ville");
                
                System.out.println("  Nom: '" + nom + "' (null? " + (nom == null) + ")");
                System.out.println("  Prénom: '" + prenom + "' (null? " + (prenom == null) + ")");
                System.out.println("  Email: '" + email + "' (null? " + (email == null) + ")");
                
                // Convertir en entier/long avec gestion des erreurs
                int id = 0;
                long numSecu = 0;
                int tel = 0;
                int cp = 0;
                
                try {
                    if (idStr != null) id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur conversion ID: " + idStr);
                }
                
                try {
                    if (numSecuStr != null) numSecu = Long.parseLong(numSecuStr);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur conversion numSecu: " + numSecuStr);
                }
                
                try {
                    if (telStr != null) tel = Integer.parseInt(telStr);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur conversion tel: " + telStr);
                }
                
                try {
                    if (cpStr != null) cp = Integer.parseInt(cpStr);
                } catch (NumberFormatException e) {
                    System.err.println("Erreur conversion cp: " + cpStr);
                }
                
                FichePatient patient = new FichePatient(
                    nom != null ? nom : "",
                    prenom != null ? prenom : "",
                    numSecu,
                    email != null ? email : "",
                    tel,
                    rue != null ? rue : "",
                    cp,
                    ville != null ? ville : ""
                );
                patient.setIdFichePatient(id);
                fichePatients.add(patient);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fiches patients: " + e.getMessage());
        }
        System.out.println("\nTotal patients récupérés: " + fichePatients.size());
        return fichePatients;
    }
    
    public boolean modifierFichePatient(FichePatient fichePatient) {
        String sql = "UPDATE fiche_patient SET nom = ?, prenom = ?, num_secu = ?, email = ?, tel = ?, rue = ?, cp = ?, ville = ? WHERE id_patient = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setString(1, fichePatient.getNom());
            stmt.setString(2, fichePatient.getPrenom());
            stmt.setLong(3, fichePatient.getNum_secu());
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
        String sql = "DELETE FROM fiche_patient WHERE id_patient = ?";
        
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
