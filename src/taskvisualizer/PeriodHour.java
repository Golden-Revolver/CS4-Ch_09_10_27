package taskvisualizer;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Christian Brandon
 */
public class PeriodHour extends Hour {
    private String period;
    private List<String> periods = Arrays.asList("AM", "PM");
            
    public PeriodHour(int hour, String period) {
        super(hour);
        this.period = period;
        
        if (hour < 0 || hour > 12) {
            throw new IllegalArgumentException("Hour must be between 0 and 12.");
        } else if (!periods.contains(period)) {
            throw new IllegalArgumentException("Period must be AM or PM.");
        }
    }
    
    public String getPeriod() {
        return period;
    }
    
    public MilitaryHour get24Hour() {
        boolean hourCheck = hour == 12;
        boolean amCheck = period.equals("AM");
        
        if (amCheck && hourCheck) hour = 0;
        else if (!amCheck && !hourCheck) hour += 12;
        return new MilitaryHour(hour);
    }
    
    @Override
    public String toString() {
        return hour + " " + period;
    }
}