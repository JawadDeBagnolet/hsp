package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import modele.RendezVous;
import repository.RendezVousRepository;
import repository.UserRepository;
import modele.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PlanningController {

    @FXML
    private Label semaineLabel;
    
    @FXML
    private Button semainePrecedenteButton;
    
    @FXML
    private Button semaineSuivanteButton;
    
    @FXML
    private Button aujourdHuiButton;
    
    @FXML
    private GridPane planningGrid;
    
    @FXML
    private ScrollPane planningScrollPane;
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private ComboBox<User> medecinComboBox;
    
    @FXML
    private Label totalRdvLabel;
    
    private RendezVousRepository rdvRepository;
    private UserRepository userRepository;
    private ObservableList<RendezVous> rendezVousObservable;
    private LocalDate debutSemaineActuelle;
    private User medecinSelectionne;

    @FXML
    public void initialize() {
        try {
            System.out.println("Initialisation du planning...");
            
            rdvRepository = new RendezVousRepository();
            userRepository = new UserRepository();
            rendezVousObservable = FXCollections.observableArrayList();
            
            // Initialiser la semaine actuelle
            debutSemaineActuelle = LocalDate.now().with(DayOfWeek.MONDAY);
            
            // Configurer les médecins
            configurerMedecins();
            
            // Configurer la grille de planning
            configurerGrillePlanning();
            
            // Charger les rendez-vous
            chargerRendezVous();
            
            System.out.println("Planning initialisé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du planning: " + e.getMessage());
            e.printStackTrace();
            if (messageLabel != null) {
                afficherMessage("Erreur d'initialisation: " + e.getMessage(), "#e74c3c");
            }
        }
    }

    private void configurerMedecins() {
        try {
            List<User> medecins = userRepository.getAllUsers();
            ObservableList<User> medecinsList = FXCollections.observableArrayList();
            
            for (User user : medecins) {
                if ("MEDECIN".equals(user.getRole())) {
                    medecinsList.add(user);
                }
            }
            
            medecinComboBox.setItems(medecinsList);
            medecinComboBox.setPromptText("Tous les médecins");
            
            // Écouteur de changement de sélection
            medecinComboBox.setOnAction(event -> {
                medecinSelectionne = medecinComboBox.getValue();
                chargerRendezVous();
            });
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des médecins: " + e.getMessage());
        }
    }

    private void configurerGrillePlanning() {
        // Nettoyer la grille
        planningGrid.getChildren().clear();
        planningGrid.getRowConstraints().clear();
        planningGrid.getColumnConstraints().clear();
        
        // Configuration des colonnes (7 jours + en-tête heures)
        planningGrid.setHgap(2);
        planningGrid.setVgap(2);
        
        // Ajouter les en-têtes des jours
        String[] jours = {"Heure", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for (int i = 0; i < jours.length; i++) {
            Label jourLabel = new Label(jours[i]);
            jourLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10; -fx-alignment: center;");
            jourLabel.setMaxWidth(Double.MAX_VALUE);
            planningGrid.add(jourLabel, i, 0);
        }
        
        // Ajouter les heures (8h à 19h)
        for (int heure = 8; heure <= 19; heure++) {
            Label heureLabel = new Label(String.format("%02d:00", heure));
            heureLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #ecf0f1; -fx-padding: 5; -fx-alignment: center;");
            planningGrid.add(heureLabel, 0, heure - 7);
            
            // Ajouter les cellules pour chaque jour
            for (int jour = 1; jour <= 7; jour++) {
                VBox cellule = new VBox();
                cellule.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-min-height: 60; -fx-padding: 2;");
                cellule.setMaxWidth(Double.MAX_VALUE);
                cellule.setMaxHeight(Double.MAX_VALUE);
                
                // Stocker la date et l'heure dans les propriétés de la cellule
                LocalDate dateCellule = debutSemaineActuelle.plusDays(jour - 1);
                LocalDateTime heureCellule = dateCellule.atTime(heure, 0);
                cellule.getProperties().put("dateHeure", heureCellule);
                
                // Double-clic pour ajouter un rendez-vous
                cellule.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        LocalDateTime dateHeureCliquée = (LocalDateTime) cellule.getProperties().get("dateHeure");
                        afficherMessage("Double-clic sur: " + dateHeureCliquée.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), "#3498db");
                        // TODO: Ouvrir une fenêtre pour ajouter un rendez-vous
                    }
                });
                
                planningGrid.add(cellule, jour, heure - 7);
            }
        }
        
        mettreAJourEnTetesJours();
    }

    private void mettreAJourEnTetesJours() {
        for (int jour = 1; jour <= 7; jour++) {
            LocalDate dateJour = debutSemaineActuelle.plusDays(jour - 1);
            String nomJour = dateJour.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            String dateFormatee = dateJour.format(DateTimeFormatter.ofPattern("dd/MM"));
            
            Label jourLabel = (Label) planningGrid.getChildren().get(jour);
            String texte = nomJour + "\n" + dateFormatee;
            
            // Mettre en évidence aujourd'hui
            if (dateJour.equals(LocalDate.now())) {
                jourLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10; -fx-alignment: center;");
                texte += "\n[Aujourd'hui]";
            } else {
                jourLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10; -fx-alignment: center;");
            }
            
            jourLabel.setText(texte);
        }
        
        // Mettre à jour le label de la semaine
        LocalDate finSemaine = debutSemaineActuelle.plusDays(6);
        semaineLabel.setText("Semaine du " + debutSemaineActuelle.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                           " au " + finSemaine.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void chargerRendezVous() {
        try {
            System.out.println("Chargement des rendez-vous pour la semaine du " + debutSemaineActuelle);
            
            List<RendezVous> rdvs;
            if (medecinSelectionne != null) {
                rdvs = rdvRepository.getRendezVousParMedecin(medecinSelectionne.getIdUser());
                // Filtrer pour la semaine actuelle
                rdvs.removeIf(rdv -> !rdv.estDansSemaine(debutSemaineActuelle));
            } else {
                rdvs = rdvRepository.getRendezVousParSemaine(debutSemaineActuelle);
            }
            
            rendezVousObservable.clear();
            rendezVousObservable.addAll(rdvs);
            
            // Vider les cellules existantes
            for (int heure = 8; heure <= 19; heure++) {
                for (int jour = 1; jour <= 7; jour++) {
                    VBox cellule = (VBox) planningGrid.getChildren().get((heure - 7) * 8 + jour);
                    cellule.getChildren().clear();
                }
            }
            
            // Ajouter les rendez-vous dans la grille
            for (RendezVous rdv : rdvs) {
                ajouterRendezVousDansGrille(rdv);
            }
            
            // Mettre à jour le compteur
            totalRdvLabel.setText("Total: " + rdvs.size() + " rendez-vous");
            
            afficherMessage("✅ " + rdvs.size() + " rendez-vous chargés", "#28a745");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des rendez-vous: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("❌ Erreur de chargement: " + e.getMessage(), "#dc3545");
        }
    }

    private void ajouterRendezVousDansGrille(RendezVous rdv) {
        if (rdv.getDateHeure() == null) return;
        
        int heure = rdv.getDateHeure().getHour();
        if (heure < 8 || heure > 19) return; // Hors des heures d'affichage
        
        int jourSemaine = rdv.getDateHeure().getDayOfWeek().getValue(); // 1=Lundi, 7=Dimanche
        
        // Trouver la cellule correspondante
        VBox cellule = (VBox) planningGrid.getChildren().get((heure - 7) * 8 + jourSemaine);
        
        // Créer l'affichage du rendez-vous
        Label rdvLabel = new Label();
        rdvLabel.setText(rdv.getHeureDebut() + " - " + rdv.getMotif());
        rdvLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 2; " +
                         "-fx-background-radius: 3; -fx-wrap-text: true; -fx-font-size: 10px;");
        rdvLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Couleur selon le statut
        switch (rdv.getStatut()) {
            case "CONFIRME":
                rdvLabel.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 2; " +
                                "-fx-background-radius: 3; -fx-wrap-text: true; -fx-font-size: 10px;");
                break;
            case "ANNULE":
                rdvLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 2; " +
                                "-fx-background-radius: 3; -fx-wrap-text: true; -fx-font-size: 10px;");
                break;
            case "EN_COURS":
                rdvLabel.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 2; " +
                                "-fx-background-radius: 3; -fx-wrap-text: true; -fx-font-size: 10px;");
                break;
        }
        
        // Tooltip avec plus d'informations
        Tooltip tooltip = new Tooltip("ID: " + rdv.getIdRdv() + 
                                    "\nPatient: " + rdv.getIdPatient() + 
                                    "\nMédecin: " + rdv.getIdMedecin() + 
                                    "\nMotif: " + rdv.getMotif() + 
                                    "\nStatut: " + rdv.getStatut());
        rdvLabel.setTooltip(tooltip);
        
        cellule.getChildren().add(rdvLabel);
    }

    @FXML
    public void semainePrecedente(ActionEvent event) {
        debutSemaineActuelle = debutSemaineActuelle.minusWeeks(1);
        configurerGrillePlanning();
        chargerRendezVous();
    }

    @FXML
    public void semaineSuivante(ActionEvent event) {
        debutSemaineActuelle = debutSemaineActuelle.plusWeeks(1);
        configurerGrillePlanning();
        chargerRendezVous();
    }

    @FXML
    public void aujourdHui(ActionEvent event) {
        debutSemaineActuelle = LocalDate.now().with(DayOfWeek.MONDAY);
        configurerGrillePlanning();
        chargerRendezVous();
    }

    @FXML
    public void actualiser(ActionEvent event) {
        chargerRendezVous();
    }

    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-size: 14px; -fx-wrap-text: true; -fx-font-weight: bold;");
    }

    // Méthodes de navigation
    @FXML
    public void versAccueil(ActionEvent event) {
        try {
            StartApplication.changeScene("pageAccueil");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers l'accueil: " + e.getMessage());
        }
    }

    @FXML
    public void versPatients(ActionEvent event) {
        try {
            StartApplication.changeScene("patientsView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers patients: " + e.getMessage());
        }
    }

    @FXML
    public void versCommandes(ActionEvent event) {
        try {
            StartApplication.changeScene("commandeView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers commandes: " + e.getMessage());
        }
    }

    @FXML
    public void versUtilisateurs(ActionEvent event) {
        try {
            StartApplication.changeScene("pageUtilisateurs");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    public void versPlanning(ActionEvent event) {
        // Déjà sur la page planning
        System.out.println("Déjà sur la page planning");
    }

    @FXML
    public void versMonEspace(ActionEvent event) {
        try {
            StartApplication.changeScene("pageMonEspace");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers mon espace: " + e.getMessage());
        }
    }

    @FXML
    public void deconnexion(ActionEvent event) {
        try {
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
