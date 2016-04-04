/**
 * @author qy
 * @@author a0125493a
 * 
 *         Interface class to the class that manages the handling of tasks
 *         during runtime
 */

package mainLogic;

import java.util.ArrayList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.ResultSet;
import entity.TaskEntity;

public class TaskManagerInterface {
    TaskManager manager;
    
    public static int DISPLAY_MAIN;
    public static int DISPLAY_FLOATING;
    public static int DISPLAY_SEARCH;
    public static int DISPLAY_COMPLETED;
    public static int DISPLAY_OTHERS;
    
    public TaskManagerInterface () {
        manager = TaskManager.getInstance();
        
        //Values copied over for modularity
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
    public Queue<String> getBackedupCommands () {
        return manager.getBackedupCommands();
    }
   
    public ResultSet add (TaskEntity newTask, String command) {
        ResultSet executionResult = manager.add(newTask);
        if(executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    public ResultSet add (ArrayList<TaskEntity> newTasks, String command) {
        ResultSet executionResult = manager.add(newTasks);
        if(executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    public ArrayList<TaskEntity> getWorkingList () {
        return manager.getWorkingList();
    }
    
    public void switchView (int newView) {
        manager.switchView(newView);
    }
    
    public ResultSet modify (String taskId, TaskEntity modifiedTask, String command) {
        ResultSet executionResult = manager.modify(taskId, modifiedTask);
        if(executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }

    public ResultSet modify(int taskId, TaskEntity modifiedTask, String command) {
        ResultSet executionResult = manager.modify(taskId, modifiedTask);
        if (executionResult.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    /**
     * Deletes a task and remove it from the lists
     * 
     * @param taskToMark - Id of the task to be delete in the displayed list
     * 
     * @return ResultSet - Deletion status to be good and deletion success to be
     *         true if deletion succeeded, bad and false otherwise, respectively
     *         Index to be the position to be in the list after deletion, -1 if the list is empty after deletion
     */
    public ResultSet delete (String taskId, String command) {
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
    
    //TODO : Function to be removed. Left here just in case first
    public boolean delete (String taskIdStart, String taskIdEnd, String command) {
        return manager.delete(taskIdStart, taskIdEnd);
    }
    
    public ResultSet changeDirectory (String newDirectory) {
        return manager.changeDirectory(newDirectory);
    }
    
    
    public ResultSet link (TaskEntity projectHeadId, TaskEntity taskUnderId, String command) {
        ResultSet executionResults = manager.link(projectHeadId, taskUnderId);
        if(executionResults.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResults;
    }
    
    public ResultSet link (String projectHeadId, String taskUnderId, String command) {
        ResultSet executionResults = manager.link(projectHeadId, taskUnderId);
        if(executionResults.isSuccess()) {
            manager.saveBackupCommand(command);
        }
        return executionResults;
    }
    
    /**
     * Marks a task completed and removes it from the uncompleted task set
     * 
     * @param taskToMark - Id of the task to be marked completed in the display
     *            list
     * 
     * @return -2 if marking done failed
     *         -1 if after marking done, the list is empty
     *         Id of the position to be in the display list after marking done
     *         otherwise
     */
    public ResultSet markAsDone (String taskToMark, String command) {
        return markAsDone(TaskUtils.convertStringToInteger(taskToMark), command);
    }
    
    public ResultSet markAsDone (int taskToMark, String command) {
        ResultSet markingResults = manager.markAsDone(taskToMark);
        
        if(!markingResults.isSuccess()) {
            return markingResults;
        } else {
            manager.saveBackupCommand(command);
            markingResults.setIndex(manager.checkCurrentId(taskToMark));
            return markingResults;
        }
    }
    
    public int getNextTimeListId() {
        return manager.getNextTimeListId();
    }
    
    public TaskEntity getRandomFloating () {
        return manager.getRandomFloating();
    }
    
    public ArrayList<String> undo () {
        return manager.undo();
    }
    
    public void undoComplete() {
        System.out.println("Undo completed");
        manager.undoComplete();
    }
    
    public ResultSet searchString (String searchTerm, String command) {
        ResultSet executionResult = manager.searchString(searchTerm);
        if(executionResult.isSuccess() ) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    /**
     * Interface for storage to get saved data for interval and command queue
     * full saving
     * 
     * @return processed task arrays fit for saving via JSON
     */
    public AllTaskLists getSaveArray() {
        return manager.generateSavedTaskArray();
    }
    
    public ResultSet saveTheme(String theme) {
        return manager.saveTheme(theme);
    }

    public String loadTheme() {
        return manager.loadTheme();
    }

    public void closeTaskManager() {
        manager.commitFullSave();
    }
    
    public ArrayList<TaskEntity> generateFakeData() {
        return manager.generateFakeData();
    }
}