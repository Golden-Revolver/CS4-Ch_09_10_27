package taskvisualizer;

import java.time.LocalDate;
import java.time.YearMonth;
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
    private static LinkedHashSet<String> categoryList = new LinkedHashSet<>();
    
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
    
    public LinkedHashSet<String> getCategoryList() {
        return categoryList;
    }
    public void addCategory(String category) {
        categoryList.add(category);
    }
    public void deleteCategory(String category) {
        if (category.equals("None")) return;
        
        categoryList.remove(category);
        for (Event e : eventList) {
            // reset all events of the category
            if (e.getCategory().equals(category)) e.setCategory("None");
        }
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
    
    /**
     * Adds a task to the user's task list. <br>
     * <i> (Note that this task cannot be accessed in the task lists of other users unless added). </i> <br><br>
     * 
     * The task will be added to another list of the user based on its subtype.
     * <ul>
     * <li> Event - eventList </li>
     * <li> Requirement - requirementList </li>
     * <li> Goal - goalList </li>
     * <li> Habit - habitList </li>
     * </ul>
     * @param task The task to be added.
     */
    
    public void addTask(Task task) {
        taskList.add(task);
        if (task instanceof Habit) habitList.add((Habit) task);
        else if (task instanceof Goal) goalList.add((Goal) task);
        else if (task instanceof Event) {
            Event event = (Event) task;
            eventList.add(event);
            categoryList.add(event.getCategory()); // set auto checks for duplicates
        }
        else if (task instanceof Requirement) requirementList.add((Requirement) task);
    }  
    public void deleteTask(Task task) {
        taskList.remove(task);
        if (task instanceof Habit) habitList.remove((Habit) task);
        else if (task instanceof Goal) goalList.remove((Goal) task);
        else if (task instanceof Event) eventList.remove((Event) task);
        else if (task instanceof Requirement) requirementList.remove((Requirement) task);
    }
    public Task getTaskById(String id) {
        for (Task t : taskList) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }
    public ArrayList<Event> getEventByName(String name) {
        ArrayList<Event> filteredList = new ArrayList<>();
        for (Event e : eventList) {
            if (e.getName().contains(name)) filteredList.add(e);
        }
        return filteredList;
    }
    public ArrayList<Event> getEventByDate(LocalDate date) {
        ArrayList<Event> filteredList = new ArrayList<>();
        for (Event e : eventList) {
            LocalDate startDate = e.getStartDate().toLocalDate();
            LocalDate endDate = e.getEndDate().toLocalDate();
            if (!startDate.isAfter(date) && !endDate.isBefore(date)) filteredList.add(e);
        }
        return filteredList;
    }
    public ArrayList<Event> getEventByMonth(YearMonth month) {
        ArrayList<Event> filteredList = new ArrayList<>();
        for (Event e : eventList) {
            YearMonth eventMonth = YearMonth.from(e.getStartDate());
            if (eventMonth.equals(month)) filteredList.add(e);
        }
        return filteredList;
    }
    public ArrayList<Event> getEventByYear(int year) {
        ArrayList<Event> filteredList = new ArrayList<>();
        for (Event e : eventList) {
            if (e.getStartDate().getYear() == year) filteredList.add(e);
        }
        return filteredList;
    }
    public ArrayList<Requirement> getRequirementByDate(LocalDate date) {
        ArrayList<Requirement> filteredList = new ArrayList<>();
        for (Requirement r : requirementList) {
            LocalDate requirementDate = r.getDeadline().toLocalDate();
            if (requirementDate.equals(date)) filteredList.add(r);
        }
        return filteredList;
    }
    public ArrayList<Goal> getGoalByDate(LocalDate date) {
        ArrayList<Goal> filteredList = new ArrayList<>();
        for (Goal g : goalList) {
            if (g.getDeadline().equals(date)) filteredList.add(g);
        }
        return filteredList;
    }
}