package fileStorage;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;

import entity.AllTaskLists;
import entity.TaskEntity;

public class StorageController implements StorageInterface {
    
    private MainFileHandler fh = new MainFileHandler();
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
        // TODO Auto-generated method stub
        return null;
    }

    // Returns true if task lists written into file. False otherwise.
    public Boolean storeTaskLists(AllTaskLists atl) {
        MainFileHandler fm = new MainFileHandler();
        JsonConverter jh = new JsonConverter();
        boolean isStored = false;
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
        JsonConverter json = new JsonConverter();
        String jsonString = "";
        
        //jsonString = json.javaToJson(te).toString();
        jsonString = json.javaToJson(te);
        
        return jsonString;
    }
}
