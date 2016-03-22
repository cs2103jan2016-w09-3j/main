package fileStorage;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class CommandHandler extends TimerTask {
    
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private static final int QUEUE_SIZE = 5;
    private StorageHandler mfh;
    
    public CommandHandler() {
        mfh = new StorageHandler();
    }
    
    public boolean saveUponFullQueue(String command) {
        boolean isSaved = false;
        System.out.println("Offer " + command);
        mfh.getAllCommandsQueue().offer(command);
        if (mfh.getAllCommandsQueue().size() >= QUEUE_SIZE) {
            isSaved = mfh.writeToCommandFile();
            System.out.println("Saved upon queue maxed.");
        }
        System.out.println("Queue size " + mfh.getAllCommandsQueue().size());
        return isSaved;
    }
    
    public boolean saveUponExit(boolean isExit) {
        boolean isSaved = false;
        if (isExit == true) {
            System.out.println("Saved upon exit.");
            isSaved = mfh.writeToCommandFile();
        }
        System.out.println("Queue size " + mfh.getAllCommandsQueue().size());
        return isSaved;
    }
    
    public void run() {
        System.out.println(mfh.writeToCommandFile());
        System.out.println("Saved upon time out.");
        System.out.println("Queue size " + mfh.getAllCommandsQueue().size());
    }
    
    public void saveUponTimeOut() {
        TimerTask saveInterval = new CommandHandler();
        Timer timer = new Timer();
        
        timer.schedule(saveInterval, 20*MILLISECONDS_TO_SECONDS, 20*MILLISECONDS_TO_SECONDS); 
    }
}
