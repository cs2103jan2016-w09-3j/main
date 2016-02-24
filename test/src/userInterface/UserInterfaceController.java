package userInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import entity.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserInterfaceController {

    private Stage _parentStage;
    private TaskViewUserInterface _taskViewInterface;
    private DescriptionComponent _descriptionComponent;
    private DetailComponent _detailComponent;
    private FloatingBarViewUserInterface _floatingBarComponent;

    public UserInterfaceController(Stage primaryStage) {
        _parentStage = primaryStage;
    }

    public void initializeInterface(Rectangle2D _screenBounds, boolean _fixedSize) {
        _taskViewInterface = new TaskViewUserInterface(_parentStage, _screenBounds, _fixedSize);
        _descriptionComponent = new DescriptionComponent(_parentStage, _screenBounds, _fixedSize);
        _floatingBarComponent = new FloatingBarViewUserInterface(_parentStage, _screenBounds, _fixedSize);
        _detailComponent = new DetailComponent(_parentStage, _screenBounds, _fixedSize);
        _taskViewInterface.buildComponent(generateFakeData());
        update(0);
    }

    public void show() {
        _taskViewInterface.show();
        _descriptionComponent.show();
        _floatingBarComponent.show();
        _detailComponent.show();
    }

    int selectedStartIndex;
    int selectedEndIndex;

    public void update(int value) {
        _taskViewInterface.update(value);
        _taskViewInterface.setItemSelected(value);
        translateComponentsY(_taskViewInterface.getTranslationY());
        _descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabels(), 0);

    }

    public void translateComponentsY(double value) {
        _taskViewInterface.updateTranslateY(value);
        _descriptionComponent.updateTranslateY(value);
    }

    public void move(int value) {
        if (value > 0) {
            double t = _taskViewInterface.getMainLayoutComponent().getTranslateY() + 50;
            _taskViewInterface.updateTranslateY(t);
            _descriptionComponent.updateTranslateY(t);
        } else {
            double t = _taskViewInterface.getMainLayoutComponent().getTranslateY() - 50;
            _taskViewInterface.updateTranslateY(t);
             _descriptionComponent.updateTranslateY(t);
        }
    }

    // deetle this method after qy implement.
    public static Label checkSameDay(Task task1, Task task2) {
        if (task1 == null) { // new day
            return new Label(task2.getDueDate().toString());
        } else {
            if (task1.getDueDate().get(Calendar.YEAR) == task2.getDueDate().get(Calendar.YEAR)) {
                if (task1.getDueDate().get(Calendar.MONTH) == task2.getDueDate().get(Calendar.MONTH)) {
                    if (task1.getDueDate().get(Calendar.DATE) == task2.getDueDate().get(Calendar.DATE)) {
                        return null;
                    }
                }
            }
        }
        return new Label(task2.getDueDate().toString());
    }

    // generate fake data.
    public static ArrayList<Task> generateFakeData() {
        ArrayList<Task> fakeData = new ArrayList<Task>();
        int k = 0;
        int day = Calendar.getInstance().get(Calendar.DATE);
        while (k < 200) {
            Random r = new Random();
            int loop = r.nextInt(2);
            for (int kk = 0; kk < loop; kk++) {
                Random rr = new Random();
                int ind = rr.nextInt(5);
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DATE, ++day);
                for (int i = 0; i < ind; i++) {
                    String d = (k) + " - - - " + Integer.toString(c.get(Calendar.DAY_OF_MONTH)) + "/"
                            + Integer.toString(c.get(Calendar.MONTH));
                    Task t = new Task(Integer.toString(k++), c, d);
                    fakeData.add(t);
                }
            }
        }

        System.out.println(k + " Fake data created");
        return fakeData;
    }

}
