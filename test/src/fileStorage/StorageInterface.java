package fileStorage;

import entity.AllTaskLists;
public interface StorageInterface {
    
    AllTaskLists getTaskLists();
    
    Boolean storeTaskLists(AllTaskLists atl);
}
