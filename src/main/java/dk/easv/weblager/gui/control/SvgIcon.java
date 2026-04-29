package dk.easv.weblager.gui.control;

import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A single-color SVG icon loaded from /dk/easv/weblager/gui/assets/icons/{name}.svg.
 * Meant to be used from FXML like:
 *   <SvgIcon iconLiteral="user" iconSize="14"/>
 */
public class SvgIcon extends Region {

    private static final double VIEWBOX = 24.0;

    /*
     * Extracts the draw-commands from <path d="..."> elements in an SVG file.
     *
     *   <path        – matches the opening of a <path> element
     *   [^>]*        – skips over any other attributes (e.g. fill, class)
     *   ·d="         – the space before 'd' ensures we match the 'd' attribute
     *                   and not a different attribute that ends in 'd' (like 'id')
     *   ([^"]+)      – captures everything inside the quotes (the actual drawing
     *                   instructions such as "M12 2C6.48 2 2 6.48 2 12s...")
     *   "            – matches the closing quote
     */
    private static final Pattern PATH_D = Pattern.compile("<path[^>]* d=\"([^\"]+)\"");

    private final Group content = new Group();
    private final Scale scale = new Scale(1, 1);

    private String iconLiteral;
    private double iconSize = 14.0;

    public SvgIcon() {
        getStyleClass().add("svg-icon");
        content.getTransforms().add(scale);
        content.setManaged(false);
        getChildren().add(content);
        applySize();
    }

    public String getIconLiteral() { return iconLiteral; }
    public void setIconLiteral(String name) {
        this.iconLiteral = name;
        reload();
    }

    public double getIconSize() { return iconSize; }
    public void setIconSize(double size) {
        this.iconSize = size;
        applySize();
    }

    private void reload() {
        content.getChildren().clear();
        if (iconLiteral == null || iconLiteral.isEmpty()) return;

        for (String d : loadPaths(iconLiteral)) {
            SVGPath path = new SVGPath();
            path.setContent(d);
            path.getStyleClass().add("icon-fill");
            content.getChildren().add(path);
        }
    }

    private static List<String> loadPaths(String iconName) {
        String resource = "/dk/easv/weblager/gui/assets/icons/" + iconName + ".svg";
        try (InputStream in = SvgIcon.class.getResourceAsStream(resource)) {
            if (in == null) return List.of();
            String svg = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<String> paths = new ArrayList<>();
            Matcher m = PATH_D.matcher(svg);
            while (m.find()) paths.add(m.group(1));
            return paths;
        } catch (Exception e) {
            return List.of();
        }
    }

    private void applySize() {
        double factor = iconSize / VIEWBOX;
        scale.setX(factor);
        scale.setY(factor);
        setPrefSize(iconSize, iconSize);
        setMinSize(iconSize, iconSize);
        setMaxSize(iconSize, iconSize);
    }
}
