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

public class StorageHandler {
    
    private String configFilePath;
    private String tasksFilePath;
    private String commandsFilePath;
    private String backUpTasksFilePath;
    
    private File configFile;
    private File tasksFile;
    private File commandsFile;
    private File backUpTasksFile;

    private String allStoredTasks;
    private String allBackUpTasks;
    private Queue<String> allCommandsQueue;

    private static Logger logger = Logger.getLogger("MainFileHandler");
    private FileHandler fileHandler;
    
    private static final int READ_FROM_MAIN_FILE = 1;
    private static final int READ_FROM_BACK_UP_FILE = 2;

    public StorageHandler() {
        tasksFilePath = "tasksList.txt";
        commandsFilePath = "commandsList.txt";
        backUpTasksFilePath = "backUpTasksList.txt";
        configFilePath = "configFile.txt";
        allCommandsQueue = new LinkedList<String>();
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
    
    public String getAllBackUpTasks() {
        return allBackUpTasks;
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
    public void processFile() {
        configFile = new File(configFilePath);
        tasksFile = new File(tasksFilePath);
        commandsFile = new File(commandsFilePath);
        backUpTasksFile = new File(backUpTasksFilePath);

        if (isExists(configFile)) {
            setAllStoredTasks(readFromExistingMainFile(READ_FROM_MAIN_FILE));
            System.out.println("Main file found, begin reading...");
        } else {
            createNewFile(tasksFile);
        }
        
        if (isExists(tasksFile)) {
            setAllStoredTasks(readFromExistingMainFile(READ_FROM_MAIN_FILE));
            System.out.println("Main file found, begin reading...");
        } else {
            createNewFile(tasksFile);
        }
        
        // Temporary back up file to be deleted after every session
        if(isExists(backUpTasksFile) == true) {
            deleteBackUpFile();
        }
        createNewFile(backUpTasksFile);
        copyToBackUp();

        if (isExists(commandsFile)) {
            setAllCommandsQueue(readFromExistingCommandFile());
            System.out.println("Command file found, begin reading...");
            System.out.println("Queue size " + getAllCommandsQueue().size());
        } else {
            createNewFile(commandsFile);
        }
    }

    public boolean isExists(File file) {
        return file.exists();
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
    public String readFromExistingMainFile(int fromFile) {        
        BufferedReader buffer;
        String readData = "";
        try {
            if (fromFile == READ_FROM_MAIN_FILE) {
                buffer = new BufferedReader(new FileReader(tasksFilePath));
            } else {
                // Read from back up file
                buffer = new BufferedReader(new FileReader(backUpTasksFilePath));
            }
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readData = readData + currentLine + "\n";
            }
            buffer.close();
            System.out.println("Tasks: Read from file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readData;
    }

    /**
     * Reads commands from an existing file and returns the queue
     * @return Queue<String>
     */
    public Queue<String> readFromExistingCommandFile() {
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
    
    public void readFromExistingConfigFile() {
        String readPaths = "";
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(configFilePath));
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readPaths = readPaths + currentLine;
            }
            buffer.close();
            String[] readPathsSplit = readPaths.split("\n");
            setMainFilePath(readPathsSplit[0]);
            setCommandFilePath(readPathsSplit[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean copyToBackUp() {
        allBackUpTasks = readFromExistingMainFile(READ_FROM_MAIN_FILE);
        return writeToMainFile(allBackUpTasks, backUpTasksFile, backUpTasksFilePath);
    }

    /**
     * Returns true if data is written to a file, false otherwise
     * Whether the data has been written depends on the last modified time of the file
     * @param data
     * @return boolean
     */
    public boolean writeToMainFile(String data) {
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
    
    public boolean writeToMainFile(String data, File destFile, String destFilePath) {
        FileWriter fileWriter;
        long beforeModify = destFile.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(destFilePath); 
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
            afterModify = destFile.lastModified();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isModified(beforeModify, afterModify);
    }

    /**
     * Returns true if commands written to a file, false otherwise
     * Whether the commands have been written depends on the last modified time of the file
     * @param writeCommands
     * @return boolean
     */
    public boolean writeToCommandFile(String command) {
        FileWriter fileWriter;
        long beforeModify = commandsFile.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(commandsFilePath, true); // True to append to file
            fileWriter.write(command + '\n');
            fileWriter.flush();
            fileWriter.close();
            afterModify = commandsFile.lastModified();
        } catch (IOException e) {
            e.printStackTrace();  
        }
        return isModified(beforeModify, afterModify); 
    }
    
    public boolean writeToConfigFile() {
        FileWriter fileWriter;
        long beforeModify = configFile.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(configFilePath); 
            fileWriter.write(getMainFilePath() + '\n' + getCommandFilePath());
            fileWriter.flush();
            fileWriter.close();
            afterModify = configFile.lastModified();
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

    /**
     * Clears command file upon committing
     */
    public void clearCommandFileUponCommit() {
        clearCommandFile();
        allCommandsQueue.clear();
    }
    
    /**
     * Clears command file upon committing
     */
    public void clearCommandFile() {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(commandsFilePath);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteBackUpFile() {
        backUpTasksFile.delete();
    }
}