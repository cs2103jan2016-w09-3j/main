/**
 * @author qy
 * @@author a0125493a
 * 
 *          Class to manage the handling of tasks during runtime. Singleton
 *          class, use GetInstance() to use this class
 */
package mainLogic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.emory.mathcs.backport.java.util.Collections;
import entity.AllTaskLists;
import entity.ResultSet;
import entity.TaskEntity;
import fileStorage.StorageInterface;

public class TaskManager {
    private StorageInterface dataLoader;

    private static TaskManager singleton;
    private Logger logger;

    private int currentDisplayedList;
    private static ArrayList<TaskEntity> displayedTasks;
    private static ArrayList<TaskEntity> floatingTaskEntities = new ArrayList<TaskEntity>();
    private static ArrayList<TaskEntity> mainTaskEntities = new ArrayList<TaskEntity>();
    private static ArrayList<TaskEntity> completedTaskEntities = new ArrayList<TaskEntity>();

    private static ArrayList<String> undoList = new ArrayList<String>();
    private int undoPointer = -1;
    private boolean _undoing = false;

    private static ArrayList<TaskEntity> searchedTasks = new ArrayList<TaskEntity>();

    public final static int DISPLAY_MAIN = 0;
    public final static int DISPLAY_FLOATING = 1;
    public final static int DISPLAY_SEARCH = 2;
    public final static int DISPLAY_COMPLETED = 3;
    public final static int DISPLAY_OTHERS = 4;

    private boolean isJsonSuccess = true;

    /**
     * Function for loadFrom to reset the undo stack upon loading other file
     */
    private void resetUndo() {
        undoList = new ArrayList<String>();
        undoPointer = -1;
        _undoing = false;
    }

    /**
     * Startup function to call and check if there was corrupted data read by
     * storage. Data passed up from storage
     * 
     * @return false if data was corrupted
     *         true if data was successfully loaded
     */
    public boolean checkLoad() {
        return isJsonSuccess;
    }

    /**
     * Prints out all the names of the tasks in the main array
     * 
     * @param display
     *            - default - to print out the array currently in focus
     *            (inclusive of DISPLAY_OTHERS)
     *            - DISPLAY_MAIN - to print out the timed tasks array
     *            - DISPLAY_FLOATING - to print out the floating tasks array
     *            - DISPLAY_SEARCH - to print out the searched tasks array
     * 
     * @return a string containing all the names of the tasks in the ArrayList
     *         printed, separated by a ", " including at the end of the last
     *         task printed
     */
    public String printArrayContentsToString(int display) {
        ArrayList<TaskEntity> arrayToBePrinted = chooseArray(display);
        String arrayContents = appendTaskNameToString(arrayToBePrinted);
        return arrayContents;
    }

    /**
     * Each name in the ArrayList is appended with ", " at the back of it and
     * all added to one return string in order
     * 
     * @param arrayToBePrinted
     * @return Every name in the ArrayList appended with ", " at the back of it
     *         and all concatenated together
     */
    private String appendTaskNameToString(ArrayList<TaskEntity> arrayToBePrinted) {
        String arrayContents = "";
        for (int i = 0; i < arrayToBePrinted.size(); i++) {
            arrayContents += arrayToBePrinted.get(i).getName() + ", ";
        }
        return arrayContents;
    }

    private ArrayList<TaskEntity> chooseArray(int display) {
        if (display == DISPLAY_MAIN) {
            return mainTaskEntities;
        } else if (display == DISPLAY_FLOATING) {
            return floatingTaskEntities;
        } else if (display == DISPLAY_SEARCH) {
            return searchedTasks;
        } else if (display == DISPLAY_COMPLETED) {
            return completedTaskEntities;
        } else {
            return displayedTasks;
        }
    }

    /**
     * Function for JUnit test case to print out the display associations of a
     * task
     * 
     * @param taskToBePrinted - Task whose array is to be printed
     */
    public String printAssociationsToString(TaskEntity taskToBePrinted) {
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

        dataLoader = new StorageInterface();
        AllTaskLists taskdata = dataLoader.getTaskLists();

        // Json load failure
        if (taskdata == null) {
            mainTaskEntities = new ArrayList<TaskEntity>();
            floatingTaskEntities = new ArrayList<TaskEntity>();
            isJsonSuccess = false;
        } else {
            mainTaskEntities = (ArrayList<TaskEntity>) taskdata.getMainTaskList().clone();
            floatingTaskEntities = (ArrayList<TaskEntity>) taskdata.getFloatingTaskList().clone();

            initializeAssociations();

            updateTaskEntityCurrentId();
            buildCompletedTasks();
        }
        displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
        currentDisplayedList = DISPLAY_MAIN;

    }

    /**
     * For loading of files. Extracting completed tasks from main and floating
     * task lists
     */
    public void buildCompletedTasks() {
        completedTaskEntities = new ArrayList<TaskEntity>();

        buildCompletedMainTasks();
        buildCompletedFloatingTasks();
    }

    private void buildCompletedFloatingTasks() {
        for (int i = 0; i < floatingTaskEntities.size(); i++) {
            if (floatingTaskEntities.get(i).isCompleted()) {
                int positionToAdd = findCompletionPositionToInsert(floatingTaskEntities.get(i));
                completedTaskEntities.add(positionToAdd, floatingTaskEntities.get(i));
                floatingTaskEntities.remove(i);
                i--;
            }
        }
    }

    private void buildCompletedMainTasks() {
        for (int i = 0; i < mainTaskEntities.size(); i++) {
            if (mainTaskEntities.get(i).isCompleted()) {
                int positionToAdd = findCompletionPositionToInsert(mainTaskEntities.get(i));
                completedTaskEntities.add(positionToAdd, mainTaskEntities.get(i));
                mainTaskEntities.remove(i);
                i--;
            }
        }
    }

