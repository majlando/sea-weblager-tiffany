package dk.easv.weblager.gui;

import dk.easv.weblager.be.Role;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.UserManager;
import dk.easv.weblager.bll.exceptions.UserManagementException;
import dk.easv.weblager.util.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Admin screen — CRUD for users.
 *
 * Table pipeline: the DAL's list is copied into {@link #allUsers}. A
 * {@link FilteredList} narrows it by search text + role before the table
 * displays it. Whenever the search field or role filter changes we just
 * update the predicate; the table re-renders automatically.
 *
 * Validation lives in {@link UserManager}. This controller only collects
 * input, calls the manager, and shows any error message back to the user.
 */
public class AdminDashboardController {

    private static final String FILTER_ALL = "All";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label welcomeLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colCreated;

    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private Label feedbackLabel;

    private final UserManager userManager = AppContext.get().userManager();

    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final FilteredList<User> filteredUsers = new FilteredList<>(allUsers, u -> true);

    @FXML
    private void initialize() {
        // Tell each column where to read its text from.
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colRole.setCellValueFactory    (c -> new SimpleStringProperty(c.getValue().getRole().display()));
        colCreated.setCellValueFactory (c -> new SimpleStringProperty(DATE_FORMAT.format(c.getValue().getCreatedAt())));

        userTable.setItems(filteredUsers);

        // Role filter: "All" + every real role.
        roleFilter.setItems(FXCollections.observableArrayList(
                FILTER_ALL, Role.ADMIN.display(), Role.OPERATOR.display()));
        roleFilter.setValue(FILTER_ALL);

        // Re-apply the filter whenever the inputs change.
        searchField.textProperty().addListener((o, a, b) -> applyFilter());
        roleFilter.valueProperty().addListener((o, a, b) -> applyFilter());

        // Edit/Delete only make sense with a selected row.
        userTable.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> refreshButtonStates());

        // Greet the signed-in admin.
        User me = AppContext.get().getCurrentUser();
        if (me != null) {
            welcomeLabel.setText("Signed in as " + me.getUsername() + " (" + me.getRole().display() + ")");
        }

        reloadUsers();
        refreshButtonStates();
        hideFeedback();
    }

    // ── Handlers ────────────────────────────────────────────────────────
    @FXML
    private void onCreate() {
        hideFeedback();
        Optional<User> created = UserDialogController.showCreate(userTable.getScene().getWindow());
        if (created.isPresent()) {
            reloadUsers();
            showSuccess("User created.");
        }
    }

    @FXML
    private void onEdit() {
        hideFeedback();
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Optional<User> edited = UserDialogController.showEdit(userTable.getScene().getWindow(), selected);
        if (edited.isPresent()) {
            reloadUsers();
            showSuccess("User updated.");
        }
    }

    @FXML
    private void onDelete() {
        hideFeedback();
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(AlertType.CONFIRMATION,
                "Delete user \"" + selected.getUsername() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm Delete");

        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isEmpty() || answer.get() != ButtonType.OK) return;

        try {
            userManager.deleteUser(selected, AppContext.get().getCurrentUser());
            reloadUsers();
            showSuccess("User deleted.");
        } catch (UserManagementException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onLogout() throws Exception {
        AppContext.get().clearCurrentUser();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "/dk/easv/weblager/gui/login-view.fxml");
    }

    // ── Filtering ───────────────────────────────────────────────────────
    private void applyFilter() {
        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String roleChoice = roleFilter.getValue();

        filteredUsers.setPredicate(user -> {
            boolean matchesName = searchText.isEmpty()
                    || user.getUsername().toLowerCase().contains(searchText);
            boolean matchesRole = roleChoice == null
                    || FILTER_ALL.equals(roleChoice)
                    || user.getRole().display().equals(roleChoice);
            return matchesName && matchesRole;
        });
    }

    // ── Helpers ─────────────────────────────────────────────────────────
    private void reloadUsers() {
        allUsers.setAll(userManager.getAllUsers());
    }

    private void refreshButtonStates() {
        boolean hasSelection = userTable.getSelectionModel().getSelectedItem() != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    private void showSuccess(String message) {
        feedbackLabel.getStyleClass().removeAll("error-label", "success-label");
        feedbackLabel.getStyleClass().add("success-label");
        feedbackLabel.setText(message);
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }

    private void showError(String message) {
        feedbackLabel.getStyleClass().removeAll("error-label", "success-label");
        feedbackLabel.getStyleClass().add("error-label");
        feedbackLabel.setText(message);
        feedbackLabel.setVisible(true);
        feedbackLabel.setManaged(true);
    }

    private void hideFeedback() {
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);
    }
}
