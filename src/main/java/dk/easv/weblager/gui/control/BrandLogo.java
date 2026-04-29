package dk.easv.weblager.gui.control;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Loads the brand logo from its SVG file and returns it as a
 * scalable JavaFX node. Because it uses vector shapes (not a
 * raster image), the logo stays crisp at any screen resolution.
 */
public final class BrandLogo {

    private static final String RESOURCE = "/dk/easv/weblager/gui/assets/images/logo-light.svg";
    private static final Color BRAND = Color.web("#2D3D4F");
    private static final double SVG_WIDTH = 1660.44;

    private BrandLogo() {}

    /** Creates the logo scaled to the given pixel width. */
    public static Node create(double targetWidth) {
        Group logo = new Group();

        try (InputStream in = BrandLogo.class.getResourceAsStream(RESOURCE)) {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(in);

            NodeList paths = doc.getElementsByTagName("path");
            for (int i = 0; i < paths.getLength(); i++) {
                Element el = (Element) paths.item(i);
                SVGPath shape = new SVGPath();
                shape.setContent(el.getAttribute("d"));
                shape.setFill("cls-1".equals(el.getAttribute("class"))
                        ? Color.WHITE : BRAND);
                logo.getChildren().add(shape);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load logo SVG", e);
        }

        double scale = targetWidth / SVG_WIDTH;
        logo.getTransforms().add(new Scale(scale, scale));

        // The Scale transform affects rendering but not layout bounds,
        // so parent containers (especially HBox) would see the original
        // ~1660 px width. Wrap in a Pane sized to the visual bounds.
        double visualWidth  = logo.getBoundsInParent().getWidth();
        double visualHeight = logo.getBoundsInParent().getHeight();
        Pane sized = new Pane(logo);
        sized.setPrefSize(visualWidth, visualHeight);
        sized.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        sized.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return sized;
    }
}
