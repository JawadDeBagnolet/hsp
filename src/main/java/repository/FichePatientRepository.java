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
        String sql = "INSERT INTO fiche_eleve (nom, prenom, num_etudiant, email, tel, rue, cp, ville, candidature) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, fichePatient.getNom());
            stmt.setString(2, fichePatient.getPrenom());
            stmt.setString(3, fichePatient.getNum_etudiant());
            stmt.setString(4, fichePatient.getEmail());
            stmt.setString(5, fichePatient.getTel());
            stmt.setString(6, fichePatient.getRue());
            stmt.setInt(7, fichePatient.getCp());
            stmt.setString(8, fichePatient.getVille());
            if (fichePatient.getCandidature() == null) {
                stmt.setNull(9, java.sql.Types.TINYINT);
            } else {
                stmt.setInt(9, fichePatient.getCandidature());
            }

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la fiche élève : " + e.getMessage());
            return false;
        }
    }

    public FichePatient trouverFichePatientParId(int id) {
        String sql = "SELECT * FROM fiche_eleve WHERE id_eleve = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la fiche élève: " + e.getMessage());
        }
        return null;
    }

    public FichePatient trouverFichePatientParNumEtudiant(String numEtudiant) {
        String sql = "SELECT * FROM fiche_eleve WHERE num_etudiant = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, numEtudiant);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par numéro étudiant: " + e.getMessage());
        }
        return null;
    }

    public List<FichePatient> getAllFichePatients() {
        List<FichePatient> fichePatients = new ArrayList<>();
        String sql = "SELECT * FROM fiche_eleve";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fichePatients.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des fiches élèves: " + e.getMessage());
        }
        System.out.println("Total élèves récupérés: " + fichePatients.size());
        return fichePatients;
    }

    public boolean modifierFichePatient(FichePatient fichePatient) {
        String sql = "UPDATE fiche_eleve SET nom = ?, prenom = ?, num_etudiant = ?, email = ?, tel = ?, rue = ?, cp = ?, ville = ?, candidature = ? WHERE id_eleve = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setString(1, fichePatient.getNom());
            stmt.setString(2, fichePatient.getPrenom());
            stmt.setString(3, fichePatient.getNum_etudiant());
            stmt.setString(4, fichePatient.getEmail());
            stmt.setString(5, fichePatient.getTel());
            stmt.setString(6, fichePatient.getRue());
            stmt.setInt(7, fichePatient.getCp());
            stmt.setString(8, fichePatient.getVille());
            if (fichePatient.getCandidature() == null) {
                stmt.setNull(9, java.sql.Types.TINYINT);
            } else {
                stmt.setInt(9, fichePatient.getCandidature());
            }
            stmt.setInt(10, fichePatient.getIdFichePatient());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la fiche élève: " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerFichePatient(int id) {
        String sql = "DELETE FROM fiche_eleve WHERE id_eleve = ?";

        try (Connection cnx = Database.getConnexion();
             PreparedStatement stmt = cnx.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la fiche élève: " + e.getMessage());
            return false;
        }
    }

    private FichePatient mapResultSet(ResultSet rs) throws SQLException {
        String candidatureStr = rs.getString("candidature");
        Integer candidature = null;
        if (candidatureStr != null) {
            try { candidature = Integer.parseInt(candidatureStr); } catch (NumberFormatException ignored) {}
        }

        String tel = "";
        int cp = 0;
        try { tel = rs.getString("tel"); if (tel == null) tel = ""; } catch (Exception ignored) {}
        try { cp = rs.getInt("cp"); } catch (Exception ignored) {}

        FichePatient eleve = new FichePatient(
            rs.getString("nom") != null ? rs.getString("nom") : "",
            rs.getString("prenom") != null ? rs.getString("prenom") : "",
            rs.getString("num_etudiant") != null ? rs.getString("num_etudiant") : "",
            rs.getString("email") != null ? rs.getString("email") : "",
            tel,
            rs.getString("rue") != null ? rs.getString("rue") : "",
            cp,
            rs.getString("ville") != null ? rs.getString("ville") : "",
            candidature
        );
        eleve.setIdFichePatient(rs.getInt("id_eleve"));
        return eleve;
    }
}
