/**
 * @author Qin Ying
 * 
 *         Class to manage the handling of tasks during runtime. Run init()
 *         before using the functions in the API
 */
package mainLogic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.emory.mathcs.backport.java.util.Collections;
import entity.AllTaskLists;
import entity.TaskEntity;
import fileStorage.StorageController;

public class TaskManager {
	private StorageController dataLoader = new StorageController();

	private static TaskManager singleton;
	private Logger logger = Logger.getLogger("TaskManager.log");

	private int currentDisplayedList;
	private static ArrayList<TaskEntity> displayedTasks;
	private static ArrayList<TaskEntity> floatingTaskEntities = new ArrayList<TaskEntity>();
	private static ArrayList<TaskEntity> mainTaskEntities = new ArrayList<TaskEntity>();
    private static ArrayList<TaskEntity> completedTaskEntities = new ArrayList<TaskEntity>();
    
	private static ArrayList<TaskEntity> searchedTasks = new ArrayList<TaskEntity>();

	public final static int DISPLAY_MAIN = 0;
	public final static int DISPLAY_FLOATING = 1;
	public final static int DISPLAY_SEARCH = 2;
	public final static int DISPLAY_COMPLETED = 3;
	public final static int DISPLAY_OTHERS = 4;

	/**
	 * TEST FUNCTION Function for manually testing functions(First
	 * test/debugging) in TaskManager class before regression testing with JUnit
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TaskManager manager = TaskManager.getInstance();
		 manager.unloadFile();
	        
	        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
	        for (int i = 0; i < 9; i++) {
	            Calendar newDate = Calendar.getInstance();
	            newDate.setTimeInMillis(newDate.getTimeInMillis() + i * 3000);
	            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), newDate, false, "some desc"));
	        }
	        for (int i = 0; i < 9; i++) {
	            newList.add(new TaskEntity("Floating Task " + Integer.toString(i + 1)));
	        }
	        
	        manager.add(newList);
	        
	        manager.switchView(manager.DISPLAY_MAIN);
	        manager.getWorkingList().get(0).markAsDone();
	        manager.getWorkingList().get(8).markAsDone();
	        manager.switchView(manager.DISPLAY_FLOATING);
	        manager.getWorkingList().get(0).markAsDone();
	        manager.getWorkingList().get(8).markAsDone();
	        manager.buildCompletedTasks();
	        
	        System.out.println(manager.printArrayContentsToString(manager.DISPLAY_COMPLETED));
	}

	/**
	 * Testing function to print out the array contents
	 */
	private void printList() {
		String output = "";

		System.out.println("Display");
		int j = 0;
		for (int i = 0; i < displayedTasks.size(); i++) {
			System.out.print(Utils.convertDecToBase36(i) + ". " + displayedTasks.get(i).getName() + "     ");
			j++;
			if (j >= 4) {
				System.out.println();
				j = 0;
			}
		}

		System.out.println();
		System.out.println("Floating");
		j = 0;
		for (int i = 0; i < floatingTaskEntities.size(); i++) {
			System.out.print(Utils.convertDecToBase36(i) + ". " + floatingTaskEntities.get(i).getName() + "     ");
			j++;
			if (j >= 4) {
				System.out.println();
				j = 0;
			}
		}

		System.out.println();
		System.out.println("Time based");
		j = 0;
		for (int i = 0; i < mainTaskEntities.size(); i++) {
			System.out.print(Utils.convertDecToBase36(i) + ". " + mainTaskEntities.get(i).getName() + "     ");
			j++;
			if (j >= 4) {
				System.out.println();
				j = 0;
			}
		}
	}

