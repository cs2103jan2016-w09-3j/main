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
import org.json.simple.JSONObject;
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
    
    // Test function
    public static void main(String args[]) {
        FileManager myFM = new FileManager(createDummyMain(), createDummyFloating());
        try {
            myFM.init();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    // Test function
    private static ArrayList<TaskEntity> createDummyMain() {
        ArrayList<TaskEntity> dummyMainList = new ArrayList<TaskEntity>();
        dummyMainList.add(new TaskEntity("firstTask"));
        dummyMainList.add(new TaskEntity("secondTask"));
        return dummyMainList;
    }
    
    // Test function
    private static ArrayList<TaskEntity> createDummyFloating() {
        ArrayList<TaskEntity> dummyFloatingList = new ArrayList<TaskEntity>();
        dummyFloatingList.add(new TaskEntity("floatingTaskOne"));
        dummyFloatingList.add(new TaskEntity("floatingTaskTwo"));
        return dummyFloatingList;
    }
    
    public FileManager() {
        fileName = "taskList.txt";
        //mainTaskEntities = new ArrayList<TaskEntity>();
        //floatingTaskEntities = new ArrayList<TaskEntity>();
        //allTaskEntities = new JSONArray();
    }
    
    public FileManager(ArrayList<TaskEntity> _main, ArrayList<TaskEntity> _floating) {
        fileName = "taskList.txt";
        mainTaskEntities = new ArrayList<TaskEntity>(_main);
        floatingTaskEntities = new ArrayList<TaskEntity>(_floating);
        allTaskEntities = new JSONArray();
    }
    
    public void init() throws JSONException, IOException {
        System.out.println("Initialising....");
        processFile();
        allTaskEntities.put(convertToJson(mainTaskEntities));
        allTaskEntities.put(convertToJson(floatingTaskEntities));
        writeToFile(allTaskEntities);
    }
    
    private void createNewFile() throws IOException {
        file = new File(fileName);
        file.createNewFile();
        
        System.out.println("Creating new file...");
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
        if(file.exists() && file.canRead()) {
            return true;
        }
        return false;
    }

    private void writeToFile(JSONArray taskListJson) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        System.out.println("Writing to file...");
        fileWriter.write(taskListJson.toString());
        fileWriter.flush();
        fileWriter.close();
        System.out.println("Wrote to file. Terminate.");
    }
}
