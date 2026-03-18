package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.FichePatient;
import modele.VisiteInfirmerie;
import repository.FichePatientRepository;
import repository.VisiteInfirmerieRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class VisiteInfirmerieController {

    @FXML private ListView<VisiteInfirmerie> visitesListView;
    @FXML private Label totalVisitesLabel;
    @FXML private Label messageLabel;

    @FXML private ComboBox<FichePatient> eleveComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField motifField;

    @FXML private Button ajouterButton;
    @FXML private Button modifierButton;
    @FXML private Button supprimerButton;
    @FXML private Button viderButton;

    @FXML private DatePicker filtreDate;
    @FXML private Button filtrerButton;
    @FXML private Button toutAfficherButton;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavDemandes;

    private VisiteInfirmerieRepository visiteRepository;
    private FichePatientRepository eleveRepository;
    private ObservableList<VisiteInfirmerie> visitesList;
    private ObservableList<FichePatient> elevesList;
    private VisiteInfirmerie visiteSelectionnee;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, null, null, null, btnNavCommandes, null, null, null, null, btnNavDemandes);
        visiteRepository = new VisiteInfirmerieRepository();
        eleveRepository = new FichePatientRepository();
        visitesList = FXCollections.observableArrayList();
        elevesList = FXCollections.observableArrayList();

        configurerEleveComboBox();
        configurerListView();

        datePicker.setValue(LocalDate.now());

        Platform.runLater(this::loadVisites);
    }

    private void configurerEleveComboBox() {
        List<FichePatient> eleves = eleveRepository.getAllFichePatients();
        elevesList.addAll(eleves);
        eleveComboBox.setItems(elevesList);

        eleveComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FichePatient item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getPrenom() + " " + item.getNom() + " (" + item.getNum_etudiant() + ")");
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

    private void configurerListView() {
        visitesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(VisiteInfirmerie visite, boolean empty) {
                super.updateItem(visite, empty);
                setText(empty || visite == null ? "" : visite.toString());
            }
        });
        visitesListView.setItems(visitesList);

        visitesListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newVal) -> {
                if (newVal != null) {
                    visiteSelectionnee = newVal;
                    afficherVisiteSelectionnee();
                }
            });
    }

    private void loadVisites() {
        try {
            List<VisiteInfirmerie> visites = visiteRepository.getAllVisites();
            visitesList.clear();
            visitesList.addAll(visites);
            totalVisitesLabel.setText(String.valueOf(visites.size()));
        } catch (Exception e) {
            afficherMessage("Erreur chargement: " + e.getMessage(), "#e74c3c");
        }
    }

    private void afficherVisiteSelectionnee() {
        if (visiteSelectionnee == null) return;

        // Sélectionner l'élève dans le combo
        for (FichePatient e : elevesList) {
            if (e.getIdFichePatient() == visiteSelectionnee.getIdEleve()) {
                eleveComboBox.setValue(e);
                break;
            }
        }

        datePicker.setValue(visiteSelectionnee.getDateVisite());
        heureField.setText(visiteSelectionnee.getHeureFormatee());
        motifField.setText(visiteSelectionnee.getMotif() != null ? visiteSelectionnee.getMotif() : "");

        ajouterButton.setDisable(true);
        modifierButton.setDisable(false);
        supprimerButton.setDisable(false);

        afficherMessage("Visite sélectionnée: " + visiteSelectionnee.getDateFormatee() + " " + visiteSelectionnee.getHeureFormatee(), "#3498db");
    }

    @FXML
    private void ajouterVisite() {
        if (!validerChamps()) return;

        try {
            LocalTime heure = LocalTime.parse(heureField.getText().trim(),
                java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

            VisiteInfirmerie visite = new VisiteInfirmerie(
                eleveComboBox.getValue().getIdFichePatient(),
                datePicker.getValue(),
                heure,
                motifField.getText().trim(),
                null
            );

            if (visiteRepository.ajouterVisite(visite)) {
                afficherMessage("Visite ajoutée avec succès!", "#27ae60");
                viderChamps();
                loadVisites();
            } else {
                afficherMessage("Erreur lors de l'ajout de la visite", "#e74c3c");
            }
        } catch (Exception e) {
            afficherMessage("Heure invalide (format HH:mm requis): " + e.getMessage(), "#e74c3c");
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

            if (visiteRepository.modifierVisite(visiteSelectionnee)) {
                afficherMessage("Visite modifiée avec succès!", "#27ae60");
                viderChamps();
                loadVisites();
            } else {
                afficherMessage("Erreur lors de la modification de la visite", "#e74c3c");
            }
        } catch (Exception e) {
            afficherMessage("Heure invalide (format HH:mm requis): " + e.getMessage(), "#e74c3c");
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
        alert.setContentText("Supprimer la visite du " + visiteSelectionnee.getDateFormatee() + " à " + visiteSelectionnee.getHeureFormatee() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (visiteRepository.supprimerVisite(visiteSelectionnee.getIdVisite())) {
                afficherMessage("Visite supprimée avec succès!", "#27ae60");
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

        visiteSelectionnee = null;
        visitesListView.getSelectionModel().clearSelection();

        ajouterButton.setDisable(false);
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);

        afficherMessage("Formulaire vidé", "#95a5a6");
    }

    @FXML
    private void filtrerParDate() {
        if (filtreDate.getValue() == null) {
            afficherMessage("Veuillez sélectionner une date de filtre", "#e74c3c");
            return;
        }
        try {
            List<VisiteInfirmerie> visites = visiteRepository.getVisitesParDate(filtreDate.getValue());
            visitesList.clear();
            visitesList.addAll(visites);
            totalVisitesLabel.setText(String.valueOf(visites.size()));
            afficherMessage(visites.size() + " visite(s) le " + filtreDate.getValue().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), "#3498db");
        } catch (Exception e) {
            afficherMessage("Erreur filtre: " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    private void toutAfficher() {
        filtreDate.setValue(null);
        loadVisites();
        afficherMessage("Toutes les visites affichées", "#3498db");
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
            afficherMessage("Format heure invalide (utilisez HH:mm, ex: 09:30)", "#e74c3c");
            return false;
        }
        return true;
    }

    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-size: 14px; -fx-wrap-text: true; -fx-font-weight: bold;");
    }

    // Navigation
    @FXML private void versAccueil() {
        try { StartApplication.changeScene("pageAccueil"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versPatients() {
        try { StartApplication.changeScene("patientsView"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versCommandes() {
        try {
            NavigationHelper.versCommandes();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers commandes: " + e.getMessage());
        }
    }
    @FXML private void versMonEspace() {
        try { StartApplication.changeScene("pageMonEspace"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void versDemandes() {
        try { StartApplication.changeScene("pageDemandeProduit"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
    @FXML private void deconnexion() {
        try { StartApplication.changeScene("helloView"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }
}
