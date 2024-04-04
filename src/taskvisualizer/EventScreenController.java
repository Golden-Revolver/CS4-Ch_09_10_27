package taskvisualizer;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import com.sun.javafx.scene.control.skin.ListViewSkin;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Christian Brandon
 */
public class EventScreenController extends UniversalController implements Initializable {
    @FXML private VBox searchBox, eventsHeader, sidebar;
    @FXML private HBox searchBar, yearBox;
    @FXML private TextField searchField;
    @FXML private Text yearText;
    @FXML private Button createButton, prevButton, nextButton;
    @FXML private GridPane events, months, eventContent;
    @FXML private ScrollPane scroll;
    @FXML private ComboBox<String> sortBox, statusBox, categoryBox;
    
    private YearMonth currentMonth;
    private int yearIndex = 0;
    private ArrayList<Integer> years = new ArrayList<>();
    private ArrayList<Event> currentEvents = new ArrayList<>();
    private HBox activeMonthBox;
    
    private void filterEventsByName(String name) {
        for (int i = 0; i < currentEvents.size(); i++) {
            Event e = currentEvents.get(i);
            boolean containsName = e.getName().toLowerCase().contains(name.toLowerCase());
            if (!containsName) {
                currentEvents.remove(e);
                i--;
            }
        }
    }
    
    private void filterEventsByDate(String filterMethod) {
        for (int i = 0; i < currentEvents.size(); i++) {
            Event e = currentEvents.get(i);
            LocalDateTime startDate = e.getDate();
            LocalDateTime endDate = e.getDate().plusDays(1); // placeholder
            
            boolean isPast = endDate.isBefore(LocalDateTime.now());
            boolean isFuture = startDate.isAfter(LocalDateTime.now());
            boolean failsCondition;
                        
            switch (filterMethod) {
                case "Incomplete":
                    failsCondition = !isFuture;
                    break;
                case "Ongoing":
                    failsCondition = isPast || isFuture;
                    break;
                case "Complete":
                    failsCondition = !isPast;
                    break;
                default:
                    failsCondition = false;
                    break;
            }
            
            if (failsCondition) {
                currentEvents.remove(e);
                i--;
            }
        }
    }
    
    private void setCurrentEvents(String name, String sortMethod, String filterMethod) {
        currentEvents = currentUser.getEventByMonth(currentMonth);
        if (name != null) filterEventsByName(name);
        if (sortMethod != null) sortEvents(sortMethod);
        if (filterMethod != null) filterEventsByDate(filterMethod);
        displayEvents(currentEvents);
    }
    
