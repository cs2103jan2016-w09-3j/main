package entity;

import java.util.ArrayList;
import java.util.Calendar;

import edu.emory.mathcs.backport.java.util.Collections;
import mainLogic.TaskDateComparator;
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
<<<<<<< HEAD
	@JsonIgnore
=======
>>>>>>> refs/remotes/origin/master
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

    public TaskEntity(boolean isFloating, boolean isFullDay, Calendar dueDate, Calendar dateCreated,
            String name, String description, int id, int association_status,
            ArrayList<TaskEntity> associations, String associationIDs) {
        _isFloating = isFloating;
        _isFullDay = isFullDay;
        _dueDate = dueDate;
        _dateCreated = dateCreated;
        _name = name;
        _description = description;
        _id = id;
        _association_status = association_status;
        _associations = associations;
        _associationIDs = associationIDs;
    }
    
	public int getAssociationState() {
		return _association_status;
	}
<<<<<<< HEAD

	@JsonIgnore
=======
	
>>>>>>> refs/remotes/origin/master
	public ArrayList<TaskEntity> getAssociations() {
		return _associations;
	}
	
	public void initAssociations() {
	    _associations = new ArrayList<TaskEntity>();
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
		
		assert _associations != null : "Associations is null at build associations!";
		
		for (int i = 0; i < _associations.size(); i++) {
			_associationIDs += Integer.toString(_associations.get(i).getId()) + ",";
		}
		_associations = null;
	}
	
    public TaskEntity clone() {
        TaskEntity newInstance = new TaskEntity(_isFloating, _isFullDay, _dueDate, _dateCreated, _name,
                _description, _id, _association_status, _associations, _associationIDs);
        return newInstance;
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
				TaskManager.getInstance().logError("Error at TaskEntity.java: Associated task has no project head");
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
	    if(_association_status == PROJECT_HEAD) {
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

	public boolean removeSelfFromProject() {
		if (getAssociationState() == TaskEntity.ASSOCIATED) {
			TaskEntity prevProjectHead = getAssociations().get(0);
			prevProjectHead.getAssociations().remove(this);
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
	
	public ArrayList<TaskEntity> getDisplayAssociations () {
	    if( _association_status == PROJECT_HEAD ) {
	        return _associations;
	    } else if ( _association_status == ASSOCIATED ) {
	        return getProjectHead().getAssociations();
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
		return _dueDate.get(Calendar.HOUR_OF_DAY) + ":" + _dueDate.get(Calendar.MINUTE);
	}
}