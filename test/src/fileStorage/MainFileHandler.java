package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFileHandler {

    private String tasksFilePath;
    private String commandsFilePath;

    private File tasksFile;
    private File commandsFile;

    private String allStoredTasks;
    private Queue<String> allCommandsQueue;

    private static Logger logger = Logger.getLogger("MainFileHandler");
    private FileHandler fileHandler;

    public MainFileHandler() {
        tasksFilePath = "tasksList.txt";
        commandsFilePath = "commandsList.txt";
        processFile();
    }

    public String getMainFilePath() {
        return tasksFilePath;
    }

    public void setMainFilePath(String filePath) {
        this.tasksFilePath = filePath;
    }

    public String getCommandFilePath() {
        return commandsFilePath;
    }

    public void setCommandFilePath(String filePath) {
        this.commandsFilePath = filePath;
    }

    public String getAllStoredTasks() {
        return allStoredTasks;
    }

    public void setAllStoredTasks(String allStoredTasks) {
        this.allStoredTasks = allStoredTasks;
    }

    public Queue<String> getAllCommandsQueue() {
        return allCommandsQueue;
    }

    public void setAllCommandsQueue(Queue<String> allCommandsQueue) {
        this.allCommandsQueue = allCommandsQueue;
    }

    /**
     * Reads and stores data from existing file if any, creates a new file otherwise
     */
    private void processFile() {
        tasksFile = new File(tasksFilePath);

        if (tasksFile.exists()) {
            setAllStoredTasks(readFromExistingMainFile());
            System.out.println("Main file found, begin reading...");
        } else {
            createNewFile(tasksFile);
        }

        if (commandsFile.exists()) {
            setAllCommandsQueue(readFromExistingCommandFile());
            System.out.println("Command file found, begin reading...");
        } else {
            createNewFile(commandsFile);
        }
    }

    private void createNewFile(File file) {
        try {
            file.createNewFile();
            System.out.println("Created new file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads data from an existing file and returns the appended string
     * @return String
     */
    private String readFromExistingMainFile() {        
        BufferedReader buffer;
        String readData = "";
        try {
            buffer = new BufferedReader(new FileReader(tasksFilePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readData = readData + currentLine.trim();
            }
            buffer.close();
            System.out.println("Tasks: Read from file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readData.trim();
    }

    private Queue<String> readFromExistingCommandFile() {
        Queue<String> readCommands = new LinkedList<String>();
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(commandsFilePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readCommands.offer(currentLine);
            }
            buffer.close();
            System.out.println("Commands: Read from file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readCommands;
    }

    /**
     * Returns true if data is written to a file, false otherwise
     * Whether the data has been written depends on the last modified time of the file
     * @param data
     * @return boolean
     */
    public boolean writeToFile(String data) {
        FileWriter fileWriter;
        long beforeModify = tasksFile.lastModified();
        long afterModify = -1;
        try {
            fileHandler = new FileHandler("storageLogFile.log");
            logger.addHandler(fileHandler);
            logger.log(Level.INFO, "Start processing...");
            fileWriter = new FileWriter(tasksFilePath); 
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
            afterModify = tasksFile.lastModified();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "IOException");
        }
        logger.log(Level.INFO, "End processing...");
        return isModified(beforeModify, afterModify);
    }

    /**
     * Returns true if data is written to a file, false otherwise
     * Whether the data has been written depends on the last modified time of the file
     * @param writeCommands
     * @return boolean
     */
    public boolean writeToCommandFile(Queue<String> writeCommands) {
        FileWriter fileWriter;
        long beforeModify = commandsFile.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(commandsFilePath); 
            while (!writeCommands.isEmpty()) {
                fileWriter.write(writeCommands.poll());
            }
            fileWriter.flush();
            fileWriter.close();
            afterModify = commandsFile.lastModified();
        } catch (IOException e) {
            e.printStackTrace();  
        }
        return isModified(beforeModify, afterModify); 
    }


    /**
     * Returns true if timeAfterMod is after timeBeforeMod, false otherwise
     * @param timeBeforeModification
     * @param timeAfterModification
     * @return boolean
     */
    private boolean isModified(long timeBeforeModification, long timeAfterModification) {
        return timeAfterModification > timeBeforeModification;
    }
}