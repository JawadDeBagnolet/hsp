package repository;

import database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

public class DemandeProduitRepository {

    public static class LigneDemandeProduit {
        private final int idProduit;
        private final int quantite;

        public LigneDemandeProduit(int idProduit, int quantite) {
            this.idProduit = idProduit;
            this.quantite = quantite;
        }

        public int getIdProduit() {
            return idProduit;
        }

        public int getQuantite() {
            return quantite;
        }
    }

    public int creerDemandeAvecProduit(int idUser, int idProduit, int quantite) {
        String insertDemandeSql = "INSERT INTO demande (id_user, date_demande, quantite) VALUES (?, ?, ?)";
        String insertLigneSql = "INSERT INTO demande_produit (id_demande, id_produit, qte) VALUES (?, ?, ?)";

        Connection cnx = null;
        try {
            cnx = Database.getConnexion();
            if (cnx == null) {
                return -1;
            }

            cnx.setAutoCommit(false);

            int idDemande;
            try (PreparedStatement stmt = cnx.prepareStatement(insertDemandeSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, idUser);
                stmt.setObject(2, LocalDateTime.now());
                stmt.setInt(3, quantite);

                int rows = stmt.executeUpdate();
                if (rows <= 0) {
                    cnx.rollback();
                    return -1;
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        cnx.rollback();
                        return -1;
                    }
                    idDemande = keys.getInt(1);
                }
            }

            try (PreparedStatement stmt2 = cnx.prepareStatement(insertLigneSql)) {
                stmt2.setInt(1, idDemande);
                stmt2.setInt(2, idProduit);
                stmt2.setInt(3, quantite);

                int rows2 = stmt2.executeUpdate();
                if (rows2 <= 0) {
                    cnx.rollback();
                    return -1;
                }
            }

            cnx.commit();
            return idDemande;

        } catch (SQLException e) {
            try {
                if (cnx != null) {
                    cnx.rollback();
                }
            } catch (SQLException ignored) {
            }
            System.err.println("Erreur lors de la création de la demande: " + e.getMessage());
            return -1;
        } finally {
            try {
                if (cnx != null) {
                    cnx.setAutoCommit(true);
                    cnx.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public int creerDemandeAvecProduits(int idUser, List<LigneDemandeProduit> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            return -1;
        }

        int totalQuantite = 0;
        for (LigneDemandeProduit l : lignes) {
            if (l == null || l.getIdProduit() <= 0 || l.getQuantite() <= 0) {
                return -1;
            }
            totalQuantite += l.getQuantite();
        }

        String insertDemandeSql = "INSERT INTO demande (id_user, date_demande, quantite) VALUES (?, ?, ?)";
        String insertLigneSql = "INSERT INTO demande_produit (id_demande, id_produit, qte) VALUES (?, ?, ?)";

        Connection cnx = null;
        try {
            cnx = Database.getConnexion();
            if (cnx == null) {
                return -1;
            }

            cnx.setAutoCommit(false);

            int idDemande;
            try (PreparedStatement stmt = cnx.prepareStatement(insertDemandeSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, idUser);
                stmt.setObject(2, LocalDateTime.now());
                stmt.setInt(3, totalQuantite);

                int rows = stmt.executeUpdate();
                if (rows <= 0) {
                    cnx.rollback();
                    return -1;
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        cnx.rollback();
                        return -1;
                    }
                    idDemande = keys.getInt(1);
                }
            }

            try (PreparedStatement stmt2 = cnx.prepareStatement(insertLigneSql)) {
                for (LigneDemandeProduit l : lignes) {
                    stmt2.setInt(1, idDemande);
                    stmt2.setInt(2, l.getIdProduit());
                    stmt2.setInt(3, l.getQuantite());
                    stmt2.addBatch();
                }

                int[] rows2 = stmt2.executeBatch();
                for (int r : rows2) {
                    if (r <= 0) {
                        cnx.rollback();
                        return -1;
                    }
                }
            }

            cnx.commit();
            return idDemande;

        } catch (SQLException e) {
            try {
                if (cnx != null) {
                    cnx.rollback();
                }
            } catch (SQLException ignored) {
            }
            System.err.println("Erreur lors de la création de la demande: " + e.getMessage());
            return -1;
        } finally {
            try {
                if (cnx != null) {
                    cnx.setAutoCommit(true);
                    cnx.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }
}
