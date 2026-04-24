module dk.easv.weblager {
    requires javafx.controls;
    requires javafx.fxml;

    opens dk.easv.weblager.gui to javafx.fxml;
    exports dk.easv.weblager;
}
