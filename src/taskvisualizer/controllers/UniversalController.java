package taskvisualizer.controllers;

import taskvisualizer.wrappers.WrappedImageView;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import taskvisualizer.CheckedRunnable;
import taskvisualizer.StyleProcessor;
import taskvisualizer.Task;
import taskvisualizer.TaskVisualizer;
import taskvisualizer.User;

/**
 *
 * @author Christian Brandon
 */
public abstract class UniversalController implements Initializable {
    protected static Stage primaryStage, popupStage;
    protected static String currentScreen;
    protected static User currentUser;
    protected static Task activeTask;
    protected Map<String, String> iconScreens = new LinkedHashMap<>();
    
    protected Pane highlight;
    @FXML protected HBox menuHeader;
    @FXML protected StackPane stackHeader;
    
    protected UniversalController() {
        iconScreens.put("calendar", "Calendar");
        iconScreens.put("event", "Event_Screen");
        iconScreens.put("requirement", "Requirement_Screen");
        iconScreens.put("goal", "Goal_Screen");
        iconScreens.put("habit", "Habit_Screen");
        iconScreens.put("user", "login-screen");
    }
    
    // User methods
    public static User getUser() {
        return currentUser;
    }
    public static void setUser(User u) {
        currentUser = u;
    }
    
    // Stage methods
    public static Stage getStage() {
        return primaryStage;
    }
    public static void setStage(Stage s) {
        primaryStage = s;
    }
    public static Stage getPopupStage() {
        return popupStage;
    }
    public static void setPopupStage(Stage s) {
        popupStage = s;
    }
    
    // Screen methods
    public static String getCurrentScreen() {
        return currentScreen;
    }
    public static void setCurrentScreen(String s) {
        currentScreen = s;
    }
    protected void switchScreen(String screen) throws Exception {
        switchScreen("Task Visualizer", screen);
    }
    protected void switchScreen(String title, String screen) throws Exception {
        primaryStage.setTitle(title);
        
        currentScreen = screen;
        Parent root = FXMLLoader.load(TaskVisualizer.class.getResource("fxml/" + screen + ".fxml"));
        Scene scene = new Scene(root);
        primaryStage.hide();
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    protected void displayPopup(String screen) throws Exception {
        displayPopup("Task Visualizer", screen);
    }
    protected void displayPopup(String title, String screen) throws Exception {
        displayPopup(title, screen, null);
    }
    protected void displayPopup(String title, String screen, Callable onClose) throws Exception {
        popupStage.setTitle(title);
        
        Parent root = FXMLLoader.load(TaskVisualizer.class.getResource("fxml/" + screen + ".fxml"));
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
        popupStage.showAndWait();
        
        onClose.call(); // triggers after popup is closed
        activeTask = null; // clears the current task
    }
    
    // Handler methods
    protected EventHandler<MouseEvent> createMouseHandler(CheckedRunnable method) {
        return (MouseEvent event) -> tryMethod(method);
    }
    protected EventHandler<ActionEvent> createActionHandler(CheckedRunnable method) {
        return (ActionEvent event) -> tryMethod(method);
    }
    protected void tryMethod(CheckedRunnable method) {
        try {
            method.run();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    // Navigation methods
    protected void navigate(MouseEvent event, String screen) throws Exception {
        VBox iconBox = (VBox) event.getSource();
        
        double boxPos = iconBox.getBoundsInParent().getMaxX();
        double highlightPos = highlight.getBoundsInParent().getMaxX();
        double distanceX = boxPos - highlightPos;
        
        double boxWidth = iconBox.getWidth();
        if (screen.equals("Calendar")) boxWidth = stackHeader.getWidth() * 0.2;
        double highlightWidth = highlight.getWidth();        
        
        double scaleChange = boxWidth / highlightWidth;
        double widthDifference = highlightWidth - boxWidth;
        
        double offset = highlight.getTranslateX();
        
        ScaleTransition squish = new ScaleTransition(Duration.millis(250), highlight);
        squish.setToX(scaleChange);
        
        TranslateTransition slide = new TranslateTransition(Duration.millis(250), highlight);
        slide.setToX(distanceX + offset + widthDifference / 2);
        
        ParallelTransition slideSquish = new ParallelTransition(squish, slide);
        PauseTransition endDelay = new PauseTransition(Duration.millis(500));
        SequentialTransition slideTransition = new SequentialTransition(slideSquish, endDelay);
        
        highlight.translateXProperty().unbind();
        highlight.setTranslateX(offset);
       
        slideTransition.setOnFinished(createActionHandler(() -> switchScreen(screen)));
        slideTransition.play();
    }
    protected void initHeader(String screen) {
        double iconNumber = 0, iconLength = 0;
        setHighlight(new Pane());
        
        ColorAdjust white = new ColorAdjust();
        white.setBrightness(1);
        
        for (Map.Entry<String, String> entry : iconScreens.entrySet()) {
            String iconName = entry.getKey();
            String iconScreen = entry.getValue();
            
            WrappedImageView icon = new WrappedImageView(iconName);
            icon.setEffect(white);
            
            VBox iconBox = new VBox(icon);
            iconBox.setAlignment(Pos.CENTER);
            iconBox.setPadding(new Insets(10));
            
            if (!iconScreen.equals(currentScreen)) {
                iconBox.setOnMousePressed(event -> tryMethod(() -> navigate(event, iconScreen)));
            } else {
                iconNumber = iconLength;
            }
            
            iconLength++;
            menuHeader.getChildren().add(iconBox);
        }
        
        StyleProcessor highlightStyle = new StyleProcessor(highlight);
        highlightStyle.addStyle("-fx-background-color", "#0030FF");
        highlightStyle.addStyle("-fx-background-radius", "0 0 25 25");
        
        highlight.setMaxWidth(Region.USE_PREF_SIZE);
        StackPane.setAlignment(highlight, Pos.CENTER_LEFT);
        
        double iconPos = iconNumber == 0 ? -0.033 : iconNumber / iconLength;
        double iconWidth = iconNumber == 0 ? 0.2 : 1 / iconLength;
        
        highlight.translateXProperty().bind(stackHeader.widthProperty().multiply(iconPos));
        highlight.prefWidthProperty().bind(stackHeader.widthProperty().multiply(iconWidth));
        
        stackHeader.getChildren().add(0, highlight);
    }
    
    // Field methods
    protected Pane getHighlight() {
        return highlight;
    }
    protected void setHighlight(Pane p) {
        highlight = p;
    }    
    protected static Task getActiveTask() {
        return activeTask;
    }
    protected static void setActiveTask(Task t) {
        activeTask = t;
    }
}