package dk.easv.weblager;

import dk.easv.weblager.util.SceneSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX entry point. Extends {@link Application}, whose {@link #start} method
 * the JavaFX runtime calls once the toolkit is ready.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneSwitcher.switchTo(stage, "/dk/easv/weblager/gui/login-view.fxml");
        stage.setTitle("Weblager");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
