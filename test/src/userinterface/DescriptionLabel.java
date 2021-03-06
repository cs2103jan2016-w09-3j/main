// @@ A0125514N
package userinterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import entity.TaskEntity;

public class DescriptionLabel {

    private double _height;
    private TaskEntity _startTask;
    private TaskEntity _endTask;
    private boolean isSelected = false;

    public DescriptionLabel(TaskEntity startTask) {
        this._startTask = startTask;
        this._endTask = null;
        _height = 0;
    }

    public DescriptionLabel(TaskEntity startTask, TaskEntity endTask) {
        this._startTask = startTask;
        this._endTask = endTask;
        _height = 0;
    }

    public void increaseHeight(double value) {
        _height = _height + value;
    }

    public double getHeight() {
        return _height;
    }

    public void setHeight(double height) {
        this._height = height;
    }

    public TaskEntity getTask() {
        return _startTask;
    }

    public void setTask(TaskEntity taskEntity) {
        this._startTask = taskEntity;
    }

    public void setSelected() {
        isSelected = true;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getFullDayLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM EEEEE");
        Calendar c = _startTask.getDueDate();
        return sdf.format(c.getTime());
    }

    public String getMediumDayLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM EEE");
        Calendar c = _startTask.getDueDate();
        return sdf.format(c.getTime());
    }

    public String getSmallDayLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        Calendar c = _startTask.getDueDate();
        return sdf.format(c.getTime());
    }

    public String getFullWeekLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d");
        Calendar sCalendar = _startTask.getDueDate();
        String weekString = null;
        if (_endTask != null) {
            Calendar eCalendar = _endTask.getDueDate();
            weekString = sdf.format(sCalendar.getTime()) + " - " + sdf.format(eCalendar.getTime());
        } else {
            weekString = sdf.format(sCalendar.getTime()) + " Week " + sCalendar.get(Calendar.WEEK_OF_MONTH);
        }
        return weekString;
    }

    public String getMediumWeekLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        Calendar sCalendar = _startTask.getDueDate();
        String weekString = null;
        if (_endTask != null) {
            Calendar eCalendar = _endTask.getDueDate();
            weekString = sdf.format(sCalendar.getTime()) + " - " + sdf.format(eCalendar.getTime());
        } else {
            weekString = sdf.format(sCalendar.getTime()) + " Week " + sCalendar.get(Calendar.WEEK_OF_MONTH);
        }
        return weekString;
    }

    public String getSmallWeekLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        Calendar c = _startTask.getDueDate();
        String weekString = sdf.format(c.getTime()) + "W" + c.get(Calendar.WEEK_OF_MONTH);
        return weekString;
    }

}
