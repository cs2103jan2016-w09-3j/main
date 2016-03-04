package userInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import entity.TaskEntity;
import mainLogic.TaskManager;
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

    // var for animation scrolling
    private int _jumpToIndex;
    private TaskManager _taskManager; 

    public UserInterfaceController(Stage primaryStage) {
        _parentStage = primaryStage;
        _taskManager = new TaskManager();
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
        _taskViewInterface.buildComponent(_taskManager.generateFakeData());
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
        // check index valid a not
        // assume is valid for now. between 0 to workingListSize;
        _jumpToIndex = index;
        Thread t = new Thread(jumpToIndexAnimation());
        t.start();
    }

    public Task<Void> jumpToIndexAnimation() {
        Task<Void> jumpToIndexAnimation = new Task<Void>() {
            @Override
            public Void call() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        
                        boolean isDone = false;
                        long startTime = System.currentTimeMillis() % 1000;
                        long secondsToAnimate = 3000;
                        int selectedIndex = _taskViewInterface.getSelectIndex();
                        int itemsTomove = selectedIndex - _jumpToIndex;
                        int indexMoved = 0;
                        do {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            Long timePassed = System.currentTimeMillis() % 1000 - startTime;
                            double indexA = timePassed / secondsToAnimate;
                            int numberToMoveNow = (int) (itemsTomove * indexA);
                            int itemToMove = Math.abs(numberToMoveNow) - indexMoved;
                            System.out.println(itemToMove);
                            if (_currentView == TASK_VIEW || _currentView == DETAILED_VIEW) {
                                if (_currentView == DETAILED_VIEW) {

                                } else {
                                    for (int i = 0; i < Math.abs(itemToMove); i++) {
                                        _taskViewInterface.update(numberToMoveNow);
                                    }
                                    _taskViewInterface.setItemSelected(numberToMoveNow);
                                    indexMoved+=itemToMove;
                                    translateComponentsY(_taskViewInterface.getTranslationY());
                                    updateDescriptionComponent();
                                }
                            }
                        } while (!isDone);
                    }

                });
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

    public void addTask(TaskEntity task) {
        _taskManager.add(task);
        _taskViewInterface.buildComponent(_taskManager.getMainDisplay());
        update(0);
    }

}
