package minicalendar;
import java.util.*;
/**
 *
 * @author Christian Brandon
 */
public class User {
    private String name, password;
    private ArrayList<Task> taskList;
    private ArrayList<Habit> habitList;
    private ArrayList<Goal> goalList;
    private ArrayList<Event> eventList;
    private ArrayList<Requirement> requirementList;
    private static ArrayList<User> userList = new ArrayList<>();
    
    public User(String name, String password) {
        this.name = name;
        this.password = password;
        taskList = new ArrayList<>();
        habitList = new ArrayList<>();
        goalList = new ArrayList<>();
        eventList = new ArrayList<>();
        requirementList = new ArrayList<>();
        userList.add(this);
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public ArrayList<Task> getTaskList() {
        return taskList;
    }
    public ArrayList<Habit> getHabitList() {
        return habitList;
    }
    public ArrayList<Goal> getGoalList() {
        return goalList;
    }
    public ArrayList<Event> getEventList() {
        return eventList;
    }
    public ArrayList<Requirement> getRequirementList() {
        return requirementList;
    }
    
    public static User findUser(String name, String password) throws UserNotFoundException {
        boolean nameCheck, passwordCheck;
        for (User u : userList) {
            nameCheck = u.getName().equals(name);
            passwordCheck = u.getPassword().equals(password);
            if (nameCheck && passwordCheck) return u;
        }
        throw new UserNotFoundException("Your username or password is wrong.");
    }
    
    public void addTask(Task task) {
        taskList.add(task);
        if (task instanceof Habit) habitList.add((Habit) task);
        else if (task instanceof Goal) goalList.add((Goal) task);
        else if (task instanceof Event) eventList.add((Event) task);
        else if (task instanceof Requirement) requirementList.add((Requirement) task);
    }  
    public void deleteTask(Task task) throws TaskNotFoundException {
        if (!taskList.contains(task)) {
            throw new TaskNotFoundException("Task not found!");
        } else { 
            taskList.remove(task);
            if (task instanceof Habit) habitList.remove((Habit) task);
            else if (task instanceof Goal) goalList.remove((Goal) task);
            else if (task instanceof Event) eventList.remove((Event) task);
            else if (task instanceof Requirement) requirementList.remove((Requirement) task);
        }
    }
    public Task getTaskById(String id) {
        for (Task t : taskList) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }
}