package appli.hsp;

import appli.StartApplication;
import appli.hsp.utils.NavbarHelper;
import appli.hsp.utils.NavigationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import modele.User;
import repository.UserRepository;

import java.util.List;
import java.util.Optional;

import appli.util.PasswordUtils;

public class PageUtilisateurs {


    @FXML
    private Text totalUsersLabel;

    @FXML
    private VBox utilisateursContainer;
    
    @FXML
    private ListView<User> utilisateursList;
    
    @FXML
    private TableColumn<User, Integer> idColumn;
    
    @FXML
    private TableColumn<User, String> nomColumn;
    
    @FXML
    private TableColumn<User, String> prenomColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;
    
    @FXML
    private TableColumn<User, String> roleColumn;
    
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField mdpField;
    
    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label messageLabel;
    
    @FXML
    private Text resultCountLabel;
    
    @FXML
    private Text formModeLabel;
    
    @FXML
    private Button ajouterButton;
    
    @FXML
    private Button modifierButton;
    
    @FXML
    private Button supprimerButton;
    
    @FXML
    private Button viderButton;
    
    @FXML
    private TextField rechercheField;

    @FXML private Button btnNavSecretariat;
    @FXML private Button btnNavDossiers;
    @FXML private Button btnNavCommandes;
    @FXML private Button btnNavPlanning;
    @FXML private Button btnNavCatalogue;

    private UserRepository userRepository;
    private ObservableList<User> utilisateursObservable;
    private User utilisateurSelectionne;

    @FXML
    public void initialize() {
        NavbarHelper.appliquerNavbar(btnNavSecretariat, btnNavDossiers, null, null, btnNavCommandes, btnNavPlanning, btnNavCatalogue, null, null, null);
        try {
            System.out.println("Initialisation de la page utilisateurs...");
            
            userRepository = new UserRepository();
            utilisateursObservable = FXCollections.observableArrayList();
            
            // Initialiser le VBox simple et efficace
            configurerVBox();
            
            // Initialiser la liste des rôles
            configurerRoles();
            
            // Configurer la recherche
            configurerRecherche();
            
            // Charger les utilisateurs EN DERNIER (après que tout soit configuré)
            chargerUtilisateurs();
            
            // Afficher les utilisateurs en console pour le débogage
            afficherUtilisateursConsole();
            
            System.out.println("Page utilisateurs initialisée avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la page utilisateurs: " + e.getMessage());
            e.printStackTrace();
            if (messageLabel != null) {
                afficherMessage("Erreur d'initialisation: " + e.getMessage(), "#e74c3c");
            }
        }
    }

    private void configurerVBox() {
        System.out.println("Configuration du VBox des utilisateurs...");
        
        // Vérifier que le VBox est bien injecté
        if (utilisateursContainer == null) {
            System.err.println("❌ utilisateursContainer est null");
            return;
        }
        
        System.out.println("✅ VBox injecté avec succès");
        System.out.println("✅ VBox configuré avec succès");
    }
    
    private String getRoleColor(String role) {
        if (role == null) return "#6c757d";
        switch (role) {
            case "ADMIN": return "#dc3545";
            case "PROF": return "#007bff";
            case "INFIRMIER": return "#28a745";
            case "SECRETAIRE": return "#ffc107";
            case "GESTIONNAIRE_DE_STOCK": return "#6f42c1";
            default: return "#6c757d";
        }
    }

    private void configurerRoles() {
        roleComboBox.setItems(FXCollections.observableArrayList(
            "ADMIN",
            "PROF",
            "INFIRMIER",
            "SECRETAIRE",
            "GESTIONNAIRE_DE_STOCK"
        ));
        roleComboBox.setPromptText("Sélectionner un rôle");
    }

