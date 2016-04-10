/**
 * @author qy
 * @@author a0125493a
 * 
 *          Testing unit for TaskManagerInterface's functionalities
 */

package test.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.TaskManagerInterface;
import mainLogic.TaskUtils;

public class JUnitTaskManagerInterface {

    TaskManagerInterface manager = new TaskManagerInterface();
    TaskManager taskmanager = TaskManager.getInstance();

    @Test
    public void testTaskManagerInterface_SwitchView_SwitchedToFloating() {
        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(manager.DISPLAY_FLOATING, manager.getView());
    }

    @Test
    public void testTaskManagerInterface_AddFloatingTask_AddedToFloatingInOrder() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task floating 1"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task floating 2"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task floating 3"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task floating 4"), "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task floating 1, Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN), "");
    }

    @Test
    public void testTaskManagerInterface_AddTimedTask_AddedToMainChronologicallyByDate() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(17, 1, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(15, 1, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(18, 1, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 3, Task 1, Task 2, Task 4, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING), "");
    }

    @Test
    public void testTaskManagerInterface_AddTimedTask_AddedToMainChronologicallyByTime() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 22, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(17, 1, 2016, 7, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(15, 1, 2016, 8, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(17, 1, 2016, 6, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 3, Task 1, Task 4, Task 2, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING), "");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "Task 3, Task 1, Task 4, Task 2, ");
    }

    @Test
    public void testTaskManagerInterface_AddTimedTask_AddedToMainChronologicallyFullDayBeforeOtherTasks() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 6, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(16, 1, 2016, 7, 0), true),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(16, 1, 2016, 9, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(16, 1, 2016, 8, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 2, Task 1, Task 4, Task 3, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING), "");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "Task 2, Task 1, Task 4, Task 3, ");
    }

    @Test
    public void testTaskManagerInterface_DeleteTimedTask_MainTask3Deleted() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 6, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(16, 1, 2016, 8, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(16, 1, 2016, 7, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(16, 1, 2016, 9, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.delete("1", "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 1, Task 2, Task 4, ");
    }

    @Test
    public void testTaskManagerInterface_DeleteFloatingTask_FloatingTask2Deleted() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4"), "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.delete("1", "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task 1, Task 3, Task 4, ");
    }

    @Test
    public void testTaskManagerInterface_ModifyTimedTask_Task3ModifiedToTask0() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 6, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(16, 1, 2016, 8, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(16, 1, 2016, 7, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(16, 1, 2016, 9, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.modify("1", new TaskEntity("Task 0", null, TaskUtils.createDate(16, 1, 2016, 5, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 0, Task 1, Task 2, Task 4, ");
    }

    @Test
    public void testTaskManagerInterface_ModifyFloatingTask_FloatingTaskMoveToMainTask() {
        taskmanager.unloadFile();
        manager.add(new TaskEntity("Task 1"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 2"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 3"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task 4"), "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.modify("0", new TaskEntity("Task 0", null, TaskUtils.createDate(16, 1, 2016, 5, 0), false),
                "PLACEHOLDER_SAVE_COMMAND");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task 2, Task 3, Task 4, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN), "Task 0, ");
    }

    @Test
    public void testTaskManagerInterface_AddModifyDeleteCompiled_PrintoutMatchesTestPrintout() {
        taskmanager.unloadFile();

        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 5; i++) {
            Calendar newDate = TaskUtils.createDate(1, 3, 2016);
            newDate.set(Calendar.MINUTE, newDate.get(Calendar.MINUTE) + i);
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), null, newDate, false, "some desc"));
        }
        manager.add(newList, "PLACEHOLDER_SAVE_COMMAND");

        System.out.println(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN));
        TaskEntity firstFloating = new TaskEntity("Task floating 1");
        assertEquals(true, manager.add(firstFloating, "PLACEHOLDER_SAVE_COMMAND").isSuccess());
        manager.add(new TaskEntity("Task floating 2"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task floating 3"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Task floating 4"), "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.delete("0", "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_MAIN);

        TaskEntity headTask = new TaskEntity("2016/2/5", null, TaskUtils.createDate(5, 2, 2016), true);
        manager.modify(1, headTask, "PLACEHOLDER_SAVE_COMMAND");

        TaskEntity childTask = new TaskEntity("2016/2/3", null, TaskUtils.createDate(3, 2, 2016), true);
        manager.modify(3, childTask, "PLACEHOLDER_SAVE_COMMAND");

        assertEquals(manager.link(headTask, childTask, "PLACEHOLDER_SAVE_COMMAND").isSuccess(), true);

        childTask = new TaskEntity("2016/3/16", null, TaskUtils.createDate(16, 3, 2016), true);
        manager.add(childTask, "PLACEHOLDER_SAVE_COMMAND");
        manager.link(headTask, childTask, "PLACEHOLDER_SAVE_COMMAND");

        assertEquals(manager.link(childTask, headTask, "PLACEHOLDER_SAVE_COMMAND").isSuccess(), false);

        manager.add(new TaskEntity("2016/3/15", null, TaskUtils.createDate(15, 3, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");

        manager.link(firstFloating, childTask, "PLACEHOLDER_SAVE_COMMAND");

        manager.modify(6, new TaskEntity("Modified task", null, TaskUtils.createDate(15, 3, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");

        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
    }

    @Test
    public void testTaskManagerInterface_markTimedDone_TaskMovedToCompleteAndIsComplete() {
        taskmanager.unloadFile();

        TaskEntity nextTaskToAdd = new TaskEntity("Assignment", null, TaskUtils.createDate(16, 1, 2016, 6, 0),
                false);
        manager.add(nextTaskToAdd, "PLACEHOLDER_SAVE_COMMAND");

        assertEquals("Assignment, ", taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN));
        manager.switchView(manager.DISPLAY_MAIN);
        manager.markAsDone(0, "PLACEHOLDER_SAVE_COMMAND");
        assertEquals("", taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN));
        assertEquals("Assignment, ", taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));
        assertEquals(true, nextTaskToAdd.isCompleted());
    }

    @Test
    public void testTaskManagerInterface_markFloatingDone_TaskMovedToCompleteAndIsComplete() {
        taskmanager.unloadFile();

        TaskEntity nextTaskToAdd = new TaskEntity("Assignment");
        manager.add(nextTaskToAdd, "PLACEHOLDER_SAVE_COMMAND");

        assertEquals("Assignment, ", taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING));
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.markAsDone(0, "PLACEHOLDER_SAVE_COMMAND");
        assertEquals("", taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING));
        assertEquals("Assignment, ", taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));
        assertEquals(true, nextTaskToAdd.isCompleted());
    }

    @Test
    public void testTaskManagerInterface_link3Task_BothLinkSuccessful() {
        taskmanager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Task 1");
        manager.add(firstTask, "PLACEHOLDER_SAVE_COMMAND");
        TaskEntity secondTask = new TaskEntity("Task 2");
        manager.add(secondTask, "PLACEHOLDER_SAVE_COMMAND");
        TaskEntity thirdTask = new TaskEntity("Task 3");
        manager.add(thirdTask, "PLACEHOLDER_SAVE_COMMAND");

        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(true, manager.link("0", "1", "PLACEHOLDER_SAVE_COMMAND").isSuccess());
        assertEquals(true, manager.link("0", "2", "PLACEHOLDER_SAVE_COMMAND").isSuccess());
    }

    @Test
    public void testTaskManagerInterface_link3Task_LinkProjectHeadAsTaskUnderFails() {
        taskmanager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Task 1");
        manager.add(firstTask, "PLACEHOLDER_SAVE_COMMAND");
        TaskEntity secondTask = new TaskEntity("Task 2");
        manager.add(secondTask, "PLACEHOLDER_SAVE_COMMAND");
        TaskEntity thirdTask = new TaskEntity("Task 3");
        manager.add(thirdTask, "PLACEHOLDER_SAVE_COMMAND");

        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(true, manager.link("0", "1", "PLACEHOLDER_SAVE_COMMAND").isSuccess());
        assertEquals(false, manager.link("2", "0", "PLACEHOLDER_SAVE_COMMAND").isSuccess());
    }

    @Test
    public void testTaskManagerInterface_link3Task_MakeAssociatedTaskProjectHeadFails() {
        taskmanager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Task 1");
        manager.add(firstTask, "PLACEHOLDER_SAVE_COMMAND");
        TaskEntity secondTask = new TaskEntity("Task 2");
        manager.add(secondTask, "PLACEHOLDER_SAVE_COMMAND");
        TaskEntity thirdTask = new TaskEntity("Task 3");
        manager.add(thirdTask, "PLACEHOLDER_SAVE_COMMAND");

        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(true, manager.link("0", "1", "PLACEHOLDER_SAVE_COMMAND").isSuccess());
        assertEquals(false, manager.link("1", "2", "PLACEHOLDER_SAVE_COMMAND").isSuccess());
    }

    @Test
    public void testTaskManagerInterface_searchStringSingleWord_AllDescriptionAndNameMatchesAddedToSearchView() {
        taskmanager.unloadFile();

        manager.add(new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Groom Dog", "Remember to bring dog to grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Groom Bird", "Remember bring bird grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Groom Rabbit", "Remember to bring rabbit to grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.searchString("to", "PLACEHOLDER_SAVE_COMMAND");
        assertEquals("Groom Cat, Groom Dog, Groom Rabbit, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));
        manager.searchString("groOming", "PLACEHOLDER_SAVE_COMMAND");
        assertEquals("Groom Cat, Groom Dog, Groom Bird, Groom Rabbit, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));

        manager.add(new TaskEntity("Do 2103 V0.4", null, TaskUtils.createDate(4, 4, 2016), true,
                "Remember to be in before 9pm"), "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Do 2103 V0.3", null, TaskUtils.createDate(28, 3, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Do 2104 V0.5", null, TaskUtils.createDate(11, 4, 2016), true),
                "PLACEHOLDER_SAVE_COMMAND");

        manager.searchString("remember", "PLACEHOLDER_SAVE_COMMAND");
        assertEquals("Do 2103 V0.4, Groom Cat, Groom Dog, Groom Bird, Groom Rabbit, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));
    }

    @Test
    public void testTaskManagerInterface_searchCompletedTask_GroomDogFound() {
        taskmanager.unloadFile();

        manager.add(new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Groom Dog", "Remember to bring dog to grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Groom Bird", "Remember bring bird grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");
        manager.add(new TaskEntity("Groom Rabbit", "Remember to bring rabbit to grooming salon"),
                "PLACEHOLDER_SAVE_COMMAND");

        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(true, manager.markAsDone(1, "PLACEHOLDER_SAVE_COMMAND").isSuccess());

        manager.searchString("completed", "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("Groom Dog, ", taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS));
    }

    @Test
    public void testTaskManagerInterface_searchCategory_OnlyExactMatchFound() {
        taskmanager.unloadFile();

        TaskEntity nextTaskToAdd = new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon");
        nextTaskToAdd.addHashtag("#pets");
        manager.add(nextTaskToAdd, "PLACEHOLDER_SAVE_COMMAND");

        manager.searchString("#pets", "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("Groom Cat, ", taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS));

        manager.searchString("#pet", "PLACEHOLDER_SAVE_COMMAND");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("", taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS));
    }
}
