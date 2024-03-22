package taskvisualizer;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author Christian Brandon
 */
public class Event extends Task implements Comparable<Event> {
    private LocalDateTime date;
    private String location;
    private ArrayList<String> participants;
    
    public Event(String name, LocalDateTime date) {
        super(name);
        this.date = date;
        this.participants = new ArrayList<>();
    }
    public Event(String name, LocalDateTime date, String location) {
        super(name);
        this.date = date;
        this.location = location;
        this.participants = new ArrayList<>();
    }
    
    @Override
    public int compareTo(Event e) { 
        if (date.equals(e.getDate())) return 0;
        else if (date.isAfter(e.getDate())) return 1;
        else return -1;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    @Override
    public String getNameFormat() {
        return name;
    }
    @Override
    public String getDataFormat() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        return String.format("%s | %s", date.format(dateFormat), 
                date.format(timeFormat));
    }
    
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    
    public ArrayList<String> getParticipants() {
        return participants;
    }
    
    public void addParticipant(String person) {
        participants.add(person);
    }
    public void removeParticipant(String person) {
        if (participants.contains(person)) {
            participants.remove(person);
        }
    }
}