package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import modele.FichePatient;
import repository.FichePatientRepository;

import java.util.List;

public class PatientsController {

    @FXML
    private ListView<FichePatient> patientsListView;
    
    @FXML
    private Label totalPatientsLabel;
    
    @FXML
    private VBox modificationPane;
    
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
    
    private FichePatientRepository patientRepository;
    private ObservableList<FichePatient> patientsList;
    private FichePatient selectedPatient;

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
                    setStyle("-fx-background-color: transparent; -fx-pref-height: 50;");
                } else {
                    setText(String.format("ID: %d | %s %s | %s", 
                        patient.getIdFichePatient(), 
                        patient.getNom(), 
                        patient.getPrenom(), 
                        patient.getEmail()));
                    
                    // Style différent si sélectionné
                    if (isSelected()) {
                        setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-border-color: #2980b9; -fx-border-width: 0 0 1 0; -fx-padding: 15; -fx-font-size: 14px; -fx-pref-height: 50;");
                    } else {
                        setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-padding: 15; -fx-font-size: 14px; -fx-pref-height: 50;");
                    }
                }
            }
        });
        
        patientsListView.setItems(patientsList);
        
        // Ajouter un listener de sélection pour éviter les problèmes au clic
        patientsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Patient sélectionné: " + (newVal != null ? newVal.getNom() : "aucun"));
        });
        
        // Charger les données après un délai pour s'assurer que l'interface est prête
        Platform.runLater(this::loadPatients);
    }

    private void loadPatients() {
        try {
            System.out.println("Début du chargement des patients...");
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
            
            // Forcer le rafraîchissement une seule fois
            Platform.runLater(() -> {
                patientsListView.refresh();
                System.out.println("ListView refresh exécuté - Items: " + patientsListView.getItems().size());
                System.out.println("ListView visible: " + patientsListView.isVisible());
                System.out.println("ListView height: " + patientsListView.getHeight());
            });
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des patients: " + e.getMessage());
            e.printStackTrace();
            totalPatientsLabel.setText("Erreur");
        }
    }

    @FXML
    private void handleRefresh() {
        loadPatients();
    }

    @FXML
    private void handleModifierPatient() {
        FichePatient selected = patientsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedPatient = selected;
            afficherFormulaireModification(selected);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText("Veuillez sélectionner un patient à modifier");
            alert.setContentText("Cliquez sur un patient dans la liste puis sur le bouton Modifier.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSaveModification() {
        try {
            // Valider les champs
            if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || 
                emailField.getText().isEmpty() || numSecuField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de validation");
                alert.setHeaderText("Champs obligatoires manquants");
                alert.setContentText("Veuillez remplir au moins le nom, prénom, email et numéro de sécurité sociale.");
                alert.showAndWait();
                return;
            }

            // Mettre à jour le patient
            selectedPatient.setNom(nomField.getText());
            selectedPatient.setPrenom(prenomField.getText());
            selectedPatient.setEmail(emailField.getText());
            
            try {
                selectedPatient.setNum_secu(Long.parseLong(numSecuField.getText()));
                selectedPatient.setTel(telField.getText().isEmpty() ? 0 : Integer.parseInt(telField.getText()));
                selectedPatient.setCp(cpField.getText().isEmpty() ? 0 : Integer.parseInt(cpField.getText()));
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de format");
                alert.setHeaderText("Format numérique incorrect");
                alert.setContentText("Veuillez vérifier les formats des champs numériques.");
                alert.showAndWait();
                return;
            }
            
            selectedPatient.setRue(rueField.getText());
            selectedPatient.setVille(villeField.getText());

            // Sauvegarder dans la base de données
            boolean success = patientRepository.modifierFichePatient(selectedPatient);
            
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Patient modifié avec succès");
                alert.setContentText("Les informations du patient ont été mises à jour.");
                alert.showAndWait();
                
                // Masquer le formulaire et recharger les données
                modificationPane.setVisible(false);
                loadPatients();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Échec de la modification");
                alert.setContentText("Une erreur est survenue lors de la mise à jour du patient.");
                alert.showAndWait();
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur inattendue");
            alert.setContentText("Une erreur est survenue: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelModification() {
        modificationPane.setVisible(false);
        selectedPatient = null;
    }

    private void afficherFormulaireModification(FichePatient patient) {
        // Remplir les champs avec les données du patient
        nomField.setText(patient.getNom());
        prenomField.setText(patient.getPrenom());
        numSecuField.setText(String.valueOf(patient.getNum_secu()));
        emailField.setText(patient.getEmail());
        telField.setText(patient.getTel() != 0 ? String.valueOf(patient.getTel()) : "");
        rueField.setText(patient.getRue());
        cpField.setText(patient.getCp() != 0 ? String.valueOf(patient.getCp()) : "");
        villeField.setText(patient.getVille());
        
        // Afficher le panneau de modification
        modificationPane.setVisible(true);
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
    private void versPatients() {
        // Déjà sur la page patients
        System.out.println("Déjà sur la page patients");
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
}
