package taskvisualizer;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 *
 * @author Christian Brandon
 */
public class BackgroundBinder {
    private Region box, reference = null;
    private ArrayList<BackgroundParameter> param = new ArrayList<>();
    
    public BackgroundBinder(BackgroundParameter... param) {
        this.param.addAll(Arrays.asList(param));
    }
    
    public BackgroundBinder reference(Region reference) {
        this.reference = reference;
        return this;
    }
    
    public void bind(Region box) {
        this.box = box;
        if (reference == null) reference = box;
                
         ObjectBinding<Background> backgroundTracker = Bindings.createObjectBinding(() -> {
                ArrayList<BackgroundFill> bgFills = new ArrayList<>();
                
                for (BackgroundParameter bg : param) {
                    double referenceWidth = reference.getWidth();
                    double referenceHeight = reference.getHeight();
                    double radius = Math.min(referenceWidth, referenceHeight) * bg.getSize();

                    double topRadius = radius * (bg.getTop() ? 1 : 0);
                    double rightRadius = radius * (bg.getRight() ? 1 : 0);
                    double bottomRadius = radius * (bg.getBottom() ? 1 : 0);
                    double leftRadius = radius * (bg.getLeft() ? 1 : 0);

                    CornerRadii bgRadius = new CornerRadii(topRadius, rightRadius, 
                            bottomRadius, leftRadius, false);

                    Color bgColor = bg.getColor();
                    if (bgColor == Color.TRANSPARENT) {
                          StyleProcessor boxStyle = new StyleProcessor(box);
                          String color = boxStyle.getStyleValue("-fx-background-color");
                          if (!color.isEmpty()) bgColor = Color.web(color);
                    }

                    BackgroundFill bgFill = new BackgroundFill(
                            bgColor, bgRadius, new Insets(bg.getInsets()));
                    bgFills.add(bgFill);
                }
                return new Background(bgFills, null);
            }, reference.widthProperty(), reference.heightProperty());
            
            box.backgroundProperty().bind(backgroundTracker);
    }
}
