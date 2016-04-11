/**
 * @author qy
 * @@author a0125493a
 * 
 *          Testing unit for TaskManager's functionalities
 */

package test.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import entity.TaskEntity;
import logic.TaskManager;
import logic.TaskUtils;

public class JUnitTaskManager {
    TaskManager manager = TaskManager.getInstance();

    @Test
    public void testTaskManager_SwitchView_SwitchedToFloating() {
        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(manager.DISPLAY_FLOATING, manager.getView());
    }

    @Test
    public void testTaskManager_AddFloatingTask_AddedToFloatingInOrder() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task floating 1"));
        manager.add(new TaskEntity("Task floating 2"));
        manager.add(new TaskEntity("Task floating 3"));
        manager.add(new TaskEntity("Task floating 4"));
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task floating 1, Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN), "");
    }

    @Test
    public void testTaskManager_AddTimedTask_AddedToMainChronologicallyByDate() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016), true));
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(17, 1, 2016), true));
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(15, 1, 2016), true));
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(18, 1, 2016), true));
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 3, Task 1, Task 2, Task 4, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING), "");
    }

    @Test
    public void testTaskManager_AddTimedTask_AddedToMainChronologicallyByTime() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 22, 0), false));
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(17, 1, 2016, 7, 0), false));
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(15, 1, 2016, 8, 0), false));
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(17, 1, 2016, 6, 0), false));
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 3, Task 1, Task 4, Task 2, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING), "");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "Task 3, Task 1, Task 4, Task 2, ");
    }

    @Test
    public void testTaskManager_AddTimedTask_AddedToMainChronologicallyFullDayBeforeOtherTasks() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 6, 0), false));
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(16, 1, 2016, 7, 0), true));
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(16, 1, 2016, 9, 0), false));
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(16, 1, 2016, 8, 0), false));
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 2, Task 1, Task 4, Task 3, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING), "");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "Task 2, Task 1, Task 4, Task 3, ");
    }

    @Test
    public void testTaskManager_DeleteTimedTask_MainTask3Deleted() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 6, 0), false));
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(16, 1, 2016, 8, 0), false));
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(16, 1, 2016, 7, 0), false));
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(16, 1, 2016, 9, 0), false));
        manager.delete("1");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN), "Task 1, Task 2, Task 4, ");
    }

    @Test
    public void testTaskManager_DeleteFloatingTask_FloatingTask2Deleted() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1"));
        manager.add(new TaskEntity("Task 2"));
        manager.add(new TaskEntity("Task 3"));
        manager.add(new TaskEntity("Task 4"));
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.delete("1");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task 1, Task 3, Task 4, ");
    }

    @Test
    public void testTaskManager_ModifyTimedTask_Task3ModifiedToTask0() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1", null, TaskUtils.createDate(16, 1, 2016, 6, 0), false));
        manager.add(new TaskEntity("Task 2", null, TaskUtils.createDate(16, 1, 2016, 8, 0), false));
        manager.add(new TaskEntity("Task 3", null, TaskUtils.createDate(16, 1, 2016, 7, 0), false));
        manager.add(new TaskEntity("Task 4", null, TaskUtils.createDate(16, 1, 2016, 9, 0), false));
        manager.modify("1", new TaskEntity("Task 0", null, TaskUtils.createDate(16, 1, 2016, 5, 0), false));
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "Task 0, Task 1, Task 2, Task 4, ");
    }

    @Test
    public void testTaskManager_ModifyFloatingTask_FloatingTaskMoveToMainTask() {
        manager.unloadFile();
        manager.add(new TaskEntity("Task 1"));
        manager.add(new TaskEntity("Task 2"));
        manager.add(new TaskEntity("Task 3"));
        manager.add(new TaskEntity("Task 4"));
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.modify("0", new TaskEntity("Task 0", null, TaskUtils.createDate(16, 1, 2016, 5, 0), false));
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task 2, Task 3, Task 4, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN), "Task 0, ");
    }

    @Test
    public void testTaskManager_AddModifyDeleteCompiled_PrintoutMatchesTestPrintout() {
        manager.unloadFile();

        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 5; i++) {
            Calendar newDate = TaskUtils.createDate(1, 3, 2016);
            newDate.set(Calendar.MINUTE, newDate.get(Calendar.MINUTE) + i);
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), null, newDate, false, "some desc"));
        }
        manager.add(newList);

        System.out.println(manager.printArrayContentsToString(manager.DISPLAY_MAIN));
        TaskEntity firstFloating = new TaskEntity("Task floating 1");
        assertTrue(manager.add(firstFloating).isSuccess());
        manager.add(new TaskEntity("Task floating 2"));
        manager.add(new TaskEntity("Task floating 3"));
        manager.add(new TaskEntity("Task floating 4"));
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.delete(0);
        manager.switchView(manager.DISPLAY_MAIN);

        TaskEntity headTask = new TaskEntity("2016/2/5", null, TaskUtils.createDate(5, 2, 2016), true);
        manager.modify(1, headTask);

        TaskEntity childTask = new TaskEntity("2016/2/3", null, TaskUtils.createDate(3, 2, 2016), true);
        manager.modify(3, childTask);

        assertEquals(manager.link(headTask, childTask).isSuccess(), true);

        childTask = new TaskEntity("2016/3/16", null, TaskUtils.createDate(16, 3, 2016), true);
        manager.add(childTask);
        manager.link(headTask, childTask);

        assertEquals(manager.link(childTask, headTask).isSuccess(), false);

        manager.add(new TaskEntity("2016/3/15", null, TaskUtils.createDate(15, 3, 2016), true));

        manager.link(firstFloating, childTask);

        manager.modify(6, new TaskEntity("Modified task", null, TaskUtils.createDate(15, 3, 2016), true));

        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
    }

    @Test
    public void testTaskManager_markTimedDone_TaskMovedToCompleteAndIsComplete() {
        manager.unloadFile();

        TaskEntity nextTaskToAdd = new TaskEntity("Assignment", null, TaskUtils.createDate(16, 1, 2016, 6, 0),
                false);
        manager.add(nextTaskToAdd);

        assertEquals("Assignment, ", manager.printArrayContentsToString(manager.DISPLAY_MAIN));
        manager.switchView(manager.DISPLAY_MAIN);
        manager.markAsDone(0);
        assertEquals("", manager.printArrayContentsToString(manager.DISPLAY_MAIN));
        assertEquals("Assignment, ", manager.printArrayContentsToString(manager.DISPLAY_COMPLETED));
        assertTrue(nextTaskToAdd.isCompleted());
    }

    @Test
    public void testTaskManager_markFloatingDone_TaskMovedToCompleteAndIsComplete() {
        manager.unloadFile();

        TaskEntity nextTaskToAdd = new TaskEntity("Assignment");
        manager.add(nextTaskToAdd);

        assertEquals("Assignment, ", manager.printArrayContentsToString(manager.DISPLAY_FLOATING));
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.markAsDone(0);
        assertEquals("", manager.printArrayContentsToString(manager.DISPLAY_FLOATING));
        assertEquals("Assignment, ", manager.printArrayContentsToString(manager.DISPLAY_COMPLETED));
        assertTrue(nextTaskToAdd.isCompleted());
    }

    @Test
    public void testTaskManager_link3Task_BothLinkSuccessful() {
        manager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Task 1");
        manager.add(firstTask);
        TaskEntity secondTask = new TaskEntity("Task 2");
        manager.add(secondTask);
        TaskEntity thirdTask = new TaskEntity("Task 3");
        manager.add(thirdTask);

        manager.switchView(manager.DISPLAY_FLOATING);
        assertTrue(manager.link(0, 1).isSuccess());
        assertTrue(manager.link(0, 2).isSuccess());
    }

    @Test
    public void testTaskManager_link3Task_LinkProjectHeadAsTaskUnderFails() {
        manager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Task 1");
        manager.add(firstTask);
        TaskEntity secondTask = new TaskEntity("Task 2");
        manager.add(secondTask);
        TaskEntity thirdTask = new TaskEntity("Task 3");
        manager.add(thirdTask);

        manager.switchView(manager.DISPLAY_FLOATING);
        assertTrue(manager.link(0, 1).isSuccess());
        assertFalse(manager.link(2, 0).isSuccess());
    }

    @Test
    public void testTaskManager_link3Task_MakeAssociatedTaskProjectHeadFails() {
        manager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Task 1");
        manager.add(firstTask);
        TaskEntity secondTask = new TaskEntity("Task 2");
        manager.add(secondTask);
        TaskEntity thirdTask = new TaskEntity("Task 3");
        manager.add(thirdTask);

        manager.switchView(manager.DISPLAY_FLOATING);
        assertTrue(manager.link(0, 1).isSuccess());
        assertFalse(manager.link(1, 2).isSuccess());
    }

    @Test
    public void testTaskManager_searchStringSingleWord_AllDescriptionAndNameMatchesAddedToSearchView() {
        manager.unloadFile();

        manager.add(new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon"));
        manager.add(new TaskEntity("Groom Dog", "Remember to bring dog to grooming salon"));
        manager.add(new TaskEntity("Groom Bird", "Remember bring bird grooming salon"));
        manager.add(new TaskEntity("Groom Rabbit", "Remember to bring rabbit to grooming salon"));
        manager.searchString("to");
        assertEquals("Groom Cat, Groom Dog, Groom Rabbit, ",
                manager.printArrayContentsToString(manager.DISPLAY_SEARCH));
        manager.searchString("groOming");
        assertEquals("Groom Cat, Groom Dog, Groom Bird, Groom Rabbit, ",
                manager.printArrayContentsToString(manager.DISPLAY_SEARCH));

        manager.add(new TaskEntity("Do 2103 V0.4", null, TaskUtils.createDate(4, 4, 2016), true,
                "Remember to be in before 9pm"));
        manager.add(new TaskEntity("Do 2103 V0.3", null, TaskUtils.createDate(28, 3, 2016), true));
        manager.add(new TaskEntity("Do 2104 V0.5", null, TaskUtils.createDate(11, 4, 2016), true));

        manager.searchString("remember");
        assertEquals("Do 2103 V0.4, Groom Cat, Groom Dog, Groom Bird, Groom Rabbit, ",
                manager.printArrayContentsToString(manager.DISPLAY_SEARCH));
    }

    @Test
    public void testTaskManager_searchCompletedTask_GroomDogFound() {
        manager.unloadFile();

        manager.add(new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon"));
        manager.add(new TaskEntity("Groom Dog", "Remember to bring dog to grooming salon"));
        manager.add(new TaskEntity("Groom Bird", "Remember bring bird grooming salon"));
        manager.add(new TaskEntity("Groom Rabbit", "Remember to bring rabbit to grooming salon"));

        manager.switchView(manager.DISPLAY_FLOATING);
        assertTrue(manager.markAsDone(1).isSuccess());

        manager.searchString("completed");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("Groom Dog, ", manager.printArrayContentsToString(manager.DISPLAY_OTHERS));
    }

    @Test
    public void testTaskManager_searchCategory_OnlyExactMatchFound() {
        manager.unloadFile();

        TaskEntity nextTaskToAdd = new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon");
        nextTaskToAdd.addHashtag("#pets");
        manager.add(nextTaskToAdd);

        manager.searchString("#pets");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("Groom Cat, ", manager.printArrayContentsToString(manager.DISPLAY_OTHERS));

        manager.searchString("#pet");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("", manager.printArrayContentsToString(manager.DISPLAY_OTHERS));
    }
}
