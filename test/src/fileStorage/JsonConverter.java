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

    /**
     * Converts Java Object to Json format via the Gson builder
     * String returned will include nulls for empty fields 
     * @param allLists
     * @return String in Json format
     */
    public String javaToJson(AllTaskLists allLists) {
        ArrayList<TaskEntity> mainTaskList = allLists.getMainTaskList();
        ArrayList<TaskEntity> floatingTaskList = allLists.getFloatingTaskList();
        ArrayList<TaskEntity> appendedList = new ArrayList<TaskEntity>();

        appendedList.addAll(mainTaskList);
        appendedList.addAll(floatingTaskList);

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls();
        Gson gson = gsonBuilder.create();

        String appendedToJson = gson.toJson(appendedList);
        assert appendedToJson != null;

        return appendedToJson;
    }

    /**
     * Converts Json format to Java Object via the Gson library
     * @param input
     * @return AllTaskLists
     */
    public AllTaskLists jsonToJava(String input) {
        Gson gson = new Gson();
        AllTaskLists allLists;
        
        // Load JSON string into custom object using TypeToken
        ArrayList<TaskEntity> allTasks = gson.fromJson(input, new TypeToken<ArrayList<TaskEntity>>(){}.getType());

        if (allTasks != null) {
            ArrayList<TaskEntity> mainTaskList = new ArrayList<TaskEntity>();
            ArrayList<TaskEntity> floatingTaskList = new ArrayList<TaskEntity>();

            for (int i = 0; i < allTasks.size(); i++) {
                if(allTasks.get(i).isFloating() == true) {
                    floatingTaskList.add(allTasks.get(i));
                } else {
                    mainTaskList.add(allTasks.get(i));
                }
            }
            allLists = new AllTaskLists(mainTaskList, floatingTaskList);
        } else {
            allLists = new AllTaskLists();
        }

        return allLists;
    }
}