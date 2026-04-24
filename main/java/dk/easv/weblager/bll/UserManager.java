package dk.easv.weblager.bll;

import dk.easv.weblager.be.User;
import dk.easv.weblager.dal.MockUserDAO;
import dk.easv.weblager.dal.UserDAO;

import java.util.Optional;

/**
 * Business Logic Layer for user operations.
 * The BLL sits between the GUI and the DAL. Controllers call the manager
 * (never the DAO directly), which keeps business rules in one place and
 * keeps the GUI free of data-access details.
 */
public class UserManager {

    private final UserDAO userDAO = new MockUserDAO();

    public Optional<User> authenticate(String username, String password) {
        return userDAO.authenticate(username, password);
    }
}
