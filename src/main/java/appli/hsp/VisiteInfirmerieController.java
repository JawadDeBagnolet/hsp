package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import modele.FichePatient;
import modele.VisiteInfirmerie;
import repository.FichePatientRepository;
import repository.VisiteInfirmerieRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class VisiteInfirmerieController {

    // ── Navbar ──────────────────────────────────────────────────────
    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavDemandes;

    // ── TableView ────────────────────────────────────────────────────
    @FXML private TableView<VisiteInfirmerie>             visitesTable;
    @FXML private TableColumn<VisiteInfirmerie, Integer>  colId;
    @FXML private TableColumn<VisiteInfirmerie, String>   colEleve;
    @FXML private TableColumn<VisiteInfirmerie, String>   colDate;
    @FXML private TableColumn<VisiteInfirmerie, String>   colHeure;
    @FXML private TableColumn<VisiteInfirmerie, String>   colMotif;
    @FXML private TableColumn<VisiteInfirmerie, String>   colTraitement;
    @FXML private TableColumn<VisiteInfirmerie, String>   colStatut;

    // ── Stats ────────────────────────────────────────────────────────
    @FXML private Label totalVisitesLabel;
    @FXML private Label messageLabel;

    // ── Filtres ──────────────────────────────────────────────────────
    @FXML private DatePicker             filtreDate;
    @FXML private ComboBox<FichePatient> filtreEleveCombo;

    // ── Formulaire ───────────────────────────────────────────────────
    @FXML private ComboBox<FichePatient> eleveComboBox;
    @FXML private DatePicker             datePicker;
    @FXML private TextField              heureField;
    @FXML private TextField              motifField;
    @FXML private TextArea               traitementArea;
    @FXML private ComboBox<String>       statutCombo;

    @FXML private Button ajouterButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;

    // ── Données ──────────────────────────────────────────────────────
    private final VisiteInfirmerieRepository visiteRepository   = new VisiteInfirmerieRepository();
    private final FichePatientRepository     eleveRepository    = new FichePatientRepository();

    private final ObservableList<VisiteInfirmerie> visitesList = FXCollections.observableArrayList();
    private final ObservableList<FichePatient>     elevesList  = FXCollections.observableArrayList();

    private VisiteInfirmerie visiteSelectionnee;

    // ── Initialisation ───────────────────────────────────────────────

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, null, null, null, btnNavCommandes, null, null, null, null, btnNavDemandes);

        chargerEleves();
        configurerEleveComboBox();
        configurerFiltreEleveCombo();
        configurerStatutCombo();
        configurerTable();

        datePicker.setValue(LocalDate.now());
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);

        Platform.runLater(this::loadVisites);
    }

    private void chargerEleves() {
        elevesList.setAll(eleveRepository.getAllFichePatients());
    }

    private void configurerEleveComboBox() {
        eleveComboBox.setItems(elevesList);
        eleveComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FichePatient item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getPrenom() + " " + item.getNom() + " (" + item.getNum_etudiant() + ")");
            }
        });
        eleveComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FichePatient item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getPrenom() + " " + item.getNom());
            }
        });
    }

    // ── Point 4 : filtre par élève ────────────────────────────────────
    private void configurerFiltreEleveCombo() {
        filtreEleveCombo.setItems(elevesList);
        filtreEleveCombo.setPromptText("Tous les élèves");
        filtreEleveCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FichePatient item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getPrenom() + " " + item.getNom());
            }
        });
        filtreEleveCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FichePatient item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Tous les élèves" : item.getPrenom() + " " + item.getNom());
            }
        });
    }

    // ── Point 4 : statut combo ────────────────────────────────────────
    private void configurerStatutCombo() {
        statutCombo.setItems(FXCollections.observableArrayList("Terminée", "En cours", "Urgences"));
        statutCombo.setValue("Terminée");
    }

    // ── Point 6 : TableView ───────────────────────────────────────────
    private void configurerTable() {
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdVisite()).asObject());

        colEleve.setCellValueFactory(c -> {
            VisiteInfirmerie v = c.getValue();
            String nom = (v.getPrenomEleve() != null && v.getNomEleve() != null)
                    ? v.getPrenomEleve() + " " + v.getNomEleve()
                    : "Élève #" + v.getIdEleve();
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        colDate.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getDateFormatee()));

        colHeure.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getHeureFormatee()));

        colMotif.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getMotif() != null ? c.getValue().getMotif() : ""));

        colTraitement.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getTraitement() != null ? c.getValue().getTraitement() : ""));

        // ── Point 5 : statut coloré ───────────────────────────────────
        colStatut.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getStatut() != null ? c.getValue().getStatut() : "Terminée"));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setText(null); setStyle(""); return; }
                setText(s);
                String bg = switch (s) {
                    case "En cours"  -> "#fef3c7; -fx-text-fill: #92400e;";
                    case "Urgences"  -> "#fee2e2; -fx-text-fill: #991b1b;";
                    default          -> "#d1fae5; -fx-text-fill: #065f46;";
                };
                setStyle("-fx-background-color: " + bg
                        + " -fx-background-radius: 4; -fx-alignment: CENTER; -fx-font-weight: bold;");
            }
        });

        visitesTable.setItems(visitesList);
        visitesTable.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        visitesTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                visiteSelectionnee = newVal;
                afficherVisiteSelectionnee();
            }
        });
    }

    // ── Chargement ───────────────────────────────────────────────────

    private void loadVisites() {
        try {
            List<VisiteInfirmerie> visites = visiteRepository.getAllVisites();
            visitesList.setAll(visites);
            totalVisitesLabel.setText(String.valueOf(visites.size()));
        } catch (Exception e) {
            afficherMessage("Erreur chargement : " + e.getMessage(), "#e74c3c");
        }
    }

    // ── Actions formulaire ───────────────────────────────────────────

    @FXML
    private void ajouterVisite() {
        if (!validerChamps()) return;

        try {
            LocalTime heure = LocalTime.parse(heureField.getText().trim(),
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

            // ── Point 3 : infirmier auto-assigné ─────────────────────
            Integer idInfirmier = null;
            if (SessionManager.estConnecte()) {
                idInfirmier = SessionManager.getUtilisateurConnecte().getIdUser();
            }

            String traitement = traitementArea.getText().trim().isEmpty() ? null : traitementArea.getText().trim();
            String statut     = statutCombo.getValue() != null ? statutCombo.getValue() : "Terminée";

            VisiteInfirmerie visite = new VisiteInfirmerie(
                    eleveComboBox.getValue().getIdFichePatient(),
                    datePicker.getValue(),
                    heure,
                    motifField.getText().trim(),
                    traitement,
                    statut,
                    idInfirmier
            );

            if (visiteRepository.ajouterVisite(visite)) {
                afficherMessage("Visite ajoutée avec succès !", "#27ae60");
                viderChamps();
                loadVisites();
            } else {
                afficherMessage("Erreur lors de l'ajout de la visite", "#e74c3c");
            }
        } catch (Exception e) {
            afficherMessage("Heure invalide (format HH:mm) : " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    private void modifierVisite() {
        if (visiteSelectionnee == null) {
            afficherMessage("Veuillez sélectionner une visite à modifier", "#e74c3c");
            return;
        }
        if (!validerChamps()) return;

        try {
            LocalTime heure = LocalTime.parse(heureField.getText().trim(),
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

            visiteSelectionnee.setIdEleve(eleveComboBox.getValue().getIdFichePatient());
            visiteSelectionnee.setDateVisite(datePicker.getValue());
            visiteSelectionnee.setHeureVisite(heure);
            visiteSelectionnee.setMotif(motifField.getText().trim());
            visiteSelectionnee.setTraitement(traitementArea.getText().trim().isEmpty() ? null : traitementArea.getText().trim());
            visiteSelectionnee.setStatut(statutCombo.getValue() != null ? statutCombo.getValue() : "Terminée");

            if (visiteRepository.modifierVisite(visiteSelectionnee)) {
                afficherMessage("Visite modifiée avec succès !", "#27ae60");
                viderChamps();
                loadVisites();
            } else {
                afficherMessage("Erreur lors de la modification", "#e74c3c");
            }
        } catch (Exception e) {
            afficherMessage("Heure invalide (format HH:mm) : " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    private void supprimerVisite() {
        if (visiteSelectionnee == null) {
            afficherMessage("Veuillez sélectionner une visite à supprimer", "#e74c3c");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la visite");
        alert.setContentText("Supprimer la visite du "
                + visiteSelectionnee.getDateFormatee() + " à "
                + visiteSelectionnee.getHeureFormatee() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (visiteRepository.supprimerVisite(visiteSelectionnee.getIdVisite())) {
                afficherMessage("Visite supprimée avec succès !", "#27ae60");
                viderChamps();
                loadVisites();
            } else {
                afficherMessage("Erreur lors de la suppression", "#e74c3c");
            }
        }
    }

    @FXML
    private void viderChamps() {
        eleveComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        heureField.clear();
        motifField.clear();
        traitementArea.clear();
        statutCombo.setValue("Terminée");

        visiteSelectionnee = null;
        visitesTable.getSelectionModel().clearSelection();

        ajouterButton.setDisable(false);
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);

        afficherMessage("Formulaire vidé", "#95a5a6");
    }

    // ── Filtres ──────────────────────────────────────────────────────

    @FXML
    private void filtrerParDate() {
        if (filtreDate.getValue() == null) {
            afficherMessage("Veuillez sélectionner une date", "#e74c3c");
            return;
        }
        try {
            List<VisiteInfirmerie> visites = visiteRepository.getVisitesParDate(filtreDate.getValue());
            visitesList.setAll(visites);
            totalVisitesLabel.setText(String.valueOf(visites.size()));
            afficherMessage(visites.size() + " visite(s) le "
                    + filtreDate.getValue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), "#3498db");
        } catch (Exception e) {
            afficherMessage("Erreur filtre : " + e.getMessage(), "#e74c3c");
        }
    }

    // ── Point 4 : filtre par élève ────────────────────────────────────
    @FXML
    private void filtrerParEleve() {
        FichePatient eleve = filtreEleveCombo.getValue();
        if (eleve == null) {
            loadVisites();
            return;
        }
        List<VisiteInfirmerie> visites = visiteRepository.getVisitesParEleve(eleve.getIdFichePatient());
        visitesList.setAll(visites);
        totalVisitesLabel.setText(String.valueOf(visites.size()));
        afficherMessage("Visites de " + eleve.getPrenom() + " " + eleve.getNom()
                + " : " + visites.size(), "#3498db");
    }

    @FXML
    private void toutAfficher() {
        filtreDate.setValue(null);
        filtreEleveCombo.setValue(null);
        loadVisites();
        afficherMessage("Toutes les visites affichées", "#3498db");
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void afficherVisiteSelectionnee() {
        for (FichePatient e : elevesList) {
            if (e.getIdFichePatient() == visiteSelectionnee.getIdEleve()) {
                eleveComboBox.setValue(e);
                break;
            }
        }
        datePicker.setValue(visiteSelectionnee.getDateVisite());
        heureField.setText(visiteSelectionnee.getHeureFormatee());
        motifField.setText(visiteSelectionnee.getMotif() != null ? visiteSelectionnee.getMotif() : "");
        traitementArea.setText(visiteSelectionnee.getTraitement() != null ? visiteSelectionnee.getTraitement() : "");
        statutCombo.setValue(visiteSelectionnee.getStatut() != null ? visiteSelectionnee.getStatut() : "Terminée");

        ajouterButton.setDisable(true);
        modifierButton.setDisable(false);
        supprimerButton.setDisable(false);

        afficherMessage("Visite sélectionnée : "
                + visiteSelectionnee.getDateFormatee() + " " + visiteSelectionnee.getHeureFormatee(), "#3498db");
    }

    private boolean validerChamps() {
        if (eleveComboBox.getValue() == null) {
            afficherMessage("Veuillez sélectionner un élève", "#e74c3c");
            return false;
        }
        if (datePicker.getValue() == null) {
            afficherMessage("Veuillez sélectionner une date", "#e74c3c");
            return false;
        }
        String heure = heureField.getText().trim();
        if (heure.isEmpty()) {
            afficherMessage("L'heure est obligatoire (format HH:mm)", "#e74c3c");
            return false;
        }
        if (!heure.matches("\\d{2}:\\d{2}")) {
            afficherMessage("Format heure invalide (ex: 09:30)", "#e74c3c");
            return false;
        }
        return true;
    }

    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur
                + "; -fx-font-size: 13px; -fx-font-weight: bold; -fx-wrap-text: true;");
    }

    // ── Navigation ───────────────────────────────────────────────────

    @FXML private void versAccueil()    { try { StartApplication.changeScene("pageAccueil"); }          catch (Exception e) { System.err.println(e.getMessage()); } }
    @FXML private void versPatients()   { try { StartApplication.changeScene("patientsView"); }          catch (Exception e) { System.err.println(e.getMessage()); } }
    @FXML private void versCommandes()  { try { NavigationHelper.versCommandes(); }                       catch (Exception e) { System.err.println(e.getMessage()); } }
    @FXML private void versMonEspace()  { try { StartApplication.changeScene("pageMonEspace"); }          catch (Exception e) { System.err.println(e.getMessage()); } }
    @FXML private void versDemandes()   { try { StartApplication.changeScene("pageDemandeProduit"); }     catch (Exception e) { System.err.println(e.getMessage()); } }
    @FXML private void deconnexion()    { try { StartApplication.changeScene("helloView"); }              catch (Exception e) { System.err.println(e.getMessage()); } }
}