    private void afficherUtilisateursConsole() {
        if (utilisateursObservable == null || utilisateursObservable.isEmpty()) {
            System.out.println("⚠️ Aucun utilisateur trouvé dans la base de données");
            return;
        }
        
        System.out.println("\n=== 📋 UTILISATEURS ENREGISTRÉS DANS LA BASE DE DONNÉES ===");
        System.out.println("┌────┬──────────────┬──────────────┬──────────────────────────┬──────────────────┐");
        System.out.println("│ ID │     NOM     │    PRÉNOM   │         EMAIL         │      RÔLE       │");
        System.out.println("├────┼──────────────┼──────────────┼──────────────────────────┼──────────────────┤");
        
        for (User user : utilisateursObservable) {
            System.out.printf("│ %2d │ %-12s │ %-12s │ %-22s │ %-14s │%n", 
                user.getIdUser(), 
                user.getNom() != null ? user.getNom() : "", 
                user.getPrenom() != null ? user.getPrenom() : "", 
                user.getEmail() != null ? user.getEmail() : "", 
                user.getRole() != null ? user.getRole() : "");
        }
        
        System.out.println("└────┴──────────────┴──────────────┴──────────────────────────┴──────────────────┘");
        System.out.println("Total: " + utilisateursObservable.size() + " utilisateur" + (utilisateursObservable.size() > 1 ? "s" : ""));
        System.out.println("\n=== 🎭 RÔLES DISPONIBLES ===");
        System.out.println("🔴 ADMIN");
        System.out.println("🔵 PROF");
        System.out.println("🔵 PROF");
        System.out.println("🟢 INFIRMIER");
        System.out.println("🟡 SECRETAIRE");
        System.out.println("🟣 GESTIONNAIRE_DE_STOCK");
        System.out.println("========================================\n");
    }

    private void chargerUtilisateurs() {
        try {
            System.out.println("Chargement des utilisateurs depuis la base de données...");
            
            // Test de connexion à la BDD
            System.out.println("Test de connexion à la base de données...");
            try (java.sql.Connection cnx = database.Database.getConnexion()) {
                if (cnx != null && !cnx.isClosed()) {
                    System.out.println("✅ Connexion à la BDD réussie");
                } else {
                    System.err.println("❌ Connexion à la BDD échouée");
                    return;
                }
            }
            
            List<User> users = userRepository.getAllUsers();
            System.out.println("Nombre brut d'utilisateurs récupérés: " + users.size());
            
            // Afficher les détails en console pour débogage
            System.out.println("=== DÉTAILS DES UTILISATEURS CHARGÉS ===");
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                System.out.printf("[%d] ID: %d, Nom: %s, Prénom: %s, Email: %s, Rôle: %s%n", 
                    i, user.getIdUser(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole());
            }
            System.out.println("========================================");
            
            // Vider et recharger la liste observable
            System.out.println("🔄 Vidage de la liste observable...");
            utilisateursObservable.clear();
            System.out.println("🔄 Ajout des " + users.size() + " utilisateurs à la liste observable...");
            utilisateursObservable.addAll(users);
            
            System.out.println("Nombre d'utilisateurs dans la liste observable: " + utilisateursObservable.size());
            
            // Vider le VBox et ajouter les utilisateurs sous forme de Labels
            System.out.println("🔄 Vidage du VBox...");
            utilisateursContainer.getChildren().clear();
            
            System.out.println("🔄 Ajout des utilisateurs au VBox...");
            for (User user : users) {
                // Créer un Label pour chaque utilisateur
                Label userLabel = new Label();
                String texte = String.format("ID: %d | %s %s | %s | Rôle: %s", 
                    user.getIdUser(), 
                    user.getNom(), 
                    user.getPrenom(), 
                    user.getEmail(), 
                    user.getRole());
                userLabel.setText(texte);
                
                // Style selon le rôle
                String bgColor = getRoleColor(user.getRole());
                userLabel.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; -fx-font-weight: 600; -fx-background-radius: 8; -fx-padding: 10; -fx-margin: 2;");
                
                // Ajouter au VBox
                utilisateursContainer.getChildren().add(userLabel);
                
                System.out.println("🔍 Ajout utilisateur au VBox: " + texte);
            }
            
            System.out.println("✅ " + utilisateursContainer.getChildren().size() + " utilisateurs ajoutés au VBox");
            
            // Mettre à jour le label de statistiques
            if (totalUsersLabel != null) {
                totalUsersLabel.setText("Total: " + users.size() + " utilisateur" + (users.size() > 1 ? "s" : ""));
            }
            if (resultCountLabel != null) {
                resultCountLabel.setText("");
            }
            
            afficherMessage("✅ " + users.size() + " utilisateur" + (users.size() > 1 ? "s" : "") + " chargé" + (users.size() > 1 ? "s" : "") + " avec succès", "#28a745");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des utilisateurs: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("❌ Erreur de chargement: " + e.getMessage(), "#dc3545");
        }
    }

