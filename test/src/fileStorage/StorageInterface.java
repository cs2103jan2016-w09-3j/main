package fileStorage;

import java.util.ArrayList;

import entity.AllTaskLists;
import entity.TaskEntity;

public interface StorageInterface {
    
    AllTaskLists getTaskLists();
    
    boolean storeTaskLists(AllTaskLists allTaskLists);
    
    boolean storeTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating);
}
