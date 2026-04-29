package dk.easv.weblager;

import javafx.application.Application;

/** Plain main-method wrapper so IDEs that won't run an Application class directly can still launch the app. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
