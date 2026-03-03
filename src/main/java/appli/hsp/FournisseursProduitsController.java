package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modele.FournisseurProduit;
import repository.FournisseurProduitRepository;

import java.util.List;
import java.util.Optional;

public class FournisseursProduitsController {

    @FXML
    private TableView<FournisseurProduit> fournisseursProduitsTable;

    @FXML
    private TableColumn<FournisseurProduit, Integer> idColumn;

    @FXML
    private TableColumn<FournisseurProduit, Integer> fournisseurColumn;

    @FXML
    private TableColumn<FournisseurProduit, Integer> produitColumn;

    @FXML
    private TableColumn<FournisseurProduit, Double> prixColumn;

    @FXML
    private TableColumn<FournisseurProduit, Void> actionsColumn;

    @FXML
    private Label messageLabel;

    private final FournisseurProduitRepository fournisseurProduitRepository = new FournisseurProduitRepository();
    private final ObservableList<FournisseurProduit> fournisseursProduitsObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur des fournisseurs produits...");

        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idFournisseurProduit"));
        System.out.println("Colonne ID configurée avec la propriété: idFournisseurProduit");
        
        fournisseurColumn.setCellValueFactory(new PropertyValueFactory<>("idFournisseur"));
        System.out.println("Colonne Fournisseur configurée avec la propriété: idFournisseur");
        
        produitColumn.setCellValueFactory(new PropertyValueFactory<>("idProduit"));
        System.out.println("Colonne Produit configurée avec la propriété: idProduit");
        
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        System.out.println("Colonne Prix configurée avec la propriété: prix");

        // Configuration de la colonne actions
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifierBtn = new Button("Modifier");
            private final Button supprimerBtn = new Button("Supprimer");
            private final javafx.scene.layout.HBox buttonsBox = new javafx.scene.layout.HBox(5, modifierBtn, supprimerBtn);

