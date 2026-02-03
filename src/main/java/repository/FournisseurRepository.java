package repository;

import database.Database;
import modele.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FournisseurRepository {

    public boolean ajouterFournisseur(Fournisseur fournisseur) {
        String sql = "INSERT INTO fournisseur (nom, email, tel) VALUES (?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getEmail());
            stmt.setInt(3, fournisseur.getTel());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du fournisseur : " + e.getMessage());
            return false;
        }
    }

    public Fournisseur trouverFournisseurParId(int id) {
        String sql = "SELECT * FROM fournisseur WHERE idFournisseur = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Fournisseur(
                    rs.getInt("idFournisseur"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getInt("tel")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du fournisseur: " + e.getMessage());
        }
        return null;
    }

    public Fournisseur trouverFournisseurParEmail(String email) {
        String sql = "SELECT * FROM fournisseur WHERE email = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Fournisseur(
                    rs.getInt("idFournisseur"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getInt("tel")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par email: " + e.getMessage());
        }
        return null;
    }

    public List<Fournisseur> getAllFournisseurs() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fournisseurs.add(new Fournisseur(
                    rs.getInt("idFournisseur"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getInt("tel")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fournisseurs: " + e.getMessage());
        }
        return fournisseurs;
    }

    public boolean modifierFournisseur(Fournisseur fournisseur) {
        String sql = "UPDATE fournisseur SET nom = ?, email = ?, tel = ? WHERE idFournisseur = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, fournisseur.getNom());
            stmt.setString(2, fournisseur.getEmail());
            stmt.setInt(3, fournisseur.getTel());
            stmt.setInt(4, fournisseur.getIdFournisseur());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du fournisseur: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerFournisseur(int id) {
        String sql = "DELETE FROM fournisseur WHERE idFournisseur = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du fournisseur: " + e.getMessage());
            return false;
        }
    }
}
