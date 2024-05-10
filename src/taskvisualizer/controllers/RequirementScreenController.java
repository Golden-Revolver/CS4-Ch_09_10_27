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
import taskvisualizer.Requirement;
import taskvisualizer.FontBinder;
import taskvisualizer.PaddingBinder;

/**
 * FXML Controller class
 *
 * @author Kiley Sorino & Christian Brandon 
 */
public class RequirementScreenController extends UniversalController implements Initializable {
    @FXML private VBox searchBox, requirementsHeader, sidebar;
    @FXML private HBox searchBar, yearBox;
    @FXML private TextField searchField;
    @FXML private Text yearText;
    @FXML private Button createButton, prevButton, nextButton;
    @FXML private GridPane requirements, months, requirementContent;
    @FXML private ScrollPane scroll;
    @FXML private ComboBox<String> sortBox, statusBox, subjectBox;
    
    private YearMonth currentMonth;
    private int yearIndex = 0;
    private ArrayList<Integer> years = new ArrayList<>();
    private ArrayList<Requirement> currentRequirements = new ArrayList<>();
    private HBox activeMonthBox;
    private FontBinder.Builder requirementBuilder, monthBuilder, 
            comboBoxBuilder, yearBuilder, titleBuilder;
    private Callable refresh;
    
    private void initRefresh() {
        refresh = () -> {
            String activeCategory = subjectBox.getSelectionModel().getSelectedItem();
            subjectBox.getItems().subList(1, subjectBox.getItems().size()).clear();
            subjectBox.getItems().addAll(currentUser.getCategoryList());
            
            if (subjectBox.getItems().contains(activeCategory)) {
                subjectBox.getSelectionModel().select(activeCategory);
            }

            if (activeTask != null) {
                Requirement r = (Requirement) activeTask;
                YearMonth yearMonth = YearMonth.from(r.getDeadline());
                int year = yearMonth.getYear();
                
                initYears();
                setYear(year);
                setMonthSidebar();
                setYearMonth(yearMonth);
            } 
            
            setCurrentRequirements(searchField.getText(), sortBox.getValue(), 
                    statusBox.getValue(), subjectBox.getValue());
            return null;
        };
    }
    
    private void initBuilders() {
        requirementBuilder = new FontBinder.Builder()
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
    
    private void filterRequirementsByName(String name) {
        for (int i = 0; i < currentRequirements.size(); i++) {
            Requirement r = currentRequirements.get(i);
            boolean containsName = r.getName().toLowerCase().contains(name.toLowerCase());
            if (!containsName) {
                currentRequirements.remove(r);
                i--;
            }
        }
    }
    
    private void filterRequirementsByDate(String filterMethod) {
        for (int i = 0; i < currentRequirements.size(); i++) {
            Requirement r = currentRequirements.get(i);
            LocalDateTime deadline = r.getDeadline();
            
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
                currentRequirements.remove(r);
                i--;
            }
        }
    }
    
    private void filterRequirementsBySubject(String subject) {
        if (subject.equals("All")) return;
        for (int i = 0; i < currentRequirements.size(); i++) {
            Requirement r = currentRequirements.get(i);
            if (!r.getSubject().equals(subject)) {
                currentRequirements.remove(r);
                i--;
            }
        }
    }
    
    private void setCurrentRequirements(String name, String sortMethod, 
            String filterMethod, String subject) {
        currentRequirements = currentUser.getRequirementByMonth(currentMonth);
        if (name != null) filterRequirementsByName(name);
        if (sortMethod != null) sortRequirements(sortMethod);
        if (filterMethod != null) filterRequirementsByDate(filterMethod);
        if (subject != null) filterRequirementsBySubject(subject);
        displayRequirements(currentRequirements);
    }
    
