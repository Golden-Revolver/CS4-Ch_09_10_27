package taskvisualizer;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
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
    @FXML private DatePicker startDateInput, endDateInput;
    @FXML private ComboBox<String> categorySelector;
    @FXML private ComboBox<String> startDayNightPicker;
    @FXML private ComboBox<String> endDayNightPicker;
    private Image headerImage, resetImage, addImage;
    private ArrayList<String> DayNight = new ArrayList<String>();
    
    
    @FXML    
    private void resetParameters() {
        endDayNightPicker.getSelectionModel().clearSelection();
        startDayNightPicker.getSelectionModel().clearSelection();
        startDateInput.setValue(null);
        endDateInput.setValue(null);
        nameInput.clear();
        startHourInput.clear();
        startMinuteInput.clear();
        endHourInput.clear();
        endMinuteInput.clear();
    }
    
    public void displayEvent(Event e) {
        nameInput.setText(e.getName());
        LocalDateTime startDate = e.getDate();
        LocalDateTime endDate = e.getDate().plusDays(1); // placeholder!

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String startTime = startDate.format(timeFormat);
        String endTime = endDate.format(timeFormat);

        String startHour = startTime.substring(0, 2);
        String startMinute = startTime.substring(3, 5);
        String startPeriod = startTime.substring(6, 8);

        String endHour = endTime.substring(0, 2);
        String endMinute = endTime.substring(3, 5);
        String endPeriod = endTime.substring(6, 8);
        
        startDateInput.setValue(startDate.toLocalDate());
        endDateInput.setValue(endDate.toLocalDate());

        startHourInput.setText(startHour);
        endHourInput.setText(endHour);

        startMinuteInput.setText(startMinute);
        endMinuteInput.setText(endMinute);

        startDayNightPicker.setValue(startPeriod);
        endDayNightPicker.setValue(endPeriod);
    }
         
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DayNight.add("AM");
        DayNight.add("PM");
        endDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        startDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        if (activeTask instanceof Event) displayEvent((Event) activeTask);
    }
}