    /**
     * Sets the currentId in TaskEntity to be 1 more than the largest ID loaded
     * so that it there will not be an Id Clash when creating new tasks
     */
    private void updateTaskEntityCurrentId() {
        checkThroughMain();
        checkThroughFloating();
    }

    /**
     * Function for updating TaskEntity's current ID
     */
    private void checkThroughFloating() {
        for (int i = 0; i < floatingTaskEntities.size(); i++) {
            if (floatingTaskEntities.get(i).getId() > TaskEntity.getCurrentId()) {
                TaskEntity.setCurrentId(floatingTaskEntities.get(i).getId() + 1);
            }
        }
    }

    /**
     * Function for updating TaskEntity's current ID
     */
    private void checkThroughMain() {
        for (int i = 0; i < mainTaskEntities.size(); i++) {
            if (mainTaskEntities.get(i).getId() > TaskEntity.getCurrentId()) {
                TaskEntity.setCurrentId(mainTaskEntities.get(i).getId() + 1);
            }
        }
    }

    /**
     * Creates the associations of the tasks based off the string
     * This function has to be called before building completed tasks
     * 
     * Pre-condition : Assumes id of tasks will not be repeated
     */
    private void initializeAssociations() {
        initializeAssociations(mainTaskEntities);
        initializeAssociations(floatingTaskEntities);
    }

    private void initializeAssociations(ArrayList<TaskEntity> arrayToInit) {
        for (int i = 0; i < arrayToInit.size(); i++) {
            arrayToInit.get(i).initAssociations();

            assert arrayToInit.get(i)
                    .getSavedAssociations() != null : "Null associations string loaded from file for task: "
                            + arrayToInit.get(i).getName();

            String[] associationIdList = arrayToInit.get(i).getSavedAssociations().split(",");

            if (!associationIdList[0].equals("")) {
                for (int j = 0; j < associationIdList.length; j++) {
                    int taskIDToAdd = Integer.parseInt(associationIdList[j]);

                    loadAssociationFromID(arrayToInit.get(i), taskIDToAdd);
                }
            }
        }
    }

    /**
     * Searches the 2 arraylist mainTaskEntities and floatingTaskEntities for an
     * object with the ID passed in and links the found task to the tasktoloadto
     * For initializeAssociations function.
     * 
     * Pre-condition : Assumes no duplicate ID
     * 
     * @param taskToLoadTo - Task that the found task is linked to
     * @param taskIDToAdd - ID of the task to be found
     */
    private void loadAssociationFromID(TaskEntity taskToLoadTo, int taskIDToAdd) {
        for (int k = 0; k < mainTaskEntities.size(); k++) {
            if (taskIDToAdd == mainTaskEntities.get(k).getId()) {
                taskToLoadTo.loadAssociation(mainTaskEntities.get(k));
                break;
            }
        }

        for (int k = 0; k < floatingTaskEntities.size(); k++) {
            if (taskIDToAdd == floatingTaskEntities.get(k).getId()) {
                taskToLoadTo.loadAssociation(floatingTaskEntities.get(k));
                break;
            }
        }
    }

    /**
     * Returns a list of raw command strings to run in the event of a crash. If
     * there was no crash, this queue is expected to be empty
     * 
     * @return all commands to be re-run before start of program
     */
    public Queue<String> getBackedupCommands() {
        Queue<String> reloadedCommands = dataLoader.getCommandsQueue();
        dataLoader.clearCommandFile();
        // Return cloned copy as same arraylist will cause infinite loop
        return new LinkedList(reloadedCommands);
    }

    /**
     * Calls storage to save each command ran. Auto commits when list is full
     * 
     * @param command - Raw command (the one that the user types) to be passed
     *            down
     */
    public void saveBackupCommand(String command) {
        // For crash
        boolean requiresFullSave = dataLoader.saveUponFullQueue(command);
        if (requiresFullSave) {
            commitFullSave();
        }

        updateUndoStack(command);
    }

    /**
     * Updates the list of commands to run for undo
     * 
     * @param command - Command to add for undo
     */
    private void updateUndoStack(String command) {
        if (!_undoing) {
            if (undoPointer == undoList.size() - 1) {
                undoList.add(command);
                undoPointer++;
            } else {
                trimAdditionalCommands(command);
            }
            System.out.println("Undo pointer updated to " + undoPointer);
        } else {
            System.out.println("running an undo command");
        }
    }

    /**
     * For use in undo to trim off additional commands after entering a command
     * when undone, then adding the new undo command in
     * 
     * @param command - command to save to undo
     */
    private void trimAdditionalCommands(String command) {
        if (undoPointer == -1) {
            undoList = new ArrayList<String>();
        } else {
            undoList = new ArrayList<String>(undoList.subList(0, undoPointer + 1));
        }
        undoList.add(command);
        undoPointer++;
    }

    /**
     * function to log error messages into TaskManager
     * 
     * @param errorMessage
     */
    public void logError(String errorMessage) {
        if (logger == null) {
            initLogger();
        }
        logger.log(Level.SEVERE, "ERROR: " + errorMessage);
    }

    /**
     * Checks if an Id has shifted out of the end of the display list
     * 
     * @param currentId - Id to be checked
     * @return currentId if it is not out of range
     *         last ID in the display list if the checked ID is out of range
     *         -1 if the display list is empty
     */
    public int checkCurrentId(int currentId) {
        if (currentId >= displayedTasks.size()) {
            return displayedTasks.size() - 1;
        } else {
            return currentId;
        }
    }

