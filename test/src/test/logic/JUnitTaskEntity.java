/**
 * @author qy
 * @@author a0125493a
 * 
 *          Testing unit for TaskEntity's functionalities
 */
package test.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Test;

import entity.TaskEntity;
import logic.TaskManager;

public class JUnitTaskEntity {
    TaskManager manager = TaskManager.getInstance();

    @Test
    public void testTaskEntity_GetAssociationPosition_positionMatchInsertionOrder() {
        manager.unloadFile();

        TaskEntity firstTask = new TaskEntity("Master Task");
        manager.add(firstTask);

        assertEquals(firstTask.getAssociationPosition(), -1);

        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for (int i = 0; i < 9; i++) {
            TaskEntity otherTasks = new TaskEntity("Task " + Integer.toString(i + 1));
            newList.add(otherTasks);
            manager.link(firstTask, otherTasks);
        }
        manager.add(newList);

        assertEquals(firstTask.getAssociationPosition(), 0);

        manager.switchView(manager.DISPLAY_FLOATING);
        ArrayList<TaskEntity> taskList = manager.getWorkingList();

        assertEquals(taskList.get(1).getAssociationPosition(), 1);

        assertEquals(taskList.get(9).getAssociationPosition(), 9);

        assertEquals(taskList.get(5).getAssociationPosition(), 5);
    }

}
