package appli.hsp;

import appli.SessionManager;
import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import modele.FichePatient;
import repository.FichePatientRepository;
import repository.TicketRepository;

import java.util.List;
import java.util.Optional;

public class PatientsController {

    @FXML
    private ListView<FichePatient> patientsListView;

    @FXML
    private Label totalPatientsLabel;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField numEtudiantField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telField;

    @FXML
    private TextField rueField;

    @FXML
    private TextField cpField;

    @FXML
    private TextField villeField;

    @FXML
    private ComboBox<String> candidatureComboBox;

    @FXML
    private Label messageLabel;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML
    private Button viderButton;

    @FXML
    private TextField rechercheField;

    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavUtilisateurs;

    private FichePatientRepository patientRepository;
    private final TicketRepository ticketRepository = new TicketRepository();
    private ObservableList<FichePatient> patientsList;
    private FichePatient eleveSelectionne;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(null, btnNavDossiers, null, null, btnNavCommandes, btnNavPlanning, btnNavCatalogue, null, btnNavUtilisateurs, null);
        patientRepository = new FichePatientRepository();
        patientsList = FXCollections.observableArrayList();

        // Candidature statuses
        candidatureComboBox.setItems(FXCollections.observableArrayList("En cours", "Refusé", "Validé"));
        candidatureComboBox.setPromptText("Statut candidature");

        patientsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FichePatient eleve, boolean empty) {
                super.updateItem(eleve, empty);
                if (empty || eleve == null) {
                    setText("");
                } else {
                    setText(String.format("ID: %d | %s %s | %s | Candidature: %s",
                        eleve.getIdFichePatient(),
                        eleve.getNom(),
                        eleve.getPrenom(),
                        eleve.getEmail(),
                        eleve.getCandidatureLibelle()));
                }
            }
        });

        patientsListView.setItems(patientsList);

        patientsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    eleveSelectionne = newSelection;
                    afficherEleveSelectionne();
                }
            });

        Platform.runLater(this::loadPatients);
    }

    private void loadPatients() {
        try {
            List<FichePatient> eleves = patientRepository.getAllFichePatients();
            patientsList.clear();
            patientsList.addAll(eleves);
            totalPatientsLabel.setText(String.valueOf(eleves.size()));
            patientsListView.refresh();
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            totalPatientsLabel.setText("Erreur");
        }
    }

    @FXML
    private void handleRefresh() {
        loadPatients();
    }

    @FXML
    private void handleBack() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void envoyerInfirmerie() {
        if (eleveSelectionne == null) {
            afficherMessage("Sélectionnez d'abord un élève dans la liste.", "#e74c3c");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Envoyer à l'infirmerie");
        dialog.setHeaderText(eleveSelectionne.getPrenom() + " " + eleveSelectionne.getNom());
        dialog.setContentText("Motif :");
        dialog.showAndWait().ifPresent(motif -> {
            if (motif.trim().isEmpty()) return;
            int idSecretaire = SessionManager.estConnecte()
                    ? SessionManager.getUtilisateurConnecte().getIdUser() : 1;
            int id = ticketRepository.creerTicket(eleveSelectionne.getIdFichePatient(), idSecretaire, motif.trim());
            if (id > 0) {
                afficherMessage("Ticket #" + id + " créé — élève envoyé à l'infirmerie.", "#27ae60");
            } else {
                afficherMessage("Erreur lors de la création du ticket.", "#e74c3c");
            }
        });
    }

    @FXML
    private void versTickets() {
        try { StartApplication.changeScene("pageTickets"); } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    @FXML
    private void versAccueil() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versDossiers() {
        try {
            StartApplication.changeScene("dossierEnChargeView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versCommandes() {
        try {
            NavigationHelper.versCommandes();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers commandes: " + e.getMessage());
        }
    }

    @FXML
    private void versPlanning() {
        try {
            StartApplication.changeScene("planningView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versFicheProduit() {
        try {
            StartApplication.changeScene("ficheProduitView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versUtilisateurs() {
        try {
            StartApplication.changeScene("pageUtilisateurs");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versMonEspace() {
        try {
            StartApplication.changeScene("pageMonEspace");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void deconnexion() {
        try {
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private void afficherEleveSelectionne() {
        if (eleveSelectionne != null) {
            nomField.setText(eleveSelectionne.getNom());
            prenomField.setText(eleveSelectionne.getPrenom());
            numEtudiantField.setText(eleveSelectionne.getNum_etudiant());
            emailField.setText(eleveSelectionne.getEmail());
            telField.setText(eleveSelectionne.getTel());
            rueField.setText(eleveSelectionne.getRue());
            cpField.setText(String.valueOf(eleveSelectionne.getCp()));
            villeField.setText(eleveSelectionne.getVille());
            candidatureComboBox.setValue(eleveSelectionne.getCandidatureLibelle());

            modifierButton.setDisable(false);
            supprimerButton.setDisable(false);
            ajouterButton.setDisable(true);

            afficherMessage("Élève sélectionné: " + eleveSelectionne.getNom() + " " + eleveSelectionne.getPrenom(), "#3498db");
        }
    }

    @FXML
    private void ajouterPatient() {
        if (!validerChamps()) return;

        try {
            FichePatient nouvelEleve = new FichePatient(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                numEtudiantField.getText().trim(),
                emailField.getText().trim(),
                telField.getText().trim(),
                rueField.getText().trim(),
                Integer.parseInt(cpField.getText().trim()),
                villeField.getText().trim(),
                candidatureToInt(candidatureComboBox.getValue())
            );

            if (patientRepository.ajouterFichePatient(nouvelEleve)) {
                afficherMessage("Élève ajouté avec succès!", "#27ae60");
                viderChamps();
                loadPatients();
            } else {
                afficherMessage("Erreur lors de l'ajout de l'élève", "#e74c3c");
            }
        } catch (NumberFormatException e) {
            afficherMessage("Veuillez vérifier les champs numériques", "#e74c3c");
        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    private void modifierPatient() {
        if (eleveSelectionne == null) {
            afficherMessage("Veuillez sélectionner un élève à modifier", "#e74c3c");
            return;
        }
        if (!validerChamps()) return;

        try {
            System.out.println("[DEBUG] Modification élève ID: " + eleveSelectionne.getIdFichePatient());
            eleveSelectionne.setNom(nomField.getText().trim());
            eleveSelectionne.setPrenom(prenomField.getText().trim());
            eleveSelectionne.setNum_etudiant(numEtudiantField.getText().trim());
            eleveSelectionne.setEmail(emailField.getText().trim());
            eleveSelectionne.setTel(telField.getText().trim());
            eleveSelectionne.setRue(rueField.getText().trim());
            eleveSelectionne.setCp(Integer.parseInt(cpField.getText().trim()));
            eleveSelectionne.setVille(villeField.getText().trim());
            eleveSelectionne.setCandidature(candidatureToInt(candidatureComboBox.getValue()));

            if (patientRepository.modifierFichePatient(eleveSelectionne)) {
                afficherMessage("Élève modifié avec succès!", "#27ae60");
                viderChamps();
                loadPatients();
            } else {
                afficherMessage("Erreur lors de la modification de l'élève", "#e74c3c");
            }
        } catch (NumberFormatException e) {
            afficherMessage("Veuillez vérifier les champs numériques", "#e74c3c");
        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    private void supprimerPatient() {
        if (eleveSelectionne == null) {
            afficherMessage("Veuillez sélectionner un élève à supprimer", "#e74c3c");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'élève");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'élève " +
            eleveSelectionne.getNom() + " " + eleveSelectionne.getPrenom() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (patientRepository.supprimerFichePatient(eleveSelectionne.getIdFichePatient())) {
                afficherMessage("Élève supprimé avec succès!", "#27ae60");
                viderChamps();
                loadPatients();
            } else {
                afficherMessage("Erreur lors de la suppression de l'élève", "#e74c3c");
            }
        }
    }

    @FXML
    private void viderChamps() {
        nomField.clear();
        prenomField.clear();
        numEtudiantField.clear();
        emailField.clear();
        telField.clear();
        rueField.clear();
        cpField.clear();
        villeField.clear();
        candidatureComboBox.setValue(null);
        rechercheField.clear();

        eleveSelectionne = null;
        patientsListView.getSelectionModel().clearSelection();

        ajouterButton.setDisable(false);
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);

        afficherMessage("Formulaire vidé", "#95a5a6");
    }

    @FXML
    private void rechercherPatients() {
        String recherche = rechercheField.getText().trim().toLowerCase();

        if (recherche.isEmpty()) {
            patientsListView.setItems(patientsList);
            return;
        }

        ObservableList<FichePatient> elevesFiltres = FXCollections.observableArrayList();

        for (FichePatient eleve : patientsList) {
            if (eleve.getNom().toLowerCase().contains(recherche) ||
                eleve.getPrenom().toLowerCase().contains(recherche) ||
                eleve.getEmail().toLowerCase().contains(recherche) ||
                eleve.getVille().toLowerCase().contains(recherche)) {
                elevesFiltres.add(eleve);
            }
        }

        patientsListView.setItems(elevesFiltres);
        afficherMessage(elevesFiltres.size() + " élève(s) trouvé(s)", "#3498db");
    }

    private boolean validerChamps() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String tel = telField.getText().trim();
        String cp = cpField.getText().trim();

        if (nom.isEmpty()) { afficherMessage("Le nom est obligatoire", "#e74c3c"); return false; }
        if (prenom.isEmpty()) { afficherMessage("Le prénom est obligatoire", "#e74c3c"); return false; }
        if (email.isEmpty()) { afficherMessage("L'email est obligatoire", "#e74c3c"); return false; }
        if (!email.contains("@") || !email.contains(".")) { afficherMessage("L'email n'est pas valide", "#e74c3c"); return false; }
        if (tel.isEmpty()) { afficherMessage("Le téléphone est obligatoire", "#e74c3c"); return false; }
        if (cp.isEmpty()) { afficherMessage("Le code postal est obligatoire", "#e74c3c"); return false; }

        try {
            Integer.parseInt(cp);
        } catch (NumberFormatException e) {
            afficherMessage("Le code postal doit être numérique", "#e74c3c");
            return false;
        }
        return true;
    }

    private Integer candidatureToInt(String libelle) {
        if (libelle == null || libelle.equals("En cours")) return null;
        if (libelle.equals("Refusé")) return 0;
        if (libelle.equals("Validé")) return 1;
        return null;
    }

    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-size: 14px; -fx-wrap-text: true; -fx-font-weight: bold;");
    }
}
