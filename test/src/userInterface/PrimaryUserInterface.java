//@@author A0125514N
package userInterface;

import userInterface.CommandBar;
import userInterface.UserInterfaceController;

import java.util.ArrayList;
import dateParser.CommandParser.COMMAND;
import dateParser.InputParser;
import dateParser.Pair;
import entity.ResultSet;
import entity.TaskEntity;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainLogic.TaskUtils;

public class PrimaryUserInterface extends Application {

	static final int PREFERED_WINDOW_WIDTH = 600; // change to 1080.
	static final double PREFERED_WINDOW_SCALE = 0.8;

	private static final boolean SUCCESS = true;
	private static final boolean FAILURE = false;
	static final int TYPE_1 = 0;
	static final int TYPE_2 = 1;
	static final int TYPE_3 = 2;

	// CommandBar dimensions.
	static final int COMMAND_BAR_HEIGTH = 70;
	static final int COMMAND_BAR_TOP_MARGIN = 10;
	static final int COMMAND_BAR_BOTTOM_MARGIN = 40;
	static final int TWO = 2;

	// Font and style.
	static final String FONT_DEFAULT = "lucida sans";
	static final String FONT_TITLE_LABLES = "lucida sans";
	static final int DEFAULT_FONT_SIZE = 24;

	private static String[] styles = { "default.css", "blackandwhite.css","red.css" };

