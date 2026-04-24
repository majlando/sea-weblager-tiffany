package dk.easv.weblager.gui.control;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads the brand logo SVG and returns it as a scaled JavaFX Node.
 * The logo has two colors (white + dark blue), set by the CSS class name
 * inside the SVG (cls-1 / cls-2).
 */
public final class BrandLogo {

    private static final String RESOURCE = "/dk/easv/weblager/gui/assets/images/logo-light.svg";

    private static final Map<String, Color> COLORS = Map.of(
            "cls-1", Color.WHITE,
            "cls-2", Color.web("#2D3D4F")
    );

    private static final Pattern VIEWBOX = Pattern.compile("viewBox=\"\\s*\\S+\\s+\\S+\\s+(\\S+)");
    private static final Pattern PATH    = Pattern.compile("<path\\b[^>]*\\bclass=\"([^\"]+)\"[^>]*\\bd=\"([^\"]+)\"");
    private static final Pattern POLYGON = Pattern.compile("<polygon\\b[^>]*\\bclass=\"([^\"]+)\"[^>]*\\bpoints=\"([^\"]+)\"");

    private BrandLogo() {}

    public static Node create(double targetWidth) {
        String svg = readSvg();
        Group content = new Group();

        Matcher pm = PATH.matcher(svg);
        while (pm.find()) {
            SVGPath p = new SVGPath();
            p.setContent(pm.group(2));
            p.setFill(COLORS.getOrDefault(pm.group(1), Color.BLACK));
            content.getChildren().add(p);
        }

        Matcher pol = POLYGON.matcher(svg);
        while (pol.find()) {
            Polygon polygon = new Polygon();
            for (String token : pol.group(2).trim().split("[\\s,]+")) {
                if (!token.isEmpty()) polygon.getPoints().add(Double.parseDouble(token));
            }
            polygon.setFill(COLORS.getOrDefault(pol.group(1), Color.BLACK));
            content.getChildren().add(polygon);
        }

        double scaleFactor = targetWidth / viewBoxWidth(svg);
        content.getTransforms().add(new Scale(scaleFactor, scaleFactor));
        return new Group(content);
    }

    private static String readSvg() {
        try (InputStream in = BrandLogo.class.getResourceAsStream(RESOURCE)) {
            if (in == null) throw new RuntimeException("Logo resource not found: " + RESOURCE);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read logo SVG", e);
        }
    }

    private static double viewBoxWidth(String svg) {
        Matcher m = VIEWBOX.matcher(svg);
        if (!m.find()) throw new IllegalArgumentException("SVG missing viewBox");
        return Double.parseDouble(m.group(1));
    }

}
