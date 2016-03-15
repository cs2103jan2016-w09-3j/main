package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFileHandler {

    private String filePath;
    private File storedLists;
    private String allStoredTasks;
    private Logger logger;

    public MainFileHandler() {
        filePath = "taskLists.txt";
        processFile();
    }

    public String getAllStoredTasks() {
        return allStoredTasks;
    }

    public void setAllStoredTasks(String allStoredTasks) {
        this.allStoredTasks = allStoredTasks;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads and stores data from existing file if any, creates a new file otherwise
     */
    private void processFile() {
        storedLists = new File(filePath);

        if (storedLists.exists()) {
            setAllStoredTasks(readFromExistingFile());
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
    
    /**
     * Reads data from an existing file and returns the appended string
     * @return String
     */
    public String readFromExistingFile() {        
        BufferedReader buffer;
        String readData = "";
        try {
            buffer = new BufferedReader(new FileReader(filePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readData = readData + currentLine.trim();
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
    
    /**
     * Returns true if data is written to a file, false otherwise
     * Whether the data has been written depends on the last modified time of the file
     * @param data
     * @return boolean
     */
    public boolean writeToFile(String data) {
        logger.log(Level.INFO, "Start processing...");
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
            logger.log(Level.WARNING, "IOException");
        }
        logger.log(Level.INFO, "End processing...");
        return isModified(beforeModify, afterModify);
    }

    private boolean isModified(long timeBeforeModification, long timeAfterModification) {
        return timeAfterModification > timeBeforeModification;
    }
}