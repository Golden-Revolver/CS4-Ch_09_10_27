package taskvisualizer;

import java.util.function.BinaryOperator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.Region;

/**
 *
 * @author Christian Brandon
 */
public class PaddingBinder {
    private Region box, reference;
    private PaddingParameter param;
    
    public PaddingBinder(PaddingParameter param) {
        this.param = param;
    }
    
    public PaddingBinder reference(Region reference) {
        this.reference = reference;
        return this;
    }
    
    public void bind(Region box) {
        this.box = box;
        if (reference == null) reference = box;

        BinaryOperator<Double> getInsetSize;
            
        if (param.getMaxReference()) getInsetSize = (width, height) -> Math.max(width, height);
        else getInsetSize = (width, height) -> Math.min(width, height);

        ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() -> {
            double referenceWidth = reference.getWidth() * param.getWidthSize();
            double referenceHeight = reference.getHeight() * param.getHeightSize();
            double insetSize = getInsetSize.apply(referenceWidth, referenceHeight) * param.getSize();

            return new Insets(param.getTop() ? insetSize : 0, param.getRight() ? insetSize : 0, 
                    param.getBottom() ? insetSize : 0, param.getLeft() ? insetSize : 0);
        }, reference.widthProperty(), reference.heightProperty());

        box.paddingProperty().bind(paddingTracker);
    }
}