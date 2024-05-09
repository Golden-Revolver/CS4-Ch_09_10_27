package taskvisualizer.controllers;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import taskvisualizer.Requirement;
import static taskvisualizer.controllers.UniversalController.activeTask;
import static taskvisualizer.controllers.UniversalController.currentUser;
import static taskvisualizer.controllers.UniversalController.popupStage;
import static taskvisualizer.controllers.UniversalController.to24Hour;

/**
 *
 * @author Kiley
 */
public class RequirementCreationController extends UniversalController implements Initializable{
    @FXML private ImageView headerIcon, resetIcon, addIcon;
    @FXML private Button reset;
    @FXML private Button add;
    @FXML private TextField nameInput;
    @FXML private TextField endHourInput, endMinuteInput;
    @FXML private DatePicker deadline;
    @FXML private ComboBox<String> subjectSelector;
    @FXML private ComboBox<String> endDayNightPicker;
    @FXML private VBox box;
    private Image headerImage, resetImage, addImage;
    private ArrayList<String> DayNight = new ArrayList<>();
    
    
    @FXML
    private void addItem(KeyEvent event) {
        TextField t = (TextField) event.getSource();
        if (event.getCode() == KeyCode.ENTER) {
            String item = t.getText();
            subjectSelector.getItems().add(item);
            currentUser.addSubject(item);
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
        subjectSelector.getItems().remove(item);
        currentUser.deleteSubject(item);
    }
    
    @FXML
    private void createRequirement(){
        String name = nameInput.getText();
        String subject = subjectSelector.getValue();
        if (subjectSelector.getValue() == null) subject = "None";
        
        String ePeriod;
        int eYear, eMonth, eDayOfMonth, eHour, eMinute;
        
        eYear = Integer.parseInt(deadline.getValue().toString().split("-")[0]);
        eMonth = Integer.parseInt(deadline.getValue().toString().split("-")[1]);
        eDayOfMonth = Integer.parseInt(deadline.getValue().toString().split("-")[2]);
        
        ePeriod = endDayNightPicker.getValue();
        eHour = to24Hour(Integer.parseInt(endHourInput.getText()), ePeriod);
        eMinute = Integer.parseInt(endMinuteInput.getText());
            
        LocalDateTime deadline = LocalDateTime.of(eYear,eMonth,eDayOfMonth,eHour,eMinute);
        
        if (activeTask != null) {
            // editing a requirement
            Requirement r = (Requirement) activeTask;
            r.setName(name);
            r.setSubject(subject);
            r.setDeadline(deadline);
        }
        else {
            // creating a requirement
            Requirement r = new Requirement(name, subject, deadline);
            currentUser.addTask(r);
        }
        popupStage.hide();
    }
    
    @FXML    
    private void resetParameters() {
        subjectSelector.getSelectionModel().clearSelection();
        endDayNightPicker.getSelectionModel().clearSelection();
        deadline.setValue(null);
        nameInput.clear();
        endHourInput.clear();
        endMinuteInput.clear();
    }
    
    public void displayRequirement(Requirement e) {
        nameInput.setText(e.getName());
        subjectSelector.getSelectionModel().select(e.getSubject());
        LocalDateTime endDate = e.getDeadline();

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        String endTime = endDate.format(timeFormat);

        String endHour = endTime.substring(0, 2);
        String endMinute = endTime.substring(3, 5);
        String endPeriod = endTime.substring(6, 8);
        
        deadline.setValue(endDate.toLocalDate());

        endHourInput.setText(endHour);

        endMinuteInput.setText(endMinute);

        endDayNightPicker.setValue(endPeriod);
    }
       @Override
    public void initialize(URL location, ResourceBundle resources) {
        Callback<ListView<String>, ListCell<String>> cellFormat = 
            (ListView<String> p) -> new ListCell<String>() {
                @Override 
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setOnMousePressed(event -> subjectSelector.hide());
                    if (empty) {
                        setGraphic(null);
                    }
                    else {
                        setGraphic(createBox(item));
                    }
                }
            };
        
        ObservableList<String> categories = 
                FXCollections.observableList(new ArrayList(currentUser.getCategoryList()));
        
        subjectSelector.setItems(categories);
        subjectSelector.setCellFactory(cellFormat);
        
        subjectSelector.setSkin(new ComboBoxListViewSkin<String>(subjectSelector) {
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
                t.setOnKeyPressed(RequirementCreationController.this::addItem);
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
        if (activeTask instanceof Requirement) displayRequirement((Requirement) activeTask);
    }
}
