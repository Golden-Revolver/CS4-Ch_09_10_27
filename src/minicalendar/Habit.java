package minicalendar;

import java.time.LocalDate;

/**
 *
 * @author Christian Brandon
 */
public class Habit extends Task implements Comparable<Habit> {
    private int streak;
    private LocalDate lastChecked;
    
    public Habit(String name) {
        super(name);
        streak = 0;
        lastChecked = null;
    }
    
    @Override
    public int compareTo(Habit h) {
        if (streak == h.getStreak()) return 0;
        else if (streak > h.getStreak()) return 1;
        else return -1;
    }
    
    @Override
    public String getNameFormat() {
        return name;
    }
    @Override
    public String getDataFormat() {
        return String.format("Streak: %d", streak);
    }
    
    public int getStreak() {
        return streak;
    }
    public void setStreak(int streak) {
        this.streak = streak;
    }
    public LocalDate getLastChecked() {
        return lastChecked;
    }
    public LocalDate setLastChecked(LocalDate date) {
        return lastChecked = date;
    }
    
    public void increaseStreak() {
        streak++;
    }
    public void decreaseStreak() {
        streak--;
    }
    public void updateStreak() {
        if (!complete) streak = 0;
        complete = false;
    }
}