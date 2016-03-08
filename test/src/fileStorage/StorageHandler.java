package fileStorage;

import java.util.ArrayList;

import org.json.JSONException;

import entity.AllTaskLists;
import entity.TaskEntity;

public class StorageHandler implements StorageInterface {
    
    // Test function
    public static void main (String args[]) {
        StorageHandler storageHandler = new StorageHandler();
        
        FileHandler fm = new FileHandler();
        
        AllTaskLists dummyTL = storageHandler.createDummy();
        fm.writeToFile(storageHandler.convertObjToJson(dummyTL));
        
        System.out.println(fm.readFromExistingFile());
    }

    public AllTaskLists getTaskLists() {
        // TODO Auto-generated method stub
        return null;
    }

    // Returns true if task lists written into file. False otherwise.
    public Boolean storeTaskLists(AllTaskLists atl) {
        FileHandler fm = new FileHandler();
        JsonHandler jh = new JsonHandler();
        boolean isStored = false;
        try {
            isStored = fm.writeToFile(jh.convertToJson(atl).toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isStored;
    }
    
    private AllTaskLists createDummy() {
        ArrayList<TaskEntity> dummyMainList = new ArrayList<TaskEntity>();
        dummyMainList.add(new TaskEntity("firstTask"));
        dummyMainList.add(new TaskEntity("secondTask"));
        
        ArrayList<TaskEntity> dummyFloatingList = new ArrayList<TaskEntity>();
        dummyFloatingList.add(new TaskEntity("floatingTaskOne"));
        dummyFloatingList.add(new TaskEntity("floatingTaskTwo"));
        
        AllTaskLists dummyTL = new AllTaskLists();
        dummyTL.setFloatingTaskList(dummyFloatingList);
        dummyTL.setMainTaskList(dummyMainList);
        return dummyTL;
    }
    
    private String convertObjToJson (AllTaskLists te) {
        JsonHandler json = new JsonHandler();
        String jsonString = "";
        
        try {
            jsonString = json.convertToJson(te).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return jsonString;
    }
}
