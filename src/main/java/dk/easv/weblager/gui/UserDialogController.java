package dk.easv.weblager.gui;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.UserManager;
import dk.easv.weblager.bll.exceptions.UserManagementException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Modal dialog for creating or editing a user.
 *
 * The controller has two "modes" — create and edit — set through
 * {@link #configureForCreate()} / {@link #configureForEdit(User)} before
 * the dialog is shown. Use the static helpers from the outside:
 *
 *   Optional<User> created = UserDialogController.showCreate(owner);
 *   Optional<User> edited  = UserDialogController.showEdit(owner, user);
 *
 * They block until the dialog is closed and return empty if the user
 * cancelled.
 */
public class UserDialogController {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final UserManager userManager = AppContext.get().userManager();

    private User editingUser;   // null in create mode
    private User savedUser;     // result — filled in by onSave()

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList(Role.values()));
        roleCombo.setValue(Role.OPERATOR);
        hideError();
    }

    // ── Mode setup ──────────────────────────────────────────────────────
    private void configureForCreate() {
        titleLabel.setText("Create User");
        saveButton.setText("Create");
        passwordField.setPromptText("Password");
    }

    private void configureForEdit(User user) {
        this.editingUser = user;
        titleLabel.setText("Edit User");
        saveButton.setText("Save");
        usernameField.setText(user.getUsername());
        roleCombo.setValue(user.getRole());
        passwordField.setPromptText("Leave blank to keep current password");
    }

    // ── Button handlers ─────────────────────────────────────────────────
    @FXML
    private void onSave() {
        hideError();
        try {
            if (editingUser == null) {
                savedUser = userManager.createUser(
                        usernameField.getText(),
                        passwordField.getText(),
                        roleCombo.getValue());
            } else {
                editingUser.setUsername(usernameField.getText());
                editingUser.setRole(roleCombo.getValue());
                if (!passwordField.getText().isEmpty()) {
                    editingUser.setPassword(passwordField.getText());
                }
                savedUser = userManager.updateUser(editingUser);
            }
            close();
        } catch (UserManagementException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    public Optional<User> getResult() {
        return Optional.ofNullable(savedUser);
    }

    // ── Public entry points ─────────────────────────────────────────────
    public static Optional<User> showCreate(Window owner) {
        return show(owner, UserDialogController::configureForCreate);
    }

    public static Optional<User> showEdit(Window owner, User user) {
        return show(owner, c -> c.configureForEdit(user));
    }

    // ── Internals ───────────────────────────────────────────────────────
    private static Optional<User> show(Window owner, Consumer<UserDialogController> configure) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    UserDialogController.class.getResource("/dk/easv/weblager/gui/user-dialog-view.fxml"));
            Parent root = loader.load();
            UserDialogController controller = loader.getController();
            configure.accept(controller);

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Stylesheets.main());
            stage.setScene(scene);
            stage.showAndWait();

            return controller.getResult();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load user dialog", e);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void close() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
