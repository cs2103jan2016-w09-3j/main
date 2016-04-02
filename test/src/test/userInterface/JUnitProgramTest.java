package test.userInterface;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import dateParser.InputParser;
import dateParser.Pair;
import entity.ResultSet;
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
		assertEquals(runCommand("add basktball").getView(), ResultSet.FLOATING_VIEW);
		assertEquals(runCommand("add basktball").getView(), ResultSet.FLOATING_VIEW);
		assertEquals(runCommand("add basktball").getView(), ResultSet.FLOATING_VIEW);
		ex.switchView(TaskManager.DISPLAY_FLOATING);
		assertEquals(runCommand("add what tmr").getView(), ResultSet.TASK_VIEW);
		assertEquals(runCommand("add har tmr").getStatus(), ResultSet.STATUS_CONFLICT);
	}

	@Test
	public void tesDeleteCommands() {
		ex = new UserInterfaceExecuter();
		assertEquals(runCommand("delete asda"), null);
		assertEquals(runCommand("delete"), null);
		assertEquals(runCommand("delete 1231"), null);
		assertEquals(runCommand("delete 00--"), null);

	}

	public ResultSet runCommand(String rawString) {
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
				return ex.modify(Utils.convertStringToInteger(id), tasks.get(0), rawString);
			} else {
				return null;
			}
		}
		case DONE: {
			String id = parser.getID();
			return ex.markAsDone(Utils.convertStringToInteger(id), rawString);
		}
		case FLOAT: {
			ex.switchView(TaskManager.DISPLAY_FLOATING);
			return null;
		}
		case MAIN: {
			ex.switchView(TaskManager.DISPLAY_MAIN);
			return null;
		}
		case SEARCH: {
			String searchStirng = parser.getSearchString();
			ResultSet r = ex.searchString(searchStirng, rawString);
			ex.switchView(TaskManager.DISPLAY_SEARCH);
			return null;
		}
		case LINK: {
			Pair<String, String> ids = parser.getLinkID();
			int index1 = Utils.convertStringToInteger(ids.getFirst());
			int index2 = Utils.convertStringToInteger(ids.getSecond());
			if (index1 < ex.getWorkingList().size() && index2 < ex.getWorkingList().size()) {
				TaskEntity t1 = ex.getWorkingList().get(index1);
				TaskEntity t2 = ex.getWorkingList().get(index2);
				ResultSet result = ex.link(t1, t2, rawString);
				return result;
			} else {
				return null;
			}
		}
		default:
			return null;
		}
	}
}
