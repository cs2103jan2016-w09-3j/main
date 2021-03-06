/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *          This class is the main UserInterface class, handles the inputs from
 *          user and relays them to the other components.
 */
package userinterface;

import java.util.ArrayList;

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
import logic.TaskUtils;
import parser.InputParser;
import parser.Pair;
import parser.CommandParser.COMMAND;
import userinterface.CommandBar;
import userinterface.UserInterfaceController;

public class PrimaryUserInterface extends Application {

    static final int PREFERED_WINDOW_WIDTH = 600;
    static final double PREFERED_WINDOW_SCALE = 0.8;

    private static final int SAME = 0;
    private static final int ZERO = 0;

    // Controls
    private static final int CTRL_UP_ARROW_KEY = -1;
    private static final int CTRL_DOWN_ARROW_KEY = 1;
    private static final int CTRL_LEFT_ARROW_KEY = -1;
    private static final int CTRL_RIGHT_ARROW_KEY = 1;

    static final int TYPE_1 = 0;
    static final int TYPE_2 = 1;

    // CommandBar dimensions.
    static final int COMMAND_BAR_HEIGTH = 70;
    static final int COMMAND_BAR_TOP_MARGIN = 10;
    static final int COMMAND_BAR_BOTTOM_MARGIN = 40;
    static final int TWO = 2;

    // Font and style.
    static final String FONT_DEFAULT = "lucida sans";
    static final String FONT_TITLE_LABLES = "lucida sans";
    static final int DEFAULT_FONT_SIZE = 24;

    private static final String[] STYLES = { "default.css", "blackandwhite.css", "red.css", "pastel.css" };
    private String _styleSheet = STYLES[ZERO];
    private int _styleSheetSelector = ZERO;

    private double _commandBarWidth;
    private Rectangle2D _screenBounds;
    private Stage _primaryStage;
    private CommandBar _commandBar;
    private boolean _isFixedSize = false;
    private UserInterfaceController uiController;
    private EventHandler<MouseEvent> _mainEventHandler;

    /**
     * This Constructor is called during JavaFX launch(). Determines user screen
     * size to set fixed value or scale value.
     */
    public PrimaryUserInterface() {
        _screenBounds = Screen.getPrimary().getVisualBounds();
        _commandBarWidth = _screenBounds.getWidth() * PREFERED_WINDOW_SCALE;
        if (_screenBounds.getWidth() * PREFERED_WINDOW_SCALE <= PREFERED_WINDOW_WIDTH) {
            _isFixedSize = true;
        }
    }

