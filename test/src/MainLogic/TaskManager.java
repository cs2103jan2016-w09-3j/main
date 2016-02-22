package MainLogic;

import java.util.ArrayList;
import java.util.Calendar;
import Entity.Task;

class TaskManager {
    private static ArrayList<Task> tasks;

    public static boolean delete(int index) {
        if (index + 1 > tasks.size()) {
            return false;
        }

        tasks.remove(index);
        return true;
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
     * Deletes a consecutive list of tasks form the arrayList
     * 
     * @param startIndex - An integer specifying the first index to be deleted.
     *            startIndex must be <= endIndex
     * @param endIndex - An integer specifying the last index to be deleted. The
     *            index must not be bigger than the size of the array List
     * @return - True if delete operation succeeded
     *         - False if delete operation failed
     */
    public static boolean delete(int startIndex, int endIndex) {
        if (endIndex + 1 > tasks.size()) {
            return false;
        } else if (startIndex > endIndex) {
            return false;
        }

        for (int i = 0; i <= endIndex - startIndex; i++) {
            tasks.remove(startIndex);
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

    public static ArrayList<Task> undo() {
        // TODO
        return new ArrayList<Task>();
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
    public static boolean checkSameDate(Task firstTask, Task secondTask) {
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