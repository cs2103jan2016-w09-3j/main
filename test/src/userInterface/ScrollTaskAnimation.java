package userInterface;

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

	private ScrollTaskAnimation(int currentIndex, int indexToGo, UserInterfaceController userInterfaceController) {
		this.currentIndex = currentIndex;
		this.indexToGo = indexToGo;
		ui = userInterfaceController;
	}

	Runnable r;

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
			while (currentIndex != indexToGo) {
				startTime = checkTime(startTime);
				checkExceed();
				if (r == null) {
					r = new Runnable() {
						public void run() {
							ui.updateUI(direction);
							currentIndex = currentIndex + direction;
							r = null;
						}
					};
					Platform.runLater(r);
				}
				Thread.sleep(80);
			}
			return 1;
		}
	}

	private long checkTime(long startTime) {
		long currTime = System.currentTimeMillis();
		if (currTime - startTime > numberOfMilliSecondsBeforeIncreaseSpeed) {
			increaseSpeed();
			return currTime;
		}
		return startTime;
	}

	private void increaseSpeed() {
		if (direction < 1) {
			direction--;
		} else {
			direction++;
		}
	}

	public void checkExceed() {
		if (direction < 0) {
			if (currentIndex + direction < indexToGo) {
				direction = currentIndex - indexToGo;
			}
		} else if (direction > 0) {
			if (currentIndex + direction > indexToGo) {
				direction = indexToGo - currentIndex;
			}
		}
	}

}
