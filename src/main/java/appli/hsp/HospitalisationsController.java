package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import modele.Chambre;
import modele.Hospitalisation;
import repository.ChambreRepository;
import repository.HospitalisationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HospitalisationsController {

    @FXML
    private TableView<Hospitalisation> hospitalisationsTable;

    @FXML
    private TableColumn<Hospitalisation, Integer> idColumn;

    @FXML
    private TableColumn<Hospitalisation, Integer> dossierColumn;

    @FXML
    private TableColumn<Hospitalisation, String> chambreColumn;

    @FXML
    private TableColumn<Hospitalisation, String> debutColumn;

    @FXML
    private TableColumn<Hospitalisation, String> finColumn;

    @FXML
    private TableColumn<Hospitalisation, String> descriptionColumn;

    @FXML
    private TableColumn<Hospitalisation, Void> actionsColumn;

    @FXML
    private Label messageLabel;

    private final HospitalisationRepository hospitalisationRepository = new HospitalisationRepository();
    private final ChambreRepository chambreRepository = new ChambreRepository();
    private final ObservableList<Hospitalisation> hospitalisationsObservable = FXCollections.observableArrayList();
    private final ObservableList<Chambre> chambresDisponibles = FXCollections.observableArrayList();

    private Hospitalisation hospitalisationSelectionnee;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur d'hospitalisations...");
        
        // Test simple pour vérifier que le repository fonctionne
        try {
            System.out.println("Test du repository ChambreRepository...");
            List<Chambre> toutesLesChambres = chambreRepository.getAllChambres();
            System.out.println("Nombre total de chambres dans la BDD: " + toutesLesChambres.size());
            
            List<Chambre> chambresDisponibles = chambreRepository.getChambresDisponibles();
            System.out.println("Nombre de chambres disponibles: " + chambresDisponibles.size());
            
            // Tester la méthode de test détaillé
            chambreRepository.testerToutesLesChambres();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du test: " + e.getMessage());
            e.printStackTrace();
        }
        
        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idHospitalisation"));
        dossierColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idDossier"));
        chambreColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            int idChambre = cellData.getValue().getIdChambre();
            Chambre chambre = chambreRepository.trouverChambreParId(idChambre);
            if (chambre != null) {
                return new javafx.beans.property.SimpleStringProperty("Chambre " + chambre.getNumeroChambre());
            }
            return new javafx.beans.property.SimpleStringProperty("Chambre " + idChambre);
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        debutColumn.setCellValueFactory(cellData -> {
            LocalDateTime v = cellData.getValue() != null ? cellData.getValue().getDateDebut() : null;
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(dtf) : "");
        });

        finColumn.setCellValueFactory(cellData -> {
            LocalDateTime v = cellData.getValue() != null ? cellData.getValue().getDateFin() : null;
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(dtf) : "");
        });

        descriptionColumn.setCellValueFactory(cellData -> {
            String v = cellData.getValue() != null ? cellData.getValue().getDesc_maladie() : null;
            return new javafx.beans.property.SimpleStringProperty(v != null ? v : "");
        });

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifierBtn = new Button("Modifier");
            private final Button cloturerBtn = new Button("Clôturer");
            private final HBox buttonsBox = new HBox(5, modifierBtn, cloturerBtn);

            {
                modifierBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                cloturerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                
                modifierBtn.setOnAction(e -> {
                    Hospitalisation h = getTableView().getItems().get(getIndex());
                    modifierHospitalisation(h);
                });
                
                cloturerBtn.setOnAction(e -> {
                    Hospitalisation h = getTableView().getItems().get(getIndex());
                    cloturerHospitalisation(h);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Hospitalisation h = getTableView().getItems().get(getIndex());
                    cloturerBtn.setDisable(h.getDateFin() != null);
                    setGraphic(buttonsBox);
                }
            }
        });

        hospitalisationsTable.setItems(hospitalisationsObservable);

        hospitalisationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> hospitalisationSelectionnee = newVal);

        chargerHospitalisations();
        chargerChambresDisponibles();
    }

    private void chargerHospitalisations() {
        try {
            System.out.println("Chargement des hospitalisations...");
            List<Hospitalisation> list = hospitalisationRepository.getAllHospitalisations();
            
            System.out.println("Avant clear - Taille de la liste observable: " + hospitalisationsObservable.size());
            hospitalisationsObservable.clear();
            System.out.println("Après clear - Taille de la liste observable: " + hospitalisationsObservable.size());
            
            hospitalisationsObservable.addAll(list);
            System.out.println("Après addAll - Taille de la liste observable: " + hospitalisationsObservable.size());
            
            afficherMessage("Total: " + list.size(), "#3498db");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des hospitalisations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerChambresDisponibles() {
        try {
            System.out.println("Chargement des chambres disponibles...");
            List<Chambre> list = chambreRepository.getChambresDisponibles();
            chambresDisponibles.clear();
            chambresDisponibles.addAll(list);
            
            System.out.println("Nombre de chambres disponibles trouvées: " + list.size());
            for (Chambre chambre : list) {
                System.out.println("  - Chambre " + chambre.getNumeroChambre() + " (ID: " + chambre.getIdChambre() + ")");
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement chambres: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNouvelleHospitalisation(ActionEvent event) {
        System.out.println("Ouverture du formulaire de nouvelle hospitalisation...");
        System.out.println("Nombre de chambres dans la liste: " + chambresDisponibles.size());
        
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle hospitalisation");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idDossierField = new TextField();
        idDossierField.setPromptText("ID du dossier patient");
        
        ComboBox<Chambre> chambreCombo = new ComboBox<>(chambresDisponibles);
        chambreCombo.setPromptText("Sélectionner une chambre");
        
        // Personnaliser l'affichage des chambres dans le ComboBox
        chambreCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Chambre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Chambre " + item.getNumeroChambre() + " (ID: " + item.getIdChambre() + ")");
            }
        });
        
        chambreCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Chambre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Chambre " + item.getNumeroChambre());
            }
        });
        
        // Bouton pour rafraîchir les chambres
        Button rafraichirChambresBtn = new Button("🔄 Rafraîchir les chambres");
        rafraichirChambresBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        rafraichirChambresBtn.setOnAction(e -> {
            chargerChambresDisponibles();
            chambreCombo.setItems(chambresDisponibles);
            afficherMessage("Chambres rafraîchies", "#3498db");
        });
        
        DatePicker dateDebutPicker = new DatePicker();
        dateDebutPicker.setPromptText("Date de début");
        
        ComboBox<String> heureDebutCombo = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                String heure = String.format("%02d:%02d", i, j);
                heureDebutCombo.getItems().add(heure);
            }
        }
        heureDebutCombo.setPromptText("Heure de début");
        
        DatePicker dateFinPicker = new DatePicker();
        dateFinPicker.setPromptText("Date de fin (optionnelle)");
        
        ComboBox<String> heureFinCombo = new ComboBox<>();
        heureFinCombo.getItems().add("");
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                String heure = String.format("%02d:%02d", i, j);
                heureFinCombo.getItems().add(heure);
            }
        }
        heureFinCombo.setPromptText("Heure de fin (optionnelle)");
        
        TextArea descField = new TextArea();
        descField.setPromptText("Description de la maladie...");
        descField.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("ID dossier:"), 0, 0);
        grid.add(idDossierField, 1, 0);
        grid.add(new Label("Chambre:"), 0, 1);
        grid.add(chambreCombo, 1, 1);
        grid.add(rafraichirChambresBtn, 2, 1);
        grid.add(new Label("Date début:"), 0, 2);
        grid.add(dateDebutPicker, 1, 2);
        grid.add(new Label("Heure début:"), 0, 3);
        grid.add(heureDebutCombo, 1, 3);
        grid.add(new Label("Date fin:"), 0, 4);
        grid.add(dateFinPicker, 1, 4);
        grid.add(new Label("Heure fin:"), 0, 5);
        grid.add(heureFinCombo, 1, 5);
        grid.add(new Label("Description maladie:"), 0, 6);
        grid.add(descField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                int idDossier = Integer.parseInt(idDossierField.getText().trim());
                Chambre chambreSelectionnee = chambreCombo.getValue();
                String desc = descField.getText() != null ? descField.getText().trim() : "";

                System.out.println("Valeurs du formulaire:");
                System.out.println("  ID dossier: " + idDossier);
                System.out.println("  Chambre sélectionnée: " + (chambreSelectionnee != null ? "Chambre " + chambreSelectionnee.getNumeroChambre() : "null"));
                System.out.println("  Description: " + desc);

                if (idDossier <= 0 || chambreSelectionnee == null || desc.isEmpty() || dateDebutPicker.getValue() == null || heureDebutCombo.getValue() == null) {
                    afficherMessage("Veuillez remplir les champs obligatoires (ID dossier, chambre, date début, heure début, description)", "#e74c3c");
                    return;
                }

                // Créer la date de début
                LocalDateTime dateDebut = LocalDateTime.of(
                    dateDebutPicker.getValue().getYear(),
                    dateDebutPicker.getValue().getMonthValue(),
                    dateDebutPicker.getValue().getDayOfMonth(),
                    Integer.parseInt(heureDebutCombo.getValue().split(":")[0]),
                    Integer.parseInt(heureDebutCombo.getValue().split(":")[1])
                );

                // Créer la date de fin si fournie
                LocalDateTime dateFin = null;
                if (dateFinPicker.getValue() != null && !heureFinCombo.getValue().isEmpty()) {
                    dateFin = LocalDateTime.of(
                        dateFinPicker.getValue().getYear(),
                        dateFinPicker.getValue().getMonthValue(),
                        dateFinPicker.getValue().getDayOfMonth(),
                        Integer.parseInt(heureFinCombo.getValue().split(":")[0]),
                        Integer.parseInt(heureFinCombo.getValue().split(":")[1])
                    );
                }

                Hospitalisation hosp = new Hospitalisation(idDossier, chambreSelectionnee.getIdChambre(), dateDebut, dateFin, desc);
                if (hospitalisationRepository.ajouterHospitalisation(hosp)) {
                    // Marquer la chambre comme non disponible
                    chambreSelectionnee.setDisponible(false);
                    chambreRepository.modifierChambre(chambreSelectionnee);
                    
                    afficherMessage("Hospitalisation créée avec succès", "#27ae60");
                    chargerHospitalisations();
                    chargerChambresDisponibles();
                } else {
                    afficherMessage("Erreur lors de la création", "#e74c3c");
                }
            } catch (NumberFormatException e) {
                afficherMessage("ID dossier invalide", "#e74c3c");
            } catch (Exception e) {
                afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
                e.printStackTrace();
            }
        });
    }

    private void cloturerHospitalisation(Hospitalisation hosp) {
        if (hosp == null) {
            return;
        }

        if (hosp.getDateFin() != null) {
            afficherMessage("Cette hospitalisation est déjà clôturée", "#f39c12");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clôturer l'hospitalisation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment clôturer l'hospitalisation " + hosp.getIdHospitalisation() + " ?\n\nLa chambre sera libérée automatiquement.");

        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                hosp.setDateFin(LocalDateTime.now());
                if (hospitalisationRepository.modifierHospitalisation(hosp)) {
                    // Libérer la chambre
                    Chambre chambre = chambreRepository.trouverChambreParId(hosp.getIdChambre());
                    if (chambre != null) {
                        chambre.setDisponible(true);
                        chambreRepository.modifierChambre(chambre);
                    }
                    
                    afficherMessage("Hospitalisation clôturée avec succès", "#27ae60");
                    chargerHospitalisations();
                    chargerChambresDisponibles();
                } else {
                    afficherMessage("Erreur lors de la clôture", "#e74c3c");
                }
            }
        });
    }

    @FXML
    public void handleRafraichir(ActionEvent event) {
        chargerHospitalisations();
        chargerChambresDisponibles();
    }

    private void modifierHospitalisation(Hospitalisation hosp) {
        if (hosp == null) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'hospitalisation");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idDossierField = new TextField(String.valueOf(hosp.getIdDossier()));
        ComboBox<Chambre> chambreCombo = new ComboBox<>(chambresDisponibles);
        chambreCombo.setPromptText("Sélectionner une chambre");
        
        // Ajouter la chambre actuelle si elle n'est pas disponible
        Chambre chambreActuelle = chambreRepository.trouverChambreParId(hosp.getIdChambre());
        if (chambreActuelle != null && !chambresDisponibles.contains(chambreActuelle)) {
            chambresDisponibles.add(chambreActuelle);
        }
        chambreCombo.setValue(chambreActuelle);
        
        // Date début
        DatePicker dateDebutPicker = new DatePicker();
        dateDebutPicker.setValue(hosp.getDateDebut().toLocalDate());
        
        ComboBox<String> heureDebutCombo = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                String heure = String.format("%02d:%02d", i, j);
                heureDebutCombo.getItems().add(heure);
            }
        }
        heureDebutCombo.setValue(String.format("%02d:%02d", hosp.getDateDebut().getHour(), hosp.getDateDebut().getMinute()));
        
        // Date fin
        DatePicker dateFinPicker = new DatePicker();
        ComboBox<String> heureFinCombo = new ComboBox<>();
        heureFinCombo.getItems().add("");
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                String heure = String.format("%02d:%02d", i, j);
                heureFinCombo.getItems().add(heure);
            }
        }
        
        if (hosp.getDateFin() != null) {
            dateFinPicker.setValue(hosp.getDateFin().toLocalDate());
            heureFinCombo.setValue(String.format("%02d:%02d", hosp.getDateFin().getHour(), hosp.getDateFin().getMinute()));
        }
        
        dateFinPicker.setPromptText("Date de fin (optionnelle)");
        heureFinCombo.setPromptText("Heure de fin (optionnelle)");
        
        TextArea descField = new TextArea(hosp.getDesc_maladie());
        descField.setPrefRowCount(3);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("ID dossier:"), 0, 0);
        grid.add(idDossierField, 1, 0);
        grid.add(new Label("Chambre:"), 0, 1);
        grid.add(chambreCombo, 1, 1);
        grid.add(new Label("Date début:"), 0, 2);
        grid.add(dateDebutPicker, 1, 2);
        grid.add(new Label("Heure début:"), 0, 3);
        grid.add(heureDebutCombo, 1, 3);
        grid.add(new Label("Date fin:"), 0, 4);
        grid.add(dateFinPicker, 1, 4);
        grid.add(new Label("Heure fin:"), 0, 5);
        grid.add(heureFinCombo, 1, 5);
        grid.add(new Label("Description maladie:"), 0, 6);
        grid.add(descField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                int idDossier = Integer.parseInt(idDossierField.getText().trim());
                Chambre nouvelleChambre = chambreCombo.getValue();
                String desc = descField.getText() != null ? descField.getText().trim() : "";

                if (idDossier <= 0 || nouvelleChambre == null || desc.isEmpty() || dateDebutPicker.getValue() == null || heureDebutCombo.getValue() == null) {
                    afficherMessage("Veuillez remplir les champs obligatoires", "#e74c3c");
                    return;
                }

                // Créer la date de début
                LocalDateTime dateDebut = LocalDateTime.of(
                    dateDebutPicker.getValue().getYear(),
                    dateDebutPicker.getValue().getMonthValue(),
                    dateDebutPicker.getValue().getDayOfMonth(),
                    Integer.parseInt(heureDebutCombo.getValue().split(":")[0]),
                    Integer.parseInt(heureDebutCombo.getValue().split(":")[1])
                );

                // Créer la date de fin si fournie
                LocalDateTime dateFin = null;
                if (dateFinPicker.getValue() != null && !heureFinCombo.getValue().isEmpty()) {
                    dateFin = LocalDateTime.of(
                        dateFinPicker.getValue().getYear(),
                        dateFinPicker.getValue().getMonthValue(),
                        dateFinPicker.getValue().getDayOfMonth(),
                        Integer.parseInt(heureFinCombo.getValue().split(":")[0]),
                        Integer.parseInt(heureFinCombo.getValue().split(":")[1])
                    );
                }

                // Libérer l'ancienne chambre si différente
                if (hosp.getIdChambre() != nouvelleChambre.getIdChambre()) {
                    Chambre ancienneChambre = chambreRepository.trouverChambreParId(hosp.getIdChambre());
                    if (ancienneChambre != null) {
                        ancienneChambre.setDisponible(true);
                        chambreRepository.modifierChambre(ancienneChambre);
                    }
                    
                    nouvelleChambre.setDisponible(false);
                    chambreRepository.modifierChambre(nouvelleChambre);
                }

                hosp.setIdDossier(idDossier);
                hosp.setIdChambre(nouvelleChambre.getIdChambre());
                hosp.setDateDebut(dateDebut);
                hosp.setDateFin(dateFin);
                hosp.setDesc_maladie(desc);
                
                if (hospitalisationRepository.modifierHospitalisation(hosp)) {
                    afficherMessage("Hospitalisation modifiée avec succès", "#27ae60");
                    chargerHospitalisations();
                    chargerChambresDisponibles();
                } else {
                    afficherMessage("Erreur lors de la modification", "#e74c3c");
                }
            } catch (NumberFormatException e) {
                afficherMessage("ID dossier invalide", "#e74c3c");
            } catch (Exception e) {
                afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
            }
        });
    }

    private void afficherMessage(String message, String couleur) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-weight: bold;");
        }
    }

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
    public void versDossiers(ActionEvent event) {
        try {
            StartApplication.changeScene("dossierEnChargeView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers dossiers: " + e.getMessage());
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
