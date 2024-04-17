package taskvisualizer;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Christian Brandon
 */
public class Goal extends Task implements Comparable<Goal> {
    private LocalDate deadline;
    private double progress, target;
    private final double initial;
    private boolean reward, late;
    
    public Goal(String name, double target, LocalDate deadline) {
        super(name);
        this.target = target;
        progress = 0;
        reward = true;
        this.deadline = deadline;
        initial = progress;
        late = LocalDate.now().isAfter(deadline);

    }
    public Goal(String name, double target, 
            boolean reward, LocalDate deadline) {
        super(name);
        this.target = target;
        progress = 0;
        initial = progress;
        this.reward = reward;
        this.deadline = deadline;
        late = LocalDate.now().isAfter(deadline);
    }
    public Goal(String name, double progress, 
            double target, LocalDate deadline) {
        super(name);
        this.progress = progress;
        initial = progress;
        this.target = target;
        reward = true;
        this.deadline = deadline;
        late = LocalDate.now().isAfter(deadline);
    }
    public Goal(String name, double progress, double target, 
            boolean reward, LocalDate deadline) {
        super(name);
        this.progress = progress;
        initial = progress;
        this.target = target;
        this.reward = reward;
        this.deadline = deadline;
        late = LocalDate.now().isAfter(deadline);
    }
    
    @Override
    public int compareTo(Goal g) {
        if (deadline.equals(g.getDeadline())) return 0;
        else if (deadline.isAfter(g.getDeadline())) return 1;
        else return -1;
    }
    
    public double getProgress() {
        return progress;
    }
    public void setProgress(double progress) {
        this.progress = progress;
        updateGoal();
    }
    public void updateProgress(double change) {
        progress += change;
        updateGoal();
    }
    
    public double getTarget() {
        return target;
    }
    public void setTarget(double target) {
        this.target = target;
    }
    
    public double getInitial() {
        return initial;
    }
    
    public boolean getReward() {
        return reward;
    }
    public void setReward(boolean reward) {
        this.reward = reward;
    }
    
    public LocalDate getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    
    public boolean isLate() {
        return late;
    }
    public void setLate(boolean late) {
        this.late = late;
    }
    public void updateLate() {
        late = LocalDate.now().isAfter(deadline);
    }
    
    @Override
    public String getNameFormat() {
        return name;
    }
    @Override
    public String getDataFormat() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return String.format("Progress: %.0f/%.0f%nFinish by %s",
                progress, target, deadline.format(dateFormat));
    }
    
    public double getGoalFraction() {
        double goalFraction;
        if (reward) goalFraction = progress / target;
        else goalFraction = (initial - progress) / (initial - target);
        
        // must be a value between 0 and 1
        if (goalFraction < 0) goalFraction = 0;
        if (goalFraction > 1) goalFraction = 1;
        return goalFraction;
    }
    
    /* If reward is true, the User is rewarded for increasing progress.
    If reward is false, the User is rewarded for decreasing progress. */
    public void updateGoal() {
        int counter = reward ? 1 : -1;
        complete = (progress * counter >= target * counter);
    }
}