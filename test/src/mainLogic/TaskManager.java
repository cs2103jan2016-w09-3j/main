package mainLogic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import edu.emory.mathcs.backport.java.util.Collections;
import entity.TaskEntity;

public class TaskManager {
    private static ArrayList<TaskEntity> displayedTasks;
    private static ArrayList<TaskEntity> floatingTaskEntities = new ArrayList<TaskEntity>();
    private static ArrayList<TaskEntity> mainTaskEntities = new ArrayList<TaskEntity>();

    /**
     * TEST FUNCTION
     * Function for manually testing functions(First test/debugging) in
     * TaskManager class before regression testing with JUnit
     * 
     * @param args
     */
    public static void main (String[] args)
    {
        System.out.println(Utils.convertBase36ToDec(",10"));
        ArrayList<TaskEntity> newList = new ArrayList<TaskEntity>();
        for(int i = 0; i < 5; i++)
        {
            Calendar newDate = Calendar.getInstance();
            newDate.setTimeInMillis(newDate.getTimeInMillis() + i * 3000);
            newList.add(new TaskEntity("Task " + Integer.toString(i+1), newDate, false));
        }
        add(newList);
        add(new TaskEntity("Task floating"));
        Calendar newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 2, 5);
        //modify(0, new TaskEntity("2016/2/5", newDate, true));
        
        newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(2016, 2, 3);
        //modify(3, new TaskEntity("2016/2/3", newDate, true));
        
        getWorkingList();
        printList();
        
