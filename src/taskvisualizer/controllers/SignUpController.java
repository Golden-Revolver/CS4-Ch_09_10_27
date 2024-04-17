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
public class SignUpController extends UniversalController implements Initializable{
    @FXML private Label LogIn;
    
    @FXML
    private void moveToLogIn(){
        LogIn.setOnMousePressed(createMouseHandler(() -> switchScreen("login-screen")));
        
    }
    @Override
    public void initialize(URL location, ResourceBundle resources){
        
    }
}