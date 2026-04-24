package dk.easv.weblager.dal;

import dk.easv.weblager.be.User;

import java.util.Optional;

/**
 * Data Access Object for users.
 * The DAL (Data Access Layer) is responsible for reading/writing data.
 * We use an interface so the rest of the app doesn't care whether the
 * data comes from a mock, a file, or a real database — it just calls
 * the methods defined here.
 */
public interface UserDAO {

    /**
     * Looks up a user by credentials.
     * @return the matching user, or empty if username/password is wrong.
     */
    Optional<User> authenticate(String username, String password);
}
