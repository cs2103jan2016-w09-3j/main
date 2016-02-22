package entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Task {
    private boolean isFloating;
    private Calendar dueDate;
    private Calendar dateCreated;
    private String name;
    private String description;
    private int id;

    private static int currentID = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFloating(boolean isFloating) {
        this.isFloating = isFloating;
    }

    /**
     * Assigns an ID to the new object and record its created timing. ONLY to be
     * used by task's constructor
     */
    private void initIdAndDate() {
        id = currentID;
        currentID++;
        dateCreated = Calendar.getInstance();
    }

    public Task() {
        initIdAndDate();

        name = "";
        description = "";
        isFloating = true;
    }

    public Task(String _name) {
        initIdAndDate();

        name = _name;
        description = "";
        isFloating = true;
    }

    public Task(String _name, Calendar _dueDate) {
        initIdAndDate();

        name = _name;
        description = "";
        dueDate = _dueDate;
        isFloating = false;
    }

    public Task(int id, Calendar calendar, String desc) {

    }

    public Task(String _name, Calendar _dueDate, String _description) {
        initIdAndDate();

        name = _name;
        description = _description;
        dueDate = _dueDate;
        isFloating = false;
    }

    public Task(String _name, String _description) {
        initIdAndDate();

        name = _name;
        description = _description;
        isFloating = true;
    }

    public String getName() {
        return name;
    }

    public Calendar getDueDate() {
        if (!isFloating) {
            return dueDate;
        } else {
            return null;
        }
    }

    public void setDueDate(Calendar _dueDate) {
        isFloating = false;
        dueDate = _dueDate;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String _description) {
        description = _description;
    }

    public boolean isFloating() {
        return isFloating;
    }

   
}