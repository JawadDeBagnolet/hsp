package repository;

import database.Database;
import modele.FicheProduit;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeProduitRepository {

    private String lastError = "";

    public String getLastError() { return lastError; }

    public static class LigneCommandeProduit {
        private final int idProduit;
        private final int quantite;

        public LigneCommandeProduit(int idProduit, int quantite) {
            this.idProduit = idProduit;
            this.quantite = quantite;
        }

        public int getIdProduit() { return idProduit; }
        public int getQuantite() { return quantite; }
    }

    public static class ProduitCommande {
        private final FicheProduit produit;
        private final int quantite;

        public ProduitCommande(FicheProduit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
        }

        public FicheProduit getProduit() { return produit; }
        public int getQuantite() { return quantite; }
    }

    /**
     * Crée une commande fournisseur avec ses produits dans une transaction.
     * Marque aussi la demande d'origine comme "Traitée" si idDemande > 0.
     *
     * @return id_commande créé, ou -1 si erreur
     */
    public int creerCommandeAvecProduits(int idUser, int idFournisseur, String libelle,
                                          List<LigneCommandeProduit> lignes, int idDemande) {
        if (lignes == null || lignes.isEmpty()) return -1;

        String insertCommande = "INSERT INTO commande (id_user, numCommande, libelle, id_fournisseur, date_commande, statut, id_demande) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertLigne = "INSERT INTO commande_produit (id_commande, id_produit, qte) VALUES (?, ?, ?)";
        String updateDemande = "UPDATE demande SET statut = 'Traitée' WHERE id_demande = ?";
        String maxNum = "SELECT COALESCE(MAX(numCommande), 0) + 1 FROM commande";

        Connection cnx = null;
        try {
            cnx = Database.getConnexion();
            if (cnx == null) return -1;
            cnx.setAutoCommit(false);

            int numCommande;
            try (PreparedStatement stmt = cnx.prepareStatement(maxNum);
                 ResultSet rs = stmt.executeQuery()) {
                numCommande = rs.next() ? rs.getInt(1) : 1;
            }

            int idCommande;
            try (PreparedStatement stmt = cnx.prepareStatement(insertCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, idUser);
                stmt.setInt(2, numCommande);
                stmt.setString(3, libelle);
                stmt.setInt(4, idFournisseur);
                stmt.setObject(5, LocalDateTime.now());
                stmt.setString(6, "En attente");
                stmt.setInt(7, idDemande > 0 ? idDemande : 0);

                if (stmt.executeUpdate() <= 0) { cnx.rollback(); return -1; }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) { cnx.rollback(); return -1; }
                    idCommande = keys.getInt(1);
                }
            }

            try (PreparedStatement stmt = cnx.prepareStatement(insertLigne)) {
                for (LigneCommandeProduit l : lignes) {
                    stmt.setInt(1, idCommande);
                    stmt.setInt(2, l.getIdProduit());
                    stmt.setInt(3, l.getQuantite());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            if (idDemande > 0) {
                try (PreparedStatement stmt = cnx.prepareStatement(updateDemande)) {
                    stmt.setInt(1, idDemande);
                    stmt.executeUpdate();
                }
            }

            cnx.commit();
            return idCommande;

        } catch (SQLException e) {
            try { if (cnx != null) cnx.rollback(); } catch (SQLException ignored) {}
            lastError = e.getMessage();
            System.err.println("Erreur creerCommandeAvecProduits: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (cnx != null) { cnx.setAutoCommit(true); cnx.close(); }
            } catch (SQLException ignored) {}
        }
    }

    public List<ProduitCommande> getProduitsByCommande(int idCommande) {
        List<ProduitCommande> liste = new ArrayList<>();
        String sql = "SELECT cp.id_produit, cp.qte, fp.libelle " +
                     "FROM commande_produit cp " +
                     "LEFT JOIN fiche_produit fp ON cp.id_produit = fp.id_produit " +
                     "WHERE cp.id_commande = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, idCommande);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String libelle = rs.getString("libelle");
                if (libelle == null) libelle = "Produit #" + rs.getInt("id_produit");
                FicheProduit fp = new FicheProduit(rs.getInt("id_produit"), libelle, "", 0, 0);
                liste.add(new ProduitCommande(fp, rs.getInt("qte")));
            }
        } catch (SQLException e) {
            System.err.println("Erreur getProduitsByCommande: " + e.getMessage());
        }
        return liste;
    }
}
