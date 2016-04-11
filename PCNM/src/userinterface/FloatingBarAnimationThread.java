/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *          This is the animation class that provides the service to animate the
 *          movement of the items in the floating bar.
 */
package userinterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class FloatingBarAnimationThread extends Service<Void> {

    private UserInterfaceController _ui;
    private boolean _isAdded;
    private boolean _isDoneAnimating;
    private static final int ANIMATE_SPEED_TOTAL = 1500;
    private static final int ANIMATION_DELAY = 10;
    private static final int TIME_INTERVAL_FOR_NEXT_FLOATING_TASK = 10000;

    private double _percentageDone;

    public FloatingBarAnimationThread(UserInterfaceController userInterfaceController) {
        _ui = userInterfaceController;
        _percentageDone = 0;
    }

    public void reset() {
        _isAdded = false;
        _isDoneAnimating = false;
        _percentageDone = 0.0;
    }

    @Override
    protected Task<Void> createTask() {
        return new MyTask();
    }

    private class MyTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            while (true) {
                Thread.sleep(TIME_INTERVAL_FOR_NEXT_FLOATING_TASK);
                reset();
                long timeStart = System.currentTimeMillis();
                while (!_isDoneAnimating) {
                    long timePast = System.currentTimeMillis() - timeStart;
                    _percentageDone = timePast / (double) ANIMATE_SPEED_TOTAL;
                    Platform.runLater(new Runnable() {
                        public void run() {
                            if (!_isAdded) {
                                _ui.addRandomTaskToDisplay();
                                _isAdded = true;
                            }
                            _isDoneAnimating = _ui.updateFloatingBar(_percentageDone);
                            if (_percentageDone > 1) {
                                _isDoneAnimating = true;
                            }
                        }
                    });
                    Thread.sleep(ANIMATION_DELAY);
                }
            }
        }

    }
}
