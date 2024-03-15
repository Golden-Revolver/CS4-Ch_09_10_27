package taskvisualizer;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
/**
 *
 * @author Kiley
 */
public class EventCreationController extends UniversalController implements Initializable{
    @FXML private ImageView headerIcon, resetIcon, addIcon;
    @FXML private Button reset;
    @FXML private Button add;
    @FXML private TextField nameInput;
    @FXML private TextField startHourInput, startMinuteInput;
    @FXML private TextField endHourInput, endMinuteInput;
    @FXML private DatePicker startDate, endDate;
    @FXML private ComboBox<String> categorySelector;
    @FXML private ComboBox<String> startDayNightPicker;
    @FXML private ComboBox<String> endDayNightPicker;
    private Image headerImage, resetImage, addImage;
    private ArrayList<String> DayNight = new ArrayList<String>();
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DayNight.add("AM");
        DayNight.add("PM");
        endDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        startDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        
    }
}