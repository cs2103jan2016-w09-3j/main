package test.logic;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.Utils;

public class JUnitTaskManager {
    TaskManager manager = TaskManager.getInstance();
    
	@Test
	public void testAddDeleteModifyLink() {	    
        manager.unloadFile();
        
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
        
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_OTHERS),"2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_FLOATING),"Task floating 1, Task floating 2, Task floating 3, Task floating 4, ");
        assertEquals(manager.printArrayContentsToString(manager.DISPLAY_MAIN),"2016/2/3, 2016/2/5, Task 1, Task 3, Task 5, 2016/3/15, Modified task, ");
	}
}
