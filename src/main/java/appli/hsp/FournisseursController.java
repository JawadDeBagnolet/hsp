package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.Fournisseur;
import modele.Produit;
import repository.FournisseurRepository;
import repository.ProduitRepository;

public class FournisseursController {

    @FXML
    private TextField nomFournisseurField;

    @FXML
    private TextField emailFournisseurField;

    @FXML
    private TextField telephoneFournisseurField;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<Fournisseur> fournisseursListView;

    @FXML
    private ListView<Produit> produitsListView;

    @FXML
    private Label fournisseurSelectionneLabel;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavUtilisateurs;

    private FournisseurRepository fournisseurRepository;
    private ProduitRepository produitRepository;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, null, null, btnNavCommandes, null, null, null, btnNavUtilisateurs, null);
        System.out.println("Initialisation du FournisseursController...");
        
        fournisseurRepository = new FournisseurRepository();
        produitRepository = new ProduitRepository();
        
        // Vérifier que les éléments FXML sont bien injectés
        if (nomFournisseurField == null || emailFournisseurField == null || telephoneFournisseurField == null) {
            System.err.println("ERREUR: Champs du formulaire non injectés !");
            return;
        }
        
        if (fournisseursListView == null || produitsListView == null) {
            System.err.println("ERREUR: ListViews non injectées !");
            return;
        }
        
        System.out.println("Tous les éléments FXML sont correctement injectés");
        
        // Initialiser la liste des fournisseurs
        chargerListeFournisseurs();
        
        // Personnaliser l'affichage des fournisseurs
        fournisseursListView.setCellFactory(param -> new javafx.scene.control.ListCell<Fournisseur>() {
            @Override
            protected void updateItem(Fournisseur fournisseur, boolean empty) {
                super.updateItem(fournisseur, empty);
                if (empty || fournisseur == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String texte = String.format("🏢 %s - 📧 %s - 📞 %d",
                        fournisseur.getNom(),
                        fournisseur.getEmail(),
                        fournisseur.getTel());
                    setText(texte);
                    setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-padding: 8;");
                }
            }
        });
        
        // Personnaliser l'affichage des produits
        produitsListView.setCellFactory(param -> new javafx.scene.control.ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String texte = String.format("📦 %s - 💰 %.2f€ - 📊 Quantité: %d",
                        produit.getNom(),
                        produit.getPrix(),
                        produit.getQuantite());
                    setText(texte);
                    setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0; -fx-padding: 8;");
                }
            }
        });
        
        // Gérer la sélection d'un fournisseur
        fournisseursListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chargerProduitsDuFournisseur(newVal);
                fournisseurSelectionneLabel.setText("Produits de: " + newVal.getNom());
            } else {
                produitsListView.getItems().clear();
                fournisseurSelectionneLabel.setText("Veuillez sélectionner un fournisseur pour voir ses produits");
            }
        });
        
        messageLabel.setText("");
        System.out.println("Initialisation terminée avec succès");
    }

    @FXML
    private void handleAjouterFournisseur() {
        System.out.println("=== DÉBUT AJOUT FOURNISSEUR ===");
        
        try {
            // Validation des champs
            if (nomFournisseurField.getText().trim().isEmpty()) {
                afficherMessage("Veuillez entrer le nom du fournisseur", "error");
                return;
            }
            
            if (emailFournisseurField.getText().trim().isEmpty()) {
                afficherMessage("Veuillez entrer l'email du fournisseur", "error");
                return;
            }
            
            if (telephoneFournisseurField.getText().trim().isEmpty()) {
                afficherMessage("Veuillez entrer le téléphone du fournisseur", "error");
                return;
            }
            
            // Validation du format du téléphone
            int telephone;
            try {
                telephone = Integer.parseInt(telephoneFournisseurField.getText().trim());
            } catch (NumberFormatException e) {
                afficherMessage("Le téléphone doit être un nombre valide", "error");
                return;
            }
            
            // Créer le fournisseur
            Fournisseur nouveauFournisseur = new Fournisseur(
                nomFournisseurField.getText().trim(),
                emailFournisseurField.getText().trim(),
                telephone
            );
            
            System.out.println("Fournisseur à ajouter: " + nouveauFournisseur.toString());
            
            // Sauvegarder dans la base de données
            boolean succes = fournisseurRepository.ajouterFournisseur(nouveauFournisseur);
            
            if (succes) {
                afficherMessage("Fournisseur ajouté avec succès!", "success");
                viderChamps();
                chargerListeFournisseurs();
            } else {
                afficherMessage("Erreur lors de l'ajout du fournisseur", "error");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur générale lors de l'ajout du fournisseur: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("Erreur: " + e.getMessage(), "error");
        }
        
        System.out.println("=== FIN AJOUT FOURNISSEUR ===");
    }

    @FXML
    private void handleViderChamps() {
        viderChamps();
        afficherMessage("Champs vidés", "info");
    }

    private void chargerListeFournisseurs() {
        try {
            System.out.println("Chargement de la liste des fournisseurs...");
            if (fournisseurRepository != null) {
                java.util.List<Fournisseur> fournisseurs = fournisseurRepository.getAllFournisseurs();
                System.out.println("Nombre de fournisseurs récupérés: " + fournisseurs.size());
                
                if (fournisseursListView != null) {
                    fournisseursListView.getItems().clear();
                    fournisseursListView.getItems().addAll(fournisseurs);
                    System.out.println("Liste des fournisseurs mise à jour dans la ListView");
                } else {
                    System.err.println("ERREUR: fournisseursListView est null !");
                }
            } else {
                System.err.println("ERREUR: fournisseurRepository est null !");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des fournisseurs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerProduitsDuFournisseur(Fournisseur fournisseur) {
        try {
            System.out.println("Chargement des produits du fournisseur: " + fournisseur.getNom());
            if (produitRepository != null) {
                java.util.List<Produit> produits = produitRepository.getProduitsParFournisseur(fournisseur.getIdFournisseur());
                System.out.println("Nombre de produits récupérés: " + produits.size());
                
                if (produitsListView != null) {
                    produitsListView.getItems().clear();
                    produitsListView.getItems().addAll(produits);
                    System.out.println("Liste des produits mise à jour dans la ListView");
                } else {
                    System.err.println("ERREUR: produitsListView est null !");
                }
            } else {
                System.err.println("ERREUR: produitRepository est null !");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des produits: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viderChamps() {
        nomFournisseurField.clear();
        emailFournisseurField.clear();
        telephoneFournisseurField.clear();
    }

    private void afficherMessage(String message, String type) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            if ("error".equals(type)) {
                messageLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else if ("success".equals(type)) {
                messageLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else {
                messageLabel.setStyle("-fx-text-fill: #3498db;"); // info
            }
        } else {
            System.out.println("Message: " + message + " (type: " + type + ")");
        }
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
    private void versDossiers() {
        try {
            StartApplication.changeScene("dossierEnChargeView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
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
            StartApplication.changeScene("helloView");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
}
