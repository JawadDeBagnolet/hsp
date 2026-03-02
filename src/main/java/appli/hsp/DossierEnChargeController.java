package appli.hsp;

import appli.StartApplication;
import appli.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.DossierEnCharge;
import modele.FichePatient;
import repository.DossierEnChargeRepository;
import repository.FichePatientRepository;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private DossierEnChargeRepository dossierRepository;
    private FichePatientRepository patientRepository;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du DossierEnChargeController...");
        
        dossierRepository = new DossierEnChargeRepository();
        patientRepository = new FichePatientRepository();
        
        // Vérifier que les éléments FXML sont bien injectés
        if (dateArriveeField == null) {
            System.err.println("ERREUR: dateArriveeField n'est pas injecté !");
            return;
        }
        if (heureCombo == null) {
            System.err.println("ERREUR: heureCombo n'est pas injecté !");
            return;
        }
        if (minuteCombo == null) {
            System.err.println("ERREUR: minuteCombo n'est pas injecté !");
            return;
        }
        if (niveauGraviteCombo == null) {
            System.err.println("ERREUR: niveauGraviteCombo n'est pas injecté !");
            return;
        }
        
        if (patientCombo == null) {
            System.err.println("ERREUR: patientCombo n'est pas injecté !");
            return;
        }
        
        System.out.println("Tous les éléments FXML sont correctement injectés");
        
        // Initialiser la date avec la date du jour (automatiquement)
        LocalDate aujourdHui = LocalDate.now();
        dateArriveeField.setValue(aujourdHui);
        System.out.println("Date du jour définie automatiquement: " + aujourdHui);
        
        // Initialiser le ComboBox des heures (00-23)
        heureCombo.getItems().clear();
        for (int i = 0; i < 24; i++) {
            heureCombo.getItems().add(String.format("%02d", i));
        }
        System.out.println("ComboBox heures initialisé avec " + heureCombo.getItems().size() + " éléments");
        
        // Personnaliser l'affichage du ComboBox des heures
        heureCombo.setCellFactory(param -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });
        
        // Personnaliser l'affichage de l'élément sélectionné pour les heures
        heureCombo.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });
        
        // Initialiser le ComboBox des minutes (00-55 par pas de 5)
        minuteCombo.getItems().clear();
        for (int i = 0; i < 60; i += 5) {
            minuteCombo.getItems().add(String.format("%02d", i));
        }
        System.out.println("ComboBox minutes initialisé avec " + minuteCombo.getItems().size() + " éléments");
        
        // Personnaliser l'affichage du ComboBox des minutes
        minuteCombo.setCellFactory(param -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });
        
        // Personnaliser l'affichage de l'élément sélectionné pour les minutes
        minuteCombo.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });
        
        // Initialiser le ComboBox de niveau de gravité
        niveauGraviteCombo.getItems().clear();
        niveauGraviteCombo.getItems().addAll(
            "1 - Urgence vitale",
            "2 - Urgence relative", 
            "3 - Urgence différée",
            "4 - Non urgent"
        );
        System.out.println("ComboBox niveau de gravité initialisé avec " + niveauGraviteCombo.getItems().size() + " éléments");
        
        // Personnaliser l'affichage du ComboBox de niveau de gravité
        niveauGraviteCombo.setCellFactory(param -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });
        
        // Personnaliser l'affichage de l'élément sélectionné pour niveau de gravité
        niveauGraviteCombo.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
                }
            }
        });
        
        // Initialiser le ComboBox des patients
        patientCombo.getItems().clear();
        try {
            java.util.List<FichePatient> patients = patientRepository.getAllFichePatients();
            System.out.println("Nombre de patients récupérés: " + patients.size());
            
            for (FichePatient patient : patients) {
                patientCombo.getItems().add(patient);
            }
            
            // Personnaliser l'affichage des patients dans le ComboBox
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
            
            // Personnaliser l'affichage de l'élément sélectionné
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
                System.out.println("Patient par défaut sélectionné: " + patientCombo.getValue().getNom() + " " + patientCombo.getValue().getPrenom());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // Valeurs par défaut pour l'heure
            LocalTime maintenant = LocalTime.now();
            String heureStr = String.format("%02d", maintenant.getHour());
            String minuteStr = String.format("%02d", (maintenant.getMinute() / 5) * 5);
            
            heureCombo.setValue(heureStr);
            minuteCombo.setValue(minuteStr);
            niveauGraviteCombo.setValue("4 - Non urgent");
            
            System.out.println("Valeurs par défaut définies - Heure: " + heureStr + ":" + minuteStr + ", Gravité: 4 - Non urgent");
            
        } catch (Exception e) {
            System.err.println("ERREUR lors de la définition des valeurs par défaut: " + e.getMessage());
            e.printStackTrace();
        }
        
        messageLabel.setText("");
        System.out.println("Initialisation terminée avec succès");
        
        // Forcer le rafraîchissement des ComboBox
        Platform.runLater(() -> {
            heureCombo.requestFocus();
            minuteCombo.requestFocus();
            niveauGraviteCombo.requestFocus();
            dateArriveeField.requestFocus();
            System.out.println("Rafraîchissement des éléments forcé");
        });
        
        // Initialiser la liste des dossiers
        if (dossiersListView != null) {
            System.out.println("Initialisation de la ListView des dossiers...");
            chargerListeDossiers();
            
            // Personnaliser l'affichage des dossiers dans la ListView
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
        } else {
            System.err.println("ERREUR: dossiersListView n'est pas injecté !");
        }
    }

    @FXML
    private void handleCreerDossier() {
        System.out.println("=== DÉBUT CRÉATION DOSSIER ===");
        
        try {
            // Validation des champs
            System.out.println("Validation des champs...");
            
            if (dateArriveeField == null) {
                System.err.println("ERREUR: dateArriveeField est null !");
                afficherMessage("Erreur interne: champ date non initialisé", "error");
                return;
            }
            
            System.out.println("dateArriveeField n'est pas null, valeur: " + dateArriveeField.getValue());
            
            if (dateArriveeField.getValue() == null) {
                System.out.println("Date non sélectionnée");
                afficherMessage("Veuillez sélectionner une date d'arrivée", "error");
                return;
            }
            System.out.println("Date sélectionnée: " + dateArriveeField.getValue());
            
            if (heureCombo == null || minuteCombo == null) {
                System.err.println("ERREUR: ComboBox heure/minute sont null !");
                afficherMessage("Erreur interne: champs heure non initialisés", "error");
                return;
            }
            
            System.out.println("heureCombo: " + (heureCombo.getValue() != null ? heureCombo.getValue() : "NULL"));
            System.out.println("minuteCombo: " + (minuteCombo.getValue() != null ? minuteCombo.getValue() : "NULL"));
            
            if (heureCombo.getValue() == null || minuteCombo.getValue() == null) {
                System.out.println("Heure non sélectionnée - Heure: " + heureCombo.getValue() + ", Minute: " + minuteCombo.getValue());
                afficherMessage("Veuillez sélectionner une heure d'arrivée", "error");
                return;
            }
            System.out.println("Heure sélectionnée: " + heureCombo.getValue() + ":" + minuteCombo.getValue());
            
            if (symptomesField == null) {
                System.err.println("ERREUR: symptomesField est null !");
                afficherMessage("Erreur interne: champ symptômes non initialisé", "error");
                return;
            }
            
            if (symptomesField.getText().trim().isEmpty()) {
                System.out.println("Symptômes vides");
                afficherMessage("Veuillez décrire les symptômes", "error");
                return;
            }
            System.out.println("Symptômes: " + symptomesField.getText().trim().substring(0, Math.min(50, symptomesField.getText().trim().length())) + "...");
            
            if (niveauGraviteCombo == null) {
                System.err.println("ERREUR: niveauGraviteCombo est null !");
                afficherMessage("Erreur interne: champ gravité non initialisé", "error");
                return;
            }
            
            System.out.println("niveauGraviteCombo: " + (niveauGraviteCombo.getValue() != null ? niveauGraviteCombo.getValue() : "NULL"));
            
            if (niveauGraviteCombo.getValue() == null) {
                System.out.println("Niveau de gravité non sélectionné: " + niveauGraviteCombo.getValue());
                afficherMessage("Veuillez sélectionner un niveau de gravité", "error");
                return;
            }
            System.out.println("Niveau de gravité sélectionné: " + niveauGraviteCombo.getValue());
            
            if (patientCombo == null) {
                System.err.println("ERREUR: patientCombo est null !");
                afficherMessage("Erreur interne: champ patient non initialisé", "error");
                return;
            }
            
            System.out.println("patientCombo: " + (patientCombo.getValue() != null ? patientCombo.getValue().getNom() + " " + patientCombo.getValue().getPrenom() : "NULL"));
            
            if (patientCombo.getValue() == null) {
                System.out.println("Aucun patient sélectionné");
                afficherMessage("Veuillez sélectionner un patient", "error");
                return;
            }
            System.out.println("Patient sélectionné: " + patientCombo.getValue().getNom() + " " + patientCombo.getValue().getPrenom() + " (ID: " + patientCombo.getValue().getIdFichePatient() + ")");
            
            // Récupérer le patient sélectionné
            System.out.println("Utilisation du patient sélectionné...");
            FichePatient patientSelectionne = patientCombo.getValue();
            System.out.println("Patient sélectionné: " + patientSelectionne.getNom() + " " + patientSelectionne.getPrenom() + " (ID: " + patientSelectionne.getIdFichePatient() + ")");
            
            // Construire l'heure à partir des ComboBox
            System.out.println("Construction de l'heure...");
            try {
                int heure = Integer.parseInt(heureCombo.getValue());
                int minute = Integer.parseInt(minuteCombo.getValue());
                LocalTime heureArrivee = LocalTime.of(heure, minute);
                System.out.println("Heure construite: " + heureArrivee);
                
                // Extraire le niveau de gravité (premier chiffre)
                System.out.println("Extraction du niveau de gravité...");
                String graviteText = niveauGraviteCombo.getValue();
                int niveauGravite = Integer.parseInt(graviteText.split(" - ")[0]);
                System.out.println("Niveau de gravité extrait: " + niveauGravite);
                
                // Créer le dossier
                System.out.println("Création de l'objet DossierEnCharge...");
                System.out.println("Patient sélectionné: " + patientSelectionne.toString());
                System.out.println("Patient sélectionné ID: " + patientSelectionne.getIdFichePatient());
                
                // Vérifier que l'ID patient est valide
                if (patientSelectionne.getIdFichePatient() <= 0) {
                    System.err.println("ERREUR: ID patient invalide: " + patientSelectionne.getIdFichePatient());
                    afficherMessage("Erreur: ID patient invalide", "error");
                    return;
                }
                
                DossierEnCharge nouveauDossier = new DossierEnCharge(
                    dateArriveeField.getValue(),
                    heureArrivee,
                    symptomesField.getText().trim(),
                    niveauGravite,
                    patientSelectionne.getIdFichePatient()
                );
                System.out.println("Objet DossierEnCharge créé: " + nouveauDossier.toString());
                
                // Sauvegarder dans la base de données
                System.out.println("Sauvegarde dans la base de données...");
                if (dossierRepository == null) {
                    System.err.println("ERREUR: dossierRepository est null !");
                    afficherMessage("Erreur interne: repository non initialisé", "error");
                    return;
                }
                
                System.out.println("Appel de ajouterDossier...");
                boolean succes = dossierRepository.ajouterDossier(nouveauDossier);
                System.out.println("Résultat sauvegarde: " + succes);
                
                if (succes) {
                    afficherMessage("Dossier de prise en charge créé avec succès!", "success");
                    viderChamps();
                    // Rafraîchir la liste des dossiers
                    chargerListeDossiers();
                } else {
                    afficherMessage("Erreur lors de la création du dossier", "error");
                }
                
            } catch (NumberFormatException e) {
                System.err.println("Erreur de format numérique: " + e.getMessage());
                afficherMessage("Erreur de format dans les données", "error");
            } catch (Exception e) {
                System.err.println("Erreur générale lors de la création du dossier: " + e.getMessage());
                e.printStackTrace();
                afficherMessage("Erreur: " + e.getMessage(), "error");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur générale dans handleCreerDossier: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur générale: " + e.getMessage(), "error");
        }
        
        System.out.println("=== FIN CRÉATION DOSSIER ===");
    }

    @FXML
    private void handleViderChamps() {
        viderChamps();
        afficherMessage("Champs vidés", "info");
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
    }

    private void afficherMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + 
            (type.equals("error") ? "#e74c3c" : 
             type.equals("success") ? "#27ae60" : "#3498db") + "; -fx-font-weight: bold;");
    }

    // Méthodes de navigation
    @FXML
    private void versAccueil() {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void versPatients() {
        try {
            StartApplication.changeScene("patientsView");
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
            SessionManager.deconnecter();
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    private void chargerListeDossiers() {
        try {
            System.out.println("Chargement de la liste des dossiers...");
            if (dossierRepository != null) {
                java.util.List<DossierEnCharge> dossiers = dossierRepository.getAllDossiers();
                System.out.println("Nombre de dossiers récupérés: " + dossiers.size());
                
                if (dossiersListView != null) {
                    dossiersListView.getItems().clear();
                    dossiersListView.getItems().addAll(dossiers);
                    System.out.println("Liste des dossiers mise à jour dans la ListView");
                } else {
                    System.err.println("ERREUR: dossiersListView est null !");
                }
            } else {
                System.err.println("ERREUR: dossierRepository est null !");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des dossiers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
