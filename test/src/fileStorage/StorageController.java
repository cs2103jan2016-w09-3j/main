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
        
        AllTaskLists dummyTL = sh.createDummy();
        fm.writeToFile(jc.javaToJson(dummyTL));
        
        String data = fm.readFromExistingFile();
        System.out.println(data);
        AllTaskLists convertedDummy = jc.jsonToJava(data);
        Calendar created = convertedDummy.getFloatingTaskList().get(0).getDateCreated();
        
        System.out.println("Calendar :" + created);
    }

    public AllTaskLists getTaskLists() {
        MainFileHandler mainHandler = new MainFileHandler();
        JsonConverter jsonConverter = new JsonConverter();
        
        String retrievedTasks = mainHandler.getAllStoredTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);
        
        return retrievedList;
    }

    // Returns true if task lists written into file. False otherwise.
    public Boolean storeTaskLists(AllTaskLists allTaskLists) {
        MainFileHandler mainHandler = new MainFileHandler();
        JsonConverter jsonConverter = new JsonConverter();
        
        String toStore = jsonConverter.javaToJson(allTaskLists);
        
        boolean isStored = mainHandler.writeToFile(toStore);
        
        return isStored;
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
}
