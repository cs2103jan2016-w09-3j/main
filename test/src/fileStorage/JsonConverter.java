package fileStorage;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.AllTaskLists;
import entity.TaskEntity;

public class JsonConverter {

    public JsonConverter() {
        
    }
    
    public String javaToJson(AllTaskLists allLists) {
        ArrayList<TaskEntity> mainTaskList = allLists.getMainTaskList();
        ArrayList<TaskEntity> floatingTaskList = allLists.getFloatingTaskList();
        
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls();
        Gson gson = gsonBuilder.create();
        
        String mainToJson = gson.toJson(mainTaskList);
        String floatingToJson = gson.toJson(floatingTaskList);
        
        return mainToJson + floatingToJson;
    }
    
    @SuppressWarnings("unchecked")
    public AllTaskLists jsonToJava(String readData) {
        Gson gson = new Gson();
        
        ArrayList<TaskEntity> allTasks = new ArrayList<TaskEntity>();
        ArrayList<TaskEntity> mainTaskList = new ArrayList<TaskEntity>();
        ArrayList<TaskEntity> floatingTaskList = new ArrayList<TaskEntity>();
        
        allTasks = gson.fromJson(readData, ArrayList.class);
        
        for (int i = 0; i < allTasks.size(); i++) {
            if(allTasks.get(i).isFloating() == true) {
                floatingTaskList.add(allTasks.get(i));
            } else {
                mainTaskList.add(allTasks.get(i));
            }
        }
        
        AllTaskLists allLists = new AllTaskLists(mainTaskList, floatingTaskList);
        
        return allLists;
    }
}