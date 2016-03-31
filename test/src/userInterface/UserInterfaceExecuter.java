package userInterface;

import java.util.ArrayList;
import java.util.Queue;

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

	public int addTask(TaskEntity task, String rawCommandWithView) {
		int insertedTo = _taskManager.add(task, rawCommandWithView);
		return insertedTo;
	}

	public int addBatch(ArrayList<TaskEntity> task, String rawCommandWithView) {
		return _taskManager.add(task, rawCommandWithView);
	}

	public int delete(String id, String rawCommandWithView) {
		if (id == null) {
			return -1;
		}
		return _taskManager.delete(id, rawCommandWithView);
	}

	public int modify(int idToModify, TaskEntity task, String rawCommandWithView) {
		return _taskManager.modify(idToModify, task, rawCommandWithView);
	}

	public int searchString(String stringToSearch, String rawCommandWithView) {
		return _taskManager.searchString(stringToSearch, rawCommandWithView);
	}

	public int markAsDone(int indexInt, String rawCommandWithView) {
		return _taskManager.markAsDone(indexInt, rawCommandWithView);
	}

	public boolean link(TaskEntity taskEntity, TaskEntity taskEntity2, String rawCommandWithView) {
		return _taskManager.link(taskEntity, taskEntity2, rawCommandWithView);
	}

	public boolean changeSaveDir(String dirPath) {
		return _taskManager.changeDirectory(dirPath);
	}

	public void closeTaskManager() {
		_taskManager.closeTaskManager();
	}

	public Queue<String> getBackedupCommands() {
		return _taskManager.getBackedupCommands();
	}
	
	public Queue<String> getCommandsToRun() {
		return null;
	}

}
