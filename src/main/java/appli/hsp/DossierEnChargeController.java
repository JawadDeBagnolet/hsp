package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import appli.SessionManager;
import appli.hsp.exception.ErrorCode;
import appli.hsp.exception.LPRSException;
import appli.hsp.utils.ErrorHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.DossierEnCharge;
import modele.FichePatient;
import modele.VisiteInfirmerie;
import repository.DossierEnChargeRepository;
import repository.FichePatientRepository;
import repository.VisiteInfirmerieRepository;

import java.util.List;

public class DossierEnChargeController {

    // Navbar
    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;
    @FXML private Button btnNavUtilisateurs;
    @FXML private Button btnNavDemandes;

    // Panneau gauche
    @FXML private ListView<FichePatient> elevesListView;

    // Panneau droit - en-tête élève
    @FXML private Label labelNomEleve;
    @FXML private Label labelInfoEleve;
    @FXML private Label labelDateCreation;

    // Formulaire dossier médical
    @FXML private TextArea antecedentsField;
    @FXML private TextArea allergiesField;
    @FXML private TextArea traitementsField;

    // Boutons et message
    @FXML private Button sauvegarderButton;
    @FXML private Button supprimerButton;
    @FXML private Label messageLabel;

    // Historique des visites
    @FXML private TableView<VisiteInfirmerie> visitesTableView;
    @FXML private TableColumn<VisiteInfirmerie, String> colDate;
    @FXML private TableColumn<VisiteInfirmerie, String> colHeure;
    @FXML private TableColumn<VisiteInfirmerie, String> colMotif;
    @FXML private TableColumn<VisiteInfirmerie, String> colTraitement;
    @FXML private TableColumn<VisiteInfirmerie, String> colStatut;

    private DossierEnChargeRepository dossierRepository;
    private FichePatientRepository patientRepository;
    private VisiteInfirmerieRepository visiteRepository;

