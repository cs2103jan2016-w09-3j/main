package userInterface;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class TaskViewDescriptionAnimation extends Task<Integer> {

	UserInterfaceController ui;
	boolean _isDoneTranslatingToOtherView;

	public TaskViewDescriptionAnimation(UserInterfaceController userInterfaceController) {
		ui = userInterfaceController;
		_isDoneTranslatingToOtherView=false;
	}

	@Override
	protected Integer call() throws Exception {
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			Platform.runLater(new Runnable() {
				public void run() {
					_isDoneTranslatingToOtherView = ui.animateView();
				}
			});
		} while (!_isDoneTranslatingToOtherView);
		return null;
	}

}
