package userInterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CommandBarAnimation extends Service<Void> {

	private static final int DELAY_BEFORE_START = 3000;
	private static final int ANIMATE_SPEED_TOTAL = 1000;
	private static final int ANIMATION_DELAY = 33;
	private CommandBar _commandBar;
	private double _percentageDone;
	private boolean isDoneAnimating;

	public CommandBarAnimation(CommandBar commandBar) {
		_commandBar = commandBar;
	}

	@Override
	protected Task<Void> createTask() {
		return new MyTask();
	}

	private class MyTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			Thread.sleep(DELAY_BEFORE_START);
			long timeStart = System.currentTimeMillis();
			while (!isDoneAnimating) {
				long timePast = System.currentTimeMillis() - timeStart;
				_percentageDone = timePast / (double) ANIMATE_SPEED_TOTAL;
				Platform.runLater(new Runnable() {
					public void run() {
						isDoneAnimating = _commandBar.updateCommandStatus(_percentageDone);
						if (_percentageDone > 1) {
							isDoneAnimating = true;
						}
					}
				});
				Thread.sleep(ANIMATION_DELAY);
			}
			return null;
		}
	}

}