            {
                modifierBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 3;");
                
                modifierBtn.setOnAction(e -> {
                    FournisseurProduit fp = getTableView().getItems().get(getIndex());
                    System.out.println("Clic sur modifier pour: " + fp.getIdFournisseurProduit());
                    modifierFournisseurProduit(fp);
                });
                
                supprimerBtn.setOnAction(e -> {
                    FournisseurProduit fp = getTableView().getItems().get(getIndex());
                    System.out.println("Clic sur supprimer pour: " + fp.getIdFournisseurProduit());
                    supprimerFournisseurProduit(fp);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        fournisseursProduitsTable.setItems(fournisseursProduitsObservable);
        System.out.println("TableView configuré avec la liste observable");

        chargerFournisseursProduits();
    }

    private void chargerFournisseursProduits() {
        try {
            System.out.println("Chargement des fournisseurs produits...");
            List<FournisseurProduit> list = fournisseurProduitRepository.getAllFournisseursProduits();
            
            System.out.println("Avant clear - Taille de la liste observable: " + fournisseursProduitsObservable.size());
            fournisseursProduitsObservable.clear();
            System.out.println("Après clear - Taille de la liste observable: " + fournisseursProduitsObservable.size());
            
            // Logs détaillés pour chaque fournisseur produit
            System.out.println("=== DÉTAIL DES FOURNISSEURS PRODUITS CHARGÉS ===");
            for (int i = 0; i < list.size(); i++) {
                FournisseurProduit fp = list.get(i);
                System.out.println("FournisseurProduit " + (i+1) + ":");
                System.out.println("  ID: " + fp.getIdFournisseurProduit());
                System.out.println("  ID Fournisseur: " + fp.getIdFournisseur());
                System.out.println("  ID Produit: " + fp.getIdProduit());
                System.out.println("  Prix: " + fp.getPrix());
                System.out.println("---");
            }
            System.out.println("=== FIN DES DÉTAILS ===");
            
            fournisseursProduitsObservable.addAll(list);
            System.out.println("Après addAll - Taille de la liste observable: " + fournisseursProduitsObservable.size());
            
            afficherMessage("Total: " + list.size(), "#3498db");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des fournisseurs produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleNouveauFournisseurProduit(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouveau fournisseur produit");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idFournisseurField = new TextField();
        idFournisseurField.setPromptText("ID du fournisseur");
        
        TextField idProduitField = new TextField();
        idProduitField.setPromptText("ID du produit");
        
        TextField prixField = new TextField();
        prixField.setPromptText("Prix");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("ID Fournisseur:"), 0, 0);
        grid.add(idFournisseurField, 1, 0);
        grid.add(new Label("ID Produit:"), 0, 1);
        grid.add(idProduitField, 1, 1);
        grid.add(new Label("Prix:"), 0, 2);
        grid.add(prixField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                int idFournisseur = Integer.parseInt(idFournisseurField.getText().trim());
                int idProduit = Integer.parseInt(idProduitField.getText().trim());
                double prix = Double.parseDouble(prixField.getText().trim());

                if (idFournisseur <= 0 || idProduit <= 0 || prix <= 0) {
                    afficherMessage("Veuillez remplir tous les champs avec des valeurs valides", "#e74c3c");
                    return;
                }

                FournisseurProduit fp = new FournisseurProduit(idFournisseur, idProduit, prix);
                if (fournisseurProduitRepository.ajouterFournisseurProduit(fp)) {
                    afficherMessage("Fournisseur produit ajouté avec succès", "#27ae60");
                    chargerFournisseursProduits();
                } else {
                    afficherMessage("Erreur lors de l'ajout", "#e74c3c");
                }
            } catch (NumberFormatException e) {
                afficherMessage("Valeurs invalides", "#e74c3c");
            }
        });
    }

    private void modifierFournisseurProduit(FournisseurProduit fp) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier le fournisseur produit");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idFournisseurField = new TextField(String.valueOf(fp.getIdFournisseur()));
        TextField idProduitField = new TextField(String.valueOf(fp.getIdProduit()));
        TextField prixField = new TextField(String.valueOf(fp.getPrix()));

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("ID Fournisseur:"), 0, 0);
        grid.add(idFournisseurField, 1, 0);
        grid.add(new Label("ID Produit:"), 0, 1);
        grid.add(idProduitField, 1, 1);
        grid.add(new Label("Prix:"), 0, 2);
        grid.add(prixField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                int idFournisseur = Integer.parseInt(idFournisseurField.getText().trim());
                int idProduit = Integer.parseInt(idProduitField.getText().trim());
                double prix = Double.parseDouble(prixField.getText().trim());

                if (idFournisseur <= 0 || idProduit <= 0 || prix <= 0) {
                    afficherMessage("Veuillez remplir tous les champs avec des valeurs valides", "#e74c3c");
                    return;
                }

                fp.setIdFournisseur(idFournisseur);
                fp.setIdProduit(idProduit);
                fp.setPrix(prix);
                
                if (fournisseurProduitRepository.modifierFournisseurProduit(fp)) {
                    afficherMessage("Fournisseur produit modifié avec succès", "#27ae60");
                    chargerFournisseursProduits();
                } else {
                    afficherMessage("Erreur lors de la modification", "#e74c3c");
                }
            } catch (NumberFormatException e) {
                afficherMessage("Valeurs invalides", "#e74c3c");
            }
        });
    }

    private void supprimerFournisseurProduit(FournisseurProduit fp) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le fournisseur produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce fournisseur produit ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (fournisseurProduitRepository.supprimerFournisseurProduit(fp.getIdFournisseurProduit())) {
                afficherMessage("Fournisseur produit supprimé avec succès", "#27ae60");
                chargerFournisseursProduits();
            } else {
                afficherMessage("Erreur lors de la suppression", "#e74c3c");
            }
        }
    }

    @FXML
    public void handleRafraichir(ActionEvent event) {
        chargerFournisseursProduits();
    }

    @FXML
    public void handleRetour(ActionEvent event) {
        try {
            StartApplication.changeScene("/appli/hsp/pageAccueil.fxml");
        } catch (Exception e) {
            afficherMessage("Erreur lors du retour: " + e.getMessage(), "#e74c3c");
        }
    }

    private void afficherMessage(String message, String couleur) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-weight: bold;");
        }
    }
}