    /**
     * Function to call for TaskManager before closing the program and whenever
     * its doing a full save. Calls storage for saving
     */
    public void commitFullSave() {
        AllTaskLists newList = generateSavedTaskArray();

        dataLoader.storeTaskLists(newList);
        dataLoader.clearCommandFileOnCommit();
    }

    /**
     * Creates a copy of the existing task lists, adding completed tasks into
     * the mainTaskEntities and floatingTaskEntities and building their string
     * list of IDs for associations
     * 
     * @return Object for saving all the tasks for Storage
     */
    public AllTaskLists generateSavedTaskArray() {
        ArrayList<TaskEntity> savedMainTaskEntities = new ArrayList<TaskEntity>();
        ArrayList<TaskEntity> savedFloatingTaskEntities = new ArrayList<TaskEntity>();

        buildMainSaveTasks(savedMainTaskEntities);
        buildFloatingSaveTasks(savedFloatingTaskEntities);
        buildCompletedSaveTasks(savedMainTaskEntities, savedFloatingTaskEntities);

        AllTaskLists newList = new AllTaskLists();
        newList.setFloatingTaskList(savedFloatingTaskEntities);
        newList.setMainTaskList(savedMainTaskEntities);

        return newList;
    }

    /**
     * Function for generateSavedTaskArray
     * 
     * @param savedMainTaskEntities - Array to put generated saved task into
     */
    private void buildMainSaveTasks(ArrayList<TaskEntity> savedMainTaskEntities) {
        for (int i = 0; i < mainTaskEntities.size(); i++) {
            TaskEntity clonedTask = mainTaskEntities.get(i).clone();
            clonedTask.buildAssociationsId();
            savedMainTaskEntities.add(clonedTask);
        }
    }

    /**
     * Function for generateSavedTaskArray
     * 
     * @param savedFloatingTaskEntities - Array to put generated saved task into
     */
    private void buildFloatingSaveTasks(ArrayList<TaskEntity> savedFloatingTaskEntities) {
        for (int i = 0; i < floatingTaskEntities.size(); i++) {
            TaskEntity clonedTask = floatingTaskEntities.get(i).clone();
            clonedTask.buildAssociationsId();
            savedFloatingTaskEntities.add(clonedTask);
        }
    }

    /**
     * Function for generateSavedTaskArray
     * 
     * @param savedMainTaskEntities - Array to put generated saved task into
     * @param savedFloatingTaskEntities - Array to put generated saved task into
     */
    private void buildCompletedSaveTasks(ArrayList<TaskEntity> savedMainTaskEntities,
            ArrayList<TaskEntity> savedFloatingTaskEntities) {
        for (int i = 0; i < completedTaskEntities.size(); i++) {
            TaskEntity clonedTask = completedTaskEntities.get(i).clone();
            clonedTask.buildAssociationsId();

            if (clonedTask.isFloating()) {
                savedFloatingTaskEntities.add(clonedTask);
            } else {
                savedMainTaskEntities.add(clonedTask);
            }
        }
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
        logger = Logger.getLogger("TaskManager.log");
        try {
            Handler fileHandler = new FileHandler("TaskManager.log");
            logger.addHandler(fileHandler);
            logger.setLevel(Level.FINEST);
        } catch (IOException e) {
            e.printStackTrace();
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
            case DISPLAY_MAIN :
                currentDisplayedList = DISPLAY_MAIN;
                displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
                break;
            case DISPLAY_FLOATING :
                currentDisplayedList = DISPLAY_FLOATING;
                displayedTasks = (ArrayList<TaskEntity>) floatingTaskEntities.clone();
                break;
            case DISPLAY_COMPLETED :
                currentDisplayedList = DISPLAY_COMPLETED;
                displayedTasks = (ArrayList<TaskEntity>) completedTaskEntities.clone();
                break;
            case DISPLAY_SEARCH :
                currentDisplayedList = DISPLAY_SEARCH;
                displayedTasks = (ArrayList<TaskEntity>) searchedTasks.clone();
                break;
        }
    }

    public int getView() {
        return currentDisplayedList;
    }

    /**
     * UI Interface function Gets the currently displayed list of tasks
     * 
     * @return ArrayList containing the list of the task in focus
     */
    public ArrayList<TaskEntity> getWorkingList() {
        return displayedTasks;
    }

    /**
     * UI Interface function Modifies the selected task, effectively deleting it
     * and adding a new task. Will relink all the task's associations
     * 
     * @param index - int index of task to be modified
     * @param modifiedTask - New data of the task
     * @return ResultSet where
     *         ResultSet.getStatus
     *         : ResultSet.STATUS_CONFLICT if replacement task is conflicting
     *         with another task(excluding full day)
     *         : ResultSet.STATUS_PAST if replacement task is added to a date
     *         before current time
     *         : ResultSet.STATUS_CONFLICT_AND_PAST if both conditions above
     *         fulfilled
     *         : ResultSet.STATUS_GOOD if otherwise and successful
     *         : ResultSet.STATUS_BAD if operation failed to add
     *         : ResultSet.STATUS_INVALID_DATE if the start time of the
     *         modifiedTask
     *         is before the dueDate of it
     * 
     *         ResultSet.isSuccess
     *         :is false if INVALID_DATE OR Array to add to is null OR invalid
     *         name
     *         for the modififedTask like "     " OR failure to delete the task
     *         specified by taskId
     *         :is true otherwise
     * 
     *         ResultSet.getIndex
     *         : ID of the task to jump to (newly added task's ID)
     *         : -1 if its added to other views
     */
    public ResultSet modify(int index, TaskEntity modifiedTask) {
        ResultSet modificationResults = new ResultSet();

        // Temporarily set results to success
        modificationResults.setSuccess();
        modificationResults = checkModifyFailure(index, modifiedTask, modificationResults);
        if (!modificationResults.isSuccess()) {
            return modificationResults;
        }

        int associationState = displayedTasks.get(index).getAssociationState();
        TaskEntity projectHead = displayedTasks.get(index).getProjectHead();
        ArrayList<TaskEntity> childTasks = displayedTasks.get(index).getAssociations();

        if (delete(index) == false) {
            modificationResults.setStatus(ResultSet.STATUS_BAD);
            modificationResults.setFail();
            return modificationResults;
        }

        relinkAssociations(modifiedTask, associationState, projectHead, childTasks);

        modificationResults = add(modifiedTask);
        return modificationResults;
    }

