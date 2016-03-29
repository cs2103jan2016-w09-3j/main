package test.userInterface;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import dateParser.InputParser;
import dateParser.Pair;
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

		int r1 = runCommand("add basktball");
		assertTrue(r1 >= 0 && r1 <= ex.getWorkingList().size());
		int r2 = runCommand("add basktball");
		assertTrue(r2 >= 0 && r2 <= ex.getWorkingList().size());
		int r3 = runCommand("add basktball");
		assertTrue(r3 >= 0 && r3 <= ex.getWorkingList().size());
		int r4 = runCommand("add basktball");
		assertTrue(r4 >= 0 && r4 <= ex.getWorkingList().size());

		assertEquals(runCommand("add basktball tmr"), -1);
		assertEquals(runCommand("add basktball 3/3"), -1);
		ex.switchView(TaskManager.DISPLAY_MAIN);

		int r5 = runCommand("add basktball tmr");
		assertTrue(r5 >= 0 && r5 <= ex.getWorkingList().size());
	}

	@Test
	public void tesDeleteCommands() {
		ex = new UserInterfaceExecuter();
		assertEquals(runCommand("delete asda"), -1);
		assertEquals(runCommand("delete"), -1);
		assertEquals(runCommand("delete 1231"), -1);
		assertEquals(runCommand("delete 00--"), -1);

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
		case FLOAT: {
			ex.switchView(TaskManager.DISPLAY_FLOATING);
			return 1;
		}
		case MAIN: {
			ex.switchView(TaskManager.DISPLAY_MAIN);
			return 1;
		}
		case SEARCH: {
			String searchStirng = parser.getSearchString();
			int r = ex.searchString(searchStirng, rawString);
			ex.switchView(TaskManager.DISPLAY_SEARCH);
			return r;
		}
		case LINK: {
			Pair<String, String> ids = parser.getLinkID();
			int index1 = Utils.convertBase36ToDec(ids.getFirst());
			int index2 = Utils.convertBase36ToDec(ids.getSecond());
			if (index1 < ex.getWorkingList().size() && index2 < ex.getWorkingList().size()) {
				TaskEntity t1 = ex.getWorkingList().get(index1);
				TaskEntity t2 = ex.getWorkingList().get(index2);
				boolean result = ex.link(t1, t2, rawString);
				if (result) {
					return 1;
				}
			} else {
				return -1;
			}
			return 0;
		}
		default:
			return -9;
		}
	}
}
