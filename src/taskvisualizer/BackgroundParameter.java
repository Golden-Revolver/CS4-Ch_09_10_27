package taskvisualizer;

import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 *
 * @author Christian Brandon
 */
public class BackgroundParameter {
    private final double size, insets;
    private final boolean top, right, bottom, left;
    private final Color color;
        
    public BackgroundParameter(Builder builder) {        
        this.size = builder.size;
        this.top = builder.top;
        this.right = builder.right;
        this.bottom = builder.bottom;
        this.left = builder.left;
        this.color = builder.color;
        this.insets = builder.insets;
    }
    
    public double getSize() {
        return size;
    }
    
    public boolean getTop() {
        return top;
    }
    public boolean getRight() {
        return right;
    }
    public boolean getBottom() {
        return bottom;
    }
    public boolean getLeft() {
        return left;
    }
    
    public Color getColor() {
        return color;
    }
    
    public double getInsets() {
        return insets;
    }
    
    public static class Builder {
        private double size = 1, insets = 0;
        private boolean top = true, left = true, bottom = true, right = true;
        private Color color = Color.TRANSPARENT;
    
        // copies the builder
        public Builder newInstance() {
            return new Builder()
                .size(size)
                .top(top)
                .right(right)
                .bottom(bottom)
                .left(left)
                .color(color)
                .insets(insets);                    
        }
        
        public Builder size(double size) {
            this.size = size;
            return this;
        }
        
        public Builder hasCurve(ArrayList<Boolean> hasCurve) {
            this.top = hasCurve.get(0);
            this.right = hasCurve.get(1);
            this.bottom = hasCurve.get(2);
            this.left = hasCurve.get(3);
            return this;
        }
        
        public Builder top(boolean top) {
            this.top = top;
            return this;
        }
        public Builder right(boolean right) {
            this.right = right;
            return this;
        }
        public Builder bottom(boolean bottom) {
            this.bottom = bottom;
            return this;
        }
        public Builder left(boolean left) {
            this.left = left;
            return this;
        }
        
        public Builder color(Color color) {
            this.color = color;
            return this;
        }
        
        public Builder insets(double insets) {
            this.insets = insets;
            return this;
        }
        
        public BackgroundParameter build() {
            return new BackgroundParameter(this);
        }
    }
}