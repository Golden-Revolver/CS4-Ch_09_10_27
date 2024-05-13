package taskvisualizer;

import taskvisualizer.wrappers.WrappedTextInputControl;
import taskvisualizer.wrappers.WrappedLabeled;
import taskvisualizer.wrappers.WrappedText;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author Christian Brandon
 */
public class FontBinder {
    // implementing the Builder pattern
    private final Textual text;
    private final Region box;
    
    private final String family;
    private final FontWeight weight;
    private final double size, widthSize, heightSize, widthSquareSize, heightSquareSize;
    
    public FontBinder(Textual text, Region box, Builder builder) {
        // mandatory parameters
        this.text = text;
        this.box = box;
        
        // optional parameters
        this.family = builder.family;
        this.weight = builder.weight;    
        this.size = builder.size;
        this.widthSize = builder.widthSize;
        this.heightSize = builder.heightSize;
        this.widthSquareSize = builder.widthSquareSize;
        this.heightSquareSize = builder.heightSquareSize;
    }
    
    public Region getBox() {
        return box;
    }
    
    public Textual getText() {
        return text;
    }
    
    public String getFamily() {
        return family;
    }
    
    public FontWeight getWeight() {
        return weight;
    }
    
    public double getSize() {
        return size;
    }
    
    public double getWidthSize() {
        return widthSize;
    }
    
    public double getHeightSize() {
        return heightSize;
    }
    
    public double getWidthSquareSize() {
        return widthSquareSize;
    }
    
    public double getHeightSquareSize() {
        return heightSquareSize;
    }
    
    public static class Builder {      
        private String family = "System";
        private FontWeight weight = FontWeight.NORMAL;
        private double size = 1, widthSize = 1, heightSize = 1, 
                // the fontSize is the minimum of widthSize, heightSize, widthSquareSize and heightSquareSize.
                // a default value of +max_value ensures that it will never be the minimum value.
                // therefore, the fontSize will not depend on it unless a lower value is specified.
                // +inf is NOT used to prevent the 0 * +inf = NaN error.
                widthSquareSize = Double.MAX_VALUE, heightSquareSize = Double.MAX_VALUE;
        
        // creates a copy of the builder
        public Builder newInstance() {
            return new Builder()
                .family(family)
                .weight(weight)
                .size(size)
                .widthSize(widthSize)
                .heightSize(heightSize)
                .widthSquareSize(widthSquareSize)
                .heightSquareSize(heightSquareSize);
        }
        
        public Builder size(double size) {
            this.size = size;
            return this;
        }
        
        public Builder widthSize(double widthSize) {
            this.widthSize = widthSize;
            return this;
        }
        
        public Builder heightSize(double heightSize) {
            this.heightSize = heightSize;
            return this;
        }
        
        public Builder family(String family) {
            this.family = family;
            return this;
        }
        
        public Builder weight(FontWeight weight) {
            this.weight = weight;
            return this;
        }
        
        public Builder widthSquareSize(double widthSquareSize) {
            this.widthSquareSize = widthSquareSize;
            return this;
        }
        
        public Builder heightSquareSize(double heightSquareSize) {
            this.heightSquareSize = heightSquareSize;
            return this;
        }
        
        // deviation from the Builder pattern
        // allows Builder to contain only optional parameters
        // forces user to specify mandatory parameters before creating FontBinder
        public FontBinder build(Text text, Region box) {
            return new FontBinder(new WrappedText(text), box, this);
        }
        public FontBinder build(Labeled text, Region box) {
            return new FontBinder(new WrappedLabeled(text), box, this);
        }
        public FontBinder build(TextInputControl text, Region box) {
            return new FontBinder(new WrappedTextInputControl(text), box, this);
        }
    }
}