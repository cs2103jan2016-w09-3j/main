package userInterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TaskViewDescriptionAnimation extends Service<Integer> {

	private static final int DIRECTION_TO_TASK_VIEW = 1;
	private static final int DIRECTION_TO_EXPANED_VIEW = -1;

	private UserInterfaceController ui;
	private boolean _isDoneTranslatingToOtherView;
	private int _direction;

	public TaskViewDescriptionAnimation(UserInterfaceController userInterfaceController, int direction) {
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
							_isDoneTranslatingToOtherView = ui.animateToExpanedView();
						} else if (_direction == DIRECTION_TO_TASK_VIEW) {
							_isDoneTranslatingToOtherView = ui.animateToTaskView();
						}
					}
				});
			}
			return null;
		}

	}
}
