package taskvisualizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author Christian Brandon
 */
public class Requirement extends Task implements Comparable<Requirement> {
    private String subject;
    private LocalDateTime deadline;
    private boolean late;
    
    public Requirement(String name, String subject, LocalDateTime deadline) {
        super(name);
        this.subject = subject;
        this.deadline = deadline;
        late = LocalDateTime.now().isAfter(deadline);
    }
    
    @Override
    public int compareTo(Requirement r) {
        if (deadline.equals(r.getDeadline())) return 0;
        else if (deadline.isAfter(r.getDeadline())) return 1;
        else return -1;
    }
    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    @Override
    public String getNameFormat() {
        return String.format("%s | %s", subject, name);
    }
    @Override
    public String getDataFormat() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        return String.format("%s | %s", deadline.format(dateFormat),
                deadline.format(timeFormat));
    }
    
    public boolean isLate() {
        return late;
    }
    public void setLate(boolean late) {
        this.late = late;
    }
    public void updateLate() {
        late = LocalDateTime.now().isAfter(deadline);
    }
}