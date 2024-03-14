package taskvisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Christian Brandon
 */
public class TaskVisualizer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        UniversalController.setStage(stage);
        Parent root = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Task Visualizer");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}