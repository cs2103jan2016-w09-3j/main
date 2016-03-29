package test.userInterface;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import dateParser.InputParser;
import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.Utils;
import userInterface.CommandBar;
import userInterface.UserInterfaceExecuter;
import dateParser.CommandParser.COMMAND;

public class JUnitProgramTest {

	UserInterfaceExecuter ex;

	@Test
	public void testFullCommands() {
		ex = new UserInterfaceExecuter();
	}
	
	@Test
	public void testAddCommands() {
		ex = new UserInterfaceExecuter();
		assertEquals(runCommand("add basktball"), -1);
		assertEquals(runCommand("add basktball"), -1);
		assertEquals(runCommand("add basktball"), -1);
		ex.switchView(TaskManager.DISPLAY_FLOATING);
		
	}
	
	@Test
	public void tesDeleteCommands() {
		ex = new UserInterfaceExecuter();
	}

	public int runCommand(String rawString) {
		InputParser parser = new InputParser(rawString);
		COMMAND cmd = parser.getCommand();
		switch (cmd) {
		case ADD: {
			ArrayList<TaskEntity> tasks = parser.getTask();
			if (tasks.size() == 1) {
				return ex.addTask(tasks.get(0), rawString);
			} else {
				return ex.addBatch(tasks, rawString);
			}
		}
		case DELETE: {
			String id = parser.getID();
			return ex.delete(id, rawString);
		}
		case EDIT: {
			String id = parser.getID();
			parser.removeId();
			ArrayList<TaskEntity> tasks = parser.getTask();
			if (tasks.size() == 1) {
				return ex.modify(Utils.convertBase36ToDec(id), tasks.get(0), rawString);
			} else {
				return -1;
			}
		}
		case DONE: {
			String id = parser.getID();
			return ex.markAsDone(Utils.convertBase36ToDec(id), rawString);
		}
		default:
			return -9;
		}
	}
}
