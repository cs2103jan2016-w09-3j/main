package fileStorage;

public class CommandHandler {
    
    private static final int QUEUE_SIZE = 5;
    private static final int SEARCH_VIEW = 4;
    
    public CommandHandler() {
        
    }
    
    /**
     * Store input command into command file 
     * Re-writes main file if command queue is full
     * Current queue size is 5 for testing
     * @param command
     * @return isFullQueue
     */
    public boolean saveUponFullQueue(String command, int queueSize) {
        boolean isFullQueue = false;
        
        String[] splitCommand = command.split(" ");
        if (splitCommand[0].equals(SEARCH_VIEW) == false && queueSize >= QUEUE_SIZE) {
            isFullQueue = true;
        }
        return isFullQueue;
    }
    
}
