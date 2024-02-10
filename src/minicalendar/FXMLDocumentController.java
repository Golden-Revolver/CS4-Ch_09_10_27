package minicalendar;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import java.util.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 *
 * @author Christian Brandon
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML private Text monthYear;
    @FXML private GridPane calendar, events, requirements, goals, habits;
    
    private int currentMonth, currentYear;
    private User currentUser;
    
    private void updateCalendar(int m, int y) {
        Locale locale = new Locale("en");
        String month = Month.of(m).getDisplayName(TextStyle.FULL_STANDALONE, locale);
        
        monthYear.setText(month + " " + Integer.toString(y));
        
        // resets all text starting from the third row
        for (int i = 0; i < calendar.getChildren().size() - 8; i++) {
            Text t = (Text) calendar.getChildren().get(i + 8);
            t.setText("");
        }
        
        LocalDate firstDay = LocalDate.of(y, m, 1);
        int weekDay = firstDay.getDayOfWeek().getValue();
        if (weekDay == 7) weekDay = 0; // resets value of sunday to 0    
        int firstIndex = weekDay + 8; // gets a cell on the third row
        
        // sets text for the first day of the month
        Text firstText = (Text) calendar.getChildren().get(firstIndex);
        firstText.setText("1");
        
        for (int i = 1; i < firstDay.lengthOfMonth(); i++) {
            Text t = (Text) calendar.getChildren().get(firstIndex + i);
            t.setText(Integer.toString(i + 1));
        }
    }
    
    @FXML
    private void previousMonth(ActionEvent event) {
        if (currentMonth == 1) {
            currentMonth = 12;
            currentYear--;
        }
        else currentMonth--;
        updateCalendar(currentMonth, currentYear);
    }
    
    @FXML
    private void nextMonth(ActionEvent event) {
        if (currentMonth == 12) {
            currentMonth = 1;
            currentYear++;
        } 
        else currentMonth++;
        updateCalendar(currentMonth, currentYear);
    }
    
    private VBox createTextBox(Task t) {
        Text name = new Text(t.getNameFormat());
        name.setStyle("-fx-font-weight: bold");
        Text data = new Text(t.getDataFormat());  
        VBox textBox = new VBox(name, data);
        return textBox;
    }
    
    private VBox createTimedTextBox(Task t, boolean complete, boolean late) {
        VBox textBox = createTextBox(t);
        Text name = (Text) textBox.getChildren().get(0);
        Text data = (Text) textBox.getChildren().get(1);
        
         // complete -> green, late -> red
        if (complete) {
            name.setFill(Color.DARKGREEN);
            data.setFill(Color.DARKGREEN);
        }
        else if (late) {
            name.setFill(Color.MAROON);
            data.setFill(Color.MAROON);
        }
        return textBox;
    }
    
    private VBox createRequirementTextBox(Requirement r) {
        return createTimedTextBox(r, r.isComplete(), r.isLate());
    }
    
    private VBox createGoalTextBox(Goal g) {
        VBox textBox = createTimedTextBox(g, g.isComplete(), g.isLate());
        Line line = new Line(-75, 0, 75, 0);
        Circle circle = new Circle(7, Color.NAVY);

        // stacking the line and circle together
        StackPane progressBar = new StackPane(line, circle);
        progressBar.setAlignment(Pos.CENTER_LEFT);

        // setting the location of the circle
        double lineLength = line.getEndX() - line.getStartX();  
        circle.setTranslateX(lineLength * g.getGoalFraction());

        textBox.getChildren().add(1, progressBar);
     
        return textBox;
    }
    
    private void displayEventList(ArrayList<Event> eventList) {
        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            events.addRow(i, createTextBox(e));
        }
    }
    
    private void displayRequirementList(ArrayList<Requirement> requirementList) {
        for (int i = 0; i < requirementList.size(); i++) {
            Requirement r = requirementList.get(i);
            CheckBox box = createRequirementBox();
            VBox textBox = createRequirementTextBox(r);
            
            box.setId(r.getId());
            textBox.setId("Text" + r.getId());
            
            requirements.addRow(i, box, textBox);
        }
    }
    
    private void displayGoalList(ArrayList<Goal> goalList) {
        for (int i = 0; i < goalList.size(); i++) {
            Goal g = goalList.get(i);
            Button btn = createGoalButton();
            VBox textBox = createGoalTextBox(g);
            
            btn.setId(g.getId());
            textBox.setId("Text" + g.getId());
            
            goals.addRow(i, btn, textBox);
        }
    }
    
    private void displayHabitList(ArrayList<Habit> habitList) {
        for (int i = 0; i < habitList.size(); i++) {
            Habit h = habitList.get(i);
            CheckBox box = createHabitBox();
            VBox textBox = createTextBox(h);
            
            box.setId(h.getId());
            textBox.setId("Text" + h.getId());
            
            habits.addRow(i, box, textBox);
        }
    }
    
    private CheckBox createCheckBox() {
        CheckBox checkBox = new CheckBox();
        checkBox.setStyle("-fx-label-padding: 0");
        return checkBox;
    }
    
    private CheckBox createRequirementBox() {
        CheckBox checkBox = createCheckBox();
        checkBox.setOnAction(this::checkRequirement);
        return checkBox;
    }
    
    private CheckBox createHabitBox() {
        CheckBox checkBox = createCheckBox();
        checkBox.setOnAction(this::checkHabit);
        return checkBox;
    }
    
    private Button createGoalButton() {
       Button btn = new Button("+");
       btn.setStyle("-fx-background-radius: 25");
       btn.setStyle("-fx-background-color: linear-gradient(mediumblue, navy); "
                + "-fx-background-radius: 25");
       btn.setTextFill(Color.WHITE);
       GridPane.setValignment(btn, VPos.TOP);     
       btn.setOnAction(this::updateProgress);
    
       return btn;
    }

    // triggers when a goal button is clicked
    @FXML
    private void updateProgress(ActionEvent event) {
        String taskId = ((Node) event.getSource()).getId();
        Goal g = (Goal) currentUser.getTaskById(taskId);
        
        TextInputDialog screen = new TextInputDialog();
        screen.setTitle(g.getName());
        screen.setHeaderText("Update your progress!");
        screen.setContentText("Please enter a number:");
   
        Button enter = (Button) screen.getDialogPane().lookupButton(ButtonType.OK);
        enter.addEventFilter(ActionEvent.ACTION, e -> {
            String result = screen.getEditor().getText();
            try {
                double change = Double.parseDouble(result);
                g.updateProgress(change);
                updateGoal(g);
            } catch (Exception error) {
                System.out.println(error);
                e.consume(); // prevents dialog from closing
            }
        });
        
        screen.showAndWait();
    }
    
    @FXML
    private void displayTasks(User u) {
       sortLists(u);
       displayEventList(u.getEventList());
       displayRequirementList(u.getRequirementList());
       displayGoalList(u.getGoalList());
       displayHabitList(u.getHabitList());
    }
    
    // sorts lists in ascending order of date
    // except for habits -> sorted by streak number
    private void sortLists(User u) {
        Collections.sort(u.getEventList());
        Collections.sort(u.getRequirementList());
        Collections.sort(u.getGoalList());
        Collections.sort(u.getHabitList());
    }
    
    
    // check methods -> trigger when user clicks on a checkbox
    // update methods -> change task info and display in the app
    private void updateTime(User u) {
        for (Habit h : u.getHabitList()) updateHabit(h);
        for (Requirement r : u.getRequirementList()) updateRequirement(r);
        for (Goal g : u.getGoalList()) updateGoal(g);
    }
    
    @FXML
    private void checkHabit(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        String taskId = checkBox.getId();
        Habit h = (Habit) currentUser.getTaskById(taskId);
        if (checkBox.isSelected()) {
            h.setComplete(true);
            h.increaseStreak();
            h.setLastChecked(LocalDate.now());
        }
        else {
            h.setComplete(false);
            h.decreaseStreak();
        }
        updateHabit(h);
    }
    
    // TO-DO: Figure out how to store lastChecked when app closes
    private void updateHabit(Habit h) {
        if (h.getLastChecked() == null) return;
        long daysPassed = ChronoUnit.DAYS.between(
                h.getLastChecked(), LocalDate.now());
        if (daysPassed == 1) h.updateStreak();
        else if (daysPassed > 1) {
            h.setStreak(0);
            h.setComplete(false);
        }
         
        // sets checkbox state to habit's complete state
        for (Node n : habits.getChildren()) {
            if (n.getId().equals(h.getId())) {
                CheckBox checkBox = (CheckBox) n;
                checkBox.setSelected(h.isComplete());
            }
        }
        updateTask(h, habits, createTextBox(h));
    }
    
    private void checkRequirement(ActionEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        String taskId = checkBox.getId();
        Requirement r = (Requirement) currentUser.getTaskById(taskId);
        if (checkBox.isSelected()) r.setComplete(true);
        else r.setComplete(false);
        updateTask(r, requirements, createRequirementTextBox(r));
    }
    
    private void updateRequirement(Requirement r) {
        r.updateLate();
        updateTask(r, requirements, createRequirementTextBox(r));
    }
    
    private void updateGoal(Goal g) {
        g.updateLate();
        g.updateGoal();
        updateTask(g, goals, createGoalTextBox(g));
    }
    
    private void updateTask(Task t, GridPane grid, VBox updatedBox) {
        updatedBox.setId("Text" + t.getId());
        Node oldBox = null;
        for (Node n : grid.getChildren()) {
            if (n.getId().equals("Text" + t.getId())) oldBox = n;
        }
        int col = GridPane.getColumnIndex(oldBox);
        int row = GridPane.getRowIndex(oldBox);
        grid.getChildren().remove(oldBox);
        grid.add(updatedBox, col, row);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LocalDateTime now = LocalDateTime.now();
        currentMonth = now.getMonthValue();
        currentYear = now.getYear();
     
        updateCalendar(currentMonth, currentYear);
        
        try {
            User u1 = User.findUser("Golden Revolver", "GoldenMan1234");
            currentUser = u1;
            displayTasks(u1);
        } catch (UserNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("User Not Found");
            alert.setHeaderText("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        
        // regularly updates tasks dependent on time every minute
        final Timeline timeline = new Timeline(
            new KeyFrame(Duration.minutes(1),
            event -> {
                updateTime(currentUser);
            }
        ));
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();
    }       
}