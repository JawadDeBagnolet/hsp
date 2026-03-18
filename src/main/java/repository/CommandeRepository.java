package repository;

import database.Database;
import modele.Commande;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeRepository {

    public int ajouterCommandeReturnId(Commande commande) {
        String sql = "INSERT INTO commande (id_user, numCommande, libelle, id_fournisseur, date_commande, statut) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, commande.getIdUser());
            stmt.setInt(2, commande.getNumCommande());
            stmt.setString(3, commande.getLibelle());
            stmt.setInt(4, commande.getIdFournisseur());
            stmt.setObject(5, LocalDateTime.now());
            stmt.setString(6, commande.getStatut() != null ? commande.getStatut() : "En attente");

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
        return -1;
    }

    public boolean ajouterCommande(Commande commande) {
        return ajouterCommandeReturnId(commande) > 0;
    }

    public int getProchainNumCommande() {
        String sql = "SELECT COALESCE(MAX(numCommande), 0) + 1 FROM commande";
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Erreur getProchainNumCommande: " + e.getMessage());
        }
        return 1;
    }

    public Commande trouverCommandeParId(int id) {
        String sql = "SELECT * FROM commande WHERE id_commande = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la commande: " + e.getMessage());
        }
        return null;
    }

    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande ORDER BY date_commande DESC";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) commandes.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes: " + e.getMessage());
        }
        return commandes;
    }

    public boolean updateStatut(int idCommande, String statut) {
        Connection cnx = null;
        try {
            cnx = Database.getConnexion();
            if (cnx == null) return false;
            cnx.setAutoCommit(false);

            // Récupérer l'ancien statut
            String ancienStatut = null;
            try (PreparedStatement s = cnx.prepareStatement("SELECT statut FROM commande WHERE id_commande = ?")) {
                s.setInt(1, idCommande);
                ResultSet rs = s.executeQuery();
                if (rs.next()) ancienStatut = rs.getString(1);
            }

            // Mettre à jour le statut
            try (PreparedStatement s = cnx.prepareStatement("UPDATE commande SET statut = ? WHERE id_commande = ?")) {
                s.setString(1, statut);
                s.setInt(2, idCommande);
                if (s.executeUpdate() <= 0) { cnx.rollback(); return false; }
            }

            // Si passage à "Livrée", incrémenter le stock de chaque produit
            if ("Livrée".equals(statut) && !"Livrée".equals(ancienStatut)) {
                String sqlStock = "UPDATE fiche_produit fp " +
                        "INNER JOIN commande_produit cp ON fp.id_produit = cp.id_produit " +
                        "SET fp.stock = fp.stock + cp.qte " +
                        "WHERE cp.id_commande = ?";
                try (PreparedStatement s = cnx.prepareStatement(sqlStock)) {
                    s.setInt(1, idCommande);
                    s.executeUpdate();
                }
                System.out.println("Stock mis à jour pour la commande #" + idCommande);
            }

            cnx.commit();
            return true;
        } catch (SQLException e) {
            try { if (cnx != null) cnx.rollback(); } catch (SQLException ignored) {}
            System.err.println("Erreur updateStatut commande: " + e.getMessage());
            return false;
        } finally {
            try { if (cnx != null) { cnx.setAutoCommit(true); cnx.close(); } } catch (SQLException ignored) {}
        }
    }

    public boolean supprimerCommande(int id) {
        String sql = "DELETE FROM commande WHERE id_commande = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la commande: " + e.getMessage());
            return false;
        }
    }

    private Commande mapRow(ResultSet rs) throws SQLException {
        LocalDateTime date = null;
        try { date = rs.getObject("date_commande", LocalDateTime.class); } catch (SQLException ignored) {}
        String statut = "En attente";
        try { statut = rs.getString("statut"); } catch (SQLException ignored) {}
        int idFournisseur = 0;
        try { idFournisseur = rs.getInt("id_fournisseur"); } catch (SQLException ignored) {}

        int idDemande = 0;
        try { idDemande = rs.getInt("id_demande"); } catch (SQLException ignored) {}

        return new Commande(
            rs.getInt("id_commande"),
            rs.getInt("id_user"),
            rs.getInt("numCommande"),
            rs.getString("libelle"),
            idFournisseur,
            date,
            statut,
            idDemande
        );
    }
}