    /**
     * Checks for the conditions where modification is not allowed
     * 
     * @param index - Id in the focused list of the task to be replaced
     * @param modifiedTask - Task to replace the edited task
     * @param modificationResults - Temporary ResultSet to set a failure if
     *            failure conditions are detected
     * @return Original ResultSet passed in if no failure conditions detected
     *         ResultSet containing failure conditions otherwise:
     *         ResultSet.getStatus() is set to STATUS_INVALID_NAME if no valid
     *         name is passed in, STATUS_BAD otherwise (Both are failure
     *         conditions)
     */
    private ResultSet checkModifyFailure(int index, TaskEntity modifiedTask, ResultSet modificationResults) {
        if (index > displayedTasks.size() - 1 || !TaskUtils.checkValidName(modifiedTask)) {
            if (!TaskUtils.checkValidName(modifiedTask)) {
                modificationResults.setStatus(ResultSet.STATUS_INVALID_NAME);
            } else {
                modificationResults.setStatus(ResultSet.STATUS_BAD);
            }
            modificationResults.setFail();
            return modificationResults;
        }

        // Check for invalid date ranges
        if (modifiedTask.getStartDate() != null && modifiedTask.getDueDate() != null) {
            if (modifiedTask.getStartDate().compareTo(modifiedTask.getDueDate()) > 0) {
                modificationResults.setStatus(ResultSet.STATUS_INVALID_DATE);
                modificationResults.setFail();
                return modificationResults;
            }
        }

        return modificationResults;
    }

    /**
     * Function for modify to link the deleted task's associations onto the new
     * task its modified to
     * 
     * @param modifiedTask -new task to replace the delete task
     * @param associationState- Association status of the task deleted
     * @param projectHead - Task that the delete task belonged to if it was
     *            associated to it
     * @param childTasks - Tasks that is under the deleted task if it is a
     *            project head
     */
    private void relinkAssociations(TaskEntity modifiedTask, int associationState, TaskEntity projectHead,
            ArrayList<TaskEntity> childTasks) {
        if (associationState == TaskEntity.ASSOCIATED) {
            link(projectHead, modifiedTask);
        } else if (associationState == TaskEntity.PROJECT_HEAD) {
            for (int i = 0; i < childTasks.size(); i++) {
                link(modifiedTask, childTasks.get(i));
            }
        }
    }

    /**
     * UI Interface function Modifies the selected task, effectively deleting it
     * and adding a new task. Will relink all the task's
     * 
     * @param index - int index of task to be modified represented by a string
     * @param modifiedTask - New data of the task
     * @return ResultSet where
     *         ResultSet.getStatus
     *         : ResultSet.STATUS_CONFLICT if replacement task is conflicting
     *         with another task(excluding full day)
     *         : ResultSet.STATUS_PAST if replacement task is added to a date
     *         before current time
     *         : ResultSet.STATUS_CONFLICT_AND_PAST if both conditions above
     *         fulfilled
     *         : ResultSet.STATUS_GOOD if otherwise and successful
     *         : ResultSet.STATUS_BAD if operation failed to add
     *         : ResultSet.STATUS_INVALID_DATE if the start time of the
     *         modifiedTask
     *         is before the dueDate of it
     * 
     *         ResultSet.isSuccess
     *         :is false if INVALID_DATE OR Array to add to is null OR invalid
     *         name
     *         for the modififedTask like "     " OR failure to delete the task
     *         specified by taskId
     *         :is true otherwise
     * 
     *         ResultSet.getIndex
     *         : ID of the task to jump to (newly added task's ID)
     *         : -1 if its added to other views
     */
    public ResultSet modify(String index, TaskEntity modifiedTask) {
        return modify(TaskUtils.convertStringToInteger(index), modifiedTask);
    }

    /**
     * Function to add multiple tasks. For test cases
     * 
     * @param tasks - An arrayList of the tasks to be added
     * @return ResultSet data from add of the first item added
     */
    public ResultSet add(ArrayList<TaskEntity> tasks) {
        ResultSet batchAddResults = new ResultSet();
        if (tasks.size() >= 1) {
            batchAddResults = add(tasks.get(0));
        }

        if (!batchAddResults.isSuccess()) {
            return batchAddResults;
        }

        for (int i = 1; i < tasks.size(); i++) {
            ResultSet currentAddResults = add(tasks.get(i));
            if (!currentAddResults.isSuccess()) {
                batchAddResults.setFail();
                return batchAddResults;
            }
        }

        return batchAddResults;
    }

