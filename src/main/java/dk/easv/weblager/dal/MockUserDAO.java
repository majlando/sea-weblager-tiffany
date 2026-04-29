package dk.easv.weblager.dal;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Sprint 1 — in-memory stand-in for the real database.
 *
 * Two hard-coded users live in an ArrayList, so the rest of the app
 * (login, admin CRUD, etc.) can be built and tested without waiting for the database.
 *
 * This class will be replaced with a database-backed UserDAO
 * (MS SQL Server) and store password hashes.
 */
public class MockUserDAO implements UserDAO {

    private final List<User> users = new ArrayList<>();
    private int nextId = 0;

    public MockUserDAO() {
        users.add(new User(++nextId, "admin",    "admin1234",    Role.ADMIN));
        users.add(new User(++nextId, "operator", "operator1234", Role.OPERATOR));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        return findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }

    @Override
    public User createUser(User user) {
        user.setId(++nextId);
        users.add(user);
        return user;
    }

    @Override
    public User updateUser(User updated) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updated.getId()) {
                users.set(i, updated);
                return updated;
            }
        }
        throw new IllegalArgumentException("No user with id=" + updated.getId());
    }

    @Override
    public void deleteUser(User user) {
        users.removeIf(u -> u.getId() == user.getId());
    }
}
