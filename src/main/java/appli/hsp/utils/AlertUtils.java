package appli.hsp.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

import java.util.Optional;

/**
 * Utilitaire pour les alertes utilisateur.
 * Fournit des méthodes simplifiées pour afficher différents types d'alertes.
 */
public class AlertUtils {
    
    /**
     * Affiche une alerte d'erreur simple.
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message, null);
    }
    
    /**
     * Affiche une alerte d'erreur avec détails.
     */
    public static void showError(String title, String message, String details) {
        showAlert(Alert.AlertType.ERROR, title, message, details);
    }
    
    /**
     * Affiche une alerte d'information.
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message, null);
    }
    
    /**
     * Affiche une alerte d'avertissement.
     */
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message, null);
    }
    
    /**
     * Affiche une alerte de confirmation et retourne la réponse.
     */
    public static boolean showConfirmation(String title, String message) {
        return showAlertAndWait(Alert.AlertType.CONFIRMATION, title, message, null);
    }
    
    /**
     * Affiche une alerte de confirmation avec message détaillé.
     */
    public static boolean showConfirmation(String title, String message, String details) {
        return showAlertAndWait(Alert.AlertType.CONFIRMATION, title, message, details);
    }
    
    /**
     * Affiche une alerte de succès.
     */
    public static void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message, null);
    }
    
    /**
     * Affiche une alerte avec exception détaillée.
     */
    public static void showException(String title, String message, Exception exception) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Erreur - LPRS");
            alert.setHeaderText(title);
            alert.setContentText(message);
            
            // Création de la zone de détails
            TextArea textArea = new TextArea(getExceptionDetails(exception));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(new Label("Détails techniques:"), 0, 0);
            expContent.add(textArea, 0, 1);
            
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            
            alert.getDialogPane().setExpandableContent(expContent);
            alert.setResizable(true);
            
            alert.showAndWait();
        });
    }
    
    /**
     * Méthode générique pour afficher une alerte.
     */
    private static void showAlert(Alert.AlertType type, String title, String message, String details) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle(getTitlePrefix(type) + " - LPRS");
            alert.setHeaderText(title);
            alert.setContentText(message);
            
            if (details != null && !details.trim().isEmpty()) {
                TextArea textArea = new TextArea(details);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setPrefHeight(150);
                
                alert.getDialogPane().setExpandableContent(textArea);
                alert.setResizable(true);
            }
            
            alert.showAndWait();
        });
    }
    
    /**
     * Méthode générique pour afficher une alerte et attendre la réponse.
     */
    private static boolean showAlertAndWait(Alert.AlertType type, String title, String message, String details) {
        Alert alert = new Alert(type);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(getTitlePrefix(type) + " - LPRS");
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        if (details != null && !details.trim().isEmpty()) {
            TextArea textArea = new TextArea(details);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(150);
            
            alert.getDialogPane().setExpandableContent(textArea);
            alert.setResizable(true);
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Retourne le préfixe du titre selon le type d'alerte.
     */
    private static String getTitlePrefix(Alert.AlertType type) {
        switch (type) {
            case ERROR: return "Erreur";
            case WARNING: return "Avertissement";
            case INFORMATION: return "Information";
            case CONFIRMATION: return "Confirmation";
            default: return "Message";
        }
    }
    
    /**
     * Extrait les détails d'une exception.
     */
    private static String getExceptionDetails(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(exception.getClass().getSimpleName()).append("\n");
        sb.append("Message: ").append(exception.getMessage()).append("\n\n");
        
        sb.append("Stack Trace:\n");
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
        }
        
        // Inclure les causes si présentes
        Throwable cause = exception.getCause();
        while (cause != null) {
            sb.append("\nCaused by: ").append(cause.getClass().getSimpleName())
              .append(": ").append(cause.getMessage()).append("\n");
            cause = cause.getCause();
        }
        
        return sb.toString();
    }
}
