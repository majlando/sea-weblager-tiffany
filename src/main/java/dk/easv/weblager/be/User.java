package dk.easv.weblager.be;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Business Entity representing a user account.
 * BE classes are plain data holders — no business logic, no database code.
 * They are passed between the layers (DAL → BLL → GUI).
 */
public class User {
    private int id;
    private String username;
    private String password;
    private Role role;
    private final LocalDateTime createdAt;

    /** Constructor used when creating a brand-new user (DAO assigns the id). */
    public User(int id, String username, String password, Role role) {
        this(id, username, password, role, LocalDateTime.now());
    }

    /** Constructor used when reading a user back from storage with a known createdAt. */
    public User(int id, String username, String password, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }

    public String getUsername()          { return username; }
    public void setUsername(String v)    { this.username = v; }

    public String getPassword()          { return password; }
    public void setPassword(String v)    { this.password = v; }

    public Role getRole()                { return role; }
    public void setRole(Role role)       { this.role = role; }

    public LocalDateTime getCreatedAt()  { return createdAt; }

    /** Two users are equal if they share an id — convenient for list updates. */
    @Override public boolean equals(Object o) {
        return (o instanceof User u) && u.id == id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
