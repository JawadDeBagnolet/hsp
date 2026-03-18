package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modele.Fournisseur;
import modele.Produit;
import repository.FournisseurRepository;
import repository.ProduitRepository;

public class FournisseursController {

    // ── Formulaire fournisseur ─────────────────────────────────────
    @FXML private TextField nomFournisseurField;
    @FXML private TextField emailFournisseurField;
    @FXML private TextField telephoneFournisseurField;
    @FXML private Button    btnAjouter;
    @FXML private Label     messageLabel;

    // ── Table fournisseurs ─────────────────────────────────────────
    @FXML private TableView<Fournisseur>         fournisseursTable;
    @FXML private TableColumn<Fournisseur, String>  colNom;
    @FXML private TableColumn<Fournisseur, String>  colEmail;
    @FXML private TableColumn<Fournisseur, Integer> colTel;
    @FXML private TableColumn<Fournisseur, Void>    colActions;

    // ── Panneau produits ───────────────────────────────────────────
    @FXML private Label  fournisseurSelectionneLabel;
    @FXML private VBox   formProduitBox;
    @FXML private Button btnNouveauProduit;
    @FXML private Label  messageProduitLabel;

    @FXML private TextField nomProduitField;
    @FXML private TextField descProduitField;
    @FXML private TextField prixProduitField;
    @FXML private TextField quantiteProduitField;
    @FXML private Button    btnAjouterProduit;

    // ── Table produits ─────────────────────────────────────────────
    @FXML private TableView<Produit>            produitsTable;
    @FXML private TableColumn<Produit, String>  colProduitNom;
    @FXML private TableColumn<Produit, Double>  colProduitPrix;
    @FXML private TableColumn<Produit, Integer> colProduitQuantite;
    @FXML private TableColumn<Produit, Void>    colProduitActions;

    // ── Navbar ─────────────────────────────────────────────────────
    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavUtilisateurs;

    // ── État ───────────────────────────────────────────────────────
    private final FournisseurRepository fournisseurRepo = new FournisseurRepository();
    private final ProduitRepository     produitRepo     = new ProduitRepository();
    private final ObservableList<Fournisseur> fournisseursData = FXCollections.observableArrayList();
    private final ObservableList<Produit>     produitsData     = FXCollections.observableArrayList();

    private Fournisseur fournisseurEnEdition = null;
    private Fournisseur fournisseurSelectionne = null;
    private Produit     produitEnEdition = null;