	/**
	 * Prints out all the names of the tasks in the main array
	 * 
	 * @param display
	 *            - default - to print out the array currently in focus
	 *            (inclusive of DISPLAY_OTHERS) - DISPLAY_MAIN - to print out
	 *            the timed tasks array - DISPLAY_FLOATING - to print out the
	 *            floating tasks array - DISPLAY_SEARCH - to print out the
	 *            searched tasks array
	 * 
	 * @return a string containing all the names of the tasks in the arraylist
	 *         printed, seperated by a ", " including at the end of the last
	 *         task printed
	 */
	public String printArrayContentsToString(int display) {
		ArrayList<TaskEntity> arrayToBePrinted;

		if (display == DISPLAY_MAIN) {
			arrayToBePrinted = mainTaskEntities;
		} else if (display == DISPLAY_FLOATING) {
			arrayToBePrinted = floatingTaskEntities;
		} else if (display == DISPLAY_SEARCH) {
			arrayToBePrinted = searchedTasks;
		} else if (display == DISPLAY_COMPLETED) {
		    arrayToBePrinted = completedTaskEntities;
		} else {
			arrayToBePrinted = displayedTasks;
		}

		String arrayContents = "";
		for (int i = 0; i < arrayToBePrinted.size(); i++) {
			arrayContents += arrayToBePrinted.get(i).getName() + ", ";
		}
		return arrayContents;
	}
	
	/**
	 * Function for JUnit test case to print out the display associations of a task
	 * @param taskToBePrinted - Task whose array is to be printed
	 */
	public String printAssociationsToString (TaskEntity taskToBePrinted){
	    String printedAssociations = "";
        ArrayList<TaskEntity> displayedAssociations = taskToBePrinted.getDisplayAssociations();
        
        for (int i = 0; i < displayedAssociations.size(); i++) {
            printedAssociations += displayedAssociations.get(i).getName() + ",";
        }
        
        return printedAssociations;
	}

	/**
	 * Function to clear saved file data from its array. For Junit testing
	 */
	public void unloadFile() {
		floatingTaskEntities.clear();
		mainTaskEntities.clear();
		completedTaskEntities.clear();
		switchView(DISPLAY_MAIN);
	}

	/**
	 * Initialization function to be called before usage of TaskManager class
	 */
	@SuppressWarnings("unchecked")
	private TaskManager() {
		initLogger();

		AllTaskLists taskdata = dataLoader.getTaskLists();
		mainTaskEntities = (ArrayList<TaskEntity>) taskdata.getMainTaskList().clone();
		floatingTaskEntities = (ArrayList<TaskEntity>) taskdata.getFloatingTaskList().clone();

		buildCompletedTasks();
		
		updateTaskEntityCurrentId();

		logger.log(Level.FINEST, "TaskManager Initialized");
		displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();

		initializeAssociations();
		currentDisplayedList = DISPLAY_MAIN;
	}

	public void buildCompletedTasks () {
	    completedTaskEntities = new ArrayList<TaskEntity>();
	    
	    for(int i = 0; i < mainTaskEntities.size(); i++) {
            if ( mainTaskEntities.get(i).isCompleted() ) {
                int positionToAdd = findCompletionPositionToInsert(mainTaskEntities.get(i));
                completedTaskEntities.add(positionToAdd, mainTaskEntities.get(i));
                mainTaskEntities.remove(i);
                i--;
            }
        }
	    
	    for(int i = 0; i < floatingTaskEntities.size(); i++) {
            if ( floatingTaskEntities.get(i).isCompleted() ) {
                int positionToAdd = findCompletionPositionToInsert(floatingTaskEntities.get(i));
                completedTaskEntities.add(positionToAdd, floatingTaskEntities.get(i));
                i--;
            }
        }
	}
	
	/**
	 * Sets the currentId in TaskEntity to be 1 more than the largest ID loaded
	 * so that it there will not be an Id Clash when creating new tasks
	 */
	private void updateTaskEntityCurrentId() {
		for (int i = 0; i < mainTaskEntities.size(); i++) {
			if (mainTaskEntities.get(i).getId() > TaskEntity.getCurrentId()) {
				TaskEntity.setCurrentId(mainTaskEntities.get(i).getId() + 1);
			}
		}

		for (int i = 0; i < floatingTaskEntities.size(); i++) {
			if (floatingTaskEntities.get(i).getId() > TaskEntity.getCurrentId()) {
				TaskEntity.setCurrentId(floatingTaskEntities.get(i).getId() + 1);
			}
		}
	}

