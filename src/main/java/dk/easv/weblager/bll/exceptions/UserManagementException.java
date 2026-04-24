package dk.easv.weblager.bll.exceptions;

/**
 * Thrown by {@link dk.easv.weblager.bll.UserManager} when a business rule
 * is violated (blank fields, duplicate username, deleting yourself, etc.).
 * The message is user-facing — controllers show it directly.
 */
public class UserManagementException extends RuntimeException {
    public UserManagementException(String message) {
        super(message);
    }
}
