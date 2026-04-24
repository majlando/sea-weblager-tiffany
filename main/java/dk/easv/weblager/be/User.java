package dk.easv.weblager.be;

/**
 * Business Entity representing a user account.
 * BE classes are plain data holders — no business logic, no database code.
 * They are passed between the layers (DAL → BLL → GUI).
 */
public class User {
    private final String username;
    private final String password;
    private final Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole()       { return role; }
}
