package mainLogic;

import java.util.ArrayList;
import java.util.Calendar;

import entity.TaskEntity;

class TaskManager {
    private static ArrayList<TaskEntity> displayedTasks;
    private static ArrayList<TaskEntity> floatingTaskEntities = new ArrayList<TaskEntity>();
    private static ArrayList<TaskEntity> mainTaskEntities = new ArrayList<TaskEntity>();
    
    public static ArrayList<TaskEntity> getWorkingList() {
        return displayedTasks;
    }
    
    /**
     * UI Interface function
     * Deletion from the displayed list, will delete the object from the main
     * list in the backend as well, whether it is from the floating or main list
     * 
     * @param index - index of the item in the displayed tasks to be deleted
     * @return false - if fail to delete
     *         true - if delete operation succeeded
     */
    public static boolean delete(int index) {
        if (index + 1 > displayedTasks.size()) {
            return false;
        }
        
        TaskEntity itemToBeDeleted = displayedTasks.get(index);
        boolean deletionSuccess = deleteFromMainList(itemToBeDeleted);
        if(!deletionSuccess){
            return false;
        }
        
        displayedTasks.remove(index);
        return true;
    }

    /**
     * Removes an object from the list containing all tasks, from its respective
     * list (floatingTaskEntities if it is a floating task, mainTaskEntities if
     * its not)
     * 
     * @param itemToBeDeleted - The task to be deleted from the main list
     * @return true - if removal operation succeeded
     *         false - if removal operation failed
     */
    private static boolean deleteFromMainList(TaskEntity itemToBeDeleted) {
        if (itemToBeDeleted.isFloating()) {
            return floatingTaskEntities.remove(itemToBeDeleted);
        } else {
            return mainTaskEntities.remove(itemToBeDeleted);
        }
    }

    /**
     * Deletes a list of tasks from the arrayList
     * 
     * @param index - A string containing a base36 number
     * @return - True if delete operation succeeded
     *         - False if delete operation failed
     */
    public static boolean delete(String index) {
        return delete(Converter.convertBase36ToDec(index));
    }   

    /**
     * Deletes a consecutive list of tasks from both the working and main
     * arrayList. Upon failing to delete any item, the function terminates at
     * the object that it fails to delete and does not attempt to delete anymore
     * items
     * 
     * @param startIndex - An integer specifying the first index to be deleted.
     *            startIndex must be <= endIndex
     * @param endIndex - An integer specifying the last index to be deleted. The
     *            index must not be bigger than the size of the array List
     * @return - True if delete operation succeeded
     *         - False if delete operation failed
     */
    public static boolean delete(int startIndex, int endIndex) {
        if (endIndex + 1 > displayedTasks.size()) {
            return false;
        } else if (startIndex > endIndex) {
            return false;
        }

        for (int i = 0; i <= endIndex - startIndex; i++) {
            if(!delete(startIndex)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param startIndex - A string containing a base36 number that represents
     *            the index of the first entry of all the entries to be deleted
     * @param endIndex - A string containing a base36 number that represents
     *            the index of the last entry of all the entries to be deleted
     * @return - True if delete operation succeeded
     *         - False if delete operation failed
     */
    public static boolean delete(String startIndex, String endIndex) {
        return delete(Converter.convertBase36ToDec(startIndex), Converter.convertBase36ToDec(endIndex));
    }

    public static ArrayList<TaskEntity> undo() {
        // TODO
        return new ArrayList<TaskEntity>();
    }

    /**
     * 
     * Checks if the 2 tasks passed in are of the same date
     * 
     * @param firstTask
     * @param secondTask
     * @return True - If the dates are the same
     *         False - If either the dates are different, or if either task is
     *         floating
     */
    public static boolean checkSameDate(TaskEntity firstTask, TaskEntity secondTask) {
        Calendar firstDate;
        if (firstTask.isFloating()) {
            return false;
        } else {
            firstDate = firstTask.getDueDate();
        }

        Calendar secondDate;
        if (secondTask.isFloating()) {
            return false;
        } else {
            secondDate = secondTask.getDueDate();
        }

        return checkSameDate(firstDate, secondDate);
    }
    
    public static boolean checkSameDate(Calendar firstDate, Calendar secondDate) {
        if (firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
                && firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
                && firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }

    public static int getTodayFirstTaskIndex() {
        return 0;
    }

    public static int isFirstItemForDay(int index) {
        // TODO
        return 0;
    }
}
