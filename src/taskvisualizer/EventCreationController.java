package taskvisualizer;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Callback;
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
    @FXML private VBox box;
    private Image headerImage, resetImage, addImage;
    private ArrayList<String> DayNight = new ArrayList<String>();
    
    
    @FXML
    private void addItem(KeyEvent event) {
        TextField t = (TextField) event.getSource();
        if (event.getCode() == KeyCode.ENTER) {
            String item = t.getText();
            categorySelector.getItems().add(item);
            t.clear();
        }
    }
    
    private HBox createBox(String name) {
        HBox box = new HBox();
        Text text = new Text(name);
        Pane gap = new Pane();
        HBox.setHgrow(gap, Priority.ALWAYS);
        
        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-background-color: red");
        removeButton.setOnMouseClicked(this::deleteItem);
        
        box.getChildren().addAll(text, gap, removeButton);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);
        
        return box;
    }
    
    private void deleteItem(javafx.scene.input.MouseEvent event) {
        HBox box = (HBox) ((Button) event.getSource()).getParent();
        String item = ((Text) box.getChildren().get(0)).getText();
        categorySelector.getItems().remove(item);
    }
    
   
    
    
    @FXML
    private void createEvent(){
        String name = nameInput.getText();
        
        int sYear, sMonth, sDayOfMonth, sHour, sMinute;
        int eYear, eMonth, eDayOfMonth, eHour, eMinute;
        
        sYear = Integer.parseInt(startDateInput.getValue().toString().split("-")[0]);
        sMonth = Integer.parseInt(startDateInput.getValue().toString().split("-")[1]);
        sDayOfMonth = Integer.parseInt(startDateInput.getValue().toString().split("-")[2]);
        
        sHour = Integer.parseInt(startHourInput.getText());
        sMinute = Integer.parseInt(startMinuteInput.getText());
        
        eYear = Integer.parseInt(endDateInput.getValue().toString().split("-")[0]);
        eMonth = Integer.parseInt(endDateInput.getValue().toString().split("-")[1]);
        eDayOfMonth = Integer.parseInt(endDateInput.getValue().toString().split("-")[2]);
        
        eHour = Integer.parseInt(endHourInput.getText());
        eMinute = Integer.parseInt(endMinuteInput.getText());
        
        if(startDayNightPicker.getValue().equals("PM")){
            sHour = sHour + 12;
            
        }  else if(endDayNightPicker.getValue().equals("PM")){
            eHour = eHour + 12;
                    
        }
            
        LocalDateTime start = LocalDateTime.of(sYear, sMonth, sDayOfMonth, 12, 30);
        LocalDateTime end = LocalDateTime.of(eYear,eMonth,eDayOfMonth,eHour,eMinute);
        
        Event e = new Event(name, start, "Sorsogon");
    }
    
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
        
        ArrayList<String> choices = new ArrayList<>(
                Arrays.asList());
        categorySelector.setItems(FXCollections.observableArrayList(choices));
        categorySelector.setPrefWidth(200);
        box.getChildren().add(categorySelector);

        Callback<ListView<String>, ListCell<String>> cellFormat = 
            (ListView<String> p) -> new ListCell<String>() {
                @Override 
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setOnMousePressed(event -> categorySelector.hide());
                    if (empty) {
                        setGraphic(null);
                    }
                    else {
                        setGraphic(createBox(item));
                    }
                }
            };

        categorySelector.setCellFactory(cellFormat);
        
        categorySelector.setSkin(new ComboBoxListViewSkin<String>(categorySelector) {
            private TextField t = new TextField("+ Add more...");
            private VBox box = new VBox();
            
            @Override
            protected boolean isHideOnClickEnabled() {
                return false;
            }
            @Override
            public Node getPopupContent() {
                Node defaultContent = super.getPopupContent();
                defaultContent.setManaged(true);
                t.setStyle("-fx-color: lightgrey");
                t.setOnKeyPressed(EventCreationController.this::addItem);
                t.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (t.getStyle().contains("-fx-color: lightgrey") && newValue) {
                        t.clear();
                        t.setStyle("");
                    }
                    if (t.getText().isEmpty() && !newValue) {
                        t.setText("+ Add more...");
                        t.setStyle("-fx-color: lightgrey");
                    }
                }) ;
                box.getChildren().setAll(defaultContent, t);
                return box;
            }
        });

        DayNight.add("AM");
        DayNight.add("PM");
        endDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        startDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        if (activeTask instanceof Event) displayEvent((Event) activeTask);
    }
}