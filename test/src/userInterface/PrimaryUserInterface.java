package userInterface;

import userInterface.CommandBar;
import userInterface.UserInterfaceController;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Calendar;

import dateParser.CommandParser.COMMAND;
import dateParser.InputParser;
import entity.TaskEntity;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainLogic.Utils;

public class PrimaryUserInterface extends Application {

	static final int PREFERED_WINDOW_WIDTH = 600; // change to 1080.
	static final double PREFERED_WINDOW_SCALE = 0.8;

	private static final boolean SUCCESS = true;
	private static final boolean FAILURE = false;
	static final int TYPE_1 = 0;
	static final int TYPE_2 = 1;

	// CommandBar dimensions.
	static final int COMMAND_BAR_HEIGTH = 70;
	static final int COMMAND_BAR_TOP_MARGIN = 10;
	static final int COMMAND_BAR_BOTTOM_MARGIN = 40;
	static final int TWO = 2;

	// Font and style.
	static final String DEFAULT_FONT = "Arial";
	static final int DEFAULT_FONT_SIZE = 24;
	static final String STYLE_SHEET = "stylesheet.css";

	private double _commandBarWidth;
	private Rectangle2D _screenBounds;
	private Stage _primaryStage;
	private CommandBar _commandBar;
	private boolean _fixedSize = false;
	private UserInterfaceController uiController;

	/**
	 * This Constructor is called during JavaFX launch(). Determines user screen
	 * size to set fixed value or scale value.
	 */
	public PrimaryUserInterface() {
		_screenBounds = Screen.getPrimary().getVisualBounds();
		_commandBarWidth = _screenBounds.getWidth() * PREFERED_WINDOW_SCALE;
		if (_screenBounds.getWidth() * PREFERED_WINDOW_SCALE <= PREFERED_WINDOW_WIDTH) {
			_fixedSize = true;
		}
	}

	/**
	 * initialize stage and components, this is the first method JavaFx calls.
	 * Initialize commandBar and components in commandBar as primary UI
	 * 
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		_primaryStage = primaryStage;
		_commandBar = new CommandBar(COMMAND_BAR_HEIGTH, _commandBarWidth);
		initializeControls();
		initializePrimaryStage(primaryStage);
		initializeUiController(primaryStage);
		InputParser parser = new InputParser("");
		focus();
	}

	/**
	 * set up the primary stage dimensions and display it on screen.
	 * 
	 * @param primaryStage.
	 */
	private void initializePrimaryStage(Stage primaryStage) {
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setX((_screenBounds.getWidth() - _commandBarWidth) / TWO);
		primaryStage.setY((_screenBounds.getHeight() - COMMAND_BAR_HEIGTH - COMMAND_BAR_BOTTOM_MARGIN));
		Scene primaryScene = new Scene(initializeRootLayout(), _commandBarWidth, COMMAND_BAR_HEIGTH);
		primaryScene.getStylesheets().add(STYLE_SHEET);
		primaryStage.setScene(primaryScene);
		_primaryStage.show();
	}

	/**
	 * initialize the content inside the primary stage, BorderPane used as
	 * rootLayout
	 * 
	 * @return mainLayout.
	 */
	private VBox initializeRootLayout() {
		VBox commandBarPane = _commandBar.getCommandBar();
		return commandBarPane;
	}

	/**
	 * initialize UserInterfaceController
	 * 
	 * @param primaryStage.
	 */
	private void initializeUiController(Stage primaryStage) {
		uiController = UserInterfaceController.getInstance(primaryStage);
		uiController.initializeInterface(_screenBounds, _fixedSize);
	}

