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
import taskvisualizer.Goal;
import static taskvisualizer.controllers.UniversalController.activeTask;
import static taskvisualizer.controllers.UniversalController.currentUser;
import static taskvisualizer.controllers.UniversalController.popupStage;
import static taskvisualizer.controllers.UniversalController.to24Hour;

/**
 *
 * @author Kiley Sorino & Christian Brandon
 */
public class GoalCreationController extends UniversalController implements Initializable{
   
    @FXML ImageView headerIcon, resetIcon, addIcon;
    @FXML private Button reset;
    @FXML private Button add;
    @FXML private TextField nameInput;
    @FXML private TextField targetInput;
    @FXML private TextField endHourInput, endMinuteInput;
    @FXML private DatePicker deadline;
    @FXML private ComboBox<String> rewardSelector;
    @FXML private ComboBox<String> endDayNightPicker;
    @FXML private VBox box;
    private Image headerImage, resetImage, addImage;
    private ArrayList<String> DayNight = new ArrayList<>();
    private ArrayList<String> rewardType = new ArrayList<>();
    
    
    @FXML
    private void addItem(KeyEvent goal) {
        TextField t = (TextField) goal.getSource();
        if (goal.getCode() == KeyCode.ENTER) {
            String item = t.getText();
            rewardSelector.getItems().add(item);
            currentUser.addCategory(item);
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
        rewardSelector.getItems().remove(item);
        currentUser.deleteCategory(item);
    }
    
    @FXML
    private void createGoal(){
        String name = nameInput.getText();
        double target = Integer.parseInt(targetInput.getText());
        boolean reward;
        if ("increase".equals(rewardSelector.getValue())) reward = true;
        else if ("decrease".equals(rewardSelector.getValue())) reward = false;
        
        
        String ePeriod;
        int eYear, eMonth, eDayOfMonth, eHour, eMinute;
        
        eYear = Integer.parseInt(deadline.getValue().toString().split("-")[0]);
        eMonth = Integer.parseInt(deadline.getValue().toString().split("-")[1]);
        eDayOfMonth = Integer.parseInt(deadline.getValue().toString().split("-")[2]);
        
        
        ePeriod = endDayNightPicker.getValue();
        if (endDayNightPicker.getValue().equals("All Day")) {
            eHour = 23;
            eMinute = 59;
        }
        else {
        eHour = to24Hour(Integer.parseInt(endHourInput.getText()), ePeriod);
        eMinute = Integer.parseInt(endMinuteInput.getText());
        }
            
        LocalDateTime deadline = LocalDateTime.of(eYear,eMonth,eDayOfMonth,eHour,eMinute);
        
        
        
        if (activeTask != null) {
            // editing a goal
            Goal g = (Goal) activeTask;
            g.setName(name);
            g.setTarget(target);
            g.setDeadline(deadline);
        }
        else {
            // creating a goal
            Goal g = new Goal(name, target, deadline);
            currentUser.addTask(g);
        }
        popupStage.hide();
    }
    
    @FXML    
    private void resetParameters() {
        rewardSelector.getSelectionModel().clearSelection();
        endDayNightPicker.getSelectionModel().clearSelection();
        deadline.setValue(null);
        nameInput.clear();
        targetInput.clear();
        endHourInput.clear();
        endMinuteInput.clear();
    }
    
    public void displayGoal(Goal e) {
        nameInput.setText(e.getName());
        rewardSelector.getSelectionModel().select(e.getReward());
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
        rewardType.add("Increase");
        rewardType.add("Decrease");
        rewardSelector.setItems(FXCollections.observableArrayList(rewardType));
        DayNight.add("All Day");
        DayNight.add("AM");
        DayNight.add("PM");
        endDayNightPicker.setItems(FXCollections.observableArrayList(DayNight));
        if (activeTask instanceof Goal) displayGoal((Goal) activeTask);
    }
}   
