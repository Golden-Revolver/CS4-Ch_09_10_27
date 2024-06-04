package taskvisualizer.controllers;

import taskvisualizer.wrappers.WrappedImageView;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import taskvisualizer.FontBinder;
import taskvisualizer.FontParameter;
import taskvisualizer.Goal;
import taskvisualizer.PaddingBinder;
import taskvisualizer.PaddingParameter;

/**
 * FXML Controller class
 *
 * @author Kiley Sorino & Christian Brandon
 */
public class GoalScreenController extends UniversalController implements Initializable {
    @FXML private VBox searchBox, goalsHeader, sidebar;
    @FXML private HBox searchBar, yearBox;
    @FXML private TextField searchField;
    @FXML private Text yearText;
    @FXML private Button createButton, prevButton, nextButton;
    @FXML private GridPane goals, months, goalContent;
    @FXML private ScrollPane scroll;
    @FXML private ComboBox<String> sortBox, statusBox;
    
    private YearMonth currentMonth;
    private int yearIndex = 0;
    private ArrayList<Integer> years = new ArrayList<>();
    private ArrayList<Goal> currentGoals = new ArrayList<>();
    private HBox activeMonthBox;
    private FontParameter.Builder goalBuilder, monthBuilder, 
            comboBoxBuilder, yearBuilder, titleBuilder;
    private Callable refresh;
    
    private DateTimeFormatter dateFormat = 
            DateTimeFormatter.ofPattern("MMMM dd, yyyy @ hh:mm a");
    
    private void initBuilders() {
        goalBuilder = new FontParameter.Builder()
            .family("Montserrat")
            .size(0.04)
            .widthSize(0.4);
        
        monthBuilder = new FontParameter.Builder()
            .family("Montserrat")
            .size(0.5)
            .widthSize(0.3);
        
        comboBoxBuilder = new FontParameter.Builder()
            .family("Montserrat")
            .widthSize(0.3)
            .widthSquareSize(0.0016);
        
        yearBuilder = new FontParameter.Builder()
            .family("Montserrat")
            .weight(FontWeight.BOLD)
            .widthSize(0.6)
            .heightSize(0.8);
        
        titleBuilder = new FontParameter.Builder()
            .family("Montserrat")
            .weight(FontWeight.BOLD)
            .size(0.2);
    }
    
    private void filterGoalsByName(String name) {
        for (int i = 0; i < currentGoals.size(); i++) {
            Goal e = currentGoals.get(i);
            boolean containsName = e.getName().toLowerCase().contains(name.toLowerCase());
            if (!containsName) {
                currentGoals.remove(e);
                i--;
            }
        }
    }
    
    private void filterGoalsByDate(String filterMethod) {
        for (int i = 0; i < currentGoals.size(); i++) {
            Goal e = currentGoals.get(i);
            LocalDateTime deadline = e.getDeadline();
            
            boolean isFuture = deadline.isAfter(LocalDateTime.now());
            boolean failsCondition;
                        
            switch (filterMethod) {
                case "Ongoing":
                    failsCondition = !isFuture;
                    break;
                case "Complete":
                    failsCondition = isFuture;
                    break;
                default:
                    failsCondition = false;
                    break;
            }
            
            if (failsCondition) {
                currentGoals.remove(e);
                i--;
            }
        }
    }
    
    private void setCurrentGoals(String name, String sortMethod, 
            String filterMethod) {
        currentGoals = currentUser.getGoalByMonth(currentMonth);
        if (name != null) filterGoalsByName(name);
        if (sortMethod != null) sortGoals(sortMethod);
        if (filterMethod != null) filterGoalsByDate(filterMethod);
        displayGoals(currentGoals);
    }
    
