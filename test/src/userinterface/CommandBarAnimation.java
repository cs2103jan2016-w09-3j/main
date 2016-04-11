/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *			This is the animation class that provides the service to animate the fading of the feedback message.
 */
package userinterface;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CommandBarAnimation extends Service<Void> {

	private static final int DELAY_BEFORE_START = 3000;
	private static final int ANIMATE_SPEED_TOTAL = 1000;
	private static final int ANIMATION_DELAY = 33;
	private CommandBar _commandBar;
	private static int count = 0;
	private int individualCount = 0;

	private static CommandBarAnimation _myInstance;

	public static void start(CommandBar commandBar) {
		if (_myInstance != null) {
			while (_myInstance.isRunning()) {
				_myInstance.cancel();
			}
		}
		_myInstance = new CommandBarAnimation(commandBar, ++count);
		_myInstance.start();
	}

	private CommandBarAnimation(CommandBar commandBar, int count) {
		_commandBar = commandBar;
		individualCount = count;
		commandBar.resetFeedBack(individualCount);
	}

	@Override
	protected Task<Void> createTask() {
		return new MyTask();
	}

	private class MyTask extends Task<Void> {
		private double _percentageDone;
		private boolean isDoneAnimating;

		@Override
		protected Void call() throws Exception {
			_percentageDone = 0;
			isDoneAnimating = false;
			Thread.sleep(DELAY_BEFORE_START);
			long timeStart = System.currentTimeMillis();
			while (!isDoneAnimating) {
				long timePast = System.currentTimeMillis() - timeStart;
				_percentageDone = timePast / (double) ANIMATE_SPEED_TOTAL;
				Platform.runLater(new Runnable() {
					public void run() {
						isDoneAnimating = _commandBar.updateCommandStatus(_percentageDone, individualCount);
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
