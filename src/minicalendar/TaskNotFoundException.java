package minicalendar;
/**
 *
 * @author Christian Brandon
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException() {
        
    }

    public TaskNotFoundException(String msg) {
        super(msg);
    }
}