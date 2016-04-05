/**
 * @author angie
 * @@author A0126357A
 * 
 *         StorageInterface as facade class for fileStorage
 */

package fileStorage;

import java.util.ArrayList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.ResultSet;
import entity.TaskEntity;

public class StorageInterface {
    
    private StorageController sc;
    
    public StorageInterface() {
        sc = new StorageController();
    }
    
    public AllTaskLists getTaskLists() {
        return sc.getTaskLists();
    }
    
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        return sc.storeTaskLists(allTaskLists);
    }
    
    public boolean storeTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating){
        return sc.storeTaskLists(main, floating);
    }
    
    public AllTaskLists getBackUpTaskLists() {
        return sc.getBackUpTaskLists();
    }
    
    public void deleteBackUp() {
        sc.deleteBackUp();
    }
    
    public boolean saveUponFullQueue(String command) {
        return sc.saveUponFullQueue(command);
    }
    
    public Queue<String> getCommandsQueue() {
        return sc.getCommandsQueue();
    }
    
    public void setCommandsQueue(Queue<String> newCommandsQueue) {
        sc.setCommandsQueue(newCommandsQueue);
    }
    
    public void clearCommandFileOnCommit() {
        sc.clearCommandFileOnCommit();
    }
    
    public void clearCommandFile() {
        sc.clearCommandFile();
    }
    
    public ResultSet saveTo(String newFilePath) {
        return sc.saveToNewDirectory(newFilePath);
    }
    
    public boolean loadFrom(String newFilePath) {
        return sc.loadFromNewFile(newFilePath);
    }
    
    public boolean saveThemePreference(String themeName) {
        return sc.saveThemePreference(themeName);
    }
    
    public String getThemePreference() {
        return sc.getThemePreference();
    }
}
