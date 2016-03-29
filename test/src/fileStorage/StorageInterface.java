package fileStorage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.TaskManagerInterface;

public class StorageInterface {
    
    public StorageHandler storageHandler;
    
    public StorageInterface() {
        storageHandler = new StorageHandler();
    }
    
    // Test function
    public static void main (String args[]) {
        StorageInterface sc = new StorageInterface();
        StorageHandler sh = new StorageHandler();
        JsonConverter jc = new JsonConverter();
        
        AllTaskLists dummyTL = sc.createDummy();
        sh.writeToMainFile(jc.javaToJson(dummyTL));
        
        //String data = fm.readFromExistingFile();
        //System.out.println(data);
        //AllTaskLists convertedDummy = jc.jsonToJava(data);
        
        Queue<String> dummyCommands = sc.createDummyCommands();
        sh.writeToCommandFile("HELLO COMMAND");
        //sh.writeToMainFile("HELLO MAIN");
        
        //ch.saveUponExit(true);
        //System.out.println(ch.readFromExistingCommandFile()); 
    }

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
        
        boolean isSaved = storageHandler.writeToMainFile(toStore);
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
    
    public Queue<String> getCommandsUponInit() {
        Queue<String> retrievedCommands = storageHandler.getAllCommandsQueue();
        
        return retrievedCommands;
    }
        
    public Queue<String> getCommandsQueue() {
        return storageHandler.getAllCommandsQueue();
    }
    
    public void setCommandsQueue(Queue<String> newCommandsQueue) {
        storageHandler.setAllCommandsQueue(newCommandsQueue);
    }
    
    public boolean storeCommandLine(String command) {
        boolean isSaved = storageHandler.writeToCommandFile(command);
        assert isSaved == true : "Command not stored.";
        
        return isSaved;
    }
    
    public void clearCommandFile() {
        storageHandler.clearCommandFileUponCommit();
    }
    
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
