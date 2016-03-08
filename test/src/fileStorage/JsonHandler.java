package fileStorage;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import entity.AllTaskLists;
import entity.TaskEntity;

public class JsonHandler {
    
    public JsonHandler() {
        
    }
    
    public JSONArray convertToJson(AllTaskLists allTaskLists) throws JSONException {
        JSONArray taskEntitiesJson = new JSONArray();
        
        for(int i = 0; i< allTaskLists.getFloatingTaskList().size(); i++) {
            taskEntitiesJson.put(extractJsonObject(allTaskLists.getFloatingTaskList(), i));
        }
        
        for(int i = 0; i< allTaskLists.getMainTaskList().size(); i++) {
            taskEntitiesJson.put(extractJsonObject(allTaskLists.getMainTaskList(), i));
        }
        
        System.out.println("Converting arraylist to JSON.");
        return taskEntitiesJson;
    }
    
    private JSONObject extractJsonObject(ArrayList<TaskEntity> taskList, int i) throws JSONException {
        JSONObject taskDetails = new JSONObject();
        
        Gson gson = new Gson();
        JSONParser parser = new JSONParser();
        
        try {
            taskDetails = (JSONObject)parser.parse(gson.toJson(taskList.get(i)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return taskDetails;
    }
    
    public void convertFromJson(JSONArray taskEntitiesJson) {
        Gson gson = new Gson();
        
        for (int i = 0; i < taskEntitiesJson.length(); i++) {
            //JSONObject currentTask = taskEntitiesJson.getJSONObject(i);
            
            //int id = gson.fromJson(String.valueOf(currentTask.get("id")), int.class);
            
        }
        
    }
}