    private HBox createIconBox() {
        WrappedImageView edit = new WrappedImageView(getImage("edit"));
        WrappedImageView notes = new WrappedImageView(getImage("notes"));
        WrappedImageView delete = new WrappedImageView(getImage("delete"));
        
        VBox editIcon = new VBox(edit);
        VBox notesIcon = new VBox(notes);
        VBox deleteIcon = new VBox(delete);
        
        editIcon.setOnMousePressed(this::editEventHandler);
        deleteIcon.setOnMousePressed(this::deleteEvent);
        
        HBox iconBox = new HBox(editIcon, notesIcon, deleteIcon);
        iconBox.setAlignment(Pos.CENTER_LEFT);
        
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
    
    private void deleteEvent(MouseEvent event) {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        
        currentUser.deleteTask(currentUser.getTaskById(iconBox.getId())); 
        setCurrentEvents(searchField.getText(), sortBox.getValue(), statusBox.getValue());
    }
    
    private void editEvent(MouseEvent event) throws Exception {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        Event e = (Event) currentUser.getTaskById(iconBox.getId());
        
        activeTask = e;
        displayPopup("Event_Creation_Screen");
    }
    private void editEventHandler(MouseEvent event) {
        try {
            editEvent(event);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void displayEvents(ArrayList<Event> eventList) {
        removeAllChildren(events);
        events.getRowConstraints().removeAll(events.getRowConstraints());
                
        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            HBox iconBox = createIconBox();
            iconBox.setId(e.getId());
            
            Text name = new Text(e.getName());
            Text category = new Text(""); // placeholder
            Text startDate = new Text(formatDate(e.getDate()));
            Text endDate = new Text(formatDate(e.getDate().plusDays(1))); // placeholder 
                        
            Pane line = new Pane();
            GridPane.setColumnSpan(line, GridPane.REMAINING);
            line.getStyleClass().add("row-border");
            Binder.bindPadding(line, scroll, 0.05);

            events.add(line, 0, i);
            events.add(iconBox, 0, i);
                        
            RowConstraints row;
            ObservableList<RowConstraints> eventRows = events.getRowConstraints();
            
            if (i < eventRows.size()) row = eventRows.get(i);
            else row = new RowConstraints();
                
            events.getRowConstraints().add(row);
            events.addRow(i, name, category, startDate, endDate);    
            
            iconBox.maxWidthProperty().bind(scroll.widthProperty().multiply(0.125));
            Binder.bindFont("Montserrat", name, scroll, 0.04, 0.4, 1);
            Binder.bindFont("Montserrat", category, scroll, 0.04, 0.4, 1);
            Binder.bindFont("Montserrat", startDate, scroll, 0.04, 0.4, 1);
            Binder.bindFont("Montserrat", endDate, scroll, 0.04, 0.4, 1);
        }
    }
    
    private void setActiveMonthBox(HBox monthBox) {
        if (activeMonthBox != null) {
            // unselects the old month box
            activeMonthBox.getStyleClass().remove("activeMonth");
            Text activeMonthText = (Text) activeMonthBox.getChildren().get(0);
            Binder.bindFont("Montserrat", activeMonthText, activeMonthBox, 0.5, 0.3, 1);
        }
        monthBox.getStyleClass().add("activeMonth");
        Text monthText = (Text) monthBox.getChildren().get(0);
        Binder.bindFont("Montserrat", FontWeight.BOLD, monthText, monthBox, 0.5, 0.3, 1);
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
        setCurrentEvents(searchField.getText(), sortBox.getValue(), statusBox.getValue());
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
        removeAllChildren(months);
        
        for (Event e : currentUser.getEventByYear(years.get(yearIndex))) {
            Month m = e.getDate().getMonth();
            monthSet.add(m);
        }
        for (Month m : monthSet) {
            Locale locale = new Locale("en");
            String month = m.getDisplayName(TextStyle.FULL_STANDALONE, locale);
            Text monthText = new Text(month);
            HBox monthBox = new HBox(monthText);
            
            Binder.bindFont("Montserrat", monthText, monthBox, 0.5, 0.3, 1);
            
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
        for (Event e : currentUser.getEventList()) {
            yearSet.add(e.getDate().getYear());
        }
        years.addAll(yearSet);
     
        // defaults to current year if an event is due then
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
                        String iconName = item.toLowerCase().replace(" ", "-");
                        WrappedImageView icon = new WrappedImageView(getImage(iconName));
                        VBox iconBox = new VBox(icon);
                        iconBox.setAlignment(Pos.CENTER);
                        
                        Text itemText = new Text(item);
                        HBox itemBox = new HBox(iconBox, itemText);
                        itemBox.setAlignment(Pos.CENTER_LEFT);
                        itemBox.spacingProperty().bind(itemBox.widthProperty().multiply(0.05));
                        
                        if (this.getParent() instanceof ComboBox) {
                            ComboBox comboBox = (ComboBox) this.getParent();
                            VBox outerBox = (VBox) comboBox.getParent();
                            Binder.bindFont("Montserrat", FontWeight.NORMAL, itemText, outerBox,
                                    0.2, 0.3, 0.0016, 1, Double.POSITIVE_INFINITY);
                        } else {
                            Binder.bindFont("Montserrat", FontWeight.NORMAL, itemText, itemBox,
                               0.4, 0.3, 0.0016, 2, Double.POSITIVE_INFINITY);
                        }
                                                
                        iconBox.prefHeightProperty().bind(itemBox.heightProperty());
                        iconBox.prefWidthProperty().bind(iconBox.prefHeightProperty());
                        
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
        
        // add category list here...
        categoryBox.getItems().add("All");
        
        categoryBox.setCellFactory(cellFormat);
        categoryBox.setButtonCell(cellFormat.call(null));
    }
    
    private void sortEvents(String sortMethod) {
        switch (sortMethod) {
            case "Earliest to Latest":
                currentEvents.sort((Event e1, Event e2) -> e1.getDate().compareTo(e2.getDate()));
                break;
            case "Latest to Earliest":
                currentEvents.sort((Event e1, Event e2) -> e2.getDate().compareTo(e1.getDate()));
                break;
            case "A to Z":
                currentEvents.sort((Event e1, Event e2) -> e1.getName().compareToIgnoreCase(e2.getName()));
                break;
            case "Z to A":
                currentEvents.sort((Event e1, Event e2) -> e2.getName().compareToIgnoreCase(e1.getName()));
                break;
            default:
                break;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initHeader(currentScreen);
        Image magnifyingGlass = new Image(getClass().getResourceAsStream("images/magnifying-glass.png"));
        WrappedImageView searchIcon = new WrappedImageView(magnifyingGlass);
        
        VBox searchIconBox = new VBox(searchIcon);
        searchIconBox.setAlignment(Pos.CENTER_RIGHT);
        
        NumberBinding searchBoxSize = Bindings.min(searchBox.heightProperty().multiply(0.4), 
                searchBox.widthProperty().multiply(0.2));
        searchBox.setMinHeight(0);
        Binder.bindPadding(searchBox, 0.1);
        
        searchIconBox.minWidthProperty().bind(searchBoxSize);
        searchIconBox.prefWidthProperty().bind(searchBoxSize);
        
        Binder.bindFont("Montserrat", searchField, searchBox, 0.15);
        
        searchField.widthProperty().addListener((observable, oldValue, value) -> {
            String input = searchField.getText();
            searchField.clear();
            searchField.setText(input);
            searchField.end();
        });     
        searchBar.getChildren().add(searchIconBox);
        
        searchField.textProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(value, sortBox.getValue(), statusBox.getValue());
        });
        
        sortBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(searchField.getText(), value, statusBox.getValue());
        });
        
        statusBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(searchField.getText(), sortBox.getValue(), value);
        });
        
        createButton.setOnAction(displayPopupActionHandler("Event_Creation_Screen"));
        eventsHeader.spacingProperty().bind(eventsHeader.heightProperty().multiply(0.11));
        Binder.bindPadding(eventsHeader, 0.11);   
        Binder.bindPadding(events, 0.01, true, false, true, false, true);
                
        VBox prevBox = (VBox) prevButton.getParent();
        VBox nextBox = (VBox) nextButton.getParent();
                                
        Binder.bindFont("Montserrat", FontWeight.BOLD, prevButton, yearBox, 0.15, 0.6, 0.8);
        Binder.bindFont("Montserrat", FontWeight.BOLD, nextButton, yearBox, 0.15, 0.6, 0.8);
        Binder.bindFont("Montserrat", FontWeight.BOLD, yearText, yearBox, 0.35, 
                0.6, 0.005, 0.8, Double.POSITIVE_INFINITY);

        prevButton.prefWidthProperty().bind(prevBox.widthProperty().multiply(0.6));
        nextButton.prefWidthProperty().bind(nextBox.widthProperty().multiply(0.6));
        
        Binder.bindPadding(prevButton, 0.1);
        ObjectBinding<Insets> buttonPaddingTracker = Bindings.createObjectBinding(() -> {
            double vPadding = Math.min(yearBox.getWidth() * 0.01, yearBox.getHeight() * 0.01);
            double hPadding = vPadding * 4;
            return new Insets(vPadding, hPadding, vPadding, hPadding);
        }, yearBox.widthProperty(), yearBox.heightProperty());
        
        prevButton.paddingProperty().bind(buttonPaddingTracker);
        nextButton.paddingProperty().bind(buttonPaddingTracker);
        
        yearBox.minHeightProperty().bind(sidebar.heightProperty().multiply(0.2));
        months.minHeightProperty().bind(sidebar.heightProperty().multiply(0.7));
        sidebar.maxWidthProperty().bind(eventContent.widthProperty().multiply(0.2));
                
        StackPane stack = (StackPane) createButton.getParent();
        NumberBinding stackSize = Bindings.min(stack.widthProperty(), stack.heightProperty());
        
        createButton.prefWidthProperty().bind(stackSize.multiply(0.1));
        createButton.prefHeightProperty().bind(stackSize.multiply(0.1));
        Binder.bindFont("Montserrat", FontWeight.BOLD, createButton, stack, 0.075);
        
        for (int i = 1; i < 5; i++) {
            Text title = (Text) eventContent.getChildren().get(i);
            Binder.bindFont("Montserrat", title, eventContent, 0.03);
        }
        
        Text eventsTitle = (Text) eventsHeader.getChildren().get(0);
        Label searchLabel = (Label) searchBox.getChildren().get(0);
                
        Binder.bindFont("Montserrat", FontWeight.BOLD, eventsTitle, eventsHeader, 
                0.2, 0.25, Double.POSITIVE_INFINITY, 1, 0.02);
        Binder.bindFont("Montserrat", FontWeight.BOLD, searchLabel, searchBox, 0.2, 0.4, 1);
        
        HBox eventsBar = (HBox) eventsHeader.getChildren().get(1);
        HBox eventsInterface = (HBox) eventsBar.getChildren().get(1);
        
        eventsInterface.setMinWidth(0);
        searchBox.prefWidthProperty().bind(eventsBar.widthProperty().multiply(0.3));
        
        for (Node n : eventsInterface.getChildren()) {
            VBox box = (VBox) n;
            box.setMinWidth(0);
            Label boxTitle = (Label) box.getChildren().get(0);
            ComboBox comboBox = (ComboBox) box.getChildren().get(1);
            comboBox.setMinHeight(0);
            
            Binder.bindFont("Montserrat", FontWeight.BOLD, boxTitle, box, 0.2, 0.5, 1);
            Binder.bindPadding(box, 0.1);
            box.prefWidthProperty().bind(eventsInterface.widthProperty().divide(3));
        }
        
        primaryStage.showingProperty().addListener((observable, oldValue, value) -> {
            Region sortArrow = (Region) sortBox.lookup(".arrow");
            Region statusArrow = (Region) statusBox.lookup(".arrow");
            Region categoryArrow = (Region) categoryBox.lookup(".arrow");
                            
            ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() -> {
                double vPadding = Math.min(eventsHeader.getWidth() * 0.01, eventsHeader.getHeight() * 0.05);
                double hPadding = vPadding * 1.25;
                return new Insets(vPadding, hPadding, vPadding, hPadding);
            }, eventsHeader.widthProperty(), eventsHeader.heightProperty());
        
            sortArrow.paddingProperty().bind(paddingTracker);
            statusArrow.paddingProperty().bind(paddingTracker);
            categoryArrow.paddingProperty().bind(paddingTracker);
        });
                
        initYears();
        setMonthSidebar();
        initComboBoxes();
    }
}