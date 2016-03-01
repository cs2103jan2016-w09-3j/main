package fileStorage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.sun.javafx.scene.paint.GradientUtils.Parser;

import entity.TaskEntity;

// External JARs: json-simple-1.1.1.jar, java-json.jar and gson-2.2.2.jar
// Go update the pom.xml file

public class FileManager {
    
    private RandomAccessFile file;
    private String fileName;
    private BufferedReader buffer;
    private ArrayList<TaskEntity> mainTaskEntities;
    private ArrayList<TaskEntity> floatingTaskEntities;
    
    public FileManager(ArrayList<TaskEntity> _main, ArrayList<TaskEntity> _floating) {
        fileName = "taskList.txt";
        mainTaskEntities = _main;
        floatingTaskEntities = _floating;
    }
    
    private void convertMainToJson(ArrayList<TaskEntity> mainTaskEntities) throws JSONException {
        JSONArray mainTaskEntitiesJson = new JSONArray();
        
        for(int i = 0; i< mainTaskEntities.size(); i++) {
            mainTaskEntitiesJson.put(extractDetails(mainTaskEntities, i));
        }
    }
    
    private void convertFloatingToJson(ArrayList<TaskEntity> floatingTaskEntities) throws JSONException {
        JSONArray floatingTaskEntitiesJson = new JSONArray();
        
        for(int i = 0; i< floatingTaskEntities.size(); i++) {
            floatingTaskEntitiesJson.put(extractDetails(floatingTaskEntities, i));
        }
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
    
    private void createNewFile() {
        try {
            file = new RandomAccessFile(fileName, "rw");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
    
    private void readFromExistingFile() {
        try {
            buffer = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
    }
    
    public void writeToFile(JSONObject task) {
        // TODO
    }
}