    /**
     * UI Interface function Adds a task into its respective arraylist,
     * appending to the bottom of floatingTaskEntities if it is a floating task,
     * and inserting into its sorted position if it is a timed task
     * 
     * @param newTask - Task to be inserted
     * @return Resultset where
     *         ResultSet.getStatus
     *         : ResultSet.STATUS_CONFLICT if conflicting with another task
     *         (excluding full day)
     *         : ResultSet.STATUS_PAST if added to a date before current time
     *         : ResultSet.STATUS_CONFLICT_AND_PAST if both conditions above
     *         fulfilled
     *         : ResultSet.STATUS_GOOD if otherwise and successful
     *         : ResultSet.STATUS_BAD if operation failed to add
     *         : ResultSet.STATUS_INVALID_DATE if the start time of the newTask
     *         is before the dueDate of it
     * 
     *         ResultSet.isSuccess
     *         :false if INVALID_DATE OR Array to add to is null OR invalid name
     *         like "     "
     *         : success otherwise
     * 
     *         ResultSet.getIndex
     *         : ID of the task to jump to (newly added task's ID)
     *         : -1 if its added to other views
     */
    public ResultSet add(TaskEntity newTask) {
        assert displayedTasks != null : "no view set in displayedTasks, probably not initialised!";

        ResultSet addResults = new ResultSet();

        // Temporarily set success to check for failure
        addResults.setSuccess();
        addResults = checkAddFailure(newTask, addResults);
        if (!addResults.isSuccess()) {
            return addResults;
        }

        if (newTask.isFloating()) {
            return addFloatingTask(newTask);
        } else {
            return addMainTask(newTask);
        }
    }

    private ResultSet checkAddFailure(TaskEntity newTask, ResultSet addResults) {
        if (displayedTasks == null || !TaskUtils.checkValidName(newTask)) {
            if (!TaskUtils.checkValidName(newTask)) {
                addResults.setStatus(ResultSet.STATUS_INVALID_NAME);
            } else {
                addResults.setStatus(ResultSet.STATUS_BAD);
            }
            addResults.setFail();
            return addResults;
        }

        // Check for invalid date ranges
        if (newTask.getStartDate() != null && newTask.getDueDate() != null) {
            if (newTask.getStartDate().compareTo(newTask.getDueDate()) > 0) {
                addResults.setStatus(ResultSet.STATUS_INVALID_DATE);
                addResults.setFail();
                return addResults;
            }
        }

        // Return the results as it is if no failure detected
        return addResults;
    }

    /**
     * Adds the task into mainTaskEntities. For add function
     * 
     * @param newTask - Task to be added
     * @return ResultSet containing results matching the details in
     *         add(TaskEntity)
     */
    private ResultSet addMainTask(TaskEntity newTask) {
        ResultSet addResults = new ResultSet();

        if (mainTaskEntities == null) {
            addResults.setStatus(ResultSet.STATUS_BAD);
            addResults.setFail();
            return addResults;
        }

        if (checkClashing(newTask)) {
            addResults.setStatus(ResultSet.STATUS_CONFLICT);
        }

        if (TaskUtils.isDateBeforeNow(newTask.getDueDate())) {
            addResults.setStatus(ResultSet.STATUS_PAST);
        }

        return setResultAndInsertMain(newTask, addResults);
    }

    /**
     * Sets the fields for ResultSet and insert the main task into
     * mainTaskEntities
     * 
     * @param newTask - Task to be added
     * @return ResultSet containing results matching the details in
     *         add(TaskEntity)
     */
    private ResultSet setResultAndInsertMain(TaskEntity newTask, ResultSet addResults) {
        addResults.setView(ResultSet.TASK_VIEW);
        int idToInsert = findPositionToInsert(newTask);
        mainTaskEntities.add(idToInsert, newTask);
        System.out.println(mainTaskEntities.size());
        addResults.setIndex(updateMainDisplay(newTask, idToInsert));

        addResults.setStatus(ResultSet.STATUS_GOOD);
        addResults.setSuccess();
        System.out.println("status of add: " + addResults.getStatus());
        return addResults;
    }

    /**
     * Adds the task into floatingTaskEntities. For add function
     * 
     * @param newTask - Task to be added
     * @return ResultSet containing results matching the details in
     *         add(TaskEntity)
     */
    private ResultSet addFloatingTask(TaskEntity newTask) {
        ResultSet addResults = new ResultSet();

        if (floatingTaskEntities == null) {
            addResults.setStatus(ResultSet.STATUS_BAD);
            addResults.setFail();
            return addResults;
        }

        return setResultAndInsertFloating(newTask, addResults);
    }

    /**
     * Sets the fields for ResultSet and insert the floating task into
     * floatingTaskEntities
     * 
     * @param newTask - Task to be added
     * @return ResultSet containing results matching the details in
     *         add(TaskEntity)
     */
    private ResultSet setResultAndInsertFloating(TaskEntity newTask, ResultSet addResults) {
        boolean addSuccess = floatingTaskEntities.add(newTask);
        assert addSuccess == true : "Failed to add to non-null floatingTaskEntities list";
        System.out.println(floatingTaskEntities.size());

        addResults.setView(ResultSet.FLOATING_VIEW);
        addResults.setIndex(updateFloatingDisplay(newTask));
        addResults.setSuccess();
        addResults.setStatus(ResultSet.STATUS_GOOD);
        return addResults;
    }

    /**
     * Marks a task as done
     * 
     * @param index - Array slot in displayedTasks to be marked as done
     *            * @return true and STATUS_GOOD for success, false and
     *            STATUS_BAD for
     *            failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *            respectively
     */
    public ResultSet markAsDone(int index) {
        ResultSet markingResults = new ResultSet();

        if ((index > displayedTasks.size() - 1) || (currentDisplayedList == DISPLAY_COMPLETED) || index < 0) {
            markingResults.setFail();
            markingResults.setStatus(ResultSet.STATUS_BAD);
            return markingResults;
        } else {
            // Checks for deletion failure
            if (!deleteFromCorrespondingDisplayList(displayedTasks.get(index))) {
                markingResults.setFail();
                markingResults.setStatus(ResultSet.STATUS_BAD);
                return markingResults;
            }

            return executeMarkTaskDone(index, markingResults);
        }
    }

