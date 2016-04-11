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

    private int _currentIndex;
    private int _indexToGo;
    private int _direction;
    private int _numberOfMilliSecondsBeforeIncreaseSpeed = 300;
    private UserInterfaceController _ui;
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
        this._currentIndex = currentIndex;
        this._indexToGo = indexToGo;
        _ui = userInterfaceController;
    }

    @Override
    protected Task<Integer> createTask() {
        return new MyTask();
    }

    private class MyTask extends Task<Integer> {
        @Override
        protected Integer call() throws Exception {
            _direction = 0;
            if (_currentIndex < _indexToGo) {
                _direction = 1;
            } else {
                _direction = -1;
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
                            _ui.updateComponents(_direction);
                            _currentIndex = _currentIndex + _direction;
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
        if (_direction > 0) {
            if (_currentIndex + _direction > _indexToGo) {
                _direction = _indexToGo - _currentIndex;
            }
        } else if (_direction < 0) {
            if (_currentIndex + _direction < _indexToGo) {
                _direction = -(_currentIndex - _indexToGo);
            }
        }
    }

    public boolean isCompleteAnimate() {
        if (_direction > 0) {
            if (_currentIndex >= _indexToGo) {
                return true;
            }
        } else if (_direction < 0) {
            if (_currentIndex <= _indexToGo) {
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
        if (currTime - startTime > _numberOfMilliSecondsBeforeIncreaseSpeed) {
            increaseSpeed();
            return currTime;
        }
        return startTime;
    }

    private void increaseSpeed() {
        if (_direction < 0) {
            _direction--;
        } else if (_direction > 0) {
            _direction++;
        }
    }
}
