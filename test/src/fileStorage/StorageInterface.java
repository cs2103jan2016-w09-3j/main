package fileStorage;

import java.util.ArrayList;
import java.util.Queue;

import entity.AllTaskLists;
import entity.TaskEntity;

public interface StorageInterface {
    
    AllTaskLists getTaskLists();
    
    boolean storeTaskLists(AllTaskLists allTaskLists);
    
    boolean storeTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating);
    
    Queue<String> getCommandsUponInit();
    
    Queue<String> getCommandsQueue();
    
    void setCommandsQueue(Queue<String> newCommandsQueue);
    
    boolean storeCommandLine(String command);
    
    void clearCommandFile();
}