    /**
     * Marks a task done. Pre-condition: All failure conditions have been
     * cleared. Called by markAsDone(int)
     * 
     * @param index - Array slot in displayedTasks to be marked as done
     *            * @return true and STATUS_GOOD for success in ResultSet
     */
    private ResultSet executeMarkTaskDone(int index, ResultSet markingResults) {
        displayedTasks.get(index).markAsDone();
        int positionToInsert = findCompletionPositionToInsert(displayedTasks.get(index));
        completedTaskEntities.add(positionToInsert, displayedTasks.get(index));

        // Remove it from displayedTasks only after processing it
        displayedTasks.remove(index);
        markingResults.setSuccess();
        markingResults.setStatus(ResultSet.STATUS_GOOD);
        return markingResults;
    }

    /**
     * Adds the task into displayed tasks too if its currently at main display.
     * For add function
     * 
     * @param newTask - Task to add
     * @param idToInsert - position to insert the task at
     * @return -1 if the focus is not on the main display
     *         ID of the position the task was added at otherwise
     */
    private int updateMainDisplay(TaskEntity newTask, int idToInsert) {
        if (currentDisplayedList == DISPLAY_MAIN) {
            displayedTasks.add(idToInsert, newTask);
            return idToInsert;
        } else {
            return -1;
        }
    }

