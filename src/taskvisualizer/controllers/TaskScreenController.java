package taskvisualizer.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import taskvisualizer.FontBinder;
import taskvisualizer.Task;
import static taskvisualizer.controllers.UniversalController.activeTask;
import static taskvisualizer.controllers.UniversalController.currentUser;
import static taskvisualizer.controllers.UniversalController.getImage;
import static taskvisualizer.controllers.UniversalController.removeAllChildren;
import taskvisualizer.wrappers.WrappedImageView;

/**
 *
 * @author Christian Brandon
 * @param <T>
 */
public abstract class TaskScreenController<T extends Task> extends UniversalController {
    @FXML protected GridPane tasks, months;
    @FXML protected HBox searchBar;
    @FXML protected Text yearText;
    @FXML protected TextField searchField;
    @FXML protected ComboBox<String> sortSelect, statusSelect;
    @FXML protected Button createButton, sortButton, prevButton, nextButton;

    protected HBox activeMonthBox;
    protected YearMonth currentMonth;
    protected int yearIndex = 0;
    protected ArrayList<Integer> years = new ArrayList<>();
    
    protected ArrayList<T> taskList, currentTasks;
    protected String name, sortMethod, filterMethod, creationScreen, creationTitle;
    protected boolean sortOrder = true;
    protected Callable refresh;
    
    protected FontBinder.Builder eventBuilder, monthBuilder, 
            comboBoxBuilder, yearBuilder, titleBuilder;
    
    protected void setTaskList(ArrayList<T> taskList) {
        this.taskList = taskList;
    }
    
    protected void setCreationScreen(String creationScreen) {
        this.creationScreen = creationScreen;
        creationTitle = creationScreen.replace("_", " ")
                .replace("Screen", "").trim();
    }
    
    protected void displayAddedTask() {
        if (activeTask != null) {
            T t = (T) activeTask;
            YearMonth yearMonth = YearMonth.from(getDate(t));
            initYears();
            yearIndex = years.indexOf(yearMonth.getYear());

            setYear();
            setMonthSidebar();
            setYearMonth(yearMonth);
        }
    }
             
    protected void refresh() {
        displayAddedTask();
        displayTasks();
    }

    protected void initRefresh() {
        refresh = () -> {
            refresh();
            return null;
        };
    }
    
    protected void initBuilders() {
        eventBuilder = new FontBinder.Builder()
            .family("Montserrat")
            .size(0.04)
            .widthSize(0.4);
        
        monthBuilder = new FontBinder.Builder()
            .family("Montserrat")
            .size(0.5)
            .widthSize(0.3);
        
        comboBoxBuilder = new FontBinder.Builder()
            .family("Montserrat")
            .widthSize(0.3)
            .widthSquareSize(0.0016);
        
        yearBuilder = new FontBinder.Builder()
            .family("Montserrat")
            .weight(FontWeight.BOLD)
            .widthSize(0.6)
            .heightSize(0.8);
        
        titleBuilder = new FontBinder.Builder()
            .family("Montserrat")
            .weight(FontWeight.BOLD)
            .size(0.2);
    }
        
    protected void filterTasks(Function<T, Boolean> condition) {
        for (int i = 0; i < currentTasks.size(); i++) {
            T t = currentTasks.get(i);
            if (!condition.apply(t)) {
                currentTasks.remove(t);
                i--;
            }
        }
    }
    
    protected void filterTasksByName() {
        filterTasks((T t) -> t.getName().toLowerCase().contains(name.toLowerCase()));
    }
    
    protected void filterTasksByCheck() {
        if (filterMethod.equals("Any")) return;
        boolean filterType = filterMethod.equals("Complete");
        filterTasks((T t) -> filterType == t.isComplete());
    }
    
    protected void filterTasksByDate() {
        filterTasks((T t) -> checkTaskDate(getStartDate(t), getEndDate(t)));
    }
    
    protected abstract void filterTasksByStatus();

