package userInterface;

import userInterface.CommandBar;
import userInterface.UserInterfaceController;

import java.util.Calendar;

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

public class PrimaryUserInterface extends Application {

	static final int PREFERED_WINDOW_WIDTH = 600; // change to 1080
	static final double PREFERED_WINDOW_SCALE = 0.8;

	static final int COMMAND_BAR_WIDTH = 600;
	static final int COMMAND_BAR_HEIGTH = 50;
	static final int COMMAND_BAR_TOP_MARGIN = 10;
	static final int COMMAND_BAR_BOTTOM_MARGIN = 40;

	static final String DEFAULT_FONT = "Arial";
	static final int DEFAULT_FONT_SIZE = 24;

	static final String STYLE_SHEET = "stylesheet.css";

	private Rectangle2D _screenBounds;
	private CommandBar _commandBar;
	private boolean _fixedSize = false;
	private UserInterfaceController uiController;
	private Stage _primaryStage;

	/**
	 * Constructor will determine if the application will run in fixed size or
	 * not
	 */
	public PrimaryUserInterface() {
		_screenBounds = Screen.getPrimary().getVisualBounds();
		if (_screenBounds.getWidth() * PREFERED_WINDOW_SCALE <= PREFERED_WINDOW_WIDTH) {
			_fixedSize = true;
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		_primaryStage = primaryStage;
		_commandBar = new CommandBar();
		initializeControls();
		initializePrimaryStage(primaryStage);
		initializeUiController(primaryStage);
		_primaryStage.requestFocus();
	}

	private void initializePrimaryStage(Stage primaryStage) {
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setX((_screenBounds.getWidth() - COMMAND_BAR_WIDTH) / 2);
		primaryStage.setY((_screenBounds.getHeight() - COMMAND_BAR_HEIGTH - COMMAND_BAR_BOTTOM_MARGIN));
		Scene primaryScene = new Scene(initializeRootLayout(), COMMAND_BAR_WIDTH, COMMAND_BAR_HEIGTH);
		primaryScene.getStylesheets().add(STYLE_SHEET);
		primaryStage.setScene(primaryScene);
		_primaryStage.show();
	}

	private BorderPane initializeRootLayout() {
		GridPane commandBarPane = _commandBar.getCommandBar();
		BorderPane rootLayout = new BorderPane();
		rootLayout.setId("rootPane");
		rootLayout.getStylesheets().add(STYLE_SHEET);
		rootLayout.setCenter(commandBarPane);
		return rootLayout;
	}

	private void initializeUiController(Stage primaryStage) {
		uiController = new UserInterfaceController(primaryStage);
		uiController.initializeInterface(_screenBounds, _fixedSize);
	}

	public void initializeControls() {
		EventHandler<KeyEvent> mainEventHandler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				processKeyInputs(_commandBar.getTextField(), event);
			}
		};
		_commandBar.setTextFieldHandler(mainEventHandler);
	}

	public boolean executeAdd(TaskEntity task) {
		if (task != null) {
			uiController.addTask(task);
			_commandBar.getTextField().setText("");
			_commandBar.focus();
			return true;
		}
		return false;
	}

	public boolean executeDelete(String indexToDelete) {
		if (uiController.deleteTask(indexToDelete)) {
			_commandBar.getTextField().setText("");
			return true;
		}
		return false;
	}

	public boolean executeModify(String indexToModify, TaskEntity task) {
		if (uiController.modifyTask(indexToModify, task)) {
			_commandBar.getTextField().setText("");
			return true;
		}
		return false;
	}

	private void processKeyInputs(TextField textField, KeyEvent event) {
		if (event.getCode().compareTo(KeyCode.ENTER) == 0) {
			if (textField.getText().toLowerCase().equals("exit")) {
				System.exit(0);
			} else {
				String t = textField.getText();
				if (t.indexOf(" ") != -1) {
					if (t.substring(0, t.indexOf(" ")).equals("del")) {
						String indexToDelete = t.substring(t.indexOf(" ") + 1);
						executeDelete(indexToDelete);
					} else if (t.substring(0, t.indexOf(" ")).equals("add")) {
						TaskEntity task = _commandBar.executeLine(t.substring(t.indexOf(" ")));
						executeAdd(task);
					} else if (t.substring(0, t.indexOf(" ")).equals("mod")) {
						String indexToModify = t.substring(t.indexOf(" ") + 1);
						TaskEntity task = new TaskEntity("modify to this task ", Calendar.getInstance(), false,
								"modified");
						executeModify(indexToModify, task);
					}
				}
			}
		} else if (event.getCode().compareTo(KeyCode.SPACE) == 0) {
			String input = textField.getText();
			// _commandBar.onSpace(input);
		}

		if (event.getCode().compareTo(KeyCode.DOWN) == 0 && event.isControlDown() && event.isShiftDown()) {
			uiController.move(-1);
		}
		if (event.getCode().compareTo(KeyCode.UP) == 0 && event.isControlDown() && event.isShiftDown()) {
			uiController.move(1);
		}

		if (event.getCode().compareTo(KeyCode.DOWN) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.update(1);
		}
		if (event.getCode().compareTo(KeyCode.UP) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.update(-1);
		}

		if (event.getCode().compareTo(KeyCode.RIGHT) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.changeView(1);
		}
		if (event.getCode().compareTo(KeyCode.LEFT) == 0 && event.isControlDown() && !event.isShiftDown()) {
			uiController.changeView(-1);
		}
		_primaryStage.requestFocus();
	}

}
