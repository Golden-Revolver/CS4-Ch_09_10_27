package taskvisualizer;

import java.util.*;

/**
 *
 * @author Christian Brandon
 */
public abstract class Task {
    protected String name, id, notes;
    protected boolean complete;
    
    public Task(String name) {
        this.name = name;
        id = UUID.randomUUID().toString();
        notes = "";
        complete = false;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public abstract String getNameFormat();
    public abstract String getDataFormat();
    
    public String getId() {
        return id;
    }
    
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public boolean isComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}