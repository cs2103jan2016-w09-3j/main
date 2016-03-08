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
    }
    
    public FileManager(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating) {
        fileName = "taskList.txt";
        mainTaskEntities = new ArrayList<TaskEntity>(main);
        floatingTaskEntities = new ArrayList<TaskEntity>(floating);
        allTaskEntities = new JSONArray();
    }
    
    public void init() throws JSONException, IOException {
        System.out.println("Initialising....");
        processFile();
        JsonHandler json = new JsonHandler();
        allTaskEntities.put(json.convertToJson(mainTaskEntities));
        allTaskEntities.put(json.convertToJson(floatingTaskEntities));
        writeToFile(allTaskEntities);
    }
    
    private void createNewFile() throws IOException {
        file = new File(fileName);
        file.createNewFile();
        
        System.out.println("Creating new file...");
    }
    
    // TODO 
    private void readFromExistingFile() throws IOException {
        ArrayList<String> storedTasks = new ArrayList<String>();
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fileName));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                storedTasks.add(currentLine);
            }
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
