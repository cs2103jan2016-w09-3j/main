package userInterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TaskViewDescriptionAnimation extends Service<Integer> {

	UserInterfaceController ui;
	boolean _isDoneTranslatingToOtherView;

	public TaskViewDescriptionAnimation(UserInterfaceController userInterfaceController) {
		ui = userInterfaceController;
		_isDoneTranslatingToOtherView = false;
	}

	@Override
	protected Task<Integer> createTask() {
		return new Task<Integer>() {
			@Override
			protected Integer call() throws InterruptedException {
				while (!_isDoneTranslatingToOtherView) {
					Thread.sleep(10);
					Platform.runLater(new Runnable() {
						public void run() {
							_isDoneTranslatingToOtherView = ui.animateView();
						}
					});
				}
				return null;
			}
		};
	}

}