    protected boolean checkTaskDate(LocalDateTime startDate, LocalDateTime endDate) {
        boolean isPast = endDate.isBefore(LocalDateTime.now());
        boolean isFuture = startDate.isAfter(LocalDateTime.now());
        boolean condition;

        switch (filterMethod) {
            case "Incomplete":
                condition = isFuture;
                break;
            case "Ongoing":
                condition = !isPast && !isFuture;
                break;
            case "Complete":
                condition = isPast;
                break;
            default:
                condition = true;
                break;
        }
        return condition;
    }
        
    protected LocalDateTime getStartDate(T t) {
        return LocalDateTime.MIN;
    }
    
    protected LocalDateTime getEndDate(T t) {
        return LocalDateTime.MAX;
    }
    
    protected LocalDateTime getDate(T t) {
        LocalDateTime startDate = getStartDate(t);
        LocalDateTime endDate = getEndDate(t);
        
        boolean minStartDate = startDate.equals(LocalDateTime.MIN);
        boolean maxEndDate = endDate.equals(LocalDateTime.MAX);
                
        if (maxEndDate) return null;
        else if (minStartDate) return endDate;
        else return startDate;
    }

    protected int compareTasks(T t1, T t2) {
        if (sortMethod.equals("Alphabetical")) {
            return t1.getName().compareToIgnoreCase(t2.getName());
        } else return 0;
    }
    
    protected void sortTasks() {
        Comparator<T> c = (T t1, T t2) -> compareTasks(t1, t2);
        if (!sortOrder) c = c.reversed();
        currentTasks.sort(c);
    }
        
    protected void initCurrentTasks() {
        currentTasks = copyArrayList(taskList);
    }
    
    protected void processCurrentTasks() {
        initCurrentTasks();
        if (name != null) filterTasksByName();
        if (sortMethod != null) sortTasks();
        if (filterMethod != null) filterTasksByStatus();
    }
    
     protected HBox createIconBox(T t) {        
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
        
        // Creates event handlers for icons
        editIcon.setOnMousePressed(event -> tryMethod(() -> editTask(event)));
        notesIcon.setOnMousePressed(event -> tryMethod(() -> openNotes(event)));
        deleteIcon.setOnMousePressed(this::deleteTask);
        
        // Places icons in an HBox
        HBox iconBox = new HBox(editIcon, notesIcon, deleteIcon);
        iconBox.setAlignment(Pos.CENTER_LEFT);
        iconBox.spacingProperty().bind(iconBox.widthProperty().multiply(0.03));
        
        for (int i = 0; i < icons.size(); i++) {
            VBox icon = icons.get(i);
            icon.setAlignment(Pos.CENTER);
            icon.setPadding(Insets.EMPTY);
            icon.maxWidthProperty().bind(iconBox.heightProperty().multiply(0.8));
            icon.maxHeightProperty().bind(iconBox.heightProperty().multiply(0.8));
        }
        
        iconBox.setId(t.getId());
        return iconBox;
    }
    
    protected T getSelectedTask(MouseEvent event) {
        VBox icon = (VBox) event.getSource();
        HBox iconBox = (HBox) icon.getParent();
        return (T) currentUser.getTaskById(iconBox.getId());
    }
    
    protected void openNotes(MouseEvent event) throws Exception {
        activeTask = getSelectedTask(event);
        displayPopup("Notes", "Note_Screen", refresh);
    }
    
    protected void deleteTask(MouseEvent event) {
        currentUser.deleteTask(getSelectedTask(event));
        refresh();
    }
    
    protected void editTask(MouseEvent event) throws Exception {
        activeTask = getSelectedTask(event);
        displayPopup(creationTitle, creationScreen, refresh);
    }
    
    protected ArrayList<Node> createTaskRow(T t) {
        Text name = new Text(t.getName()); 
        ArrayList<Node> taskRow = new ArrayList<>();
        taskRow.add(name);
        return taskRow;
    }
    
    protected Pane createLine() {
        // Creates a line separating each task
        Pane line = new Pane();
        GridPane.setColumnSpan(line, GridPane.REMAINING);
        line.getStyleClass().add("row-border");
        return line;
    }
    
