package dk.easv.weblager.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Small helper that loads an FXML file and puts it on the given stage.
 * Returns the {@link FXMLLoader} so the caller can grab the controller
 * (via {@code loader.getController()}) and pass data into it — for example
 * the logged-in user.
 */
public final class SceneSwitcher {

    private SceneSwitcher() {}

    public static FXMLLoader switchTo(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        return loader;
    }
}