    /**
     * initialize stage and components, this is the first method JavaFx calls.
     * Initialize commandBar and components in commandBar as primary UI.
     * 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        InputParser parser = new InputParser("");
        _primaryStage = primaryStage;
        _commandBar = CommandBar.getInstance(_commandBarWidth, COMMAND_BAR_HEIGTH);
        initializeControls();
        initializePrimaryStage(primaryStage);
        initializeUiController(primaryStage);
        ResultSet resultSet = uiController.isFileLoadedProper();
        if (resultSet != null) {
            _commandBar.showFeedBackMessage(COMMAND.LOADFROM, resultSet, null);
        }
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
     * initialize the content inside the primary stage, VBox used as rootLayout
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
        String theme = uiController.loadTheme();
        if (isValidTheme(theme)) {
            _styleSheet = theme;
            _primaryStage.getScene().getStylesheets().clear();
            _primaryStage.getScene().getStylesheets().add(theme);
        }
        uiController.initializeInterface(_screenBounds, _isFixedSize, _styleSheet, _mainEventHandler);
    }

    /**
     * initialize the main controls for the application. _mainEventHandler is
     * the event thats get mouse click on any panel of the application and set
     * focus to the command bar.
     * 
     * 
     */
    private void initializeControls() {
        _mainEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0) {
                focus();
            }
        };
        EventHandler<KeyEvent> commandBarMainEventHandler = new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                processKeyPress(_commandBar.getTextField(), event);
            }
        };
        EventHandler<KeyEvent> releaseEventHandler = new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                processKeyRelease(_commandBar.getTextField(), event);
            }
        };
        _commandBar.setTextFieldHandler(commandBarMainEventHandler, releaseEventHandler);
    }

    private void processKeyRelease(TextField textField, KeyEvent event) {
        _commandBar.release();
        if (event.getCode().compareTo(KeyCode.TAB) == SAME) {
            _commandBar.changeSelector();
        }
    }

    // @@author A0125415N
    /**
     * takes the data from key press and executes the related command
     * 
     * @param textField
     * @param KeyEvent
     *            event
     */
    private void processKeyPress(TextField textField, KeyEvent event) {
        if (event.getCode().compareTo(KeyCode.BACK_SPACE) == SAME) {
            _commandBar.deleteKey();
        } else if (event.getCode().compareTo(KeyCode.ENTER) == SAME) {
            COMMAND cmd = _commandBar.onEnter();

            if (cmd.equals(COMMAND.EXIT)) {
                executeExit();
            } else if (cmd.equals(COMMAND.INVALID)) {
                if (!uiController.processEnter()) {
                    executeInvalidCommand();
                }
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
                uiController.showFloatingView(ZERO);
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
            } else if (cmd.equals(COMMAND.LOADFROM)) {
                String loadFrom = _commandBar.getSearchStr();
                executeLoadFrom(loadFrom);
            }
        } else {
            _commandBar.onKeyReleased();
        }
        processControls(event);
        focus();
    }

    // @@author A0125514N
    /**
     * Process the event receive to and execute the next action.
     * 
     * @param event
     */
    private void processControls(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if (keyCode.isFunctionKey()) {
            processFunctionKeys(keyCode);
        } else if (!event.isControlDown()) {
            processControlKeyNotPressed(keyCode);
        } else if (event.isControlDown()) {
            processControlKeyPressed(keyCode);
        }
    }

    private void processFunctionKeys(KeyCode keyCode) {
        if (keyCode.compareTo(KeyCode.F1) == SAME) {
            uiController.showHelpView();
        } else if (keyCode.compareTo(KeyCode.F2) == SAME) {
            uiController.hide();
        } else if (keyCode.compareTo(KeyCode.F3) == SAME) {
            uiController.show();
        } else if (keyCode.compareTo(KeyCode.F4) == SAME) {
            executeChangeTheme();
        } else if (keyCode.compareTo(KeyCode.F5) == SAME) {
            uiController.destroy();
            uiController.initializeInterface(_screenBounds, _isFixedSize, _styleSheet, _mainEventHandler);
            resetCommandInput();
        }
    }

    private void processControlKeyPressed(KeyCode keyCode) {
        if (keyCode.compareTo(KeyCode.DOWN) == SAME) {
            uiController.stopScrollingAnimation();
            uiController.updateComponents(CTRL_DOWN_ARROW_KEY);
        } else if (keyCode.compareTo(KeyCode.UP) == SAME) {
            uiController.stopScrollingAnimation();
            uiController.updateComponents(CTRL_UP_ARROW_KEY);
        } else if (keyCode.compareTo(KeyCode.RIGHT) == SAME) {
            uiController.changeView(CTRL_RIGHT_ARROW_KEY);
        } else if (keyCode.compareTo(KeyCode.LEFT) == SAME) {
            uiController.changeView(CTRL_LEFT_ARROW_KEY);
        }
    }

    private void processControlKeyNotPressed(KeyCode keyCode) {
        if (keyCode.compareTo(KeyCode.UP) == SAME) {
            _commandBar.getPrevCommand();
        } else if (keyCode.compareTo(KeyCode.DOWN) == SAME) {
            _commandBar.getNextCommand();
        } else if (keyCode.compareTo(KeyCode.LEFT) == SAME) {
            uiController.updateHelpView(CTRL_LEFT_ARROW_KEY);
        } else if (keyCode.compareTo(KeyCode.RIGHT) == SAME) {
            uiController.updateHelpView(CTRL_RIGHT_ARROW_KEY);
        }
    }

    /**
     * This method set the focus back on to the commandBar.
     * 
     */
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

    // @@author A0125415N
    /**
     * add a task into the system and display the changes in the selected view.
     * 
     * @param task
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
            boolean isSuccess = uiController.jumpToIndex(indexToJump);
            if (isSuccess) {
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

    private void executeLoadFrom(String loadFrom) {
        ResultSet resultSet = uiController.processLoadFrom(loadFrom);
        if (resultSet != null) {
            if (resultSet.isSuccess()) {
                resetCommandInput();
            }
        }
        _commandBar.showFeedBackMessage(COMMAND.LOADFROM, resultSet, loadFrom);
    }

    // @@author A0125514N

    private void executeChangeTheme() {
        _styleSheetSelector++;
        if (_styleSheetSelector >= STYLES.length) {
            _styleSheetSelector = ZERO;
        }
        _styleSheet = STYLES[_styleSheetSelector];
        _primaryStage.getScene().getStylesheets().clear();
        _primaryStage.getScene().getStylesheets().add(_styleSheet);
        uiController.changeTheme(_styleSheet);
    }

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

    private boolean isValidTheme(String theme) {
        if (theme == null) {
            return false;
        }
        for (int i = 0; i < STYLES.length; i++) {
            if (STYLES[i].equals(theme)) {
                _styleSheetSelector = i;
                return true;
            }
        }
        return false;
    }

    private String getStyleSheetList() {
        String styleList = "";
        for (int i = 0; i < STYLES.length; i++) {
            styleList = styleList.concat(STYLES[i]);
            if (i < STYLES.length - 1) {
                styleList = styleList.concat(", ");
            }
        }
        return styleList;
    }

    /**
     * Reset the layout and style of the commandBar for new input. usually
     * called after a success in executing a command.
     * 
     */
    private void resetCommandInput() {
        _commandBar.reset();
        focus();
    }
}
