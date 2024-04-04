package taskvisualizer;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 *
 * @author Christian Brandon
 */
public class CalendarController extends UniversalController implements Initializable {
    
    @FXML private HBox activeCell;
    @FXML private VBox taskBox, eventBox, requirementBox, goalBox,
            events, requirements, goals;
    @FXML private GridPane layout, content, calendar, calendarHeader;
    @FXML private Text monthYear;
    @FXML private Button prevButton, nextButton;
    @FXML private ArrayList<Text> dayList = new ArrayList<>();
    @FXML private Map<HBox, ArrayList<Boolean>> calendarCorners = new HashMap<>();
    
    private int currentMonth, currentYear;
    private LocalDate activeDate;
    private ArrayList<Integer> typeList = new ArrayList<>();
    
    private void setActiveCell(HBox cell) {
        Color bgColor = Color.web("#65D1FF");
        BackgroundFill bgFill = new BackgroundFill(bgColor, CornerRadii.EMPTY, new Insets(3));
        BackgroundFill activeFill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, new Insets(1));
        
        // resets style of the old cell
        Background background = new Background(bgFill);
        if (calendarCorners.containsKey(activeCell)) {
            activeCell.backgroundProperty().unbind();
            activeCell.setBackground(null);
        }
        else if (activeCell != null) activeCell.setBackground(background);
        
        // sets style of the selected cell
        Background activeBackground = new Background(activeFill, bgFill);
        if (calendarCorners.containsKey(cell)) {
            Binder.bindActiveBackgroundRadius(cell, calendar, 0.1, 
                    calendarCorners.get(cell), bgColor);
        }
        else if (cell != null) cell.setBackground(activeBackground);

