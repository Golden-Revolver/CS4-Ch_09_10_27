package taskvisualizer;

/**
 *
 * @author Christian Brandon
 */
public abstract class Hour {
    protected int hour;
    
    protected Hour(int hour) {
        this.hour = hour;
    }
    
    public int getHour() {
        return hour;
    }
}