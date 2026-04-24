package dk.easv.weblager;

import javafx.application.Application;

/**
 * Some IDEs refuse to run a class that extends {@link Application} directly.
 * This wrapper gives them a plain main method to launch from.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
