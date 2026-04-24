package dk.easv.weblager.gui;

import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.UserManager;
import dk.easv.weblager.dal.MockUserDAO;

/**
 * Single shared place to get the app's services and remember who is signed in.
 * Using a singleton keeps things simple for an MVP — every controller sees the
 * same {@link UserManager} (so data written in a dialog shows up in the table)
 * and the same {@code currentUser}.
 */
public final class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private final UserManager userManager = new UserManager(new MockUserDAO());
    private User currentUser;

    private AppContext() {}

    public static AppContext get() { return INSTANCE; }

    public UserManager userManager() { return userManager; }

    public User getCurrentUser()            { return currentUser; }
    public void setCurrentUser(User user)   { this.currentUser = user; }
    public void clearCurrentUser()          { this.currentUser = null; }
}
