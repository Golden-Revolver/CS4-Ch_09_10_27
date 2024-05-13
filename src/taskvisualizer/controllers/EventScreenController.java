package taskvisualizer.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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
public class EventScreenController extends TaskScreenController<Event> implements Initializable {
    @FXML private VBox searchBox, eventsHeader, sidebar;
    @FXML private HBox yearBox;
    @FXML private GridPane eventContent;
    @FXML private ScrollPane scroll;
    @FXML private ComboBox<String> categorySelect;

    private String category;
    
    @Override
    protected void refresh() {
        String activeCategory = categorySelect.getSelectionModel().getSelectedItem();
        categorySelect.getItems().subList(1, categorySelect.getItems().size()).clear();
        categorySelect.getItems().addAll(currentUser.getCategoryList());
        
        if (categorySelect.getItems().contains(activeCategory)) {
            categorySelect.getSelectionModel().select(activeCategory);
        }
        super.refresh();
    }
    
    @Override
    protected LocalDateTime getStartDate(Event e) {
        return e.getStartDate();
    }
    
    @Override
    protected LocalDateTime getEndDate(Event e) {
        return e.getEndDate();
    }
    
    @Override
    protected void filterTasksByStatus() {
        filterTasksByDate();
    }
    
    private void filterEventsByCategory() {
        if (category.equals("All")) return;
        filterTasks((Event e) -> e.getCategory().equals(category));
    }
    
    @Override
    protected void initCurrentTasks() {
        currentTasks = currentUser.getEventByMonth(currentMonth);
    }
    
    @Override
    protected void processCurrentTasks() {
        super.processCurrentTasks();
        if (category != null) filterEventsByCategory();
    }
    
    @Override
    protected HBox createIconBox(Event e) {
        HBox iconBox = super.createIconBox(e);
        PaddingBinder iconPadding = new PaddingBinder.Builder()
            .size(0.05)
            .top(false)
            .bottom(false)
            .build(iconBox);
        Binder.bindPadding(iconPadding);
        iconBox.maxWidthProperty().bind(scroll.widthProperty().multiply(0.125));
        return iconBox;
    }
    
    @Override
    protected ArrayList<Node> createTaskRow(Event e) {
        ArrayList<Node> taskRow = super.createTaskRow(e);
        
        Text name = (Text) taskRow.get(0);
        Text category = new Text(e.getCategory());
        Text startDate = new Text(formatDate(e.getStartDate()));
        Text endDate = new Text(formatDate(e.getEndDate()));
        
        FontBinder nameFont = eventBuilder.newInstance().build(name, scroll);
        FontBinder categoryFont = eventBuilder.newInstance().build(category, scroll);
        FontBinder startDateFont = eventBuilder.newInstance().build(startDate, scroll);
        FontBinder endDateFont = eventBuilder.newInstance().build(endDate, scroll);

        Binder.bindFont(nameFont);
        Binder.bindFont(categoryFont);
        Binder.bindFont(startDateFont);
        Binder.bindFont(endDateFont);
        
        taskRow.add(category);
        taskRow.add(startDate);
        taskRow.add(endDate);
        return taskRow;
    }
    
    @Override
    protected Pane createLine() {
        Pane line = super.createLine();
        PaddingBinder linePadding = new PaddingBinder.Builder()
            .size(0.05)
            .reference(scroll)
            .build(line);
        Binder.bindPadding(linePadding);
        return line;
    }
    
    @Override
    protected HashMap<String, String> linkComboIcons() {
        HashMap<String, String> icons = super.linkComboIcons();
        icons.put("Date", "earliest-to-latest");
        icons.put("Ongoing", "ongoing");
        icons.put("Any", "any");
        icons.put("All", "all");
        return icons;
    }
    
    @Override
    protected void addComboItems() {
        super.addComboItems();
        sortSelect.getItems().add("Date");
        statusSelect.getItems().add("Ongoing");
        categorySelect.getItems().add("All");
        categorySelect.getItems().addAll(currentUser.getCategoryList());
    }
    
    @Override
    protected ListCell<String> createListCell() {
        ListCell<String> listCell = super.createListCell();
        if (listCell.getGraphic() != null) {
            HBox itemBox = (HBox) listCell.getGraphic();
            int lastIndex = itemBox.getChildren().size() - 1;
            Text itemText = (Text) itemBox.getChildren().get(lastIndex);
            itemBox.spacingProperty().bind(itemBox.widthProperty().multiply(0.05));

            if (listCell.getParent() instanceof ComboBox) {
                ComboBox comboBox = (ComboBox) listCell.getParent();
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
        }
        return listCell;
    }
    
    @Override
    protected Callback<ListView<String>, ListCell<String>> createCellFactory() {
        Callback<ListView<String>, ListCell<String>> cellFormat = 
                super.createCellFactory();
        categorySelect.setCellFactory(cellFormat);
        categorySelect.setButtonCell(cellFormat.call(null));
        return cellFormat;
    }
    
    @Override
    protected int compareTasks(Event e1, Event e2) {
        if (sortMethod.equals("Date")) {
            return e1.getStartDate().compareTo(e2.getStartDate());
        } else return super.compareTasks(e1, e2);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTaskList(currentUser.getEventList());
        setCreationScreen("Event_Creation_Screen");
        super.initialize(url, rb);
        
        categorySelect.valueProperty().addListener((observable, oldValue, value) -> {
            category = value;
            displayTasks();
        });
        
        searchBox.setMinHeight(0);
        
        PaddingBinder searchPadding = new PaddingBinder.Builder()
            .size(0.1)
            .build(searchBox);
        Binder.bindPadding(searchPadding);
        
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
            Node boxBar = box.getChildren().get(1);
            ComboBox comboBox;

            if (boxBar instanceof HBox) {
                HBox bar = (HBox) boxBar;
                comboBox = (ComboBox) bar.getChildren().get(0);
            } else {
                comboBox = (ComboBox) boxBar;
            }            
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
            Region sortArrow = (Region) sortSelect.lookup(".arrow");
            Region statusArrow = (Region) statusSelect.lookup(".arrow");
            Region categoryArrow = (Region) categorySelect.lookup(".arrow");
                            
            ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() -> {
                double vPadding = Math.min(eventsHeader.getWidth() * 0.01, eventsHeader.getHeight() * 0.05);
                double hPadding = vPadding * 1.25;
                return new Insets(vPadding, hPadding, vPadding, hPadding);
            }, eventsHeader.widthProperty(), eventsHeader.heightProperty());
        
            sortArrow.paddingProperty().bind(paddingTracker);
            statusArrow.paddingProperty().bind(paddingTracker);
            categoryArrow.paddingProperty().bind(paddingTracker);
        });
    }
}