package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import modele.Hospitalisation;
import repository.HospitalisationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HospitalisationsController {

    @FXML
    private TableView<Hospitalisation> hospitalisationsTable;

    @FXML
    private TableColumn<Hospitalisation, Integer> idColumn;

    @FXML
    private TableColumn<Hospitalisation, Integer> dossierColumn;

    @FXML
    private TableColumn<Hospitalisation, Integer> chambreColumn;

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
    private final ObservableList<Hospitalisation> hospitalisationsObservable = FXCollections.observableArrayList();

    private Hospitalisation hospitalisationSelectionnee;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idHospitalisation"));
        dossierColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idDossier"));
        chambreColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("idChambre"));

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
            private final Button cloturerBtn = new Button("Clôturer");

            {
                cloturerBtn.setOnAction(e -> {
                    Hospitalisation h = getTableView().getItems().get(getIndex());
                    cloturerHospitalisation(h);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cloturerBtn);
            }
        });

        hospitalisationsTable.setItems(hospitalisationsObservable);

        hospitalisationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> hospitalisationSelectionnee = newVal);

        chargerHospitalisations();
    }

    private void chargerHospitalisations() {
        try {
            List<Hospitalisation> list = hospitalisationRepository.getAllHospitalisations();
            hospitalisationsObservable.clear();
            hospitalisationsObservable.addAll(list);
            afficherMessage("Total: " + list.size(), "#3498db");
        } catch (Exception e) {
            afficherMessage("Erreur: " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    public void handleNouvelleHospitalisation(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle hospitalisation");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idDossierField = new TextField();
        TextField idChambreField = new TextField();
        TextField descField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("ID dossier:"), 0, 0);
        grid.add(idDossierField, 1, 0);
        grid.add(new Label("ID chambre:"), 0, 1);
        grid.add(idChambreField, 1, 1);
        grid.add(new Label("Description maladie:"), 0, 2);
        grid.add(descField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                int idDossier = Integer.parseInt(idDossierField.getText().trim());
                int idChambre = Integer.parseInt(idChambreField.getText().trim());
                String desc = descField.getText() != null ? descField.getText().trim() : "";

                if (idDossier <= 0 || idChambre <= 0 || desc.isEmpty()) {
                    afficherMessage("Champs invalides", "#e74c3c");
                    return;
                }

                Hospitalisation hosp = new Hospitalisation(idDossier, idChambre, LocalDateTime.now(), null, desc);
                if (hospitalisationRepository.ajouterHospitalisation(hosp)) {
                    afficherMessage("Hospitalisation créée", "#27ae60");
                    chargerHospitalisations();
                } else {
                    afficherMessage("Création impossible", "#e74c3c");
                }
            } catch (NumberFormatException e) {
                afficherMessage("IDs invalides", "#e74c3c");
            }
        });
    }

    private void cloturerHospitalisation(Hospitalisation hosp) {
        if (hosp == null) {
            return;
        }

        if (hosp.getDateFin() != null) {
            afficherMessage("Déjà clôturée", "#f39c12");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clôturer");
        alert.setHeaderText(null);
        alert.setContentText("Clôturer l'hospitalisation " + hosp.getIdHospitalisation() + " ?");

        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                hosp.setDateFin(LocalDateTime.now());
                if (hospitalisationRepository.modifierHospitalisation(hosp)) {
                    afficherMessage("Hospitalisation clôturée", "#27ae60");
                    chargerHospitalisations();
                } else {
                    afficherMessage("Erreur clôture", "#e74c3c");
                }
            }
        });
    }

    @FXML
    public void handleRafraichir(ActionEvent event) {
        chargerHospitalisations();
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
            System.err.println("Erreur: " + e.getMessage());
        }
    }
}
