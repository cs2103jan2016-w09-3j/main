/**
 * @author qy
 * @@author A0125493A
 * 
 *          Interface class to the class that manages the handling of tasks
 *          during runtime
 */

package logic;

import java.util.ArrayList;
import java.util.Queue;
import entity.ResultSet;
import entity.TaskEntity;

public class TaskManagerInterface {
    TaskManager manager;

    public static int DISPLAY_MAIN;
    public static int DISPLAY_FLOATING;
    public static int DISPLAY_SEARCH;
    public static int DISPLAY_COMPLETED;
    public static int DISPLAY_OTHERS;

    public TaskManagerInterface() {
        manager = TaskManager.getInstance();

        // Values copied over for modularity
        DISPLAY_MAIN = manager.DISPLAY_MAIN;
        DISPLAY_FLOATING = manager.DISPLAY_FLOATING;
        DISPLAY_SEARCH = manager.DISPLAY_SEARCH;
        DISPLAY_COMPLETED = manager.DISPLAY_COMPLETED;
        DISPLAY_OTHERS = manager.DISPLAY_OTHERS;
    }

    /**
     * Returns a list of raw command strings to run in the event of a crash. If
     * there was no crash, this queue is expected to be empty
     * 
     * @return all commands to be re-run before start of program
     */
    public Queue<String> getBackedupCommands() {
        return manager.getBackedupCommands();
    }

    /**
     * Takes in a task to add and its raw command to store, adds the tasks into
     * the working list and returns a ResultSet indicating add results
     * 
     * @param newTask - Task to be added
     * @param command - Raw command prefixed with a view number at the front
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
    public ResultSet add(TaskEntity newTask, String command) {
        ResultSet executionResult = manager.add(newTask);
        if (executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }

    /**
     * Takes in a list of tasks and adds them into the working list. Also takes
     * in a raw command to store. Returns a resultset indicating add results
     * 
     * @param newTasks - ArrayList of tasks to be added
     * @param command - Raw command prefixed with a view number at the front
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
    public ResultSet add(ArrayList<TaskEntity> newTasks, String command) {
        ResultSet executionResult = manager.add(newTasks);
        if (executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }

    /**
     * Gets the task list in focus
     * 
     * @return
     */
    public ArrayList<TaskEntity> getWorkingList() {
        return manager.getWorkingList();
    }

    /**
     * Switches to the designated view
     * 
     * @param newView - Use one of the presets
     *            TaskManagerInterface.DISPLAY_MAIN,
     *            TaskManagerInterface.DISPLAY_FLOAT,
     *            TaskManagerInterface.DISPLAY_SEARCH
     */
    public void switchView(int newView) {
        manager.switchView(newView);
    }

    /**
     * Gets the current view in focus in TaskManager
     * 
     * @return int representing the current view
     *         TaskManagerInterface.DISPLAY_MAIN,
     *         TaskManagerInterface.DISPLAY_FLOAT,
     *         TaskManagerInterface.DISPLAY_SEARCH
     */
    public int getView() {
        return manager.getView();
    }

    /**
     * Takes in an ID specifying a task to be replaced with the task passed in.
     * Also stores the raw command. Returns a resultset for the modify results
     * 
     * @param taskId - ID relative to the current displayed list. String
     *            representation of an int
     * @param modifiedTask - Task to add after removing the ID specified
     * @param command - Raw command of the task prefixed by the view number the
     *            command is carried out in
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
    public ResultSet modify(String taskId, TaskEntity modifiedTask, String command) {
        ResultSet executionResult = manager.modify(taskId, modifiedTask);
        if (executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }

    /**
     * Takes in an ID specifying a task to be replaced with the task passed in.
     * Also stores the raw command. Returns a ResultSet for the modify results
     * 
     * @param taskId - ID relative to the current displayed list
     * @param modifiedTask - Task to add after removing the ID specified
     * @param command - Raw command of the task prefixed by the view number the
     *            command is carried out in
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
    public ResultSet modify(int taskId, TaskEntity modifiedTask, String command) {
        ResultSet executionResult = manager.modify(taskId, modifiedTask);
        if (executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }

    /**
     * Takes in an Id to delete a task and remove it from the lists. Also stores
     * the raw command. Returns a ResultSet of the deletion result
     * 
     * @param taskToMark - Id of the task to be delete in the displayed list
     * @param command - Raw command of the operation prefixed by an int
     *            representing the view the operation was carried out in
     * @return ResultSet - Deletion status to be good and deletion success to be
     *         true if deletion succeeded, bad and false otherwise,
     *         respectively.
     *         ResultSet's getIndex to be the position to be in the list after
     *         deletion, -1 if the list is empty after deletion
     */
    public ResultSet delete(String taskId, String command) {
        ResultSet deletionResult = new ResultSet();
        if (manager.delete(taskId)) {
            deletionResult.setSuccess();
            deletionResult.setStatus(ResultSet.STATUS_GOOD);
        } else {
            deletionResult.setFail();
            deletionResult.setStatus(ResultSet.STATUS_BAD);
        }

        if (!deletionResult.isSuccess()) {
            return deletionResult;
        } else {
            manager.saveBackupCommand(command);
            deletionResult.setIndex(manager.checkCurrentId(TaskUtils.convertStringToInteger(taskId)));
            return deletionResult;
        }
    }

    /**
     * Takes in a filepath to change the save directory for the tasklist to.
     * Results operation result in the form of a ResultSet
     * 
     * @param newDirectory - pathfile INCLUDING file name of the new save
     *            location
     * @return operation results
     */
    public ResultSet changeDirectory(String newDirectory) {
        return manager.changeDirectory(newDirectory);
    }