    private HBox createIconBox() {
        WrappedImageView edit = new WrappedImageView("edit");
        WrappedImageView notes = new WrappedImageView("notes");
        WrappedImageView delete = new WrappedImageView("delete");
        WrappedImageView progress = new WrappedImageView("progress");
        
        VBox editIcon = new VBox(edit);
        VBox notesIcon = new VBox(notes);
        VBox deleteIcon = new VBox(delete);
        VBox progressIcon = new VBox(progress);
        
        editIcon.setOnMousePressed(goal -> tryMethod(() -> editGoal(goal)));
        deleteIcon.setOnMousePressed(this::deleteGoal);
        progressIcon.setOnMousePressed(goal -> tryMethod(() -> editProgress(goal)));
        
        HBox iconBox = new HBox(editIcon, notesIcon, deleteIcon, progressIcon);
        iconBox.setAlignment(Pos.CENTER_LEFT);
        
        PaddingParameter iconPadding = new PaddingParameter.Builder()
            .size(0.05)
            .top(false)
            .bottom(false)
            .build();
        
        PaddingBinder iconPaddingBinder = new PaddingBinder(iconPadding);
        iconPaddingBinder.bind(iconBox);
        
        for (Node n : iconBox.getChildren()) {
            VBox v = (VBox) n;
            NumberBinding iconBoxSize = Bindings.min(iconBox.heightProperty().multiply(0.8), 
                    iconBox.widthProperty().multiply(0.3));
            v.maxWidthProperty().bind(iconBoxSize);
            v.maxHeightProperty().bind(iconBoxSize);
        }
        
        iconBox.spacingProperty().bind(iconBox.widthProperty().multiply(0.03));
        return iconBox;
    }
    
    private void deleteGoal(MouseEvent event) {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        
        currentUser.deleteTask(currentUser.getTaskById(iconBox.getId())); 
        setCurrentGoals(searchField.getText(), sortBox.getValue(), 
                statusBox.getValue());
    }
    
    private void editGoal(MouseEvent event) throws Exception {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        Goal g = (Goal) currentUser.getTaskById(iconBox.getId());
        
        activeTask = g;
        displayPopup("Goal Creation", "Goal_Creation_Screen", refresh);
    }
    
     private void editProgress(MouseEvent event) throws Exception {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        Goal g = (Goal) currentUser.getTaskById(iconBox.getId());
        
        activeTask = g;
        displayPopup("Goal Progress", "Goal_Progress_Screen", refresh);
    }
    
    private void displayGoals(ArrayList<Goal> goalList) {
        goals.getChildren().clear();
        goals.getRowConstraints().removeAll(goals.getRowConstraints());
                
        for (int i = 0; i < goalList.size(); i++) {
            Goal g = goalList.get(i);
            HBox iconBox = createIconBox();
            iconBox.setId(g.getId());
                             
            String goalProgress = String.valueOf(g.getProgress()), 
                    goalTarget = String.valueOf(g.getTarget());
            
            if (g.getProgress() % 1 == 0) goalProgress = 
                    String.format("%.0f", g.getProgress());
            if (g.getTarget() % 1 == 0) goalTarget = 
                    String.format("%.0f", g.getTarget());
            
            Text name = new Text(g.getName());
            Text progress = new Text(goalProgress + "/" + goalTarget);         
            Text endDate = new Text(g.getDeadline().format(dateFormat)); 
                        
            Pane line = new Pane();
            GridPane.setColumnSpan(line, GridPane.REMAINING);
            line.getStyleClass().add("row-border");
            
            PaddingParameter linePadding = new PaddingParameter.Builder()
                .size(0.05)
                .build();
            
            PaddingBinder linePaddingBinder = new PaddingBinder(linePadding);
            linePaddingBinder.reference(scroll).bind(line);

            goals.add(line, 0, i);
            goals.add(iconBox, 0, i);
                        
            RowConstraints row;
            ObservableList<RowConstraints> goalRows = goals.getRowConstraints();
            
            if (i < goalRows.size()) row = goalRows.get(i);
            else row = new RowConstraints();
                
            goals.getRowConstraints().add(row);
            goals.addRow(i, name, progress, endDate);    
            
            iconBox.maxWidthProperty().bind(scroll.widthProperty().multiply(0.125));
            
            FontBinder goalFontBinder = new FontBinder(goalBuilder.build());
            goalFontBinder.bind(name, scroll);
            goalFontBinder.bind(progress, scroll);
            goalFontBinder.bind(endDate, scroll);
        }
    }
    
    private void setActiveMonthBox(HBox monthBox) {     
        if (activeMonthBox != null) {
            // unselects the old month box
            activeMonthBox.getStyleClass().remove("activeMonth");
            Text activeMonthText = (Text) activeMonthBox.getChildren().get(0);
            
            FontBinder monthFontBinder = new FontBinder(monthBuilder.build());
            monthFontBinder.bind(activeMonthText, activeMonthBox);
        }
        monthBox.getStyleClass().add("activeMonth");
        Text monthText = (Text) monthBox.getChildren().get(0);
        
        FontParameter activeMonthFont = monthBuilder.newInstance()
                .weight(FontWeight.BOLD)
                .build();
        
        FontBinder activeMonthFontBinder = new FontBinder(activeMonthFont);
        activeMonthFontBinder.bind(monthText, monthBox);
        activeMonthBox = monthBox;
    }
    
