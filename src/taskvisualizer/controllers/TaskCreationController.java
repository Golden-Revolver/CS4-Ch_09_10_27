package taskvisualizer.controllers;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import taskvisualizer.Task;
import static taskvisualizer.controllers.UniversalController.currentUser;
import static taskvisualizer.controllers.UniversalController.to24Hour;

/**
 *
 * @author Christian Brandon
 * @param <T>
 */
public abstract class TaskCreationController<T extends Task> extends UniversalController {
    @FXML protected TextField nameInput;
    @FXML protected DatePicker startDateInput, endDateInput;
    @FXML protected ComboBox<String> startDayNight, endDayNight;
    @FXML protected TextField startHourInput, startMinuteInput, endHourInput, endMinuteInput;
    
    protected ComboBox<String> editableSelector;
    protected LinkedHashSet<String> editableList = new LinkedHashSet<>();
    
    protected ArrayList<TextField> textFields = new ArrayList<>();
    protected ArrayList<ComboBox> comboBoxes = new ArrayList<>();
    protected ArrayList<DatePicker> datePickers = new ArrayList<>();
    
    protected String name;
    protected LocalDateTime startDate, endDate;
    protected boolean hasStartDate = false, hasEndDate = false;
    
    // Setter methods
    protected void setEditableSelector(ComboBox editableSelector) {
        this.editableSelector = editableSelector;
    }
    protected void setEditableList(LinkedHashSet<String> editableList) {
        this.editableList = editableList;
    }
    protected void hasStartDate(boolean hasStartDate) {
        this.hasStartDate = hasStartDate;
    }
    protected void hasEndDate(boolean hasEndDate) {
        this.hasEndDate = hasEndDate;
    }
    
    protected void initControl() {
        textFields.add(nameInput);
        if (hasStartDate) initStartControl();
        if (hasEndDate) initEndControl();
    }
    
    protected void initStartControl() {
        textFields.add(startHourInput);
        textFields.add(startMinuteInput);
        comboBoxes.add(startDayNight);
        datePickers.add(startDateInput);
    }
    
    protected void initEndControl() {
        textFields.add(endHourInput);
        textFields.add(endMinuteInput);
        comboBoxes.add(endDayNight);
        datePickers.add(endDateInput);
    }
    
    @FXML
    protected void addItem(KeyEvent event) {
        TextField t = (TextField) event.getSource();
        
        if (event.getCode() == KeyCode.ENTER) {
            String item = t.getText();
            editableSelector.getItems().add(item);
            editableList.add(item);
            t.clear();
        }
    }
            
    protected void deleteItem(MouseEvent event) {
        Button deleteButton = (Button) event.getSource();        
        HBox box = (HBox) deleteButton.getParent();
        Text itemText = (Text) box.getChildren().get(0);
        String item = itemText.getText();
        
        editableSelector.getItems().remove(item);
        editableList.remove(item);
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
    
    protected LocalDateTime createDate(DatePicker dateInput, ComboBox<String> dayNight,
            TextField hourInput, TextField minuteInput) {
        String period = dayNight.getValue();
        int hour = to24Hour(Integer.parseInt(hourInput.getText()), period);
        int minute = Integer.parseInt(minuteInput.getText());
        
        LocalDate date = dateInput.getValue();
        LocalTime time = LocalTime.of(hour, minute);
        return LocalDateTime.of(date, time);
    }
    
    protected LocalDateTime createStartDate() {
        return createDate(startDateInput, startDayNight, 
                startHourInput, startMinuteInput);
    }
    
    protected LocalDateTime createEndDate() {
        return createDate(endDateInput, endDayNight, 
                endHourInput, endMinuteInput);
    }
    
    @FXML
    protected void resetParameters() {
        for (TextField tf : textFields) tf.clear();
        for (ComboBox cb : comboBoxes) cb.getSelectionModel().clearSelection();
        for (DatePicker dp : datePickers) dp.setValue(null);
    }
    
    protected void displayDate(LocalDateTime date, DatePicker dateInput, ComboBox<String> dayNight,
            TextField hourInput, TextField minuteInput) {
        int hour = date.getHour();
        int minute = date.getMinute();
        
        dateInput.setValue(date.toLocalDate());
        dayNight.setValue(getPeriod(hour));
        hourInput.setText(String.valueOf(to12Hour(hour)));
        minuteInput.setText(String.valueOf(minute));
    }
    
    protected void displayStartDate(LocalDateTime date) {
        displayDate(date, startDateInput, startDayNight,
                startHourInput, startMinuteInput);
    }
    
    protected void displayEndDate(LocalDateTime date) {
        displayDate(date, endDateInput, endDayNight,
                endHourInput, endMinuteInput);
    }
    
    protected void displayTask(T t) {
        nameInput.setText(t.getName());
    }
     
    protected void extractData() {
        name = nameInput.getText();
        if (hasStartDate) startDate = createStartDate();
        if (hasEndDate) endDate = createEndDate();
    }
    
    protected void editTask(T t) {
        t.setName(name);
    }
    
    protected abstract T createTask();
    
    @FXML
    protected void submit() {
        extractData();
        if (activeTask != null) {
            T t = (T) activeTask;
            editTask(t);
        }
        else {
            T t = createTask();
            activeTask = t;
            currentUser.addTask(t);
        }
        popupStage.hide();
    }
    
    protected void initEditableSelector() {
        Callback<ListView<String>, ListCell<String>> cellFormat = 
            (ListView<String> p) -> new ListCell<String>() {
                @Override 
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setOnMousePressed(event -> editableSelector.hide());
                    if (empty) {
                        setGraphic(null);
                    }
                    else {
                        setGraphic(createBox(item));
                    }
                }
            };
        
        editableSelector.setItems(FXCollections.observableList(
                new ArrayList(editableList)));
        editableSelector.setCellFactory(cellFormat);
        
        editableSelector.setSkin(new ComboBoxListViewSkin<String>(editableSelector) {
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
                t.setOnKeyPressed(TaskCreationController.this::addItem);
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
    }
    
    protected void initDayNight() {
        if (hasStartDate) startDayNight.getItems().addAll("AM", "PM");
        if (hasEndDate) endDayNight.getItems().addAll("AM", "PM");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        initControl();
        initDayNight();
        if (editableSelector != null) initEditableSelector();
        if (activeTask != null) displayTask((T) activeTask);
    }
}