package taskvisualizer.controllers;

import taskvisualizer.wrappers.WrappedImageView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BinaryOperator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import taskvisualizer.CheckedRunnable;
import taskvisualizer.FontBinder;
import taskvisualizer.Habit;
import taskvisualizer.PaddingBinder;
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
    
    public static class Binder {
        // master bindFont method
        protected static void bindFont(FontBinder param) {
            ObjectBinding<Font> fontTracker = Bindings.createObjectBinding(() -> {
                double width = param.getBox().getWidth();
                double height = param.getBox().getHeight();
                
                double boxWidth = width * param.getWidthSize();
                double boxSquareWidth = width * width * param.getWidthSquareSize();
                double boxHeight = height * param.getHeightSize();
                double boxSquareHeight = height * height * param.getHeightSquareSize();
                
                double fontSize = min(boxWidth, boxSquareWidth, boxHeight, boxSquareHeight) * param.getSize();
                return Font.font(param.getFamily(), param.getWeight(), fontSize);
            }, param.getBox().widthProperty(), param.getBox().heightProperty());
            
            param.getText().fontProperty().bind(fontTracker);
        }
        
        // master bindPadding method
        protected static void bindPadding(PaddingBinder param) {
            BinaryOperator<Double> getInsetSize;
            
            if (param.getMaxReference()) getInsetSize = (width, height) -> Math.max(width, height);
            else getInsetSize = (width, height) -> Math.min(width, height);
            
            ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() -> {
                double referenceWidth = param.getReference().getWidth() * param.getWidthSize();
                double referenceHeight = param.getReference().getHeight() * param.getHeightSize();
                double insetSize = getInsetSize.apply(referenceWidth, referenceHeight) * param.getSize();
                
                return new Insets(param.getTop() ? insetSize : 0, param.getRight() ? insetSize : 0, 
                        param.getBottom() ? insetSize : 0, param.getLeft() ? insetSize : 0);
            }, param.getReference().widthProperty(), param.getReference().heightProperty());
            
            param.getBox().paddingProperty().bind(paddingTracker);
        }
      
        protected static void bindBackgroundRadius(Pane box, double size) {
            bindBackgroundRadius(box, box, size);
        }
        
        protected static void bindBackgroundRadius(Pane box, Pane reference, double size) {
            bindBackgroundRadius(box, reference, size, Insets.EMPTY);
        }
        
        protected static void bindBackgroundRadius(Pane box, Pane reference, double size, Insets bgInsets) {
            String color = getStyleValue(box, "-fx-background-color");
            
            final Color bgColor;
            if (color.isEmpty()) bgColor = Color.TRANSPARENT;
            else bgColor = Color.web(color);
            
            ObjectBinding<Background> backgroundTracker = Bindings.createObjectBinding(() -> {
                double radius = Math.min(reference.getWidth(), reference.getHeight()) * size;
                CornerRadii bgRadius = new CornerRadii(radius);
                BackgroundFill bgFill = new BackgroundFill(bgColor, bgRadius, bgInsets);
                return new Background(bgFill);
            }, reference.widthProperty(), reference.heightProperty());
            
            box.backgroundProperty().bind(backgroundTracker);
        }
        
        protected static void bindActiveBackgroundRadius(Pane box, Pane reference, double size) {
            ArrayList<Boolean> hasCurve = new ArrayList<>(Arrays.asList(true, true, true, true));
            bindActiveBackgroundRadius(box, reference, size, hasCurve);
        }
        
        protected static void bindActiveBackgroundRadius(Pane box, Pane reference,
                double size, ArrayList<Boolean> hasCurve) {
            String color = getStyleValue(box, "-fx-background-color");
            final Color bgColor;
            if (color.isEmpty()) bgColor = Color.TRANSPARENT;
            else bgColor = Color.web(color);
            
            bindActiveBackgroundRadius(box, reference, size, hasCurve, bgColor);
        }
        
        protected static void bindActiveBackgroundRadius(Pane box, Pane reference, 
                double size, ArrayList<Boolean> hasCurve, Color bgColor) {
            
            ObjectBinding<Background> backgroundTracker = Bindings.createObjectBinding(() -> {
                double radius = Math.min(reference.getWidth(), reference.getHeight()) * size;
                
                ArrayList<Double> bgRadii = new ArrayList<>();
                ArrayList<Double> activeRadii = new ArrayList<>();
                for (boolean b : hasCurve) {
                    double value = b ? 1 : 0;
                    bgRadii.add(value * radius * 0.98);
                    activeRadii.add(value * radius);
                }
                
                CornerRadii bgRadius = new CornerRadii(bgRadii.get(0), bgRadii.get(1),
                bgRadii.get(2), bgRadii.get(3), false);
                
                CornerRadii activeRadius = new CornerRadii(activeRadii.get(0), activeRadii.get(1),
                activeRadii.get(2), activeRadii.get(3), false);
                
                BackgroundFill bgFill = new BackgroundFill(bgColor, bgRadius, new Insets(3));
                BackgroundFill activeFill = new BackgroundFill(Color.RED, activeRadius, new Insets(1));
                return new Background(activeFill, bgFill);
                
            }, reference.widthProperty(), reference.heightProperty());
            
            box.backgroundProperty().bind(backgroundTracker);
        }
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
        
        onClose.call(); // triggers after popup is closeds
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
            
            WrappedImageView icon = new WrappedImageView(getImage(iconName));
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
        
        addStyle(highlight, "-fx-background-color", "#0030FF");
        addStyle(highlight, "-fx-background-radius", "0 0 25 25");
        
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
    
    // CSS methods
    protected static String getLastChar(String s) {
        if (s.isEmpty()) return "";
        return s.substring(s.length() - 1);
    }
    protected static String getStyleValue(Node n, String property) {
        if (n == null) return "";
        
        String style = n.getStyle();
        int propertyIndex = style.indexOf(property);
        if (propertyIndex == -1) return "";
        
        int startIndex = propertyIndex + property.length() + 2;
        int endIndex = style.indexOf(";", startIndex);
        return style.substring(startIndex, endIndex);
    }
    protected static void addStyle(Node n, String property, String value) {
        if (n == null) return;
        
        String style = n.getStyle();
        if (!(getLastChar(style).equals(";") || style.isEmpty())) style += ";";
        n.setStyle(style + " " + property + ": " + value + ";");
    }
    protected static void changeStyle(Node n, String property, String value) {
        // property must be in the format -fx-property
        // no semicolon or spaces!!
        if (n == null) return;
        
        String style = n.getStyle();
        int propertyIndex = style.indexOf(property);
        if (propertyIndex == -1) {
            addStyle(n, property, value); // adds style if not found!
            return;
        }
        
        int startIndex = propertyIndex + property.length() + 2;
        int endIndex = style.indexOf(";", propertyIndex);
        
        String startPart = style.substring(0, startIndex);
        String endPart = style.substring(endIndex, style.length());
        style = startPart + value + endPart;
        
        n.setStyle(style);
    }
    
    // Helper methods
    protected <T> T getController(String screen) {
        FXMLLoader loader = new FXMLLoader(TaskVisualizer.class.getResource("fxml/" + screen + ".fxml"));
        return loader.getController();
    }
    protected static void removeAllChildren(Pane p) {
        p.getChildren().removeAll(p.getChildren());
    }
    protected static void removeAllChildren(Pane... p) {
        for (Pane pane : p) removeAllChildren(pane);
    }
    protected static String formatDate(LocalDateTime date) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy @ hh:mm a");
        return date.format(dateFormat);
    }
    protected static Image getImage(String filename) {
        return new Image(TaskVisualizer.class.getResourceAsStream("images/" + filename + ".png"));
    }
    protected static double min(double... x) {
        double min = x[0];
        for (double d : x) min = Math.min(min, d);
        return min;
    }
    protected static int to24Hour(int hour, String period) {
        boolean hourCheck = hour == 12;
        boolean amCheck = period.equals("AM");
        
        if (amCheck && hourCheck) return 0;
        if (!amCheck && !hourCheck) return hour + 12;
        else return hour;
    }
    protected <T> ArrayList<T> copyArrayList(ArrayList<T> arrayList) {
        ArrayList<T> copy = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            copy.add(arrayList.get(i));
        }
        return copy;
    }
            
    
    // Filter methods
    protected static void filterTasksByName(String name, ArrayList<? extends Task> taskList) {
        for (int i = 0; i < taskList.size(); i++) {
            Task t = taskList.get(i);
            boolean containsName = t.getName().toLowerCase().contains(name.toLowerCase());
            if (!containsName) {
                taskList.remove(t);
                i--;
            }
        }
    }
}