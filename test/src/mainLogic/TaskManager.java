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
        
        while(true){
            System.out.println(getNextTimeListId());
        }
    }
    
    /**
     * TEST FUNCTION
     * Prints out the 2 arraylists
     */
    private static void testDisplay(){
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
     * Populates the displayedTasks and taskEntities array with fake data for testing
     */
    private static void populateArray() {
        for(int i = 0; i < 30; i++)
        {
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
     * @return ArrayList containing the list of time sorted tasks
     */
    public static ArrayList<TaskEntity> getWorkingList() {
        displayedTasks = mainTaskEntities;
        return displayedTasks;
    }

    /**
     * Modifies the selected task, effectively deleting it and adding a new
     * task. Base10 version
     * 
     * @param index - int(in base10) index of task to be modified
     * @param modifiedTask - New data of the task
     * @return id of new position of the modified task in the display list if
     *         succeeded in deleting the task, returns -1 otherwise
     */
    public static int modify(int index, TaskEntity modifiedTask) {
        if (delete(index) == false) {
            return -1;
        }
        return add(modifiedTask);
    }

    /**
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
    
    public static int add(TaskEntity newTask) {
        if(newTask.isFloating()){
            floatingTaskEntities.add(newTask);
            return floatingTaskEntities.size();
        }else{
            int idToInsert = Collections.binarySearch(mainTaskEntities, newTask, new TaskDateComparator());

            // Due to Collections.binarySearch's implementation, all objects
            // that can't be found will return a negative value, which indicates
            // the position where the object that is being searched is supposed
            // to be minus 1. This if case figures out the position to slot it in
            if (idToInsert < 0) {
                idToInsert = -(idToInsert+1);
            }
            mainTaskEntities.add(idToInsert, newTask);
            return idToInsert;
        }
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
        return delete(Utils.convertBase36ToDec(startIndex), Utils.convertBase36ToDec(endIndex));
    }
    
    /**
     * Gets the ID number of the next task in time order
     * @return ID of the task that is next, counting from the current time
     */
    public static int getNextTimeListId() {
        TaskEntity currentTimePlaceholder = new TaskEntity("", Calendar.getInstance(), false);
        int nextTimeId = Collections.binarySearch(mainTaskEntities, currentTimePlaceholder, new TaskDateComparator());

        // Due to Collections.binarySearch's implementation, all objects
        // that can't be found will return a negative value, which indicates
        // the position where the object that is being searched is supposed
        // to be minus 1. This if case figures out the position to slot it in
        if (nextTimeId < 0) {
            nextTimeId = -(nextTimeId+1);
        }
        return nextTimeId;
    }

    public static ArrayList<TaskEntity> undo() {
        // TODO
        return new ArrayList<TaskEntity>();
    }
    
    //ys method, do not remove, ys will remove it ^_^
    // generate fake data.
    public ArrayList<TaskEntity> generateFakeData() {
        int k = 0;
        int day = Calendar.getInstance().get(Calendar.DATE);
        while (k <200) {
            Random r = new Random();
            int loop = r.nextInt(2);
            for (int kk = 0; kk < loop; kk++) {
                Random rr = new Random();
                int ind = rr.nextInt(5);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DATE, ++day);
                for (int i = 0; i < ind; i++) {
                    String d = (k) + " - - - " + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/"
                            + Integer.toString(c.get(Calendar.MONTH));
                    TaskEntity t = new TaskEntity(Integer.toString(k++), c, false, d);
                    add(t);
                }
            }
        }

        System.out.println(k + " Fake data created");
        return mainTaskEntities;
    }
}
