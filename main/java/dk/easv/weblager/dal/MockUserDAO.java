package dk.easv.weblager.dal;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;

import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of {@link UserDAO} with two hard-coded test users.
 * Useful while the real database isn't ready yet — the rest of the app can
 * still be developed and tested against this mock.
 */
public class MockUserDAO implements UserDAO {

    private final List<User> users = List.of(
            new User("admin",    "admin1234",    Role.ADMIN),
            new User("operator", "operator1234", Role.OPERATOR)
    );

    @Override
    public Optional<User> authenticate(String username, String password) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username)
                          && u.getPassword().equals(password))
                .findFirst();
    }
}
