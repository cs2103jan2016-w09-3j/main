/**
 * @author Angie A0126357A
 * @@author Angie A0126357A
 * 
 *          StorageController to create instances of StorageHandler and
 *          JsonConverter to merge functionalities.
 */

package fileStorage;

import java.util.ArrayList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.ResultSet;
import entity.TaskEntity;

public class StorageController {

    public StorageHandler storageHandler;
    private static final int STORAGE_WRITE_TO_MAIN_FILE = 2;
    private static final int UI_SEARCH_VIEW = 4;
    public static final int STORAGE_COMMAND_QUEUE_SIZE = 5;

    /**
     * Class constructor.
     */
    public StorageController() {
        storageHandler = new StorageHandler();
    }

    // ============================================================================
    // Handling main file
    // ============================================================================

    /**
     * Retrieves all tasks previously saved in the Main Task File.
     * 
     * @return retrievedList Returns an AllTaskLists containing both main and
     *         floating ArrayLists.
     */
    public AllTaskLists getTaskLists() {
        JsonConverter jsonConverter = new JsonConverter();

        String retrievedTasks = storageHandler.getAllStoredTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);

        return retrievedList;
    }

    /**
     * Returns true if tasks are written to Main Task File, false otherwise.
     * This method stores all working tasks into the Main Task File.
     * 
     * @param allTaskLists Takes in an AllTaskLists containing both main and
     *            floating ArrayLists.
     * @return isSaved returns true if written to file, false otherwise.
     */
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        JsonConverter jsonConverter = new JsonConverter();

        String toStore = jsonConverter.javaToJson(allTaskLists);

        boolean isSaved = storageHandler.identifyWriteTo(toStore, STORAGE_WRITE_TO_MAIN_FILE);
        assert isSaved == true : "Tasks not stored.";

        return isSaved;
    }

    /**
     * Takes in two ArrayLists, main and floating, combines them into an
     * AllTaskLists, and stores them into the Main Task File.
     * 
     * @param main
     * @param floating
     * @return isSaved returns true if written to file, false otherwise.
     */
    public boolean storeTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating) {
        AllTaskLists newList = new AllTaskLists();

        newList.setFloatingTaskList(floating);
        newList.setMainTaskList(main);

        return storeTaskLists(newList);
    }

    // ============================================================================
    // Handling back up file
    // ============================================================================

    /**
     * Retrieves user's tasks from the Back Up File.
     * 
     * @return retrievedList Returns an AllTaskLists containing both main and
     *         floating ArrayLists.
     */
    public AllTaskLists getBackUpTaskLists() {
        JsonConverter jsonConverter = new JsonConverter();

        String retrievedTasks = storageHandler.getAllBackUpTasks();
        AllTaskLists retrievedList = jsonConverter.jsonToJava(retrievedTasks);

        return retrievedList;
    }

    /**
     * Deletes Back Up File, to be done after every session.
     */
    public void deleteBackUp() {
        storageHandler.deleteBackUpFile();
    }

    // ============================================================================
    // Handling command file
    // ============================================================================

    /**
     * Returns true if command queue is full and the last command is not in
     * UI_SEARCH_VIEW = 4, false otherwise.
     * This method also offers the new command to the existing command queue.
     * 
     * @param command Takes in and stores user's input.
     * @return isFullQueue
     */
    public boolean saveUponFullQueue(String command) {
        singleCommandSavedChecker(command);

        Queue<String> newCommandsQueue = getCommandsQueue();
        newCommandsQueue.offer(command);
        setCommandsQueue(newCommandsQueue);

        boolean isFullQueue = queueFullChecker(command, newCommandsQueue);
        return isFullQueue;
    }

    /**
     * Checks if conditions for committing into Main Task File is met.
     * 
     * @param command
     * @param newCommandsQueue
     * @return isFullQueue
     */
    private boolean queueFullChecker(String command, Queue<String> newCommandsQueue) {
        boolean isFullQueue = false;

        String[] splitCommand = command.split(" ");
        if (isSearchView(splitCommand) == false && isFullQueue(newCommandsQueue)) {
            isFullQueue = true;
        }
        return isFullQueue;
    }

    /**
     * Takes in a command queue and checks if it is more than or equals to
     * QUEUE_SIZE = 20.
     * 
     * @param newCommandsQueue
     * @return isFullQueue returns true if Queue is more than or equals to
     *         QUEUE_SIZE = 20, false otherwise.
     */
    private boolean isFullQueue(Queue<String> newCommandsQueue) {
        return newCommandsQueue.size() >= STORAGE_COMMAND_QUEUE_SIZE;
    }

    /**
     * Checks if UI is in Search View.
     * This method returns true if UI is in search view, false otherwise.
     * 
     * @param splitCommand Takes in first int of command.
     * @return isSearchView returns true if UI is in Search View, false
     *         otherwise.
     */
    private boolean isSearchView(String[] splitCommand) {
        return splitCommand[0].equals(UI_SEARCH_VIEW);
    }

    /**
     * Checks if the command has been written to Command File.
     * 
     * @param command Takes in one line of user's input command.
     */
    private void singleCommandSavedChecker(String command) {
        boolean isSaved = storeCommandLine(command);
        if (isSaved == false) {
            System.out.println("============================");
            System.out.println("***DID NOT WRITE TO FILE***");
            System.out.println("============================");
        }
    }

    public boolean storeCommandLine(String command) {
        return storageHandler.writeToCommandFile(command);
    }

    public Queue<String> getCommandsQueue() {
        return storageHandler.getAllCommandsQueue();
    }

    public void setCommandsQueue(Queue<String> newCommandsQueue) {
        storageHandler.setAllCommandsQueue(newCommandsQueue);
    }

    public void clearCommandFileOnCommit() {
        storageHandler.clearCommandFileUponCommit();
    }

    public void clearCommandFile() {
        storageHandler.clearCommandFile();
    }

    // ============================================================================
    // Changing directory
    // ============================================================================

    public ResultSet saveToNewDirectory(String newFilePath) {
        return storageHandler.changeDirectory(newFilePath);
    }

    public boolean loadFromSpecifiedFile(String newFilePath) {
        return storageHandler.loadFromExistingFile(newFilePath);
    }

    public String getMainFilePath() {
        return storageHandler.getMainFilePath();
    }

    // ============================================================================
    // Saving theme
    // ============================================================================

    public boolean saveThemePreference(String themeName) {
        return storageHandler.saveThemeName(themeName);
    }

    public String getThemePreference() {
        return storageHandler.getThemeName();
    }
}
