package entity;

import java.util.Calendar;

public class TaskEntity {
    private boolean _isFloating;
    private boolean _isFullDay;
    private Calendar _dueDate;
    private Calendar _dateCreated;
    private String _name;
    private String _description;
    private int _id;

    private static int currentID = 0;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public void setFloating(boolean isFloating) {
        this._isFloating = isFloating;
    }

    /**
     * Assigns an ID to the new object and record its created timing. ONLY to be
     * used by task's constructor
     */
    private void initIdAndDate() {
        _id = currentID;
        currentID++;
        _dateCreated = Calendar.getInstance();
    }

    public TaskEntity() {
        initIdAndDate();

        _name = "";
        _description = "";
        _isFloating = true;
    }

    public TaskEntity(String name) {
        initIdAndDate();

        _name = name;
        _description = "";
        _isFloating = true;
    }

    public TaskEntity(String name, Calendar dueDate, boolean isFullDay) {
        initIdAndDate();

        _name = name;
        _description = "";
        _dueDate = dueDate;
        _isFloating = false;
        _isFullDay = isFullDay;
    }

    public TaskEntity(String name, Calendar dueDate, boolean isFullDay, String description) {
        initIdAndDate();

        _name = name;
        _description = description;
        _dueDate = dueDate;
        _isFullDay = isFullDay;
        _isFloating = false;
    }

    public TaskEntity(String name, String description) {
        initIdAndDate();

        _name = name;
        _description = description;
        _isFloating = true;
    }

    public String getName() {
        return _name;
    }

    public Calendar getDueDate() {
        if (!_isFloating) {
            return _dueDate;
        } else {
            return null;
        }
    }

    public void setDueDate(Calendar dueDate, boolean isFullDay) {
        _isFloating = false;
        _dueDate = dueDate;
        _isFullDay = isFullDay;
    }

    public Calendar getDateCreated() {
        return _dateCreated;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public boolean isFloating() {
        return _isFloating;
    }

    public void setIfFullDay(boolean isFullDay) {
        _isFullDay = isFullDay;
    }

    public boolean isFullDay() {
        return _isFullDay;
    }
}