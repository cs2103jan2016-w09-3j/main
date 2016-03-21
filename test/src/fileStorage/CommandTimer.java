package fileStorage;

import java.util.Timer;
import java.util.TimerTask;

public class CommandTimer extends TimerTask {
    public void run() {
        System.out.println("Timer print.");
    }
    
    public static void main (String args[]) {
        TimerTask saveInterval = new CommandTimer();
        Timer timer = new Timer();
        
        timer.schedule(saveInterval, 0, 1000);
    }
}
