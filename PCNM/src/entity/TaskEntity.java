/**
 * @author User Angie A0126357A
 * @@author A0126357A
 */
package entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.emory.mathcs.backport.java.util.Collections;
import logic.TaskDateComparator;
import logic.TaskManager;
import logic.TaskUtils;

public class TaskEntity {
    public static final int NOT_ASSOCIATED = 0;
    public static final int ASSOCIATED = 1;
    public static final int PROJECT_HEAD = 2;

    private boolean _isFloating;
    private boolean _isFullDay;
    private Calendar _startDate;
    private Calendar _dueDate;
    private Calendar _dateCreated;
    private Calendar _completionDate;
    private String _name;
    private String _description;
    private int _id;
    private int _association_status;
    private boolean _isCompleted;

    private String _hashtags;
    private ArrayList<TaskEntity> _associations;
    private String _associationIDs;

    private static int currentId = 0;

    public static void setCurrentId(int newId) {
        currentId = newId;
    }

    public static int getCurrentId() {
        return currentId;
    }

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
    private void initCommonData() {
        _id = currentId;
        currentId++;
        _dateCreated = Calendar.getInstance();
        _association_status = NOT_ASSOCIATED;
        _associations = new ArrayList<TaskEntity>();
        _completionDate = Calendar.getInstance();
        // Tasks to have a default same value of completion date at the start
        _completionDate.set(0, 0, 0);
        _hashtags = "";
    }

    public TaskEntity() {
        initCommonData();

        _name = "";
        _description = "";
        _isFloating = true;
    }

    public TaskEntity(String name) {
        initCommonData();

        _name = name;
        _description = "";
        _isFloating = true;
    }

    public TaskEntity(String name, Calendar startDate, Calendar dueDate, boolean isFullDay) {
        initCommonData();

        _name = name;
        _description = "";
        _startDate = startDate;
        _dueDate = dueDate;
        _isFloating = false;
        _isFullDay = isFullDay;
    }

    public TaskEntity(String name, Calendar startDate, Calendar dueDate, boolean isFullDay,
            String description) {
        initCommonData();

        _name = name;
        _description = description;
        _startDate = startDate;
        _dueDate = dueDate;
        _isFullDay = isFullDay;
        _isFloating = false;
    }

    public TaskEntity(String name, String description) {
        initCommonData();

        _name = name;
        _description = description;
        _isFloating = true;
    }

    public TaskEntity(boolean isFloating, boolean isFullDay, Calendar startDate, Calendar dueDate,
            Calendar dateCreated, String name, String description, int id, int association_status,
            ArrayList<TaskEntity> associations, String associationIDs, boolean isCompleted,
            Calendar completionDate, String hashtags) {
        _isFloating = isFloating;
        _isFullDay = isFullDay;
        _startDate = startDate;
        _dueDate = dueDate;
        _dateCreated = dateCreated;
        _name = name;
        _description = description;
        _id = id;
        _association_status = association_status;
        _associations = associations;
        _associationIDs = associationIDs;
        _isCompleted = isCompleted;
        _completionDate = completionDate;
        _hashtags = hashtags;
    }

    public int getAssociationState() {
        return _association_status;
    }

    public ArrayList<TaskEntity> getAssociations() {
        return _associations;
    }

    /**
     * Mark a task as done and move the completed task into the
     * completedTaskEntities array.
     */
    public void markAsDone() {
        // Skip the whole process if it was already done in the first place
        if (_isCompleted) {
            return;
        }
        _isCompleted = true;
        _completionDate = Calendar.getInstance();
    }

    public boolean isCompleted() {
        return _isCompleted;
    }

    public Calendar getCompletionDate() {
        return _completionDate;
    }

    /**
     * Function to initialize associations array if it is null (Used for
     * overwriting it being set to null by the file loader)
     */
    public void initAssociations() {
        if (_associations == null) {
            _associations = new ArrayList<TaskEntity>();
        }
    }

    /**
     * Builds an ArrayList of all task's ID in associations for saving. Used to
     * rebuild associations on load from file
     * 
     * @return - ArrayList matching associations, but instead of having the task
     *         object, has its corresponding ID
     */
    public void buildAssociationsId() {
        _associationIDs = "";

        assert _associations != null : "Associations is null at build associations when saving task: "
                + getName();

        for (int i = 0; i < _associations.size(); i++) {
            _associationIDs += Integer.toString(_associations.get(i).getId()) + ",";
        }
        _associations = null;
    }

