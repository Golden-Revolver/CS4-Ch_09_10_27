package taskvisualizer.controllers;

import taskvisualizer.wrappers.WrappedImageView;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
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
import taskvisualizer.Event;
import taskvisualizer.FontBinder;
import taskvisualizer.PaddingBinder;

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
    private FontBinder.Builder eventBuilder, monthBuilder, 
            comboBoxBuilder, yearBuilder, titleBuilder;
    private Callable refresh;
    
    private void initRefresh() {
        refresh = () -> {
            String activeCategory = categoryBox.getSelectionModel().getSelectedItem();
            categoryBox.getItems().subList(1, categoryBox.getItems().size()).clear();
            categoryBox.getItems().addAll(currentUser.getCategoryList());
            
            if (categoryBox.getItems().contains(activeCategory)) {
                categoryBox.getSelectionModel().select(activeCategory);
            }
            setCurrentEvents(searchField.getText(), sortBox.getValue(), 
                    statusBox.getValue(), categoryBox.getValue());
            return null;
        };
    }
    
    private void initBuilders() {
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
            LocalDateTime startDate = e.getStartDate();
            LocalDateTime endDate = e.getEndDate();
            
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
    
    private void filterEventsByCategory(String category) {
        if (category.equals("All")) return;
        for (int i = 0; i < currentEvents.size(); i++) {
            Event e = currentEvents.get(i);
            if (!e.getCategory().equals(category)) {
                currentEvents.remove(e);
                i--;
            }
        }
    }
    
    private void setCurrentEvents(String name, String sortMethod, 
            String filterMethod, String category) {
        currentEvents = currentUser.getEventByMonth(currentMonth);
        if (name != null) filterEventsByName(name);
        if (sortMethod != null) sortEvents(sortMethod);
        if (filterMethod != null) filterEventsByDate(filterMethod);
        if (category != null) filterEventsByCategory(category);
        displayEvents(currentEvents);
    }
    
    private HBox createIconBox() {
        WrappedImageView edit = new WrappedImageView(getImage("edit"));
        WrappedImageView notes = new WrappedImageView(getImage("notes"));
        WrappedImageView delete = new WrappedImageView(getImage("delete"));
        
        VBox editIcon = new VBox(edit);
        VBox notesIcon = new VBox(notes);
        VBox deleteIcon = new VBox(delete);
        
        editIcon.setOnMousePressed(event -> tryMethod(() -> editEvent(event)));
        deleteIcon.setOnMousePressed(this::deleteEvent);
        
        HBox iconBox = new HBox(editIcon, notesIcon, deleteIcon);
        iconBox.setAlignment(Pos.CENTER_LEFT);
        
        PaddingBinder iconPadding = new PaddingBinder.Builder()
            .size(0.05)
            .top(false)
            .bottom(false)
            .build(iconBox);
        Binder.bindPadding(iconPadding);
        
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
        setCurrentEvents(searchField.getText(), sortBox.getValue(), 
                statusBox.getValue(), categoryBox.getValue());
    }
    
    private void editEvent(MouseEvent event) throws Exception {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        Event e = (Event) currentUser.getTaskById(iconBox.getId());
        
        activeTask = e;
        displayPopup("Event Creation", "Event_Creation_Screen", refresh);
    }
    
    private void displayEvents(ArrayList<Event> eventList) {
        removeAllChildren(events);
        events.getRowConstraints().removeAll(events.getRowConstraints());
                
        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            HBox iconBox = createIconBox();
            iconBox.setId(e.getId());
            
            Text name = new Text(e.getName());
            Text category = new Text(e.getCategory());
            Text startDate = new Text(formatDate(e.getStartDate()));
            Text endDate = new Text(formatDate(e.getEndDate())); 
                        
            Pane line = new Pane();
            GridPane.setColumnSpan(line, GridPane.REMAINING);
            line.getStyleClass().add("row-border");
            
            PaddingBinder linePadding = new PaddingBinder.Builder()
                .size(0.05)
                .reference(scroll)
                .build(line);
            Binder.bindPadding(linePadding);

            events.add(line, 0, i);
            events.add(iconBox, 0, i);
                        
            RowConstraints row;
            ObservableList<RowConstraints> eventRows = events.getRowConstraints();
            
            if (i < eventRows.size()) row = eventRows.get(i);
            else row = new RowConstraints();
                
            events.getRowConstraints().add(row);
            events.addRow(i, name, category, startDate, endDate);    
            
            iconBox.maxWidthProperty().bind(scroll.widthProperty().multiply(0.125));
            
            FontBinder nameFont = eventBuilder.newInstance().build(name, scroll);
            FontBinder categoryFont = eventBuilder.newInstance().build(category, scroll);
            FontBinder startDateFont = eventBuilder.newInstance().build(startDate, scroll);
            FontBinder endDateFont = eventBuilder.newInstance().build(endDate, scroll);
            
            Binder.bindFont(nameFont);
            Binder.bindFont(categoryFont);
            Binder.bindFont(startDateFont);
            Binder.bindFont(endDateFont);
        }
    }
    
    private void setActiveMonthBox(HBox monthBox) {     
        if (activeMonthBox != null) {
            // unselects the old month box
            activeMonthBox.getStyleClass().remove("activeMonth");
            Text activeMonthText = (Text) activeMonthBox.getChildren().get(0);
            
            FontBinder monthFont = monthBuilder.newInstance()
                    .build(activeMonthText, activeMonthBox);
            Binder.bindFont(monthFont);
        }
        monthBox.getStyleClass().add("activeMonth");
        Text monthText = (Text) monthBox.getChildren().get(0);
        
        FontBinder activeMonthFont = monthBuilder.newInstance()
                .weight(FontWeight.BOLD)
                .build(monthText, monthBox);
        Binder.bindFont(activeMonthFont);
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
        setCurrentEvents(searchField.getText(), sortBox.getValue(), 
                statusBox.getValue(), categoryBox.getValue());
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
            Month m = e.getStartDate().getMonth();
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
            yearSet.add(e.getStartDate().getYear());
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
                        Text itemText = new Text(item);
                        HBox itemBox = new HBox();
                        try {
                            String iconName = item.toLowerCase().replace(" ", "-");
                            WrappedImageView icon = new WrappedImageView(getImage(iconName));
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
                            
                            FontBinder outerFont = comboBoxBuilder.newInstance()
                                    .size(0.2)
                                    .build(itemText, outerBox);
                            Binder.bindFont(outerFont);
                        } else {
                            FontBinder itemFont = comboBoxBuilder.newInstance()
                                    .size(0.2)
                                    .heightSize(3)
                                    .build(itemText, itemBox);
                            Binder.bindFont(itemFont);
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
        
        categoryBox.getItems().add("All");
        categoryBox.getItems().addAll(currentUser.getCategoryList());
        
        categoryBox.setCellFactory(cellFormat);
        categoryBox.setButtonCell(cellFormat.call(null));
    }
    
    private void sortEvents(String sortMethod) {
        switch (sortMethod) {
            case "Earliest to Latest":
                currentEvents.sort((Event e1, Event e2) -> e1.getStartDate().compareTo(e2.getStartDate()));
                break;
            case "Latest to Earliest":
                currentEvents.sort((Event e1, Event e2) -> e2.getStartDate().compareTo(e1.getStartDate()));
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
        initRefresh();
        initBuilders();
        initHeader(currentScreen);
        
        WrappedImageView searchIcon = new WrappedImageView(getImage("magnifying-glass"));
        VBox searchIconBox = new VBox(searchIcon);
        searchIconBox.setAlignment(Pos.CENTER_RIGHT);
        
        NumberBinding searchBoxSize = Bindings.min(searchBox.heightProperty().multiply(0.4), 
                searchBox.widthProperty().multiply(0.2));
        searchBox.setMinHeight(0);
        
        PaddingBinder searchPadding = new PaddingBinder.Builder()
            .size(0.1)
            .build(searchBox);
        Binder.bindPadding(searchPadding);
        
        searchIconBox.minWidthProperty().bind(searchBoxSize);
        searchIconBox.prefWidthProperty().bind(searchBoxSize);
        
        FontBinder searchFont = new FontBinder.Builder()
            .family("Montserrat")
            .size(0.15)
            .build(searchField, searchBox);
        Binder.bindFont(searchFont);
        
        searchField.widthProperty().addListener((observable, oldValue, value) -> {
            String input = searchField.getText();
            searchField.clear();
            searchField.setText(input);
            searchField.end();
        });     
        searchBar.getChildren().add(searchIconBox);
        
        searchField.textProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(value, sortBox.getValue(), 
                    statusBox.getValue(), categoryBox.getValue());
        });
        
        sortBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(searchField.getText(), value, 
                    statusBox.getValue(), categoryBox.getValue());
        });
        
        statusBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(searchField.getText(), sortBox.getValue(), 
                    value, categoryBox.getValue());
        });
        
        categoryBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentEvents(searchField.getText(), sortBox.getValue(),
                    statusBox.getValue(), value);
        });
                
        createButton.setOnAction(createActionHandler(() -> 
                displayPopup("Event Creation", "Event_Creation_Screen", refresh)));
        
        eventsHeader.spacingProperty().bind(eventsHeader.heightProperty().multiply(0.11));
        
        PaddingBinder headerPadding = new PaddingBinder.Builder()
            .size(0.11)
            .build(eventsHeader);
        Binder.bindPadding(headerPadding); 
                
        VBox prevBox = (VBox) prevButton.getParent();
        VBox nextBox = (VBox) nextButton.getParent();
                                
        FontBinder prevButtonFont = yearBuilder.newInstance()
                .size(0.15)
                .build(prevButton, yearBox);
        FontBinder nextButtonFont = yearBuilder.newInstance()
                .size(0.15)
                .build(nextButton, yearBox);
        FontBinder yearFont = yearBuilder.newInstance()
                .size(0.35)
                .widthSquareSize(0.005)
                .build(yearText, yearBox);
        
        Binder.bindFont(prevButtonFont);
        Binder.bindFont(nextButtonFont);
        Binder.bindFont(yearFont);

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
        sidebar.maxWidthProperty().bind(eventContent.widthProperty().multiply(0.2));
                
        StackPane stack = (StackPane) createButton.getParent();
        NumberBinding stackSize = Bindings.min(stack.widthProperty(), stack.heightProperty());
        
        createButton.prefWidthProperty().bind(stackSize.multiply(0.1));
        createButton.prefHeightProperty().bind(stackSize.multiply(0.1));
        
        FontBinder createFont = new FontBinder.Builder()
            .family("Montserrat")
            .weight(FontWeight.BOLD)
            .size(0.075)
            .build(createButton, stack);
        Binder.bindFont(createFont);
        
        for (int i = 1; i < 5; i++) {
            Text title = (Text) eventContent.getChildren().get(i);
            FontBinder headingFont = new FontBinder.Builder()
                .family("Montserrat")
                .size(0.03)
                .build(title, eventContent);
            Binder.bindFont(headingFont);
        }
        
        Text eventsTitle = (Text) eventsHeader.getChildren().get(0);
        Label searchLabel = (Label) searchBox.getChildren().get(0);
                
        FontBinder eventTitleFont = titleBuilder.newInstance()
                .widthSize(0.25)
                .heightSquareSize(0.02)
                .build(eventsTitle, eventsHeader);
        FontBinder searchTitleFont = titleBuilder.newInstance()
                .widthSize(0.4)
                .build(searchLabel, searchBox);
        
        Binder.bindFont(eventTitleFont);
        Binder.bindFont(searchTitleFont);
        
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
            
            FontBinder boxTitleFont = titleBuilder.newInstance()
                .widthSize(0.5)
                .build(boxTitle, box);
            Binder.bindFont(boxTitleFont);
            
            PaddingBinder boxPadding = new PaddingBinder.Builder()
                .size(0.1)
                .build(box);
            Binder.bindPadding(boxPadding);
            
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