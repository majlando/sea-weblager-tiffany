package dk.easv.weblager.be;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Business Entity representing a user account.
 * Passwords are stored as plain text for now; they will
 * become BCrypt hashes when the real DAO replaces the mock.
 */

public class User {
    private int id;
    private String username;
    private String password;
    private Role role;
    private final LocalDateTime createdAt;

    public User(int id, String username, String password, Role role) {
        this(id, username, password, role, LocalDateTime.now());
    }

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        User other = (User) o;
        return other.id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
