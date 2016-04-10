/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *         This class is the only class that interacts with Logic component.
 */
package userInterface;

import java.util.ArrayList;
import java.util.Queue;
import entity.ResultSet;
import entity.TaskEntity;
import mainLogic.TaskManagerInterface;

public class UserInterfaceExecuter {

	private TaskManagerInterface _taskManager = new TaskManagerInterface();

	/**
	 * Gets the working list, depending on _currentView.
	 * 
	 * @return list of task (floating list, task list, search list)
	 */
	public ArrayList<TaskEntity> getWorkingList() {
		return _taskManager.getWorkingList();
	}

	/**
	 * Get the index of the most upcoming task in the working list.
	 * 
	 * @return index
	 */
	public int getNextTimeListId() {
		return _taskManager.getNextTimeListId();
	}

	/**
	 * Gets a random task from the floating list.
	 * 
	 * @return TaskEntity
	 */
	public TaskEntity getRandomFloating() {
		return _taskManager.getRandomFloating();
	}

	/**
	 * Switch the view in logic to switch all working list to the selected list.
	 * 
	 * @param displaySearch
	 */
	public void switchView(int view) {
		_taskManager.switchView(view);
	}

	// commands execution //

	/**
	 * Adds a task into the task list.
	 * 
	 * @param task
	 * @param rawCommandWithView
	 * @return ResultSet
	 */
	public ResultSet addTask(TaskEntity task, String rawCommandWithView) {
		return _taskManager.add(task, rawCommandWithView);
	}

	/**
	 * Deletes task base on _currentView and id.
	 * 
	 * @param id
	 * @param rawCommandWithView
	 * @return ResultSet
	 */
	public ResultSet delete(String id, String rawCommandWithView) {
		if (id == null) {
			return null;
		}
		return _taskManager.delete(id, rawCommandWithView);
	}

	/**
	 * Modifies the task base on _currentView and id.
	 * 
	 * @param idToModify
	 * @param task
	 * @param rawCommandWithView
	 * @return ResultSet
	 */
	public ResultSet modify(int idToModify, TaskEntity task, String rawCommandWithView) {
		return _taskManager.modify(idToModify, task, rawCommandWithView);
	}

	/**
	 * Search for the task that is similar to the search string.
	 * 
	 * @param stringToSearch
	 * @param rawCommandWithView
	 * @return ResultSet
	 */
	public ResultSet searchString(String stringToSearch, String rawCommandWithView) {
		return _taskManager.searchString(stringToSearch, rawCommandWithView);
	}

	/**
	 * Marks the given task base on id and the current workingList as completed.
	 * 
	 * @param indexInt
	 * @param rawCommandWithView
	 * @return ResultSet
	 */
	public ResultSet markAsDone(int indexInt, String rawCommandWithView) {
		return _taskManager.markAsDone(indexInt, rawCommandWithView);
	}

	/**
	 * Links the task together.
	 * 
	 * @param taskEntity as project head
	 * @param taskEntity2 as sub task
	 * @param rawCommandWithView
	 * @return ResultSet
	 */
	public ResultSet link(TaskEntity taskEntity, TaskEntity taskEntity2, String rawCommandWithView) {
		return _taskManager.link(taskEntity, taskEntity2, rawCommandWithView);
	}

	/**
	 * Change the saving directory.
	 * 
	 * @param dirPath
	 * @return ResultSet
	 */
	public ResultSet changeSaveDir(String dirPath) {
		return _taskManager.changeDirectory(dirPath);
	}

	/**
	 * Close the manager and trigger saving of file.
	 */
	public void closeTaskManager() {
		_taskManager.closeTaskManager();
	}

	/**
	 * Get all commands that have not been saved to file. 
	 * 
	 * @return Queue<String>
	 */
	public Queue<String> getBackedupCommands() {
		return _taskManager.getBackedupCommands();
	}

	/**
	 * Get all commands to run after an undo is executed.
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getCommandsToRun() {
		return _taskManager.undo();
	}

	/**
	 * Resets the state for undo.
	 */
	public void undoComplete() {
		_taskManager.undoComplete();
	}

	/**
	 * Change the theme, saved preference in config file.
	 * 
	 * @param styleSheet
	 * @return ResultSet
	 */
	public ResultSet changeTheme(String styleSheet) {
		return _taskManager.saveTheme(styleSheet);
	}

	/**
	 * Tries to load the file given.
	 * 
	 * @param loadFrom
	 * @return ResultSet
	 */
	public ResultSet loadFrom(String loadFrom) {
		return _taskManager.loadFrom(loadFrom);
	}

	/**
	 * Tries to get theme saved in config file.
	 * 
	 * @return String
	 */
	public String loadTheme() {
		return _taskManager.loadTheme();
	}

	/**
	 * Get filePath where file was loaded from.
	 * 
	 * @return String
	 */
	public String getLoadFromFilePath() {
		return _taskManager.getMainFilePath();
	}

	/**
	 * Check if the file has loaded successfully.
	 * 
	 * @return true only if the file has loaded successfully
	 */
	public boolean isFileLoadedSuccess() {
		return _taskManager.checkLoad();
	}

	/**
	 * Gets the current view of the manager. 
	 * 
	 * @return view
	 */
	public int getCurrentManagerView() {
		return _taskManager.getView();
	}
}
