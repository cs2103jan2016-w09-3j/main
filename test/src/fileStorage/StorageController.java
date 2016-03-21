package fileStorage;

import java.util.ArrayList;
import java.util.Calendar;

import entity.AllTaskLists;
import entity.TaskEntity;

public class StorageController implements StorageInterface {
    
    // Test function
    public static void main (String args[]) {
        StorageController sh = new StorageController();
        MainFileHandler fm = new MainFileHandler();
        JsonConverter jc = new JsonConverter();
        CommandHandler ch = new CommandHandler();
        
        //AllTaskLists dummyTL = sh.createDummy();
        //fm.writeToFile(jc.javaToJson(dummyTL));
        
        //String data = fm.readFromExistingFile();
        //System.out.println(data);
        //AllTaskLists convertedDummy = jc.jsonToJava(data);
        //Calendar created = convertedDummy.getFloatingTaskList().get(0).getDateCreated();
        
        //System.out.println("Calendar :" + created);
        ArrayList<String> dummyCommands = sh.createDummyCommands();
        System.out.println(ch.writeToCommandFile(dummyCommands));
        
        System.out.println(ch.readFromExistingCommandFile());
        
    }

    public AllTaskLists getTaskLists() {
        MainFileHandler mainHandler = new MainFileHandler();
        JsonConverter jsonConverter = new JsonConverter();
        
        String retrievedTasks = mainHandler.getAllStoredTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);
        
        return retrievedList;
    }

    // Returns true if task lists written into file. False otherwise.
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        MainFileHandler mainHandler = new MainFileHandler();
        JsonConverter jsonConverter = new JsonConverter();
        
        String toStore = jsonConverter.javaToJson(allTaskLists);
        
        boolean isStored = mainHandler.writeToFile(toStore);
        
        return isStored;
    }
    
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
    
    private ArrayList<String> createDummyCommands() {
        ArrayList<String> dummyCommands = new ArrayList<String>();
        dummyCommands.add("add whatever : blah blah at blah time blah data");
        dummyCommands.add("add hello : blah blah at blah time blah data");
        dummyCommands.add("add annyeong : blah blah at blah time blah data");
        return dummyCommands;
    }
}