    @FXML
    private void setYearMonth(MouseEvent event) {
        HBox monthBox = (HBox) event.getSource();
        setActiveMonthBox(monthBox);
        
        Text monthText = (Text) monthBox.getChildren().get(0);
        String month = monthText.getText();
        Month m = Month.valueOf(month.toUpperCase());
        
        currentMonth = YearMonth.of(years.get(yearIndex), m);
        setCurrentGoals(searchField.getText(), sortBox.getValue(), 
                statusBox.getValue());
    }
    
    @FXML
    private void prevYear(ActionEvent event) {        
        yearIndex--;
        yearText.setText(String.valueOf(years.get(yearIndex)));
        setMonthSidebar();
        
        nextButton.setDisable(false);
        if (yearIndex <= 0) prevButton.setDisable(true);
    }
    
    @FXML
    private void nextYear(ActionEvent event) {
        yearIndex++;
        yearText.setText(String.valueOf(years.get(yearIndex)));
        setMonthSidebar();
        
        prevButton.setDisable(false);
        if (yearIndex >= years.size() - 1) nextButton.setDisable(true);
    }
    
    private void setMonthSidebar() {
        TreeSet<Month> monthSet = new TreeSet<>();
        int row = 0;
        months.getChildren().clear();
        
        for (Goal g : currentUser.getGoalByYear(years.get(yearIndex))) {
            Month m = g.getDeadline().getMonth();
            monthSet.add(m);
        }
        for (Month m : monthSet) {
            Locale locale = new Locale("en");
            String month = m.getDisplayName(TextStyle.FULL_STANDALONE, locale);
            Text monthText = new Text(month);
            HBox monthBox = new HBox(monthText);
            
            FontBinder monthFontBinder = new FontBinder(monthBuilder.build());
            monthFontBinder.bind(monthText, monthBox);
            
            YearMonth yearMonth = YearMonth.of(years.get(yearIndex), m);
            if (yearMonth.equals(currentMonth)) setActiveMonthBox(monthBox);
            
            monthBox.setAlignment(Pos.CENTER);
            monthBox.getStyleClass().add("month");
            monthBox.setOnMousePressed(this::setYearMonth);
            months.addRow(row, monthBox);
            row++;
        }
    }
    
    private void initYears() {
        TreeSet<Integer> yearSet = new TreeSet<>();
        for (Goal g : currentUser.getGoalList()) {
            yearSet.add(g.getDeadline().getYear());
        }
        years.addAll(yearSet);
     
        // defaults to current year if a goal is due then
        int currentYear = Year.now().getValue();
        if (years.contains(currentYear)) yearIndex = years.indexOf(currentYear);
        
        yearText.setText(String.valueOf(years.get(yearIndex)));
        if (yearIndex <= 0) prevButton.setDisable(true);
        if (yearIndex >= years.size() - 1) nextButton.setDisable(true);
    }
    
