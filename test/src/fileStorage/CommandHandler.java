package fileStorage;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class CommandHandler extends TimerTask {
    
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private static final int QUEUE_SIZE = 20;
    private StorageHandler mfh;
    
    public CommandHandler() {
        mfh = new StorageHandler();
    }
    
    public boolean saveUponFullQueue(String command) {
        boolean isSaved = false;
        Queue<String> commandsQueue = mfh.getAllCommandsQueue();
        commandsQueue.offer(command);
        if (commandsQueue.size() >= QUEUE_SIZE) {
            isSaved = mfh.writeToCommandFile(commandsQueue);
            commandsQueue.clear();
        }
        return isSaved;
    }
    
    public boolean saveUponExit(boolean isExit) {
        boolean isSaved = false;
        if (isExit == true) {
            System.out.println("Saved upon exit.");
            isSaved = mfh.writeToCommandFile(mfh.getAllCommandsQueue());
        }
        return isSaved;
    }
    
    public void run() {
        System.out.println(mfh.writeToCommandFile(mfh.getAllCommandsQueue()));
    }
    
    public void saveUponTimeOut() {
        TimerTask saveInterval = new CommandHandler();
        Timer timer = new Timer();
        
        timer.schedule(saveInterval, 0, 20*MILLISECONDS_TO_SECONDS); 
    }
}
