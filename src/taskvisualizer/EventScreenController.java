package taskvisualizer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Christian Brandon
 */
public class EventScreenController extends UniversalController implements Initializable {
    @FXML VBox searchBox, eventsHeader;
    @FXML HBox searchBar;
    @FXML TextField searchField;
    @FXML Button createButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initHeader(currentScreen);
        Image magnifyingGlass = new Image(getClass().getResourceAsStream("images/magnifying-glass.png"));
        WrappedImageView searchIcon = new WrappedImageView(magnifyingGlass);
        
        VBox searchIconBox = new VBox(searchIcon);
        searchIconBox.setAlignment(Pos.CENTER_RIGHT);
        
        NumberBinding searchBoxSize = Bindings.min(searchBox.heightProperty().multiply(0.4), 
                searchBox.widthProperty().multiply(0.2));
        
        searchIconBox.minWidthProperty().bind(searchBoxSize);
        searchIconBox.prefWidthProperty().bind(searchBoxSize);
        
        searchField.widthProperty().addListener((observable, oldValue, value) -> {
            String input = searchField.getText();
            searchField.clear();
            searchField.setText(input);
            searchField.end();
        });     
        searchBar.getChildren().add(searchIconBox);
        
        createButton.setOnAction(displayPopupActionHandler("Event_Creation_Screen"));
        eventsHeader.spacingProperty().bind(eventsHeader.heightProperty().multiply(0.11));
        Binder.bindPadding(eventsHeader, 0.11);
    }
}