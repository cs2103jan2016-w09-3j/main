/**
 * @author Qin Ying
 * 
 *         Interface class to the class that manages the handling of tasks
 *         during runtime
 */

package mainLogic;

import java.util.ArrayList;
import java.util.Queue;

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
   
    public int add (TaskEntity newTask, String command) {
        int executionResult = manager.add(newTask);
        if(executionResult != -2) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    public int add (ArrayList<TaskEntity> newTasks, String command) {
        int executionResult = manager.add(newTasks);
        if(executionResult != -2) {
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
    
    public int modify (String taskId, TaskEntity modifiedTask, String command) {
        int executionResult = manager.modify(taskId, modifiedTask);
        if(executionResult != -2) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    public int modify (int taskId, TaskEntity modifiedTask, String command) {
        int executionResult = manager.modify(taskId, modifiedTask);
        if(executionResult != -2) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    
    /**
     * Deletes a task and remove it from the lists
     * 
     * @param taskToMark - Id of the task to be delete in the displayed list
     * 
     * @return -2 if the deletion failed
     *         -1 if after deletion, the list is empty
     *         Id of the position to be in the display list after deleting
     *         otherwise
     */
    public int delete (String taskId, String command) {
        boolean deletionResult = manager.delete(taskId);
        
        if (!deletionResult) {
            return -2;
        } else {
            manager.saveBackupCommand(command);
            return manager.checkCurrentId(Utils.convertBase36ToDec(taskId));
        }
    }
    
    //TODO : Save this to command list for crash. String command now only there for show
    public boolean delete (String taskIdStart, String taskIdEnd, String command) {
        return manager.delete(taskIdStart, taskIdEnd);
    }
    
    public boolean link (TaskEntity projectHeadId, TaskEntity taskUnderId, String command) {
        boolean executionSucceeded = manager.link(projectHeadId, taskUnderId);
        if(executionSucceeded) {
            manager.saveBackupCommand(command);
        }
        return executionSucceeded;
    }
    
    public boolean link (String projectHeadId, String taskUnderId, String command) {
        boolean executionSucceeded = manager.link(projectHeadId, taskUnderId);
        if(executionSucceeded) {
            manager.saveBackupCommand(command);
        }
        return executionSucceeded;
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
    public int markAsDone (String taskToMark, String command) {
        return markAsDone(Utils.convertBase36ToDec(taskToMark), command);
    }
    
    public int markAsDone (int taskToMark, String command) {
        boolean markingResults = manager.markAsDone(taskToMark);
        
        if(!markingResults) {
            return -2;
        } else {
            manager.saveBackupCommand(command);
            return manager.checkCurrentId(taskToMark);
        }
    }
    
    public int getNextTimeListId() {
        return manager.getNextTimeListId();
    }
    
    public TaskEntity getRandomFloating () {
        return manager.getRandomFloating();
    }
    
    public ArrayList<TaskEntity> undo () {
        return manager.undo();
    }
    
    public int searchString (String searchTerm, String command) {
        int executionResult = manager.searchString(searchTerm);
        if(executionResult != -2 ) {
            manager.saveBackupCommand(command);
        }
        return executionResult;
    }
    public void closeTaskManager () {
        manager.closeTaskManager();
    }
}