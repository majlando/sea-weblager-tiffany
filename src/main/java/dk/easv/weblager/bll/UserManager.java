package dk.easv.weblager.bll;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.exceptions.UserManagementException;
import dk.easv.weblager.dal.UserDAO;

import java.util.List;
import java.util.Optional;

/**
 * Business Logic Layer for user operations.
 * The BLL sits between the GUI and the DAL. Controllers call the manager
 * (never the DAO directly), which keeps business rules in one place and
 * keeps the GUI free of data-access details.
 *
 * Rules enforced here:
 *   - Usernames must be non-blank, unique, and case-insensitive.
 *   - Passwords must be non-blank when creating.
 *   - An admin cannot delete themselves.
 *   - The last remaining admin cannot be deleted.
 */
public class UserManager {

    private final UserDAO dao;

    /** Constructor injection — tests and main wiring decide which DAO to use. */
    public UserManager(UserDAO dao) {
        this.dao = dao;
    }

    // ── Authentication ──────────────────────────────────────────────────
    public Optional<User> authenticate(String username, String password) {
        return dao.authenticate(
                username == null ? "" : username.trim(),
                password == null ? "" : password);
    }

    // ── Read ────────────────────────────────────────────────────────────
    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    public long countAdmins() {
        return dao.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .count();
    }

    // ── Create ──────────────────────────────────────────────────────────
    public User createUser(String username, String password, Role role) {
        String cleanUsername = requireNonBlank(username, "Username is required.");
        String cleanPassword = requireNonBlank(password, "Password is required.");
        if (role == null) throw new UserManagementException("Role is required.");

        if (dao.findByUsername(cleanUsername).isPresent()) {
            throw new UserManagementException(
                    "Username \"" + cleanUsername + "\" is already taken.");
        }
        return dao.createUser(new User(0, cleanUsername, cleanPassword, role));
    }

    // ── Update ──────────────────────────────────────────────────────────
    public User updateUser(User edited) {
        if (edited == null) throw new UserManagementException("No user selected.");

        String cleanUsername = requireNonBlank(edited.getUsername(), "Username is required.");
        if (edited.getRole() == null) throw new UserManagementException("Role is required.");

        boolean duplicate = dao.getAllUsers().stream()
                .anyMatch(u -> u.getId() != edited.getId()
                        && u.getUsername().equalsIgnoreCase(cleanUsername));
        if (duplicate) {
            throw new UserManagementException(
                    "Username \"" + cleanUsername + "\" is already taken.");
        }

        edited.setUsername(cleanUsername);
        return dao.updateUser(edited);
    }

    // ── Delete ──────────────────────────────────────────────────────────
    public void deleteUser(User target, User currentUser) {
        if (target == null) throw new UserManagementException("No user selected.");
        if (currentUser != null && currentUser.getId() == target.getId()) {
            throw new UserManagementException("You cannot delete your own account.");
        }
        if (target.getRole() == Role.ADMIN && countAdmins() <= 1) {
            throw new UserManagementException("Cannot delete the last remaining Admin.");
        }
        dao.deleteUser(target);
    }

    // ── Helpers ─────────────────────────────────────────────────────────
    private static String requireNonBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new UserManagementException(message);
        }
        return value.trim();
    }
}