    private HBox createIconBox() {
        WrappedImageView edit = new WrappedImageView(getImage("edit"));
        WrappedImageView notes = new WrappedImageView(getImage("notes"));
        WrappedImageView delete = new WrappedImageView(getImage("delete"));
        
        VBox editIcon = new VBox(edit);
        VBox notesIcon = new VBox(notes);
        VBox deleteIcon = new VBox(delete);
        
        editIcon.setOnMousePressed(event -> tryMethod(() -> editRequirement(event)));
        deleteIcon.setOnMousePressed(this::deleteRequirement);
        
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
    
    private void deleteRequirement(MouseEvent event) {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        
        currentUser.deleteTask(currentUser.getTaskById(iconBox.getId())); 
        setCurrentRequirements(searchField.getText(), sortBox.getValue(), 
                statusBox.getValue(), subjectBox.getValue());
    }
    
    private void editRequirement(MouseEvent event) throws Exception {
        VBox deleteIcon = (VBox) event.getSource();
        HBox iconBox = (HBox) deleteIcon.getParent();
        Requirement r = (Requirement) currentUser.getTaskById(iconBox.getId());
        
        activeTask = r;
        displayPopup("Requirement Creation", "Requirement_Creation_Screen", refresh);
    }
    
    private void displayRequirements(ArrayList<Requirement> requirementList) {
        removeAllChildren(requirements);
        requirements.getRowConstraints().removeAll(requirements.getRowConstraints());
                
        for (int i = 0; i < requirementList.size(); i++) {
            Requirement r = requirementList.get(i);
            HBox iconBox = createIconBox();
            iconBox.setId(r.getId());
            
            Text name = new Text(r.getName());
            Text category = new Text(r.getSubject());
            Text startDate = new Text(formatDate(r.getDeadline()));
                        
            Pane line = new Pane();
            GridPane.setColumnSpan(line, GridPane.REMAINING);
            line.getStyleClass().add("row-border");
            
            PaddingBinder linePadding = new PaddingBinder.Builder()
                .size(0.05)
                .reference(scroll)
                .build(line);
            Binder.bindPadding(linePadding);

            requirements.add(line, 0, i);
            requirements.add(iconBox, 0, i);
                        
            RowConstraints row;
            ObservableList<RowConstraints> requirementRows = requirements.getRowConstraints();
            
            if (i < requirementRows.size()) row = requirementRows.get(i);
            else row = new RowConstraints();
                
            requirements.getRowConstraints().add(row);
            requirements.addRow(i, name, category, startDate);    
            
            iconBox.maxWidthProperty().bind(scroll.widthProperty().multiply(0.125));
            
            FontBinder nameFont = requirementBuilder.newInstance().build(name, scroll);
            FontBinder categoryFont = requirementBuilder.newInstance().build(category, scroll);
            FontBinder startDateFont = requirementBuilder.newInstance().build(startDate, scroll);
            
            Binder.bindFont(nameFont);
            Binder.bindFont(categoryFont);
            Binder.bindFont(startDateFont);
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
    
    private void setYearMonth(HBox monthBox) {
        setActiveMonthBox(monthBox);
        
        Text monthText = (Text) monthBox.getChildren().get(0);
        String month = monthText.getText();
        Month m = Month.valueOf(month.toUpperCase());
        
        currentMonth = YearMonth.of(years.get(yearIndex), m);
        setCurrentRequirements(searchField.getText(), sortBox.getValue(), 
                statusBox.getValue(), subjectBox.getValue());
    }
    
    @FXML
    private void setYearMonth(MouseEvent event) {
        HBox monthBox = (HBox) event.getSource();
        setYearMonth(monthBox);
    }
    
    private void setYearMonth(YearMonth yearMonth) {
        for (int i = 0; i < months.getChildren().size(); i++) {
            HBox monthBox = (HBox) months.getChildren().get(i);
            YearMonth monthDate = YearMonth.parse(monthBox.getId());
            if (monthDate.equals(yearMonth)) setYearMonth(monthBox);
        }
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
        
        for (Requirement r : currentUser.getRequirementByYear(years.get(yearIndex))) {
            Month m = r.getDeadline().getMonth();
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
    
    private void initYears() {
        TreeSet<Integer> yearSet = new TreeSet<>();
        for (Requirement r : currentUser.getRequirementList()) {
            yearSet.add(r.getDeadline().getYear());
        }
        years.addAll(yearSet);
     
        // defaults to current year if an Requirement is due then
        int currentYear = Year.now().getValue();
        if (years.contains(currentYear)) yearIndex = years.indexOf(currentYear);
        setYear();
    }
    
    private void setYear() {
        yearText.setText(String.valueOf(years.get(yearIndex)));
        if (yearIndex <= 0) prevButton.setDisable(true);
        if (yearIndex >= years.size() - 1) nextButton.setDisable(true);
    }
    
    private void setYear(int year) {
        yearIndex = years.indexOf(year);
        setYear();
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
        
        subjectBox.getItems().add("All");
        subjectBox.getItems().addAll(currentUser.getCategoryList());
        
        subjectBox.setCellFactory(cellFormat);
        subjectBox.setButtonCell(cellFormat.call(null));
    }
    
    private void sortRequirements(String sortMethod) {
        switch (sortMethod) {
            case "Earliest to Latest":
                currentRequirements.sort((Requirement r1, Requirement r2) -> r1.getDeadline().compareTo(r2.getDeadline()));
                break;
            case "Latest to Earliest":
                currentRequirements.sort((Requirement r1, Requirement r2) -> r2.getDeadline().compareTo(r1.getDeadline()));
                break;
            case "A to Z":
                currentRequirements.sort((Requirement r1, Requirement r2) -> r1.getName().compareToIgnoreCase(r2.getName()));
                break;
            case "Z to A":
                currentRequirements.sort((Requirement r1, Requirement r2) -> r2.getName().compareToIgnoreCase(r1.getName()));
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
            setCurrentRequirements(value, sortBox.getValue(), 
                    statusBox.getValue(), subjectBox.getValue());
        });
        
        sortBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentRequirements(searchField.getText(), value, 
                    statusBox.getValue(), subjectBox.getValue());
        });
        
        statusBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentRequirements(searchField.getText(), sortBox.getValue(), 
                    value, subjectBox.getValue());
        });
        
        subjectBox.valueProperty().addListener((observable, oldValue, value) -> {
            setCurrentRequirements(searchField.getText(), sortBox.getValue(),
                    statusBox.getValue(), value);
        });
                
        createButton.setOnAction(createActionHandler(() -> 
                displayPopup("Requirement Creation", "Requirement_Creation_Screen", refresh)));
        
        requirementsHeader.spacingProperty().bind(requirementsHeader.heightProperty().multiply(0.11));
        
        PaddingBinder headerPadding = new PaddingBinder.Builder()
            .size(0.11)
            .build(requirementsHeader);
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
        sidebar.maxWidthProperty().bind(requirementContent.widthProperty().multiply(0.2));
                
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
        
        for (int i = 1; i < 4; i++) {
            Text title = (Text) requirementContent.getChildren().get(i);
            FontBinder headingFont = new FontBinder.Builder()
                .family("Montserrat")
                .size(0.03)
                .build(title, requirementContent);
            Binder.bindFont(headingFont);
        }
        
        Text requirementsTitle = (Text) requirementsHeader.getChildren().get(0);
        Label searchLabel = (Label) searchBox.getChildren().get(0);
                
        FontBinder requirementTitleFont = titleBuilder.newInstance()
                .widthSize(0.25)
                .heightSquareSize(0.02)
                .build(requirementsTitle, requirementsHeader);
        FontBinder searchTitleFont = titleBuilder.newInstance()
                .widthSize(0.4)
                .build(searchLabel, searchBox);
        
        Binder.bindFont(requirementTitleFont);
        Binder.bindFont(searchTitleFont);
        
        HBox requirementsBar = (HBox) requirementsHeader.getChildren().get(1);
        HBox requirementsInterface = (HBox) requirementsBar.getChildren().get(1);
        
        requirementsInterface.setMinWidth(0);
        searchBox.prefWidthProperty().bind(requirementsBar.widthProperty().multiply(0.3));
        
        for (Node n : requirementsInterface.getChildren()) {
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
            
            box.prefWidthProperty().bind(requirementsInterface.widthProperty().divide(3));
        }
        
        primaryStage.showingProperty().addListener((observable, oldValue, value) -> {
            Region sortArrow = (Region) sortBox.lookup(".arrow");
            Region statusArrow = (Region) statusBox.lookup(".arrow");
            Region categoryArrow = (Region) subjectBox.lookup(".arrow");
                            
            ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() -> {
                double vPadding = Math.min(requirementsHeader.getWidth() * 0.01, requirementsHeader.getHeight() * 0.05);
                double hPadding = vPadding * 1.25;
                return new Insets(vPadding, hPadding, vPadding, hPadding);
            }, requirementsHeader.widthProperty(), requirementsHeader.heightProperty());
        
            sortArrow.paddingProperty().bind(paddingTracker);
            statusArrow.paddingProperty().bind(paddingTracker);
            categoryArrow.paddingProperty().bind(paddingTracker);
        });
        
        initYears();
        setMonthSidebar();
        initComboBoxes();
    }
}