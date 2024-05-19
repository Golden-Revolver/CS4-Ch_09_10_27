package taskvisualizer.controllers;

import javafx.fxml.Initializable;
import taskvisualizer.Habit;

/**
 *
 * @author Kiley
 */
public class HabitCreationController extends TaskCreationController<Habit> implements Initializable {
    
    @Override
    protected Habit createTask() {
        return new Habit(name);
    }
}