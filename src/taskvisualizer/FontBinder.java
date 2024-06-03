package taskvisualizer;

import java.util.stream.DoubleStream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import taskvisualizer.wrappers.WrappedLabeled;
import taskvisualizer.wrappers.WrappedText;
import taskvisualizer.wrappers.WrappedTextInputControl;

/**
 *
 * @author Christian Brandon
 */
public class FontBinder {
    private Textual text;
    private Region box;
    private FontParameter param;
    
    public FontBinder(FontParameter param) {
        this.param = param;
    }
    
    public void bind(Text text, Region box) {
        bind(new WrappedText(text), box);
    }
    public void bind(Labeled text, Region box) {
        bind(new WrappedLabeled(text), box);
    }
    public void bind(TextInputControl text, Region box) {
        bind(new WrappedTextInputControl(text), box);
    }
    
    private void bind(Textual text, Region box) {
        this.text = text;
        this.box = box;
        
        ObjectBinding<Font> fontTracker = Bindings.createObjectBinding(() -> {
                double width = box.getWidth();
                double height = box.getHeight();
                
                double boxWidth = width * param.getWidthSize();
                double boxSquareWidth = width * width * param.getWidthSquareSize();
                double boxHeight = height * param.getHeightSize();
                double boxSquareHeight = height * height * param.getHeightSquareSize();
                
                double fontSize = DoubleStream.of(boxWidth, boxSquareWidth, 
                        boxHeight, boxSquareHeight).min().getAsDouble() * param.getSize();
                
                if (fontSize == 0) fontSize = 1; // setting this to 0 glitches the font
                return Font.font(param.getFamily(), param.getWeight(), fontSize);
            }, box.widthProperty(), box.heightProperty());
            
            text.fontProperty().bind(fontTracker);
    }
}