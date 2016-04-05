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

    private String themeName;

    private static final int READ_FROM_CONFIG_FILE = 1;
    private static final int READ_FROM_MAIN_FILE = 2;
    private static final int READ_FROM_BACK_UP_FILE = 3;
    private static final int WRITE_TO_CONFIG_FILE = 1;
    private static final int WRITE_TO_MAIN_FILE = 2;
    private static final int WRITE_TO_BACK_UP_FILE = 3;
    
    private static final String CONFIG_FILE_NAME = "configFile.txt";
    private static final String MAIN_FILE_NAME = "mainTasksFile.txt";
    private static final String BACK_UP_FILE_NAME = "backUpTasksFile.txt";
    private static final String COMMAND_FILE_NAME = "commandsFile.txt";
    private static final String DEFAULT_THEME = "default";
    private static final String NEW_LINE = "\n";
    
    public StorageHandler() {
        allCommandsQueue = new LinkedList<String>();
        processFile();
    }

    //============================================================================
    // Getters and setters
    // ===========================================================================

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

    public String getThemeName() {
        return themeName;
    }
        
    public void setThemeName(String themeName) {
        this.themeName = themeName;
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
    
    private void initConfigFile() {
        configFilePath = CONFIG_FILE_NAME;
        configFile = new File(configFilePath);
        
        if (isExists(configFile)) {
            extractConfigSettings();
            tasksFile = new File(tasksFilePath);
        } else {
            createNewFile(configFile);
            tasksFilePath = MAIN_FILE_NAME;
            tasksFile = new File(tasksFilePath);
            themeName = DEFAULT_THEME;
            writeConfigSettings();
        }
    }

    private boolean writeConfigSettings() {
        return identifyWriteTo(tasksFilePath + NEW_LINE + themeName, WRITE_TO_CONFIG_FILE);
    }

    private void extractConfigSettings() {
        String settings = readFromExistingFile(READ_FROM_CONFIG_FILE);
        String[] settingsSplit = settings.split("\n");
        setMainFilePath(settingsSplit[0]);
        setThemeName(settingsSplit[1]);
    }
        
    private void initMainFile() {
        if (isExists(tasksFile)) {
            setAllStoredTasks(readFromExistingFile(READ_FROM_MAIN_FILE));
        } else {
            makeNewDirectory(tasksFile);
            createNewFile(tasksFile);
        }
    }
       
    private void initBackUpFile() {
        backUpTasksFilePath = BACK_UP_FILE_NAME;
        backUpTasksFile = new File(backUpTasksFilePath);
        
        // Temporary back up file to be deleted after every session
        if(isExists(backUpTasksFile) == true) {
            deleteBackUpFile();
        }
        
        createNewFile(backUpTasksFile);
        copyToBackUp();
    }
    
    private void initCommandFile() {
        commandsFilePath = COMMAND_FILE_NAME;
        commandsFile = new File(commandsFilePath);
        
        if (isExists(commandsFile)) {
            setAllCommandsQueue(readFromExistingCommandFile());
        } else {
            createNewFile(commandsFile);
        }
    }
   
    private boolean createNewFile(File file) {
        boolean isCreated = false;
        
        try {
            isCreated = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isCreated;
    }
    
    private boolean makeNewDirectory(File file) {
        boolean isCreated = false;
        
        if (hasDirectory(file) == false) {
            if (file.getParentFile() != null) {
                isCreated = file.getParentFile().mkdirs();
            }
        }
        return isCreated;
    }

    private boolean hasDirectory(File file) {
        return file.isDirectory();
    }

    public boolean isExists(File file) {
        return file.exists();
    }

    //============================================================================
    // Reading from existing files
    // ===========================================================================

    private BufferedReader identifyReadFrom(int fromFile) throws FileNotFoundException {
        BufferedReader buffer = null;
        switch (fromFile) {
            case READ_FROM_MAIN_FILE:
                buffer = new BufferedReader(new FileReader(tasksFilePath));
                break;
            case READ_FROM_BACK_UP_FILE:
                buffer = new BufferedReader(new FileReader(backUpTasksFilePath));
                break;
            case READ_FROM_CONFIG_FILE:
                buffer = new BufferedReader(new FileReader(configFilePath));
                break;
        }
        return buffer;
    }
    
    /**
     * Reads data from an existing file and returns the appended string
     * @return String
     */
    public String readFromExistingFile(int fromFile) {        
        BufferedReader buffer;
        String readData = "";
        try {
            buffer = identifyReadFrom(fromFile);
            readData = readString(buffer, readData);
            buffer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readData.trim();
    }

    private String readString(BufferedReader buffer, String readData) throws IOException {
        String currentLine;
        while ((currentLine = buffer.readLine()) != null) {
            readData = readData + currentLine + "\n";
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
            readQueue(readCommands, buffer);
            buffer.close();
            System.out.println("Commands: Read from file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readCommands;
    }

    private void readQueue(Queue<String> readCommands, BufferedReader buffer) throws IOException {
        String currentLine;
        while ((currentLine = buffer.readLine()) != null) {
            readCommands.offer(currentLine);
        }
    }

    public boolean copyToBackUp() {
        allBackUpTasks = readFromExistingFile(READ_FROM_MAIN_FILE);
        return identifyWriteTo(allBackUpTasks, WRITE_TO_BACK_UP_FILE);
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
    public boolean identifyWriteTo(String data, int toFile) {
        File file = null;
        String filePath = null; 
        switch (toFile) {
            case WRITE_TO_MAIN_FILE:
                file = new File(tasksFilePath);
                filePath = tasksFilePath;
                break;
            case WRITE_TO_BACK_UP_FILE:
                file = backUpTasksFile;
                filePath = backUpTasksFilePath;
                break;
            case WRITE_TO_CONFIG_FILE:
                file = configFile;
                filePath = configFilePath;
                break;
        }
        return isWritten(data, file, filePath);
    }

    private boolean isWritten(String data, File file, String filePath) {
        long beforeModify = file.lastModified();
        long afterModify = -1;
        afterModify = writeToFile(data, file, filePath, afterModify);
        return isModified(beforeModify, afterModify);
    }

    private long writeToFile(String data, File file, String filePath, long afterModify) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(filePath); 
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
            afterModify = file.lastModified();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return afterModify;
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
    
    //============================================================================
    // Changing file directory
    // ===========================================================================

    public boolean changeDirectory(String newFilePath) {
        boolean isChanged = false;
        File newFile = new File(newFilePath);
        
        if (newFile.exists() == false) {
            isChanged = makeNewDirectory(newFile);
            isChanged = createNewFile(newFile);
            
            String transferData = readFromExistingFile(READ_FROM_MAIN_FILE);
            setMainFilePath(newFile.getAbsolutePath());
            tasksFile = newFile;
            setAllStoredTasks(transferData);
            identifyWriteTo(transferData, WRITE_TO_MAIN_FILE);
        }         
        isChanged = writeConfigSettings();
        return isChanged;
    }
    
    public boolean loadFromExistingFile(String newFilePath) {
        boolean isLoaded = false;
        File newFile = new File(newFilePath);
        
        if (isExists(newFile)) {
            isLoaded = true;
            setMainFilePath(newFile.getAbsolutePath());
            tasksFile = newFile;
            setAllStoredTasks(readFromExistingFile(READ_FROM_MAIN_FILE));
            writeConfigSettings();
        }
        return isLoaded;
    }
    
    public boolean resetToDefaultSettings() {
        setMainFilePath(MAIN_FILE_NAME);
        tasksFile = new File(tasksFilePath);
        initMainFile();
        return loadFromExistingFile(MAIN_FILE_NAME);
    }

    //============================================================================
    // Saving theme preference
    // ===========================================================================

    public boolean saveThemeName(String themeName) {
        setThemeName(themeName);
        return writeConfigSettings();
    }
}