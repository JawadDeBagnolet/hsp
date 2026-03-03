package appli.hsp.utils;

import appli.hsp.exception.ErrorCode;
import appli.hsp.exception.HSPException;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire centralisé des erreurs pour l'application HSP.
 * Fournit un logging structuré et des alertes utilisateur appropriées.
 */
public class ErrorHandler {
    
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Gère une exception de manière centralisée.
     * @param exception L'exception à gérer
     * @param context Contexte de l'erreur (ex: "Navigation vers patients")
     * @param showToUser Si vrai, affiche une alerte à l'utilisateur
     */
    public static void handleException(Exception exception, String context, boolean showToUser) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        
        // Logging détaillé
        if (exception instanceof HSPException) {
            HSPException hspException = (HSPException) exception;
            LOGGER.log(Level.SEVERE, 
                "[{0}] {1} - Code: {2} - Message: {3}", 
                new Object[]{timestamp, context, hspException.getErrorCode(), hspException.getMessage()});
            LOGGER.log(Level.FINE, "Stack trace:", exception);
            
            if (showToUser) {
                showErrorAlert(hspException.getUserMessage(), context, hspException);
            }
        } else {
            LOGGER.log(Level.SEVERE, 
                "[{0}] {1} - Erreur système: {2}", 
                new Object[]{timestamp, context, exception.getMessage()});
            LOGGER.log(Level.FINE, "Stack trace:", exception);
            
            if (showToUser) {
                showErrorAlert("Une erreur technique est survenue", context, exception);
            }
        }
    }
    
    /**
     * Gère une exception avec affichage utilisateur par défaut.
     */
    public static void handleException(Exception exception, String context) {
        handleException(exception, context, true);
    }
    
    /**
     * Affiche une alerte d'erreur à l'utilisateur.
     */
    private static void showErrorAlert(String userMessage, String context, Exception exception) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Erreur - HSP");
            alert.setHeaderText(userMessage);
            
            String contentText = String.format("Contexte: %s", context);
            if (exception instanceof HSPException) {
                HSPException hspException = (HSPException) exception;
                contentText += String.format("\nCode erreur: %s", hspException.getErrorCode().getCode());
            }
            
            alert.setContentText(contentText);
            
            // Ajout du bouton pour voir les détails techniques
            ButtonType detailsButton = new ButtonType("Détails techniques");
            alert.getButtonTypes().add(detailsButton);
            
            // Personnalisation de l'action pour le bouton détails
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == detailsButton) {
                    showTechnicalDetails(exception, context);
                }
            });
        });
    }
    
    /**
     * Affiche les détails techniques de l'erreur.
     */
    private static void showTechnicalDetails(Exception exception, String context) {
        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Détails techniques - HSP");
        detailsAlert.setHeaderText("Informations techniques");
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        pw.println("=== INFORMATIONS D'ERREUR ===");
        pw.println("Timestamp: " + LocalDateTime.now().format(TIMESTAMP_FORMAT));
        pw.println("Contexte: " + context);
        pw.println("Type d'exception: " + exception.getClass().getSimpleName());
        
        if (exception instanceof HSPException) {
            HSPException hspException = (HSPException) exception;
            pw.println("Code erreur: " + hspException.getErrorCode().getCode());
            pw.println("Description: " + hspException.getErrorCode().getDescription());
            pw.println("Message utilisateur: " + hspException.getUserMessage());
        }
        
        pw.println("\n=== MESSAGE D'ERREUR ===");
        pw.println(exception.getMessage());
        
        pw.println("\n=== STACK TRACE ===");
        exception.printStackTrace(pw);
        
        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(600);
        
        detailsAlert.getDialogPane().setContent(textArea);
        detailsAlert.setResizable(true);
        detailsAlert.showAndWait();
    }
    
    /**
     * Affiche une alerte d'information.
     */
    public static void showInfoAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information - HSP");
            alert.setHeaderText(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Affiche une alerte de confirmation.
     */
    public static boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation - HSP");
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    /**
     * Affiche une alerte d'avertissement.
     */
    public static void showWarningAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement - HSP");
            alert.setHeaderText(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Crée une HSPException pour les erreurs de navigation.
     */
    public static HSPException createNavigationException(String message, String fxmlFile) {
        return new HSPException(
            ErrorCode.FXML_NOT_FOUND, 
            message,
            new RuntimeException("Fichier FXML recherché: " + fxmlFile)
        );
    }
    
    /**
     * Crée une HSPException pour les erreurs de données.
     */
    public static HSPException createDataException(String message, Throwable cause) {
        return new HSPException(ErrorCode.DATA_ACCESS_ERROR, message, cause);
    }
}
