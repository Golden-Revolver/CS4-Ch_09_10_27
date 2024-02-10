package minicalendar;
import java.util.*;
/**
 *
 * @author Christian Brandon
 */
public abstract class Task {
    protected String name, id;
    protected boolean complete;
    
    public Task(String name) {
        this.name = name;
        id = UUID.randomUUID().toString();
        complete = false;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public abstract String getNameFormat();
    public abstract String getDataFormat();
    
    public boolean isComplete() {
        return complete;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}