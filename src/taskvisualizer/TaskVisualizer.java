package taskvisualizer;

import taskvisualizer.controllers.UniversalController;
import java.time.LocalDateTime;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Christian Brandon
 */
public class TaskVisualizer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        UniversalController.setStage(stage);  
        
        // edit this to change the starting screen
        // in the final product, this will be the log-in screen
        String startScreen = "Requirement_Screen";
        
        UniversalController.setCurrentScreen(startScreen);
        Parent root = FXMLLoader.load(getClass().getResource("fxml/" + startScreen + ".fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("TaskVisualizer.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Task Visualizer");
        stage.show();
        
        // initialize the popup stage
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(UniversalController.getStage());
        
        UniversalController.setPopupStage(popup);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        User goldenRevolver = new User("Golden Revolver", "GoldenMan1234");
        UniversalController.setUser(goldenRevolver);
        /*  
            This is temporary. 
            In the final app, Users will be created from the sign-up screen and added to the userList. 
            Upon log-in, findUser() will take the name and password as parameters. 
            If there is a match, the returned User will be set as the currentUser with setUser().
        */
        
        // Scenario 1: School Day
        LocalDateTime literatureTime = LocalDateTime.of(2024, 1, 22, 0, 0);
        Requirement literature = new Requirement("Literature Review", "Research", literatureTime);
        goldenRevolver.addTask(literature);
        
        LocalDateTime pedigreeTime = LocalDateTime.of(2024, 1, 16, 18, 0);
        Requirement pedigree = new Requirement("Family Pedigree", "Biology", pedigreeTime);
        goldenRevolver.addTask(pedigree);
        
        LocalDateTime exercise12Time = LocalDateTime.of(2024, 2, 12, 15, 22);
        Requirement exercise12 = new Requirement("Exercise 12", "Computer Science", exercise12Time);
        goldenRevolver.addTask(exercise12);
        
        // Scenario 2: Family Reunion
        LocalDateTime gatheringStart = LocalDateTime.of(2024, 6, 30, 12, 30);
        LocalDateTime gatheringEnd = LocalDateTime.of(2024, 6, 30, 18, 00);
        Event gathering = new Event("Family Gathering", gatheringStart, gatheringEnd);
        goldenRevolver.addTask(gathering);

        LocalDateTime fairStart = LocalDateTime.of(2024, 7, 24, 8, 0);
        LocalDateTime fairEnd = LocalDateTime.of(2024, 7, 31, 16, 15);
        Event fair = new Event("School Fair", fairStart, fairEnd);
        goldenRevolver.addTask(fair);
        
        // Scenario 3: Workout Plan
        Habit exercisePlan = new Habit("Complete exercise plan");
        goldenRevolver.addTask(exercisePlan);
        
        LocalDateTime pushUpsDate = LocalDateTime.of(2024, 1, 30, 23, 59);
        Goal pushUps = new Goal("Push-ups", 10, pushUpsDate);
        goldenRevolver.addTask(pushUps);

        LocalDateTime loseWeightDate = LocalDateTime.of(2024, 2, 12, 23, 59);
        Goal loseWeight = new Goal("Lose weight", 50.0, 40.0, false, loseWeightDate);
        goldenRevolver.addTask(loseWeight);
        
        // April 2024 Test
        LocalDateTime birthdayStart = LocalDateTime.of(2024, 4, 1, 10, 0);
        LocalDateTime birthdayEnd = LocalDateTime.of(2024, 4, 1, 15, 0);
        Event catBirthday = new Event("Cat birthday :3", birthdayStart, birthdayEnd);
        goldenRevolver.addTask(catBirthday);
        
        LocalDateTime aprilFoolsDay = LocalDateTime.of(2024, 4, 1, 0, 0);
        Event aprilFools = new Event("April Fools", aprilFoolsDay);
        goldenRevolver.addTask(aprilFools);
        
        LocalDateTime listingTime = LocalDateTime.of(2024, 4, 12, 16, 30);
        Requirement listing = new Requirement("Protocol Listing", "Research", listingTime);
        goldenRevolver.addTask(listing);
        
        // Different Years Test
        LocalDateTime start2025 = LocalDateTime.of(2025, 1, 1, 0, 0);
        Event newYear2025 = new Event("2025 New Year!", start2025);
        goldenRevolver.addTask(newYear2025);
        
        LocalDateTime piDayStart = LocalDateTime.of(2027, 3, 14, 0, 0);
        Event piDay = new Event("Pi day", piDayStart);
        goldenRevolver.addTask(piDay);
        
        launch(args);
    }
}