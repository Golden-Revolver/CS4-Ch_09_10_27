package taskvisualizer.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import taskvisualizer.Event;
/**
 *
 * @author Kiley
 */
public class EventCreationController extends TaskCreationController<Event> implements Initializable {
    @FXML private ComboBox<String> categorySelector;
    protected String category;
    
    @Override
    protected void initControl() {
        super.initControl();
        comboBoxes.add(categorySelector);
    }
    
    @Override
    protected void extractData() {
        super.extractData();
        category = categorySelector.getValue();
    }
    
    @Override
    protected void editTask(Event e) {
        super.editTask(e);
        e.setStartDate(startDate);
        e.setEndDate(endDate);
        e.setCategory(category);
    }
        
    @Override
    protected Event createTask() {
        return new Event(name, startDate, endDate, category);
    }
    
    @Override
    protected void displayTask(Event e) {
        super.displayTask(e);   
        displayStartDate(e.getStartDate());
        displayEndDate(e.getEndDate());
        categorySelector.setValue(e.getCategory());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setEditableSelector(categorySelector);
        setEditableList(currentUser.getCategoryList());
        hasStartDate(true);
        hasEndDate(true);
        super.initialize(url, rb);
    }
}