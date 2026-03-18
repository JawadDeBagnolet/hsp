package repository;

import database.Database;
import modele.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitRepository {

    /**
     * Ajoute un produit dans fiche_produit puis crée le lien dans fournisseur_produit.
     * produit.idFournisseur et produit.prix doivent être renseignés.
     */
    public boolean ajouterProduit(Produit produit) {
        String sqlFiche = "INSERT INTO fiche_produit (libelle, description, niveau_dangerosite, stock) VALUES (?, ?, 0, ?)";
        String sqlLien  = "INSERT INTO fournisseur_produit (id_fournisseur, id_produit, prix) VALUES (?, ?, ?)";

        try (Connection cnx = Database.getConnexion()) {
            cnx.setAutoCommit(false);

            // 1. Insérer dans fiche_produit
            int idProduit;
            try (PreparedStatement stmt = cnx.prepareStatement(sqlFiche, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, produit.getNom());
                stmt.setString(2, produit.getDescription() != null ? produit.getDescription() : "");
                stmt.setInt(3, produit.getQuantite());
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (!keys.next()) { cnx.rollback(); return false; }
                idProduit = keys.getInt(1);
            }

            // 2. Créer le lien fournisseur_produit
            try (PreparedStatement stmt = cnx.prepareStatement(sqlLien)) {
                stmt.setInt(1, produit.getIdFournisseur());
                stmt.setInt(2, idProduit);
                stmt.setDouble(3, produit.getPrix());
                stmt.executeUpdate();
            }

            cnx.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère tous les produits liés à un fournisseur via fournisseur_produit.
     */
    public List<Produit> getProduitsParFournisseur(int idFournisseur) {
        List<Produit> produits = new ArrayList<>();
        String sql = """
                SELECT fp.id_produit, fp.libelle, fp.description, fop.prix, fp.stock, fop.id_fournisseur
                FROM fiche_produit fp
                JOIN fournisseur_produit fop ON fp.id_produit = fop.id_produit
                WHERE fop.id_fournisseur = ?
                """;

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, idFournisseur);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(new Produit(
                    rs.getInt("id_produit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getInt("stock"),
                    rs.getInt("id_fournisseur")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits du fournisseur: " + e.getMessage());
        }
        return produits;
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = """
                SELECT fp.id_produit, fp.libelle, fp.description, COALESCE(fop.prix, 0) AS prix, fp.stock,
                       COALESCE(fop.id_fournisseur, 0) AS id_fournisseur
                FROM fiche_produit fp
                LEFT JOIN fournisseur_produit fop ON fp.id_produit = fop.id_produit
                """;

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                produits.add(new Produit(
                    rs.getInt("id_produit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getInt("stock"),
                    rs.getInt("id_fournisseur")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits: " + e.getMessage());
        }
        return produits;
    }

    public Produit trouverProduitParId(int id) {
        String sql = """
                SELECT fp.id_produit, fp.libelle, fp.description, COALESCE(fop.prix, 0) AS prix, fp.stock,
                       COALESCE(fop.id_fournisseur, 0) AS id_fournisseur
                FROM fiche_produit fp
                LEFT JOIN fournisseur_produit fop ON fp.id_produit = fop.id_produit
                WHERE fp.id_produit = ?
                """;

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produit(
                    rs.getInt("id_produit"),
                    rs.getString("libelle"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getInt("stock"),
                    rs.getInt("id_fournisseur")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du produit: " + e.getMessage());
        }
        return null;
    }

    /**
     * Met à jour fiche_produit (libelle, description, stock) et fournisseur_produit (prix).
     */
    public boolean modifierProduit(Produit produit) {
        String sqlFiche = "UPDATE fiche_produit SET libelle = ?, description = ?, stock = ? WHERE id_produit = ?";
        String sqlLien  = "UPDATE fournisseur_produit SET prix = ? WHERE id_produit = ? AND id_fournisseur = ?";

        try (Connection cnx = Database.getConnexion()) {
            cnx.setAutoCommit(false);

            try (PreparedStatement stmt = cnx.prepareStatement(sqlFiche)) {
                stmt.setString(1, produit.getNom());
                stmt.setString(2, produit.getDescription() != null ? produit.getDescription() : "");
                stmt.setInt(3, produit.getQuantite());
                stmt.setInt(4, produit.getIdProduit());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = cnx.prepareStatement(sqlLien)) {
                stmt.setDouble(1, produit.getPrix());
                stmt.setInt(2, produit.getIdProduit());
                stmt.setInt(3, produit.getIdFournisseur());
                stmt.executeUpdate();
            }

            cnx.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du produit: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime le lien fournisseur_produit (le produit reste dans fiche_produit).
     */
    public boolean supprimerProduit(int idProduit) {
        String sql = "DELETE FROM fournisseur_produit WHERE id_produit = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, idProduit);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
            return false;
        }
    }
}
