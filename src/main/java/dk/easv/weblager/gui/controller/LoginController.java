package dk.easv.weblager.gui.controller;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.UserManager;
import dk.easv.weblager.gui.AppContext;
import dk.easv.weblager.gui.control.BrandLogo;
import dk.easv.weblager.gui.control.MaskedPasswordSkin;
import dk.easv.weblager.util.SceneSwitcher;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Controller for the login view.
 * The @FXML fields are filled in automatically by JavaFX — each one is
 * matched to an element in login-view.fxml by its fx:id. The onLogin()
 * method is wired to the Login button's onAction in the same file.
 */
public class LoginController {

    private static final PseudoClass CONTAINS_FOCUS =
            PseudoClass.getPseudoClass("contains-focus");

    @FXML private Pane logoContainer;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserManager userManager = AppContext.get().userManager();

    @FXML
    private void initialize() {
        logoContainer.getChildren().setAll(BrandLogo.create(220));
        passwordField.setSkin(new MaskedPasswordSkin(passwordField));
        trackFocus(usernameField);
        trackFocus(passwordField);
    }

    private void trackFocus(TextField field) {
        field.focusedProperty().addListener((obs, was, focused) ->
                field.getParent().pseudoClassStateChanged(CONTAINS_FOCUS, focused));
    }

    @FXML
    private void onLogin() throws Exception {
        Optional<User> result = userManager.authenticate(
                usernameField.getText(),
                passwordField.getText());

        if (result.isEmpty()) {
            errorLabel.setText("Invalid username or password.");
            return;
        }

        User user = result.get();
        AppContext.get().setCurrentUser(user);
        goToDashboard(user);
    }

    private void goToDashboard(User user) throws Exception {
        String fxml = (user.getRole() == Role.ADMIN)
                ? "/dk/easv/weblager/gui/admin-dashboard-view.fxml"
                : "/dk/easv/weblager/gui/scanning-dashboard-view.fxml";

        // Standard JavaFX trick: walk from any control up to its containing Stage.
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneSwitcher.switchTo(stage, fxml);
    }
}
