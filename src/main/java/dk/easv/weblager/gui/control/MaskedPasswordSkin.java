package dk.easv.weblager.gui.control;

import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;

/**
 * Custom skin that replaces the default password bullet (•) with a
 * larger filled circle so it renders crisply on all displays.
 */
public class MaskedPasswordSkin extends TextFieldSkin {

    public MaskedPasswordSkin(PasswordField field) {
        super(field);
    }

    @Override
    protected String maskText(String text) {
        return "\u2B24".repeat(text.length());
    }
}
