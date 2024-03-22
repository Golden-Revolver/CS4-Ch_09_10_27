package taskvisualizer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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
        User u1 = new User("Golden Revolver", "GoldenMan1234");
        UniversalController.setUser(u1);
        /* This is temporary. In the final app, Users will be created from the sign-up
        screen and added to the userList. Upon log-in, findUser() will take the name
        and password as parameters. If there is a match, the returned User will be
        set as the currentUser with setUser().
        */
        
        // Scenario 1: School Day
        LocalDateTime d1 = LocalDateTime.of(2024, 1, 22, 0, 0);
        LocalDateTime d2 = LocalDateTime.of(2024, 1, 16, 18, 0);
        LocalDateTime d3 = LocalDateTime.of(2024, 2, 12, 15, 22);
        Requirement r1 = new Requirement("Literature Review", "Research", d1);
        Requirement r2 = new Requirement("Family Pedigree", "Biology", d2);
        Requirement r3 = new Requirement("Exercise 12", "Computer Science", d3);
        
        u1.addTask(r1);
        u1.addTask(r2);
        u1.addTask(r3);
        
        // Scenario 2: Family Reunion
        LocalDateTime d4 = LocalDateTime.of(2024, 6, 30, 12, 30);
        LocalDateTime d5 = LocalDateTime.of(2024, 7, 24, 8, 0);
        Event e1 = new Event("Family Gathering", d4, "Sorsogon");
        Event e2 = new Event("School Fair", d5, "PSHS-MC");
        
        u1.addTask(e1);
        u1.addTask(e2);
        
        // Scenario 3: Workout Plan
        LocalDate d6 = LocalDate.of(2024, 1, 30);
        LocalDate d7 = LocalDate.of(2024, 2, 12);
        Habit h1 = new Habit("Complete exercise plan");
        Goal g1 = new Goal("Push-ups", 10, d6);
        Goal g2 = new Goal("Lose weight", 50.0, 40.0, false, d7);
        
        u1.addTask(h1);
        u1.addTask(g1);
        u1.addTask(g2);
        
        // Testing
        LocalDateTime d8 = LocalDateTime.of(2024, 4, 1, 0, 0);
        Event e3 = new Event("Cat birthday :3", d8);
        Event e4 = new Event("April Fools", d8);
        Requirement r4 = new Requirement("Protocol Listing", "Research", d8);
        
        u1.addTask(e3);
        u1.addTask(e4);
        u1.addTask(r4);
        
        launch(args);
    }
}