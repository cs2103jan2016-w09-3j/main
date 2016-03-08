package fileStorage;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
    
    public ArrayList<TaskEntity> convertFromJson(JSONArray taskEntitiesJson) {
        ArrayList<TaskEntity> retrievedTaskList = new ArrayList<TaskEntity>();
        
        for (int i = 0; i < taskEntitiesJson.length(); i++) {
            extractJavaObject(taskEntitiesJson, retrievedTaskList, i);
        }
        return retrievedTaskList;
    }

    private void extractJavaObject(JSONArray taskEntitiesJson, ArrayList<TaskEntity> retrievedTaskList, int i) {
        Gson gson = new Gson();
        JSONObject currentTask = null;
        try {
            currentTask = taskEntitiesJson.getJSONObject(i);
            String taskName = gson.fromJson(String.valueOf(currentTask.get("_name")), String.class);
            String taskDesc = gson.fromJson(String.valueOf(currentTask.get("_description")), String.class);
            Calendar dueDate = gson.fromJson(String.valueOf(currentTask.get("_duedate")), Calendar.class);
            //boolean isFloating = gson.fromJson(String.valueOf(currentTask.get("_isFloating")), boolean.class);
            //boolean isFullDay = gson.fromJson(String.valueOf(currentTask.get("_isFullDay")), boolean.class);
            TaskEntity retrievedTask = new TaskEntity(taskName, taskDesc);
            retrievedTaskList.add(retrievedTask);
            } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
