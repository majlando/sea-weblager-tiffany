package dk.easv.weblager.bll;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.exceptions.UserManagementException;
import dk.easv.weblager.dal.MockUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for UserManager.
 *
 * Each test follows the Arrange-Act-Assert (AAA) pattern:
 *   Arrange — set up the data (done in setUp() with a new MockUserDAO).
 *   Act     — call the UserManager method under test.
 *   Assert  — verify the result with an assert*() call.
 */
class UserManagerTest {

    private UserManager manager;
    private User admin;
    private User operator;

    // New DAO + manager before every test so tests stay independent.
    @BeforeEach
    void setUp() {
        MockUserDAO dao = new MockUserDAO();   // seeds admin (id=1) and operator (id=2)
        manager = new UserManager(dao);
        List<User> seeded = dao.getAllUsers();
        admin = seeded.get(0);
        operator = seeded.get(1);
    }

    @Test
    void authenticateValid() {
        assertTrue(manager.authenticate("admin", "admin1234").isPresent());
    }

    @Test
    void authenticateWrong() {
        assertTrue(manager.authenticate("admin", "wrong").isEmpty());
    }

    @Test
    void createValid() {
        User created = manager.createUser("alice", "secret123", Role.OPERATOR);
        assertEquals("alice", created.getUsername());
        assertEquals(3, manager.getAllUsers().size());
    }

    @Test
    void createBlankUsername() {
        assertThrows(UserManagementException.class,
                () -> manager.createUser("  ", "secret123", Role.OPERATOR));
    }

    // Usernames are case-insensitive, so "ADMIN" clashes with the seeded "admin".
    @Test
    void createDuplicate() {
        assertThrows(UserManagementException.class,
                () -> manager.createUser("ADMIN", "secret123", Role.OPERATOR));
    }

    // deleteUser(target, currentUser) — second arg is the logged-in user.
    @Test
    void deleteSelf() {
        assertThrows(UserManagementException.class,
                () -> manager.deleteUser(admin, admin));
    }

    @Test
    void deleteLastAdmin() {
        assertThrows(UserManagementException.class,
                () -> manager.deleteUser(admin, operator));
    }

    @Test
    void deleteOperator() {
        manager.deleteUser(operator, admin);
        assertEquals(1, manager.getAllUsers().size());
    }
}
