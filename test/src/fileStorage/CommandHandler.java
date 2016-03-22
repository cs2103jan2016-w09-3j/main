package fileStorage;

import java.util.Queue;

public class CommandHandler {
    
    private StorageHandler mfh;
    
    public CommandHandler() {
        mfh = new StorageHandler();
    }
    
    public boolean saveCommand(String command) {
        return mfh.writeToCommandFile(command);
    }
    
    public Queue<String> retrieveCommand() {
        return mfh.getAllCommandsQueue();
    }
    
    public void clearCommandFile() {
        mfh.clearCommandFileUponExit();
    }
}
