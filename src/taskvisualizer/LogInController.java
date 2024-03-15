package taskvisualizer;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author cyd jairo
 */
public class LogInController extends UniversalController implements Initializable{
    @FXML private Label SignUp;
    
    @FXML
    private void moveToSignUp(){
        SignUp.setOnMousePressed(switchScreenHandler("signup-screen"));
        
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        
    }
}
