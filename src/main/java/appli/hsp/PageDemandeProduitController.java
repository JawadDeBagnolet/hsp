package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import modele.Demande;
import modele.FicheProduit;
import repository.DemandeProduitRepository;
import repository.DemandeProduitRepository.LigneDemandeProduit;
import repository.DemandeProduitRepository.ProduitCommande;
import repository.DemandeRepository;
import repository.FicheProduitRepository;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PageDemandeProduitController implements Initializable {

    // ── Navbar ──────────────────────────────────────────────────────
    @FXML private Button btnNavTickets;
    @FXML private Button btnNavInfirmerie;
    @FXML private Button btnNavDossiers;

    // ── Panneau gauche : formulaire ──────────────────────────────────
    @FXML private ComboBox<FicheProduit> produitCombo;
    @FXML private Spinner<Integer>       quantiteSpinner;
    @FXML private TableView<LignePanier> panier;
    @FXML private TableColumn<LignePanier, String>  colProduitNom;
    @FXML private TableColumn<LignePanier, Integer> colProduitQte;
    @FXML private TableColumn<LignePanier, Void>    colProduitRetirer;
    @FXML private TextArea noteArea;
    @FXML private Label    messageLabel;

    // ── Panneau droit : historique ───────────────────────────────────
    @FXML private TableView<Demande>     demandesTable;
    @FXML private TableColumn<Demande, Integer> colId;
    @FXML private TableColumn<Demande, String>  colDate;
    @FXML private TableColumn<Demande, String>  colStatut;
    @FXML private TableColumn<Demande, Integer> colQte;
    @FXML private TableColumn<Demande, Void>    colActions;

    // ── Panneau détail ───────────────────────────────────────────────
    @FXML private VBox   detailBox;
    @FXML private Text   detailTitre;
    @FXML private TableView<ProduitCommande>     detailTable;
    @FXML private TableColumn<ProduitCommande, String>  colDetailProduit;
    @FXML private TableColumn<ProduitCommande, Integer> colDetailQte;

    // ── Données ─────────────────────────────────────────────────────
    private final FicheProduitRepository   produitRepo  = new FicheProduitRepository();
    private final DemandeRepository        demandeRepo  = new DemandeRepository();
    private final DemandeProduitRepository dpRepo       = new DemandeProduitRepository();

    private final ObservableList<LignePanier> panierData    = FXCollections.observableArrayList();
    private final ObservableList<Demande>     demandesData  = FXCollections.observableArrayList();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Classe interne panier ────────────────────────────────────────
    public static class LignePanier {
        private final FicheProduit produit;
        private int quantite;

        public LignePanier(FicheProduit p, int q) { this.produit = p; this.quantite = q; }
        public FicheProduit getProduit()  { return produit; }
        public String getLibelle()        { return produit.getLibelle(); }
        public int    getQuantite()       { return quantite; }
        public void   setQuantite(int q)  { this.quantite = q; }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        NavbarHelper.appliquerNavbar(null, btnNavDossiers, btnNavTickets, btnNavInfirmerie, null, null, null, null, null, null);
        setupPanier();
        setupDemandesTable();
        chargerProduits();
        chargerMesDemandes();
    }

    // ── Setup tableaux ───────────────────────────────────────────────

    private void setupPanier() {
        colProduitNom.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getLibelle()));
        colProduitQte.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());

        colProduitRetirer.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✕");
            { btn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 3 8;");
              btn.setOnAction(e -> panierData.remove(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btn);
            }
        });

        panier.setItems(panierData);

        produitCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(FicheProduit p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getLibelle() + " (stock: " + p.getStockActuel() + ")");
            }
        });
        produitCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(FicheProduit p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getLibelle());
            }
        });
    }

    private void setupDemandesTable() {
        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdDemande()).asObject());
        colDate.setCellValueFactory(c -> {
            if (c.getValue().getDateDemande() == null)
                return new javafx.beans.property.SimpleStringProperty("-");
            return new javafx.beans.property.SimpleStringProperty(c.getValue().getDateDemande().format(FMT));
        });
        colQte.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());

        colStatut.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut()));
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                String bg = switch (s) {
                    case "En attente" -> "#fef3c7; -fx-text-fill: #92400e;";
                    case "Approuvée"  -> "#d1fae5; -fx-text-fill: #065f46;";
                    case "Refusée"    -> "#fee2e2; -fx-text-fill: #991b1b;";
                    case "Annulée"    -> "#f1f5f9; -fx-text-fill: #475569;";
                    default           -> "#ede9fe; -fx-text-fill: #5b21b6;";
                };
                setStyle("-fx-background-color: " + bg + " -fx-background-radius: 4; -fx-alignment: CENTER; -fx-font-weight: bold;");
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button voirBtn    = new Button("Détails");
            private final Button annulerBtn = new Button("Annuler");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, voirBtn, annulerBtn);

            {
                voirBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 4 10;");
                annulerBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 4 10;");

                voirBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    afficherDetail(d);
                });
                annulerBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    if (!"En attente".equals(d.getStatut())) {
                        setMessage("Seules les demandes 'En attente' peuvent être annulées.", false);
                        return;
                    }
                    if (demandeRepo.updateStatut(d.getIdDemande(), "Annulée")) {
                        chargerMesDemandes();
                        setMessage("Demande #" + d.getIdDemande() + " annulée.", true);
                    }
                });
            }

            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Demande d = getTableView().getItems().get(getIndex());
                annulerBtn.setVisible("En attente".equals(d.getStatut()));
                annulerBtn.setManaged("En attente".equals(d.getStatut()));
                setGraphic(box);
            }
        });

        demandesTable.setItems(demandesData);
    }

    // ── Chargements ──────────────────────────────────────────────────

    private void chargerProduits() {
        List<FicheProduit> produits = produitRepo.getAllFicheProduits();
        produitCombo.setItems(FXCollections.observableArrayList(produits));
        if (!produits.isEmpty()) produitCombo.setValue(produits.get(0));
    }

    private void chargerMesDemandes() {
        if (!SessionManager.estConnecte()) return;
        int idUser = SessionManager.getUtilisateurConnecte().getIdUser();
        demandesData.setAll(demandeRepo.getDemandesByUser(idUser));
        detailBox.setVisible(false);
        detailBox.setManaged(false);
    }

    // ── Actions formulaire ───────────────────────────────────────────

    @FXML
    public void handleAjouterProduit(ActionEvent event) {
        FicheProduit p = produitCombo.getValue();
        if (p == null) return;
        int qte = quantiteSpinner.getValue();

        // Merge si déjà dans le panier
        for (LignePanier ligne : panierData) {
            if (ligne.getProduit().getIdProduit() == p.getIdProduit()) {
                ligne.setQuantite(ligne.getQuantite() + qte);
                panier.refresh();
                return;
            }
        }
        panierData.add(new LignePanier(p, qte));
    }

    @FXML
    public void handleEnvoyerDemande(ActionEvent event) {
        if (panierData.isEmpty()) {
            setMessage("Ajoutez au moins un produit avant d'envoyer.", false);
            return;
        }
        if (!SessionManager.estConnecte()) return;
        int idUser = SessionManager.getUtilisateurConnecte().getIdUser();

        List<LigneDemandeProduit> lignes = new ArrayList<>();
        for (LignePanier lp : panierData) {
            lignes.add(new LigneDemandeProduit(lp.getProduit().getIdProduit(), lp.getQuantite()));
        }

        int idDemande = dpRepo.creerDemandeAvecProduits(idUser, lignes);
        if (idDemande > 0) {
            panierData.clear();
            noteArea.clear();
            chargerMesDemandes();
            setMessage("Demande #" + idDemande + " envoyée avec succès !", true);
        } else {
            setMessage("Erreur lors de l'envoi de la demande.", false);
        }
    }

    @FXML
    public void handleRafraichir(ActionEvent event) {
        chargerMesDemandes();
    }

    // ── Détail ───────────────────────────────────────────────────────

    private void afficherDetail(Demande d) {
        List<ProduitCommande> produits = dpRepo.getProduitsByDemande(d.getIdDemande());
        colDetailProduit.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getProduit().getLibelle()));
        colDetailQte.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        detailTable.setItems(FXCollections.observableArrayList(produits));
        detailTitre.setText("Produits de la demande #" + d.getIdDemande() + " — " + d.getStatut());
        detailBox.setVisible(true);
        detailBox.setManaged(true);
    }

    private void setMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: "
                + (success ? "#10b981;" : "#ef4444;"));
    }

    // ── Navigation ───────────────────────────────────────────────────

    @FXML public void versAccueil(ActionEvent e)    { try { StartApplication.changeScene("pageAccueil"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versTickets(ActionEvent e)     { try { StartApplication.changeScene("pageTickets"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versInfirmerie(ActionEvent e)  { try { StartApplication.changeScene("visiteInfirmerieView"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versDossiers(ActionEvent e)    { try { StartApplication.changeScene("dossierEnChargeView"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versMonEspace(ActionEvent e)   { try { StartApplication.changeScene("pageMonEspace"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void deconnexion(ActionEvent e)     { try { StartApplication.changeScene("helloView"); } catch (Exception ex) { ex.printStackTrace(); } }
}
