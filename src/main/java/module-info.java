/**
 * Module declaration for the Tiffany app (built on the WebLager design system).
 *
 * - {@code requires} lists the modules our code uses.
 * - {@code opens ... to javafx.fxml} lets FXMLLoader read @FXML fields by reflection.
 *   Without these, JavaFX would throw "no controller specified" on startup.
 * - {@code exports} makes the entry-point package visible to the JavaFX runtime.
 */
module dk.easv.weblager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;   // SwingFXUtils — converts BufferedImage to a JavaFX Image
    requires java.desktop;   // ImageIO + BufferedImage (TIFF decoding via TwelveMonkeys)

    opens dk.easv.weblager.gui            to javafx.fxml;
    opens dk.easv.weblager.gui.controller to javafx.fxml;
    opens dk.easv.weblager.gui.control    to javafx.fxml;

    exports dk.easv.weblager;
}
