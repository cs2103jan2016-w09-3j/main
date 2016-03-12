package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainFileHandler {

    private String filePath;
    private File storedLists;

    public MainFileHandler() {
        filePath = "taskLists.txt";
        processFile();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // if file exists, use file. Else, create new file
    private void processFile() {
        storedLists = new File(filePath);

        if (storedLists.exists()) {
            readFromExistingFile();
            System.out.println("File found, begin reading...");
        } else {
            createNewFile(storedLists);
        }
    }

    private void createNewFile(File storedLists) {
        try {
            storedLists.createNewFile();
            System.out.println("Created new file.");
        } catch (IOException e) {
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
                readData = readData.trim() + currentLine.trim();
            }
            buffer.close();
            System.out.println("Read from file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readData.trim();
    }

    public boolean writeToFile(String data) {
        FileWriter fileWriter;
        long beforeModify = storedLists.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(filePath); 
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
            afterModify = storedLists.lastModified();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isModified(beforeModify, afterModify);
    }

    private boolean isModified(long timeBeforeModification, long timeAfterModification) {
        return timeAfterModification > timeBeforeModification;
    }
}