	private String _styleSheet = styles[2];
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
		_commandBar = CommandBar.getInstance(COMMAND_BAR_HEIGTH, _commandBarWidth);
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
		primaryScene.getStylesheets().add(_styleSheet);
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
		EventHandler<MouseEvent> mainEventHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent arg0) {
				focus();
			}
		};
		
		uiController = UserInterfaceController.getInstance(primaryStage);
		String theme = uiController.loadTheme();
		if (isValidTheme(theme)) {
			_styleSheet = theme;
			_primaryStage.getScene().getStylesheets().clear();
			_primaryStage.getScene().getStylesheets().add(theme);
		}
		uiController.initializeInterface(_screenBounds, _fixedSize, _styleSheet, mainEventHandler);
	}

	/**
	 * initialize the main controls for the application.
	 * 
	 */
	private void initializeControls() {
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

	private void processKeyRelease(TextField textField, KeyEvent event) {
		_commandBar.release();
		if (event.getCode().compareTo(KeyCode.TAB) == 0) {
			_commandBar.changeSelector();
		}
	}

	//@@author a0125415n
	/**
	 * takes the data from key press and executes the related command
	 * @param textField
	 * @param KeyEvent event
	 */
	private void processKeyPress(TextField textField, KeyEvent event) {

		if (event.getCode().compareTo(KeyCode.BACK_SPACE) == 0) {
			_commandBar.deleteKey();
		} else if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
			COMMAND cmd = _commandBar.onEnter();

			if (cmd.equals(COMMAND.EXIT)) {
				executeExit();
			} else if (cmd.equals(COMMAND.INVALID)) {
				executeInvalidCommand();
			} else if (cmd.equals(COMMAND.ADD)) {
				executeAdd(_commandBar.getTasks(), _commandBar.getFullInput());
			} else if (cmd.equals(COMMAND.DELETE)) {
				executeDelete(_commandBar.getId(), _commandBar.getFullInput());
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
			} else if (cmd.equals(COMMAND.JUMP)) {
				executeJump();
				return;
			} else if (cmd.equals(COMMAND.DONE)) {
				String indexToMarkComplete = _commandBar.getId();
				executeMarkComplete(indexToMarkComplete, _commandBar.getFullInput());
			} else if (cmd.equals(COMMAND.LINK)) {
				Pair<String, String> ids = _commandBar.getLinkId();
				executeLink(ids.getFirst(), ids.getSecond(), _commandBar.getFullInput());
			} else if (cmd.equals(COMMAND.SEARCH)) {
				String stringToSearch = _commandBar.getSearchStr();
				executeSearch(stringToSearch, _commandBar.getFullInput());
			} else if (cmd.equals(COMMAND.SAVETO)) {
				String stringToSearch = _commandBar.getSearchStr();
				executeChangeSaveDir(stringToSearch);
			} else if (cmd.equals(COMMAND.UNDO)) {
				executeUndo();
			} else if (cmd.equals(COMMAND.THEME)) {
				String themeChange = _commandBar.getSearchStr();
				executeChangeTheme(themeChange);
			}
		} else {
			_commandBar.onKeyReleased();
		}
		processControls(event);
		focus();
	}
	//@@author A0125514N
	private void processControls(KeyEvent event) {
		if (event.getCode().compareTo(KeyCode.UP) == 0 && !event.isControlDown() && !event.isShiftDown()) {
			_commandBar.getPrevCommand();
		}

		if (event.getCode().compareTo(KeyCode.DOWN) == 0 && !event.isControlDown() && !event.isShiftDown()) {
			_commandBar.getNextCommand();
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

		if (event.getCode().isFunctionKey()) {
			if (event.getCode().compareTo(KeyCode.F1) == 0) {
				uiController.showHelpView();
			} else if (event.getCode().compareTo(KeyCode.F2) == 0) {
				uiController.hide();
			} else if (event.getCode().compareTo(KeyCode.F3) == 0) {
				uiController.show();
			}
		}

		if (event.getCode().compareTo(KeyCode.LEFT) == 0) {
			uiController.updateHelpView(-1);
		} else if (event.getCode().compareTo(KeyCode.RIGHT) == 0) {
			uiController.updateHelpView(1);
		}
	}

	private void focus() {
		_primaryStage.requestFocus();
		_commandBar.focus();
	}

	private void executeExit() {
		uiController.saveStuff();
		System.exit(0);
	}

	/**
	 * Get feedBack message and show user.
	 */
	private void executeInvalidCommand() {
		_commandBar.showFeedBackMessage(COMMAND.INVALID, null, null);
	}

	//@@author a0125415n
	/**
	 * add a task into the system and display the changes in the selected view.
	 * 
	 * @param task
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	private void executeAdd(ArrayList<TaskEntity> tasks, String rawInput) {
		ResultSet resultSet;
		String taskName = tasks.get(0).getName();
		if (tasks.size() > 0) {
			if (tasks.get(0) != null) {
				resultSet = uiController.addTask(tasks.get(0), rawInput, true);
				if (resultSet.isSuccess()) {
					resetCommandInput();
				}
				_commandBar.showFeedBackMessage(COMMAND.ADD, resultSet, taskName);
			}
		}
	}

	/**
	 * delete task from the system.
	 * 
	 * @param taskToCheck.
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	private void executeDelete(String id, String rawString) {
		ResultSet resultSet = null;
		if (id != null) {
			resultSet = uiController.deleteTask(id, rawString, true);
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					resetCommandInput();
				}
			}
		}
		_commandBar.showFeedBackMessage(COMMAND.DELETE, resultSet, "ID" + id);
	}

	/**
	 * Modify a task, if task has not been retrieved, will display task to
	 * modify.
	 * 
	 * @param taskToCheck
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	private void executeModify(String id) {
		if (id != null) {
			int indexToModify = TaskUtils.convertStringToInteger(id);
			ArrayList<TaskEntity> tasks = _commandBar.getTasksPartialInput();
			if (indexToModify != -1) {
				if (tasks.size() == 1) {
					executeModify(indexToModify, tasks.get(0), _commandBar.getFullInput());
				} else {
					getItemToModify(indexToModify);
				}
			}
		} else {
			_commandBar.showFeedBackMessage(COMMAND.EDIT, null, null);
		}
	}

	private void executeModify(int indexToModify, TaskEntity taskEntity, String rawString) {
		ResultSet resultSet = uiController.modifyTask(indexToModify, taskEntity, rawString, true);
		if (resultSet.isSuccess()) {
			resetCommandInput();
		}
		_commandBar.showFeedBackMessage(COMMAND.EDIT, resultSet, Integer.toString(indexToModify));
	}

	/**
	 * Retrieve task base on indexToModify and display task in the command bar
	 * for modification.
	 * 
	 * @param indexToModify
	 */
	private void getItemToModify(int indexToModify) {
		String setString = uiController.getTaskToEditString(indexToModify);
		if (setString != null) {
			_commandBar.addToFullInput(setString);
			_commandBar.onKeyReleased();
		} else {
			_commandBar.showFeedBackMessage(COMMAND.EDIT, null, Integer.toString(indexToModify));
		}

	}

	/**
	 * Jump to index.
	 * 
	 * @param indexToJump
	 *            : base36 format
	 * @return boolean, true if value found, false it value not found
	 */
	private void executeJump() {
		ResultSet resultSet = new ResultSet();
		resultSet.setFail();
		resultSet.setIndex(TYPE_2);

		String indexToJump = _commandBar.getId();
		if (indexToJump != null) {
			boolean success = uiController.jumpToIndex(indexToJump);
			if (success) {
				resetCommandInput();
			} else {
				resultSet.setIndex(TYPE_1);
				_commandBar.showFeedBackMessage(COMMAND.JUMP, resultSet, indexToJump);
			}
		} else {
			_commandBar.showFeedBackMessage(COMMAND.JUMP, resultSet, indexToJump);
		}
	}

	/**
	 * Search base on the search string input.
	 * 
	 * @param stringToSearch
	 */
	private void executeSearch(String stringToSearch, String rawString) {
		ResultSet resultSet = uiController.executeSearch(stringToSearch, rawString, true);
		if (resultSet.isSuccess()) {
			resetCommandInput();
		}
		_commandBar.showFeedBackMessage(COMMAND.SEARCH, resultSet, null);
	}

	/**
	 * Link param 1 to param 2 with param 1 as project head.
	 * 
	 * @param indexZZ1
	 * @param indexZZ2
	 */
	private void executeLink(String indexZZ1, String indexZZ2, String rawString) {
		if (indexZZ1 != null && indexZZ2 != null) {
			ResultSet resultSet = uiController.link(indexZZ1, indexZZ2, rawString, true);
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					resetCommandInput();
				}
			}
			_commandBar.showFeedBackMessage(COMMAND.LINK, resultSet, null);
		} else {
			_commandBar.showFeedBackMessage(COMMAND.LINK, null, null);
		}
	}

	/**
	 * mark the selected task as complete.
	 * 
	 * @param indexZZ
	 */
	private void executeMarkComplete(String indexZZ, String rawString) {
		if (indexZZ != null) {
			ResultSet resultSet = uiController.markAsCompleted(indexZZ, rawString, true);
			if (resultSet.isSuccess()) {
				resetCommandInput();
			}
			_commandBar.showFeedBackMessage(COMMAND.DONE, resultSet, indexZZ);
		} else {
			_commandBar.showFeedBackMessage(COMMAND.DONE, null, indexZZ);
		}
	}

	private void executeChangeSaveDir(String stringToSave) {
		ResultSet resultSet = uiController.changeSaveDir(stringToSave);
		if (resultSet.isSuccess()) {
			resetCommandInput();
		}
		_commandBar.showFeedBackMessage(COMMAND.SAVETO, resultSet, null);
	}

	private void executeUndo() {

		ResultSet resultSet = uiController.undoLastCommand();
		if (resultSet.isSuccess()) {
			resetCommandInput();
		}
		_commandBar.showFeedBackMessage(COMMAND.UNDO, resultSet, null);
	}
	//@@author A0125514N
	private void executeChangeTheme(String themeChange) {
		if (isValidTheme(themeChange)) {
			_styleSheet = themeChange;
			_primaryStage.getScene().getStylesheets().clear();
			_primaryStage.getScene().getStylesheets().add(_styleSheet);
			ResultSet resultSet = uiController.changeTheme(_styleSheet);
			if (resultSet.isSuccess()) {
				resetCommandInput();
			}
			_commandBar.showFeedBackMessage(COMMAND.THEME, resultSet, getStyleSheetList());
		} else {
			_commandBar.showFeedBackMessage(COMMAND.THEME, null, getStyleSheetList());
		}
	}

	public boolean isValidTheme(String theme) {
		if (theme == null) {
			return false;
		}
		for (int i = 0; i < styles.length; i++) {
			if (styles[i].equals(theme)) {
				return true;
			}
		}
		return false;
	}

	public String getStyleSheetList() {
		String styleList = "";
		for (int i = 0; i < styles.length; i++) {
			styleList = styleList.concat(styles[i]);
			if (i < styles.length - 1) {
				styleList = styleList.concat(",");
			}
		}
		return styleList;
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