    private DossierEnCharge dossierActuel = null;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, null, null, null, btnNavCommandes, btnNavPlanning, btnNavCatalogue, null, btnNavUtilisateurs, btnNavDemandes);
        try {
            dossierRepository = new DossierEnChargeRepository();
            patientRepository = new FichePatientRepository();
            visiteRepository = new VisiteInfirmerieRepository();

            configurerTableauVisites();
            chargerListeEleves();
            configurerSelectionEleve();
            afficherEtatVide();
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.SYSTEM_ERROR, "Erreur lors de l'initialisation", e),
                "Initialisation DossierEnChargeController"
            );
        }
    }

    private void configurerTableauVisites() {
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateFormatee()));
        colHeure.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeureFormatee()));
        colMotif.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMotif()));
        colTraitement.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getTraitement() != null ? data.getValue().getTraitement() : ""
        ));
        colStatut.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getStatut() != null ? data.getValue().getStatut() : "Terminée"
        ));

        // Coloration des lignes selon le statut
        visitesTableView.setRowFactory(tv -> new TableRow<VisiteInfirmerie>() {
            @Override
            protected void updateItem(VisiteInfirmerie item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if ("Urgences".equals(item.getStatut())) {
                    setStyle("-fx-background-color: rgba(239,68,68,0.15);");
                } else if ("En cours".equals(item.getStatut())) {
                    setStyle("-fx-background-color: rgba(245,158,11,0.15);");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void chargerListeEleves() {
        try {
            List<FichePatient> eleves = patientRepository.getAllFichePatients();
            elevesListView.getItems().clear();
            elevesListView.getItems().addAll(eleves);

            elevesListView.setCellFactory(param -> new ListCell<FichePatient>() {
                @Override
                protected void updateItem(FichePatient eleve, boolean empty) {
                    super.updateItem(eleve, empty);
                    if (empty || eleve == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(eleve.getPrenom() + " " + eleve.getNom());
                        setStyle("-fx-padding: 8 12; -fx-font-size: 13px;");
                    }
                }
            });
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors du chargement des élèves", e),
                "Chargement élèves"
            );
        }
    }

    private void configurerSelectionEleve() {
        elevesListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    chargerDossierEleve(newValue);
                }
            });
    }

    private void chargerDossierEleve(FichePatient eleve) {
        try {
            labelNomEleve.setText(eleve.getPrenom() + " " + eleve.getNom());
            labelInfoEleve.setText("N° étudiant : " + eleve.getNum_etudiant());

            dossierActuel = dossierRepository.getDossierByEleve(eleve.getIdFichePatient());

            if (dossierActuel != null) {
                antecedentsField.setText(dossierActuel.getAntecedents());
                allergiesField.setText(dossierActuel.getAllergies());
                traitementsField.setText(dossierActuel.getTraitementsChroniques());
                labelDateCreation.setText("Dossier créé le : " + dossierActuel.getDateCreation());
                supprimerButton.setDisable(false);
            } else {
                antecedentsField.clear();
                allergiesField.clear();
                traitementsField.clear();
                labelDateCreation.setText("Aucun dossier — sera créé à la première sauvegarde");
                supprimerButton.setDisable(true);
            }

            sauvegarderButton.setDisable(false);
            messageLabel.setText("");

            chargerHistoriqueVisites(eleve.getIdFichePatient());

        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors du chargement du dossier", e),
                "Chargement dossier élève"
            );
        }
    }

    private void chargerHistoriqueVisites(int idEleve) {
        try {
            List<VisiteInfirmerie> visites = visiteRepository.getVisitesParEleve(idEleve);
            visitesTableView.getItems().clear();
            visitesTableView.getItems().addAll(visites);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'historique des visites: " + e.getMessage());
        }
    }

    @FXML
    private void handleSauvegarder() {
        FichePatient eleveSelectionne = elevesListView.getSelectionModel().getSelectedItem();
        if (eleveSelectionne == null) {
            afficherMessage("Veuillez sélectionner un élève", "error");
            return;
        }

        try {
            String antecedents = antecedentsField.getText().trim();
            String allergies = allergiesField.getText().trim();
            String traitements = traitementsField.getText().trim();

            boolean succes;
            if (dossierActuel == null) {
                DossierEnCharge nouveauDossier = new DossierEnCharge(
                    eleveSelectionne.getIdFichePatient(), antecedents, allergies, traitements
                );
                succes = dossierRepository.creerDossier(nouveauDossier);
                if (succes) afficherMessage("Dossier médical créé avec succès", "success");
            } else {
                dossierActuel.setAntecedents(antecedents);
                dossierActuel.setAllergies(allergies);
                dossierActuel.setTraitementsChroniques(traitements);
                succes = dossierRepository.modifierDossier(dossierActuel);
                if (succes) afficherMessage("Dossier médical mis à jour avec succès", "success");
            }

            if (succes) {
                chargerDossierEleve(eleveSelectionne);
            } else {
                afficherMessage("Échec de la sauvegarde", "error");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(
                new LPRSException(ErrorCode.DATA_ACCESS_ERROR, "Erreur lors de la sauvegarde", e),
                "Sauvegarde dossier"
            );
        }
    }

    @FXML
    private void handleSupprimerDossier() {
        FichePatient eleveSelectionne = elevesListView.getSelectionModel().getSelectedItem();
        if (eleveSelectionne == null || dossierActuel == null) return;

        boolean confirmed = ErrorHandler.showConfirmationAlert(
            "Suppression du dossier médical",
            "Supprimer le dossier médical de " + eleveSelectionne.getPrenom() + " " + eleveSelectionne.getNom() + " ?"
        );

        if (confirmed) {
            boolean succes = dossierRepository.supprimerDossier(eleveSelectionne.getIdFichePatient());
            if (succes) {
                afficherMessage("Dossier supprimé", "success");
                chargerDossierEleve(eleveSelectionne);
            } else {
                afficherMessage("Échec de la suppression", "error");
            }
        }
    }

    private void afficherEtatVide() {
        labelNomEleve.setText("Sélectionnez un élève");
        labelInfoEleve.setText("");
        labelDateCreation.setText("");
        antecedentsField.clear();
        allergiesField.clear();
        traitementsField.clear();
        sauvegarderButton.setDisable(true);
        supprimerButton.setDisable(true);
        messageLabel.setText("");
        visitesTableView.getItems().clear();
    }

    private void afficherMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " +
            (type.equals("error") ? "#e74c3c" :
             type.equals("success") ? "#27ae60" : "#3498db") + "; -fx-font-weight: bold;");
    }

    // Navigation
    @FXML private void versAccueil() {
        try { StartApplication.changeScene("pageAccueil"); } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à l'accueil", e), "Navigation");
        }
    }

    @FXML private void versPatients() {
        try { StartApplication.changeScene("patientsView"); } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder aux patients", e), "Navigation");
        }
    }

    @FXML private void versCommandes() {
        try { NavigationHelper.versCommandes(); } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    @FXML private void versPlanning() {
        try { StartApplication.changeScene("planningView"); } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au planning", e), "Navigation");
        }
    }

    @FXML private void versFicheProduit() {
        try { StartApplication.changeScene("ficheProduitView"); } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder au catalogue", e), "Navigation");
        }
    }

    @FXML private void versUtilisateurs() {
        try { StartApplication.changeScene("pageUtilisateurs"); } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder aux utilisateurs", e), "Navigation");
        }
    }

    @FXML private void versMonEspace() {
        try { StartApplication.changeScene("pageMonEspace"); } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Impossible d'accéder à Mon Espace", e), "Navigation");
        }
    }

    @FXML private void versDemandes() {
        try { StartApplication.changeScene("pageDemandeProduit"); } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void deconnexion() {
        try {
            boolean confirmed = ErrorHandler.showConfirmationAlert("Déconnexion", "Êtes-vous sûr de vouloir vous déconnecter ?");
            if (confirmed) {
                SessionManager.deconnecter();
                StartApplication.changeScene("helloView");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(new LPRSException(ErrorCode.NAVIGATION_ERROR, "Erreur lors de la déconnexion", e), "Déconnexion");
        }
    }
}
