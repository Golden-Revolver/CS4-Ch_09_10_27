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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author Christian Brandon
 */
public abstract class UniversalController implements Initializable {
    protected static Stage primaryStage;
    protected Map<String, String> iconScreens = new LinkedHashMap<>();
    
    protected UniversalController() {
        iconScreens.put("calendar", "Calendar");
        iconScreens.put("event", "Event_Creation_Screen");
        iconScreens.put("requirement", "Requirement_Creation_Screen");
        iconScreens.put("goal", "Goal_Creation_Screen");
        iconScreens.put("habit", "Habit_Creation_Screen");
        iconScreens.put("user", null);
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
            String color = "";
            String[] styles = box.getStyle().split(";");
            for (String s : styles) {
                if (s.contains("-fx-background-color")) {
                    color = s.replace("-fx-background-color:", "");
                }
            }
            final Color bgColor;
            if (color.isEmpty()) bgColor = Color.TRANSPARENT;
            else bgColor = Color.web(color.trim());
            System.out.println(box.getStyle());

            ObjectBinding<Background> backgroundTracker = Bindings.createObjectBinding(() -> {
                    CornerRadii radius = new CornerRadii(Math.min(box.getWidth(), box.getHeight()) * size);
                    BackgroundFill bgFill = new BackgroundFill(bgColor, 
                            radius, Insets.EMPTY);
                    return new Background(bgFill);
            }, box.widthProperty(), box.heightProperty());
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
    protected void switchScreen(String screen) throws Exception {
        switchScreen("Task Visualizer", screen);
    }
    protected void switchScreen(String title, String screen) throws Exception {
        primaryStage.setTitle(title);
        Parent root = FXMLLoader.load(getClass().getResource(screen + ".fxml"));
        Scene scene = new Scene(root);
        primaryStage.hide();
        primaryStage.setScene(scene);
        primaryStage.show();
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
}