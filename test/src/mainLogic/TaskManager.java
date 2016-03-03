package mainLogic;

import java.util.ArrayList;
import java.util.Calendar;

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
            displayedTasks = (ArrayList<TaskEntity>) mainTaskEntities.clone();
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

    public static ArrayList<TaskEntity> getWorkingList() {
        return displayedTasks;
    }

    public static void add(TaskEntity newTask) {
        if(newTask.isFloating()){
            floatingTaskEntities.add(newTask);
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

    public static ArrayList<TaskEntity> undo() {
        // TODO
        return new ArrayList<TaskEntity>();
    }
}
