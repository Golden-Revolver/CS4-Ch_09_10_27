package taskvisualizer;

/**
 *
 * @author Christian Brandon
 */
public class PaddingParameter {
    private final double size, widthSize, heightSize;
    private final boolean maxReference, top, right, bottom, left;
    
    public PaddingParameter(Builder builder) {
        this.size = builder.size;
        this.maxReference = builder.maxReference;
        
        // value needed to ensure the padding doesn't depend on it.
        double ignoreValue = this.maxReference ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        this.widthSize = builder.hasWidth ? builder.widthSize : ignoreValue;
        this.heightSize = builder.hasHeight ? builder.heightSize : ignoreValue;
        
        this.top = builder.top;
        this.right = builder.right;
        this.bottom = builder.bottom;
        this.left = builder.left;
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
    
    public boolean getMaxReference() {
        return maxReference;
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
    
    public static class Builder {        
        private double size = 1, widthSize = 1, heightSize = 1;
        private boolean maxReference = false, 
                // by default, all sides have padding.
                top = true, right = true, bottom = true, left = true,
                hasWidth = true, hasHeight = true;
        
        // copies the builder
        public Builder newInstance() {
            return new Builder()
                .size(size)
                .widthSize(widthSize)
                .heightSize(heightSize)
                .maxReference(maxReference)
                .top(top)
                .right(right)
                .bottom(bottom)
                .left(left)
                .hasWidth(hasWidth)
                .hasHeight(hasHeight);
        }
        
        public Builder size(double size) {
            this.size = size;
            return this;
        }
        
        public Builder widthSize(double widthSize) {
            this.widthSize = widthSize;
            return this;
        }
        public Builder hasWidth(boolean hasWidth) {
            this.hasWidth = hasWidth;
            return this;
        }
        
        public Builder heightSize(double heightSize) {
            this.heightSize = heightSize;
            return this;
        }
        public Builder hasHeight(boolean hasHeight) {
            this.hasHeight = hasHeight;
            return this;
        }
        
        public Builder maxReference(boolean maxReference) {
            this.maxReference = maxReference;
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
        
        public PaddingParameter build() {
            return new PaddingParameter(this);
        }
    }
}