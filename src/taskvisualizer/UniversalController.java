package taskvisualizer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 *
 * @author Christian Brandon
 */
public abstract class UniversalController implements Initializable {
    protected static Stage primaryStage;
    protected static String currentScreen;
    protected static User currentUser;
    protected Map<String, String> iconScreens = new LinkedHashMap<>();
    
    protected Pane highlight;
    @FXML protected HBox menuHeader;
    @FXML protected StackPane stackHeader;
    
    protected UniversalController() {
        iconScreens.put("calendar", "Calendar");
        iconScreens.put("event", "Event_Screen");
        iconScreens.put("requirement", "Requirement_Creation_Screen");
        iconScreens.put("goal", "Goal_Creation_Screen");
        iconScreens.put("habit", "Habit_Creation_Screen");
        iconScreens.put("user", "login-screen");
    }
    
    public static class Binder {
        protected static void bindFont(Text text, Pane box, double size) {        
            bindFont("System", FontWeight.NORMAL, text, box, size, 1, 1);
        }

        protected static void bindFont(Labeled text, Pane box, double size) {        
            bindFont("System", FontWeight.NORMAL, text, box, size, 1, 1);
        }

        protected static void bindFont(Text text, Pane box, double size, double widthSize, double heightSize) {
            bindFont("System", FontWeight.NORMAL, text, box, size, widthSize, heightSize);
        }

        protected static void bindFont(Labeled text, Pane box, double size, double widthSize, double heightSize) {
            bindFont("System", FontWeight.NORMAL, text, box, size, widthSize, heightSize);
        }

        protected static void bindFont(String family, FontWeight weight, Text text, Pane box, double size) {
            bindFont(family, weight, text, box, size, 1, 1);
        }

        protected static void bindFont(String family, FontWeight weight, Labeled text, Pane box, double size) {
            bindFont(family, weight, text, box, size, 1, 1);
        }

        protected static void bindFont(String family, FontWeight weight, Text text, Pane box,
                double size, double widthSize, double heightSize) {
            ObjectBinding<Font> fontTracker = Bindings.createObjectBinding(() ->
                Font.font(family, weight, Math.min(box.getWidth() * widthSize, box.getHeight() * heightSize) * size),
                        box.widthProperty(), box.heightProperty());
            text.fontProperty().bind(fontTracker);
        }

        protected static void bindFont(String family, FontWeight weight, Labeled text, Pane box,
                double size, double widthSize, double heightSize) {
            ObjectBinding<Font> fontTracker = Bindings.createObjectBinding(() ->
                Font.font(family, weight, Math.min(box.getWidth() * widthSize, box.getHeight() * heightSize) * size),
                        box.widthProperty(), box.heightProperty());
            text.fontProperty().bind(fontTracker);
        }

        protected static void bindPadding(Pane box, double size) {
            ObjectBinding<Insets> paddingTracker = Bindings.createObjectBinding(() ->
            new Insets(Math.min(box.getWidth(), box.getHeight()) * size), 
             box.widthProperty(), box.heightProperty());
            box.paddingProperty().bind(paddingTracker);
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
    protected Color hexColor(String hex) {
        int red = Integer.parseInt(hex.substring(1, 3), 16);
        int green = Integer.parseInt(hex.substring(3, 5), 16);
        int blue = Integer.parseInt(hex.substring(5, 7), 16);
        return Color.rgb(red, green, blue);
    }
    protected static Stage getStage() {
        return primaryStage;
    }
    protected static void setStage(Stage s) {
        primaryStage = s;
    }
    protected static String getCurrentScreen() {
        return currentScreen;
    }
    protected static void setCurrentScreen(String s) {
        currentScreen = s;
    }
    protected void switchScreen(String screen) throws Exception {
        switchScreen("Task Visualizer", screen);
    }
    protected void switchScreen(String title, String screen) throws Exception {
        primaryStage.setTitle(title);
        currentScreen = screen;
        Parent root = FXMLLoader.load(getClass().getResource(screen + ".fxml"));
        Scene scene = new Scene(root);
        primaryStage.hide();
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    protected void displayPopup(String screen) throws Exception {
        displayPopup("Task Visualizer", screen);
    }
    protected void displayPopup(String title, String screen) throws Exception {
        Stage popup = new Stage();
        popup.setTitle(title);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(primaryStage);
        
        Parent root = FXMLLoader.load(getClass().getResource(screen + ".fxml"));
        Scene scene = new Scene(root);
        popup.setScene(scene);
        popup.showAndWait();
    }
    protected EventHandler<MouseEvent> switchScreenHandler(String screen) {
        return switchScreenHandler("Task Visualizer", screen);
    }
    protected EventHandler<MouseEvent> switchScreenHandler(String title, String screen) {
        EventHandler<MouseEvent> eventHandler = (MouseEvent event) -> {
            try {  
                switchScreen(title, screen);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
        return eventHandler;
    };
    protected EventHandler<MouseEvent> displayPopupHandler(String screen) {
        return displayPopupHandler("Task Visualizer", screen);
    }
    protected EventHandler<MouseEvent> displayPopupHandler(String title, String screen) {
        EventHandler<MouseEvent> eventHandler = (MouseEvent event) -> {
            try {
                displayPopup(title, screen);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
        return eventHandler;
    }
    protected EventHandler<ActionEvent> switchScreenActionHandler(String screen) {
        return switchScreenActionHandler("Task Visualizer", screen);
    }
    protected EventHandler<ActionEvent> switchScreenActionHandler(String title, String screen) {
         EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            try {  
                switchScreen(title, screen);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
        return eventHandler;
    }
    protected EventHandler<ActionEvent> displayPopupActionHandler(String screen) {
        return displayPopupActionHandler("Task Visualizer", screen);
    }
    protected EventHandler<ActionEvent> displayPopupActionHandler(String title, String screen) {
        EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            try {
                displayPopup(title, screen);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
        return eventHandler;
    }
    protected Pane getHighlight() {
        return highlight;
    }
    protected void setHighlight(Pane p) {
        highlight = p;
    }    
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
       
        slideTransition.setOnFinished(switchScreenActionHandler(screen));
        slideTransition.play();
    } 
    protected EventHandler<MouseEvent> navigateHandler(String screen) {
         EventHandler<MouseEvent> eventHandler = (MouseEvent event) -> {
            try {  
                navigate(event, screen);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
        return eventHandler;
    }
    
    protected void initHeader(String screen) {
        double iconNumber = 0, iconLength = 0;
        setHighlight(new Pane());
        
        ColorAdjust white = new ColorAdjust();
        white.setBrightness(1);
        
        for (Map.Entry<String, String> entry : iconScreens.entrySet()) {
            String iconName = entry.getKey();
            String iconScreen = entry.getValue();
            
            Image img = new Image(getClass().getResourceAsStream("images/" + iconName + ".png"));
            WrappedImageView icon = new WrappedImageView(img);
            icon.setEffect(white);
            
            VBox iconBox = new VBox(icon);
            iconBox.setAlignment(Pos.CENTER);
            iconBox.setPadding(new Insets(10));
            
            if (!iconScreen.equals(currentScreen)) {
                iconBox.setOnMousePressed(navigateHandler(iconScreen));
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
    
    protected static User getUser() {
        return currentUser;
    }
    protected static void setUser(User u) {
        currentUser = u;
    }
    protected static void removeAllChildren(Pane p) {
        p.getChildren().removeAll(p.getChildren());
    }
    protected static void removeAllChildren(Pane... p) {
        for (Pane pane : p) removeAllChildren(pane);
    }
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
}