package taskvisualizer.wrappers;

import javafx.beans.property.ObjectProperty;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import taskvisualizer.Textual;

/**
 *
 * @author Christian Brandon
 */
public class WrappedText implements Textual {
    protected Text text;
    
    public WrappedText(Text text) {
        this.text = text;
    }
    
    @Override
    public ObjectProperty<Font> fontProperty() {
        return text.fontProperty();
    }
}