package dk.easv.weblager.be;

/**
 * The role of a user. Determines which dashboard they see after login.
 */
public enum Role {
    ADMIN,
    OPERATOR;

    public String display() {
        if (this == ADMIN) {
            return "Admin";
        }
        return "Operator";
    }
}
