package taskvisualizer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Christian Brandon
 */
public class Habit extends Task implements Comparable<Habit> {
    private int streak, completed, missed;
    private LocalDate lastChecked;
    private ArrayList<Boolean> weekStatus = new ArrayList<>(
            Collections.nCopies(7, false));
    
    public Habit(String name) {
        super(name);
        streak = completed = missed = 0;
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
    public void increaseStreak() {
        streak++;
    }
    public void decreaseStreak() {
        streak--;
    }
    
    public int getCompleted() {
        return completed;
    }
    public void setCompleted(int completed) {
        this.completed = completed;
    }
    public void increaseCompleted() {
        completed++;
    }
    public void decreaseCompleted() {
        completed--;
    }
    
    public int getMissed() {
        return missed;
    }
    public void setMissed(int missed) {
        this.missed = missed;
    }
    public void increaseMissed() {
        missed++;
    }
    
    public double getConsistency() {
        if (completed == 0 && missed == 0) return 0;
        else return completed / (completed + missed);
    }
    
    public LocalDate getLastChecked() {
        return lastChecked;
    }
    public LocalDate setLastChecked(LocalDate date) {
        return lastChecked = date;
    }
    
    public ArrayList<Boolean> getWeekStatus() {
        return weekStatus;
    }
    public void setWeekStatus(ArrayList<Boolean> weekStatus) {
        this.weekStatus = weekStatus;
    }
    public void resetWeekStatus() {
        Collections.fill(weekStatus, false);
    }
    
    public boolean getWeekDayStatus(int weekDay) {
        return weekStatus.get(weekDay);
    }
    public void setWeekDayStatus(int weekDay, boolean status) {
        weekStatus.set(weekDay, status);
    }

    public void updateStreak() {
        if (!complete) streak = 0;
        complete = false;
    }
}