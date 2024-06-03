package taskvisualizer;

/**
 *
 * @author Christian Brandon
 */
public class MilitaryHour extends Hour {
    
    public MilitaryHour(int hour) {
        super(hour);
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23.");
        }
    }
    
    public PeriodHour get12Hour() {
        String period = "AM";
        
        if (hour == 0) hour = 12;
        if (hour > 12) hour -= 12;
        if (hour >= 12) period = "PM";
        return new PeriodHour(hour, period);
    }
    
    @Override
    public String toString() {
        return String.valueOf(hour);
    }
}