package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import entity.TaskEntity;

public class FileManager {
    
    private File file;
    private String fileName;
    private ArrayList<TaskEntity> mainTaskEntities;
    private ArrayList<TaskEntity> floatingTaskEntities;
    private JSONArray allTaskEntities;
    
    public FileManager(ArrayList<TaskEntity> _main, ArrayList<TaskEntity> _floating) {
        fileName = "taskList.txt";
        mainTaskEntities = new ArrayList<TaskEntity>(_main);
        floatingTaskEntities = new ArrayList<TaskEntity>(_floating);
        allTaskEntities = new JSONArray();
    }
    
    public void init() throws JSONException, IOException {
        processFile();
        allTaskEntities.put(convertToJson(mainTaskEntities));
        allTaskEntities.put(convertToJson(floatingTaskEntities));
        writeToFile(allTaskEntities);
    }
    
    private JSONArray convertToJson(ArrayList<TaskEntity> taskEntities) throws JSONException {
        JSONArray taskEntitiesJson = new JSONArray();
        
        for(int i = 0; i< taskEntities.size(); i++) {
            taskEntitiesJson.put(extractDetails(taskEntities, i));
        }
        
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
        
        /*taskDetails.put("id", taskList.get(i).getId());
        taskDetails.put("name", taskList.get(i).getName());
        taskDetails.put("dueDate", taskList.get(i).getDueDate());
        taskDetails.put("dateCreated", taskList.get(i).getDateCreated());
        taskDetails.put("description", taskList.get(i).getDescription());
        taskDetails.put("floatingTask", taskList.get(i).isFloating());*/
        
        return taskDetails;
    }
    
    private void createNewFile() throws IOException {
        file = new File(fileName);
        file.createNewFile();
    }
    
    // TODO stub
    private void readFromExistingFile() throws IOException {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fileName));
            buffer.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
    
    private void processFile() throws IOException {
        if (hasExistingFile() == false) {
            createNewFile();
        } else {   
            readFromExistingFile();
        }
    }
    
    private boolean hasExistingFile() {
        // TODO Auto-generated method stub
        return false;
    }

    public void writeToFile(JSONArray taskListJson) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(taskListJson.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