    // ══════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, null, null,
                btnNavCommandes, null, null, null, btnNavUtilisateurs, null);

        // Colonnes fournisseurs
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button mod = bouton("Modifier", "#3b82f6");
            private final Button sup = bouton("Supprimer", "#ef4444");
            private final HBox box = new HBox(6, mod, sup);
            {
                mod.setOnAction(e -> passerEnModeEditionFournisseur(getTableView().getItems().get(getIndex())));
                sup.setOnAction(e -> supprimerFournisseur(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Colonnes produits
        colProduitNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colProduitPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colProduitQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colProduitActions.setCellFactory(col -> new TableCell<>() {
            private final Button mod = bouton("Modifier", "#3b82f6");
            private final Button sup = bouton("Supprimer", "#ef4444");
            private final HBox box = new HBox(6, mod, sup);
            {
                mod.setOnAction(e -> passerEnModeEditionProduit(getTableView().getItems().get(getIndex())));
                sup.setOnAction(e -> supprimerProduit(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        fournisseursTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        produitsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        fournisseursTable.setItems(fournisseursData);
        produitsTable.setItems(produitsData);

        // Sélection fournisseur → charge ses produits
        fournisseursTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                fournisseurSelectionne = sel;
                fournisseurSelectionneLabel.setText("Produits de : " + sel.getNom());
                afficherBoutonNouveauProduit(true);
                chargerProduits(sel);
            }
        });

        chargerFournisseurs();
        messageLabel.setText("");
    }

    // ── CRUD Fournisseur ───────────────────────────────────────────

    @FXML
    private void handleAjouterFournisseur() {
        String nom   = nomFournisseurField.getText().trim();
        String email = emailFournisseurField.getText().trim();
        String telStr = telephoneFournisseurField.getText().trim();

        if (nom.isEmpty() || email.isEmpty() || telStr.isEmpty()) {
            afficherMessage("Veuillez remplir tous les champs", "error"); return;
        }
        int tel;
        try { tel = Integer.parseInt(telStr); }
        catch (NumberFormatException e) { afficherMessage("Téléphone invalide", "error"); return; }

        if (fournisseurEnEdition != null) {
            fournisseurEnEdition.setNom(nom);
            fournisseurEnEdition.setEmail(email);
            fournisseurEnEdition.setTel(tel);
            boolean ok = fournisseurRepo.modifierFournisseur(fournisseurEnEdition);
            afficherMessage(ok ? "Fournisseur modifié" : "Erreur modification", ok ? "success" : "error");
            fournisseurEnEdition = null;
            btnAjouter.setText("Ajouter le fournisseur");
        } else {
            boolean ok = fournisseurRepo.ajouterFournisseur(new Fournisseur(nom, email, tel));
            if (!ok) { afficherMessage("Erreur lors de l'ajout", "error"); return; }
            afficherMessage("Fournisseur ajouté", "success");
        }
        viderChampsFournisseur();
        chargerFournisseurs();
    }

    @FXML
    private void handleViderChamps() {
        viderChampsFournisseur();
        fournisseurEnEdition = null;
        btnAjouter.setText("Ajouter le fournisseur");
        afficherMessage("", "info");
    }

    private void passerEnModeEditionFournisseur(Fournisseur f) {
        fournisseurEnEdition = f;
        nomFournisseurField.setText(f.getNom());
        emailFournisseurField.setText(f.getEmail());
        telephoneFournisseurField.setText(String.valueOf(f.getTel()));
        btnAjouter.setText("Enregistrer les modifications");
        afficherMessage("Modification de : " + f.getNom(), "info");
    }

    private void supprimerFournisseur(Fournisseur f) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le fournisseur");
        alert.setContentText("Supprimer \"" + f.getNom() + "\" ?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                boolean ok = fournisseurRepo.supprimerFournisseur(f.getIdFournisseur());
                afficherMessage(ok ? "Fournisseur supprimé" : "Erreur suppression", ok ? "success" : "error");
                if (ok) {
                    produitsData.clear();
                    fournisseurSelectionne = null;
                    fournisseurSelectionneLabel.setText("Sélectionnez un fournisseur");
                    afficherBoutonNouveauProduit(false);
                    cacherFormProduit();
                }
                chargerFournisseurs();
            }
        });
    }

    // ── CRUD Produit ───────────────────────────────────────────────

    @FXML
    private void handleNouveauProduit() {
        produitEnEdition = null;
        viderChampsProduit();
        btnAjouterProduit.setText("Ajouter");
        afficherFormProduit(true);
    }

    @FXML
    private void handleAjouterProduit() {
        if (fournisseurSelectionne == null) {
            afficherMessageProduit("Sélectionnez d'abord un fournisseur", "error"); return;
        }
        String nom  = nomProduitField.getText().trim();
        String desc = descProduitField.getText().trim();
        String prixStr = prixProduitField.getText().trim();
        String qteStr  = quantiteProduitField.getText().trim();

        if (nom.isEmpty() || prixStr.isEmpty() || qteStr.isEmpty()) {
            afficherMessageProduit("Nom, prix et quantité sont requis", "error"); return;
        }
        double prix; int qte;
        try { prix = Double.parseDouble(prixStr.replace(",", ".")); }
        catch (NumberFormatException e) { afficherMessageProduit("Prix invalide", "error"); return; }
        try { qte = Integer.parseInt(qteStr); }
        catch (NumberFormatException e) { afficherMessageProduit("Quantité invalide", "error"); return; }

        if (produitEnEdition != null) {
            produitEnEdition.setNom(nom);
            produitEnEdition.setDescription(desc);
            produitEnEdition.setPrix(prix);
            produitEnEdition.setQuantite(qte);
            boolean ok = produitRepo.modifierProduit(produitEnEdition);
            afficherMessage(ok ? "Produit modifié" : "Erreur modification", ok ? "success" : "error");
            produitEnEdition = null;
            btnAjouterProduit.setText("Ajouter");
        } else {
            Produit p = new Produit(nom, desc, prix, qte, fournisseurSelectionne.getIdFournisseur());
            boolean ok = produitRepo.ajouterProduit(p);
            if (!ok) { afficherMessageProduit("Erreur lors de l'ajout", "error"); return; }
            afficherMessage("Produit ajouté", "success");
        }
        viderChampsProduit();
        afficherFormProduit(false);
        chargerProduits(fournisseurSelectionne);
    }

    @FXML
    private void handleAnnulerProduit() {
        viderChampsProduit();
        produitEnEdition = null;
        btnAjouterProduit.setText("Ajouter");
        afficherFormProduit(false);
        afficherMessageProduit("", "info");
    }

    private void passerEnModeEditionProduit(Produit p) {
        produitEnEdition = p;
        nomProduitField.setText(p.getNom());
        descProduitField.setText(p.getDescription() != null ? p.getDescription() : "");
        prixProduitField.setText(String.valueOf(p.getPrix()));
        quantiteProduitField.setText(String.valueOf(p.getQuantite()));
        btnAjouterProduit.setText("Enregistrer");
        afficherFormProduit(true);
        afficherMessageProduit("Modification de : " + p.getNom(), "info");
    }

    private void supprimerProduit(Produit p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Supprimer \"" + p.getNom() + "\" ?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                boolean ok = produitRepo.supprimerProduit(p.getIdProduit());
                afficherMessage(ok ? "Produit supprimé" : "Erreur suppression", ok ? "success" : "error");
                if (fournisseurSelectionne != null) chargerProduits(fournisseurSelectionne);
            }
        });
    }

    // ── Helpers ────────────────────────────────────────────────────

    private void chargerFournisseurs() {
        fournisseursData.setAll(fournisseurRepo.getAllFournisseurs());
    }

    private void chargerProduits(Fournisseur f) {
        produitsData.setAll(produitRepo.getProduitsParFournisseur(f.getIdFournisseur()));
    }

    private void afficherFormProduit(boolean visible) {
        formProduitBox.setVisible(visible);
        formProduitBox.setManaged(visible);
        btnNouveauProduit.setVisible(!visible);
        btnNouveauProduit.setManaged(!visible);
    }

    private void cacherFormProduit() {
        formProduitBox.setVisible(false);
        formProduitBox.setManaged(false);
        btnNouveauProduit.setVisible(false);
        btnNouveauProduit.setManaged(false);
    }

    private void afficherBoutonNouveauProduit(boolean visible) {
        btnNouveauProduit.setVisible(visible);
        btnNouveauProduit.setManaged(visible);
        if (!visible) {
            formProduitBox.setVisible(false);
            formProduitBox.setManaged(false);
        }
    }

    private void viderChampsFournisseur() {
        nomFournisseurField.clear();
        emailFournisseurField.clear();
        telephoneFournisseurField.clear();
    }

    private void viderChampsProduit() {
        nomProduitField.clear();
        descProduitField.clear();
        prixProduitField.clear();
        quantiteProduitField.clear();
    }

    private void afficherMessage(String msg, String type) {
        if (messageLabel == null) return;
        messageLabel.setText(msg);
        messageLabel.setStyle(switch (type) {
            case "error"   -> "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
            case "success" -> "-fx-text-fill: #10b981; -fx-font-weight: bold;";
            default        -> "-fx-text-fill: #818cf8; -fx-font-weight: bold;";
        });
    }

    private void afficherMessageProduit(String msg, String type) {
        if (messageProduitLabel == null) return;
        messageProduitLabel.setText(msg);
        messageProduitLabel.setStyle(switch (type) {
            case "error"   -> "-fx-text-fill: #ef4444; -fx-font-weight: bold;";
            case "success" -> "-fx-text-fill: #10b981; -fx-font-weight: bold;";
            default        -> "-fx-text-fill: #818cf8; -fx-font-weight: bold;";
        });
    }

    private Button bouton(String texte, String couleur) {
        Button b = new Button(texte);
        b.setStyle("-fx-background-color: " + couleur + "; -fx-text-fill: white; -fx-background-radius: 5;"
                + " -fx-cursor: hand; -fx-padding: 4 8; -fx-font-size: 11px;");
        return b;
    }

    // ── Navigation ─────────────────────────────────────────────────
    @FXML private void versAccueil()       { naviguer("pageAccueil"); }
    @FXML private void versPatients()      { naviguer("patientsView"); }
    @FXML private void versDossiers()      { naviguer("dossierEnChargeView"); }
    @FXML private void versCommandes()     { try { NavigationHelper.versCommandes(); } catch (Exception e) { e.printStackTrace(); } }
    @FXML private void versUtilisateurs()  { naviguer("pageUtilisateurs"); }
    @FXML private void versMonEspace()     { naviguer("pageMonEspace"); }
    @FXML private void deconnexion()       { naviguer("helloView"); }

    private void naviguer(String page) {
        try { StartApplication.changeScene(page); } catch (Exception e) { e.printStackTrace(); }
    }
}
