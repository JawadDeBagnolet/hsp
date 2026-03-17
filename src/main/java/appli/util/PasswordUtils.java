package appli.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }

        // Si la base contient des mots de passe en clair (ou un autre format), BCrypt lèvera
        // IllegalArgumentException("Invalid salt version"). On gère les 2 cas.
        if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(password, hashedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return password.equals(hashedPassword);
    }
}