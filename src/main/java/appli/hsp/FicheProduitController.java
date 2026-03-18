package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.FicheProduit;
import repository.FicheProduitRepository;

import java.util.List;

public class FicheProduitController {

    @FXML private TextField libelleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField dangerositéField;
    @FXML private Label messageLabel;
    @FXML private Label alerteStockLabel;
    @FXML private ListView<FicheProduit> produitsListView;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavUtilisateurs;

    private FicheProduitRepository ficheProduitRepository;
    private FicheProduit produitSelectionne;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, null, null, btnNavCommandes, btnNavPlanning, null, null, btnNavUtilisateurs, null);
        ficheProduitRepository = new FicheProduitRepository();

        chargerListeProduits();

        produitsListView.setCellFactory(param -> new ListCell<FicheProduit>() {
            @Override
            protected void updateItem(FicheProduit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String[] niveaux = {"Faible", "Moyen", "Élevé", "Critique"};
                    int niv = produit.getNivDangerosite();
                    String danger = (niv >= 0 && niv < niveaux.length) ? niveaux[niv] : String.valueOf(niv);
                    int stock = produit.getStockActuel();
                    String stockTag = stock <= 0 ? " ⛔ RUPTURE" : (stock <= 5 ? " ⚠ STOCK FAIBLE" : "");
                    setText(String.format("📦 %s  |  Danger: %s  |  Stock: %d%s",
                            produit.getLibelle(), danger, stock, stockTag));
                    if (stock <= 0) {
                        setStyle("-fx-background-color: #fee2e2; -fx-border-color: #fca5a5; -fx-border-width: 0 0 1 0; -fx-padding: 8; -fx-font-weight: bold;");
                    } else if (stock <= 5) {
                        setStyle("-fx-background-color: #fef3c7; -fx-border-color: #fcd34d; -fx-border-width: 0 0 1 0; -fx-padding: 8;");
                    } else {
                        setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-padding: 8;");
                    }
                }
            }
        });

        produitsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                produitSelectionne = newVal;
                libelleField.setText(newVal.getLibelle());
                descriptionArea.setText(newVal.getDescription() != null ? newVal.getDescription() : "");
                dangerositéField.setText(String.valueOf(newVal.getNivDangerosite()));
                afficherMessage("Produit sélectionné : " + newVal.getLibelle(), "info");
            }
        });

        messageLabel.setText("");
    }

    @FXML
    private void handleAjouter() {
        if (!validerChamps()) return;

        String libelle = libelleField.getText().trim();
        String description = descriptionArea.getText().trim();
        int dangerosite;
        try {
            dangerosite = Integer.parseInt(dangerositéField.getText().trim());
            if (dangerosite < 0 || dangerosite > 3) {
                afficherMessage("Le niveau de dangerosité doit être entre 0 et 3", "error");
                return;
            }
        } catch (NumberFormatException e) {
            afficherMessage("Le niveau de dangerosité doit être un nombre (0-3)", "error");
            return;
        }

        if (ficheProduitRepository.trouverFicheProduitParLibelle(libelle) != null) {
            afficherMessage("Un produit avec ce libellé existe déjà", "error");
            return;
        }

        FicheProduit nouveau = new FicheProduit(libelle, description, dangerosite, 0);
        if (ficheProduitRepository.ajouterFicheProduit(nouveau)) {
            afficherMessage("Produit \"" + libelle + "\" ajouté avec succès !", "success");
            viderChamps();
            chargerListeProduits();
        } else {
            afficherMessage("Erreur lors de l'ajout du produit", "error");
        }
    }

    @FXML
    private void handleModifier() {
        if (produitSelectionne == null) {
            afficherMessage("Veuillez sélectionner un produit à modifier", "error");
            return;
        }
        if (!validerChamps()) return;

        String libelle = libelleField.getText().trim();
        String description = descriptionArea.getText().trim();
        int dangerosite;
        try {
            dangerosite = Integer.parseInt(dangerositéField.getText().trim());
            if (dangerosite < 0 || dangerosite > 3) {
                afficherMessage("Le niveau de dangerosité doit être entre 0 et 3", "error");
                return;
            }
        } catch (NumberFormatException e) {
            afficherMessage("Le niveau de dangerosité doit être un nombre (0-3)", "error");
            return;
        }

        produitSelectionne.setLibelle(libelle);
        produitSelectionne.setDescription(description);
        produitSelectionne.setNivDangerosite(dangerosite);

        if (ficheProduitRepository.modifierFicheProduit(produitSelectionne)) {
            afficherMessage("Produit modifié avec succès !", "success");
            viderChamps();
            chargerListeProduits();
            produitSelectionne = null;
        } else {
            afficherMessage("Erreur lors de la modification du produit", "error");
        }
    }

    @FXML
    private void handleSupprimer() {
        if (produitSelectionne == null) {
            afficherMessage("Veuillez sélectionner un produit à supprimer", "error");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer le produit");
        confirmation.setHeaderText("Confirmer la suppression");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer \"" + produitSelectionne.getLibelle() + "\" ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (ficheProduitRepository.supprimerFicheProduit(produitSelectionne.getIdProduit())) {
                afficherMessage("Produit supprimé avec succès !", "success");
                viderChamps();
                chargerListeProduits();
                produitSelectionne = null;
            } else {
                afficherMessage("Erreur lors de la suppression du produit", "error");
            }
        }
    }

    @FXML
    private void handleVider() {
        viderChamps();
        produitSelectionne = null;
        produitsListView.getSelectionModel().clearSelection();
        afficherMessage("", "info");
    }

    private boolean validerChamps() {
        if (libelleField.getText() == null || libelleField.getText().trim().isEmpty()) {
            afficherMessage("Le libellé est obligatoire", "error");
            return false;
        }
        return true;
    }

    private void chargerListeProduits() {
        List<FicheProduit> produits = ficheProduitRepository.getAllFicheProduits();
        produitsListView.getItems().clear();
        produitsListView.getItems().addAll(produits);

        // Bannière alerte stock faible
        if (alerteStockLabel != null) {
            long enRupture = produits.stream().filter(p -> p.getStockActuel() <= 0).count();
            long faibles   = produits.stream().filter(p -> p.getStockActuel() > 0 && p.getStockActuel() <= 5).count();
            if (enRupture > 0 || faibles > 0) {
                StringBuilder msg = new StringBuilder("⚠  ");
                if (enRupture > 0) msg.append(enRupture).append(" produit(s) en rupture de stock");
                if (enRupture > 0 && faibles > 0) msg.append("  |  ");
                if (faibles > 0) msg.append(faibles).append(" produit(s) avec stock faible (≤ 5)");
                msg.append("  — Pensez à réapprovisionner !");
                alerteStockLabel.setText(msg.toString());
                alerteStockLabel.setVisible(true);
                alerteStockLabel.setManaged(true);
            } else {
                alerteStockLabel.setVisible(false);
                alerteStockLabel.setManaged(false);
            }
        }
    }

    private void viderChamps() {
        libelleField.clear();
        descriptionArea.clear();
        dangerositéField.clear();
    }

    private void afficherMessage(String message, String type) {
        messageLabel.setText(message);
        if ("error".equals(type)) {
            messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        } else if ("success".equals(type)) {
            messageLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: #3498db;");
        }
    }

    // Navigation
    @FXML private void versAccueil() {
        try { StartApplication.changeScene("pageAccueil"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versPatients() {
        try { StartApplication.changeScene("patientsView"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versDossiers() {
        try { StartApplication.changeScene("dossierEnChargeView"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versCommandes() {
        try {
            NavigationHelper.versCommandes();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers commandes: " + e.getMessage());
        }
    }
    @FXML private void versFicheProduit() {
        System.out.println("Déjà sur la page catalogue produits");
    }
    @FXML private void versPlanning() {
        try { StartApplication.changeScene("planningView"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versUtilisateurs() {
        try { StartApplication.changeScene("pageUtilisateurs"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versMonEspace() {
        try { StartApplication.changeScene("pageMonEspace"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void deconnexion() {
        try { StartApplication.changeScene("helloView"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}
