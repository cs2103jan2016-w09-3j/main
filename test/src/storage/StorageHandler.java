/**
 * @author Angie A0126357A
 * @@author Angie A0126357A
 * 
 *          StorageHandler to handle reading, writing and changing directory of
 *          files. Four files involved: Configuration File, Main Task File, Back
 *          Up Task File and Command File.
 */

package storage;

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

import entity.ResultSet;

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
    private Queue<String> allCommandsQueue = new LinkedList<String>();

    private String themeName;

    private Logger logger;
    private FileHandler fileHandler;

    private static final int READ_FROM_CONFIG_FILE = 1;
    private static final int READ_FROM_MAIN_FILE = 2;
    private static final int READ_FROM_BACK_UP_FILE = 3;
    private static final int WRITE_TO_CONFIG_FILE = 1;
    private static final int WRITE_TO_MAIN_FILE = 2;
    private static final int WRITE_TO_BACK_UP_FILE = 3;

    private static final int FILE_ALREADY_EXISTS = -1;
    private static final int RESULT_SET_FALSE = 0;

    private static final String CONFIG_FILE_NAME = "configFile.txt";
    private static final String MAIN_FILE_NAME = "mainTasksFile.txt";
    private static final String BACK_UP_FILE_NAME = "backUpTasksFile.txt";
    private static final String COMMAND_FILE_NAME = "commandsFile.txt";
    private static final String DEFAULT_THEME = "default";
    private static final String NEW_LINE = "\n";

    /**
     * Class constructor.
     */
    public StorageHandler() {
        initLogger();
        processFile();
    }

    // ============================================================================
    // Getters and setters
    // ============================================================================

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

    // ============================================================================
    // Initialising and creating new files
    // ============================================================================

    /**
     * Initialises Configuration File, Main Task File, Back Up Task File and
     * Command File.
     */
    private void processFile() {
        logger.log(Level.INFO, "Processing file...");
        initConfigFile();
        initMainFile();
        initBackUpFile();
        initCommandFile();
    }

    /**
     * Initialises Configuration File containing the absolute directory of the
     * Main Task File and theme name.
     */
    private void initConfigFile() {
        configFilePath = CONFIG_FILE_NAME;
        configFile = new File(configFilePath);

        if (isExists(configFile)) {
            extractConfigSettings();
            tasksFile = new File(tasksFilePath);
        } else {
            logger.log(Level.INFO, "Creating new config file...");
            createNewFile(configFile);
            tasksFilePath = MAIN_FILE_NAME;
            tasksFile = new File(tasksFilePath);
            setMainFilePath(tasksFile.getAbsolutePath());
            themeName = DEFAULT_THEME;
            writeConfigSettings();
        }
    }

    /**
     * Writes current settings into Configuration File.
     * 
     * @return isWritten returns true if settings successfully written into
     *         Configuration File, false otherwise.
     */
    private boolean writeConfigSettings() {
        return identifyWriteTo(tasksFilePath + NEW_LINE + themeName, WRITE_TO_CONFIG_FILE);
    }

    /**
     * Takes in settings from Configuration File and splits them into Main Task
     * File directory and theme name.
     */
    private void extractConfigSettings() {
        String settings = readFromExistingFile(READ_FROM_CONFIG_FILE);
        String[] settingsSplit = settings.split(NEW_LINE);
        setMainFilePath(settingsSplit[0]);
        setThemeName(settingsSplit[1]);
    }

    /**
     * Initialises Main Task File containing user's tasks.
     * This method creates a new file if an existing file is not found, or reads
     * from the existing file otherwise.
     */
    private void initMainFile() {
        if (isExists(tasksFile)) {
            setAllStoredTasks(readFromExistingFile(READ_FROM_MAIN_FILE));
        } else {
            logger.log(Level.INFO, "Creating new main file...");
            makeNewDirectory(tasksFile);
            createNewFile(tasksFile);
            setMainFilePath(tasksFile.getAbsolutePath());
        }
    }

    /**
     * Initialises Back Up Task File for 'Undo' function.
     * The Back Up Task File created is a temporary file that will be deleted
     * after every session.
     */
    private void initBackUpFile() {
        backUpTasksFilePath = BACK_UP_FILE_NAME;
        backUpTasksFile = new File(backUpTasksFilePath);

        // Temporary back up file to be deleted after every session
        if (isExists(backUpTasksFile) == true) {
            deleteBackUpFile();
        }
        logger.log(Level.INFO, "Creating new back up file...");
        createNewFile(backUpTasksFile);
        copyToBackUp();
    }

    /**
     * Initialises Command File storing all user's inputs for data recovery
     * purposes.
     */
    private void initCommandFile() {
        commandsFilePath = COMMAND_FILE_NAME;
        commandsFile = new File(commandsFilePath);

        if (isExists(commandsFile)) {
            setAllCommandsQueue(readFromExistingCommandFile());
        } else {
            logger.log(Level.INFO, "Creating new command file...");
            createNewFile(commandsFile);
        }
    }

    private boolean createNewFile(File file) {
        boolean isCreated = false;

        try {
            isCreated = file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Creating new file failed.");
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
        logger.log(Level.WARNING, "New directory is created: " + isCreated);
        return isCreated;
    }

    private boolean hasDirectory(File file) {
        return file.isDirectory();
    }

    public boolean isExists(File file) {
        return file.exists();
    }

    // ============================================================================
    // Reading from existing files
    // ============================================================================

    private BufferedReader identifyReadFrom(int fromFile) throws FileNotFoundException {
        BufferedReader buffer = null;
        switch (fromFile) {
            case READ_FROM_MAIN_FILE :
                logger.log(Level.INFO, "Reading from main file...");
                buffer = new BufferedReader(new FileReader(tasksFilePath));
                break;
            case READ_FROM_BACK_UP_FILE :
                logger.log(Level.INFO, "Reading from back up file...");
                buffer = new BufferedReader(new FileReader(backUpTasksFilePath));
                break;
            case READ_FROM_CONFIG_FILE :
                logger.log(Level.INFO, "Reading from config file...");
                buffer = new BufferedReader(new FileReader(configFilePath));
                break;
        }
        return buffer;
    }

    /**
     * Reads data from an existing file and returns the appended String.
     * 
     * @param fromFile int representing the file read from.
     * @return readData returns String of data read from the specified file.
     */
    public String readFromExistingFile(int fromFile) {
        BufferedReader buffer;
        String readData = "";
        try {
            buffer = identifyReadFrom(fromFile);
            readData = readString(buffer, readData);
            buffer.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "FileNotFoundException: " + fromFile);
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException: " + fromFile);
            e.printStackTrace();
        }
        return readData.trim();
    }

    private String readString(BufferedReader buffer, String readData) throws IOException {
        String currentLine;
        while ((currentLine = buffer.readLine()) != null) {
            readData = readData + currentLine + NEW_LINE;
        }
        return readData;
    }

    /**
     * Reads commands from an existing Command File and returns a command queue.
     * 
     * @return readCommands
     */
    public Queue<String> readFromExistingCommandFile() {
        Queue<String> readCommands = new LinkedList<String>();
        BufferedReader buffer;
        try {
            logger.log(Level.INFO, "Reading from command file...");
            buffer = new BufferedReader(new FileReader(commandsFilePath));
            readQueue(readCommands, buffer);
            buffer.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "FileNotFoundException for command file.");
            e.printStackTrace();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException for command file.");
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

    /**
     * Copies data from the Main Task File to Back Up Task File to facilitate
     * 'Undo' function.
     * 
     * @return isWritten returns true if settings successfully written into
     *         Configuration File, false otherwise.
     */
    public boolean copyToBackUp() {
        allBackUpTasks = readFromExistingFile(READ_FROM_MAIN_FILE);
        return identifyWriteTo(allBackUpTasks, WRITE_TO_BACK_UP_FILE);
    }

    // ============================================================================
    // Writing to files
    // ============================================================================

    /**
     * Identifies which file to write to based on an int.
     * This method returns true if the data is written into the file, false
     * otherwise.
     * Whether the data has been written into the file depends on its last
     * modified time.
     * 
     * @param data String to be written into file.
     * @param toFile int representing the file to write to.
     * @return isWritten returns true if settings successfully written into
     *         Configuration File, false otherwise.
     */
    public boolean identifyWriteTo(String data, int toFile) {
        File file = null;
        String filePath = null;
        switch (toFile) {
            case WRITE_TO_MAIN_FILE :
                logger.log(Level.INFO, "Writing to main file...");
                file = new File(tasksFilePath);
                filePath = tasksFilePath;
                break;
            case WRITE_TO_BACK_UP_FILE :
                logger.log(Level.INFO, "Writing to back up file...");
                file = backUpTasksFile;
                filePath = backUpTasksFilePath;
                break;
            case WRITE_TO_CONFIG_FILE :
                logger.log(Level.INFO, "Writing to config file...");
                file = configFile;
                filePath = configFilePath;
                break;
        }
        return isWritten(data, file, filePath);
    }

    /**
     * Returns true if file is modified and data is written into file, false
     * otherwise
     * 
     * @param data
     * @param file file written to.
     * @param filePath directory of the file written to.
     * @return isModified returns true if time after modification is after time
     *         before modification, false otherwise.
     */
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
            logger.log(Level.SEVERE, "IOException when writing.");
            e.printStackTrace();
        }
        return afterModify;
    }

    /**
     * Returns true if commands are written to the Command File, false
     * otherwise.
     * 
     * @param command String of user's input.
     * @return isModified returns true if time after modification is after time
     *         before modification, false otherwise.
     */
    public boolean writeToCommandFile(String command) {
        FileWriter fileWriter;
        long beforeModify = commandsFile.lastModified();
        long afterModify = -1;
        try {
            // True to append to file
            fileWriter = new FileWriter(commandsFilePath, true);
            fileWriter.write(command + NEW_LINE);
            fileWriter.flush();
            fileWriter.close();
            afterModify = commandsFile.lastModified();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException when writing.");
            e.printStackTrace();
        }
        return isModified(beforeModify, afterModify);
    }

    /**
     * Returns true if timeAfterMod is after timeBeforeMod, false otherwise.
     * 
     * @param timeBeforeModification
     * @param timeAfterModification
     * @return isModified returns true if time after modification is after time
     *         before modification, false otherwise.
     */
    private boolean isModified(long timeBeforeModification, long timeAfterModification) {
        return timeAfterModification > timeBeforeModification;
    }

    // ============================================================================
    // Clearing and removing files
    // ============================================================================

    /**
     * Clears Command File upon committing.
     */
    public void clearCommandFileUponCommit() {
        clearCommandFile();
        allCommandsQueue.clear();
    }

    /**
     * Clears Command File upon committing.
     */
    public void clearCommandFile() {
        logger.log(Level.INFO, "Clearing command file...");
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(commandsFilePath);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException when clearing.");
            e.printStackTrace();
        }
    }

    public void deleteBackUpFile() {
        backUpTasksFile.delete();
    }

    // ============================================================================
    // Changing file directory
    // ============================================================================

    /**
     * Saves user's tasks to a new Main Task File and updates the Configuration
     * File with the new directory.
     * This method returns FILE_ALREADY_EXISTS = -1 when the file specified
     * already exists,
     * a success ResultSet if directory is successfully changed,
     * a fail ResultSet when a new file or directory cannot be created.
     * 
     * @param newFilePath
     * @return resultSet
     */
    public ResultSet changeDirectory(String newFilePath) {
        boolean isChanged = false;
        ResultSet resultSet = new ResultSet();
        resultSet.setIndex(RESULT_SET_FALSE);

        logger.log(Level.INFO, "Setting new file path...");
        File newFile = new File(newFilePath);

        isChanged = newFileExistsChecker(resultSet, isChanged, newFile);
        if (isChanged == true) {
            isChanged = writeConfigSettings();
        }
        resultSetChecker(resultSet, isChanged);
        return resultSet;
    }

    /**
     * Checks if new file specified by user exists.
     * 
     * @param resultSet
     * @param isChanged
     * @param newFile
     * @return isChanged returns true if the new file specified by user exists,
     *         false otherwise.
     */
    private boolean newFileExistsChecker(ResultSet resultSet, boolean isChanged, File newFile) {
        if (newFile.exists() == false) {
            isChanged = transferToNewFile(newFile);
        } else {
            logger.log(Level.WARNING, "File already exists. Saving failed.");
            resultSet.setIndex(FILE_ALREADY_EXISTS);
        }
        return isChanged;
    }

    /**
     * Transfers user's tasks to the new created file in the new directory.
     * 
     * @param newFile
     * @return isChanged
     */
    private boolean transferToNewFile(File newFile) {
        boolean isChanged;
        isChanged = makeNewDirectory(newFile);
        isChanged = createNewFile(newFile);

        String transferData = readFromExistingFile(READ_FROM_MAIN_FILE);
        setMainFilePath(newFile.getAbsolutePath());
        tasksFile = newFile;
        setAllStoredTasks(transferData);
        identifyWriteTo(transferData, WRITE_TO_MAIN_FILE);
        return isChanged;
    }

    /**
     * Converts boolean isChanged to the corresponding ResultSet.
     * 
     * @param resultSet
     * @param isChanged
     */
    private void resultSetChecker(ResultSet resultSet, boolean isChanged) {
        if (isChanged) {
            logger.log(Level.INFO, "File path changed successfully.");
            resultSet.setSuccess();
        } else {
            resultSet.setFail();
        }
    }

    /**
     * Loads tasks from an existing file specified by the user.
     * 
     * @param newFilePath directory of the new file specified by the user.
     * @return isLoaded returns true if tasks from new Main Task File is loaded,
     *         false if specified file does not exist.
     */
    public boolean loadFromExistingFile(String newFilePath) {
        boolean isLoaded = false;
        File newFile = new File(newFilePath);

        if (isExists(newFile)) {
            isLoaded = extractDataFromNewFile(newFile);
        } else {
            logger.log(Level.WARNING, "File does not exist. Loading failed.");
        }
        return isLoaded;
    }

    private boolean extractDataFromNewFile(File newFile) {
        boolean isLoaded;
        logger.log(Level.INFO, "Loading from existing file...");
        isLoaded = true;
        setMainFilePath(newFile.getAbsolutePath());
        tasksFile = newFile;
        setAllStoredTasks(readFromExistingFile(READ_FROM_MAIN_FILE));
        copyToBackUp();
        writeConfigSettings();
        return isLoaded;
    }

    // ============================================================================
    // Saving theme preference
    // ============================================================================

    /**
     * Writes the chosen theme into the Configuration File.
     * 
     * @param themeName
     * @return isWritten returns true if new theme is written to the
     *         Configuration File, false otherwise.
     */
    public boolean saveThemeName(String themeName) {
        setThemeName(themeName);
        return writeConfigSettings();
    }

    // ============================================================================
    // Logger files
    // ============================================================================

    /**
     * Initialises Logger File to store storage handling process.
     */
    private void initLogger() {
        logger = Logger.getLogger("StorageHandler");
        initFileHandler();
        logger.addHandler(fileHandler);
        fileHandler.setLevel(Level.ALL);
        logger.setLevel(Level.ALL);
        logger.config("Logger initialised.");
        logger.info("Logger name: " + logger.getName());
    }

    /**
     * Initialises File Handler for Logger File.
     */
    private void initFileHandler() {
        try {
            fileHandler = new FileHandler("StorageLogFile.log");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}