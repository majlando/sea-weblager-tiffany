package dk.easv.weblager.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Small helper that loads an FXML file and puts it on the given stage.
 * Each FXML declares its own stylesheet, so styling travels with the view.
 */
public final class SceneSwitcher {

    private SceneSwitcher() {}

    public static FXMLLoader switchTo(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = stage.getScene();
        if (scene == null) {
            stage.setScene(new Scene(root));
        } else {
            scene.setRoot(root);
        }
        // Resize the stage to each view's preferred size — otherwise a small
        // login window would squash the larger admin dashboard, and vice versa.
        stage.sizeToScene();
        stage.centerOnScreen();
        return loader;
    }
}
