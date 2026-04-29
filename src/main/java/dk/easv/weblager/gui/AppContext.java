package dk.easv.weblager.gui;

import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.ScanningManager;
import dk.easv.weblager.bll.UserManager;
import dk.easv.weblager.dal.MockUserDAO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Singleton holding the shared UserManager / ScanningManager and the
 * currently signed-in user. Reach it via AppContext.get().
 *
 * "Singleton" = one instance, private constructor, public static getter.
 */
public final class AppContext {

    // Folder that ScanningManager reads .tiff files from.
    private static final Path SCAN_FOLDER = locateScansFolder();

    // The one and only instance. Created when the class is first loaded.
    private static final AppContext INSTANCE = new AppContext();

    private final UserManager userManager = new UserManager(new MockUserDAO());
    private final ScanningManager scanningManager = new ScanningManager(SCAN_FOLDER);
    private User currentUser;

    /**
     * Searches the working directory and a few parent directories for a
     * folder named "scans". Returns the first hit, or cwd/scans as a
     * fallback so the resulting error message points at a sensible path.
     */
    private static Path locateScansFolder() {
        Path cwd = Paths.get("").toAbsolutePath();
        Path dir = cwd;
        for (int i = 0; i < 4 && dir != null; i++) {
            Path candidate = dir.resolve("scans");
            if (Files.isDirectory(candidate)) return candidate;
            dir = dir.getParent();
        }
        return cwd.resolve("scans");
    }

    // Private constructor: callers cannot do `new AppContext()`.
    private AppContext() {}

    /** Returns the shared instance. */
    public static AppContext get() { return INSTANCE; }

    public UserManager userManager()         { return userManager; }
    public ScanningManager scanningManager() { return scanningManager; }

    public User getCurrentUser()            { return currentUser; }
    public void setCurrentUser(User user)   { this.currentUser = user; }
    public void clearCurrentUser()          { this.currentUser = null; }
}
