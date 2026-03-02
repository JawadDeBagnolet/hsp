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

public class PatientsController {

    @FXML
    private ListView<FichePatient> patientsListView;
    
    @FXML
    private Label totalPatientsLabel;
    
    private FichePatientRepository patientRepository;
    private ObservableList<FichePatient> patientsList;

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
    public void versPatients() {
        // Déjà sur la page patients
        System.out.println("Déjà sur la page patients");
    }

    @FXML
    public void versAccueil() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void versDossiers() {
        try {
            StartApplication.changeScene("dossierEnChargeView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void versCommandes() {
        try {
            StartApplication.changeScene("commandeView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void versUtilisateurs() {
        try {
            StartApplication.changeScene("pageUtilisateurs");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void versMonEspace() {
        try {
            StartApplication.changeScene("pageMonEspace");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void deconnexion() {
        try {
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
}
