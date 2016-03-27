package mainLogic;

import java.util.Calendar;
import java.util.Comparator;

import entity.TaskEntity;

public class TaskCompletionTimeComparator implements Comparator<TaskEntity> {
    public int compare(TaskEntity task1, TaskEntity task2) {
        //System.out.println(task1.getCompletionDate());
        //System.out.println(task2.getCompletionDate());
        return task1.getCompletionDate().compareTo(task2.getCompletionDate());
    }
}