	/**
	 * Creates the associations of the tasks based off the string
	 * 
	 * Pre-condition : Assumes id of tasks will not be repeated
	 */
    private void initializeAssociations() {
        for (int i = 0; i < mainTaskEntities.size(); i++) {
            assert mainTaskEntities.get(i)
                    .getSavedAssociations() != null : "Null associations string loaded from file";
            
            mainTaskEntities.get(i).initAssociations();
            String[] associationIdList = mainTaskEntities.get(i).getSavedAssociations().split(",");

            for (int j = 0; j < associationIdList.length; j++) {
                if (!associationIdList[0].equals("")) {
                    int taskToAdd = Integer.parseInt(associationIdList[j]);

                    for (int k = 0; k < mainTaskEntities.size(); k++) {
                        if (taskToAdd == mainTaskEntities.get(k).getId()) {
                            mainTaskEntities.get(i).loadAssociation(mainTaskEntities.get(k));
                            break;
						}
					}

					for (int k = 0; k < floatingTaskEntities.size(); k++) {
						if (taskToAdd == floatingTaskEntities.get(k).getId()) {
							mainTaskEntities.get(i).loadAssociation(floatingTaskEntities.get(k));
							break;
						}
					}
				}
			}
		}

		for (int i = 0; i < floatingTaskEntities.size(); i++) {
		    floatingTaskEntities.get(i).initAssociations();
			String[] associationIdList = floatingTaskEntities.get(i).getSavedAssociations().split(",");

			for (int j = 0; j < associationIdList.length; j++) {
				if (!associationIdList[0].equals("")) {
					int taskToAdd = Integer.parseInt(associationIdList[j]);

					for (int k = 0; k < mainTaskEntities.size(); k++) {
						if (taskToAdd == mainTaskEntities.get(k).getId()) {
							floatingTaskEntities.get(i).loadAssociation(mainTaskEntities.get(k));
							break;
						}
					}

					for (int k = 0; k < floatingTaskEntities.size(); k++) {
						if (taskToAdd == floatingTaskEntities.get(k).getId()) {
							floatingTaskEntities.get(i).loadAssociation(floatingTaskEntities.get(k));
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * function to log error messages into TaskManager
	 * 
	 * @param errorMessage
	 */
	public void logError(String errorMessage) {
		logger.log(Level.SEVERE, errorMessage);
	}

	/**
	 * Function to call for TaskManager before closing the program
	 */
	public void closeTaskManager() {
        ArrayList<TaskEntity> savedMainTaskEntities = new ArrayList<TaskEntity>();
        ArrayList<TaskEntity> savedFloatingTaskEntities = new ArrayList<TaskEntity>();
        
		for (int i = 0; i < mainTaskEntities.size(); i++) {
			TaskEntity clonedTask = mainTaskEntities.get(i).clone();
			clonedTask.buildAssociationsId();
			savedMainTaskEntities.add(clonedTask);
		}

		for (int i = 0; i < floatingTaskEntities.size(); i++) {
		    TaskEntity clonedTask = floatingTaskEntities.get(i).clone();
            clonedTask.buildAssociationsId();
            savedFloatingTaskEntities.add(clonedTask);
		}

		for (int i = 0; i < completedTaskEntities.size(); i++ ) {
		    TaskEntity clonedTask = completedTaskEntities.get(i).clone();
            clonedTask.buildAssociationsId();
            
            if ( clonedTask.isFloating() ) {
                savedFloatingTaskEntities.add(clonedTask);
            } else {
                savedMainTaskEntities.add(clonedTask);
            }
		}
		dataLoader.storeTaskLists(savedMainTaskEntities, savedFloatingTaskEntities);
	}

	/**
	 * Gets the singleton instance of TaskManager
	 * 
	 * @return Singleton instance of TaskManager
	 */
	public static TaskManager getInstance() {
		if (singleton == null) {
			singleton = new TaskManager();
		}
		return singleton;
	}

	/**
	 * Initializes the logger
	 */
	private void initLogger() {
		try {
			Handler fileHandler = new FileHandler("TaskManager.log");
			logger.addHandler(fileHandler);
			logger.setLevel(Level.FINEST);
		} catch (IOException e) {

		}
	}

	/**
	 * Sets focus on a different list
	 * 
	 * @param view
	 *            TaskManager.DISPLAY_MAIN, taskManager.DISPLAY_FLOATING,
	 *            taskManager.DISPLAY_SEARCH taskMAnager.DISPLAY_COMPLETED
	 */
	public void switchView(int view) {
		switch (view) {
		case DISPLAY_MAIN:
			currentDisplayedList = DISPLAY_MAIN;
			displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
			break;
		case DISPLAY_FLOATING:
			currentDisplayedList = DISPLAY_FLOATING;
			displayedTasks = (ArrayList<TaskEntity>) floatingTaskEntities.clone();
			break;
		case DISPLAY_COMPLETED:
	            currentDisplayedList = DISPLAY_COMPLETED;
	            displayedTasks = (ArrayList<TaskEntity>) completedTaskEntities.clone();
	            break;	
		}
	}

	/**
	 * UI Interface function Gets the currently displayed list of tasks
	 * 
	 * @return ArrayList containing the list of time sorted tasks
	 */
	public ArrayList<TaskEntity> getWorkingList() {
		return displayedTasks;
	}

	/**
	 * UI Interface function Modifies the selected task, effectively deleting it
	 * and adding a new task. Base10 version. Will relink all the task's
	 * associations
	 * 
	 * @param index
	 *            - int(in base10) index of task to be modified
	 * @param modifiedTask
	 *            - New data of the task
	 * @return id of new position of the modified task in the display list if
	 *         succeeded in deleting the task returns -1 if after modification,
	 *         the task is no longer in displayedTasks returns -2 if the
	 *         modification failed (Index out of bounds or deletion failed)
	 */
	public int modify(int index, TaskEntity modifiedTask) {
		if (index > displayedTasks.size() - 1) {
			return -2;
		}

		int initialDisplayedArraySize = displayedTasks.size();
		int associationState = displayedTasks.get(index).getAssociationState();
		TaskEntity projectHead = displayedTasks.get(index).getProjectHead();
		ArrayList<TaskEntity> childTasks = displayedTasks.get(index).getAssociations();

		if (delete(index) == false) {
			return -2;
		}

		relinkAssociations(modifiedTask, associationState, projectHead, childTasks);

		int newIndex = add(modifiedTask);
		if (initialDisplayedArraySize == displayedTasks.size()) {
			return newIndex;
		} else {
			return -1;
		}
	}

	/**
	 * Function for modify to link the deleted task's associations onto the new
	 * task its modified to
	 * 
	 * @param modifiedTask
	 *            - new task to replace the delete task
	 * @param associationState
	 *            - Association status of the task deleted
	 * @param projectHead
	 *            - Task that the delete task belonged to if it was associated
	 *            to it
	 * @param childTasks
	 *            - Tasks that is under the deleted task if it is a project head
	 */
	private void relinkAssociations(TaskEntity modifiedTask, int associationState, TaskEntity projectHead,
			ArrayList<TaskEntity> childTasks) {
		if (associationState == TaskEntity.ASSOCIATED) {
			link(projectHead, modifiedTask);
		} else if (associationState == TaskEntity.PROJECT_HEAD) {
			for (int i = 0; i < childTasks.size(); i++) {
				link(modifiedTask, childTasks.get(i));
				;
			}
		}
	}

	/**
	 * UI Interface function Modifies the selected task, effectively deleting it
	 * and adding a new task. Base36 version
	 * 
	 * @param index
	 *            - index(in base36) of task to be modified in string
	 * @param modifiedTask
	 *            - new data of the task
	 * @return id of new position of the modified task in the display list if
	 *         succeeded in deleting the task, returns -1 otherwise
	 */
	public int modify(String index, TaskEntity modifiedTask) {
		return modify(Utils.convertBase36ToDec(index), modifiedTask);
	}

	/**
	 * UI Interface function Insert an arrayList of tasks using the same rules
	 * as adding a single task
	 * 
	 * @param tasks
	 *            - An arrayList of the tasks to be added
	 * @return ID of the first task that was inserted
	 */
	public int add(ArrayList<TaskEntity> tasks) {
		int firstIndex = -1;
		if (tasks.size() >= 1) {
			firstIndex = add(tasks.get(0));
		}

		for (int i = 1; i < tasks.size(); i++) {
			add(tasks.get(i));
		}

		return firstIndex;
	}

	/**
	 * UI Interface function Adds a task into its respective arraylist,
	 * appending to the bottom of floatingTaskEntities if it is a floating task,
	 * and inserting into its sorted position if it is a timed task
	 * 
	 * @param newTask
	 *            - Task to be inserted
	 * @return ID of the task that has been inserted if it is inserted into
	 *         displayedTask -1 if it is inserted into a list that is not in
	 *         view
	 */
	public int add(TaskEntity newTask) {
		assert displayedTasks != null : "no view set in displayedTasks, probably not initialised!";

		if (newTask.isFloating()) {
			floatingTaskEntities.add(newTask);
			return updateFloatingDisplay(newTask);
		} else {
			int idToInsert = findPositionToInsert(newTask);
			mainTaskEntities.add(idToInsert, newTask);
			return updateMainDisplay(newTask, idToInsert);
		}
	}
	
	/**
	 * Marks a task as done
	 * 
	 * @param index - Arrayslot in displayedTasks to be marked as done
	 * @return true - if the task was successfully marked as done
	 *         false - if failed
	 */
    public boolean markAsDone(int index) {
        if ( (index > displayedTasks.size() - 1) || (currentDisplayedList == DISPLAY_COMPLETED) ) {
            return false;
        } else {
            //Checks for deletion failure
            if ( !deleteFromMainList(displayedTasks.get(index)) ) {
                return false;
            }

            displayedTasks.get(index).markAsDone();
            int positionToInsert = findCompletionPositionToInsert(displayedTasks.get(index));
            System.out.println(displayedTasks.get(index).getCompletionDate() + "Insert " + displayedTasks.get(index).getName() + " at " + positionToInsert);
            completedTaskEntities.add(positionToInsert, displayedTasks.get(index));

            markAssociationsUnderComplete(index);
            
            //Remove it from displayedTasks only after processing it
            displayedTasks.remove(index);
            return true;
        }
	}

    private void markAssociationsUnderComplete(int index) {
        if (displayedTasks.get(index).getAssociationState() == TaskEntity.PROJECT_HEAD) {
            
            ArrayList<TaskEntity> associationsToMarkComplete = displayedTasks.get(index).getAssociations();
            
            for(int i = 0; i < associationsToMarkComplete.size(); i++) {
                deleteFromMainList(associationsToMarkComplete.get(i));
                associationsToMarkComplete.get(i).markAsDone();
                int positionToInsert = findCompletionPositionToInsert(associationsToMarkComplete.get(i));
                System.out.println(associationsToMarkComplete.get(i).getCompletionDate() + "Insert " +  associationsToMarkComplete.get(i).getName() + " at " + positionToInsert);
                completedTaskEntities.add(positionToInsert, associationsToMarkComplete.get(i));
                displayedTasks.remove(associationsToMarkComplete.get(i));
            }
            
        }
    }

	private int updateMainDisplay(TaskEntity newTask, int idToInsert) {
		if (currentDisplayedList == DISPLAY_MAIN) {
			displayedTasks.add(idToInsert, newTask);
			return idToInsert;
		} else {
			return -1;
		}
	}

	private int updateFloatingDisplay(TaskEntity newTask) {
		if (currentDisplayedList == DISPLAY_FLOATING) {
			displayedTasks.add(newTask);
			return floatingTaskEntities.size() - 1;
		} else {
			return -1;
		}
	}

	/**
	 * UI Interface function Searches for the position to insert a newTask into
	 * a sorted list of timed tasks using rules dictated in TaskDateComparator
	 * 
	 * @param newTask
	 *            - The task object to be sorted
	 * @return ID of the position where the task should be placed in the sorted
	 *         list
	 */
	private int findPositionToInsert(TaskEntity newTask) {
		int idToInsert = Collections.binarySearch(mainTaskEntities, newTask, new TaskDateComparator());

		// Due to Collections.binarySearch's implementation, all objects
		// that can't be found will return a negative value, which indicates
		// the position where the object that is being searched is supposed
		// to be minus 1. This if case figures out the position to slot it in
		if (idToInsert < 0) {
			idToInsert = -(idToInsert + 1);
		}
		return idToInsert;
	}
	
	/**
     * UI Interface function Searches for the position to insert a newTask into
     * a sorted list of completed tasks by their completion date
     * 
     * @param newTask
     *            - The task object to be sorted
     * @return ID of the position where the task should be placed in the sorted
     *         list
     */
    private int findCompletionPositionToInsert(TaskEntity newTask) {
        int idToInsert = Collections.binarySearch(completedTaskEntities, newTask, new TaskCompletionTimeComparator());

        // Due to Collections.binarySearch's implementation, all objects
        // that can't be found will return a negative value, which indicates
        // the position where the object that is being searched is supposed
        // to be minus 1. This if case figures out the position to slot it in
        if (idToInsert < 0) {
            idToInsert = -(idToInsert + 1);
        }
        return idToInsert;
    }

	/**
	 * UI Interface function Deletion from the displayed list, will delete the
	 * object from the main list in the backend as well, whether it is from the
	 * floating or main list
	 * 
	 * @param index
	 *            - index of the item in the displayed tasks to be deleted
	 * @return false - if fail to delete true - if delete operation succeeded
	 */
	public boolean delete(int index) {
		assert displayedTasks != null : "No list in focus";

		if (displayedTasks == null) {
			displayedTasks = mainTaskEntities;
		}

		if (index + 1 > displayedTasks.size()) {
			return false;
		}

		TaskEntity itemToBeDeleted = displayedTasks.get(index);
		boolean deletionSuccess = deleteFromMainList(itemToBeDeleted);
		if (!deletionSuccess) {
			return false;
		}

		itemToBeDeleted.removeSelfFromProject();

		try {
			displayedTasks.remove(index);
		} catch (ArrayIndexOutOfBoundsException e) {
			logError("Error at delete, removing from index that does not exist");
		}
		return true;
	}

	/**
	 * Removes an object from the list containing all tasks, from its respective
	 * list (floatingTaskEntities if it is a floating task, mainTaskEntities if
	 * its not)
	 * 
	 * @param itemToBeDeleted
	 *            - The task to be deleted from the main list
	 * @return true - if removal operation succeeded false - if removal
	 *         operation failed
	 */
	private boolean deleteFromMainList(TaskEntity itemToBeDeleted) {
		if (currentDisplayedList == DISPLAY_FLOATING) {
			return floatingTaskEntities.remove(itemToBeDeleted);
		} else if(currentDisplayedList == DISPLAY_MAIN) {
        	return mainTaskEntities.remove(itemToBeDeleted);
		} else if(currentDisplayedList == DISPLAY_SEARCH) {
		    return searchedTasks.remove(itemToBeDeleted);
		} else if(currentDisplayedList == DISPLAY_COMPLETED) {
            return mainTaskEntities.remove(itemToBeDeleted);
		} else {
		    return false;
		}
	}

	/**
	 * UI Interface function Deletes a list of tasks from the arrayList
	 * 
	 * @param index
	 *            - A string containing a base36 number
	 * @return - True if delete operation succeeded - False if delete operation
	 *         failed
	 */
	public boolean delete(String index) {
		return delete(Utils.convertBase36ToDec(index));
	}

	/**
	 * UI Interface function Deletes a consecutive list of tasks from both the
	 * working and main arrayList. Upon failing to delete any item, the function
	 * terminates at the object that it fails to delete and does not attempt to
	 * delete anymore items
	 * 
	 * @param startIndex
	 *            - An integer specifying the first index to be deleted.
	 *            startIndex must be <= endIndex
	 * @param endIndex
	 *            - An integer specifying the last index to be deleted. The
	 *            index must not be bigger than the size of the array List
	 * @return - True if delete operation succeeded - False if delete operation
	 *         failed
	 */
	public boolean delete(int startIndex, int endIndex) {
		if (checkValidIndex(startIndex, endIndex) == false) {
			return false;
		}

		for (int i = 0; i <= endIndex - startIndex; i++) {
			if (!delete(startIndex)) {
				return false;
			}
		}
		return true;
	}

    /**
     * Associates a task to a project head task. Project heads are not allowed
     * to be under other tasks (Checked in the first line to prevent the first
     * link from happening) and projects under other project heads are not
     * allowed to be project heads
     * 
     * @param projectHead
     *            - Task to be linked to
     * @param linkedTask
     *            - Task to be linked
     * @return True if success in linking false if failed to link
     */
	public boolean link(TaskEntity projectHead, TaskEntity linkedTask) {
	    
	    if(linkedTask.getAssociationState() == TaskEntity.PROJECT_HEAD) {
	        return false;
	    }
	    
		boolean linkSuccess = projectHead.addAssociation(linkedTask);
		if (!linkSuccess) {
			return false;
		}

		linkedTask.setAssociationHead(projectHead);
		return true;
	}

	/**
	 * Checks if the Index passed in for deletion is a valid index
	 * 
	 * @param startIndex
	 *            - Index of the first item in the range to be deleted
	 * @param endIndex
	 *            - Index of the last item in the range to be deleted
	 * @return true if the index specified exists in the displayedTasks
	 *         ArrayList
	 */
	private boolean checkValidIndex(int startIndex, int endIndex) {
		if (endIndex + 1 > displayedTasks.size()) {
			return false;
		} else if (startIndex > endIndex) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * UI Interface function Deletes a consecutive list of tasks from both the
	 * working and main arrayList. Upon failing to delete any item, the function
	 * terminates at the object that it fails to delete and does not attempt to
	 * delete anymore items
	 * 
	 * @param startIndex
	 *            - A string containing a base36 number that represents the
	 *            index of the first entry of all the entries to be deleted
	 * @param endIndex
	 *            - A string containing a base36 number that represents the
	 *            index of the last entry of all the entries to be deleted
	 * @return - True if delete operation succeeded - False if delete operation
	 *         failed
	 */
	public boolean delete(String startIndex, String endIndex) {
		return delete(Utils.convertBase36ToDec(startIndex), Utils.convertBase36ToDec(endIndex));
	}

	/**
	 * UI Interface function Gets the ID number of the next task in time order
	 * 
	 * @return ID of the task that is next, counting from the current time
	 */
	public int getNextTimeListId() {
		TaskEntity currentTimePlaceholder = new TaskEntity("", Calendar.getInstance(), false);
		int nextTimeId = findPositionToInsert(currentTimePlaceholder);

		// Setting ID to the last index in the list if all tasks comes before
		// current time
		if (nextTimeId > mainTaskEntities.size() - 1) {
			nextTimeId = mainTaskEntities.size() - 1;
		}

		return nextTimeId;
	}

	/**
	 * Gets a random floating task for display at UI
	 * 
	 * @return null - if floatingTaskEntities is empty A TaskEntity object that
	 *         is a random floating task in floatingTaskEntities
	 */
	public TaskEntity getRandomFloating() {
		if (floatingTaskEntities.size() == 0) {
			return null;
		} else {
			Random rand = new Random();
			int randomNum = rand.nextInt(floatingTaskEntities.size());
			return floatingTaskEntities.get(randomNum);
		}
	}

	/**
	 * UI Interface function Undoes the last command performed by the user
	 * 
	 * @return The currently displayed list after the undo operation
	 */
	public ArrayList<TaskEntity> undo() {
		// TODO
		return getWorkingList();
	}

	// ys method, do not remove, ys will remove it ^_^
	// generate fake data.
	public ArrayList<TaskEntity> generateFakeData() {
		int k = 0;
		int day = Calendar.getInstance().get(Calendar.DATE);
		while (k < 200) {
			Random r = new Random();
			int monthBuffer = r.nextInt(12);
			int dateBuffer = r.nextInt(31);
			int hourBuffer = r.nextInt(12);

			Calendar c = Calendar.getInstance();
			c.set(Calendar.MONTH, monthBuffer);
			c.set(Calendar.DATE, dateBuffer);
			c.set(Calendar.HOUR, hourBuffer);

			TaskEntity t = new TaskEntity("Task Name", c, false, "task description");
			add(t);
			k++;
		}

		System.out.println(k + " Fake data created");
		return mainTaskEntities;
	}
}
