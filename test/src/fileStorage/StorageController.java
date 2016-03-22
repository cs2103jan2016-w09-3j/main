package fileStorage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.TaskEntity;

public class StorageController implements StorageInterface {
    
    // Test function
    public static void main (String args[]) {
        StorageController sc = new StorageController();
        StorageHandler sh = new StorageHandler();
        JsonConverter jc = new JsonConverter();
        CommandHandler ch = new CommandHandler();
        
        //AllTaskLists dummyTL = sh.createDummy();
        //fm.writeToFile(jc.javaToJson(dummyTL));
        
        //String data = fm.readFromExistingFile();
        //System.out.println(data);
        //AllTaskLists convertedDummy = jc.jsonToJava(data);
        
        Queue<String> dummyCommands = sc.createDummyCommands();
        
        sh.setAllCommandsQueue(dummyCommands);
        //
        ch.saveUponTimeOut();
        ch.saveUponFullQueue("Add blah blah angie awesome1");
        ch.saveUponFullQueue("Add blah blah angie awesome2");
        ch.saveUponFullQueue("Add blah blah angie awesome3");
        ch.saveUponFullQueue("Add blah blah angie awesome4");
        ch.saveUponFullQueue("Add blah blah angie awesome5");
        ch.saveUponFullQueue("Add blah blah angie awesome6");
        ch.saveUponFullQueue("Add blah blah angie awesome7");
        ch.saveUponFullQueue("Add blah blah angie awesome8");
        ch.saveUponFullQueue("Add blah blah angie awesome9");
        ch.saveUponFullQueue("Add blah blah angie awesome10");
        ch.saveUponFullQueue("Add blah blah angie awesome11");
        ch.saveUponFullQueue("Add blah blah angie awesome12");
        //ch.saveUponExit(true);
        //System.out.println(ch.readFromExistingCommandFile()); 
    }

    /**
     * Retrieve all tasks previously saved in the text file.
     * @return AllTaskLists
     */
    public AllTaskLists getTaskLists() {
        StorageHandler mainHandler = new StorageHandler();
        JsonConverter jsonConverter = new JsonConverter();
        
        String retrievedTasks = mainHandler.getAllStoredTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);
        
        return retrievedList;
    }

    /** 
     * Returns true if tasks are written to file, false otherwise. 
     * @return boolean
     */    
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        StorageHandler mainHandler = new StorageHandler();
        JsonConverter jsonConverter = new JsonConverter();
        
        String toStore = jsonConverter.javaToJson(allTaskLists);
        
        boolean isSaved = mainHandler.writeToMainFile(toStore);
        
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
