package appli.hsp.exception;

/**
 * Exception personnalisée pour les erreurs métier de l'application LPRS.
 * Permet de différencier les erreurs applicatives des erreurs système.
 */
public class LPRSException extends Exception {
    
    private final ErrorCode errorCode;
    private final String userMessage;
    
    public LPRSException(ErrorCode errorCode, String userMessage) {
        super(userMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public LPRSException(ErrorCode errorCode, String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    @Override
    public String toString() {
        return "LPRSException{" +
                "errorCode=" + errorCode +
                ", userMessage='" + userMessage + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
