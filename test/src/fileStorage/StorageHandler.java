package fileStorage;

import entity.AllTaskLists;

public class StorageHandler implements StorageInterface {
    public static void main (String args[]) {
        FileHandler fm = new FileHandler();
        System.out.println(fm.getFilePath());
        fm.writeToFile("Testing a file");
        System.out.println(fm.readFromExistingFile());
    }

    public AllTaskLists getTaskLists() {
        // TODO Auto-generated method stub
        return null;
    }

    public Boolean storeTaskLists(AllTaskLists atl) {
        // TODO Auto-generated method stub
        return null;
    }
}
