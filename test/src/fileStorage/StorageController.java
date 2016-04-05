/**
 * @author angie
 * @@author A0126357A
 * 
 *         StorageController to create instances of StorageHandler and JsonConverter
 *         to merge functionalities.
 */

package fileStorage;

import java.util.ArrayList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.ResultSet;
import entity.TaskEntity;

public class StorageController {
    
    public StorageHandler storageHandler;
    public static final int QUEUE_SIZE = 5;
    private static final int SEARCH_VIEW = 4;
    private static final int WRITE_TO_MAIN_FILE = 2;
    
    public StorageController() {
        storageHandler = new StorageHandler();
    }
    
    //============================================================================
    // Handling main file
    // ===========================================================================

    /**
     * Retrieve all tasks previously saved in the text file.
     * 
     * @return AllTaskLists
     */
    public AllTaskLists getTaskLists() {
        JsonConverter jsonConverter = new JsonConverter();
        
        String retrievedTasks = storageHandler.getAllStoredTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);
        
        return retrievedList;
    }

    /** 
     * Returns true if tasks are written to file, false otherwise.
     *  
     * @return boolean
     */    
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        JsonConverter jsonConverter = new JsonConverter();
        
        String toStore = jsonConverter.javaToJson(allTaskLists);
        
        boolean isSaved = storageHandler.identifyWriteTo(toStore, WRITE_TO_MAIN_FILE);
        assert isSaved == true : "Tasks not stored.";
        
        return isSaved;
    }
    
    /** 
     * Returns true if tasks are written to file, false otherwise. 
     * @return boolean
     */
    public boolean storeTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating){
        AllTaskLists newList = new AllTaskLists();
        
        newList.setFloatingTaskList(floating);
        newList.setMainTaskList(main);
        
        return storeTaskLists(newList);
    }
    
    //============================================================================
    // Handling back up file
    // ===========================================================================

    public AllTaskLists getBackUpTaskLists() {
        JsonConverter jsonConverter = new JsonConverter();
        
        String retrievedTasks = storageHandler.getAllBackUpTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);
        
        return retrievedList;
    }
    
    public void deleteBackUp() {
        storageHandler.deleteBackUpFile();
    }
    
    //============================================================================
    // Handling command file
    // ===========================================================================
    
    /**
     * Returns true if commands queue is full, false otherwise
     * This is to be passed on to Logic, Logic will then commit the working tasks
     * 
     * @param command
     * @return isFullQueue
     */
    public boolean saveUponFullQueue(String command) {
        
        boolean isSaved = storeCommandLine(command);
        assert isSaved == true : "Not commited to main file.";
      
        Queue<String> newCommandsQueue = getCommandsQueue();
        newCommandsQueue.offer(command);
        setCommandsQueue(newCommandsQueue);
        
        boolean isFullQueue = false;
        
        String[] splitCommand = command.split(" ");
        if (splitCommand[0].equals(SEARCH_VIEW) == false && newCommandsQueue.size() >= QUEUE_SIZE) {
            isFullQueue = true;
        }
        return isFullQueue;
    }
    
    public boolean storeCommandLine(String command) {
        return storageHandler.writeToCommandFile(command);
    }
        
    public Queue<String> getCommandsQueue() {
        return storageHandler.getAllCommandsQueue();
    }
    
    public void setCommandsQueue(Queue<String> newCommandsQueue) {
        storageHandler.setAllCommandsQueue(newCommandsQueue);
    }
    
    public void clearCommandFileOnCommit() {
        storageHandler.clearCommandFileUponCommit();
    }
    
    public void clearCommandFile() {
        storageHandler.clearCommandFile();
    }
    
    //============================================================================
    // Changing directory
    // ===========================================================================
    
    public ResultSet saveToNewDirectory(String newFilePath) {
        return storageHandler.changeDirectory(newFilePath);
    }
    
    public boolean loadFromNewFile(String newFilePath) {
        return storageHandler.loadFromExistingFile(newFilePath);
    }
    
    //============================================================================
    // Saving theme
    // ===========================================================================
    
    public boolean saveThemePreference(String themeName) {
        return storageHandler.saveThemeName(themeName);
    }
    
    public String getThemePreference() {
        return storageHandler.getThemeName();
    }
}
