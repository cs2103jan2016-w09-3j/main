package fileStorage;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class CommandHandler extends TimerTask {
    
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private StorageHandler mfh;
    private StorageController sc;
    
    public CommandHandler() {
        mfh = new StorageHandler();
    }
    
    public boolean saveCommand(String command) {
        return mfh.writeToCommandFile(command);
    }
    
    /*public boolean saveUponFullQueue(String command) {
        boolean isSaved = false;
        mfh.getAllCommandsQueue().offer(command);
        if (mfh.getAllCommandsQueue().size() >= QUEUE_SIZE) {
            isSaved = mfh.storeTaskLists();
        }
        return isSaved;
    }*/
    
    public Queue<String> retrieveCommand() {
        return mfh.getAllCommandsQueue();
    }
    
    public void clearCommandFile() {
        mfh.clearCommandFileUponCommit();
    }
    
    public void run() {
        /*boolean isSaved = sc.storeTaskLists();
        mfh.clearCommandFileUponCommit();
        System.out.println(isSaved);*/
    }
    
    public void commitUponTimeOut() {
        TimerTask saveInterval = new CommandHandler();
        Timer timer = new Timer();
        
        timer.schedule(saveInterval, 20*MILLISECONDS_TO_SECONDS, 20*MILLISECONDS_TO_SECONDS); 
    }
}
