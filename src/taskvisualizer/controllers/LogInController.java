package taskvisualizer.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

/**
 *
 * @author cyd jairo
 */
public class LogInController extends UniversalController implements Initializable{
    @FXML private Label SignUp;
    
    @FXML
    private void moveToSignUp(){
        SignUp.setOnMousePressed(createMouseHandler(() -> switchScreen("sign-up screen")));
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        
    }
}