    public TaskEntity clone() {
        TaskEntity newInstance = new TaskEntity(_isFloating, _isFullDay, _startDate, _dueDate, _dateCreated,
                _name, _description, _id, _association_status, _associations, _associationIDs, _isCompleted,
                _completionDate, _hashtags);
        return newInstance;
    }

    public void addHashtag(String newTag) {
        if (_hashtags == null) {
            _hashtags = "";
        }
        _hashtags += newTag;
    }

    /**
     * Gets the saved string of IDs for the association list
     * 
     * @return String of all association IDs separated by a comma
     */
    public String getSavedAssociations() {
        return _associationIDs;
    }

    /**
     * Gets the task representing the project head
     * 
     * @return null if project head cant be found, or if this task is not under
     *         another task TaskEntity item that is he project head of this
     *         object
     */
    public TaskEntity getProjectHead() {
        if (_association_status == NOT_ASSOCIATED || _association_status == PROJECT_HEAD) {
            return null;
        } else {
            try {
                return _associations.get(0);
            } catch (IndexOutOfBoundsException e) {
                TaskManager.getInstance()
                        .logError("Error at TaskEntity.java: Associated task has no project head");
                return null;
            }
        }
    }

    /**
     * Function to link this object as a task under projectHead. Project heads
     * are not allowed to be under other tasks
     * 
     * @param projectHead
     *            - Task to be added under
     */
    public void setAssociationHead(TaskEntity projectHead) {
        if (_association_status == PROJECT_HEAD) {
            return;
        }

        if (_associations == null) {
            _associations = new ArrayList<TaskEntity>();
        } else {
            // Unlink from last project first
            removeSelfFromProject();
            _associations.clear();
        }
        // Tasks under other tasks only have the project head in their
        // associations array
        _associations.add(projectHead);
        _association_status = ASSOCIATED;
    }

    /**
     * Called when the project head is deleted
     * 
     * @return
     */
    public void disassociateFromDeletedProject() {
        _association_status = TaskEntity.NOT_ASSOCIATED;
        _associations.clear();
    }

