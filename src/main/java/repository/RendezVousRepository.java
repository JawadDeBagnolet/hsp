package repository;

import database.Database;
import modele.RendezVous;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RendezVousRepository {

    public boolean ajouterRendezVous(RendezVous rdv) {
        String sql = "INSERT INTO rendez_vous (id_patient, id_medecin, date_heure, motif, statut, notes) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, rdv.getIdPatient());
            stmt.setInt(2, rdv.getIdMedecin());
            stmt.setObject(3, rdv.getDateHeure());
            stmt.setString(4, rdv.getMotif());
            stmt.setString(5, rdv.getStatut());
            stmt.setString(6, rdv.getNotes());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du rendez-vous : " + e.getMessage());
            return false;
        }
    }
    
    public RendezVous trouverRendezVousParId(int id) {
        String sql = "SELECT * FROM rendez_vous WHERE id_rdv = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new RendezVous(
                    rs.getInt("id_rdv"),
                    rs.getInt("id_patient"),
                    rs.getInt("id_medecin"),
                    rs.getObject("date_heure", LocalDateTime.class),
                    rs.getString("motif"),
                    rs.getString("statut"),
                    rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du rendez-vous: " + e.getMessage());
        }
        return null;
    }
    
    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> rdvs = new ArrayList<>();
        String sql = "SELECT * FROM rendez_vous ORDER BY date_heure";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("📡 Exécution de la requête rendez-vous: " + sql);
            
            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                    rs.getInt("id_rdv"),
                    rs.getInt("id_patient"),
                    rs.getInt("id_medecin"),
                    rs.getObject("date_heure", LocalDateTime.class),
                    rs.getString("motif"),
                    rs.getString("statut"),
                    rs.getString("notes")
                );
                rdvs.add(rdv);
                System.out.println("✅ Rendez-vous récupéré: " + rdv.toString());
            }
            
            System.out.println("📊 Total de rendez-vous récupérés: " + rdvs.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des rendez-vous: " + e.getMessage());
            e.printStackTrace();
        }
        return rdvs;
    }
    
    public List<RendezVous> getRendezVousParSemaine(java.time.LocalDate debutSemaine) {
        List<RendezVous> rdvs = new ArrayList<>();
        String sql = "SELECT * FROM rendez_vous WHERE date_heure >= ? AND date_heure <= ? ORDER BY date_heure";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            LocalDateTime debut = debutSemaine.atStartOfDay();
            LocalDateTime fin = debutSemaine.plusDays(6).atTime(23, 59, 59);
            
            stmt.setObject(1, debut);
            stmt.setObject(2, fin);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("📡 Recherche rendez-vous semaine du " + debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                             " au " + fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                    rs.getInt("id_rdv"),
                    rs.getInt("id_patient"),
                    rs.getInt("id_medecin"),
                    rs.getObject("date_heure", LocalDateTime.class),
                    rs.getString("motif"),
                    rs.getString("statut"),
                    rs.getString("notes")
                );
                rdvs.add(rdv);
            }
            
            System.out.println("📊 " + rdvs.size() + " rendez-vous trouvés pour cette semaine");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des rendez-vous de la semaine: " + e.getMessage());
            e.printStackTrace();
        }
        return rdvs;
    }
    
    public List<RendezVous> getRendezVousParMedecin(int idMedecin) {
        List<RendezVous> rdvs = new ArrayList<>();
        String sql = "SELECT * FROM rendez_vous WHERE id_medecin = ? ORDER BY date_heure";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, idMedecin);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                    rs.getInt("id_rdv"),
                    rs.getInt("id_patient"),
                    rs.getInt("id_medecin"),
                    rs.getObject("date_heure", LocalDateTime.class),
                    rs.getString("motif"),
                    rs.getString("statut"),
                    rs.getString("notes")
                );
                rdvs.add(rdv);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des rendez-vous du médecin: " + e.getMessage());
        }
        return rdvs;
    }
    
    public List<RendezVous> getRendezVousParPatient(int idPatient) {
        List<RendezVous> rdvs = new ArrayList<>();
        String sql = "SELECT * FROM rendez_vous WHERE id_patient = ? ORDER BY date_heure";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, idPatient);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                    rs.getInt("id_rdv"),
                    rs.getInt("id_patient"),
                    rs.getInt("id_medecin"),
                    rs.getObject("date_heure", LocalDateTime.class),
                    rs.getString("motif"),
                    rs.getString("statut"),
                    rs.getString("notes")
                );
                rdvs.add(rdv);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des rendez-vous du patient: " + e.getMessage());
        }
        return rdvs;
    }
    
    public boolean modifierRendezVous(RendezVous rdv) {
        String sql = "UPDATE rendez_vous SET id_patient = ?, id_medecin = ?, date_heure = ?, motif = ?, statut = ?, notes = ? WHERE id_rdv = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, rdv.getIdPatient());
            stmt.setInt(2, rdv.getIdMedecin());
            stmt.setObject(3, rdv.getDateHeure());
            stmt.setString(4, rdv.getMotif());
            stmt.setString(5, rdv.getStatut());
            stmt.setString(6, rdv.getNotes());
            stmt.setInt(7, rdv.getIdRdv());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du rendez-vous: " + e.getMessage());
            return false;
        }
    }
    
    public boolean supprimerRendezVous(int id) {
        String sql = "DELETE FROM rendez_vous WHERE id_rdv = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du rendez-vous: " + e.getMessage());
            return false;
        }
    }
}
