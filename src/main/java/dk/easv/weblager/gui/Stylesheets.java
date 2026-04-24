package dk.easv.weblager.gui;

import java.util.Objects;

/**
 * Central place for stylesheet URLs, so every Scene gets styled the same way
 * and we don't sprinkle hard-coded paths around the code.
 */
public final class Stylesheets {

    public static final double LOGIN_LOGO_WIDTH = 220.0;

    private Stylesheets() {}

    public static String main() {
        return url("styles.css");
    }

    private static String url(String name) {
        return Objects.requireNonNull(
                Stylesheets.class.getResource(name),
                "Stylesheet not found: " + name
        ).toExternalForm();
    }
}
