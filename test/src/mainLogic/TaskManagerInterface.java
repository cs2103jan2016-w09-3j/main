/**
 * @author Qin Ying
 * 
 *         Interface class to the class that manages the handling of tasks
 *         during runtime
 */

package mainLogic;

import java.util.ArrayList;

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
    
    public int add (TaskEntity newTask) {
        return manager.add(newTask);
    }
    
    public int add (ArrayList<TaskEntity> newTasks) {
        return manager.add(newTasks);
    }
    
    public ArrayList<TaskEntity> getWorkingList () {
        return manager.getWorkingList();
    }
    
    public void switchView (int newView) {
        manager.switchView(newView);
    }
    
    public int modify (String taskId, TaskEntity modifiedTask) {
        return manager.modify(taskId, modifiedTask);
    }
    
    public int modify (int taskId, TaskEntity modifiedTask) {
        return manager.modify(taskId, modifiedTask);
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
    public int delete (String taskId) {
        boolean deletionResult = manager.delete(taskId);
        
        if(!deletionResult) {
            return -2;
        } else {
            return manager.checkCurrentId(Utils.convertBase36ToDec(taskId));
        }
    }
    
    public boolean delete (String taskIdStart, String taskIdEnd) {
        return manager.delete(taskIdStart, taskIdEnd);
    }
    
    public boolean link (TaskEntity projectHeadId, TaskEntity taskUnderId) {
        return manager.link(projectHeadId, taskUnderId);
    }
    
    public boolean link (String projectHeadId, String taskUnderId) {
        return manager.link(projectHeadId, taskUnderId);
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
    public int markAsDone (String taskToMark) {
        return markAsDone(Utils.convertBase36ToDec(taskToMark));
    }
    
    public int markAsDone (int taskToMark) {
        boolean markingResults = manager.markAsDone(taskToMark);
        
        if(!markingResults) {
            return -2;
        } else {
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
    public void closeTaskManager () {
        manager.closeTaskManager();
    }
}