package minicalendar;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import java.util.*;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 *
 * @author Christian Brandon
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML private Text monthYear;
    @FXML private GridPane calendar;
    
    private int currentMonth, currentYear;
    
    private void updateCalendar(int m, int y) {
        Locale locale = new Locale("en");
        String month = Month.of(m).getDisplayName(TextStyle.FULL_STANDALONE, locale);
        
        monthYear.setText(month + " " + Integer.toString(y));
        
        // resets all text starting from the third row
        for (int i = 0; i < calendar.getChildren().size() - 8; i++) {
            Text t = (Text) calendar.getChildren().get(i + 8);
            t.setText("");
        }
        
        LocalDate firstDay = LocalDate.of(y, m, 1);
        int weekDay = firstDay.getDayOfWeek().getValue();
        if (weekDay == 7) weekDay = 0; // resets value of sunday to 0    
        int firstIndex = weekDay + 8; // gets a cell on the third row
        
        // sets text for the first day of the month
        Text firstText = (Text) calendar.getChildren().get(firstIndex);
        firstText.setText("1");
        
        for (int i = 1; i < firstDay.lengthOfMonth(); i++) {
            Text t = (Text) calendar.getChildren().get(firstIndex + i);
            t.setText(Integer.toString(i + 1));
        }
    }
    
    @FXML
    private void previousMonth(ActionEvent event) {
        if (currentMonth == 1) {
            currentMonth = 12;
            currentYear--;
        }
        else currentMonth--;
        updateCalendar(currentMonth, currentYear);
    }
    
    @FXML
    private void nextMonth(ActionEvent event) {
        if (currentMonth == 12) {
            currentMonth = 1;
            currentYear++;
        } 
        else currentMonth++;
        updateCalendar(currentMonth, currentYear);
    }
    
    private String capitalize(String s) {
        String first = s.substring(0, 1).toUpperCase(); // capital first letter
        String rest = s.substring(1).toLowerCase(); // lowercase other letters
        return first + rest;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LocalDateTime now = LocalDateTime.now();
        currentMonth = now.getMonthValue();
        currentYear = now.getYear();
     
        updateCalendar(currentMonth, currentYear);
    }       
}