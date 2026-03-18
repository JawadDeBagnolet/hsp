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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import modele.Commande;
import modele.Demande;
import modele.FicheProduit;
import modele.Fournisseur;
import repository.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PageCommandes implements Initializable {

    // --- Onglet Demandes ---
    @FXML private TableView<Demande> demandesTable;
    @FXML private TableColumn<Demande, Integer> demIdCol;
    @FXML private TableColumn<Demande, String> demDateCol;
    @FXML private TableColumn<Demande, String> demInfirmierCol;
    @FXML private TableColumn<Demande, Integer> demQuantiteCol;
    @FXML private TableColumn<Demande, String> demStatutCol;
    @FXML private TableColumn<Demande, Void> demActionsCol;
    @FXML private ComboBox<String> filtreStatutCombo;

    // --- Onglet Commandes ---
    @FXML private TableView<Commande> commandesTable;
    @FXML private TableColumn<Commande, Integer> cmdIdCol;
    @FXML private TableColumn<Commande, String> cmdDateCol;
    @FXML private TableColumn<Commande, String> cmdFournisseurCol;
    @FXML private TableColumn<Commande, String> cmdLibelleCol;
    @FXML private TableColumn<Commande, String> cmdStatutCol;
    @FXML private TableColumn<Commande, Void> cmdActionsCol;

    private final DemandeRepository demandeRepo = new DemandeRepository();
    private final CommandeRepository commandeRepo = new CommandeRepository();
    private final FournisseurRepository fournisseurRepo = new FournisseurRepository();
    private final DemandeProduitRepository demandeProduitRepo = new DemandeProduitRepository();
    private final CommandeProduitRepository commandeProduitRepo = new CommandeProduitRepository();
    private final FicheProduitRepository ficheProduitRepo = new FicheProduitRepository();
    private final FournisseurProduitRepository fournisseurProduitRepo = new FournisseurProduitRepository();
    private final UserRepository userRepo = new UserRepository();

    private final ObservableList<Demande> demandesData = FXCollections.observableArrayList();
    private final ObservableList<Commande> commandesData = FXCollections.observableArrayList();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavUtilisateurs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, null, null, null, btnNavPlanning, btnNavCatalogue, null, btnNavUtilisateurs, null);

        filtreStatutCombo.setItems(FXCollections.observableArrayList(
                "Toutes", "En attente", "Approuvée", "Refusée", "Traitée"));
        filtreStatutCombo.setValue("En attente");

        setupDemandesTable();
        setupCommandesTable();
        chargerDemandes();
        chargerCommandes();
    }

    // ======================== SETUP TABLES ========================

    private void setupDemandesTable() {
        demIdCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdDemande()).asObject());
        demDateCol.setCellValueFactory(c -> {
            if (c.getValue().getDateDemande() == null)
                return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(c.getValue().getDateDemande().format(FMT));
        });
        demInfirmierCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(getUserLabel(c.getValue().getIdUser())));
        demQuantiteCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        demStatutCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut()));

        // Coloration du statut
        demStatutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Approuvée" -> setTextFill(Color.web("#10b981"));
                        case "Refusée"   -> setTextFill(Color.web("#ef4444"));
                        case "Traitée"   -> setTextFill(Color.web("#6366f1"));
                        default          -> setTextFill(Color.web("#f59e0b")); // En attente
                    }
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        // Colonne actions : Approuver | Refuser | Créer commande
        demActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button approuverBtn = new Button("✓ Approuver");
            private final Button refuserBtn   = new Button("✗ Refuser");
            private final Button cmdBtn       = new Button("Cmd fournisseur");
            private final HBox box = new HBox(5, approuverBtn, refuserBtn, cmdBtn);

            {
                approuverBtn.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 8;");
                refuserBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 8;");
                cmdBtn.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 8;");

                approuverBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    handleApprouverDemande(d);
                });
                refuserBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    handleRefuserDemande(d);
                });
                cmdBtn.setOnAction(e -> {
                    Demande d = getTableView().getItems().get(getIndex());
                    ouvrirDialogCreerCommande(d);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Demande d = getTableView().getItems().get(getIndex());
                    boolean enAttente = "En attente".equals(d.getStatut());
                    boolean traitee   = "Traitée".equals(d.getStatut());
                    approuverBtn.setDisable(!enAttente);
                    approuverBtn.setOpacity(enAttente ? 1.0 : 0.4);
                    refuserBtn.setDisable(!enAttente);
                    refuserBtn.setOpacity(enAttente ? 1.0 : 0.4);
                    cmdBtn.setDisable(traitee);
                    cmdBtn.setOpacity(traitee ? 0.4 : 1.0);
                    setGraphic(box);
                }
            }
        });

        demandesTable.setItems(demandesData);
    }

    private void setupCommandesTable() {
        cmdIdCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdCommande()).asObject());
        cmdDateCol.setCellValueFactory(c -> {
            if (c.getValue().getDateCommande() == null)
                return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(c.getValue().getDateCommande().format(FMT));
        });
        cmdFournisseurCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(getFournisseurNom(c.getValue().getIdFournisseur())));
        cmdLibelleCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getLibelle()));
        cmdStatutCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatut()));

        cmdStatutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("-fx-font-weight: bold;");
                switch (item) {
                    case "Livrée"    -> setTextFill(Color.web("#10b981"));
                    case "Annulée"   -> setTextFill(Color.web("#ef4444"));
                    case "Validée"   -> setTextFill(Color.web("#6366f1"));
                    default          -> setTextFill(Color.web("#f59e0b"));
                }
            }
        });

        cmdActionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button voirBtn   = new Button("Voir");
            private final Button statutBtn = new Button("Statut");
            private final HBox box = new HBox(6, voirBtn, statutBtn);

            {
                voirBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");
                statutBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 10;");
                voirBtn.setOnAction(e -> afficherDetailsCommande(getTableView().getItems().get(getIndex())));
                statutBtn.setOnAction(e -> changerStatutCommande(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        commandesTable.setItems(commandesData);
    }

    // ======================== CHARGEMENT ========================

    private void chargerDemandes() {
        demandesData.clear();
        List<Demande> all = demandeRepo.getAllDemandes();
        String filtre = (filtreStatutCombo != null && filtreStatutCombo.getValue() != null)
                ? filtreStatutCombo.getValue() : "Toutes";
        if ("Toutes".equals(filtre)) {
            demandesData.addAll(all);
        } else {
            for (Demande d : all) {
                if (filtre.equals(d.getStatut())) demandesData.add(d);
            }
        }
    }

    private void chargerCommandes() {
        commandesData.clear();
        commandesData.addAll(commandeRepo.getAllCommandes());
    }

    // ======================== ACTIONS DEMANDES ========================

    @FXML
    public void handleFiltreStatut(ActionEvent event) {
        chargerDemandes();
    }

    private void handleApprouverDemande(Demande d) {
        if (!"En attente".equals(d.getStatut())) {
            new Alert(Alert.AlertType.WARNING, "Cette demande n'est plus en attente.", ButtonType.OK).showAndWait();
            return;
        }

        List<DemandeProduitRepository.ProduitCommande> produits =
                demandeProduitRepo.getProduitsByDemande(d.getIdDemande());

        if (produits.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Aucun produit");
            a.setContentText("Cette demande ne contient aucun produit enregistré.");
            a.showAndWait();
            return;
        }

        // Vérifier stock disponible
        StringBuilder manquants = new StringBuilder();
        for (DemandeProduitRepository.ProduitCommande pc : produits) {
            FicheProduit fp = ficheProduitRepo.trouverFicheProduitParId(pc.getProduit().getIdProduit());
            if (fp != null && fp.getStockActuel() < pc.getQuantite()) {
                manquants.append("• ").append(fp.getLibelle())
                        .append(" : stock=").append(fp.getStockActuel())
                        .append(", demandé=").append(pc.getQuantite()).append("\n");
            }
        }

        if (manquants.length() > 0) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Stock insuffisant");
            a.setHeaderText("Certains produits ont un stock insuffisant :");
            a.setContentText(manquants + "\nApprouver quand même ?");
            a.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            if (a.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        }

        // Décrémenter le stock
        for (DemandeProduitRepository.ProduitCommande pc : produits) {
            ficheProduitRepo.decrementerStock(pc.getProduit().getIdProduit(), pc.getQuantite());
        }

        demandeRepo.updateStatut(d.getIdDemande(), "Approuvée");
        new Alert(Alert.AlertType.INFORMATION,
                "Demande #" + d.getIdDemande() + " approuvée. Stock mis à jour.", ButtonType.OK).showAndWait();
        chargerDemandes();
    }

    private void handleRefuserDemande(Demande d) {
        if (!"En attente".equals(d.getStatut())) {
            new Alert(Alert.AlertType.WARNING, "Cette demande n'est plus en attente.", ButtonType.OK).showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Refuser la demande");
        dialog.setHeaderText("Demande #" + d.getIdDemande() + " — " + getUserLabel(d.getIdUser()));
        dialog.setContentText("Motif du refus :");

        dialog.showAndWait().ifPresent(motif -> {
            if (motif.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez saisir un motif de refus.", ButtonType.OK).showAndWait();
                return;
            }
            demandeRepo.updateStatutAvecMotif(d.getIdDemande(), "Refusée", motif.trim());
            new Alert(Alert.AlertType.INFORMATION,
                    "Demande #" + d.getIdDemande() + " refusée.\nMotif : " + motif.trim(), ButtonType.OK).showAndWait();
            chargerDemandes();
        });
    }

    // ======================== ACTIONS COMMANDES ========================

    private void ouvrirDialogCreerCommande(Demande demande) {
        List<DemandeProduitRepository.ProduitCommande> produitsDemande =
                demandeProduitRepo.getProduitsByDemande(demande.getIdDemande());

        if (produitsDemande.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Cette demande ne contient aucun produit enregistré.", ButtonType.OK).showAndWait();
            return;
        }

        // Répartition automatique par fournisseur via algorithme glouton
        Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> repartition =
                assignerFournisseurs(produitsDemande);

        List<DemandeProduitRepository.ProduitCommande> sansFournisseur = repartition.get(null);
        long nbCommandes = repartition.keySet().stream().filter(Objects::nonNull).count();

        // Blocage si au moins un produit n'a aucun fournisseur
        if (sansFournisseur != null && !sansFournisseur.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    "Impossible de créer la commande.\n\n"
                    + "Les produits suivants n'ont aucun fournisseur associé :\n");
            for (DemandeProduitRepository.ProduitCommande pc : sansFournisseur)
                sb.append("  • ").append(pc.getProduit().getLibelle()).append(" × ").append(pc.getQuantite()).append("\n");
            sb.append("\nAssociez ces produits à un fournisseur dans le Catalogue avant de passer commande.");
            Alert erreur = new Alert(Alert.AlertType.ERROR);
            erreur.setTitle("Fournisseur manquant");
            erreur.setHeaderText("Certains produits n'ont pas de fournisseur");
            erreur.setContentText(sb.toString());
            erreur.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Commande fournisseur — Demande #" + demande.getIdDemande());
        dialog.setHeaderText("Répartition automatique par fournisseur");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox content = new VBox(12);
        content.setPrefWidth(520);

        Label titre = new Label(nbCommandes + " commande(s) vont être créées :");
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        content.getChildren().addAll(titre, construireApercu(repartition));

        TextField libelleField = new TextField("Commande — demande #" + demande.getIdDemande());
        libelleField.setPrefWidth(460);
        content.getChildren().addAll(new Label("Libellé de base :"), libelleField);

        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            String libelleBase = libelleField.getText().trim();
            if (libelleBase.isEmpty()) libelleBase = "Commande demande #" + demande.getIdDemande();
            int nb = creerCommandesSplittees(repartition, demande.getIdDemande(), libelleBase);
            if (nb > 0) {
                new Alert(Alert.AlertType.INFORMATION,
                        nb + " commande(s) créée(s).\nLa demande est marquée Traitée.", ButtonType.OK).showAndWait();
                chargerDemandes();
                chargerCommandes();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Erreur lors de la création.\n" + commandeProduitRepo.getLastError(),
                        ButtonType.OK).showAndWait();
            }
        });
    }

    /** Réapprovisionnement sans demande liée — répartition automatique par fournisseur. */
    @FXML
    public void handleNouvelleCommandeFournisseur(ActionEvent event) {
        List<FicheProduit> catalogue = ficheProduitRepo.getAllFicheProduits();
        if (catalogue.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Aucun produit dans le catalogue.", ButtonType.OK).showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Réapprovisionnement fournisseur");
        dialog.setHeaderText("Sélectionnez les produits — la répartition par fournisseur est automatique");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Tableau de sélection des produits
        ObservableList<CommandeController.LigneProduitUI> lignes = FXCollections.observableArrayList();
        TableView<CommandeController.LigneProduitUI> lignesTable = new TableView<>(lignes);
        lignesTable.setPrefHeight(160);
        TableColumn<CommandeController.LigneProduitUI, String> colProduit = new TableColumn<>("Produit");
        colProduit.setPrefWidth(250);
        colProduit.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProduitLibelle()));
        TableColumn<CommandeController.LigneProduitUI, Integer> colQte = new TableColumn<>("Quantité");
        colQte.setPrefWidth(90);
        colQte.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        lignesTable.getColumns().addAll(colProduit, colQte);

        // Zone de prévisualisation de la répartition (mise à jour dynamique)
        VBox apercuBox = new VBox(6);
        Label apercuTitre = new Label("Répartition : ajoutez des produits pour voir l'aperçu.");
        apercuTitre.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b;");
        apercuBox.getChildren().add(apercuTitre);

        // Listener : recalcule l'aperçu à chaque changement de la liste
        lignes.addListener((javafx.collections.ListChangeListener<CommandeController.LigneProduitUI>) c -> {
            apercuBox.getChildren().clear();
            if (lignes.isEmpty()) {
                Label l = new Label("Répartition : ajoutez des produits pour voir l'aperçu.");
                l.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b;");
                apercuBox.getChildren().add(l);
            } else {
                List<DemandeProduitRepository.ProduitCommande> lignesPc = lignes.stream()
                        .map(l2 -> new DemandeProduitRepository.ProduitCommande(l2.getProduit(), l2.getQuantite()))
                        .collect(Collectors.toList());
                Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> repartition =
                        assignerFournisseurs(lignesPc);
                long nb = repartition.keySet().stream().filter(Objects::nonNull).count();
                Label titre = new Label(nb + " commande(s) seront créées :");
                titre.setStyle("-fx-font-weight: bold;");
                apercuBox.getChildren().addAll(titre, construireApercu(repartition));
            }
        });

        TextField libelleField = new TextField("Réapprovisionnement");
        libelleField.setPrefWidth(460);

        Button ajouterBtn  = new Button("+ Ajouter produit");
        Button supprimerBtn = new Button("- Supprimer");
        supprimerBtn.disableProperty().bind(lignesTable.getSelectionModel().selectedItemProperty().isNull());

        ajouterBtn.setOnAction(e -> {
            Dialog<ButtonType> add = new Dialog<>();
            add.setTitle("Ajouter un produit");
            add.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            ComboBox<FicheProduit> produitCombo = new ComboBox<>(FXCollections.observableArrayList(catalogue));
            produitCombo.setCellFactory(p -> new ListCell<>() {
                @Override protected void updateItem(FicheProduit item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null
                            : item.getLibelle() + "  (stock: " + item.getStockActuel() + ")");
                }
            });
            produitCombo.setButtonCell(new ListCell<>() {
                @Override protected void updateItem(FicheProduit item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getLibelle());
                }
            });
            if (!catalogue.isEmpty()) produitCombo.setValue(catalogue.get(0));
            Spinner<Integer> qteSpinner = new Spinner<>(1, 10000, 1);
            qteSpinner.setEditable(true);
            GridPane g = new GridPane();
            g.setHgap(10); g.setVgap(10);
            g.add(new Label("Produit :"),  0, 0); g.add(produitCombo, 1, 0);
            g.add(new Label("Quantité :"), 0, 1); g.add(qteSpinner,   1, 1);
            add.getDialogPane().setContent(g);
            add.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK && produitCombo.getValue() != null)
                    lignes.add(new CommandeController.LigneProduitUI(produitCombo.getValue(), qteSpinner.getValue()));
            });
        });
        supprimerBtn.setOnAction(e -> {
            CommandeController.LigneProduitUI sel = lignesTable.getSelectionModel().getSelectedItem();
            if (sel != null) lignes.remove(sel);
        });

        VBox content = new VBox(10,
                new Label("Produits à commander :"),
                lignesTable,
                new HBox(8, ajouterBtn, supprimerBtn),
                new Separator(),
                apercuBox,
                new Separator(),
                new Label("Libellé de base :"),
                libelleField);
        content.setPrefWidth(520);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
            if (lignes.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Ajoutez au moins un produit.", ButtonType.OK).showAndWait();
                return;
            }
            List<DemandeProduitRepository.ProduitCommande> lignesPc = lignes.stream()
                    .map(l -> new DemandeProduitRepository.ProduitCommande(l.getProduit(), l.getQuantite()))
                    .collect(Collectors.toList());
            Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> repartition =
                    assignerFournisseurs(lignesPc);
            long nbCmd = repartition.keySet().stream().filter(Objects::nonNull).count();
            if (nbCmd == 0) {
                new Alert(Alert.AlertType.WARNING,
                        "Aucun produit n'a de fournisseur associé. Impossible de créer des commandes.",
                        ButtonType.OK).showAndWait();
                return;
            }
            String libelleBase = libelleField.getText().trim();
            if (libelleBase.isEmpty()) libelleBase = "Réapprovisionnement";
            int nb = creerCommandesSplittees(repartition, 0, libelleBase);
            if (nb > 0) {
                new Alert(Alert.AlertType.INFORMATION,
                        nb + " commande(s) de réapprovisionnement créée(s) !", ButtonType.OK).showAndWait();
                chargerCommandes();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Erreur lors de la création.\n" + commandeProduitRepo.getLastError(),
                        ButtonType.OK).showAndWait();
            }
        });
    }

    private void afficherDetailsCommande(Commande cmd) {
        List<CommandeProduitRepository.ProduitCommande> produits =
                commandeProduitRepo.getProduitsByCommande(cmd.getIdCommande());

        StringBuilder sb = new StringBuilder();
        sb.append("Libellé : ").append(cmd.getLibelle()).append("\n");
        sb.append("Fournisseur : ").append(getFournisseurNom(cmd.getIdFournisseur())).append("\n");
        sb.append("Date : ").append(cmd.getDateCommande() != null ? cmd.getDateCommande().format(FMT) : "N/A").append("\n\n");
        sb.append("Produits :\n");
        if (produits.isEmpty()) {
            sb.append("  (aucun produit enregistré)");
        } else {
            for (CommandeProduitRepository.ProduitCommande pc : produits) {
                sb.append("  • ").append(pc.getProduit().getLibelle()).append(" × ").append(pc.getQuantite()).append("\n");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails commande #" + cmd.getIdCommande());
        alert.setHeaderText("Commande #" + cmd.getNumCommande() + " — " + cmd.getStatut());
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    private void changerStatutCommande(Commande cmd) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                cmd.getStatut(), "En attente", "Validée", "Livrée", "Annulée");
        dialog.setTitle("Changer le statut");
        dialog.setHeaderText("Commande #" + cmd.getIdCommande());
        dialog.setContentText("Nouveau statut :");
        dialog.showAndWait().ifPresent(statut -> {
            if (commandeRepo.updateStatut(cmd.getIdCommande(), statut)) {
                chargerCommandes();
                if ("Livrée".equals(statut)) {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Commande marquée Livrée. Le stock a été mis à jour.", ButtonType.OK).showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Impossible de modifier le statut.", ButtonType.OK).showAndWait();
            }
        });
    }

    // ======================== HANDLERS FXML ========================

    @FXML public void handleRafraichirDemandes(ActionEvent event) { chargerDemandes(); }
    @FXML public void handleRafraichirCommandes(ActionEvent event) { chargerCommandes(); }

    // ======================== NAVIGATION ========================

    @FXML public void versAccueil(ActionEvent event) {
        try { StartApplication.changeScene("pageAccueil"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versPatients(ActionEvent event) {
        try { StartApplication.changeScene("patientsView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versDossiers(ActionEvent event) {
        try { StartApplication.changeScene("dossierEnChargeView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versCommandes(ActionEvent event) { System.out.println("Déjà sur la page commandes"); }
    @FXML public void versPlanning(ActionEvent event) {
        try { StartApplication.changeScene("planningView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versFicheProduit(ActionEvent event) {
        try { StartApplication.changeScene("ficheProduitView"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versUtilisateurs(ActionEvent event) {
        try { StartApplication.changeScene("pageUtilisateurs"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void versMonEspace(ActionEvent event) {
        try { StartApplication.changeScene("pageMonEspace"); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML public void deconnexion(ActionEvent event) {
        try { StartApplication.changeScene("helloView"); } catch (Exception e) { e.printStackTrace(); }
    }

    // ======================== HELPERS ========================

    private ComboBox<Fournisseur> buildFournisseurCombo(List<Fournisseur> fournisseurs) {
        ComboBox<Fournisseur> combo = new ComboBox<>(FXCollections.observableArrayList(fournisseurs));
        combo.setCellFactory(p -> new ListCell<>() {
            @Override protected void updateItem(Fournisseur f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getNom() + " (" + f.getEmail() + ")");
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Fournisseur f, boolean empty) {
                super.updateItem(f, empty);
                setText(empty || f == null ? null : f.getNom());
            }
        });
        combo.setPrefWidth(300);
        if (!fournisseurs.isEmpty()) combo.setValue(fournisseurs.get(0));
        return combo;
    }

    private TableView<DemandeProduitRepository.ProduitCommande> buildProduitsPreviewTable(
            List<DemandeProduitRepository.ProduitCommande> produits) {
        TableView<DemandeProduitRepository.ProduitCommande> table = new TableView<>();
        TableColumn<DemandeProduitRepository.ProduitCommande, String> nomCol = new TableColumn<>("Produit");
        nomCol.setPrefWidth(220);
        nomCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProduit() != null ? c.getValue().getProduit().getLibelle() : ""));
        TableColumn<DemandeProduitRepository.ProduitCommande, Integer> qteCol = new TableColumn<>("Quantité");
        qteCol.setPrefWidth(90);
        qteCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        table.getColumns().addAll(nomCol, qteCol);
        table.setItems(FXCollections.observableArrayList(produits));
        table.setPrefHeight(140);
        return table;
    }

    private String getUserLabel(int idUser) {
        if (idUser <= 0) return "";
        try {
            modele.User u = userRepo.trouverUtilisateurParId(idUser);
            if (u == null) return "Utilisateur #" + idUser;
            String label = (u.getNom() + " " + u.getPrenom()).trim();
            return label.isEmpty() ? "Utilisateur #" + idUser : label;
        } catch (Exception e) {
            return "Utilisateur #" + idUser;
        }
    }

    private String getFournisseurNom(int idFournisseur) {
        if (idFournisseur <= 0) return "-";
        try {
            Fournisseur f = fournisseurRepo.trouverFournisseurParId(idFournisseur);
            return f != null ? f.getNom() : "Fournisseur #" + idFournisseur;
        } catch (Exception e) {
            return "Fournisseur #" + idFournisseur;
        }
    }

    /**
     * Algorithme glouton de set-cover :
     * assigne chaque produit au fournisseur qui couvre le plus de produits restants.
     * Retourne une map Fournisseur → produits à commander.
     * La clé null regroupe les produits sans aucun fournisseur.
     */
    private Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> assignerFournisseurs(
            List<DemandeProduitRepository.ProduitCommande> produits) {

        // Construire la map produit → fournisseurs disponibles
        Map<Integer, List<Fournisseur>> produitFournisseurs = new HashMap<>();
        for (DemandeProduitRepository.ProduitCommande pc : produits) {
            int idProduit = pc.getProduit().getIdProduit();
            List<Fournisseur> f = fournisseurProduitRepo.getFournisseursParProduitObj(idProduit);
            produitFournisseurs.put(idProduit, f);
        }

        Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> result = new LinkedHashMap<>();
        Set<Integer> restants = new HashSet<>();
        for (DemandeProduitRepository.ProduitCommande pc : produits)
            restants.add(pc.getProduit().getIdProduit());

        // Map id_produit → ProduitCommande pour retrouver facilement l'objet
        Map<Integer, DemandeProduitRepository.ProduitCommande> parId = new HashMap<>();
        for (DemandeProduitRepository.ProduitCommande pc : produits)
            parId.put(pc.getProduit().getIdProduit(), pc);

        // Produits sans fournisseur du tout
        List<DemandeProduitRepository.ProduitCommande> sansFournisseur = new ArrayList<>();
        for (DemandeProduitRepository.ProduitCommande pc : produits) {
            if (produitFournisseurs.getOrDefault(pc.getProduit().getIdProduit(), List.of()).isEmpty()) {
                sansFournisseur.add(pc);
                restants.remove(pc.getProduit().getIdProduit());
            }
        }
        if (!sansFournisseur.isEmpty()) result.put(null, sansFournisseur);

        // Glouton : tant qu'il reste des produits à assigner
        while (!restants.isEmpty()) {
            // Compter combien de produits restants chaque fournisseur peut couvrir
            Map<Fournisseur, List<Integer>> couverture = new HashMap<>();
            for (int idProduit : restants) {
                for (Fournisseur f : produitFournisseurs.getOrDefault(idProduit, List.of())) {
                    couverture.computeIfAbsent(f, k -> new ArrayList<>()).add(idProduit);
                }
            }
            if (couverture.isEmpty()) break; // sécurité

            // Choisir le fournisseur qui couvre le plus de produits restants
            Fournisseur meilleur = couverture.entrySet().stream()
                    .max(Comparator.comparingInt(e -> e.getValue().size()))
                    .map(Map.Entry::getKey).orElse(null);
            if (meilleur == null) break;

            List<DemandeProduitRepository.ProduitCommande> lignes = new ArrayList<>();
            for (int idProduit : couverture.get(meilleur)) {
                lignes.add(parId.get(idProduit));
                restants.remove(idProduit);
            }
            result.put(meilleur, lignes);
        }

        return result;
    }

    /**
     * Construit un aperçu VBox listant chaque fournisseur et ses produits.
     */
    private VBox construireApercu(
            Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> repartition) {
        VBox box = new VBox(8);
        for (Map.Entry<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> entry : repartition.entrySet()) {
            if (entry.getKey() == null) continue; // produits sans fournisseur déjà affichés
            VBox card = new VBox(4);
            card.setStyle("-fx-background-color: #f0f4ff; -fx-padding: 8; -fx-background-radius: 6; "
                    + "-fx-border-color: #c7d2fe; -fx-border-width: 1; -fx-border-radius: 6;");
            Label titre = new Label("📦 " + entry.getKey().getNom());
            titre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            card.getChildren().add(titre);
            for (DemandeProduitRepository.ProduitCommande pc : entry.getValue()) {
                Label ligne = new Label("  • " + pc.getProduit().getLibelle() + " × " + pc.getQuantite());
                ligne.setStyle("-fx-font-size: 12px;");
                card.getChildren().add(ligne);
            }
            box.getChildren().add(card);
        }
        return box;
    }

    /**
     * Crée une commande par fournisseur dans la répartition.
     * Passe idDemande > 0 uniquement à la première commande pour marquer la demande Traitée.
     * Retourne le nombre de commandes créées.
     */
    private int creerCommandesSplittees(
            Map<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> repartition,
            int idDemande, String libelleBase) {

        modele.User session = appli.SessionManager.getUtilisateurConnecte();
        int idUser = session != null ? session.getIdUser() : 0;
        int nbCrees = 0;
        boolean demandeMarquee = false;

        for (Map.Entry<Fournisseur, List<DemandeProduitRepository.ProduitCommande>> entry : repartition.entrySet()) {
            if (entry.getKey() == null) continue;

            List<CommandeProduitRepository.LigneCommandeProduit> lignes = new ArrayList<>();
            for (DemandeProduitRepository.ProduitCommande pc : entry.getValue()) {
                lignes.add(new CommandeProduitRepository.LigneCommandeProduit(
                        pc.getProduit().getIdProduit(), pc.getQuantite()));
            }

            String libelle = libelleBase + " — " + entry.getKey().getNom();
            // Passe l'idDemande à la première commande seulement pour mettre à jour le statut une fois
            int demandeId = (!demandeMarquee && idDemande > 0) ? idDemande : 0;
            int idCmd = commandeProduitRepo.creerCommandeAvecProduits(
                    idUser, entry.getKey().getIdFournisseur(), libelle, lignes, demandeId);

            if (idCmd > 0) {
                nbCrees++;
                demandeMarquee = true;
            }
        }
        return nbCrees;
    }
}