    protected void displayTasks() {
        processCurrentTasks();
        removeAllChildren(tasks);
        tasks.getRowConstraints().removeAll(tasks.getRowConstraints());
        
        for (int i = 0; i < currentTasks.size(); i++) {
            T t = currentTasks.get(i);
            HBox iconBox = createIconBox(t);
            Pane line = createLine();
            
            // addRow skips cells with elements in them
            tasks.add(line, 0, i);
            tasks.add(iconBox, 0, i);
            
            ArrayList<Node> taskRow = createTaskRow(t);
            tasks.getRowConstraints().add(new RowConstraints());                                    
            tasks.addRow(i, taskRow.toArray(new Node[taskRow.size()]));
        }
    }
    
    protected void setActiveMonthBox(HBox monthBox) {
        if (activeMonthBox != null) {
            // unselects the old month box
            activeMonthBox.getStyleClass().remove("activeMonth");
            Text activeMonthText = (Text) activeMonthBox.getChildren().get(0);
            
            FontBinder monthFont = monthBuilder.newInstance()
                    .build(activeMonthText, activeMonthBox);
            Binder.bindFont(monthFont);
        }
        
        // selects the new month box
        monthBox.getStyleClass().add("activeMonth");
        Text monthText = (Text) monthBox.getChildren().get(0);
        
        FontBinder activeMonthFont = monthBuilder.newInstance()
                .weight(FontWeight.BOLD)
                .build(monthText, monthBox);
        Binder.bindFont(activeMonthFont);
        activeMonthBox = monthBox;
    }
    
    protected void setYearMonth(HBox monthBox) {
        setActiveMonthBox(monthBox);
        
        Text monthText = (Text) monthBox.getChildren().get(0);
        String month = monthText.getText();
        Month m = Month.valueOf(month.toUpperCase());
        
        currentMonth = YearMonth.of(years.get(yearIndex), m);
        displayTasks();
    }
    
    protected void setYearMonth(YearMonth yearMonth) {
        for (int i = 0; i < months.getChildren().size(); i++) {
            HBox monthBox = (HBox) months.getChildren().get(i);
            YearMonth monthDate = YearMonth.parse(monthBox.getId());
            if (monthDate.equals(yearMonth)) setYearMonth(monthBox);
        }
    }
    
    @FXML
    protected void setYearMonth(MouseEvent event) {
        HBox monthBox = (HBox) event.getSource();
        setYearMonth(monthBox);
    }
    
    @FXML
    protected void prevYear(ActionEvent event) {
        yearIndex--;
        yearText.setText(String.valueOf(years.get(yearIndex)));
        setMonthSidebar();
        
        nextButton.setDisable(false);
        if (yearIndex <= 0) prevButton.setDisable(true);        
    }
    
    @FXML
    protected void nextYear(ActionEvent event) {
        yearIndex++;
        yearText.setText(String.valueOf(years.get(yearIndex)));
        setMonthSidebar();
        
        prevButton.setDisable(false);
        if (yearIndex >= years.size() - 1) nextButton.setDisable(true);
    }
    
    protected ArrayList<T> getTasksByYear(int year) {
        ArrayList<T> filteredList = new ArrayList<>();
        for (T t : taskList) {
            if (getDate(t).getYear() == year) filteredList.add(t);
        }
        return filteredList;
    }
    
    protected void setMonthSidebar() {
        TreeSet<Month> monthSet = new TreeSet<>();
        int row = 0;
        removeAllChildren(months);
        
        for (T t : getTasksByYear(years.get(yearIndex))) {
            Month m = getDate(t).getMonth();
            monthSet.add(m);
        }
        
        for (Month m : monthSet) {
            Locale locale = new Locale("en");
            String month = m.getDisplayName(TextStyle.FULL_STANDALONE, locale);
            Text monthText = new Text(month);
            HBox monthBox = new HBox(monthText);
            
            FontBinder monthFont = monthBuilder.newInstance().build(monthText, monthBox);
            Binder.bindFont(monthFont);
            
            YearMonth yearMonth = YearMonth.of(years.get(yearIndex), m);
            if (yearMonth.equals(currentMonth)) setActiveMonthBox(monthBox);
            monthBox.setId(yearMonth.toString());
            
            monthBox.setAlignment(Pos.CENTER);
            monthBox.getStyleClass().add("month");
            monthBox.setOnMousePressed(this::setYearMonth);
            months.addRow(row, monthBox);
            row++;
        }
    }
    
