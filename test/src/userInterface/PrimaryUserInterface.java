package userInterface;

import userInterface.CommandBar;
import userInterface.UserInterfaceController;

import java.awt.Event;
import java.util.ArrayList;
import dateParser.CommandParser.COMMAND;
import dateParser.XMLParser;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainLogic.Utils;

public class PrimaryUserInterface extends Application {

	static final int PREFERED_WINDOW_WIDTH = 600; // change to 1080.
	static final double PREFERED_WINDOW_SCALE = 0.8;

	// CommandBar dimensions.
	static final int COMMAND_BAR_WIDTH = 600;
	static final int COMMAND_BAR_HEIGTH = 50;
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
		_commandBar = new CommandBar();
		initializeControls();
		initializePrimaryStage(primaryStage);
		initializeUiController(primaryStage);
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
	 * @return rootLayout.
	 */
	private BorderPane initializeRootLayout() {
		GridPane commandBarPane = _commandBar.getCommandBar();
		BorderPane rootLayout = new BorderPane();
		rootLayout.setId("rootPane");
		rootLayout.getStylesheets().add(STYLE_SHEET);
		rootLayout.setCenter(commandBarPane);
		return rootLayout;
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
				processKeyInputs(_commandBar.getTextField(), event);

			}

		};
		EventHandler<KeyEvent> keyReleasedEventHandler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				processKeyReleased(_commandBar.getTextField(), event);
			}
		};
		_commandBar.setTextFieldHandler(mainEventHandler, keyReleasedEventHandler);
	}

	/*
	 * private void initializeData(){ ArrayList<String> cmdArrs =
	 * uiController.readFromFile(); System.out.println(cmdArrs.size()); for(int
	 * j=0; j<cmdArrs.size(); j++){ COMMAND cmd =
	 * _commandBar.onEnter(cmdArrs.get(j)); String t = cmdArrs.get(j); if
	 * (cmd.equals(COMMAND.EXIT)) {
	 * uiController.saveToFile(_commandBar.get_allSessionCmds());
	 * System.exit(0); } else if (cmd.equals(COMMAND.ADD)) {
	 * ArrayList<TaskEntity> tasks = _commandBar.getTasks(t); for (int i = 0; i
	 * < tasks.size(); i++) { executeAdd(tasks.get(i)); } } else if
	 * (cmd.equals(COMMAND.EDIT)) { ArrayList<TaskEntity> tasks =
	 * _commandBar.getTasks(t); for (int i = 0; i < tasks.size(); i++) {
	 * executeModify(tasks.get(i)); } } else if (cmd.equals(COMMAND.DELETE)) {
	 * ArrayList<TaskEntity> tasks = _commandBar.getTasks(t); for (int i = 0; i
	 * < tasks.size(); i++) { executeDelete(tasks.get(i)); }
	 * 
	 * } } }
	 */

	/**
	 * add a task into the system and display the changes in the selected view.
	 * 
	 * @param task
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	public boolean executeAdd(TaskEntity task) {
		if (task != null) {
			uiController.addTask(task);
			_commandBar.getTextField().setText("");
			_commandBar.addSessionCmds(_commandBar.get_textInField());
			focus();
			return true;
		}
		return false;
	}

	public boolean executeBatchAdd(ArrayList<TaskEntity> task) {
		uiController.addBatchTask(task);
		focus();
		return true;
	}

	/**
	 * delete task from the system.
	 * 
	 * @param taskToCheck.
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	public boolean executeDelete(TaskEntity taskToCheck) {
		int indexToDelete = uiController.getTaskID(taskToCheck);
		if (indexToDelete > -1) {
			boolean temp = uiController.deleteTask(indexToDelete);
			if (temp) {
				_commandBar.addSessionCmds(_commandBar.get_textInField());
				_commandBar.getTextField().setText("");
				return true;
			}
		}
		return false;
	}

	public boolean executeDelete(String indexZZ) {
		int indexToDelete = Utils.convertBase36ToDec(indexZZ);
		if (indexToDelete != -1) {
			return uiController.deleteTask(indexToDelete);
		} else {
			return false;
		}
	}

	/**
	 * modify a task.
	 * 
	 * @param taskToCheck
	 * @return boolean, true for successful and false for unsuccessful.
	 */
	public boolean executeModify(TaskEntity taskToCheck) {
		int indexToModify = uiController.getTaskID(taskToCheck);
		if (indexToModify > -1) {
			boolean temp = uiController.modifyTask(indexToModify, taskToCheck);
			if (temp) {
				_commandBar.addSessionCmds(_commandBar.get_textInField());
				_commandBar.getTextField().setText("");
				return true;
			}
		}

		return false;
	}

	/**
	 * Jump to index.
	 * 
	 * @param indexToJump
	 *            : base36 format
	 * @return boolean, true if value found, false it value not found
	 */
	public boolean executeJump(String indexToJump) {
		uiController.jumpToIndex(indexToJump);
		_commandBar.getTextField().setText("");
		return true;
	}

	private void processKeyReleased(TextField textField, KeyEvent event) {
		String input = textField.getText();
		_commandBar.onKeyReleased(input);
	}

	private void processKeyInputs(TextField textField, KeyEvent event) {
		if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
			COMMAND cmd = _commandBar.onEnter(textField.getText());
			String t = XMLParser.removeAllTags(textField.getText());
			// Ten add to mod to cater for theses commands
			if (t.equals("float")) {
				uiController.showFloatingView();
				textField.setText("");
				focus();
			}   else if (t.indexOf(" ") != -1) {
				if (t.substring(0, t.indexOf(" ")).equals("jump")) {
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
				// uiController.saveToFile(_commandBar.get_allSessionCmds());
				uiController.saveStuff();
				System.exit(0);
			} else if (cmd.equals(COMMAND.ADD)) {
				ArrayList<TaskEntity> tasks = _commandBar.getTasks(t);
				// System.out.println(tasks.get(0).getName());
				if (tasks.size() == 1) {
					executeAdd(tasks.get(0));
				} else {
					executeBatchAdd(tasks);
				}
			} else if (cmd.equals(COMMAND.EDIT)) {
				ArrayList<TaskEntity> tasks = _commandBar.getTasks(t);
				for (int i = 0; i < tasks.size(); i++) {
					executeModify(tasks.get(i));
				}
			} else if (cmd.equals(COMMAND.DELETE)) {
				String id = _commandBar.HasId(textField.getText());
				if (id != null) {
					executeDelete(id);
				} else {
					ArrayList<TaskEntity> tasks = _commandBar.getTasks(t);
					for (int i = 0; i < tasks.size(); i++) {
						executeDelete(tasks.get(i));
					}
				}
			} else if (cmd.equals(COMMAND.MAIN)) {
				uiController.showMainView(-1);
				textField.setText("");
				focus();
			}else if (cmd.equals(COMMAND.HIDE)) {
				uiController.hide();
				textField.setText("");
				focus();
				return;
			} else if (cmd.equals(COMMAND.SHOW)) {
				uiController.show();
				textField.setText("");
				focus();
				return;
			} else if (cmd.equals(COMMAND.FLOAT)) {
				uiController.showFloatingView();
				textField.setText("");
				focus();
			}  
		}

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

		_primaryStage.requestFocus();
	}

	public void focus() {
		_primaryStage.requestFocus();
		_commandBar.focus();
	}

}
