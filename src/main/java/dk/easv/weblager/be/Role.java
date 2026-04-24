package dk.easv.weblager.be;

/**
 * The role of a user. Determines which dashboard they see after login.
 */
public enum Role {
    ADMIN,
    OPERATOR;

    /** Human-readable name used in labels and table cells. */
    public String display() {
        return switch (this) {
            case ADMIN    -> "Admin";
            case OPERATOR -> "Operator";
        };
    }
}
