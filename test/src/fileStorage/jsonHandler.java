package fileStorage;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import entity.TaskEntity;

public class jsonHandler {
    
    private JSONArray convertToJson(ArrayList<TaskEntity> taskEntities) throws JSONException {
        JSONArray taskEntitiesJson = new JSONArray();
        
        for(int i = 0; i< taskEntities.size(); i++) {
            taskEntitiesJson.put(extractDetails(taskEntities, i));
        }
        System.out.println("Converting arraylist to JSON.");
        return taskEntitiesJson;
    }
    
    private JSONObject extractDetails(ArrayList<TaskEntity> taskList, int i) throws JSONException {
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
    
    private ArrayList<String> convertFromJson() {
        
    }
}
