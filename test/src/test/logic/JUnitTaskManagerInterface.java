package test.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.TaskManagerInterface;
import mainLogic.Utils;

public class JUnitTaskManagerInterface {

    TaskManagerInterface manager = new TaskManagerInterface();
    TaskManager taskmanager = TaskManager.getInstance();

    @Test
    public void testAdd () {
        taskmanager.unloadFile();

        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 5; i++) {
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), null, Utils.createDate(1, 4, 2016, 12 + i, 0), false, "some desc"));
        }
        manager.add(newList, "PLACEHOLDER");
        assertEquals("Task 1, Task 2, Task 3, Task 4, Task 5, ", taskmanager.printArrayContentsToString(taskmanager.DISPLAY_MAIN));
        
        newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 5; i++) {
            newList.add(new TaskEntity("Task " + Integer.toString(i + 6), null, Utils.createDate(2, 4, 2016, 12 + i, 0), false, "some desc"));
        }
        manager.add(newList, "PLACEHOLDER");
        assertEquals("Task 1, Task 2, Task 3, Task 4, Task 5, Task 6, Task 7, Task 8, Task 9, Task 10, ", taskmanager.printArrayContentsToString(taskmanager.DISPLAY_MAIN));
    }
    
    @Test
    public void testAddDeleteModifyLink() {
        taskmanager.unloadFile();

        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 5; i++) {
            Calendar newDate = Utils.createDate(1, 3, 2016);
            newDate.set(Calendar.MINUTE, newDate.get(Calendar.MINUTE) + i);
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), null, newDate, false, "some desc"));
        }
        manager.add(newList, "PLACEHOLDER");

        TaskEntity firstFloating = new TaskEntity("Task floating 1");
        manager.add(firstFloating, "PLACEHOLDER");
        manager.add(new TaskEntity("Task floating 2"), "PLACEHOLDER");
        manager.add(new TaskEntity("Task floating 3"), "PLACEHOLDER");
        manager.add(new TaskEntity("Task floating 4"), "PLACEHOLDER");

        Calendar newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 2, 5);
        TaskEntity headTask = new TaskEntity("2016/2/5", null, newDate, true);
        manager.modify(1, headTask, "PLACEHOLDER");

        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 2, 3);
        TaskEntity childTask = new TaskEntity("2016/2/3", null, newDate, true);
        manager.modify(3, childTask, "PLACEHOLDER");

        assertEquals(manager.link(headTask, childTask, "PLACEHOLDER").isSuccess(), true);

        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 3, 16);
        childTask = new TaskEntity("2016/3/16", null, newDate, true);
        manager.add(childTask, "PLACEHOLDER");
        manager.link(headTask, childTask, "PLACEHOLDER");

        assertEquals(manager.link(childTask, headTask, "PLACEHOLDER").isSuccess(), false);

        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 3, 15);
        manager.add(new TaskEntity("2016/3/15", null, newDate, true), "PLACEHOLDER");

        manager.link(firstFloating, childTask, "PLACEHOLDER");

        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 3, 15);
        manager.modify(6, new TaskEntity("Modified task", null, newDate, true), "PLACEHOLDER");

        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS),
                "2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING),
                "Task floating 1, Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),
                "2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");

    }

    @Test
    public void testMarkAsDone() {
        taskmanager.unloadFile();

        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 9; i++) {
            Calendar newDate = Calendar.getInstance();
            newDate.setTimeInMillis(newDate.getTimeInMillis() + i * 3000);
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), null, newDate, false, "some desc"));
        }
        for (int i = 0; i < 9; i++) {
            newList.add(new TaskEntity("Floating Task " + Integer.toString(i + 1)));
        }

        manager.add(newList, "PLACEHOLDER");

        manager.switchView(manager.DISPLAY_MAIN);

        assertEquals(0, manager.markAsDone(0, "PLACEHOLDER"));
        assertEquals(6, manager.markAsDone(7, "PLACEHOLDER"));

        assertEquals("Task 2, Task 3, Task 4, Task 5, Task 6, Task 7, Task 8, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN));
        assertEquals("Task 1, Task 9, ", taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));

        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(0, manager.markAsDone(0, "PLACEHOLDER"));

        assertEquals("Task 1, Task 9, Floating Task 1, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));

    }

    @Test
    public void testSearchString() {
        taskmanager.unloadFile();

        manager.add(new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon"), "PLACEHOLDER");
        manager.add(new TaskEntity("Groom Dog", "Remember to bring dog to grooming salon"), "PLACEHOLDER");
        manager.add(new TaskEntity("Groom Bird", "Remember bring bird grooming salon"), "PLACEHOLDER");
        manager.add(new TaskEntity("Groom Rabbit", "Remember to bring rabbit to grooming salon"),
                "PLACEHOLDER");
        manager.searchString("to", "PLACEHOLDER");
        assertEquals("Groom Cat, Groom Dog, Groom Rabbit, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));
        manager.searchString("groOming", "PLACEHOLDER");
        assertEquals("Groom Cat, Groom Dog, Groom Bird, Groom Rabbit, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));

        assertEquals(-2, manager.markAsDone(2, "PLACEHOLDER"));

        System.out.println(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING));
        System.out.println(taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));

        manager.add(new TaskEntity("Do 2103 V0.4", null, Utils.createDate(4, 4, 2016), true,
                "Remember to be in before 9pm"), "PLACEHOLDER");
        manager.add(new TaskEntity("Do 2103 V0.3", null, Utils.createDate(28, 3, 2016), true), "PLACEHOLDER");
        manager.add(new TaskEntity("Do 2104 V0.5", null, Utils.createDate(11, 4, 2016), true), "PLACEHOLDER");

        manager.searchString("remember", "PLACEHOLDER");
        assertEquals("Do 2103 V0.4, Groom Cat, Groom Dog, Groom Bird, Groom Rabbit, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));

        manager.switchView(manager.DISPLAY_FLOATING);
        assertEquals(1, manager.markAsDone(1, "PLACEHOLDER"));

        manager.switchView(manager.DISPLAY_SEARCH);

        manager.searchString("remember", "PLACEHOLDER");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("Do 2103 V0.4, Groom Cat, Groom Bird, Groom Rabbit, Groom Dog, ",
                taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));

        manager.searchString("completed", "PLACEHOLDER");
        manager.switchView(manager.DISPLAY_SEARCH);
        assertEquals("Groom Dog, ", taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS));
    }
}
