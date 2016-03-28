package test.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.TaskManagerInterface;

public class JUnitTaskManagerInterface {

    TaskManagerInterface manager = new TaskManagerInterface();
    TaskManager taskmanager = TaskManager.getInstance();
    
    @Test
    public void testAddDeleteModifyLink() {
        taskmanager.unloadFile();
        
        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 5; i++) {
            Calendar newDate = Calendar.getInstance();
            newDate.setTimeInMillis(newDate.getTimeInMillis() + i * 3000);
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), newDate, false, "some desc"));
        }
        manager.add(newList);
        
        TaskEntity firstFloating = new TaskEntity("Task floating 1"); 
        manager.add(firstFloating);
        manager.add(new TaskEntity("Task floating 2"));
        manager.add(new TaskEntity("Task floating 3"));
        manager.add(new TaskEntity("Task floating 4"));
        
        
        Calendar newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 2, 5);
        TaskEntity headTask = new TaskEntity("2016/2/5", newDate, true);
        manager.modify(1, headTask);

        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 2, 3);
        TaskEntity childTask = new TaskEntity("2016/2/3", newDate, true);
        manager.modify(3, childTask);

        assertEquals(manager.link(headTask, childTask), true);
        
        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 3, 16);
        childTask = new TaskEntity("2016/3/16", newDate, true);
        manager.add(childTask);
        manager.link(headTask, childTask);

        assertEquals(manager.link(childTask, headTask), false);
        
        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 3, 15);
        manager.add(new TaskEntity("2016/3/15", newDate, true));

        manager.link(firstFloating, childTask);
        
        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 3, 15);
        manager.modify(6, new TaskEntity("Modified task", newDate, true));
        
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_OTHERS),"2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_FLOATING),"Task floating 1, Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN),"2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");

    }

    @Test
    public void testMarkAsDone () {
        taskmanager.unloadFile();
        
        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 9; i++) {
            Calendar newDate = Calendar.getInstance();
            newDate.setTimeInMillis(newDate.getTimeInMillis() + i * 3000);
            newList.add(new TaskEntity("Task " + Integer.toString(i + 1), newDate, false, "some desc"));
        }
        for (int i = 0; i < 9; i++) {
            newList.add(new TaskEntity("Floating Task " + Integer.toString(i + 1)));
        }
        
        manager.add(newList);
        
        manager.switchView(manager.DISPLAY_MAIN);
       
        assertEquals(0, manager.markAsDone(0));
        assertEquals(6, manager.markAsDone(7));

        assertEquals("Task 2, Task 3, Task 4, Task 5, Task 6, Task 7, Task 8, ", taskmanager.printArrayContentsToString(manager.DISPLAY_MAIN));
        assertEquals("Task 1, Task 9, ", taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));
        
        //Testing for linked tasks
        manager.switchView(manager.DISPLAY_FLOATING);
        manager.link(manager.getWorkingList().get(0), manager.getWorkingList().get(1) );
        manager.link(manager.getWorkingList().get(0), manager.getWorkingList().get(2) ); 
        assertEquals(0, manager.markAsDone(0));

        manager.link(manager.getWorkingList().get(4), manager.getWorkingList().get(5) );
        

        assertEquals("Task 1, Task 9, Floating Task 1, Floating Task 2, Floating Task 3, ", taskmanager.printArrayContentsToString(manager.DISPLAY_COMPLETED));

    }
    
    @Test
    public void testSearchString () {
        taskmanager.unloadFile();

        manager.add(new TaskEntity("Groom Cat", "Remember to bring cat to grooming salon"));
        manager.add(new TaskEntity("Groom Dog", "Remember to bring dog to grooming salon"));
        manager.add(new TaskEntity("Groom Bird", "Remember bring bird grooming salon"));
        manager.add(new TaskEntity("Groom Rabbit", "Remember to bring rabbit to grooming salon"));
        manager.searchString("to");
        manager.switchView(manager.DISPLAY_SEARCH);
        System.out.println(taskmanager.printArrayContentsToString(manager.DISPLAY_SEARCH));
    }
}
