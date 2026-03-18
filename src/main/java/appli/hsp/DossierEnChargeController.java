package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import appli.SessionManager;
import appli.hsp.exception.ErrorCode;
import appli.hsp.exception.LPRSException;
import appli.hsp.utils.ErrorHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.DossierEnCharge;
import modele.FichePatient;
import repository.DossierEnChargeRepository;
import repository.FichePatientRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class DossierEnChargeController {

    @FXML
    private DatePicker dateArriveeField;

    @FXML
    private ComboBox<String> heureCombo;

    @FXML
    private ComboBox<String> minuteCombo;

    @FXML
    private TextArea symptomesField;

    @FXML
    private ComboBox<String> niveauGraviteCombo;

    @FXML
    private ComboBox<FichePatient> patientCombo;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<DossierEnCharge> dossiersListView;

    @FXML
    private Button nouveauButton;

    @FXML
    private Button modifierButton;

    @FXML
    private Button supprimerButton;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavUtilisateurs;
    @FXML private Button btnNavDemandes;

    private DossierEnChargeRepository dossierRepository;
    private FichePatientRepository patientRepository;
    private DossierEnCharge dossierEnEdition = null;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, null, null, null, btnNavCommandes, btnNavPlanning, btnNavCatalogue, null, btnNavUtilisateurs, btnNavDemandes);
        try {
            dossierRepository = new DossierEnChargeRepository();
            patientRepository = new FichePatientRepository();
            
            initialiserComposants();
            chargerListeDossiers();
            configurerSelectionDossier();
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.SYSTEM_ERROR, "Erreur lors de l'initialisation du contrôleur", e),
                "Initialisation DossierEnChargeController"
            );
        }
    }
    
    private void initialiserComposants() {
        // Initialiser la date avec la date du jour
        dateArriveeField.setValue(LocalDate.now());
        
        // Initialiser le ComboBox des heures (00-23)
        heureCombo.getItems().clear();
        for (int i = 0; i < 24; i++) {
            heureCombo.getItems().add(String.format("%02d", i));
        }
        
        // Initialiser le ComboBox des minutes (00-55 par pas de 5)
        minuteCombo.getItems().clear();
        for (int i = 0; i < 60; i += 5) {
            minuteCombo.getItems().add(String.format("%02d", i));
        }
        
        // Initialiser le ComboBox de niveau de gravité
        niveauGraviteCombo.getItems().addAll(
            "1 - Urgence vitale",
            "2 - Urgence relative", 
            "3 - Urgence différée",
            "4 - Non urgent"
        );
        
        // Initialiser le ComboBox des patients
        try {
            java.util.List<FichePatient> patients = patientRepository.getAllFichePatients();
            for (FichePatient patient : patients) {
                patientCombo.getItems().add(patient);
            }
            
            // Personnaliser l'affichage des patients
            patientCombo.setCellFactory(param -> new javafx.scene.control.ListCell<FichePatient>() {
                @Override
                protected void updateItem(FichePatient patient, boolean empty) {
                    super.updateItem(patient, empty);
                    if (empty || patient == null) {
                        setText(null);
                    } else {
                        setText(patient.getPrenom() + " " + patient.getNom() + " (ID: " + patient.getIdFichePatient() + ")");
                    }
                }
            });
            
            patientCombo.setButtonCell(new javafx.scene.control.ListCell<FichePatient>() {
                @Override
                protected void updateItem(FichePatient patient, boolean empty) {
                    super.updateItem(patient, empty);
                    if (empty || patient == null) {
                        setText(null);
                    } else {
                        setText(patient.getPrenom() + " " + patient.getNom());
                    }
                }
            });
            
            // Sélectionner le premier patient par défaut
            if (!patientCombo.getItems().isEmpty()) {
                patientCombo.setValue(patientCombo.getItems().get(0));
            }
            
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors du chargement des patients", e),
                "Chargement patients"
            );
        }
        
        // Valeurs par défaut
        LocalTime maintenant = LocalTime.now();
        heureCombo.setValue(String.format("%02d", maintenant.getHour()));
        minuteCombo.setValue(String.format("%02d", (maintenant.getMinute() / 5) * 5));
        niveauGraviteCombo.setValue("4 - Non urgent");
        
        messageLabel.setText("");
        
        // Personnaliser l'affichage des dossiers
        dossiersListView.setCellFactory(param -> new javafx.scene.control.ListCell<DossierEnCharge>() {
            @Override
            protected void updateItem(DossierEnCharge dossier, boolean empty) {
                super.updateItem(dossier, empty);
                if (empty || dossier == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String texte = String.format("📅 %s ⏰ %s - 🏥 %s (Niveau: %d)",
                        dossier.getDateArrivee(),
                        dossier.getHeureArrivee(),
                        dossier.getSymptomes().length() > 30 ? 
                            dossier.getSymptomes().substring(0, 30) + "..." : 
                            dossier.getSymptomes(),
                        dossier.getNiveauGravite());
                    setText(texte);
                    setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-padding: 8;");
                }
            }
        });
        
        // État initial des boutons
        mettreAJourBoutonsEdition(false);
    }
    
    private void configurerSelectionDossier() {
        dossiersListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                boolean dossierSelectionne = newValue != null;
                mettreAJourBoutonsEdition(dossierSelectionne);
                
                if (dossierSelectionne && dossierEnEdition == null) {
                    // Charger le dossier sélectionné dans le formulaire (mode lecture seule)
                    chargerDossierDansFormulaire(newValue, false);
                }
            });
    }
    
    private void mettreAJourBoutonsEdition(boolean editionActive) {
        modifierButton.setDisable(!editionActive);
        supprimerButton.setDisable(!editionActive);
        nouveauButton.setDisable(editionActive);
    }
    
        
    private void chargerDossierDansFormulaire(DossierEnCharge dossier, boolean modeEdition) {
        dossierEnEdition = modeEdition ? dossier : null;
        
        dateArriveeField.setValue(dossier.getDateArrivee());
        
        // Extraire heure et minute
        String[] heureParts = dossier.getHeureArrivee().toString().split(":");
        heureCombo.setValue(heureParts[0]);
        minuteCombo.setValue(heureParts[1]);
        
        symptomesField.setText(dossier.getSymptomes());
        niveauGraviteCombo.setValue(dossier.getNiveauGravite() + " - " + getLabelGravite(dossier.getNiveauGravite()));
        
        // Sélectionner le patient correspondant
        for (FichePatient patient : patientCombo.getItems()) {
            if (patient.getIdFichePatient() == dossier.getIdEleve()) {
                patientCombo.setValue(patient);
                break;
            }
        }
        
        // Activer/désactiver les champs selon le mode
        boolean champsActifs = modeEdition;
        dateArriveeField.setDisable(!champsActifs);
        heureCombo.setDisable(!champsActifs);
        minuteCombo.setDisable(!champsActifs);
        symptomesField.setDisable(!champsActifs);
        niveauGraviteCombo.setDisable(!champsActifs);
        patientCombo.setDisable(!champsActifs);
    }
    
    private String getLabelGravite(int niveau) {
        switch (niveau) {
            case 1: return "Urgence vitale";
            case 2: return "Urgence relative";
            case 3: return "Urgence différée";
            case 4: return "Non urgent";
            default: return "Inconnu";
        }
    }

    @FXML
    private void handleCreerDossier() {
        try {
            if (!validerFormulaire()) {
                return;
            }
            
            DossierEnCharge nouveauDossier = creerDossierFromFormulaire();
            
            boolean succes = dossierRepository.ajouterDossier(nouveauDossier);
            
            if (succes) {
                afficherMessage("Dossier de prise en charge créé avec succès!", "success");
                viderChamps();
                chargerListeDossiers();
            } else {
                throw new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Échec de la création du dossier");
            }
            
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors de la création du dossier", e),
                "Création dossier"
            );
        }
    }

    @FXML
    private void handleViderChamps() {
        viderChamps();
        afficherMessage("Champs vidés", "info");
    }

    @FXML
    private void handleModifierDossier() {
        try {
            DossierEnCharge dossierSelectionne = dossiersListView.getSelectionModel().getSelectedItem();
            if (dossierSelectionne == null) {
                afficherMessage("Veuillez sélectionner un dossier à modifier", "error");
                return;
            }
            
            if (dossierEnEdition == null) {
                // Passer en mode édition
                chargerDossierDansFormulaire(dossierSelectionne, true);
                afficherMessage("Mode édition activé", "info");
            } else {
                // Sauvegarder les modifications
                if (!validerFormulaire()) {
                    return;
                }
                
                DossierEnCharge dossierModifie = creerDossierFromFormulaire();
                // Conserver l'ID original
                dossierModifie.setIdDossier(dossierSelectionne.getIdDossier());
                
                boolean succes = dossierRepository.modifierDossier(dossierModifie);
                
                if (succes) {
                    afficherMessage("Dossier modifié avec succès!", "success");
                    annulerEdition();
                    chargerListeDossiers();
                } else {
                    throw new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Échec de la modification du dossier");
                }
            }
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors de la modification du dossier", e),
                "Modification dossier"
            );
        }
    }

    @FXML
    private void handleSupprimerDossier() {
        try {
            DossierEnCharge dossierSelectionne = dossiersListView.getSelectionModel().getSelectedItem();
            if (dossierSelectionne == null) {
                afficherMessage("Veuillez sélectionner un dossier à supprimer", "error");
                return;
            }
            
            boolean confirmed = ErrorHandler.showConfirmationAlert(
                "Suppression de dossier",
                "Êtes-vous sûr de vouloir supprimer le dossier du " + dossierSelectionne.getDateArrivee() + " ?"
            );
            
            if (confirmed) {
                boolean succes = dossierRepository.supprimerDossier(dossierSelectionne.getIdDossier());
                
                if (succes) {
                    afficherMessage("Dossier supprimé avec succès!", "success");
                    annulerEdition();
                    chargerListeDossiers();
                } else {
                    throw new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Échec de la suppression du dossier");
                }
            }
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors de la suppression du dossier", e),
                "Suppression dossier"
            );
        }
    }

    @FXML
    private void handleNouveauDossier() {
        annulerEdition();
        viderChamps();
        afficherMessage("Prêt pour la création d'un nouveau dossier", "info");
    }

    @FXML
    private void handleRetour() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            afficherMessage("Erreur lors du retour: " + e.getMessage(), "error");
        }
    }

    private void viderChamps() {
        dateArriveeField.setValue(LocalDate.now());
        
        // Réinitialiser les ComboBox d'heure à l'heure actuelle
        LocalTime maintenant = LocalTime.now();
        heureCombo.setValue(String.format("%02d", maintenant.getHour()));
        minuteCombo.setValue(String.format("%02d", (maintenant.getMinute() / 5) * 5));
        
        symptomesField.clear();
        niveauGraviteCombo.setValue("4 - Non urgent");
        
        // Réinitialiser le patient au premier de la liste
        if (patientCombo != null && !patientCombo.getItems().isEmpty()) {
            patientCombo.setValue(patientCombo.getItems().get(0));
        }
        
        // Réactiver tous les champs
        activerChamps(true);
    }
    
    private void annulerEdition() {
        dossierEnEdition = null;
        viderChamps();
        mettreAJourBoutonsEdition(false);
    }
    
    private void activerChamps(boolean activer) {
        dateArriveeField.setDisable(!activer);
        heureCombo.setDisable(!activer);
        minuteCombo.setDisable(!activer);
        symptomesField.setDisable(!activer);
        niveauGraviteCombo.setDisable(!activer);
        patientCombo.setDisable(!activer);
    }
    
    private boolean validerFormulaire() {
        if (dateArriveeField.getValue() == null) {
            afficherMessage("Veuillez sélectionner une date d'arrivée", "error");
            return false;
        }
        
        if (heureCombo.getValue() == null || minuteCombo.getValue() == null) {
            afficherMessage("Veuillez sélectionner une heure d'arrivée", "error");
            return false;
        }
        
        if (symptomesField.getText().trim().isEmpty()) {
            afficherMessage("Veuillez décrire les symptômes", "error");
            return false;
        }
        
        if (niveauGraviteCombo.getValue() == null) {
            afficherMessage("Veuillez sélectionner un niveau de gravité", "error");
            return false;
        }
        
        if (patientCombo.getValue() == null) {
            afficherMessage("Veuillez sélectionner un élève", "error");
            return false;
        }
        
        return true;
    }
    
    private DossierEnCharge creerDossierFromFormulaire() {
        try {
            int heure = Integer.parseInt(heureCombo.getValue());
            int minute = Integer.parseInt(minuteCombo.getValue());
            LocalTime heureArrivee = LocalTime.of(heure, minute);
            
            String graviteText = niveauGraviteCombo.getValue();
            int niveauGravite = Integer.parseInt(graviteText.split(" - ")[0]);
            
            FichePatient patientSelectionne = patientCombo.getValue();
            
            return new DossierEnCharge(
                dateArriveeField.getValue(),
                heureArrivee,
                symptomesField.getText().trim(),
                niveauGravite,
                patientSelectionne.getIdFichePatient()
            );
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erreur de format dans les données", e);
        }
    }

    private void afficherMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + 
            (type.equals("error") ? "#e74c3c" : 
             type.equals("success") ? "#27ae60" : "#3498db") + "; -fx-font-weight: bold;");
    }

    // Méthodes de navigation avec gestion d'erreur
    @FXML
    private void versAccueil() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à l'accueil", e),
                "Navigation vers Accueil"
            );
        }
    }

    @FXML
    private void versPatients() {
        try {
            StartApplication.changeScene("patientsView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des patients", e),
                "Navigation vers Patients"
            );
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
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au planning", e),
                "Navigation vers Planning"
            );
        }
    }

    @FXML
    private void versFicheProduit() {
        try {
            StartApplication.changeScene("ficheProduitView");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au catalogue produits", e),
                "Navigation vers Catalogue"
            );
        }
    }

    @FXML
    private void versUtilisateurs() {
        try {
            StartApplication.changeScene("pageUtilisateurs");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à la gestion des utilisateurs", e),
                "Navigation vers Utilisateurs"
            );
        }
    }

    @FXML
    private void versMonEspace() {
        try {
            StartApplication.changeScene("pageMonEspace");
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à votre espace personnel", e),
                "Navigation vers Mon Espace"
            );
        }
    }

    @FXML
    private void versDemandes() {
        try { StartApplication.changeScene("pageDemandeProduit"); } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void deconnexion() {
        try {
            boolean confirmed = ErrorHandler.showConfirmationAlert(
                "Déconnexion", 
                "Êtes-vous sûr de vouloir vous déconnecter ?"
            );
            if (confirmed) {
                SessionManager.deconnecter();
                StartApplication.changeScene("helloView");
                ErrorHandler.showInfoAlert("Déconnexion", "Vous avez été déconnecté avec succès");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.NAVIGATION_ERROR, "Erreur lors de la déconnexion", e),
                "Déconnexion"
            );
        }
    }
    
    private void chargerListeDossiers() {
        try {
            java.util.List<DossierEnCharge> dossiers = dossierRepository.getAllDossiers();
            System.out.println("Nombre de dossiers récupérés: " + dossiers.size());
            
            dossiersListView.getItems().clear();
            dossiersListView.getItems().addAll(dossiers);
            
            // Forcer le rafraîchissement de la ListView
            dossiersListView.refresh();
            
            // Rafraîchir aussi dans le thread UI pour être sûr
            Platform.runLater(() -> {
                dossiersListView.refresh();
                System.out.println("ListView rafraîchie - Items: " + dossiersListView.getItems().size());
            });
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors du chargement des dossiers", e),
                "Chargement liste dossiers"
            );
        }
    }
}
