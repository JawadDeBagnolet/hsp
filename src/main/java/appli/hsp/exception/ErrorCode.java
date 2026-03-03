package appli.hsp.exception;

/**
 * Codes d'erreur pour l'application HSP.
 * Permet de classifier et traiter différemment les types d'erreurs.
 */
public enum ErrorCode {
    
    // Erreurs de navigation
    NAVIGATION_ERROR("NAV_001", "Erreur de navigation"),
    FXML_NOT_FOUND("NAV_002", "Fichier FXML introuvable"),
    SCENE_CHANGE_FAILED("NAV_003", "Échec du changement de scène"),
    
    // Erreurs de données
    DATA_ACCESS_ERROR("DATA_001", "Erreur d'accès aux données"),
    DATA_VALIDATION_ERROR("DATA_002", "Erreur de validation des données"),
    DATA_NOT_FOUND("DATA_003", "Donnée non trouvée"),
    
    // Erreurs métier
    BUSINESS_ERROR("BIZ_001", "Erreur métier"),
    AUTHENTICATION_ERROR("BIZ_002", "Erreur d'authentification"),
    AUTHORIZATION_ERROR("BIZ_003", "Erreur d'autorisation"),
    
    // Erreurs système
    SYSTEM_ERROR("SYS_001", "Erreur système"),
    CONFIGURATION_ERROR("SYS_002", "Erreur de configuration"),
    NETWORK_ERROR("SYS_003", "Erreur réseau"),
    
    // Erreurs utilisateur
    USER_INPUT_ERROR("USER_001", "Erreur de saisie utilisateur"),
    FILE_OPERATION_ERROR("USER_002", "Erreur d'opération fichier");
    
    private final String code;
    private final String description;
    
    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return code + " - " + description;
    }
}
