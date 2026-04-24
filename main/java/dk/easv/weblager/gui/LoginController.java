package dk.easv.weblager.gui;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.UserManager;
import dk.easv.weblager.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Controller for the login view.
 * The @FXML fields are filled in automatically by JavaFX — each one is
 * matched to an element in login-view.fxml by its fx:id. The onLogin()
 * method is wired to the Login button's onAction in the same file.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserManager userManager = new UserManager();

    @FXML
    private void onLogin() throws Exception {
        Optional<User> result = userManager.authenticate(
                usernameField.getText(),
                passwordField.getText());

        if (result.isEmpty()) {
            errorLabel.setText("Invalid username or password.");
            return;
        }

        goToDashboard(result.get());
    }

    private void goToDashboard(User user) throws Exception {
        String fxml = (user.getRole() == Role.ADMIN)
                ? "/dk/easv/weblager/gui/admin-dashboard-view.fxml"
                : "/dk/easv/weblager/gui/scanning-dashboard-view.fxml";

        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneSwitcher.switchTo(stage, fxml);
    }
}
