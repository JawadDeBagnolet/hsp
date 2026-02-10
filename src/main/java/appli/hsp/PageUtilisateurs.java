package appli.hsp;

import appli.StartApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modele.User;
import repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class PageUtilisateurs {

    @FXML
    private TableView<User> utilisateursTable;
    
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
    private Label totalUsersLabel;
    
    @FXML
    private Label messageLabel;
    
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

    private UserRepository userRepository;
    private ObservableList<User> utilisateursList;
    private User utilisateurSelectionne;

    @FXML
    public void initialize() {
        userRepository = new UserRepository();
        utilisateursList = FXCollections.observableArrayList();
        
        // Initialiser les colonnes du tableau
        configurerTableau();
        
        // Initialiser la liste des rôles
        configurerRoles();
        
        // Charger les utilisateurs
        chargerUtilisateurs();
        
        // Configurer la sélection dans le tableau
        configurerSelectionTableau();
        
        // Configurer la recherche
        configurerRecherche();
        
        // Afficher les utilisateurs en console pour le débogage
        afficherUtilisateursConsole();
    }

    private void configurerTableau() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        utilisateursTable.setItems(utilisateursList);
    }

    private void configurerRoles() {
        roleComboBox.setItems(FXCollections.observableArrayList(
            "ADMIN", 
            "MEDECIN", 
            "INFIRMIER", 
            "SECRETAIRE", 
            "GESTIONNAIRE_DE_STOCK"
        ));
        roleComboBox.setPromptText("Sélectionner un rôle");
    }

    private void afficherUtilisateursConsole() {
        System.out.println("\n=== UTILISATEURS ENREGISTRÉS DANS LA BASE DE DONNÉES ===");
        System.out.println("ID\tNOM\t\tPRÉNOM\t\tEMAIL\t\t\tRÔLE");
        System.out.println("----------------------------------------------------------------------------------------");
        
        for (User user : utilisateursList) {
            System.out.printf("%d\t%-10s\t%-10s\t%-20s\t%s%n", 
                user.getIdUser(), 
                user.getNom(), 
                user.getPrenom(), 
                user.getEmail(), 
                user.getRole());
        }
        
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("Total: " + utilisateursList.size() + " utilisateur(s)");
        System.out.println("\n=== RÔLES DISPONIBLES ===");
        System.out.println("ADMIN");
        System.out.println("MEDECIN");
        System.out.println("INFIRMIER");
        System.out.println("SECRETAIRE");
        System.out.println("GESTIONNAIRE_DE_STOCK");
        System.out.println("========================================\n");
    }

    private void chargerUtilisateurs() {
        List<User> users = userRepository.getAllUsers();
        utilisateursList.clear();
        utilisateursList.addAll(users);
        
        // Mettre à jour le label de statistiques
        totalUsersLabel.setText(String.valueOf(users.size()));
        
        afficherMessage("Utilisateurs chargés: " + users.size(), "#2ecc71");
    }

    private void configurerSelectionTableau() {
        utilisateursTable.getSelectionModel().selectedItemProperty().addListener(
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
            utilisateursTable.setItems(utilisateursList);
            return;
        }
        
        ObservableList<User> utilisateursFiltres = FXCollections.observableArrayList();
        String rechercheLower = recherche.toLowerCase().trim();
        
        for (User user : utilisateursList) {
            if (user.getNom().toLowerCase().contains(rechercheLower) ||
                user.getPrenom().toLowerCase().contains(rechercheLower) ||
                user.getEmail().toLowerCase().contains(rechercheLower) ||
                user.getRole().toLowerCase().contains(rechercheLower)) {
                utilisateursFiltres.add(user);
            }
        }
        
        utilisateursTable.setItems(utilisateursFiltres);
        afficherMessage("Résultats: " + utilisateursFiltres.size(), "#3498db");
    }

    private void afficherUtilisateurSelectionne() {
        if (utilisateurSelectionne != null) {
            nomField.setText(utilisateurSelectionne.getNom());
            prenomField.setText(utilisateurSelectionne.getPrenom());
            emailField.setText(utilisateurSelectionne.getEmail());
            mdpField.setText(""); // Ne pas afficher le mot de passe
            roleComboBox.setValue(utilisateurSelectionne.getRole());
            
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
        
        User nouvelUtilisateur = new User(
            nomField.getText().trim(),
            prenomField.getText().trim(),
            emailField.getText().trim(),
            mdpField.getText(),
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
        
        if (roleFiltre != null) {
            ObservableList<User> utilisateursFiltres = FXCollections.observableArrayList();
            
            for (User user : utilisateursList) {
                if (roleFiltre.equals(user.getRole())) {
                    utilisateursFiltres.add(user);
                }
            }
            
            utilisateursTable.setItems(utilisateursFiltres);
            afficherMessage("Filtre: " + roleFiltre + " (" + utilisateursFiltres.size() + " utilisateur(s))", "#3498db");
        }
    }

    @FXML
    public void afficherTous(ActionEvent event) {
        utilisateursTable.setItems(utilisateursList);
        afficherMessage("Affichage de tous les utilisateurs", "#2ecc71");
    }

    @FXML
    public void chargerUtilisateurs(ActionEvent event) {
        chargerUtilisateurs();
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
        utilisateursTable.getSelectionModel().clearSelection();
        
        // Réactiver/désactiver les boutons
        ajouterButton.setDisable(false);
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        
        // Recharger la liste complète
        utilisateursTable.setItems(utilisateursList);
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
            StartApplication.changeScene("pagePatients");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers patients: " + e.getMessage());
        }
    }

    @FXML
    public void versCommandes(ActionEvent event) {
        try {
            StartApplication.changeScene("pageCommandes");
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers commandes: " + e.getMessage());
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
