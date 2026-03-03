package repository;

import database.Database;
import modele.FicheProduit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public static class ProduitCommande {
        private final FicheProduit produit;
        private final int quantite;

        public ProduitCommande(FicheProduit produit, int quantite) {
            this.produit = produit;
            this.quantite = quantite;
        }

        public FicheProduit getProduit() {
            return produit;
        }

        public int getQuantite() {
            return quantite;
        }
    }

    public List<ProduitCommande> getProduitsByDemande(int idDemande) {
        List<ProduitCommande> produits = new ArrayList<>();
        
        System.out.println("=== RECHERCHE PRODUITS POUR DEMANDE " + idDemande + " ===");
        
        // D'abord vérifier s'il y a quelque chose dans demande_produit pour cette demande
        try (Connection cnx = Database.getConnexion();
             Statement stmt = cnx.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM demande_produit WHERE id_demande = " + idDemande);
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("NOMBRE DE LIGNES DANS demande_produit POUR CETTE DEMANDE: " + total);
                if (total == 0) {
                    System.out.println("-> AUCUN PRODUIT ASSOCIÉ À CETTE DEMANDE !");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERREUR VÉRIFICATION COUNT: " + e.getMessage());
        }
        
        // Récupérer les données avec jointure pour avoir le vrai nom du produit
        String sql = "SELECT dp.id_produit, dp.qte, fp.libelle " +
                    "FROM demande_produit dp " +
                    "LEFT JOIN fiche_produit fp ON dp.id_produit = fp.id_produit " +
                    "WHERE dp.id_demande = ?";
        
        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            
            stmt.setInt(1, idDemande);
            ResultSet rs = stmt.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                int idProduit = rs.getInt("id_produit");
                int quantite = rs.getInt("qte");
                String libelle = rs.getString("libelle");
                
                // Si le libelle est null (pas de jointure trouvée), utiliser une valeur par défaut
                if (rs.wasNull() || libelle == null) {
                    libelle = "Produit #" + idProduit;
                }
                
                System.out.println("PRODUIT TROUVÉ #" + count + ": id_produit=" + idProduit + 
                                 ", qte=" + quantite + ", libelle='" + libelle + "'");
                
                // Créer un FicheProduit avec le vrai nom
                FicheProduit produit = new FicheProduit(
                    idProduit,
                    libelle,
                    "Description non disponible",  // description temporaire
                    0,  // nivDangerosite par défaut
                    0   // stockActuel par défaut
                );
                
                produits.add(new ProduitCommande(produit, quantite));
            }
            System.out.println("TOTAL PRODUITS TROUVÉS: " + count);
        } catch (SQLException e) {
            System.err.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== FIN RECHERCHE ===\n");
        return produits;
    }

    // Méthode de test pour diagnostiquer
    public void testDatabaseContent() {
        System.out.println("=== TEST CONTENU BASE DE DONNÉES ===");
        
        // Vérifier contenu de demande_produit
        try (Connection cnx = Database.getConnexion();
             Statement stmt = cnx.createStatement()) {
            
            System.out.println("\n--- Table demande_produit ---");
            ResultSet rs = stmt.executeQuery("SELECT * FROM demande_produit LIMIT 10");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Ligne " + count + ": id_demande=" + rs.getInt("id_demande") + 
                                 ", id_produit=" + rs.getInt("id_produit") + 
                                 ", qte=" + rs.getInt("qte"));
            }
            System.out.println("Total lignes dans demande_produit: " + count);
            
        } catch (SQLException e) {
            System.err.println("Erreur lecture demande_produit: " + e.getMessage());
        }
        
        // Vérifier contenu de fiche_produit
        try (Connection cnx = Database.getConnexion();
             Statement stmt = cnx.createStatement()) {
            
            System.out.println("\n--- Table fiche_produit ---");
            ResultSet rs = stmt.executeQuery("SELECT id_produit, libelle FROM fiche_produit LIMIT 5");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("Ligne " + count + ": id_produit=" + rs.getInt("id_produit") + 
                                 ", libelle=" + rs.getString("libelle"));
            }
            System.out.println("Total lignes dans fiche_produit (échantillon): " + count);
            
        } catch (SQLException e) {
            System.err.println("Erreur lecture fiche_produit: " + e.getMessage());
        }
        
        // Vérifier les demandes qui ont des produits
        try (Connection cnx = Database.getConnexion();
             Statement stmt = cnx.createStatement()) {
            
            System.out.println("\n--- Jointure test ---");
            ResultSet rs = stmt.executeQuery(
                "SELECT d.id_demande, COUNT(dp.id_produit) as nb_produits " +
                "FROM demande d " +
                "LEFT JOIN demande_produit dp ON d.id_demande = dp.id_demande " +
                "GROUP BY d.id_demande " +
                "ORDER BY d.id_demande " +
                "LIMIT 10"
            );
            while (rs.next()) {
                System.out.println("Demande " + rs.getInt("id_demande") + 
                                 " a " + rs.getInt("nb_produits") + " produit(s)");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur jointure test: " + e.getMessage());
        }
        
        System.out.println("=== FIN TEST ===\n");
    }
}