    public boolean removeSelfFromProject() {
        if (getAssociationState() == TaskEntity.ASSOCIATED) {
            TaskEntity prevProjectHead = getAssociations().get(0);
            prevProjectHead.getAssociations().remove(this);
            return true;
        } else if (getAssociationState() == TaskEntity.PROJECT_HEAD) {
            for (int i = 0; i < _associations.size(); i++) {
                _associations.get(i).disassociateFromDeletedProject();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Function for init function to reload all the associations
     * 
     * @param taskToInsert
     *            - Task to be inserted into the associations list
     */
    public void loadAssociation(TaskEntity taskToInsert) {
        if (_associations == null) {
            _associations = new ArrayList<TaskEntity>();
        }
        _associations.add(taskToInsert);
    }

    /**
     * Function to link an object under this task
     * 
     * @param childTask
     *            - Task to be under this task
     * @return true if this task can be a project head false if this task is
     *         under another task and cannot be a project head
     */
    public boolean addAssociation(TaskEntity childTask) {
        if (_association_status == ASSOCIATED) {
            return false;
        } else {
            _association_status = PROJECT_HEAD;
            int idToInsert = findPositionToInsert(childTask);
            _associations.add(idToInsert, childTask);
            return true;
        }
    }

    /**
     * Gets this TaskEntity's position in its association list
     * 
     * @return slot number in the project head's _association array, or -1 if it
     *         doesn't belong to any projects
     */
    public int getAssociationPosition() {
        if (_association_status == PROJECT_HEAD) {
            return 0;
        } else if (_association_status == ASSOCIATED) {
            ArrayList<TaskEntity> displayedAssociations = getProjectHead().getAssociations();
            for (int i = 0; i < displayedAssociations.size(); i++) {
                if (displayedAssociations.get(i) == this) {
                    // i + 1 because accounting for project head being slotted
                    // into the first position of the list
                    return i + 1;
                }
            }
            return -1;
        } else {
            return -1;
        }
    }

    public ArrayList<TaskEntity> getDisplayAssociations() {
        if (_association_status == PROJECT_HEAD) {
            ArrayList<TaskEntity> displayedAssociations = (ArrayList<TaskEntity>) _associations.clone();
            displayedAssociations.add(0, this);
            return displayedAssociations;
        } else if (_association_status == ASSOCIATED) {
            ArrayList<TaskEntity> displayedAssociations = (ArrayList<TaskEntity>) getProjectHead()
                    .getAssociations().clone();
            displayedAssociations.add(0, getProjectHead());
            return displayedAssociations;
        } else {
            return new ArrayList<TaskEntity>();
        }
    }

    private int findPositionToInsert(TaskEntity newTask) {
        if (_associations == null) {
            _associations = new ArrayList<TaskEntity>();
        }
        int idToInsert = Collections.binarySearch(_associations, newTask, new TaskDateComparator());

        // Due to Collections.binarySearch's implementation, all objects
        // that can't be found will return a negative value, which indicates
        // the position where the object that is being searched is supposed
        // to be minus 1. This if case figures out the position to slot it in
        if (idToInsert < 0) {
            idToInsert = -(idToInsert + 1);
        }
        return idToInsert;
    }

    public String getName() {
        return _name;
    }

    /**
     * Prints the start date to a string for junit
     * 
     * @return String containing the date in DD/MM/YYYY
     */
    public String printStartDate() {
        String returnDate = "";
        if (_startDate != null) {
            returnDate += _startDate.get(Calendar.DAY_OF_MONTH) + "/";
            returnDate += _startDate.get(Calendar.MONTH) + "/";
            returnDate += _startDate.get(Calendar.YEAR);
        }
        return returnDate;
    }

    /**
     * Prints the due date to a string for junit
     * 
     * @return String containing the date in DD/MM/YYYY
     */
    public String printDueDate() {
        String returnDate = "";
        if (_dueDate != null) {
            returnDate += _dueDate.get(Calendar.DAY_OF_MONTH) + "/";
            returnDate += _dueDate.get(Calendar.MONTH) + "/";
            returnDate += _dueDate.get(Calendar.YEAR);
        }
        return returnDate;
    }

    public Calendar getDueDate() {

        if (!_isFloating) {
            return _dueDate;
        } else {
            System.out.println("Trying to get due date from a full day task");
            return null;
        }
    }

    public Calendar getStartDate() {
        if (!_isFloating) {
            return _startDate;
        } else {
            return null;
        }
    }

    public void setDate(Calendar dueDate, boolean isFullDay) {
        _isFloating = false;
        _startDate = null;
        _dueDate = dueDate;
        _isFullDay = isFullDay;
    }

    public void setDate(Calendar startDate, Calendar dueDate, boolean isFullDay) {
        _isFloating = false;
        _startDate = startDate;
        _dueDate = dueDate;
        _isFullDay = isFullDay;
    }

    public Calendar getDateCreated() {
        return _dateCreated;
    }

    public String getDescription() {
        if (_description == null) {
            _description = "";
        }
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

    /**
     * Adds a zero to the string to show time in double digit format
     * 
     * @param digit
     *            - Digit to be padded
     * @return String representation of the time passed in in double digit
     *         format
     */
    private String padZero(int time) {
        String displayedTime = "";
        if (time < 10) {
            displayedTime += "0";
        }
        displayedTime += Integer.toString(time);
        return displayedTime;
    }

    /**
     * Gets time display string for UI to print. Shows time if the duration is
     * within the same day, shows date if the duration crosses to different days
     * 
     * @return 15 character string representing the duration/dueDate of this
     *         task
     */
    public String getTime() {
        if (_isFloating) {
            return null;
        }

        if (_isFullDay) {
            return "[Full Day Task]";
        }

        if (_dueDate == null) {
            return "[Full Day Task]";
        } else if (_startDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm          ");
            return sdf.format(_dueDate.getTime());
        } else {
            return formatTimeDurations();
        }
    }

    /**
     * Formats the string when it has a start and end time. For getTime()
     * function
     * 
     * @return 15 char string containing year if the dates are different years,
     *         only time if they are the same day, and date if they are
     *         different days
     */
    private String formatTimeDurations() {
        String returnDate = "";
        SimpleDateFormat sdf;

        if (_startDate.get(Calendar.YEAR) != _dueDate.get(Calendar.YEAR)) {
            sdf = new SimpleDateFormat("[dd/MM/YY]");

        } else if (TaskUtils.checkSameDate(_startDate, _dueDate)) {
            sdf = new SimpleDateFormat(" HH:mm ");
        } else {
            sdf = new SimpleDateFormat("[dd/MM]");
        }
        returnDate += sdf.format(_startDate.getTime());
        returnDate += "-";
        returnDate += sdf.format(_dueDate.getTime());

        return returnDate;
    }

    public String getHashtags() {
        assert _hashtags != null : "Hash tag was set to null";
        if (_hashtags == null) {
            _hashtags = "";
        }
        return _hashtags;
    }
}