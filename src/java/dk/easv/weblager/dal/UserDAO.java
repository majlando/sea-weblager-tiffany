package dk.easv.weblager.dal;

import dk.easv.weblager.be.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for users.
 * The DAL (Data Access Layer) is responsible for reading/writing data.
 * We use an interface so the rest of the app doesn't care whether the
 * data comes from a mock, a file, or a real database — it just calls
 * the methods defined here.
 */
public interface UserDAO {

    /** @return all users currently stored. */
    List<User> getAllUsers();

    /** @return the matching user, or empty if no user has this username. */
    Optional<User> findByUsername(String username);

    /** @return the matching user, or empty if username/password is wrong. */
    Optional<User> authenticate(String username, String password);

    /** Inserts the user and assigns an id. Returns the stored user. */
    User createUser(User user);

    /** Updates the user identified by {@code user.getId()}. Returns the stored user. */
    User updateUser(User user);

    /** Removes the user identified by {@code user.getId()}. */
    void deleteUser(User user);
}
