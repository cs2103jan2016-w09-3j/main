package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
    
    private String filePath;
    private File storedLists;
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public FileHandler() {
        filePath = "taskLists.txt";
        processFile();
    }
    
    public boolean writeToFile(String input) {
        FileWriter fileWriter;
        long timeBeforeModification = storedLists.lastModified();
        long timeAfterModification = -1;
        try {
            fileWriter = new FileWriter(filePath);        
            fileWriter.write(input);
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

    public String readFromExistingFile() {        
        BufferedReader buffer;
        String readData = "";
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readData = readData + currentLine;
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