/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *          This is the animation class that provides the service to animate the
 *          scrolling of task.
 */
package userinterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ScrollTaskAnimation extends Service<Integer> {

    private int currentIndex;
    private int indexToGo;
    private int direction;
    private int numberOfMilliSecondsBeforeIncreaseSpeed = 300;
    private UserInterfaceController ui;
    private static ScrollTaskAnimation _myInstance;
    private Runnable _thread;

    /**
     * Return an instance of the animation, existing Thread will be discarded.
     * 
     * @param currentIndex
     * @param indexToGo
     * @param userInterfaceController
     * 
     * @return instance
     */
    public static ScrollTaskAnimation getInstance(int currentIndex, int indexToGo,
            UserInterfaceController userInterfaceController) {
        if (_myInstance != null) {
            if (_myInstance.isRunning()) {
                _myInstance.cancel();
            }
        }
        _myInstance = new ScrollTaskAnimation(currentIndex, indexToGo, userInterfaceController);
        return _myInstance;
    }

    private ScrollTaskAnimation(int currentIndex, int indexToGo,
            UserInterfaceController userInterfaceController) {
        this.currentIndex = currentIndex;
        this.indexToGo = indexToGo;
        ui = userInterfaceController;
    }

    @Override
    protected Task<Integer> createTask() {
        return new MyTask();
    }

    private class MyTask extends Task<Integer> {
        @Override
        protected Integer call() throws Exception {
            direction = 0;
            if (currentIndex < indexToGo) {
                direction = 1;
            } else {
                direction = -1;
            }
            long startTime = System.currentTimeMillis();
            while (true) {
                if (isCompleteAnimate()) {
                    break;
                }
                startTime = checkTime(startTime);
                if (_thread == null) {
                    _thread = new Runnable() {
                        public void run() {
                            checkExceed();
                            ui.updateComponents(direction);
                            currentIndex = currentIndex + direction;
                            _thread = null;
                        }
                    };
                    Platform.runLater(_thread);
                }
                Thread.sleep(80);
            }
            return 1;
        }
    }

    /**
     * Checks if the direction to move will exceed the position required, if so,
     * set direction to be the difference.
     */
    public void checkExceed() {
        if (direction > 0) {
            if (currentIndex + direction > indexToGo) {
                direction = indexToGo - currentIndex;
            }
        } else if (direction < 0) {
            if (currentIndex + direction < indexToGo) {
                direction = -(currentIndex - indexToGo);
            }
        }
    }

    public boolean isCompleteAnimate() {
        if (direction > 0) {
            if (currentIndex >= indexToGo) {
                return true;
            }
        } else if (direction < 0) {
            if (currentIndex <= indexToGo) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check the time pass since the animation stars. if its over the time,
     * increase speed of animation.
     * 
     * @param startTime
     * @return time of change
     */
    private long checkTime(long startTime) {
        long currTime = System.currentTimeMillis();
        if (currTime - startTime > numberOfMilliSecondsBeforeIncreaseSpeed) {
            increaseSpeed();
            return currTime;
        }
        return startTime;
    }

    private void increaseSpeed() {
        if (direction < 0) {
            direction--;
        } else if (direction > 0) {
            direction++;
        }
    }
}