        activeCell = cell;
    }
    
    private void initCalendar() {
        String cellStyle = "-fx-border-color: #006DD1; -fx-border-width: 0.5;";
        
        Binder.bindBackgroundRadius(calendar, 0.10);  
        Binder.bindFont(FontWeight.BOLD, monthYear, calendarHeader, 0.35, 0.20, 1);

        prevButton.prefHeightProperty().bind(calendarHeader.heightProperty().multiply(0.70));
        nextButton.prefHeightProperty().bind(calendarHeader.heightProperty().multiply(0.70));
        
        Binder.bindFont(prevButton, calendarHeader, 0.30, 0.20, 1);
        Binder.bindFont(nextButton, calendarHeader, 0.30, 0.20, 1);
        
        Binder.bindPadding(calendarHeader, 0.15);
        
        for (int i = 1; i < 8; i++) {
            VBox weekPane = (VBox) calendar.getChildren().get(i);
            Text weekDay = (Text) weekPane.getChildren().get(0);
            Binder.bindFont(weekDay, weekPane, 0.35);
        }
        
        for (int i = 8; i < 50; i++) {
            HBox cell = new HBox();
            Pane gap = new Pane();
            Text day = new Text();
            
            cell.setStyle(cellStyle);
            addStyle(cell, "-fx-background-insets", "0, 2");
            cell.setOnMousePressed(this::selectDay);
            HBox.setHgrow(gap, Priority.ALWAYS);
            
            double numberSize = 0.25;
            double paddingSize = 0.06;
            double spacingSize = 0.03;
            
            // binding text
            Binder.bindFont(day, cell, numberSize);
            
            // binding spacing and padding
            cell.spacingProperty().bind(cell.widthProperty().multiply(spacingSize));
            Binder.bindPadding(cell, paddingSize);
            
            cell.getChildren().addAll(day, gap);
            calendar.add(cell, (i - 1) % 7, (i + 6) / 7);
            
            dayList.add(day);
            typeList.add(0);
        }
        
        // hiding edge borders
        for (int i = 0; i < 5; i++) {
            addStyle(calendar.getChildren().get(i * 7 + 8), "-fx-border-style", "solid solid solid hidden");
            addStyle(calendar.getChildren().get(i * 7 + 14), "-fx-border-style", "solid hidden solid solid");
            addStyle(calendar.getChildren().get(i + 44), "-fx-border-style", "solid solid hidden solid");
        }
        
        // hiding corner borders
        HBox leftCorner = (HBox) calendar.getChildren().get(43);
        HBox rightCorner = (HBox) calendar.getChildren().get(49);
        
        addStyle(leftCorner, "-fx-border-style", "solid solid hidden hidden");
        ArrayList<Boolean> leftCurve = new ArrayList<>(Arrays.asList(false, false, false, true));
        calendarCorners.put(leftCorner, leftCurve);
        
        addStyle(rightCorner, "-fx-border-style", "solid hidden hidden solid");
        ArrayList<Boolean> rightCurve = new ArrayList<>(Arrays.asList(false, false, true, false));
        calendarCorners.put(rightCorner, rightCurve);
    }
    
    private void updateCalendar(int m, int y) {
        setActiveCell(null); // resets the active cell
        Locale locale = new Locale("en");
        String month = Month.of(m).getDisplayName(TextStyle.FULL_STANDALONE, locale);
        
        monthYear.setText(month + " " + Integer.toString(y));
        
        // resets all text and circles starting from the third row
        for (int i = 0; i < dayList.size(); i++) {
            dayList.get(i).setText("");
            HBox cell = (HBox) calendar.getChildren().get(i + 8);
            int types = typeList.get(i);
            if (types > 0) cell.getChildren().remove(2, types + 2);
            typeList.set(i, 0);
        }
        
        LocalDate firstDay = LocalDate.of(y, m, 1);
        int weekDay = firstDay.getDayOfWeek().getValue();
        if (weekDay == 7) weekDay = 0; // resets value of sunday to 0
        
        for (int i = 0; i < firstDay.lengthOfMonth(); i++) {
            dayList.get(weekDay + i).setText(Integer.toString(i + 1));
            HBox cell = (HBox) calendar.getChildren().get(weekDay + i + 8);
            
            Circle eventCircle = new Circle();
            Circle requirementCircle = new Circle();
            Circle goalCircle = new Circle();
            
            eventCircle.setFill(Color.web("A20000"));
            requirementCircle.setFill(Color.web("#057D03"));
            goalCircle.setFill(Color.web("#FFFC00"));
            
            double circleSize = 0.1;
            
            // binding circles
            NumberBinding cellWidth = Bindings.min(cell.widthProperty(), 
                    cell.widthProperty().multiply(cell.widthProperty()).divide(50));
            NumberBinding cellSize = Bindings.min(cellWidth, cell.heightProperty().multiply(2));
            
            eventCircle.radiusProperty().bind(cellSize.multiply(circleSize / 2));
            requirementCircle.radiusProperty().bind(cellSize.multiply(circleSize / 2));
            goalCircle.radiusProperty().bind(cellSize.multiply(circleSize / 2));
            
            LocalDate currentDate = LocalDate.of(currentYear, currentMonth, i + 1);
            if (currentDate.equals(activeDate)) setActiveCell(cell);
            int types = 0;
            if (!currentUser.getEventByDate(currentDate).isEmpty()) {
                cell.getChildren().add(eventCircle);
                types++;
            }
            if (!currentUser.getRequirementByDate(currentDate).isEmpty()) {
                cell.getChildren().add(requirementCircle);
                types++;
            }
            if (!currentUser.getGoalByDate(currentDate).isEmpty()) {
                cell.getChildren().add(goalCircle);
                types++;
            }
            typeList.set(weekDay + i, types);
        }
    }
    
    @FXML
    private void previousMonth(ActionEvent event) {
        if (currentMonth == 1) {
            currentMonth = 12;
            currentYear--;
        }
        else currentMonth--;
        updateCalendar(currentMonth, currentYear);
    }
    
    @FXML
    private void nextMonth(ActionEvent event) {
        if (currentMonth == 12) {
            currentMonth = 1;
            currentYear++;
        } 
        else currentMonth++;
        updateCalendar(currentMonth, currentYear);
    }
    
    @FXML
    private void selectDay(MouseEvent event) {
        removeAllChildren(events, requirements, goals);
        
        HBox cell = (HBox) event.getSource();
        Text day = (Text) cell.getChildren().get(0);
        setActiveCell(cell);

        // sets the active date
        if (day.getText().isEmpty()) activeDate = null;
        else {
            int currentDay = Integer.parseInt(day.getText());
            activeDate = LocalDate.of(currentYear, currentMonth, currentDay);
        }
        
        ArrayList<Event> eventList = currentUser.getEventByDate(activeDate);
        ArrayList<Requirement> requirementList = currentUser.getRequirementByDate(activeDate);
        ArrayList<Goal> goalList = currentUser.getGoalByDate(activeDate);
        
        for (Event e : eventList) {
            Text info = new Text(e.getNameFormat() + "\n" + e.getDataFormat());
            Binder.bindFont(info, eventBox, 0.075, 0.5, 1);
            events.getChildren().add(info);
        }
        for (Requirement r : requirementList) {
            Text info = new Text(r.getNameFormat() + "\n" + r.getDataFormat());
            Binder.bindFont(info, requirementBox, 0.075, 0.5, 1);
            requirements.getChildren().add(info);
        }
        for (Goal g : goalList) {
            Text info = new Text(g.getNameFormat() + "\n" + g.getDataFormat());
            Binder.bindFont(info, goalBox, 0.075, 0.5, 1);
            goals.getChildren().add(info);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        content.hgapProperty().bind(layout.widthProperty().multiply(0.04));
        Binder.bindPadding(content, 0.05);
        
        initCalendar();
        
        LocalDateTime now = LocalDateTime.now();
        currentMonth = now.getMonthValue();
        currentYear = now.getYear();
        
        updateCalendar(currentMonth, currentYear);
        
        initHeader(currentScreen);
        
        ArrayList<Color> circleColors = new ArrayList<>();
        circleColors.addAll(Arrays.asList(Color.web("#A20000"),
                Color.web("#057D03"), Color.web("FFFC00")));
        
        for (int i = 0; i < 3; i++) {
            VBox box = (VBox) taskBox.getChildren().get(i);
            HBox header = (HBox) box.getChildren().get(0);
            Text title = (Text) header.getChildren().get(0);
            
            // adding text demo
            ScrollPane scroll = (ScrollPane) box.getChildren().get(1);
            VBox list = (VBox) scroll.getContent();
            Text task = new Text("Hello!");
            Binder.bindFont(task, box, 0.1, 0.5, 1);
            list.getChildren().add(task);
            
            Circle circle = new Circle();
            circle.setFill(circleColors.get(i));
            header.getChildren().add(0, circle);
            
            NumberBinding boxSize = Bindings.min(box.widthProperty(), box.heightProperty());
            
            circle.radiusProperty().bind(boxSize.multiply(0.125 / 2));
            Binder.bindFont(FontWeight.BOLD, title, box, 0.125, 0.5, 1);
            
            Binder.bindPadding(box, 0.1);
            box.spacingProperty().bind(box.heightProperty().multiply(0.05));
            header.spacingProperty().bind(boxSize.multiply(0.05));
            list.spacingProperty().bind(boxSize.multiply(0.05));
        }
        NumberBinding taskBoxSize = Bindings.min(taskBox.widthProperty(), taskBox.heightProperty());
        taskBox.spacingProperty().bind(taskBoxSize.multiply(0.05));
    }    
}