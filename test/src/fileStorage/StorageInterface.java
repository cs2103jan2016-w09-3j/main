package fileStorage;

import java.util.ArrayList;

import entity.AllTaskLists;

public interface StorageInterface {
    
    AllTaskLists getTaskLists();
    
    Boolean storeTaskLists(AllTaskLists atl);
}
