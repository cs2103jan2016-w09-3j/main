package entity;

import java.util.ArrayList;
import java.util.Calendar;

import mainLogic.TaskManager;

public class TaskEntity {
    public static final int NOT_ASSOCIATED = 0;
    public static final int ASSOCIATED = 1;
    public static final int PROJECT_HEAD = 2;
    
    private boolean _isFloating;
    private boolean _isFullDay;
    private Calendar _dueDate;
    private Calendar _dateCreated;
    private String _name;
    private String _description;
    private int _id;
    private int _association_status;
    private ArrayList<TaskEntity> _associations;

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
    private void initCommonData() {
        _id = currentID;
        currentID++;
        _dateCreated = Calendar.getInstance();
        _association_status = NOT_ASSOCIATED;
        _associations = new ArrayList<TaskEntity>();
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

    public TaskEntity(String name, Calendar dueDate, boolean isFullDay) {
        initCommonData();

        _name = name;
        _description = "";
        _dueDate = dueDate;
        _isFloating = false;
        _isFullDay = isFullDay;
    }

    public TaskEntity(String name, Calendar dueDate, boolean isFullDay, String description) {
        initCommonData();

        _name = name;
        _description = description;
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

    public int getAssociationState() {
        return _association_status;
    }
    
    public ArrayList<TaskEntity> getAssociations () {
        return _associations;
    }
    
    /**
     * Gets the task representing the project head
     * 
     * @return null if project head cant be found, or if this task is not under
     *         another task
     *         TaskEntity item that is he project head of this object
     */
    public TaskEntity getProjectHead() {
        if (_association_status == NOT_ASSOCIATED || _association_status == PROJECT_HEAD) {
            return null;
        } else {
            try {
                return _associations.get(0);
            } catch (IndexOutOfBoundsException e) {
                TaskManager.getInstance().logError("Error at TaskEntity.java: Associated task has no project head");
                return null;
            }
        }
    }
    
    /**
     * Function to link this object as a task under projectHead
     * 
     * @param projectHead - Task to be added under
     */
    public void setAssociationHead (TaskEntity projectHead) {
        //Unlink from last project first
        removeSelfFromProject();
        
        _associations.clear();
        
        //Tasks under other tasks only have the project head in their associations array
        _associations.add(projectHead);
        _association_status = ASSOCIATED;
    }
    
    public boolean removeSelfFromProject(){
        if(getAssociationState() == TaskEntity.ASSOCIATED) {
            TaskEntity prevProjectHead = getAssociations().get(0);
            prevProjectHead.getAssociations().remove(this);
            return true;
        }else{
            return false;
        }
    }
    /**
     * Function to link an object under this task
     * 
     * @param childTask - Task to be under this task
     * @return true if this task can be a project head
     *         false if this task is under another task and cannot be a project head
     */
    public boolean addAssociation (TaskEntity childTask) {
        if(_association_status == ASSOCIATED) {
            return false;
        } else {
            _association_status = PROJECT_HEAD;
            _associations.add(childTask);
            return true;
        }
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

    public String getTime() {
        if (_isFloating) {
            return null;
        }
        if (_isFullDay) {
            return "Full Day event";
        }
       return  _dueDate.get(Calendar.HOUR) +":"+_dueDate.get(Calendar.MINUTE);
        
    }
}