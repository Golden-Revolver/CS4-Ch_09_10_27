package taskvisualizer.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import taskvisualizer.Habit;

/**
 *
 * @author Kiley
 */
public class HabitCreationController extends UniversalController implements Initializable {
    @FXML Text title;
    @FXML TextField name;
    @FXML Label nameTitle;
    @FXML Button resetButton, addButton;
    
    @FXML
    private void resetParameters() {
        name.clear();
    }
    
    @FXML
    private void addHabit() {
        if (activeTask != null) {
            Habit h = (Habit) activeTask;
            h.setName(name.getText());
        } else {        
            Habit h = new Habit(name.getText());
            currentUser.addTask(h);  
        }
        popupStage.hide();
    }
    
    private void displayHabit(Habit h) {
        name.setText(h.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       if (activeTask instanceof Habit) displayHabit((Habit) activeTask);
    }
}