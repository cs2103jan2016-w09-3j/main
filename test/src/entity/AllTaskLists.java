package entity;

import java.util.ArrayList;

public class AllTaskLists {
    
    private ArrayList<TaskEntity> mainTaskList;
    private ArrayList<TaskEntity> floatingTaskList;
    
    public AllTaskLists() {
        
    }
    
    public AllTaskLists(ArrayList<TaskEntity> main, ArrayList<TaskEntity> floating) {
        setMainTaskList(main);
        setFloatingTaskList(floating);
    }

    public ArrayList<TaskEntity> getMainTaskList() {
        return mainTaskList;
    }

    public void setMainTaskList(ArrayList<TaskEntity> mainTaskList) {
        this.mainTaskList = mainTaskList;
    }

    public ArrayList<TaskEntity> getFloatingTaskList() {
        return floatingTaskList;
    }

    public void setFloatingTaskList(ArrayList<TaskEntity> floatingTaskList) {
        this.floatingTaskList = floatingTaskList;
    }
}