    private void configurerSelectionListe() {
        utilisateursList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    utilisateurSelectionne = newSelection;
                    afficherUtilisateurSelectionne();
                }
            });
    }

    private void configurerRecherche() {
        rechercheField.textProperty().addListener((obs, oldValue, newValue) -> {
            filtrerUtilisateurs(newValue);
        });
    }

    private void filtrerUtilisateurs(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            utilisateursList.setItems(utilisateursObservable);
            resultCountLabel.setText("");
            return;
        }
        
        ObservableList<User> utilisateursFiltres = FXCollections.observableArrayList();
        String rechercheLower = recherche.toLowerCase().trim();
        
        for (User user : utilisateursObservable) {
            if (user.getNom().toLowerCase().contains(rechercheLower) ||
                user.getPrenom().toLowerCase().contains(rechercheLower) ||
                user.getEmail().toLowerCase().contains(rechercheLower) ||
                user.getRole().toLowerCase().contains(rechercheLower)) {
                utilisateursFiltres.add(user);
            }
        }
        
        utilisateursList.setItems(utilisateursFiltres);
        resultCountLabel.setText(utilisateursFiltres.size() + " résultat" + (utilisateursFiltres.size() > 1 ? "s" : ""));
        afficherMessage("Résultats: " + utilisateursFiltres.size(), "#3498db");
    }

    private void afficherUtilisateurSelectionne() {
        if (utilisateurSelectionne != null) {
            nomField.setText(utilisateurSelectionne.getNom());
            prenomField.setText(utilisateurSelectionne.getPrenom());
            emailField.setText(utilisateurSelectionne.getEmail());
            mdpField.setText(""); // Ne pas afficher le mot de passe
            roleComboBox.setValue(utilisateurSelectionne.getRole());
            
            // Mettre à jour le mode du formulaire
            if (formModeLabel != null) {
                formModeLabel.setText("Mode: Modification");
            }
            
            // Activer les boutons de modification
            modifierButton.setDisable(false);
            supprimerButton.setDisable(false);
            ajouterButton.setDisable(true);
        }
    }

    @FXML
    public void ajouterUtilisateur(ActionEvent event) {
        if (!validerChamps()) {
            return;
        }
        String hash = PasswordUtils.hashPassword(mdpField.getText());

        User nouvelUtilisateur = new User(
            nomField.getText().trim(),
            prenomField.getText().trim(),
            emailField.getText().trim(),
            hash,
            roleComboBox.getValue()
        );
        
        if (userRepository.ajouterUtilisateur(nouvelUtilisateur)) {
            afficherMessage("Utilisateur ajouté avec succès!", "#2ecc71");
            viderChampsAction();
            chargerUtilisateurs();
        } else {
            afficherMessage("Erreur lors de l'ajout de l'utilisateur", "#e74c3c");
        }
    }

    @FXML
    public void modifierUtilisateur(ActionEvent event) {
        if (utilisateurSelectionne == null) {
            afficherMessage("Veuillez sélectionner un utilisateur à modifier", "#e74c3c");
            return;
        }
        
        if (!validerChamps()) {
            return;
        }
        
        // Mettre à jour les informations
        utilisateurSelectionne.setNom(nomField.getText().trim());
        utilisateurSelectionne.setPrenom(prenomField.getText().trim());
        utilisateurSelectionne.setEmail(emailField.getText().trim());
        
        // Mettre à jour le mot de passe seulement si un nouveau est fourni
        if (!mdpField.getText().trim().isEmpty()) {
            utilisateurSelectionne.setMdp(mdpField.getText());
        }
        
        utilisateurSelectionne.setRole(roleComboBox.getValue());
        
        if (userRepository.modifierUtilisateur(utilisateurSelectionne)) {
            afficherMessage("Utilisateur modifié avec succès!", "#2ecc71");
            viderChampsAction();
            chargerUtilisateurs();
        } else {
            afficherMessage("Erreur lors de la modification de l'utilisateur", "#e74c3c");
        }
    }

    @FXML
    public void supprimerUtilisateur(ActionEvent event) {
        if (utilisateurSelectionne == null) {
            afficherMessage("Veuillez sélectionner un utilisateur à supprimer", "#e74c3c");
            return;
        }
        
        // Confirmation de suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'utilisateur");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + 
            utilisateurSelectionne.getNom() + " " + utilisateurSelectionne.getPrenom() + " ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userRepository.supprimerUtilisateur(utilisateurSelectionne.getIdUser())) {
                afficherMessage("Utilisateur supprimé avec succès!", "#2ecc71");
                viderChampsAction();
                chargerUtilisateurs();
            } else {
                afficherMessage("Erreur lors de la suppression de l'utilisateur", "#e74c3c");
            }
        }
    }

    @FXML
    public void filtrerParRole(ActionEvent event) {
        Button boutonClique = (Button) event.getSource();
        String roleFiltre = (String) boutonClique.getUserData();
        
        // Réinitialiser les couleurs de tous les boutons de filtre
        reinitialiserCouleursFiltres();
        
        // Mettre en évidence le bouton sélectionné
        boutonClique.setStyle("-fx-background-color: #3498db; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        
        if (roleFiltre != null) {
            ObservableList<User> utilisateursFiltres = FXCollections.observableArrayList();
            
            for (User user : utilisateursObservable) {
                if (roleFiltre.equals(user.getRole())) {
                    utilisateursFiltres.add(user);
                }
            }
            
            utilisateursList.setItems(utilisateursFiltres);
            resultCountLabel.setText(utilisateursFiltres.size() + " " + roleFiltre.toLowerCase());
            afficherMessage("Filtre: " + roleFiltre + " (" + utilisateursFiltres.size() + " utilisateur" + (utilisateursFiltres.size() > 1 ? "s" : "") + ")", "#3498db");
        }
    }
    
    private void reinitialiserCouleursFiltres() {
        // Cette méthode sera appelée depuis le contrôleur FXML si nécessaire
        // Pour l'instant, les couleurs sont gérées directement dans filtrerParRole
    }

    @FXML
    public void afficherTous(ActionEvent event) {
        utilisateursList.setItems(utilisateursObservable);
        resultCountLabel.setText("");
        
        // Réinitialiser les couleurs des boutons de filtre
        reinitialiserCouleursFiltres();
        
        afficherMessage("Affichage de tous les utilisateurs", "#2ecc71");
    }

    @FXML
    public void chargerUtilisateurs(ActionEvent event) {
        chargerUtilisateurs();
    }
    
    // Méthode de test pour ajouter des utilisateurs factices
    @FXML
    public void chargerUtilisateursTest(ActionEvent event) {
        try {
            System.out.println("🧪 Test avec utilisateurs factices...");
            
            // Créer des utilisateurs factices pour tester
            ObservableList<User> testUsers = FXCollections.observableArrayList();
            testUsers.add(new User(1, "Dupont", "Jean", "jean.dupont@email.com", "mdp123", "ADMIN"));
            testUsers.add(new User(2, "Martin", "Sophie", "sophie.martin@email.com", "mdp456", "MEDECIN"));
            testUsers.add(new User(3, "Bernard", "Pierre", "pierre.bernard@email.com", "mdp789", "INFIRMIER"));
            testUsers.add(new User(4, "Petit", "Marie", "marie.petit@email.com", "mdp012", "SECRETAIRE"));
            
            // Vider et recharger avec les données de test
            utilisateursObservable.clear();
            utilisateursObservable.addAll(testUsers);
            
            System.out.println("🧪 " + testUsers.size() + " utilisateurs factices ajoutés");
            
            // Forcer la mise à jour de la ListView (SIMPLE)
            System.out.println("🧪 Début de la mise à jour de la ListView (TEST)...");
            System.out.println("   - Nombre d'items dans testUsers: " + testUsers.size());
            System.out.println("   - Nombre d'items dans ListView avant: " + utilisateursList.getItems().size());
            
            utilisateursList.setItems(utilisateursObservable);
            System.out.println("   - Liste observable réassignée (TEST)");
            
            System.out.println("   - Nombre d'items dans ListView après: " + utilisateursList.getItems().size());
            System.out.println("🧪 ListView mise à jour avec les données de test");
            
            // Mettre à jour les labels
            if (totalUsersLabel != null) {
                totalUsersLabel.setText("Total (TEST): " + testUsers.size() + " utilisateur" + (testUsers.size() > 1 ? "s" : ""));
            }
            
            afficherMessage("🧪 Mode test activé - " + testUsers.size() + " utilisateurs factices chargés", "#9b59b6");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test: " + e.getMessage());
            e.printStackTrace();
            afficherMessage("❌ Erreur de test: " + e.getMessage(), "#dc3545");
        }
    }

    @FXML
    public void viderChamps(ActionEvent event) {
        viderChampsAction();
    }

    private void viderChampsAction() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        mdpField.clear();
        roleComboBox.setValue(null);
        rechercheField.clear();
        
        utilisateurSelectionne = null;
        utilisateursList.getSelectionModel().clearSelection();
        
        // Réinitialiser le mode du formulaire
        if (formModeLabel != null) {
            formModeLabel.setText("Mode: Ajout");
        }
        if (resultCountLabel != null) {
            resultCountLabel.setText("");
        }
        
        // Réactiver/désactiver les boutons
        ajouterButton.setDisable(false);
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        
        // Recharger la liste complète
        utilisateursList.setItems(utilisateursObservable);
    }

    private boolean validerChamps() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String mdp = mdpField.getText();
        String role = roleComboBox.getValue();
        
        if (nom.isEmpty()) {
            afficherMessage("Le nom est obligatoire", "#e74c3c");
            return false;
        }
        
        if (prenom.isEmpty()) {
            afficherMessage("Le prénom est obligatoire", "#e74c3c");
            return false;
        }
        
        if (email.isEmpty()) {
            afficherMessage("L'email est obligatoire", "#e74c3c");
            return false;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            afficherMessage("L'email n'est pas valide", "#e74c3c");
            return false;
        }
        
        if (utilisateurSelectionne == null && mdp.isEmpty()) {
            afficherMessage("Le mot de passe est obligatoire pour un nouvel utilisateur", "#e74c3c");
            return false;
        }
        
        if (!mdp.isEmpty() && mdp.length() < 4) {
            afficherMessage("Le mot de passe doit contenir au moins 4 caractères", "#e74c3c");
            return false;
        }
        
        if (role == null) {
            afficherMessage("Veuillez sélectionner un rôle", "#e74c3c");
            return false;
        }
        
        return true;
    }

    private void afficherMessage(String message, String couleur) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + couleur + "; -fx-font-size: 14px; -fx-wrap-text: true; -fx-font-weight: bold;");
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
            NavigationHelper.versCommandes();
        } catch (Exception e) {
            System.err.println("Erreur navigation vers commandes: " + e.getMessage());
        }
    }

    @FXML
    public void versPlanning(ActionEvent event) {
        try {
            StartApplication.changeScene("planningView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers planning: " + e.getMessage());
        }
    }

    @FXML
    public void versFicheProduit(ActionEvent event) {
        try {
            StartApplication.changeScene("ficheProduitView");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers catalogue: " + e.getMessage());
        }
    }

    @FXML
    public void versUtilisateurs(ActionEvent event) {
        // Déjà sur la page utilisateurs
        System.out.println("Déjà sur la page utilisateurs");
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
