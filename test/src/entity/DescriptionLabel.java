package entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DescriptionLabel {

    private double height;
    private Task task;
    private boolean selected = false;

    public DescriptionLabel(Task task) {
        this.task = task;
        height = 0;
    }

    public void increaseHeight(double value) {
        height = height + value;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setSelected() {
        selected = true;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getFullLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd EEEE");    
        Calendar c = task.getDueDate();
        return sdf.format(c.getTime());
    }
    
    public String getMediumLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd EEE");    
        Calendar c = task.getDueDate();
        return sdf.format(c.getTime());
    }
    
    public String getSmallLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");    
        Calendar c = task.getDueDate();
        return sdf.format(c.getTime());
    }

}
