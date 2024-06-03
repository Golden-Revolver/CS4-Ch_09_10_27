package taskvisualizer.wrappers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import taskvisualizer.TaskVisualizer;

/**
 *
 * @author Christian Brandon
 */
public class WrappedImageView extends ImageView { // copied online w/ slight modifications
    public WrappedImageView() {
        this.setPreserveRatio(true);
    }
    
    public WrappedImageView(Image i) {
        this();
        this.setImage(i);
    }
    
    public WrappedImageView(String filename) {
        this(new Image(TaskVisualizer.class.getResourceAsStream("images/" + filename + ".png")));
    }
 
    @Override
    public double minWidth(double height) {
        return 1;
    }

    @Override
    public double prefWidth(double height) {
        Image i = this.getImage();
        if (i == null) return minWidth(height);
        return i.getWidth();
    }

    @Override
    public double maxWidth(double height) {
        return 16384;
    }

    @Override
    public double minHeight(double width) {
        return 1;
    }

    @Override
    public double prefHeight(double width) {
        Image i = this.getImage();
        if (i == null) return minHeight(width);
        return i.getHeight();
    }

    @Override
    public double maxHeight(double width) {
        return 16384;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        setFitWidth(width);
        setFitHeight(height);
    }
}