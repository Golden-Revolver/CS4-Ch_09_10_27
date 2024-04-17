package taskvisualizer.wrappers;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;
import taskvisualizer.Textual;

/**
 *
 * @author Christian Brandon
 */
public class WrappedTextInputControl implements Textual {
    protected TextInputControl text;
    
    public WrappedTextInputControl(TextInputControl text) {
        this.text = text;
    }
    
    @Override
    public ObjectProperty<Font> fontProperty() {
        return text.fontProperty();
    }
}