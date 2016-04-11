// @@author A0125415N
package test.userinterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import entity.ResultSet;
import entity.TaskEntity;
import logic.TaskManager;
import logic.TaskUtils;
import parser.CommandParser.COMMAND;
import parser.InputParser;
import parser.Pair;
import userinterface.UserInterfaceExecuter;

public class JUnitProgramTest {

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private static UserInterfaceExecuter _executor = new UserInterfaceExecuter();
    private static int counter = 0;

    @BeforeClass
    public static void oneTimeSetUp() {
        try {
            folder.newFolder("temp");
        } catch (IOException e) {
        }
    }

    public static void changeFile() throws IOException {
        String fileName = "ProjectTest_".concat(Integer.toString(counter++)).concat(".txt");
        File f = folder.newFile("temp/".concat(fileName));
        _executor.loadFrom(f.getPath());
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    @Before
    public void setUp() {
        try {
            changeFile();
        } catch (IOException e) {
        }
        _executor.switchView(TaskManager.DISPLAY_MAIN);
        runCommand("add floating task1");
        runCommand("add floating task2");
        runCommand("add floating task3");
        runCommand("add some task tmr");
        runCommand("add some task tmr");
        runCommand("add some task tmr");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAddCommand_addTimedTask_MainListHas4TasksAfterAdding() {
        assertTrue(runCommand("Add basketball tmr").isSuccess());
        _executor.switchView(TaskManager.DISPLAY_MAIN);

        assertEquals(4, _executor.getWorkingList().size());
    }

    @Test
    public void testAddCommand_addInvalidTimedTask_MainListHas3TasksAfterAdding() {
        assertFalse(runCommand("Add tmr").isSuccess());
        _executor.switchView(TaskManager.DISPLAY_MAIN);

        assertEquals(3, _executor.getWorkingList().size());
    }

    @Test
    public void testAddCommand_addFloatingTask_FloatingListHas4TasksAfterAdding() {
        assertTrue(runCommand("Add baseball").isSuccess());
        _executor.switchView(TaskManager.DISPLAY_FLOATING);

        assertEquals(4, _executor.getWorkingList().size());
    }

    @Test
    public void testInvalidCommands_executeInvalidCommands_commandsGetsRejected() {
        assertEquals(runCommand("basktball"), null);
        assertEquals(runCommand("ad d temp"), null);
        assertEquals(runCommand("123"), null);
        assertEquals(runCommand("-.;as;"), null);
    }

    @Test
    public void testAddTaskToSystem_addDummyTasks_taskAddedToCorrectList() {
        assertEquals(runCommand("add basktball").getView(), ResultSet.FLOATING_VIEW);
        _executor.switchView(TaskManager.DISPLAY_FLOATING);
        assertEquals(runCommand("add what tmr").getView(), ResultSet.TASK_VIEW);
        assertEquals(runCommand("add floating tasak").getView(), ResultSet.FLOATING_VIEW);
    }

    @Test
    public void testModifyCommand_ModifyFloatingToTimed_() {
        _executor.switchView(TaskManager.DISPLAY_FLOATING);
        runCommand("Edit ID0 floating task1 tmr 3pm");

        assertEquals(2, _executor.getWorkingList().size());
        _executor.switchView(TaskManager.DISPLAY_MAIN);
        assertEquals(4, _executor.getWorkingList().size());
    }

    @Test
    public void testDeleteCommands_executeInvalidDeleteCommands_commandsGetRejected() {
        assertNull(runCommand("delete asda"));
        assertNull(runCommand("delete "));
        assertNull(runCommand("delete   "));
        assertNull(runCommand("delete 123123"));
        assertNull(runCommand("delete --00"));
    }

    @Test
    public void testDoneCommand_executeInvalidCommands_AllMarkAsDoneFails() {
        assertFalse(runCommand("done IDD0").isSuccess());
        assertFalse(runCommand("done DDDII0").isSuccess());
        assertFalse(runCommand("done ").isSuccess());
        assertFalse(runCommand("done i").isSuccess());
    }

    @Test
    public void testDoneCommand_markSomeTaskDone_All6TaskGoesIntoCompleteList() {
        assertTrue(runCommand("done ID0").isSuccess());
        assertTrue(runCommand("done ID0").isSuccess());
        assertTrue(runCommand("done ID0").isSuccess());
        _executor.switchView(TaskManager.DISPLAY_FLOATING);
        assertTrue(runCommand("done ID0").isSuccess());
        assertTrue(runCommand("done ID0").isSuccess());
        assertTrue(runCommand("done ID0").isSuccess());
        _executor.switchView(TaskManager.DISPLAY_COMPLETED);
        assertEquals(_executor.getWorkingList().size(), 6);
        // no task to mark as done
        assertFalse(runCommand("done ID0").isSuccess());
        // Completeed list remains the same size
        assertEquals(_executor.getWorkingList().size(), 6);
    }

    @Test
    public void testDeleteTask_deleteTaskFromFloatingList_taskDeleted() {
        _executor.switchView(TaskManager.DISPLAY_FLOATING);
        assertTrue(runCommand("delete ID0").isSuccess());
        assertFalse(runCommand("delete ID10").isSuccess());
        assertTrue(runCommand("delete ID0").isSuccess());
        assertTrue(runCommand("delete ID0").isSuccess());
        assertFalse(runCommand("delete ID0").isSuccess());
        assertFalse(runCommand("delete ID0").isSuccess());
    }

    // @@ A0125514N
    public ResultSet runCommand(String rawString) {
        InputParser parser = new InputParser(rawString);
        COMMAND cmd = parser.getCommand();
        switch (cmd) {
            case ADD : {
                ArrayList<TaskEntity> tasks = parser.getTask();
                if (tasks.size() == 1) {
                    return _executor.addTask(tasks.get(0), rawString);
                }
            }
            case DELETE : {
                String id = parser.getID();
                return _executor.delete(id, rawString);
            }
            case EDIT : {
                String id = parser.getID();
                parser.removeId();
                ArrayList<TaskEntity> tasks = parser.getTask();
                if (tasks.size() == 1) {
                    return _executor.modify(TaskUtils.convertStringToInteger(id), tasks.get(0), rawString);
                } else {
                    return null;
                }
            }
            case DONE : {
                String id = parser.getID();
                return _executor.markAsDone(TaskUtils.convertStringToInteger(id), rawString);
            }
            case FLOAT : {
                _executor.switchView(TaskManager.DISPLAY_FLOATING);
                return null;
            }
            case MAIN : {
                _executor.switchView(TaskManager.DISPLAY_MAIN);
                return null;
            }
            case SEARCH : {
                String searchStirng = parser.getSearchString();
                ResultSet r = _executor.searchString(searchStirng, rawString);
                _executor.switchView(TaskManager.DISPLAY_SEARCH);
                return null;
            }
            case LINK : {
                Pair<String, String> ids = parser.getLinkID();
                int index1 = TaskUtils.convertStringToInteger(ids.getFirst());
                int index2 = TaskUtils.convertStringToInteger(ids.getSecond());
                if (index1 < _executor.getWorkingList().size()
                        && index2 < _executor.getWorkingList().size()) {
                    TaskEntity t1 = _executor.getWorkingList().get(index1);
                    TaskEntity t2 = _executor.getWorkingList().get(index2);
                    ResultSet result = _executor.link(t1, t2, rawString);
                    return result;
                } else {
                    return null;
                }
            }
            default :
                return null;
        }
    }
}