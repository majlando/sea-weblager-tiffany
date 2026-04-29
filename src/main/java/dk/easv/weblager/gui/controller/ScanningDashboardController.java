package dk.easv.weblager.gui.controller;

import dk.easv.weblager.be.ScanFile;
import dk.easv.weblager.be.User;
import dk.easv.weblager.bll.ScanningManager;
import dk.easv.weblager.gui.AppContext;
import dk.easv.weblager.gui.control.BrandLogo;
import dk.easv.weblager.util.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Controller for the scanning dashboard.
 *
 * Layout: a sidebar with the list of TIFF files on disk, and a preview
 * pane on the right that shows the currently selected file as a JavaFX
 * Image.
 *
 * TIFF decoding (ImageIO + TwelveMonkeys) runs on a JavaFX background
 * thread (Task) so the UI stays responsive on large files. The
 * "currentDecode" reference lets us cancel an in-flight decode when
 * the user picks a different file.
 */
public class ScanningDashboardController {

    @FXML private Pane logoContainer;
    @FXML private Label welcomeLabel;

    @FXML private Label fileCountLabel;
    @FXML private ListView<ScanFile> fileList;
    @FXML private Label statusLabel;

    @FXML private StackPane previewContainer;
    @FXML private ImageView previewView;
    @FXML private Label placeholderLabel;

    private final ScanningManager scanningManager = AppContext.get().scanningManager();
    private final ObservableList<ScanFile> files = FXCollections.observableArrayList();

    // Most-recent decode task. Used so a slow decode whose user has
    // already moved on can be cancelled / its result discarded.
    private Task<Image> currentDecode;

    @FXML
    private void initialize() {
        logoContainer.getChildren().setAll(BrandLogo.create(120));

        // Greet the signed-in user.
        User me = AppContext.get().getCurrentUser();
        if (me != null) {
            welcomeLabel.setText("Signed in as " + me.getUsername()
                    + " (" + me.getRole().display() + ")");
        }

        // The ListView shows whatever is in `files`. Selecting a row
        // triggers showPreview() with that ScanFile.
        fileList.setItems(files);
        fileList.getSelectionModel().selectedItemProperty()
                .addListener((obs, was, now) -> showPreview(now));

        // Make the ImageView fill the preview pane (minus padding).
        previewView.fitWidthProperty().bind(previewContainer.widthProperty().subtract(24));
        previewView.fitHeightProperty().bind(previewContainer.heightProperty().subtract(24));

        // Placeholder text only while no image is loaded.
        placeholderLabel.visibleProperty().bind(previewView.imageProperty().isNull());
        placeholderLabel.managedProperty().bind(placeholderLabel.visibleProperty());

        hideStatus();
        updateFileCount();
    }

    // Button handlers
    @FXML
    private void onLoadFiles() {
        hideStatus();
        try {
            List<ScanFile> loaded = scanningManager.loadFiles();
            files.setAll(loaded);
            updateFileCount();

            if (loaded.isEmpty()) {
                showStatus("No TIFF files in " + scanningManager.getScanFolder() + ".");
            } else {
                fileList.getSelectionModel().selectFirst();
            }
        } catch (RuntimeException e) {
            showStatus(e.getMessage());
        }
    }

    @FXML
    private void onLogout() throws Exception {
        AppContext.get().clearCurrentUser();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        SceneSwitcher.switchTo(stage, "/dk/easv/weblager/gui/login-view.fxml");
    }

    // Preview
    private void showPreview(ScanFile file) {
        // Cancel any decode that's still running so a fast click stream
        // doesn't end up showing a stale image.
        if (currentDecode != null) currentDecode.cancel();

        if (file == null) {
            previewView.setImage(null);
            return;
        }

        Path path = file.getPath();

        // A Task is JavaFX's helper for running work off the FX thread
        // and delivering the result back on it. call() runs on the
        // background thread; setOnSucceeded/Failed run on the FX thread.
        Task<Image> decode = new Task<>() {
            @Override
            protected Image call() throws Exception {
                try (InputStream in = Files.newInputStream(path)) {
                    // ImageIO + TwelveMonkeys decode the TIFF as a BufferedImage,
                    // SwingFXUtils bridges it across to a JavaFX Image.
                    BufferedImage buffered = ImageIO.read(in);
                    return buffered == null ? null : SwingFXUtils.toFXImage(buffered, null);
                }
            }
        };
        currentDecode = decode;

        decode.setOnSucceeded(e -> {
            // Ignore the result if a newer decode has been started since.
            if (decode != currentDecode) return;
            Image image = decode.getValue();
            if (image == null) {
                showStatus("Could not decode " + path.getFileName());
                previewView.setImage(null);
            } else {
                previewView.setImage(image);
                hideStatus();
            }
        });
        decode.setOnFailed(e -> {
            if (decode != currentDecode) return;
            Throwable ex = decode.getException();
            showStatus("Failed to read " + path.getFileName() + ": "
                    + (ex == null ? "unknown error" : ex.getMessage()));
            previewView.setImage(null);
        });

        // Daemon thread so it cannot keep the JVM alive on shutdown.
        Thread thread = new Thread(decode, "tiff-decode");
        thread.setDaemon(true);
        thread.start();
    }

    // ── Helpers ─────────────────────────────────────────────────────────
    private void updateFileCount() {
        int n = files.size();
        if (n == 0) {
            fileCountLabel.setText("No files loaded.");
        } else if (n == 1) {
            fileCountLabel.setText("1 file loaded.");
        } else {
            fileCountLabel.setText(n + " files loaded.");
        }
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideStatus() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}
