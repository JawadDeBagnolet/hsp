package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import modele.FichePatient;
import modele.FicheProduit;
import modele.Ticket;
import modele.VisiteInfirmerie;
import repository.FichePatientRepository;
import repository.FicheProduitRepository;
import repository.TicketRepository;
import repository.VisiteInfirmerieRepository;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class PageTickets implements Initializable {

    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, Integer> colId;
    @FXML private TableColumn<Ticket, String>  colDate;
    @FXML private TableColumn<Ticket, String>  colEleve;
    @FXML private TableColumn<Ticket, String>  colMotif;
    @FXML private TableColumn<Ticket, String>  colStatut;
    @FXML private TableColumn<Ticket, String>  colPrescription;
    @FXML private TableColumn<Ticket, Void>    colActions;
    @FXML private ComboBox<String>             filtreStatutCombo;
    @FXML private Button                       btnNouveauTicket;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavDemandes;

    private final TicketRepository ticketRepo           = new TicketRepository();
    private final FicheProduitRepository produitRepo    = new FicheProduitRepository();
    private final FichePatientRepository eleveRepo      = new FichePatientRepository();
    private final VisiteInfirmerieRepository visiteRepo = new VisiteInfirmerieRepository();
    private final ObservableList<Ticket> data           = FXCollections.observableArrayList();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final List<String> STATUTS = List.of(
        Ticket.STATUT_ATTENTE,
        Ticket.STATUT_RETOUR,
        Ticket.STATUT_EA,
        Ticket.STATUT_MAISON
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, null, null, null, btnNavCommandes, btnNavPlanning, btnNavCatalogue, null, null, btnNavDemandes);
        filtreStatutCombo.setItems(FXCollections.observableArrayList(STATUTS));

        String role = SessionManager.getUtilisateurConnecte() != null
                ? SessionManager.getUtilisateurConnecte().getRole() : "";
        boolean estSecretaire = "SECRETAIRE".equals(role) || "ADMIN".equals(role);

        btnNouveauTicket.setVisible(estSecretaire);
        btnNouveauTicket.setManaged(estSecretaire);

        setupTable();
        charger();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdTicket()).asObject());
        colDate.setCellValueFactory(c -> {
            if (c.getValue().getDateCreation() == null) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(c.getValue().getDateCreation().format(FMT));
        });
        colEleve.setCellValueFactory(c -> {
            String n = c.getValue().getNomEleve();
            return new javafx.beans.property.SimpleStringProperty(n != null ? n : "Élève #" + c.getValue().getIdEleve());
        });
        colMotif.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getMotif() != null ? c.getValue().getMotif() : ""));
        colStatut.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut()));
        colPrescription.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPrescription() != null ? c.getValue().getPrescription() : ""));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                String bg = switch (s) {
                    case Ticket.STATUT_ATTENTE -> "#fef3c7; -fx-text-fill: #92400e;";
                    case Ticket.STATUT_RETOUR  -> "#dbeafe; -fx-text-fill: #1e40af;";
                    case Ticket.STATUT_EA      -> "#fce7f3; -fx-text-fill: #9d174d;";
                    case Ticket.STATUT_MAISON  -> "#d1fae5; -fx-text-fill: #065f46;";
                    default -> "#f1f5f9; -fx-text-fill: #334155;";
                };
                setStyle("-fx-background-color: " + bg + " -fx-background-radius: 4; -fx-alignment: CENTER; -fx-font-weight: bold;");
            }
        });

        String role = SessionManager.getUtilisateurConnecte() != null
                ? SessionManager.getUtilisateurConnecte().getRole() : "";
        boolean peutTraiter = "INFIRMIER".equals(role) || "ADMIN".equals(role);

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button actionBtn    = new Button("Prendre en charge");
            private final Button supprimerBtn = new Button("Supprimer");
            private final HBox boxInfirmier  = new HBox(6, actionBtn, supprimerBtn);
            private final HBox boxSecretaire = new HBox(6, supprimerBtn);

            {
                actionBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");
                supprimerBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");

                actionBtn.setOnAction(e -> {
                    Ticket t = getTableView().getItems().get(getIndex());
                    choisirStatut(t);
                });
                supprimerBtn.setOnAction(e -> {
                    Ticket t = getTableView().getItems().get(getIndex());
                    if (ticketRepo.supprimerTicket(t.getIdTicket())) charger();
                });
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : (peutTraiter ? boxInfirmier : boxSecretaire));
            }
        });

        ticketsTable.setItems(data);
    }

    /** Classe interne pour les lignes du tableau produits donnés */
    public static class LigneProduit {
        private final FicheProduit produit;
        private int quantite;
        public LigneProduit(FicheProduit p, int q) { this.produit = p; this.quantite = q; }
        public FicheProduit getProduit() { return produit; }
        public int getQuantite()         { return quantite; }
        public String getLibelle()       { return produit.getLibelle(); }
        public int getStock()            { return produit.getStockActuel(); }
    }

    private void choisirStatut(Ticket t) {
        java.util.List<FicheProduit> catalogue = produitRepo.getAllFicheProduits();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Prise en charge");
        dialog.setHeaderText("Ticket #" + t.getIdTicket()
                + " — " + (t.getNomEleve() != null ? t.getNomEleve() : "Élève #" + t.getIdEleve()));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // --- Statut ---
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll(Ticket.STATUT_ATTENTE, Ticket.STATUT_RETOUR, Ticket.STATUT_EA, Ticket.STATUT_MAISON);
        statutCombo.setValue(t.getStatut());
        statutCombo.setPrefWidth(260);

        // --- Statut visite ---
        ComboBox<String> statutVisiteCombo = new ComboBox<>();
        statutVisiteCombo.getItems().addAll("Terminée", "En cours", "Urgences");
        statutVisiteCombo.setValue("Terminée");
        statutVisiteCombo.setPrefWidth(160);

        // --- Traitement / Notes ---
        TextArea notesArea = new TextArea(t.getPrescription() != null ? t.getPrescription() : "");
        notesArea.setPromptText("Notes, observations...");
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);

        // --- Produits donnés ---
        ObservableList<LigneProduit> lignes = FXCollections.observableArrayList();

        TableView<LigneProduit> produitsTable = new TableView<>(lignes);
        produitsTable.setPrefHeight(130);

        TableColumn<LigneProduit, String> colProd = new TableColumn<>("Produit");
        colProd.setPrefWidth(180);
        colProd.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLibelle()));

        TableColumn<LigneProduit, Integer> colQte = new TableColumn<>("Qté");
        colQte.setPrefWidth(60);
        colQte.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());

        TableColumn<LigneProduit, Integer> colStock = new TableColumn<>("Stock dispo");
        colStock.setPrefWidth(90);
        colStock.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getStock()).asObject());

        produitsTable.getColumns().addAll(colProd, colQte, colStock);

        Button ajouterProdBtn = new Button("+ Ajouter produit");
        Button suppProdBtn    = new Button("Retirer");
        suppProdBtn.disableProperty().bind(produitsTable.getSelectionModel().selectedItemProperty().isNull());
        suppProdBtn.setOnAction(e -> {
            LigneProduit sel = produitsTable.getSelectionModel().getSelectedItem();
            if (sel != null) lignes.remove(sel);
        });

        ajouterProdBtn.setOnAction(e -> {
            if (catalogue.isEmpty()) return;
            Dialog<ButtonType> add = new Dialog<>();
            add.setTitle("Ajouter un produit");
            add.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            ComboBox<FicheProduit> prodCombo = new ComboBox<>(FXCollections.observableArrayList(catalogue));
            prodCombo.setCellFactory(p -> new ListCell<>() {
                @Override protected void updateItem(FicheProduit f, boolean empty) {
                    super.updateItem(f, empty);
                    setText(empty || f == null ? null : f.getLibelle() + " (stock: " + f.getStockActuel() + ")");
                }
            });
            prodCombo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(FicheProduit f, boolean empty) {
                    super.updateItem(f, empty);
                    setText(empty || f == null ? null : f.getLibelle());
                }
            });
            prodCombo.setValue(catalogue.get(0));

            Spinner<Integer> qteSpinner = new Spinner<>(1, 9999, 1);
            qteSpinner.setEditable(true);

            GridPane g = new GridPane();
            g.setHgap(10); g.setVgap(8);
            g.add(new Label("Produit :"), 0, 0);  g.add(prodCombo, 1, 0);
            g.add(new Label("Quantité :"), 0, 1); g.add(qteSpinner, 1, 1);
            add.getDialogPane().setContent(g);

            add.showAndWait().ifPresent(r -> {
                if (r != ButtonType.OK) return;
                FicheProduit fp = prodCombo.getValue();
                int qte = qteSpinner.getValue();
                if (fp != null && qte > 0) lignes.add(new LigneProduit(fp, qte));
            });
        });

        HBox btnsProd = new HBox(8, ajouterProdBtn, suppProdBtn);

        // --- Assemblage ---
        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.add(new Label("Statut ticket :"), 0, 0);
        grid.add(statutCombo, 1, 0);
        grid.add(new Label("Statut visite :"), 0, 1);
        grid.add(statutVisiteCombo, 1, 1);
        grid.add(new Label("Produits donnés :"), 0, 2);
        grid.add(produitsTable, 0, 3, 2, 1);
        grid.add(btnsProd, 0, 4, 2, 1);
        grid.add(new Label("Traitement :"), 0, 5);
        grid.add(notesArea, 0, 6, 2, 1);
        GridPane.setHgrow(notesArea, Priority.ALWAYS);
        GridPane.setHgrow(produitsTable, Priority.ALWAYS);
        GridPane.setHgrow(statutVisiteCombo, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(440);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;

            // Vérifier le stock disponible
            for (LigneProduit lp : lignes) {
                if (lp.getQuantite() > lp.getStock()) {
                    new Alert(Alert.AlertType.ERROR,
                            "Stock insuffisant pour " + lp.getLibelle()
                            + " (disponible : " + lp.getStock() + ")", ButtonType.OK).showAndWait();
                    return;
                }
            }

            // Décrémenter le stock
            for (LigneProduit lp : lignes) {
                produitRepo.decrementerStock(lp.getProduit().getIdProduit(), lp.getQuantite());
            }

            // Construire le texte de prescription
            StringBuilder sb = new StringBuilder();
            for (LigneProduit lp : lignes) {
                sb.append("• ").append(lp.getLibelle()).append(" × ").append(lp.getQuantite()).append("\n");
            }
            String notes = notesArea.getText().trim();
            if (!notes.isEmpty()) sb.append(notes);

            String statut = statutCombo.getValue();
            String prescription = sb.isEmpty() ? null : sb.toString().trim();
            ticketRepo.updateStatutEtPrescription(t.getIdTicket(), statut, prescription);

            // Créer automatiquement une visite infirmerie à partir du ticket
            Integer idInfirmier = SessionManager.getUtilisateurConnecte() != null
                    ? SessionManager.getUtilisateurConnecte().getIdUser() : null;
            VisiteInfirmerie visite = new VisiteInfirmerie(
                    t.getIdEleve(),
                    LocalDate.now(),
                    LocalTime.now(),
                    t.getMotif(),
                    prescription,
                    statutVisiteCombo.getValue(),
                    idInfirmier
            );
            visiteRepo.ajouterVisite(visite);

            charger();
        });
    }

    // ======================== TICKETS ========================

    @FXML
    private void handleNouveauTicket(ActionEvent e) {
        List<FichePatient> eleves = eleveRepo.getAllFichePatients();
        if (eleves.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Aucun élève enregistré.", ButtonType.OK).showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouveau ticket infirmerie");
        dialog.setHeaderText("Envoyer un élève à l'infirmerie");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<FichePatient> eleveCombo = new ComboBox<>(FXCollections.observableArrayList(eleves));
        eleveCombo.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(FichePatient f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getPrenom() + " " + f.getNom());
            }
        });
        eleveCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(FichePatient f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getPrenom() + " " + f.getNom());
            }
        });
        eleveCombo.setValue(eleves.get(0));
        eleveCombo.setPrefWidth(240);

        TextArea motifArea = new TextArea();
        motifArea.setPromptText("Motif de l'envoi à l'infirmerie...");
        motifArea.setPrefRowCount(3);
        motifArea.setWrapText(true);

        Label statutLabel = new Label(Ticket.STATUT_ATTENTE);
        statutLabel.setStyle("-fx-text-fill: #92400e; -fx-background-color: #fef3c7; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.add(new Label("Élève :"),  0, 0); grid.add(eleveCombo,  1, 0);
        grid.add(new Label("Motif :"),  0, 1); grid.add(motifArea,   1, 1);
        grid.add(new Label("Statut :"), 0, 2); grid.add(statutLabel, 1, 2);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(420);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            FichePatient eleve = eleveCombo.getValue();
            String motif = motifArea.getText().trim();
            if (eleve == null || motif.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un élève et saisir un motif.", ButtonType.OK).showAndWait();
                return;
            }
            int idSecretaire = SessionManager.getUtilisateurConnecte().getIdUser();
            int idTicket = ticketRepo.creerTicket(eleve.getIdFichePatient(), idSecretaire, motif);
            if (idTicket > 0) charger();
        });
    }

    private void charger() {
        String filtre = filtreStatutCombo.getValue();
        List<Ticket> tickets = (filtre != null)
                ? ticketRepo.getTicketsParStatut(filtre)
                : ticketRepo.getAllTickets();
        data.setAll(tickets);
    }

    @FXML public void handleRafraichir(ActionEvent e)    { charger(); }
    @FXML public void handleToutAfficher(ActionEvent e)  { filtreStatutCombo.setValue(null); charger(); }

    // Navigation
    @FXML public void versAccueil(ActionEvent e)     { try { StartApplication.changeScene("pageAccueil"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versPatients(ActionEvent e)    { try { StartApplication.changeScene("patientsView"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versCommandes(ActionEvent e)   { try { NavigationHelper.versCommandes(); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versPlanning(ActionEvent e)    { try { StartApplication.changeScene("planningView"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versFicheProduit(ActionEvent e){ try { StartApplication.changeScene("ficheProduitView"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versMonEspace(ActionEvent e)   { try { StartApplication.changeScene("pageMonEspace"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void versDemandes(ActionEvent e)    { try { StartApplication.changeScene("pageDemandeProduit"); } catch (Exception ex) { ex.printStackTrace(); } }
    @FXML public void deconnexion(ActionEvent e)     { try { StartApplication.changeScene("helloView"); } catch (Exception ex) { ex.printStackTrace(); } }
}
