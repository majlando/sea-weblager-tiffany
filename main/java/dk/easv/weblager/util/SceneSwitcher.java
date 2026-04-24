package dk.easv.weblager.util;

import dk.easv.weblager.gui.Stylesheets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Small helper that loads an FXML file, applies the app stylesheet,
 * and puts it on the given stage.
 */
public final class SceneSwitcher {

    private SceneSwitcher() {}

    public static FXMLLoader switchTo(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            scene.getStylesheets().add(Stylesheets.main());
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        return loader;
    }
}
