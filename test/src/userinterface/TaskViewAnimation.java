/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *          This is the animation class that provides the service to animate the
 *          TaskView to the ExpandedView.
 */
package userinterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TaskViewAnimation extends Service<Integer> {

    // This variable to keep track if the animation is already in process
    private static TaskViewAnimation _myInstances;

    private static final int DIRECTION_TO_TASK_VIEW = 1;
    private static final int DIRECTION_TO_EXPANED_VIEW = -1;

    private UserInterfaceController _ui;
    private boolean _isDoneTranslatingToOtherView;
    private int _direction;

    /**
     * Returns a new instance of the thread, existing thread will stop running.
     * This method ensures only 1 instacne of the thread is runnign at any given
     * period.
     * 
     * @param userInterfaceInstance
     * @param direction
     *            - animate to TaskView or ExpandedView
     * 
     * @return instance
     */
    public static TaskViewAnimation getInstance(UserInterfaceController ui, int direction) {
        if (_myInstances != null) {
            if (_myInstances.isRunning()) {
                _myInstances.cancel();
            }
        }
        _myInstances = new TaskViewAnimation(ui, direction);
        return _myInstances;
    }

    private TaskViewAnimation(UserInterfaceController userInterfaceController, int direction) {
        _ui = userInterfaceController;
        _isDoneTranslatingToOtherView = false;
        _direction = direction;
    }

    @Override
    protected Task<Integer> createTask() {
        return new MyTask();
    }

    private class MyTask extends Task<Integer> {
        @Override
        protected Integer call() throws Exception {
            while (!_isDoneTranslatingToOtherView) {
                Thread.sleep(10);
                Platform.runLater(new Runnable() {
                    public void run() {
                        if (_direction == DIRECTION_TO_EXPANED_VIEW) {
                            _isDoneTranslatingToOtherView = _ui.isAtExpanedView();
                        } else if (_direction == DIRECTION_TO_TASK_VIEW) {
                            _isDoneTranslatingToOtherView = _ui.isAtTaskView();
                        }
                    }
                });
            }
            return null;
        }

    }
}
