package fileStorage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.TaskEntity;

public class StorageController {
    
    public StorageHandler storageHandler;
    public static final int QUEUE_SIZE = 5;
    private static final int SEARCH_VIEW = 4;
    private static final int WRITE_TO_MAIN_FILE = 2;
    
    public StorageController() {
        storageHandler = new StorageHandler();
    }
    
    // Test function
    public static void main (String args[]) {
        StorageHandler sh = new StorageHandler();
        StorageController si = new StorageController();
        
        //sh.writeToFile("testing see if it works", 2);
        //sh.changeDirectory("Desktop/list.txt");
    }

    //============================================================================
    // Handling main file
    // ===========================================================================

    /**
     * Retrieve all tasks previously saved in the text file.
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
     * @return boolean
     */    
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        JsonConverter jsonConverter = new JsonConverter();
        
        String toStore = jsonConverter.javaToJson(allTaskLists);
        
        boolean isSaved = storageHandler.writeToFile(toStore, WRITE_TO_MAIN_FILE);
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
        storageHandler.deleteBackUpFile();;
    }
    
    //============================================================================
    // Handling command file
    // ===========================================================================
        
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
    
    public Queue<String> getCommandsUponInit() {
        Queue<String> retrievedCommands = storageHandler.getAllCommandsQueue();
        
        return retrievedCommands;
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
    
    public boolean saveToNewDirectory(String newFilePath) {
        return storageHandler.changeDirectory(newFilePath);
    }
    
    //============================================================================
    // Generate dummy data
    // ===========================================================================
    
    private AllTaskLists createDummy() {
        ArrayList<TaskEntity> dummyMainList = new ArrayList<TaskEntity>();
        dummyMainList.add(new TaskEntity("firstTask"));
        dummyMainList.add(new TaskEntity("secondTask"));
        assert dummyMainList.size() > 0;
        
        ArrayList<TaskEntity> dummyFloatingList = new ArrayList<TaskEntity>();
        dummyFloatingList.add(new TaskEntity("floatingTaskOne"));
        dummyFloatingList.add(new TaskEntity("floatingTaskTwo"));
        assert dummyFloatingList.size() > 0;
        
        AllTaskLists dummyTL = new AllTaskLists();
        dummyTL.setFloatingTaskList(dummyFloatingList);
        dummyTL.setMainTaskList(dummyMainList);
        return dummyTL;
    }
    
    private Queue<String> createDummyCommands() {
        Queue<String> dummyCommands = new LinkedList<String>();
        dummyCommands.offer("add whatever : blah blah at blah time blah data");
        dummyCommands.offer("add hello : blah blah at blah time blah data");
        dummyCommands.offer("add annyeong : blah blah at blah time blah data");
        return dummyCommands;
    }
}
