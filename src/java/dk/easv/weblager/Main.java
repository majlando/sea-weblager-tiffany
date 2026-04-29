package dk.easv.weblager;

import dk.easv.weblager.util.SceneSwitcher;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * JavaFX entry point. The runtime calls start(Stage) once the toolkit is ready.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        loadFonts();
        SceneSwitcher.switchTo(stage, "/dk/easv/weblager/gui/login-view.fxml");
        stage.setTitle("Tiffany");

        Image appIcon = loadAppIcon();
        if (appIcon != null) {
            stage.getIcons().add(appIcon);
        }

        stage.show();
    }

    private void loadFonts() {
        loadFont("/dk/easv/weblager/gui/assets/fonts/Montserrat-Regular.ttf");
        loadFont("/dk/easv/weblager/gui/assets/fonts/Montserrat-Medium.ttf");
        loadFont("/dk/easv/weblager/gui/assets/fonts/Montserrat-SemiBold.ttf");
        loadFont("/dk/easv/weblager/gui/assets/fonts/Montserrat-Bold.ttf");
    }

    private void loadFont(String resource) {
        try (InputStream in = Main.class.getResourceAsStream(resource)) {
            // 12 = initial size in points; CSS overrides this per-control.
            if (in != null) Font.loadFont(in, 12);
        } catch (Exception ignored) {
            // A missing font shouldn't crash the app — JavaFX falls back to system fonts.
        }
    }

    private Image loadAppIcon() {
        try (InputStream in = Main.class.getResourceAsStream(
                "/dk/easv/weblager/gui/assets/images/app-icon.png")) {
            if (in == null) {
                return null;
            }
            return new Image(in);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
