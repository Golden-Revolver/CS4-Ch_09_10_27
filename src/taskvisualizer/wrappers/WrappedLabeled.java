package taskvisualizer.wrappers;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Labeled;
import javafx.scene.text.Font;
import taskvisualizer.Textual;

/**
 *
 * @author Christian Brandon
 */
public class WrappedLabeled implements Textual {
    protected Labeled text;
    
    public WrappedLabeled(Labeled text) {
        this.text = text;
    }
    
    @Override
    public ObjectProperty<Font> fontProperty() {
        return text.fontProperty();
    }
}