        //while(true){
        //    System.out.println(getNextTimeListId());
        //}
    }
    
    /**
     * TEST FUNCTION
     * Prints out the 2 arraylists
     */
    private static void testDisplay() {
        displayedTasks = new ArrayList<TaskEntity>();
        displayedTasks.add(floatingTaskEntities.get(0));
        displayedTasks.add(floatingTaskEntities.get(5));
        displayedTasks.add(floatingTaskEntities.get(10));
        displayedTasks.add(floatingTaskEntities.get(8));
        displayedTasks.add(floatingTaskEntities.get(15));
        displayedTasks.add(floatingTaskEntities.get(3));
        displayedTasks.add(floatingTaskEntities.get(6));
    }
    
    /**
     * TESTING FUNCTION
     * Populates the displayedTasks and taskEntities array with fake data for
     * testing
     */
    private static void populateArray() {
        for (int i = 0; i < 30; i++) {
            TaskEntity new_task = new TaskEntity("Task " + Integer.toString(i));
            floatingTaskEntities.add(new_task);
        }
        displayedTasks = (ArrayList<TaskEntity>) floatingTaskEntities.clone();
    }
    
    /**
     * Testing function to print out the array contents
     */
    private static void printList()
    {
        String output = "";
        if(displayedTasks == null){
            displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities;
        }
        
        System.out.println("Display");
        int j = 0;
        for(int i = 0; i < displayedTasks.size(); i++)
        {
            System.out.print(Utils.convertDecToBase36(i) + ". " + displayedTasks.get(i).getName() + "     ");
            j++;
            if(j >= 4){
                System.out.println();
                j = 0;
            }
        }
        
        System.out.println();
        System.out.println("Floating");
        j = 0;
        for(int i = 0; i < floatingTaskEntities.size(); i++)
        {
            System.out.print(Utils.convertDecToBase36(i) + ". " + floatingTaskEntities.get(i).getName() + "     ");
            j++;
            if(j >= 4){
                System.out.println();
                j = 0;
            }
        }
        
        System.out.println();
        System.out.println("Time based");
        j = 0;
        for(int i = 0; i < mainTaskEntities.size(); i++)
        {
            System.out.print(Utils.convertDecToBase36(i) + ". " + mainTaskEntities.get(i).getName() + "     ");
            j++;
            if(j >= 4){
                System.out.println();
                j = 0;
            }
        }
    }

    /**
     * Testing function for JUnit test to check the output
     */
    public static String printListToString()
    {
        String output = "";
        if(displayedTasks == null){
            displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
        }
        
        output += "Display\n";
        int j = 0;
        for(int i = 0; i < displayedTasks.size(); i++)
        {
            output += Utils.convertDecToBase36(i) + ". " + displayedTasks.get(i).getName() + "     ";
            j++;
            if(j >= 4){
                output += "\n";
                j = 0;
            }
        }
        
        output+= "\nFloating\n";
        j = 0;
        for(int i = 0; i < floatingTaskEntities.size(); i++)
        {
            output += Utils.convertDecToBase36(i) + ". " + floatingTaskEntities.get(i).getName() + "     ";
            j++;
            if(j >= 4){
                output += "\n";
                j = 0;
            }
        }
        
        output += "\nTime based\n";
        
        j = 0;
        for(int i = 0; i < mainTaskEntities.size(); i++)
        {
            output += Utils.convertDecToBase36(i) + ". " + mainTaskEntities.get(i).getName() + "     ";
            j++;
            if(j >= 4){
                output += "\n";
                j = 0;
            }
        }
        return output;
    }

    /**
     * UI Interface function
     * Gets the currently displayed list of tasks
     * 
     * @return ArrayList containing the list of time sorted tasks
     */
    public static ArrayList<TaskEntity> getWorkingList() {
        displayedTasks = mainTaskEntities;
        return displayedTasks;
    }

    /**
     * UI Interface function
     * Modifies the selected task, effectively deleting it and adding a new
     * task. Base10 version
     * 
     * @param index - int(in base10) index of task to be modified
     * @param modifiedTask - New data of the task
     * @return id of new position of the modified task in the display list if
     *         succeeded in deleting the task, returns -1 if deletion failed
     */
    public static int modify(int index, TaskEntity modifiedTask) {
        if (delete(index) == false) {
            return -1;
        }
        return add(modifiedTask);
    }

    /**
     * UI Interface function
     * Modifies the selected task, effectively deleting it and adding a new
     * task. Base36 version
     * 
     * @param index - index(in base36) of task to be modified in string
     * @param modifiedTask - new data of the task
     * @return id of new position of the modified task in the display list if
     *         succeeded in deleting the task, returns -1 otherwise
     */     
    public static int modify(String index, TaskEntity modifiedTask) {
        return modify(Utils.convertBase36ToDec(index), modifiedTask);
    }
    
    /**
     * UI Interface function
     * Insert an arrayList of tasks using the same rules as adding a single task
     * 
     * @param tasks - An arrayList of the tasks to be added
     * @return  ID of the first task that was inserted
     */
    public static int add(ArrayList<TaskEntity> tasks)
    {
        int firstIndex = -1;
        if(tasks.size() >= 1) {
            firstIndex = add(tasks.get(0));
        }
        
        for(int i = 1; i < tasks.size(); i++) {
            add(tasks.get(i));
        }
        
        return firstIndex;
    }
    
    /**
     * UI Interface function
     * Adds a task into its respective arraylist, appending to the bottom of
     * floatingTaskEntities if it is a floating task, and inserting into its
     * sorted position if it is a timed task
     * 
     * @param newTask - Task to be inserted
     * @return ID of the task that has been inserted
     */
    public static int add(TaskEntity newTask) {
        if(newTask.isFloating()){
            floatingTaskEntities.add(newTask);
            return floatingTaskEntities.size() - 1;
        }else{
            int idToInsert = findPositionToInsert(newTask);
            mainTaskEntities.add(idToInsert, newTask);
            return idToInsert;
        }
    }

    /**
     * UI Interface function
     * Searches for the position to insert a newTask into a sorted list of timed
     * tasks using rules dictated in TaskDateComparator
     * 
     * @param newTask - The task object to be sorted
     * @return ID of the position where the task should be placed in the sorted list
     */
    private static int findPositionToInsert(TaskEntity newTask) {
        int idToInsert = Collections.binarySearch(mainTaskEntities, newTask, new TaskDateComparator());

        // Due to Collections.binarySearch's implementation, all objects
        // that can't be found will return a negative value, which indicates
        // the position where the object that is being searched is supposed
        // to be minus 1. This if case figures out the position to slot it in
        if (idToInsert < 0) {
            idToInsert = -(idToInsert+1);
        }
        return idToInsert;
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
        if(displayedTasks == null) {
            displayedTasks = mainTaskEntities;
        }
        
        if (index + 1 > displayedTasks.size()) {
            return false;
        }
        
        TaskEntity itemToBeDeleted = displayedTasks.get(index);
        boolean deletionSuccess = deleteFromMainList(itemToBeDeleted);
        if(!deletionSuccess){
            return false;
        }
        
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
     * UI Interface function
     * Deletes a list of tasks from the arrayList
     * 
     * @param index - A string containing a base36 number
     * @return - True if delete operation succeeded
     *         - False if delete operation failed
     */
    public static boolean delete(String index) {
        return delete(Utils.convertBase36ToDec(index));
    }   

    /**
     * UI Interface function
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
        if (checkValidIndex(startIndex, endIndex) == false) {
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
     * Checks if the Index passed in for deletion is a valid index
     * 
     * @param startIndex - Index of the first item in the range to be deleted
     * @param endIndex - Index of the last item in the range to be deleted
     * @return
     */
    private static boolean checkValidIndex(int startIndex, int endIndex) {
        if (endIndex + 1 > displayedTasks.size()) {
            return false;
        } else if (startIndex > endIndex) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * UI Interface function
     * Deletes a consecutive list of tasks from both the working and main
     * arrayList. Upon failing to delete any item, the function terminates at
     * the object that it fails to delete and does not attempt to delete anymore
     * items
     * 
     * @param startIndex - A string containing a base36 number that represents
     *            the index of the first entry of all the entries to be deleted
     * @param endIndex - A string containing a base36 number that represents
     *            the index of the last entry of all the entries to be deleted
     * @return - True if delete operation succeeded
     *         - False if delete operation failed
     */
    public static boolean delete(String startIndex, String endIndex) {
        return delete(Utils.convertBase36ToDec(startIndex), Utils.convertBase36ToDec(endIndex));
    }
    
    /**
     * UI Interface function
     * Gets the ID number of the next task in time order
     * 
     * @return ID of the task that is next, counting from the current time
     */
    public static int getNextTimeListId() {
        TaskEntity currentTimePlaceholder = new TaskEntity("", Calendar.getInstance(), false);
        int nextTimeId = findPositionToInsert(currentTimePlaceholder);
        return nextTimeId;
    }

    /**
     * UI Interface function
     * Undoes the last command performed by the user
     * 
     * @return The currently displayed list after the undo operation
     */
    public static ArrayList<TaskEntity> undo() {
        // TODO
        return getWorkingList();
    }
    
    //ys method, do not remove, ys will remove it ^_^
    // generate fake data.
    public ArrayList<TaskEntity> generateFakeData() {
        int k = 0;
        int day = Calendar.getInstance().get(Calendar.DATE);
        while (k <200) {
            Random r = new Random();
            int monthBuffer = r.nextInt(12);
            int dateBuffer = r.nextInt(31);
            int hourBuffer = r.nextInt(12);
            
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, monthBuffer);
            c.set(Calendar.DATE, dateBuffer);
            c.set(Calendar.HOUR, hourBuffer);
            
            TaskEntity t = new TaskEntity(Integer.toString(k++), c, false, "yea");
            add(t);
        }

        System.out.println(k + " Fake data created");
        return mainTaskEntities;
    }
}