    /**
     * Adds the task into displayed tasks too if its currently at floating
     * display. For add function
     * 
     * @param newTask - Task to add
     * @return -1 if the focus is not on the floating display
     *         ID of the position the task was added at otherwise
     */
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
     * @param newTask - The task object to be sorted
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
     * @param newTask - The task object to be sorted
     * @return ID of the position where the task should be placed in the sorted
     *         list
     */
    private int findCompletionPositionToInsert(TaskEntity newTask) {
        int idToInsert = Collections.binarySearch(completedTaskEntities, newTask,
                new TaskCompletionTimeComparator());

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
     * Deletion from the displayed list, will delete the object from the main
     * list in the backend's corresponding list as well
     * 
     * @param index- index of the item in the displayed tasks to be deleted
     * @return false - if fail to delete, true - if delete operation succeeded
     */
    public boolean delete(int index) {
        assert displayedTasks != null : "No list in focus when attempting to delete a task";

        if (displayedTasks == null) {
            logError("Attempted deletion with no list in focus");
            displayedTasks = mainTaskEntities;
        }

        if (index + 1 > displayedTasks.size()) {
            return false;
        }

        TaskEntity itemToBeDeleted = displayedTasks.get(index);
        boolean deletionSuccess = deleteFromCorrespondingDisplayList(itemToBeDeleted);
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
     * Removes an object from the corresponding list that is being displayed
     * 
     * @param itemToBeDeleted
     *            - The task to be deleted from the main list
     * @return true - if removal operation succeeded false - if removal
     *         operation failed
     */
    private boolean deleteFromCorrespondingDisplayList(TaskEntity itemToBeDeleted) {
        if (currentDisplayedList == DISPLAY_FLOATING) {
            return floatingTaskEntities.remove(itemToBeDeleted);
        } else if (currentDisplayedList == DISPLAY_MAIN) {
            return mainTaskEntities.remove(itemToBeDeleted);
        } else if (currentDisplayedList == DISPLAY_SEARCH) {
            // Removes the object from its main list too
            if (!deleteFromMainList(itemToBeDeleted)) {
                return false;
            }
            return searchedTasks.remove(itemToBeDeleted);
        } else if (currentDisplayedList == DISPLAY_COMPLETED) {
            return mainTaskEntities.remove(itemToBeDeleted);
        } else {
            return false;
        }
    }

    /**
     * Removes an object from the main 3 list of floating/main/completed
     * 
     * @param itemToBeDeleted - Item that is being searched for to be removed
     * @return success of the deletion. True if succeeded in deleting
     */
    private boolean deleteFromMainList(TaskEntity itemToBeDeleted) {
        if (itemToBeDeleted.isCompleted()) {
            if (!completedTaskEntities.remove(itemToBeDeleted)) {
                return false;
            }
        } else {
            if (itemToBeDeleted.isFloating()) {
                if (!floatingTaskEntities.remove(itemToBeDeleted)) {
                    return false;
                }
            } else {
                if (!mainTaskEntities.remove(itemToBeDeleted)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deletion from the displayed list, will delete the object from the main
     * list in the backend's corresponding list as well
     * 
     * @param index- index of the item in the displayed tasks to be deleted.
     *            String representation of an int
     * @return false - if fail to delete, true - if delete operation succeeded
     */
    public boolean delete(String index) {
        return delete(TaskUtils.convertStringToInteger(index));
    }

    /**
     * Deletes a consecutive list of tasks from both the working and main
     * arrayList. Upon failing to delete any item, the function terminates at
     * the object that it fails to delete and does not attempt to delete anymore
     * items
     * 
     * @param startIndex - An integer specifying the first index to be deleted.
     *            startIndex must be <= endIndex
     * @param endIndex - An integer specifying the last index to be deleted. The
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
     * Saves current data before changing directory using Storage, passes on
     * storage's ResultSet for changing directory
     * 
     * @param newDirectory - Directory to change to
     * @return ResultSet from storage's saveto()
     */
    public ResultSet changeDirectory(String newDirectory) {
        commitFullSave();
        ResultSet saveSuccess = dataLoader.saveTo(newDirectory);

        return saveSuccess;
    }

    /**
     * Saves the current data first before changing the pathfile of the file and
     * loading it from there.
     * 
     * @param newDirectory - Directory to load from
     * @return ResultSet:
     *         ResultSet.getStatus - STATUS_GOOD if success
     *         STATUS_JSON_ERROR if file found but corrupted
     *         STATUS_BAD if file not found
     *         ResultSet.isSuccess - true if success
     *         false if either file not found or corrupted
     */
    public ResultSet loadFrom(String newDirectory) {
        ResultSet loadResult = new ResultSet();

        commitFullSave();

        boolean loadSuccess = dataLoader.loadFrom(newDirectory);
        if (loadSuccess == true) {
            if (reloadFile()) {
                dataLoader.clearCommandFile();
                loadResult.setSuccess();
                loadResult.setStatus(ResultSet.STATUS_GOOD);
            } else {
                loadResult.setStatus(ResultSet.STATUS_JSON_ERROR);
                loadResult.setFail();
            }
        } else {
            loadResult.setFail();
            loadResult.setStatus(ResultSet.STATUS_BAD);
        }

        return loadResult;
    }

    /**
     * Retrieves the filepath of the file loaded from storage
     * 
     * @return - String containing filepath of the file INCLUDING the filename
     */
    public String getMainFilePath() {
        return dataLoader.getMainFilePath();
    }

    /**
     * Takes in 2 IDs and make the second task an association under the first
     * task. Returns a ResultSet for the operation
     * 
     * @param projectHeadId - Id of Task that takes in other task as
     *            associations. String representing an int
     * @param taskUnderId - Id of Task to be associated under the first task.
     *            String representing an int
     * @return ResultSet, where status is STATUS_GOOD and isSuccess is true if
     *         operation succeeded, bad and false otherwise, respectively
     */
    public ResultSet link(String projectHeadId, String taskUnderId) {
        return link(TaskUtils.convertStringToInteger(projectHeadId),
                TaskUtils.convertStringToInteger(taskUnderId));
    }

    /**
     * Takes in 2 IDs and make the second task an association under the first
     * task. Returns a ResultSet for the operation
     * 
     * @param projectHeadId - Id of Task that takes in other task as
     *            associations.
     * @param taskUnderId - Id of Task to be associated under the first task.
     * @return ResultSet, where status is STATUS_GOOD and isSuccess is true if
     *         operation succeeded, bad and false otherwise, respectively
     */
    public ResultSet link(int projectHeadId, int taskUnderId) {
        ResultSet linkResult = new ResultSet();
        // Prevents linking to itself and to tasks IDs that are not valid
        if (projectHeadId >= displayedTasks.size() || taskUnderId >= displayedTasks.size()
                || projectHeadId == taskUnderId) {
            linkResult.setFail();
            linkResult.setStatus(ResultSet.STATUS_BAD);
            return linkResult;
        } else {
            return link(displayedTasks.get(projectHeadId), displayedTasks.get(taskUnderId));
        }
    }

    /**
     * Associates a task to a project head task. Project heads are not allowed
     * to be under other tasks and projects under other project heads are not
     * allowed to be project heads
     * 
     * @param projectHead - Task to be linked to
     * @param linkedTask - Task to be linked
     * @return ResultSet, where status is STATUS_GOOD and isSuccess is true if
     *         operation succeeded, bad and false otherwise, respectively
     */
    public ResultSet link(TaskEntity projectHead, TaskEntity linkedTask) {
        ResultSet linkResult = new ResultSet();
        int projectHeadId = displayedTasks.indexOf(projectHead);
        linkResult.setIndex(projectHeadId);

        if (linkedTask.getAssociationState() == TaskEntity.PROJECT_HEAD) {
            linkResult.setFail();
            linkResult.setStatus(ResultSet.STATUS_BAD);
            return linkResult;
        }

        boolean linkSuccess = projectHead.addAssociation(linkedTask);
        if (!linkSuccess) {
            linkResult.setFail();
            linkResult.setStatus(ResultSet.STATUS_BAD);
            return linkResult;
        }

        linkedTask.setAssociationHead(projectHead);
        linkResult.setSuccess();
        linkResult.setStatus(ResultSet.STATUS_GOOD);

        return linkResult;
    }

    /**
     * Checks if the Index passed in for deletion is a valid index
     * 
     * @param startIndex - Index of the first item in the range to be deleted
     * @param endIndex - Index of the last item in the range to be deleted
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
     * Deletes a consecutive list of tasks from both the
     * working and main arrayList. Upon failing to delete any item, the function
     * terminates at the object that it fails to delete and does not attempt to
     * delete anymore items
     * 
     * @param startIndex - A string representing an int that represents the
     *            index of the first entry of all the entries to be deleted
     * @param endIndex - A string representing an int that represents the index
     *            of the last entry of all the entries to be deleted
     * @return - True if delete operation succeeded - False if delete operation
     *         failed
     */
    public boolean delete(String startIndex, String endIndex) {
        return delete(TaskUtils.convertStringToInteger(startIndex),
                TaskUtils.convertStringToInteger(endIndex));
    }

    /**
     * Gets the ID number of the upcoming task
     * 
     * @return ID of the task that is next, counting from the current time
     */
    public int getNextTimeListId() {
        TaskEntity currentTimePlaceholder = new TaskEntity("", null, Calendar.getInstance(), false);
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

    public boolean checkClashing(TaskEntity newlyAddedTask) {
        for (int i = 0; i < mainTaskEntities.size(); i++) {
            if (TaskUtils.isClashing(newlyAddedTask, mainTaskEntities.get(i))) {
                return true;
            }
        }
        // No clash if none of the task is clashing with it
        return false;
    }

    public ResultSet searchForCompleted() {
        ResultSet searchResults = new ResultSet();
        if (completedTaskEntities == null) {
            searchResults.setFail();
            searchResults.setStatus(ResultSet.STATUS_BAD);
            return searchResults;
        }
        searchedTasks = (ArrayList<TaskEntity>) completedTaskEntities.clone();
        return populateResultSet();
    }

    /**
     * Default function for search to not narrow the search
     * 
     * @param searchTerm - String to search for
     * @return True if there are results, false if otherwise
     */
    public ResultSet searchString(String searchTerm) {
        if (searchTerm.equalsIgnoreCase("completed")) {
            return searchForCompleted();
        } else {
            return searchString(searchTerm, false);
        }
    }

    /**
     * Searches the description, name and hashtag fields of taskentity for the
     * string given. Non-case sensitive, populates the search view with the
     * search results
     * 
     * @param searchTerm - String to search for
     * @param narrowSearch - Set to true if you want to trim the current search
     *            instead of searching for a new term
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively. ResultSet.searchCount() indicates how many search
     *         results were found
     */
    public ResultSet searchString(String searchTerm, boolean narrowSearch) {
        // Ensure that search is properly initialized
        if (searchedTasks == null) {
            searchedTasks = new ArrayList<TaskEntity>();
        }

        if (!narrowSearch) {
            searchedTasks = new ArrayList<TaskEntity>();
        }

        if (mainTaskEntities == null || floatingTaskEntities == null || completedTaskEntities == null) {
            ResultSet searchResults = new ResultSet();
            searchResults.setFail();
            searchResults.setStatus(ResultSet.STATUS_BAD);
            return searchResults;
        }

        searchAllArrays(searchTerm);
        return populateResultSet();
    }

    /**
     * Fill in ResultSet's data for searchString function. Only for use for
     * searchString. Pre-condition: search has succeeded
     * 
     * @return true and STATUS_GOOD for success in ResultSet.isSuccess() and
     *         ResultSet.getStatus() respectively. ResultSet.searchCount()
     *         indicates how many search results were found
     */
    private ResultSet populateResultSet() {
        ResultSet searchResults = new ResultSet();

        if (searchedTasks.size() > 0) {
            searchResults.setSuccess();
            searchResults.setStatus(ResultSet.STATUS_GOOD);
            searchResults.setSearchCount(searchedTasks.size());
            return searchResults;
        } else {
            searchResults.setSuccess();
            searchResults.setStatus(ResultSet.STATUS_GOOD);
            searchResults.setSearchCount(-1);
            return searchResults;
        }
    }

    /**
     * For searchString function
     * 
     * @param searchTerm - string to be searched
     */
    private void searchAllArrays(String searchTerm) {
        SearchModule.searchStringAddToResults(searchTerm, mainTaskEntities, searchedTasks);
        SearchModule.searchStringAddToResults(searchTerm, floatingTaskEntities, searchedTasks);
        SearchModule.searchStringAddToResults(searchTerm, completedTaskEntities, searchedTasks);
    }

    /**
     * Undoes the last command performed by the user
     * 
     * @return ArrayList of commands for main program to re-run
     */
    public ArrayList<String> undo() {
        System.out.println("undolist size: " + undoList.size() + " undoPointer : " + undoPointer);
        if (undoList.size() > 0 && undoPointer >= 0) {
            startUndo();

            if (undoPointer == -1) {
                System.out.println("Running 0 commands for undo");

                _undoing = false;
                return new ArrayList<String>();
            } else {
                System.out.println("Running " + (undoPointer + 1) + " commands for undo");

                ArrayList<String> commandsToRun = new ArrayList<String>(undoList.subList(0, undoPointer + 1));
                return commandsToRun;
            }
        } else {
            return null;
        }
    }

    /**
     * For undo function
     */
    private void startUndo() {
        reloadBackUpFile();
        _undoing = true;
        dataLoader.clearCommandFile();
        undoPointer--;
    }

    /**
     * Allows the undo list to be populated again
     */
    public void undoComplete() {
        _undoing = false;
    }

    /**
     * Reloads the state of the program at startup
     */
    public void reloadBackUpFile() {
        AllTaskLists taskdata = dataLoader.getBackUpTaskLists();

        mainTaskEntities = (ArrayList<TaskEntity>) taskdata.getMainTaskList().clone();
        floatingTaskEntities = (ArrayList<TaskEntity>) taskdata.getFloatingTaskList().clone();

        initializeAssociations();

        updateTaskEntityCurrentId();
        buildCompletedTasks();
        displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
        currentDisplayedList = DISPLAY_MAIN;
    }

    /**
     * Loads task data from file. For use when doing loadfrom to load new file
     * data
     * 
     * @return true if new file is successfully loaded. False if there was a
     *         JSon error when reading the new file
     */
    public boolean reloadFile() {
        AllTaskLists taskdata = dataLoader.getTaskLists();
        if (taskdata == null) {
            return false;
        }

        mainTaskEntities = (ArrayList<TaskEntity>) taskdata.getMainTaskList().clone();
        floatingTaskEntities = (ArrayList<TaskEntity>) taskdata.getFloatingTaskList().clone();

        initializeAssociations();

        updateTaskEntityCurrentId();
        buildCompletedTasks();
        displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
        currentDisplayedList = DISPLAY_MAIN;
        resetUndo();
        return true;
    }

    /**
     * Saves the user's theme preference
     * 
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively
     */
    public ResultSet saveTheme(String theme) {
        ResultSet saveResult = new ResultSet();
        boolean saveSuccess = dataLoader.saveThemePreference(theme);

        if (saveSuccess) {
            saveResult.setSuccess();
            saveResult.setStatus(ResultSet.STATUS_GOOD);
        } else {
            saveResult.setFail();
            saveResult.setStatus(ResultSet.STATUS_BAD);
        }
        return saveResult;
    }

    /**
     * Gets the user's saved theme preference
     * 
     * @return String indicating which css to use
     */
    public String loadTheme() {
        return dataLoader.getThemePreference();
    }
}