    protected void initYears() {
        TreeSet<Integer> yearSet = new TreeSet<>();
        for (T t : taskList) {
            yearSet.add(getDate(t).getYear());
        }
        years.addAll(yearSet);
    }
    
    protected void initYearIndex() {
        // defaults to current year if a task is due then
        int currentYear = Year.now().getValue();
        if (years.contains(currentYear)) yearIndex = years.indexOf(currentYear);
    }
    
    protected void setYear() {
        yearText.setText(String.valueOf(years.get(yearIndex)));
        if (yearIndex <= 0) prevButton.setDisable(true);
        if (yearIndex >= years.size() - 1) nextButton.setDisable(true);
    }
    
    protected HashMap<String, String> linkComboIcons() {
        HashMap<String, String> icons = new HashMap<>();
        icons.put("Alphabetical", "a-to-z");
        icons.put("Complete", "complete");
        icons.put("Incomplete", "incomplete");
        return icons;
    }
    
    protected HBox createItemBox(String item) {
        HashMap<String, String> icons = linkComboIcons();
        
        Text itemText = new Text(item);
        HBox itemBox = new HBox();

        if (icons.containsKey(item)) {
            String iconName = icons.get(item);
            WrappedImageView icon = new WrappedImageView(getImage(iconName));
            VBox iconBox = new VBox(icon);
            iconBox.setAlignment(Pos.CENTER);
            itemBox.getChildren().add(iconBox);
            
            iconBox.prefHeightProperty().bind(itemBox.heightProperty());
            iconBox.prefWidthProperty().bind(iconBox.prefHeightProperty());
        }
        
        itemBox.getChildren().add(itemText);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setSpacing(10);
        return itemBox;
    }
    
    protected void addComboItems() {
        sortSelect.getItems().add("Alphabetical");
        statusSelect.getItems().add("Complete");
        statusSelect.getItems().add("Incomplete");
        statusSelect.getItems().add("Any");
    }
    
    protected ListCell<String> createListCell() {
        return new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox itemBox = createItemBox(item);
                    setGraphic(itemBox);
                }
            }
        };
    }
    
    protected Callback<ListView<String>, ListCell<String>> createCellFactory() {
        return (ListView<String> p) -> createListCell();
    }
        
    protected void initComboBoxes() {
        HashMap<String, String> icons = linkComboIcons();
        Callback<ListView<String>, ListCell<String>> cellFormat = createCellFactory();

        addComboItems();
        sortSelect.setCellFactory(cellFormat);
        sortSelect.setButtonCell(cellFormat.call(null));
                
        statusSelect.setCellFactory(cellFormat);
        statusSelect.setButtonCell(cellFormat.call(null));
        
        WrappedImageView upArrow = new WrappedImageView(getImage("up-arrow"));
        WrappedImageView downArrow = new WrappedImageView(getImage("down-arrow"));
        sortButton.setGraphic(upArrow);
        
        sortButton.setOnMousePressed((event) -> {
            if (sortOrder) sortButton.setGraphic(downArrow);
            else sortButton.setGraphic(upArrow);
            sortOrder = !sortOrder;
            refresh();
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initHeader(currentScreen);
        initRefresh();
        initBuilders();
        initComboBoxes();
        initYears();
        initYearIndex();
        setYear();
        setMonthSidebar();
        
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
            displayTasks();
        });
        
        sortSelect.valueProperty().addListener((observable, oldValue, value) -> {
            sortMethod = value;
            displayTasks();
        });
        
        statusSelect.valueProperty().addListener((observable, oldValue, value) -> {
            filterMethod = value;
            displayTasks();
        });
        
        createButton.setOnAction(createActionHandler(() ->
                displayPopup(creationTitle, creationScreen, refresh)));
        
        displayTasks();
    }
}