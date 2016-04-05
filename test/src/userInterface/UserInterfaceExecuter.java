//@@author A0125514N
package userInterface;

import java.util.ArrayList;
import java.util.Queue;

import entity.ResultSet;
import entity.TaskEntity;
import mainLogic.TaskManagerInterface;

public class UserInterfaceExecuter {

	private TaskManagerInterface _taskManager = new TaskManagerInterface();

	public ArrayList<TaskEntity> getWorkingList() {
		return _taskManager.getWorkingList();
	}

	public int getNextTimeListId() {
		return _taskManager.getNextTimeListId();
	}

	public TaskEntity getRandomFloating() {
		return _taskManager.getRandomFloating();
	}

	public void switchView(int displaySearch) {
		_taskManager.switchView(displaySearch);
	}

	// commands execution //

	public ResultSet addTask(TaskEntity task, String rawCommandWithView) {
		return _taskManager.add(task, rawCommandWithView);
	}

	public ResultSet addBatch(ArrayList<TaskEntity> task, String rawCommandWithView) {
		return _taskManager.add(task, rawCommandWithView);
	}

	public ResultSet delete(String id, String rawCommandWithView) {
		if (id == null) {
			return null;
		}
		return _taskManager.delete(id, rawCommandWithView);
	}

	public ResultSet modify(int idToModify, TaskEntity task, String rawCommandWithView) {
		return _taskManager.modify(idToModify, task, rawCommandWithView);
	}

	public ResultSet searchString(String stringToSearch, String rawCommandWithView) {
		return _taskManager.searchString(stringToSearch, rawCommandWithView);
	}

	public ResultSet markAsDone(int indexInt, String rawCommandWithView) {
		return _taskManager.markAsDone(indexInt, rawCommandWithView);
	}

	public ResultSet link(TaskEntity taskEntity, TaskEntity taskEntity2, String rawCommandWithView) {
		return _taskManager.link(taskEntity, taskEntity2, rawCommandWithView);
	}

	public ResultSet changeSaveDir(String dirPath) {
		return _taskManager.changeDirectory(dirPath);
	}

	public void closeTaskManager() {
		_taskManager.closeTaskManager();
	}

	public Queue<String> getBackedupCommands() {
		return _taskManager.getBackedupCommands();
	}

	public ArrayList<String> getCommandsToRun() {
		return _taskManager.undo();
	}

	public void undoComplete() {
		_taskManager.undoComplete();
	}

	public ResultSet changeTheme(String styleSheet) {
		return _taskManager.saveTheme(styleSheet);
	}

	public ResultSet loadFrom(String loadFrom) {
		return null;
	}

	public String loadTheme() {
		return _taskManager.loadTheme();
	}
}
