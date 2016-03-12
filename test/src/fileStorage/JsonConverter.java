package fileStorage;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import entity.AllTaskLists;
import entity.TaskEntity;

public class JsonConverter {

    public JsonConverter() {
        
    }
    
    public String javaToJson(AllTaskLists allLists) {
        ArrayList<TaskEntity> mainTaskList = allLists.getMainTaskList();
        ArrayList<TaskEntity> floatingTaskList = allLists.getFloatingTaskList();
        ArrayList<TaskEntity> appendedList = new ArrayList<TaskEntity>();
        
        appendedList.addAll(mainTaskList);
        appendedList.addAll(floatingTaskList);
        
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls();
        Gson gson = gsonBuilder.create();
        
        String appendedToJson = gson.toJson(appendedList);
        
        return appendedToJson;
    }
    
    public AllTaskLists jsonToJava(String readData) {
        Gson gson = new Gson();

        // Load json string into custom object
        ArrayList<TaskEntity> allTasks = gson.fromJson(readData, new TypeToken<ArrayList<TaskEntity>>(){}.getType());
        
        ArrayList<TaskEntity> mainTaskList = new ArrayList<TaskEntity>();
        ArrayList<TaskEntity> floatingTaskList = new ArrayList<TaskEntity>();
        
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