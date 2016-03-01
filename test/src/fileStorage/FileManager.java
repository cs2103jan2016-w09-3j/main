package fileStorage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import entity.TaskEntity;

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
    
    public void extractDetails() throws JSONException {
        for(int i = 0; i< mainTaskEntities.size(); i++) {
            convertToJson(i);
        }
    }
    
    public void convertToJson(int i) throws JSONException {
        JSONObject taskDetails = new JSONObject();
        
        taskDetails.put("id", mainTaskEntities.get(i).getId());
        taskDetails.put("name", mainTaskEntities.get(i).getName());
        taskDetails.put("dueDate", mainTaskEntities.get(i).getDueDate());
        taskDetails.put("dateCreated", mainTaskEntities.get(i).getDateCreated());
        taskDetails.put("description", mainTaskEntities.get(i).getDescription());
        taskDetails.put("floatingTask", mainTaskEntities.get(i).isFloating());
        
        writeToFile(taskDetails);
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
    
    private void writeToFile(JSONObject task) {
        // TODO
    }
}
