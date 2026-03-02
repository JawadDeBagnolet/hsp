package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.FichePatient;
import repository.FichePatientRepository;

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
    private TextField numSecuField;
    
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
    
    private FichePatientRepository patientRepository;
    private ObservableList<FichePatient> patientsList;
    private FichePatient patientSelectionne;

    @FXML
    public void initialize() {
        patientRepository = new FichePatientRepository();
        patientsList = FXCollections.observableArrayList();
        
        System.out.println("Initialisation du controller avec ListView...");
        
        // Configuration de la ListView pour afficher les patients
        patientsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FichePatient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setText("");
                } else {
                    setText(String.format("ID: %d | %s %s | %s", 
                        patient.getIdFichePatient(), 
                        patient.getNom(), 
                        patient.getPrenom(), 
                        patient.getEmail()));
                }
            }
        });
        
        patientsListView.setItems(patientsList);
        
        // Configuration de la sélection
        patientsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    patientSelectionne = newSelection;
                    afficherPatientSelectionne();
                }
            });
        
        // Charger les données après un délai pour s'assurer que l'interface est prête
        Platform.runLater(this::loadPatients);
    }

    private void loadPatients() {
        try {
            List<FichePatient> patients = patientRepository.getAllFichePatients();
            patientsList.clear();
            patientsList.addAll(patients);
            
            System.out.println("Controller - Patients reçus: " + patients.size());
            for (FichePatient patient : patients) {
                System.out.println("Controller - Patient: " + patient.getIdFichePatient() + 
                                 ", " + patient.getNom() + 
                                 ", " + patient.getPrenom() + 
                                 ", " + patient.getEmail());
            }
            
            // Mettre à jour le compteur
            totalPatientsLabel.setText(String.valueOf(patients.size()));
            
            // Forcer le rafraîchissement
            patientsListView.refresh();
            Platform.runLater(() -> {
                patientsListView.refresh();
                System.out.println("ListView refresh exécuté - Items: " + patientsListView.getItems().size());
            });
            
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
    private void versAccueil() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versCommandes() {
        try {
            StartApplication.changeScene("commandeView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versUtilisateurs() {
        try {
            StartApplication.changeScene("pageMonEspace");
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
    
    private void afficherPatientSelectionne() {
        if (patientSelectionne != null) {
            nomField.setText(patientSelectionne.getNom());
            prenomField.setText(patientSelectionne.getPrenom());
            numSecuField.setText(String.valueOf(patientSelectionne.getNum_secu()));
            emailField.setText(patientSelectionne.getEmail());
            telField.setText(String.valueOf(patientSelectionne.getTel()));
            rueField.setText(patientSelectionne.getRue());
            cpField.setText(String.valueOf(patientSelectionne.getCp()));
            villeField.setText(patientSelectionne.getVille());
            
            // Activer les boutons de modification
            modifierButton.setDisable(false);
            supprimerButton.setDisable(false);
            ajouterButton.setDisable(true);
            
            afficherMessage("Patient sélectionné: " + patientSelectionne.getNom() + " " + patientSelectionne.getPrenom(), "#3498db");
        }
    }
    
    @FXML
    private void ajouterPatient() {
        if (!validerChamps()) {
            return;
        }
        
        try {
            FichePatient nouveauPatient = new FichePatient(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                Long.parseLong(numSecuField.getText().trim()),
                emailField.getText().trim(),
                Integer.parseInt(telField.getText().trim()),
                rueField.getText().trim(),
                Integer.parseInt(cpField.getText().trim()),
                villeField.getText().trim()
            );
            
            if (patientRepository.ajouterFichePatient(nouveauPatient)) {
                afficherMessage("Patient ajouté avec succès!", "#27ae60");
                viderChamps();
                loadPatients();
            } else {
                afficherMessage("Erreur lors de l'ajout du patient", "#e74c3c");
            }
        } catch (NumberFormatException e) {
            afficherMessage("Veuillez vérifier les champs numériques", "#e74c3c");
        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
        }
    }
    
    @FXML
    private void modifierPatient() {
        if (patientSelectionne == null) {
            afficherMessage("Veuillez sélectionner un patient à modifier", "#e74c3c");
            return;
        }
        
        if (!validerChamps()) {
            return;
        }
        
        try {
            // Mettre à jour les informations du patient sélectionné
            patientSelectionne.setNom(nomField.getText().trim());
            patientSelectionne.setPrenom(prenomField.getText().trim());
            patientSelectionne.setNum_secu(Long.parseLong(numSecuField.getText().trim()));
            patientSelectionne.setEmail(emailField.getText().trim());
            patientSelectionne.setTel(Integer.parseInt(telField.getText().trim()));
            patientSelectionne.setRue(rueField.getText().trim());
            patientSelectionne.setCp(Integer.parseInt(cpField.getText().trim()));
            patientSelectionne.setVille(villeField.getText().trim());
            
            if (patientRepository.modifierFichePatient(patientSelectionne)) {
                afficherMessage("Patient modifié avec succès!", "#27ae60");
                viderChamps();
                loadPatients();
            } else {
                afficherMessage("Erreur lors de la modification du patient", "#e74c3c");
            }
        } catch (NumberFormatException e) {
            afficherMessage("Veuillez vérifier les champs numériques", "#e74c3c");
        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
        }
    }
    
    @FXML
    private void supprimerPatient() {
        if (patientSelectionne == null) {
            afficherMessage("Veuillez sélectionner un patient à supprimer", "#e74c3c");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le patient");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le patient " + 
            patientSelectionne.getNom() + " " + patientSelectionne.getPrenom() + " ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (patientRepository.supprimerFichePatient(patientSelectionne.getIdFichePatient())) {
                afficherMessage("Patient supprimé avec succès!", "#27ae60");
                viderChamps();
                loadPatients();
            } else {
                afficherMessage("Erreur lors de la suppression du patient", "#e74c3c");
            }
        }
    }
    
    @FXML
    private void viderChamps() {
        nomField.clear();
        prenomField.clear();
        numSecuField.clear();
        emailField.clear();
        telField.clear();
        rueField.clear();
        cpField.clear();
        villeField.clear();
        rechercheField.clear();
        
        patientSelectionne = null;
        patientsListView.getSelectionModel().clearSelection();
        
        // Réactiver/désactiver les boutons
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
        
        ObservableList<FichePatient> patientsFiltres = FXCollections.observableArrayList();
        
        for (FichePatient patient : patientsList) {
            if (patient.getNom().toLowerCase().contains(recherche) ||
                patient.getPrenom().toLowerCase().contains(recherche) ||
                patient.getEmail().toLowerCase().contains(recherche) ||
                patient.getVille().toLowerCase().contains(recherche)) {
                patientsFiltres.add(patient);
            }
        }
        
        patientsListView.setItems(patientsFiltres);
        afficherMessage(patientsFiltres.size() + " patient(s) trouvé(s)", "#3498db");
    }
    
    private boolean validerChamps() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String numSecu = numSecuField.getText().trim();
        String email = emailField.getText().trim();
        String tel = telField.getText().trim();
        String cp = cpField.getText().trim();
        
        if (nom.isEmpty()) {
            afficherMessage("Le nom est obligatoire", "#e74c3c");
            return false;
        }
        
        if (prenom.isEmpty()) {
            afficherMessage("Le prénom est obligatoire", "#e74c3c");
            return false;
        }
        
        if (numSecu.isEmpty()) {
            afficherMessage("Le numéro de sécurité sociale est obligatoire", "#e74c3c");
            return false;
        }
        
        if (email.isEmpty()) {
            afficherMessage("L'email est obligatoire", "#e74c3c");
            return false;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            afficherMessage("L'email n'est pas valide", "#e74c3c");
            return false;
        }
        
        if (tel.isEmpty()) {
            afficherMessage("Le téléphone est obligatoire", "#e74c3c");
            return false;
        }
        
        if (cp.isEmpty()) {
            afficherMessage("Le code postal est obligatoire", "#e74c3c");
            return false;
        }
        
        try {
            Long.parseLong(numSecu);
            Integer.parseInt(tel);
            Integer.parseInt(cp);
        } catch (NumberFormatException e) {
            afficherMessage("Les champs numériques ne sont pas valides", "#e74c3c");
            return false;
        }
        
        return true;
    }
    
    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-size: 14px; -fx-wrap-text: true; -fx-font-weight: bold;");
    }
}
