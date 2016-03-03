package userInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import entity.TaskEntity;
import mainLogic.Utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserInterfaceController {

    private final static int CALENDAR_VIEW = 0;
    final static int TASK_VIEW = 1;
    final static int DETAILED_VIEW = 2;

    private Stage _parentStage;
    private TaskViewUserInterface _taskViewInterface;
    private DescriptionComponent _descriptionComponent;
    private DetailComponent _detailComponent;
    private FloatingBarViewUserInterface _floatingBarComponent;
    private Rectangle2D _screenBounds;
    private boolean _fixedSize;

    // var for animation and changing views;
    private int _currentView = TASK_VIEW;
    private boolean _isDoneTranslatingToOtherView;
    private Thread _threadToAnimate;

    public UserInterfaceController(Stage primaryStage) {
        _parentStage = primaryStage;
    }

    public void initializeInterface(Rectangle2D screenBounds, boolean fixedSize) {
        this._screenBounds = screenBounds;
        this._fixedSize = fixedSize;
        initializeTaskView();
        show();
    }

    public void initializeTaskView() {
        _taskViewInterface = new TaskViewUserInterface(_parentStage, _screenBounds, _fixedSize);
        _descriptionComponent = new DescriptionComponent(_parentStage, _screenBounds, _fixedSize);
        _floatingBarComponent = new FloatingBarViewUserInterface(_parentStage, _screenBounds, _fixedSize);
        _detailComponent = new DetailComponent(_parentStage, _screenBounds, _fixedSize);
        _taskViewInterface.buildComponent(generateFakeData());
        update(0);
    }

    public void show() {
        if (_currentView == TASK_VIEW) {
            _taskViewInterface.show();
            _descriptionComponent.show();
            _floatingBarComponent.show();
            _detailComponent.show();
        } else if (_currentView == DETAILED_VIEW) {
            _taskViewInterface.show();
            _descriptionComponent.show();
            _floatingBarComponent.show();
            _detailComponent.show();
        }
    }

    public void destory() {
        _taskViewInterface.destoryStage();
        _descriptionComponent.destoryStage();
        _floatingBarComponent.destoryStage();
        _detailComponent.destoryStage();
    }

    public void update(int value) {
        _taskViewInterface.update(value);
        _taskViewInterface.setItemSelected(value);
        translateComponentsY(_taskViewInterface.getTranslationY());
        updateDescriptionComponent();
    }

    public void updateDescriptionComponent() {
        if (_currentView == TASK_VIEW) {
            _descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(),
                    TASK_VIEW);
        } else if (_currentView == DETAILED_VIEW) {
            _descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(),
                    DETAILED_VIEW);
        }
    }

    public void translateComponentsY(double value) {
        _taskViewInterface.updateTranslateY(value);
        _descriptionComponent.updateTranslateY(value);
    }

    public void jumpToIndex(String indexZZ) {
        int index = Integer.parseInt(indexZZ);
        _taskViewInterface.jumpToIndex(index);
        _taskViewInterface.setItemSelected(0);
       
        Thread t = new Thread(jumpToIndexAnimation());
        t.start();
    }

    public Task<Void> jumpToIndexAnimation() {
        _isDoneTranslatingToOtherView = false;
        Task<Void> jumpToIndexAnimation = new Task<Void>() {
            @Override
            public Void call() {
                do {
                    int secondsToAnimate = 3;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    Platform.runLater(new Runnable() {
                        public void run() {
                            if (_currentView == TASK_VIEW || _currentView == DETAILED_VIEW) {
                                if (_currentView == DETAILED_VIEW) {

                                } else {
                                    _descriptionComponent.buildComponent(
                                            _taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
                                    double currentMainY = _taskViewInterface.getMainLayoutComponent()
                                            .getTranslateY();
                                    double selectedItemY = _taskViewInterface.getTranslationY();

                                    if (currentMainY < selectedItemY) {
                                        translateComponentsY(currentMainY + 10);
                                        _descriptionComponent.buildComponent(
                                                _taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
                                    } else {
                                        _isDoneTranslatingToOtherView = true;
                                    }
                                }
                            }
                        }
                    });
                } while (!_isDoneTranslatingToOtherView);
                return null;
            }
        };
        return jumpToIndexAnimation;
    }

    public Task<Void> animateTransitionForView() {
        _isDoneTranslatingToOtherView = false;
        Task<Void> animateChangeView = new Task<Void>() {
            @Override
            public Void call() {
                do {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    Platform.runLater(new Runnable() {
                        public void run() {
                            if (_currentView == TASK_VIEW || _currentView == DETAILED_VIEW) {
                                if (_currentView == DETAILED_VIEW) {
                                    _isDoneTranslatingToOtherView = _taskViewInterface.isAtDetailedView(1);
                                    _descriptionComponent.buildComponent(
                                            _taskViewInterface.rebuildDescriptionLabelsForDay(),
                                            DETAILED_VIEW);
                                } else {
                                    _isDoneTranslatingToOtherView = _taskViewInterface.isAtTaskView(-1);
                                    _descriptionComponent.buildComponent(
                                            _taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
                                }
                                translateComponentsY(_taskViewInterface.getTranslationY());
                            }
                        }
                    });
                } while (!_isDoneTranslatingToOtherView);
                return null;
            }
        };
        return animateChangeView;
    }

    public void changeView(int value) {
        int view = _currentView + value;
        switch (view) {
            case CALENDAR_VIEW : {
                _currentView = view;
                break;
            }
            case TASK_VIEW : {
                _currentView = view;
                _taskViewInterface.setView(_currentView);
                startThreadToAnimate();
                break;
            }
            case DETAILED_VIEW : {
                _currentView = view;
                _taskViewInterface.setView(_currentView);
                startThreadToAnimate();
                break;
            }
            default :
                break;
        }
    }

    public void startThreadToAnimate() {
        _threadToAnimate = new Thread(animateTransitionForView());
        _threadToAnimate.start();
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

    /**
     * This method will call utils and check if the both task have the same date
     * 
     * @param task1
     * @param task2
     * @return label for task2 if the dates are not the same, null if they are
     *         the same
     */
    public static Label checkSameDay(TaskEntity task1, TaskEntity task2) {
        if (task1 == null) { // new day
            return new Label(task2.getDueDate().toString());
        } else {
            if (Utils.checkSameDate(task1, task2)) {
                return null;
            }
        }
        return new Label(task2.getDueDate().toString());
    }

    // generate fake data.
    public static ArrayList<TaskEntity> generateFakeData() {
        ArrayList<TaskEntity> fakeData = new ArrayList<TaskEntity>();
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
                    TaskEntity t = new TaskEntity(Integer.toString(k++), c, false, d);
                    fakeData.add(t);
                }
            }
        }

        System.out.println(k + " Fake data created");
        return fakeData;
    }

}
