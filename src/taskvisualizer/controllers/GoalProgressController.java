/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package taskvisualizer.controllers;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import taskvisualizer.Goal;
import taskvisualizer.PeriodHour;
import static taskvisualizer.controllers.UniversalController.activeTask;
import static taskvisualizer.controllers.UniversalController.currentUser;
import static taskvisualizer.controllers.UniversalController.popupStage;

/**
 *
 * @author Kiley Sorino & Christian Brandon
 */
public class GoalProgressController extends UniversalController implements Initializable{
   
    @FXML ImageView headerIcon, resetIcon, addIcon;
    @FXML private Button reset;
    @FXML private Button add;
    @FXML private TextField progressInput;
    @FXML private VBox box;
    private Image headerImage, resetImage, addImage;
    private ArrayList<String> DayNight = new ArrayList<>();
    private ArrayList<String> rewardType = new ArrayList<>();
    
  
    
    @FXML
    private void editProgress(){
        double progress = Integer.parseInt(progressInput.getText());

            // editing a goal
            Goal g = (Goal) activeTask;
            String reward = g.getReward();
            if(reward.equals("increase")){
                g.updateProgress(progress);
                
            }
            else {
                g.updateProgress(-1 * progress);
            }
        
        
        popupStage.hide();
        System.out.println(progress);
    }
    
    @FXML    
    private void resetParameters() {
        progressInput.clear();
    }


   @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}   

