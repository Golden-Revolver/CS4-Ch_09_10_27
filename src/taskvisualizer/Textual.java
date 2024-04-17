package taskvisualizer;

import javafx.beans.property.ObjectProperty;
import javafx.scene.text.Font;

/**
 *
 * @author Christian Brandon
 */
public interface Textual {
    // every object that has a fontProperty is Textual.
    ObjectProperty<Font> fontProperty();
}