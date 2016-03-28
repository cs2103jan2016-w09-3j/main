package mainLogic;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import fileStorage.StorageController;
import fileStorage.StorageHandler;

public class CommandHandler extends TimerTask {
    
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private static final int QUEUE_SIZE = 5;
    private StorageHandler storageHandler;
    private StorageController storageController;
    
    public CommandHandler() {
        storageHandler = new StorageHandler();
    }
    
    private boolean storeCommand(String command) {
        return storageHandler.writeToCommandFile(command);
    }
    
    /**
     * Store input command into command file 
     * Re-writes main file if command queue is full
     * Current queue size is 5 for testing
     * @param command
     * @return isSavedMain
     */
    public boolean saveUponFullQueue(String command) {
        boolean isSavedMain = false;
        boolean isSavedCommand = storeCommand(command);
        assert isSavedCommand == true : "Command not saved.";
        storageHandler.getAllCommandsQueue().offer(command);
        if (storageHandler.getAllCommandsQueue().size() >= QUEUE_SIZE) {
            //isSavedMain = storageController.storeTaskLists();
            storageHandler.clearCommandFileUponCommit();
            storageHandler.getAllCommandsQueue().clear();
        }
        return isSavedMain;
    }
    
    public Queue<String> retrieveCommand() {
        return storageHandler.getAllCommandsQueue();
    }
    
    public void run() {
        //boolean isSavedMain = storageController.storeTaskLists();
        storageHandler.clearCommandFileUponCommit();
        storageHandler.getAllCommandsQueue().clear();
        //assert isSavedMain == true;
        //System.out.println(isSavedMain);
    }
    
    /**
     * Runs a thread to store all working task lists every 5 seconds
     * 5 seconds is for testing, after that will change to 30 minutes interval
     */
    public void commitUponTimeOut() {
        TimerTask saveInterval = new CommandHandler();
        Timer timer = new Timer();
        
        timer.schedule(saveInterval, 5*MILLISECONDS_TO_SECONDS, 5*MILLISECONDS_TO_SECONDS); 
    }
}
