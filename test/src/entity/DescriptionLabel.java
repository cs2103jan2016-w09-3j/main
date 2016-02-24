package entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DescriptionLabel {

    private double _height;
    private Task _startTask;
    private Task _endTask;
    private boolean selected = false;

    public DescriptionLabel(Task startTask, Task endTask) {
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

    public Task getTask() {
        return _startTask;
    }

    public void setTask(Task task) {
        this._startTask = task;
    }

    public void setSelected() {
        selected = true;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getFullDayLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd EEEE");
        Calendar c = _startTask.getDueDate();
        return sdf.format(c.getTime());
    }

    public String getMediumDayLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd EEE");
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
                weekString = sdf.format(sCalendar.getTime())+" - "+sdf.format(eCalendar.getTime());
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
                weekString = sdf.format(sCalendar.getTime())+" - "+sdf.format(eCalendar.getTime());
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

    private boolean isSameMonth(Calendar c1, Calendar c2) {
        if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
            return true;
        }
        return false;
    }
}
