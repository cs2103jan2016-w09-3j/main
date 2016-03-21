package mainLogic;

import java.util.Calendar;
import java.util.Comparator;

import entity.TaskEntity;

public class TaskDateComparator implements Comparator<TaskEntity> {
    public int compare(TaskEntity task1, TaskEntity task2) {
        if (task1.isFloating() || task2.isFloating()) {
            return compareFloating(task1, task2);
        } else {        
            Calendar date1 = task1.getDueDate();
            Calendar date2 = task2.getDueDate();
    
            // 0 being returned means both tasks have the same date on the calendar.
            // Terminate if different days, carry on sorting them if they are the
            // same day
            int differentDayResult = compareDifferentDay(date1, date2);
            if( differentDayResult != 0){
                return differentDayResult;
            }
    
            return compareFullDayTask(task1, task2, date1, date2);
        }
    }

    private int compareFloating(TaskEntity task1, TaskEntity task2) {
        if(task1.isFloating() && !task2.isFloating()){
            return -1;
        }else if(!task1.isFloating() && task2.isFloating()){
            return 1;
        }else{
            return task1.getName().compareToIgnoreCase(task2.getName());
        }
    }

    private int compareFullDayTask(TaskEntity task1, TaskEntity task2, Calendar date1, Calendar date2) {
        if (task1.isFullDay() && !task2.isFullDay()) {
            return -1;
        } else if (!task1.isFullDay() && task2.isFullDay()) {
            return 1;
        } else if (!task1.isFullDay() && !task2.isFullDay()) {
            return date1.compareTo(date2);
        } else {
            // If both tasks are full day, sort them alphabetically
            return task1.getName().compareToIgnoreCase(task2.getName());
        }
    }

    private int compareDifferentDay(Calendar date1, Calendar date2) {
        if (date1.get(Calendar.YEAR) > date2.get(Calendar.YEAR)) {
            return 1;
        } else if (date1.get(Calendar.YEAR) < date2.get(Calendar.YEAR)) {
            return -1;
        } else if (date1.get(Calendar.MONTH) > date2.get(Calendar.MONTH)) {
            return 1;
        } else if (date1.get(Calendar.MONTH) < date2.get(Calendar.MONTH)) {
            return -1;
        } else if (date1.get(Calendar.DATE) > date2.get(Calendar.DATE)) {
            return 1;
        } else if (date1.get(Calendar.DATE) < date2.get(Calendar.DATE)) {
            return -1;
        }
        return 0;
    }
}