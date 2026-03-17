package repository;

import database.Database;
import modele.Ticket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository {

    public int creerTicket(int idEleve, int idSecretaire, String motif) {
        String sql = "INSERT INTO ticket (id_eleve, id_secretaire, date_creation, motif, statut) VALUES (?, ?, ?, ?, ?)";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idEleve);
            stmt.setInt(2, idSecretaire);
            stmt.setObject(3, LocalDateTime.now());
            stmt.setString(4, motif);
            stmt.setString(5, Ticket.STATUT_ATTENTE);
            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur creerTicket: " + e.getMessage());
        }
        return -1;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT t.*, CONCAT(fe.prenom, ' ', fe.nom) AS nom_eleve " +
                     "FROM ticket t LEFT JOIN fiche_eleve fe ON t.id_eleve = fe.id_eleve " +
                     "ORDER BY t.date_creation DESC";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Ticket t = mapRow(rs);
                t.setNomEleve(rs.getString("nom_eleve"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAllTickets: " + e.getMessage());
        }
        return list;
    }

    public List<Ticket> getTicketsParStatut(String statut) {
        List<Ticket> list = new ArrayList<>();
        String sql = "SELECT t.*, CONCAT(fe.prenom, ' ', fe.nom) AS nom_eleve " +
                     "FROM ticket t LEFT JOIN fiche_eleve fe ON t.id_eleve = fe.id_eleve " +
                     "WHERE t.statut = ? ORDER BY t.date_creation DESC";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ticket t = mapRow(rs);
                t.setNomEleve(rs.getString("nom_eleve"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getTicketsParStatut: " + e.getMessage());
        }
        return list;
    }

    public boolean updateStatutEtPrescription(int idTicket, String statut, String prescription) {
        String sql = "UPDATE ticket SET statut = ?, prescription = ? WHERE id_ticket = ?";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, statut);
            stmt.setString(2, prescription);
            stmt.setInt(3, idTicket);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur updateStatutEtPrescription ticket: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatut(int idTicket, String statut) {
        return updateStatutEtPrescription(idTicket, statut, null);
    }

    public boolean supprimerTicket(int idTicket) {
        String sql = "DELETE FROM ticket WHERE id_ticket = ?";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, idTicket);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur supprimerTicket: " + e.getMessage());
            return false;
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        Ticket t = new Ticket(
            rs.getInt("id_ticket"),
            rs.getInt("id_eleve"),
            rs.getInt("id_secretaire"),
            rs.getObject("date_creation", LocalDateTime.class),
            rs.getString("motif"),
            rs.getString("statut")
        );
        try { t.setPrescription(rs.getString("prescription")); } catch (SQLException ignored) {}
        return t;
    }
}
