package taskvisualizer.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import taskvisualizer.wrappers.WrappedImageView;

/**
 * FXML Controller class
 *
 * @author Christian Brandon
 */
public class NoteScreenController extends UniversalController implements Initializable {
    @FXML private GridPane layout;
    @FXML private HBox header, titleBox;
    @FXML private Text title, name;
    @FXML private TextArea notesArea;
    @FXML private Button clearButton, saveButton;

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private void clearNotes() {
        notesArea.clear();
    }
    
    @FXML
    private void saveNotes() {
        activeTask.setNotes(notesArea.getText());
        popupStage.hide();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WrappedImageView noteIcon = new WrappedImageView("writing");
        VBox noteBox = new VBox(noteIcon);
        noteBox.minWidthProperty().bind(header.heightProperty());
        titleBox.getChildren().add(0, noteBox);        
        
        name.setText(activeTask.getName());
        notesArea.setText(activeTask.getNotes());
        notesArea.end();
    }    
}