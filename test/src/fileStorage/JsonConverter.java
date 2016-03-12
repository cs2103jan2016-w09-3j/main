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
    
    public AllTaskLists jsonToJava() {
        
    }
}