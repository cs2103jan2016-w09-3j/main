package entity;

import java.util.ArrayList;

public class AllTaskLists {
    
    private ArrayList<String> mainTaskList;
    private ArrayList<String> floatingTaskList;
    
    public AllTaskLists(ArrayList<String> main, ArrayList<String> floating) {
        setMainTaskList(new ArrayList<String>(main));
        setFloatingTaskList(new ArrayList<String>(floating));
    }

    public ArrayList<String> getMainTaskList() {
        return mainTaskList;
    }

    public void setMainTaskList(ArrayList<String> mainTaskList) {
        this.mainTaskList = mainTaskList;
    }

    public ArrayList<String> getFloatingTaskList() {
        return floatingTaskList;
    }

    public void setFloatingTaskList(ArrayList<String> floatingTaskList) {
        this.floatingTaskList = floatingTaskList;
    }
}