    private void initComboBoxes() {
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
                        try {
                            String iconName = item.toLowerCase().replace(" ", "-");
                            WrappedImageView icon = new WrappedImageView(iconName);
                            VBox iconBox = new VBox(icon);
                            iconBox.setAlignment(Pos.CENTER);
                            itemBox.getChildren().add(iconBox);
                            
                            iconBox.prefHeightProperty().bind(itemBox.heightProperty());
                            iconBox.prefWidthProperty().bind(iconBox.prefHeightProperty());
                        } catch (Exception e) {
                            System.out.println("Image not available.");
                        }
                        itemBox.getChildren().add(itemText);
                        itemBox.setAlignment(Pos.CENTER_LEFT);
                        itemBox.spacingProperty().bind(itemBox.widthProperty().multiply(0.05));
                        
                        if (this.getParent() instanceof ComboBox) {
                            ComboBox comboBox = (ComboBox) this.getParent();
                            VBox outerBox = (VBox) comboBox.getParent();
                            
                            FontParameter outerFont = comboBoxBuilder.newInstance()
                                    .size(0.2)
                                    .build();
                            
                            FontBinder outerFontBinder = new FontBinder(outerFont);
                            outerFontBinder.bind(itemText, outerBox);
                        } else {
                            FontParameter itemFont = comboBoxBuilder.newInstance()
                                    .size(0.2)
                                    .heightSize(3)
                                    .build();
                            
                            FontBinder itemFontBinder = new FontBinder(itemFont);
                            itemFontBinder.bind(itemText, itemBox);
                        }
                        
                        setGraphic(itemBox);
                     }
                }
            };
                
        sortBox.getItems().add("Earliest to Latest");
        sortBox.getItems().add("Latest to Earliest");
        sortBox.getItems().add("A to Z");
        sortBox.getItems().add("Z to A");
                        
        sortBox.setCellFactory(cellFormat);
        sortBox.setButtonCell(cellFormat.call(null));
        
        statusBox.getItems().add("Incomplete");
        statusBox.getItems().add("Ongoing");
        statusBox.getItems().add("Complete");
        statusBox.getItems().add("Any");
        
        statusBox.setCellFactory(cellFormat);
        statusBox.setButtonCell(cellFormat.call(null));
        
    }
    
    private void sortGoals(String sortMethod) {
        switch (sortMethod) {
            case "Earliest to Latest":
                currentGoals.sort((Goal g1, Goal g2) -> g1.getDeadline().compareTo(g2.getDeadline()));
                break;
            case "Latest to Earliest":
                currentGoals.sort((Goal g1, Goal g2) -> g2.getDeadline().compareTo(g1.getDeadline()));
                break;
            case "A to Z":
                currentGoals.sort((Goal g1, Goal g2) -> g1.getName().compareToIgnoreCase(g2.getName()));
                break;
            case "Z to A":
                currentGoals.sort((Goal g1, Goal g2) -> g2.getName().compareToIgnoreCase(g1.getName()));
                break;
            default:
                break;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initBuilders();
        initHeader(currentScreen);
        
        WrappedImageView searchIcon = new WrappedImageView("magnifying-glass");
        VBox searchIconBox = new VBox(searchIcon);
        searchIconBox.setAlignment(Pos.CENTER_RIGHT);
        
        NumberBinding searchBoxSize = Bindings.min(searchBox.heightProperty().multiply(0.4), 
                searchBox.widthProperty().multiply(0.2));
        searchBox.setMinHeight(0);
        
        PaddingParameter searchPadding = new PaddingParameter.Builder()
            .size(0.1)
            .build();
        
        PaddingBinder searchPaddingBinder = new PaddingBinder(searchPadding);
        searchPaddingBinder.bind(searchBox);
        
        searchIconBox.minWidthProperty().bind(searchBoxSize);
        searchIconBox.prefWidthProperty().bind(searchBoxSize);
        
        FontParameter searchFont = new FontParameter.Builder()
            .family("Montserrat")
            .size(0.15)
            .build();
        
        FontBinder searchFontBinder = new FontBinder(searchFont);
        searchFontBinder.bind(searchField, searchBox);
        
        searchField.widthProperty().addListener((observable, oldValue, value) -> {
            String input = searchField.getText();
            searchField.clear();
            searchField.setText(input);
            searchField.end();
        });     
        searchBar.getChildren().add(searchIconBox);
        
        searchField.textProperty().addListener((observable, oldValue, value) -> {
            setCurrentGoals(value, sortBox.getValue(), 
                    statusBox.getValue());
        });
        
        sortBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentGoals(searchField.getText(), value, 
                    statusBox.getValue());
        });
        
        statusBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentGoals(searchField.getText(), sortBox.getValue(), 
                    value);
        });
        
                
        createButton.setOnAction(createActionHandler(() -> 
                displayPopup("Goal Creation", "Goal_Creation_Screen", refresh)));
        
        goalsHeader.spacingProperty().bind(goalsHeader.heightProperty().multiply(0.11));
        
        PaddingParameter headerPadding = new PaddingParameter.Builder()
            .size(0.11)
            .build();
        
        PaddingBinder headerPaddingBinder = new PaddingBinder(headerPadding);
        headerPaddingBinder.bind(goalsHeader);
        
        VBox prevBox = (VBox) prevButton.getParent();
        VBox nextBox = (VBox) nextButton.getParent();
        
        FontParameter buttonFont = yearBuilder.newInstance()
                .size(0.15)
                .build();
        
        FontBinder buttonFontBinder = new FontBinder(buttonFont);
        buttonFontBinder.bind(prevButton, yearBox);
        buttonFontBinder.bind(nextButton, yearBox);
                                
        FontParameter yearFont = yearBuilder.newInstance()
                .size(0.35)
                .widthSquareSize(0.005)
                .build();
        
        FontBinder yearFontBinder = new FontBinder(yearFont);
        yearFontBinder.bind(yearText, yearBox);

        prevButton.prefWidthProperty().bind(prevBox.widthProperty().multiply(0.6));
        nextButton.prefWidthProperty().bind(nextBox.widthProperty().multiply(0.6));
        
        ObjectBinding<Insets> buttonPaddingTracker = Bindings.createObjectBinding(() -> {
            double vPadding = Math.min(yearBox.getWidth() * 0.01, yearBox.getHeight() * 0.01);
            double hPadding = vPadding * 4;
            return new Insets(vPadding, hPadding, vPadding, hPadding);
        }, yearBox.widthProperty(), yearBox.heightProperty());
        
        prevButton.paddingProperty().bind(buttonPaddingTracker);
        nextButton.paddingProperty().bind(buttonPaddingTracker);
        
        yearBox.minHeightProperty().bind(sidebar.heightProperty().multiply(0.2));
        months.minHeightProperty().bind(sidebar.heightProperty().multiply(0.7));
        sidebar.maxWidthProperty().bind(goalContent.widthProperty().multiply(0.2));
                
        StackPane stack = (StackPane) createButton.getParent();
        NumberBinding stackSize = Bindings.min(stack.widthProperty(), stack.heightProperty());
        
        createButton.prefWidthProperty().bind(stackSize.multiply(0.1));
        createButton.prefHeightProperty().bind(stackSize.multiply(0.1));
        
        FontParameter createFont = new FontParameter.Builder()
            .family("Montserrat")
            .weight(FontWeight.BOLD)
            .size(0.075)
            .build();
        
        FontBinder createFontBinder = new FontBinder(createFont);
        createFontBinder.bind(createButton, stack);
        
        for (int i = 1; i < 4; i++) {
            Text title = (Text) goalContent.getChildren().get(i);
            FontParameter headingFont = new FontParameter.Builder()
                .family("Montserrat")
                .size(0.03)
                .build();
            
            FontBinder headingFontBinder = new FontBinder(headingFont);
            headingFontBinder.bind(title, goalContent);
        }
        
        Text goalsTitle = (Text) goalsHeader.getChildren().get(0);
        Label searchLabel = (Label) searchBox.getChildren().get(0);
                
        FontParameter goalTitleFont = titleBuilder.newInstance()
                .widthSize(0.25)
                .heightSquareSize(0.02)
                .build();
        
        FontBinder goalTitleFontBinder = new FontBinder(goalTitleFont);
        goalTitleFontBinder.bind(goalsTitle, goalsHeader);
        
        FontParameter searchTitleFont = titleBuilder.newInstance()
                .widthSize(0.4)
                .build();
        
        FontBinder searchTitleFontBinder = new FontBinder(searchTitleFont);
        searchTitleFontBinder.bind(searchLabel, searchBox);

        HBox goalsBar = (HBox) goalsHeader.getChildren().get(1);
        HBox goalsInterface = (HBox) goalsBar.getChildren().get(1);
        
        goalsInterface.setMinWidth(0);
        searchBox.prefWidthProperty().bind(goalsBar.widthProperty().multiply(0.3));
        
        for (Node n : goalsInterface.getChildren()) {
            VBox box = (VBox) n;
            box.setMinWidth(0);
            Label boxTitle = (Label) box.getChildren().get(0);
            ComboBox comboBox = (ComboBox) box.getChildren().get(1);
            comboBox.setMinHeight(0);
            
            FontParameter boxTitleFont = titleBuilder.newInstance()
                .widthSize(0.5)
                .build();
            
            FontBinder boxTitleFontBinder = new FontBinder(boxTitleFont);
            boxTitleFontBinder.bind(boxTitle, box);
            
            PaddingParameter boxPadding = new PaddingParameter.Builder()
                .size(0.1)
                .build();
            
            PaddingBinder boxPaddingBinder = new PaddingBinder(boxPadding);
            boxPaddingBinder.bind(box);
            
            box.prefWidthProperty().bind(goalsInterface.widthProperty().divide(3));
        }
        
        primaryStage.showingProperty().addListener((observable, oldValue, value) -> {
            Region sortArrow = (Region) sortBox.lookup(".arrow");
            Region statusArrow = (Region) statusBox.lookup(".arrow");
                            
            ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() -> {
                double vPadding = Math.min(goalsHeader.getWidth() * 0.01, goalsHeader.getHeight() * 0.05);
                double hPadding = vPadding * 1.25;
                return new Insets(vPadding, hPadding, vPadding, hPadding);
            }, goalsHeader.widthProperty(), goalsHeader.heightProperty());
        
            sortArrow.paddingProperty().bind(paddingTracker);
            statusArrow.paddingProperty().bind(paddingTracker);
        });
        
        initYears();
        setMonthSidebar();
        initComboBoxes();
    }
}