	/**
	 * initialize the main controls for the application.
	 * 
	 */
	public void initializeControls() {
		EventHandler<KeyEvent> mainEventHandler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				processKeyPress(_commandBar.getTextField(), event);
			}
		};
		EventHandler<KeyEvent> releaseEventHandler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				processKeyRelease(_commandBar.getTextField(), event);
			}
		};
		_commandBar.setTextFieldHandler(mainEventHandler, releaseEventHandler);
	}

	public boolean executeMarkComplete(String indexZZ) {
		_commandBar.setFullInput("");
		_commandBar.onKeyReleased();
		uiController.markAsCompleted(indexZZ);
		focus();
		return false;
	}

	private void processKeyRelease(TextField textField, KeyEvent event) {
		_commandBar.release();
		if (event.getCode().compareTo(KeyCode.TAB) == 0) {
			_commandBar.changeSelector();
		}
	}

	private void processKeyPress(TextField textField, KeyEvent event) {

		if (event.getCode().compareTo(KeyCode.BACK_SPACE) == 0) {
			_commandBar.deleteKey();
		} else if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
			COMMAND cmd = _commandBar.onEnter();
			String t = _commandBar.getFullInput();
			if (t.equals("float")) {
				uiController.showFloatingView();
				textField.setText("");
				focus();
			} else if (t.indexOf(" ") != -1) {
				if (t.substring(0, t.indexOf(" ")).equals("mark")) {
					String indexToMarkComplete = t.substring(t.indexOf(" ") + 1);
					executeMarkComplete(indexToMarkComplete);
				} else if (t.substring(0, t.indexOf(" ")).equals("jump")) {
					String indexToJump = t.substring(t.indexOf(" ") + 1);
					executeJump(indexToJump);
					return;
				} else if (t.substring(0, t.indexOf(" ")).equals("link")) {
					String[] spilt = t.split(" ");
					if (spilt.length == 3) {
						System.out.println("link " + spilt[1] + " to " + spilt[2]);
						uiController.link(spilt[1], spilt[2]);
					}
				}
			}

			if (cmd.equals(COMMAND.EXIT)) {
				uiController.saveStuff();
				System.exit(0);
			} else if (cmd.equals(COMMAND.ADD)) {
				executeAdd(_commandBar.getTasks());
			} else if (cmd.equals(COMMAND.DELETE)) {
				executeDelete(_commandBar.getId());
			} else if (cmd.equals(COMMAND.EDIT)) {
				executeModify(_commandBar.getId());
			} else if (cmd.equals(COMMAND.MAIN)) {
				uiController.showMainView(-1);
				resetCommandInput();
			} else if (cmd.equals(COMMAND.HIDE)) {
				uiController.hide();
				resetCommandInput();
				return;
			} else if (cmd.equals(COMMAND.SHOW)) {
				uiController.show();
				resetCommandInput();
				return;
			} else if (cmd.equals(COMMAND.FLOAT)) {
				uiController.showFloatingView();
				resetCommandInput();
			}
		} else {
			_commandBar.onKeyReleased();
		}
		processControls(event);
		focus();
	}

	private void processControls(KeyEvent event) {
		if (event.getCode().compareTo(KeyCode.DOWN) == 0 && event.isControlDown() && event.isShiftDown()) {
			uiController.move(-1);
		}
		if (event.getCode().compareTo(KeyCode.UP) == 0 && event.isControlDown() && event.isShiftDown()) {
			uiController.move(1);
		}

		if (event.getCode().compareTo(KeyCode.DOWN) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.stopScrollingAnimation();
			uiController.updateComponents(1);
		}
		if (event.getCode().compareTo(KeyCode.UP) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.stopScrollingAnimation();
			uiController.updateComponents(-1);
		}

		if (event.getCode().compareTo(KeyCode.RIGHT) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.changeView(1);
		}
		if (event.getCode().compareTo(KeyCode.LEFT) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.changeView(-1);
		}
	}

	private void focus() {
		_primaryStage.requestFocus();
		_commandBar.focus();
	}

	/**
	 * add a task into the system and display the changes in the selected view.
	 * 
	 * @param task
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	private void executeAdd(ArrayList<TaskEntity> tasks) {
		int status = -2;
		String taskName = null;
		if (tasks.size() == 1) {
			if (tasks.get(0) != null) {
				status = uiController.addTask(tasks.get(0));
			}
		} else {
			status = uiController.addBatchTask(tasks);
		}
		if (status == 1) {
			taskName = tasks.get(0).getName();
			_commandBar.showFeedBackMessage(COMMAND.ADD, SUCCESS, TYPE_1, taskName);
			resetCommandInput();
		} else if (status == -1) {
			taskName = tasks.get(0).getName();
			_commandBar.showFeedBackMessage(COMMAND.ADD, SUCCESS, TYPE_2, taskName);
			resetCommandInput();
		} else if (status == -2) {
			_commandBar.showFeedBackMessage(COMMAND.ADD, FAILURE, TYPE_1, taskName);
		}
	}

	/**
	 * delete task from the system.
	 * 
	 * @param taskToCheck.
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	private void executeDelete(String id) {
		boolean success = false;
		if (id != null) {
			success = uiController.deleteTask(id);
		}
		if (success) {
			_commandBar.showFeedBackMessage(COMMAND.DELETE, SUCCESS, TYPE_1, id);
			resetCommandInput();
		} else {
			_commandBar.showFeedBackMessage(COMMAND.DELETE, FAILURE, TYPE_1, id);
		}

	}

	/**
	 * Modify a task, if task has not been retrieved, will display task to
	 * modify.
	 * 
	 * @param taskToCheck
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	public void executeModify(String id) {
		int indexToModify = Utils.convertBase36ToDec(id);
		ArrayList<TaskEntity> tasks = _commandBar.getTasksPartialInput();
		if (indexToModify != -1) {
			if (tasks.size() == 1) {
				executeModify(indexToModify, tasks.get(0));
			} else {
				getItemToModify(indexToModify);
			}
		}
	}

	private void executeModify(int indexToModify, TaskEntity taskEntity) {
		boolean success = uiController.modifyTask(indexToModify, taskEntity);
		if (success) {
			_commandBar.showFeedBackMessage(COMMAND.EDIT, SUCCESS, TYPE_1, Utils.convertDecToBase36(indexToModify));
			resetCommandInput();
		} else {
			_commandBar.showFeedBackMessage(COMMAND.EDIT, FAILURE, TYPE_1, Utils.convertDecToBase36(indexToModify));
		}
	}

	/**
	 * Retrieve task base on indexToModify and display task in the command bar
	 * for modification.
	 * 
	 * @param indexToModify
	 */
	private void getItemToModify(int indexToModify) {
		TaskEntity toPopulate = uiController.getTaskByID(indexToModify);
		if (toPopulate != null) {
			String toSet = " " + toPopulate.getName();
			if (toPopulate.getDescription() != null) {
				toSet += " : " + toPopulate.getDescription();
			}
			if (toPopulate.getDueDate() != null) {
				Calendar c = toPopulate.getDueDate();
				int day = c.get(Calendar.DATE);
				int month = c.get(Calendar.MONTH) + 1;
				int year = c.get(Calendar.YEAR);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int min = c.get(Calendar.MINUTE);
				toSet += " " + day + "-" + month + "-" + year + " " + hour + min;
			}
			if (toPopulate.getProjectHead() != null) {
				toSet += " @" + toPopulate.getProjectHead().getName();
			}
			_commandBar.addToFullInput(toSet);
			_commandBar.onKeyReleased();
		} else {
			_commandBar.showFeedBackMessage(COMMAND.EDIT, FAILURE, TYPE_2, Utils.convertDecToBase36(indexToModify));
		}

	}

	/**
	 * Jump to index.
	 * 
	 * @param indexToJump
	 *            : base36 format
	 * @return boolean, true if value found, false it value not found
	 */
	public void executeJump(String indexToJump) {
		boolean success = uiController.jumpToIndex(indexToJump);
		if (success) {
			resetCommandInput();
		} else {
			// change to jump _commandBar.showFeedBackMessage(COMMAND.EDIT,
			// FAILURE, TYPE_1);
		}
	}

	/**
	 * Reset the layout and style of the commandBar for new input. usually
	 * called after a success in executing a command.
	 * 
	 */
	public void resetCommandInput() {
		_commandBar.reset();
		focus();
	}
}
