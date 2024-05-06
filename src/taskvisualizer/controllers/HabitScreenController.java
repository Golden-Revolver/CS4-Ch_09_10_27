package taskvisualizer.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Callback;
import taskvisualizer.Habit;
import static taskvisualizer.controllers.UniversalController.activeTask;
import static taskvisualizer.controllers.UniversalController.currentUser;
import static taskvisualizer.controllers.UniversalController.getImage;
import static taskvisualizer.controllers.UniversalController.removeAllChildren;
import taskvisualizer.wrappers.WrappedImageView;

/**
 * FXML Controller class
 *
 * @author Christian Brandon
 */
public class HabitScreenController extends UniversalController implements Initializable {
    @FXML private GridPane layout, content, habits;
    @FXML private StackPane stackHeader;
    @FXML private HBox menuHeader, searchBar;
    @FXML private Text title, searchTitle, sortTitle, statusTitle;
    @FXML private VBox searchBox, sortBox, statusBox;
    @FXML private ComboBox<String> sortSelect, statusSelect;
    @FXML private TextField searchField;
    @FXML private Button createButton, sortButton;
    
    private ArrayList<Habit> currentHabits = new ArrayList<>();
    private String name, sortMethod, filterMethod;
    private boolean sortOrder = true; // true if ascending, false if descending
    private Callable refresh;

    /**
     * Initializes the controller class.
     */
    
    private void initRefresh() {
        refresh = () -> {
            setCurrentHabits();
            return null;
        };
    }
    
    private void filterHabitsByName() {
        filterTasksByName(name, currentHabits);
    }   
 
    private void filterHabitsByStatus() {
        if (filterMethod.equals("Any")) return;  
        for (int i = 0; i < currentHabits.size(); i++) {
            Habit h = currentHabits.get(i);
            boolean filterType = filterMethod.equals("Complete");
            if (!filterType == h.isComplete()) {
                currentHabits.remove(h);
                i--;
            }
        }
    }
    
    private void sortHabits() {
        Comparator<Habit> c = (Habit h1, Habit h2) -> {  
            switch (sortMethod) {
                case "Alphabetical":
                    return h1.getName().compareToIgnoreCase(h2.getName());
                case "Streak":
                    return h1.getStreak() - h2.getStreak();
                case "Consistency":
                    return Double.compare(h1.getConsistency(), h2.getConsistency());
                default:
                    return 0;
            }
        };
        if (!sortOrder) c = c.reversed();
        currentHabits.sort(c);
    }
    
    private void setCurrentHabits() {
        currentHabits = copyArrayList(currentUser.getHabitList());
        if (name != null) filterHabitsByName();
        if (sortMethod != null) sortHabits();
        if (filterMethod != null) filterHabitsByStatus();
        displayHabits(currentHabits);
    }
    
    private HBox createIconBox() {        
        // Gets the icon images
        WrappedImageView edit = new WrappedImageView(getImage("edit"));
        WrappedImageView notes = new WrappedImageView(getImage("notes"));
        WrappedImageView delete = new WrappedImageView(getImage("delete"));
                
        // Places the images in a VBox
        VBox editIcon = new VBox(edit);
        VBox notesIcon = new VBox(notes);
        VBox deleteIcon = new VBox(delete);
        
        ArrayList<VBox> icons = new ArrayList<>();
        icons.add(editIcon);
        icons.add(notesIcon);
        icons.add(deleteIcon);
        
        // Create checkbox
        CheckBox checkBox = new CheckBox();
        
        // Creates event handlers for icons
        checkBox.setOnMousePressed(this::checkHabit);
        editIcon.setOnMousePressed(event -> tryMethod(() -> editHabit(event)));
        notesIcon.setOnMousePressed(event -> tryMethod(() -> openNotes(event)));
        deleteIcon.setOnMousePressed(this::deleteHabit);
        
        // Places icons in an HBox
        HBox iconBox = new HBox(checkBox, editIcon, notesIcon, deleteIcon);
        iconBox.setAlignment(Pos.CENTER_LEFT);
        iconBox.spacingProperty().bind(iconBox.widthProperty().multiply(0.03));
        
        for (int i = 0; i < icons.size(); i++) {
            VBox icon = icons.get(i);
            icon.setAlignment(Pos.CENTER);
            icon.setPadding(Insets.EMPTY);
            icon.maxWidthProperty().bind(iconBox.heightProperty().multiply(0.8));
            icon.maxHeightProperty().bind(iconBox.heightProperty().multiply(0.8));
        }
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
        
        setCurrentHabits();
    }
    
    private Habit getSelectedHabit(MouseEvent event) {
        VBox icon = (VBox) event.getSource();
        HBox iconBox = (HBox) icon.getParent();
        return (Habit) currentUser.getTaskById(iconBox.getId());
    }
    
    private void openNotes(MouseEvent event) throws Exception {
        activeTask = getSelectedHabit(event);      
        displayPopup("Notes", "Note_Screen", refresh);
    }
    
