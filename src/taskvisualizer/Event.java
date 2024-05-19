package taskvisualizer;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Christian Brandon
 */
public class Event extends Task implements Comparable<Event> {
    private LocalDateTime startDate, endDate;
    private String category;
    
    public Event(String name, LocalDateTime date) {
        this(name, date, date);
    }
    public Event(String name, LocalDateTime startDate, LocalDateTime endDate) {
        this(name, startDate, endDate, "None");
    }
    public Event(String name, LocalDateTime startDate, 
            LocalDateTime endDate, String category) {
        super(name);
        this.startDate = startDate;
        this.endDate = endDate;
        if (category == null) category = "None"; // default value
        this.category = category;
    }
    
    @Override
    public int compareTo(Event e) { 
        if (startDate.equals(e.getStartDate())) return 0;
        else if (startDate.isAfter(e.getStartDate())) return 1;
        else return -1;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) { 
        this.category = category;
    }
    
    @Override
    public String getNameFormat() {
        return name;
    }
    @Override
    public String getDataFormat() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");
        
        String startDateFormat = startDate.format(dateFormat);
        String startTimeFormat = startDate.format(timeFormat);
        
        if (startDate.equals(endDate)) {
            return String.format("%s @ %s", startDateFormat, startTimeFormat);
        } else {
            String endDateFormat = endDate.format(dateFormat);
            String endTimeFormat = endDate.format(timeFormat);
            return String.format("%s @ %s - %s @ %s", startDateFormat, startTimeFormat,
                    endDateFormat, endTimeFormat);
        }
    }
}