    /**
     * Loads from a filepath and changes the save directory for the tasklist.
     * Results operation result in the form of a ResultSet
     * 
     * @param newDirectory - pathfile INCLUDING file name of the new loaded file
     * @return ResultSet:
     *         ResultSet.getStatus - STATUS_GOOD if success
     *         STATUS_JSON_ERROR if file found but corrupted
     *         STATUS_BAD if file not found
     *         ResultSet.isSuccess - true if success
     *         false if either file not found or corrupted
     */
    public ResultSet loadFrom(String newDirectory) {
        return manager.loadFrom(newDirectory);
    }

    /**
     * Gets where the file is currently stored to
     * 
     * @return filepath INCLUDING filename
     */
    public String getMainFilePath() {
        return manager.getMainFilePath();
    }

    /**
     * Checks if JSON can read the file loaded. Returns true if JSON parsing is
     * successful
     * 
     * @return true if JSON read success, false otherwise
     */
    public boolean checkLoad() {
        return manager.checkLoad();
    }

    /**
     * Takes in tasks and make the second task an association under the first
     * task. Also stores the raw command. Returns a ResultSet for the operation
     * 
     * @param projectHeadId - Task that takes in other task as associations
     * @param taskUnderId - Task to be associated under the first task
     * @param command - Raw command prefixed with a int representing the view in
     *            which this command is carried out in
     * @return ResultSet, where status is STATUS_GOOD and isSuccess is true if
     *         operation succeeded, bad and false otherwise, respectively
     */
    public ResultSet link(TaskEntity projectHeadId, TaskEntity taskUnderId, String command) {
        ResultSet executionResults = manager.link(projectHeadId, taskUnderId);
        if (executionResults.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResults;
    }

    /**
     * Takes in 2 IDs and make the second task an association under the first
     * task. Also stores the raw command. Returns a ResultSet for the operation
     * 
     * @param projectHeadId - Id of task that takes in other task as
     *            associations.
     *            String representing an int
     * @param taskUnderId - Id of task to be associated under the first task.
     *            String
     *            representing an int
     * @param command - Raw command prefixed with a int representing the view in
     *            which this command is carried out in
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively
     */
    public ResultSet link(String projectHeadId, String taskUnderId, String command) {
        ResultSet executionResults = manager.link(projectHeadId, taskUnderId);
        if (executionResults.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResults;
    }

    /**
     * Takes an ID of a task to mark complete and removes it from the
     * uncompleted task set. Stores the raw command and returns a ResultSet of
     * the results of the operation
     * 
     * @param taskToMark - Id of the task to be marked completed in the display
     *            list. String representation of an int.
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively
     */
    public ResultSet markAsDone(String taskToMark, String command) {
        return markAsDone(TaskUtils.convertStringToInteger(taskToMark), command);
    }

    /**
     * Takes an ID of a task to mark complete and removes it from the
     * uncompleted task set. Stores the raw command and returns a resultset of
     * the results of the operation
     * 
     * @param taskToMark - Id of the task to be marked completed in the display
     *            list
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively
     */
    public ResultSet markAsDone(int taskToMark, String command) {
        ResultSet markingResults = manager.markAsDone(taskToMark);

        if (!markingResults.isSuccess()) {
            return markingResults;
        } else {
            manager.saveBackupCommand(command);
            markingResults.setIndex(manager.checkCurrentId(taskToMark));
            return markingResults;
        }
    }

    /**
     * Gets the next upcoming task
     * 
     * @return ID of the upcoming task
     */
    public int getNextTimeListId() {
        return manager.getNextTimeListId();
    }

    /**
     * Gets a random floating task. Returns null if there are no floating tasks
     * 
     * @return a random TaskEntity that is from floatingTaskEntities
     */
    public TaskEntity getRandomFloating() {
        return manager.getRandomFloating();
    }

    /**
     * Undos the last command. Returns ArrayList of raw commands to re-run
     * 
     * @return ArrayList of raw commands to re-run
     */
    public ArrayList<String> undo() {
        return manager.undo();
    }

    /**
     * Gets the program out of an undo-ing state, allowing commands to be saved
     * to the undo stack again
     */
    public void undoComplete() {
        System.out.println("Undo completed");
        manager.undoComplete();
    }

    /**
     * Takes in a string to search for and builds search results in the search
     * view. Also stores the raw command. Returns ResultSet for results of
     * search
     * 
     * @param searchTerm - String that may contain substrings to search for or
     *            hashtags
     * @param command - Raw command prefixed with a int representing the view in
     *            which this command is carried out in
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively. ResultSet.searchCount() indicates how many search
     *         results were found
     */
    public ResultSet searchString(String searchTerm, String command) {
        ResultSet executionResult = manager.searchString(searchTerm);
        if (executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }

    /**
     * Saves the user's theme preference
     * 
     * @return true and STATUS_GOOD for success, false and STATUS_BAD for
     *         failure in ResultSet.isSuccess() and ResultSet.getStatus()
     *         respectively
     */
    public ResultSet saveTheme(String theme) {
        return manager.saveTheme(theme);
    }

    /**
     * Gets the user's saved theme preference
     * 
     * @return String indicating which css to use
     */
    public String loadTheme() {
        return manager.loadTheme();
    }

    /**
     * Saves the tasklist into the file. Use when exiting program
     */
    public void closeTaskManager() {
        manager.commitFullSave();
    }
}