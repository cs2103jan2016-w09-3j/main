//@@author A0125514N
package userInterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TaskViewDescriptionAnimation extends Service<Integer> {

	// This variable to keep track if the animation is already in process
	private static TaskViewDescriptionAnimation _myInstances;

	private static final int DIRECTION_TO_TASK_VIEW = 1;
	private static final int DIRECTION_TO_EXPANED_VIEW = -1;

	private UserInterfaceController ui;
	private boolean _isDoneTranslatingToOtherView;
	private int _direction;

	/**
	 * Returns a new instance of the thread, existing thread will stop running.
	 * This method ensures only 1 instacne of the thread is runnign at any given
	 * period.
	 * 
	 * @param userInterfaceInstance
	 * @param direction - animate to TaskView or ExpandedView
	 * 
	 * @return instance
	 */
	public static TaskViewDescriptionAnimation getInstance(UserInterfaceController ui, int direction) {
		if (_myInstances != null) {
			if (_myInstances.isRunning()) {
				_myInstances.cancel();
			}
		}
		_myInstances = new TaskViewDescriptionAnimation(ui, direction);
		return _myInstances;
	}

	private TaskViewDescriptionAnimation(UserInterfaceController userInterfaceController, int direction) {
		ui = userInterfaceController;
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
							_isDoneTranslatingToOtherView = ui.isAtExpanedView();
						} else if (_direction == DIRECTION_TO_TASK_VIEW) {
							_isDoneTranslatingToOtherView = ui.isAtTaskView();
						}
					}
				});
			}
			return null;
		}

	}
}
