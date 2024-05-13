package taskvisualizer.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import taskvisualizer.Habit;
import static taskvisualizer.controllers.UniversalController.currentUser;

/**
 * FXML Controller class
 *
 * @author Christian Brandon
 */
public class HabitScreenController extends TaskScreenController<Habit> implements Initializable {
    
    @Override
    protected int compareTasks(Habit h1, Habit h2) {
        switch (sortMethod) {
            case "Streak":
                return h1.getStreak() - h2.getStreak();
            case "Consistency":
                return Double.compare(h1.getConsistency(), h2.getConsistency());
            default:
                return super.compareTasks(h1, h2);
        }        
    }
    
    @Override
    protected void filterTasksByStatus() {
        filterTasksByCheck();
    }
    
    @Override
    protected HBox createIconBox(Habit h) {        
       HBox iconBox = super.createIconBox(h);
       CheckBox checkBox = new CheckBox();
       checkBox.setOnMousePressed(this::checkHabit);
       if (h.isComplete()) checkBox.setSelected(true);
       iconBox.getChildren().add(0, checkBox);
       return iconBox;
    }
    
    private void checkHabit(MouseEvent event) {
        CheckBox checkBox = (CheckBox) event.getSource();
        HBox iconBox = (HBox) checkBox.getParent();
        int weekDay = LocalDate.now().getDayOfWeek().getValue();
        if (weekDay == 7) weekDay = 0; // Setting Sunday as 0

        Habit h = (Habit) currentUser.getTaskById(iconBox.getId());
        if (!checkBox.isSelected()) {
            h.setComplete(true);
            h.increaseStreak();
            h.increaseCompleted();
            h.setWeekDayStatus(weekDay, true);
        } else {
            h.setComplete(false);
            h.decreaseStreak();
            h.decreaseCompleted();
            h.setWeekDayStatus(weekDay, false);
        }
        
        displayTasks();
    }

    private Path createStar() {
        Path star = new Path();
        double deltaAngleRad = Math.PI / 5;
        
        for (int i = 0; i <= 10; i++) {
            double angleRad = Math.PI / 10 + i * deltaAngleRad;
            double x = Math.cos(angleRad);
            double y = Math.sin(angleRad);
            
            if (i % 2 == 1) {
                x *= 24;
                y *= 24;
            } else {
                x *= 12;
                y *= 12;
            }
            
            if (i == 0) {
               MoveTo moveTo = new MoveTo(x, y);
               star.getElements().add(moveTo);
            } else {
               LineTo line = new LineTo(x, y);
               star.getElements().add(line);
            }
        }
        return star;
    }
    
    private HBox createWeekBox() {
        HBox weekBox = new HBox();
        ArrayList<String> weekdays = new ArrayList<>(
            Arrays.asList("Su", "M", "T", "W", "Th", "F", "S"));
        
        for (int i = 0; i < 7; i++) {
            StackPane starIcon = new StackPane(createStar(), new Text(weekdays.get(i)));
            weekBox.getChildren().add(starIcon);
        }
        weekBox.setPadding(new Insets(5));
        weekBox.setAlignment(Pos.CENTER);
        return weekBox;
    }
    
    @Override
    protected ArrayList<Node> createTaskRow(Habit h) {
        ArrayList<Node> taskRow = super.createTaskRow(h);
        
        HBox week = createWeekBox();
        GridPane.setHalignment(week, HPos.CENTER);
        Text streak = new Text(String.valueOf(h.getStreak()));
        Text consistency = new Text(h.getConsistency() * 100 + "%");
        
        // Sets the star colors
        for (int j = 0; j < week.getChildren().size(); j++) {
            StackPane starIcon = (StackPane) week.getChildren().get(j);
            Shape star = (Shape) starIcon.getChildren().get(0);
            star.setFill(h.getWeekDayStatus(j) ? Color.YELLOW : Color.TRANSPARENT);
        }
        
        taskRow.add(week);
        taskRow.add(streak);
        taskRow.add(consistency);
        return taskRow;
    }
    
    // Habits do not have dates
    @Override protected void displayAddedTask() {}
    @Override protected void setMonthSidebar() {}
    @Override protected void initYears() {}
    @Override protected void initYearIndex() {}
    @Override protected void setYear() {}
    
    @Override
    protected HashMap<String, String> linkComboIcons() {
        HashMap<String, String> icons = super.linkComboIcons();
        icons.put("Streak", "star");
        icons.put("Consistency", "fire");
        icons.put("Any", "all");
        return icons;
    }
    
    @Override
    protected void addComboItems() {
        super.addComboItems();
        sortSelect.getItems().add("Streak");
        sortSelect.getItems().add("Consistency");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTaskList(currentUser.getHabitList());
        setCreationScreen("Habit_Creation_Screen");
        super.initialize(url, rb);
    }
}