package dk.easv.weblager;

import dk.easv.weblager.util.SceneSwitcher;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * JavaFX entry point. Extends {@link Application}, whose {@link #start} method
 * the JavaFX runtime calls once the toolkit is ready.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        loadFonts();
        SceneSwitcher.switchTo(stage, "/dk/easv/weblager/gui/login-view.fxml");
        stage.setTitle("Weblager");
        loadAppIcon().ifPresent(stage.getIcons()::add);
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
            if (in != null) Font.loadFont(in, 12);
        } catch (Exception ignored) {
        }
    }

    private java.util.Optional<Image> loadAppIcon() {
        try (InputStream in = Main.class.getResourceAsStream(
                "/dk/easv/weblager/gui/assets/images/app-icon.png")) {
            return (in == null) ? java.util.Optional.empty() : java.util.Optional.of(new Image(in));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
