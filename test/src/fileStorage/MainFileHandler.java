package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainFileHandler {
    
    private String filePath;
    private File storedLists;
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public MainFileHandler() {
        filePath = "taskLists.txt";
        processFile();
    }
    
    public boolean writeToFile(ArrayList<String> input) {
        FileWriter fileWriter;
        long timeBeforeModification = storedLists.lastModified();
        long timeAfterModification = -1;
        try {
            fileWriter = new FileWriter(filePath); 
            for (int i = 0; i < input.size(); i++) {
                fileWriter.write(input.get(i) + "\n");
            }
            fileWriter.flush();
            fileWriter.close();
            timeAfterModification = storedLists.lastModified();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean isModified = timeAfterModification > timeBeforeModification;
        return isModified;
    }

    // if file exists, use file. Else, create new file
    private void processFile() {
        storedLists = new File(filePath);
        
        if (storedLists.exists()) {
            System.out.println("File found, begin reading...");
        } else {
            createNewFile(storedLists);
        }
    }

    private void createNewFile(File storedLists) {
        try {
            storedLists.createNewFile();
            System.out.println("Created new file");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<String> readFromExistingFile() {        
        BufferedReader buffer;
        ArrayList<String> readData = new ArrayList<String>();
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readData.add(currentLine);
            }
            buffer.close();
            System.out.println("Read from file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return readData;
    }
}