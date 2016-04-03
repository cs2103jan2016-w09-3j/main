package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

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

    private static final int READ_FROM_CONFIG_FILE = 1;
    private static final int READ_FROM_MAIN_FILE = 2;
    private static final int READ_FROM_BACK_UP_FILE = 3;
    private static final int WRITE_TO_CONFIG_FILE = 1;
    private static final int WRITE_TO_MAIN_FILE = 2;
    private static final int WRITE_TO_BACK_UP_FILE = 3;

    public StorageHandler() {
        allCommandsQueue = new LinkedList<String>();
        processFile();
    }

    public String getMainFilePath() {
        return tasksFilePath;
    }

    public void setMainFilePath(String filePath) {
        this.tasksFilePath = filePath;
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

    //============================================================================
    // Initialising, creating new files
    // ===========================================================================

    /**
     * Reads and stores data from existing file if any, creates a new file otherwise
     */
    public void processFile() {
        initConfigFile(); 
        initMainFile();        
        initBackUpFile();
        initCommandFile();
    }

    private void initCommandFile() {
        commandsFilePath = "commandsList.txt";
        commandsFile = new File(commandsFilePath);
        if (isExists(commandsFile)) {
            setAllCommandsQueue(readFromExistingCommandFile());
            System.out.println("Command file found, begin reading...");
            System.out.println("Queue size " + getAllCommandsQueue().size());
        } else {
            System.out.println("New command file created.");
            createNewFile(commandsFile);
        }
    }

    private void initBackUpFile() {
        backUpTasksFilePath = "backUpTasksList.txt";
        backUpTasksFile = new File(backUpTasksFilePath);
        // Temporary back up file to be deleted after every session
        if(isExists(backUpTasksFile) == true) {
            deleteBackUpFile();
        }
        System.out.println("New back up created.");
        createNewFile(backUpTasksFile);
        copyToBackUp();
    }

    private void initMainFile() {
        if (isExists(tasksFile)) {
            setAllStoredTasks(readFromExistingFile(READ_FROM_MAIN_FILE));
            System.out.println("Main file found, begin reading...");
        } else {
            System.out.println("New main file created.");
            createNewFile(tasksFile);
        }
    }

    private void initConfigFile() {
        configFilePath = "configFile.txt";
        configFile = new File(configFilePath);
        if (isExists(configFile)) {
            setMainFilePath(readFromExistingFile(READ_FROM_CONFIG_FILE));
            tasksFile = new File(tasksFilePath);
            System.out.println(tasksFilePath);
            System.out.println("Config file found, begin reading...");
        } else {
            createNewFile(configFile);
            tasksFilePath = "tasksList.txt";
            tasksFile = new File(tasksFilePath);
            setMainFilePath(tasksFile.getAbsolutePath());
            writeToFile(tasksFilePath, WRITE_TO_CONFIG_FILE);
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

    //============================================================================
    // Reading from existing files
    // ===========================================================================

    /**
     * Reads data from an existing file and returns the appended string
     * @return String
     */
    public String readFromExistingFile(int fromFile) {        
        BufferedReader buffer;
        String readData = "";
        try {
            buffer = identifyReadFrom(fromFile);
            String currentLine = "";
            while ((currentLine = buffer.readLine()) != null) {
                readData = readData + currentLine + "\n";
            }
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readData.trim();
    }

    private BufferedReader identifyReadFrom(int fromFile) throws FileNotFoundException {
        BufferedReader buffer;
        if (fromFile == READ_FROM_MAIN_FILE) {
            buffer = new BufferedReader(new FileReader(tasksFilePath));
        } else if (fromFile == READ_FROM_BACK_UP_FILE){
            buffer = new BufferedReader(new FileReader(backUpTasksFilePath));
        } else {
            // Read from config file
            buffer = new BufferedReader(new FileReader(configFilePath));
        }
        return buffer;
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

    public boolean copyToBackUp() {
        allBackUpTasks = readFromExistingFile(READ_FROM_MAIN_FILE);
        return writeToFile(allBackUpTasks, WRITE_TO_BACK_UP_FILE);
    }

    //============================================================================
    // Writing to files
    // ===========================================================================

    /**
     * Returns true if data is written to a file, false otherwise
     * Whether the data has been written depends on the last modified time of the file
     * @param data
     * @return boolean
     */
    public boolean writeToFile(String data, int toFile) {
        File file;
        String filePath;
        if (toFile == WRITE_TO_MAIN_FILE) {
            file = new File(tasksFilePath);
            filePath = tasksFilePath;
        } else if (toFile == WRITE_TO_BACK_UP_FILE) {
            file = backUpTasksFile;
            filePath = backUpTasksFilePath;
        } else {
            file = configFile;
            filePath = configFilePath;
        }
        FileWriter fileWriter;
        long beforeModify = file.lastModified();
        long afterModify = -1;
        try {
            fileWriter = new FileWriter(filePath); 
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
            afterModify = file.lastModified();
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

    /**
     * Returns true if timeAfterMod is after timeBeforeMod, false otherwise
     * @param timeBeforeModification
     * @param timeAfterModification
     * @return boolean
     */
    private boolean isModified(long timeBeforeModification, long timeAfterModification) {
        return timeAfterModification > timeBeforeModification;
    }

    //============================================================================
    // Clearing and removing files
    // ===========================================================================

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

    public boolean changeDirectory(String newFilePath) {
        boolean isChanged = false;

        File oldFile = new File(tasksFilePath);
        String transferData = readFromExistingFile(READ_FROM_MAIN_FILE);

        File newFile = new File(newFilePath);
        if (newFile.isDirectory() == false) {
            if (newFile.getParentFile() != null) {
                isChanged = newFile.getParentFile().mkdirs();
                System.out.println("Creating dir: " + isChanged);
            }
        } 
        try {
            isChanged = newFile.createNewFile();
            System.out.println("Create new file: " + isChanged);
            setMainFilePath(newFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeToFile(transferData, WRITE_TO_MAIN_FILE);
        writeToFile(newFile.getAbsolutePath(), WRITE_TO_CONFIG_FILE);
        oldFile.delete();
        return isChanged;
    }
}