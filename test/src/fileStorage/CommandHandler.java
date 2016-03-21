package fileStorage;

import java.util.Timer;
import java.util.TimerTask;

public class CommandHandler extends TimerTask {
    
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private StorageHandler mfh;
    private int i;
    
    public CommandHandler() {
        mfh = new StorageHandler();
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
        System.out.println(i);
        i++;
    }
    
    public void saveUponTimeOut() {
        TimerTask saveInterval = new CommandHandler();
        Timer timer = new Timer();
        
        timer.schedule(saveInterval, 0, 20*MILLISECONDS_TO_SECONDS); 
    }
    
    public void saveUponFullQueue() {
        
    }
}
