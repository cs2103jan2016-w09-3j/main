/**
 * @author Angie A0126357A
 * @@author A0126357A
 * 
 *          StorageInterface acts as a facade class for the storage component.
 */

package storage;

import java.util.ArrayList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.ResultSet;
import entity.TaskEntity;

public class StorageInterface {

    private StorageController storageController;

    /**
     * Public class constructor.
     */
    public StorageInterface() {
        storageController = new StorageController();
    }

    /**
     * Retrieves all user's tasks previously saved into the Main Task File.
     * 
     * @return retrievedTaskList returns tasks read from the Main Task File.
     */
    public AllTaskLists getTaskLists() {
        return storageController.getTaskLists();
    }

    /**
     * Gets the absolute directory of the Main Task File.
     * 
     * @return mainFilePath returns a String of directory.
     */
    public String getMainFilePath() {
        return storageController.getMainFilePath();
    }

    /**
     * Takes in an AllTaskLists of the current working tasks. This method
     * returns true if all tasks are written into the Main Task File, false
     * otherwise.
     * 
     * @param allTaskLists takes in an AllTaskLists of working tasks.
     * @return isSaved returns true if all tasks are written into the Main Task
     *         File, false otherwise.
     */
    public boolean storeTaskLists(AllTaskLists allTaskLists) {
        return storageController.storeTaskLists(allTaskLists);
    }

    /**
     * Takes in main and floating ArrayLists of the current working tasks. This
     * method returns true if all tasks are written into the Main Task File,
     * false otherwise.
     * 
     * @param main takes in an ArrayList of main tasks.
     * @param floating takes in an ArrayList of floating tasks.
     * @return isSaved returns true if all tasks are written into the Main Task
     *         File, false otherwise.
     */
    public boolean storeTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating) {
        return storageController.storeTaskLists(main, floating);
    }

    /**
     * Retrieves all tasks read from the Back Up Task File.
     * 
     * @return retrievedTaskList returns tasks read from the Back Up Task File.
     */
    public AllTaskLists getBackUpTaskLists() {
        return storageController.getBackUpTaskLists();
    }

    /**
     * Deletes the Back Up Task File for the current session.
     */
    public void deleteBackUp() {
        storageController.deleteBackUp();
    }

    /**
     * Checks if the queue size of the command queue is more than or equals to
     * the constant QUEUE_SIZE and if the UI is in Search View. This method
     * returns true if both conditions are met, and false otherwise.
     * 
     * @param command takes in one single line of user's input.
     * @return isFullQueue returns true if conditions are met, false otherwise.
     */
    public boolean saveUponFullQueue(String command) {
        return storageController.saveUponFullQueue(command);
    }

    /**
     * Returns the current command queue based on the Command File.
     * 
     * @return commandsQueue returns a Queue of previously input commands up to
     *         a constant QUEUE_SIZE of 20.
     */
    public Queue<String> getCommandsQueue() {
        return storageController.getCommandsQueue();
    }

    /**
     * Takes in a new command queue and replaces the existing command queue with
     * the new queue.
     * 
     * @param newCommandsQueue
     */
    public void setCommandsQueue(Queue<String> newCommandsQueue) {
        storageController.setCommandsQueue(newCommandsQueue);
    }

    /**
     * Clears the Command File when the changes are committed to the Main Task
     * File.
     */
    public void clearCommandFileOnCommit() {
        storageController.clearCommandFileOnCommit();
    }

    /**
     * Clears the Command File Upon proper 'Exit'.
     */
    public void clearCommandFile() {
        storageController.clearCommandFile();
    }

    /**
     * Takes in a file path for the new saving location of the Main Task File.
     * This method returns a ResultSet to indicate the success or failure of
     * changing the directory. -1 will be returned if an existing file is found.
     * If the changing of directory is successful, the new directory will be
     * saved to the Configuration File.
     * 
     * @param newFilePath
     * @return resultSet
     */
    public ResultSet saveTo(String newFilePath) {
        return storageController.saveToNewDirectory(newFilePath);
    }

    /**
     * Loads user's tasks from the specified file path. This method returns true
     * if a file is found, false otherwise. It also updates the Configuration
     * File with the new directory of the Main Task File.
     * 
     * @param newFilePath
     * @return isFileExists
     */
    public boolean loadFrom(String newFilePath) {
        return storageController.loadFromSpecifiedFile(newFilePath);
    }

    /**
     * Writes the chosen theme preference into the Configuration File. Returns
     * true if the changes are successfully updated into the Configuration File,
     * false otherwise.
     * 
     * @param themeName
     * @return isWritten returns true if the changes are written into the
     *         Configuration File, false otherwise.
     */
    public boolean saveThemePreference(String themeName) {
        return storageController.saveThemePreference(themeName);
    }

    /**
     * Returns the name of the preferred theme, previously read from the
     * Configuration File.
     * 
     * @return themeName returns a String theme name.
     */
    public String getThemePreference() {
        return storageController.getThemePreference();
    }
}