    private void deleteHabit(MouseEvent event) {
        currentUser.deleteTask(getSelectedHabit(event));
        setCurrentHabits();
    }
    
    private void editHabit(MouseEvent event) throws Exception {
        activeTask = getSelectedHabit(event);
        displayPopup("Habit Creation", "Habit_Creation_Screen", refresh);
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
    
    private void displayHabits(ArrayList<Habit> habitList) {
        // Removes all displayed habits and rows
        removeAllChildren(habits);
        habits.getRowConstraints().removeAll(habits.getRowConstraints());
        
        for (int i = 0; i < habitList.size(); i++) {
            Habit h = habitList.get(i);
            HBox iconBox = createIconBox();
            iconBox.setId(h.getId());
            
            CheckBox checkBox = (CheckBox) iconBox.getChildren().get(0);
            if (h.isComplete()) checkBox.setSelected(true);
            
            Text name = new Text(h.getName());
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

            // Creates a line separating each habit
            Pane line = new Pane();
            GridPane.setColumnSpan(line, GridPane.REMAINING);
            line.getStyleClass().add("row-border");
            
            // addRow skips cells with elements in them
            habits.add(line, 0,i);
            habits.add(iconBox, 0, i);
            
            habits.getRowConstraints().add(new RowConstraints());
            habits.addRow(i, name, week, streak, consistency);            
        }
    }
    
    private void initComboBoxes() {
        HashMap<String, String> icons = new HashMap<>();
        // sort box
        icons.put("Alphabetical", "a-to-z");
        icons.put("Streak", "star");
        icons.put("Consistency", "fire");

        // status box
        icons.put("Complete", "complete");
        icons.put("Incomplete", "incomplete");
        icons.put("Any", "all");
        
        Callback<ListView<String>, ListCell<String>> cellFormat =
            (ListView<String> p) -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {       
                        Text itemText = new Text(item);
                        HBox itemBox = new HBox();

                        String iconName = icons.get(item);
                        WrappedImageView icon = new WrappedImageView(getImage(iconName));
                        VBox iconBox = new VBox(icon);
                        iconBox.setAlignment(Pos.CENTER);
                        itemBox.getChildren().add(iconBox);

                        iconBox.prefHeightProperty().bind(itemBox.heightProperty());
                        iconBox.prefWidthProperty().bind(iconBox.prefHeightProperty());

                        itemBox.getChildren().add(itemText);
                        itemBox.setAlignment(Pos.CENTER_LEFT);
                        itemBox.setSpacing(10);
                        
                        setGraphic(itemBox);
                     }
                }
            };
        
        sortSelect.getItems().add("Alphabetical");
        sortSelect.getItems().add("Streak");
        sortSelect.getItems().add("Consistency");
        
        sortSelect.setCellFactory(cellFormat);
        sortSelect.setButtonCell(cellFormat.call(null));
        
        WrappedImageView upArrow = new WrappedImageView(getImage("up-arrow"));
        WrappedImageView downArrow = new WrappedImageView(getImage("down-arrow"));
        sortButton.setGraphic(upArrow);
        
        sortButton.setOnMousePressed((event) -> {
            if (sortOrder) sortButton.setGraphic(downArrow);
            else sortButton.setGraphic(upArrow);
            sortOrder = !sortOrder;
            setCurrentHabits();
        });
        
        statusSelect.getItems().add("Complete");
        statusSelect.getItems().add("Incomplete");
        statusSelect.getItems().add("Any");
        
        statusSelect.setCellFactory(cellFormat);
        statusSelect.setButtonCell(cellFormat.call(null));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initHeader(currentScreen);
        initRefresh();
        initComboBoxes();

        WrappedImageView searchIcon = new WrappedImageView(getImage("magnifying-glass"));
        VBox searchIconBox = new VBox(searchIcon);
        searchIconBox.setAlignment(Pos.CENTER_RIGHT);
        searchIconBox.maxWidthProperty().bind(searchIconBox.prefHeightProperty());
        searchIconBox.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.05));
        
        searchBar.getChildren().add(searchIconBox);
        
        primaryStage.setOnShown((event) -> {
            sortButton.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.05));
            sortButton.prefWidthProperty().bind(sortButton.prefHeightProperty());
            sortButton.minWidthProperty().bind(sortButton.prefWidthProperty());
            createButton.prefWidthProperty().bind(createButton.heightProperty());
        });
        
        searchField.textProperty().addListener((observable, oldValue, value) -> {
            name = value;
            setCurrentHabits();
        });
        
        sortSelect.valueProperty().addListener((observable, oldValue, value) -> {
            sortMethod = value;
            setCurrentHabits();
        });
        
        statusSelect.valueProperty().addListener((observable, oldValue, value) -> {
            filterMethod = value;
            setCurrentHabits();
        });
        
        currentHabits = copyArrayList(currentUser.getHabitList());
        displayHabits(currentHabits);
        
        createButton.setOnAction(createActionHandler(() -> 
                displayPopup("Habit Creation", "Habit_Creation_Screen", refresh)));
    }    
}