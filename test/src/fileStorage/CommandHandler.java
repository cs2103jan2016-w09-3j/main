package fileStorage;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import fileStorage.StorageInterface;

public class CommandHandler {
    
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private static final int QUEUE_SIZE = 5;
    private static final int SEARCH_VIEW = 4;
    private StorageInterface storageInterface;
    
    public CommandHandler() {
        storageInterface = new StorageInterface();
    }
    
    /**
     * Store input command into command file 
     * Re-writes main file if command queue is full
     * Current queue size is 5 for testing
     * @param command
     * @return isFullQueue
     */
    public boolean saveUponFullQueue(String command) {
        boolean isFullQueue = false;
        
        boolean isSaved = storageInterface.storeCommandLine(command);
        assert isSaved == true : "Not commited to main file.";
      
        Queue<String> newCommandsQueue = storageInterface.getCommandsQueue();
        newCommandsQueue.offer(command);
        storageInterface.setCommandsQueue(newCommandsQueue);
        
        String[] splitCommand = command.split(" ");
        if (splitCommand[0].equals(SEARCH_VIEW) == false && newCommandsQueue.size() >= QUEUE_SIZE) {
            isFullQueue = true;
        }
        return isFullQueue;
    }
    
}
