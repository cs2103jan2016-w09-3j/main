/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *         This class controls all the other components except the command bar.
 */
package userInterface;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import dateParser.InputParser;
import dateParser.Pair;
import dateParser.ParserCommons;
import dateParser.CommandParser.COMMAND;
import entity.ResultSet;
import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.TaskUtils;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class UserInterfaceController {

	// Singleton
	private static UserInterfaceController _instance;

	private static final int ZERO_VALUE = 0;

	// view indicators
	final static int CALENDAR_VIEW = 0;
	final static int TASK_VIEW = 1;
	final static int EXPANDED_VIEW = 2;
	final static int ASSOCIATE_VIEW = 3;
	final static int FLOATING_VIEW = 4;
	final static int SEARCH_VIEW = 5;
	private int _previousView = -1;

	// Return values
	private static final int SUCCESSFULLY_ADDED_DIFF = -1;

	private String _styleSheet;
	private Stage _parentStage;
	private TaskViewUserInterface _taskViewInterface;
	private DescriptionComponent _descriptionComponent;
	private DetailComponent _detailComponent;
	private FloatingBarViewUserInterface _floatingBarComponent;
	private FloatingTaskUserInterface _floatingViewInterface;
	private SearchUserInterface _searchViewInterface;
	private HelpScreenUserInterface _helpScreen;

	// This mouseEvent is to aid focus, when user click on other areas of the
	// application, focus will be triggered to command bar.
	private EventHandler<MouseEvent> _mouseEvent;

	private Rectangle2D _screenBounds;
	private boolean _isFixedSize;

	// variables for animation and changing views;
	private int _currentView = TASK_VIEW;
	private ScrollTaskAnimation _scorllAnimation;
	private FloatingBarAnimationThread _floatingThread;

	// Main logic class to interact
	private UserInterfaceExecuter _logicFace;
	private boolean _isLoaded;

	// Debug purpose
	private static Logger logger = Logger.getLogger("UserInterfaceController");

	/**
	 * Create an instance of UserInterfaceController.
	 * 
	 * @param PrimaryStage
	 * @return Instance of UserInterfaceController only if there isn't an
	 *         instance already.
	 */
	public static UserInterfaceController getInstance(Stage primaryStage) {
		if (_instance == null) {
			_instance = new UserInterfaceController(primaryStage);
			return _instance;
		} else {
			return null;
		}
	}

	/**
	 * Initialize logic components and recover any lost data.
	 * 
	 * @param primaryStage
	 */
	private UserInterfaceController(Stage primaryStage) {
		try {
			Handler handler = new FileHandler("uiinterfaceLog.log");
			logger.addHandler(handler);
			logger.setLevel(Level.FINEST);
		} catch (IOException e) {
		}
		logger.log(Level.INFO, "Init");

		_parentStage = primaryStage;
		_logicFace = new UserInterfaceExecuter();
		_isLoaded = _logicFace.isFileLoadedSuccess();
		if (_isLoaded) {
			recoverLostCommands();
			logger.log(Level.INFO, "Recovery done.");
		}

	}

	/**
	 * Initialize the other views.
	 * 
	 * @param screenBounds
	 * @param isFixedSize
	 * @param styleSheet
	 * @param mouseEvent
	 */
	public void initializeInterface(Rectangle2D screenBounds, boolean isFixedSize, String styleSheet,
			EventHandler<MouseEvent> mouseEvent) {
		this._styleSheet = styleSheet;
		this._screenBounds = screenBounds;
		this._isFixedSize = isFixedSize;
		this._mouseEvent = mouseEvent;
		initializeViews();
		show();
	}

	/**
	 * Initialize an instance of FloatingBarViewUserInterface,
	 * TaskViewUserInterface, DescriptionComponent, DetailsComponent,
	 * FloatingTaskUserInterface, SearchUserInterface, HelpScreenUserInterface.
	 */
	public void initializeViews() {
		_currentView = TASK_VIEW;
		setManagerView(TASK_VIEW);
		logger.log(Level.INFO, "initializing views.");
		initializeHelpScreen();
		initializeFloatingBar();
		initializeFloatingView();
		initializeSearchView();
		initializeTaskView();
		initializeDescriptionComponent();
		initializeDetailComponent();
		updateComponents(ZERO_VALUE);
	}

	private void initializeHelpScreen() {
		logger.log(Level.INFO, "initializing help view.");
		String loadFromFilePath = _logicFace.getLoadFromFilePath();
		_helpScreen = HelpScreenUserInterface.getInstance(_parentStage, _screenBounds, _isFixedSize, _styleSheet,
				_mouseEvent, loadFromFilePath);
	}

	private void initializeTaskView() {
		logger.log(Level.INFO, "initializing task view.");
		_taskViewInterface = TaskViewUserInterface.getInstance(_parentStage, _screenBounds, _isFixedSize, _styleSheet,
				_mouseEvent);
		_taskViewInterface.buildComponent(_logicFace.getWorkingList(), _logicFace.getNextTimeListId());
	}

	private void initializeFloatingView() {
		logger.log(Level.INFO, "initializing floating view.");
		_floatingViewInterface = FloatingTaskUserInterface.getInstance(_parentStage, _screenBounds, _isFixedSize,
				_styleSheet, _mouseEvent);
	}

	private void initializeFloatingBar() {
		logger.log(Level.INFO, "initializing floating bar component.");
		_floatingBarComponent = FloatingBarViewUserInterface.getInstance(_parentStage, _screenBounds, _isFixedSize,
				_styleSheet, _mouseEvent);
		TaskEntity floatingTask = _logicFace.getRandomFloating();
		if (floatingTask != null) {
			startFloatingThread();
		}
	}

	private void initializeSearchView() {
		logger.log(Level.INFO, "initializing search view.");
		_searchViewInterface = SearchUserInterface.getInstance(_parentStage, _screenBounds, _isFixedSize, _styleSheet,
				_mouseEvent);
	}

	private void initializeDetailComponent() {
		logger.log(Level.INFO, "initializing detail component.");
		_detailComponent = DetailComponent.getInstance(_parentStage, _screenBounds, _isFixedSize, _styleSheet,
				_mouseEvent);
	}

	private void initializeDescriptionComponent() {
		logger.log(Level.INFO, "initializing description component.");
		_descriptionComponent = DescriptionComponent.getInstance(_parentStage, _screenBounds, _isFixedSize, _styleSheet,
				_mouseEvent);
	}

	/**
	 * Show the various components depending on _currentView.
	 */
	public void show() {
		if (_currentView == TASK_VIEW) {
			_taskViewInterface.show();
			_descriptionComponent.show();
			_floatingBarComponent.show();
			_detailComponent.show();
			_floatingViewInterface.hide();
			_searchViewInterface.hide();
			_helpScreen.hide();
		} else if (_currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
			_taskViewInterface.show();
			_descriptionComponent.show();
			_floatingBarComponent.show();
			_detailComponent.show();
			_floatingViewInterface.hide();
			_searchViewInterface.hide();
			_helpScreen.hide();
		} else if (_currentView == FLOATING_VIEW) {
			_taskViewInterface.hide();
			_descriptionComponent.hide();
			_detailComponent.hide();
			_searchViewInterface.hide();
			_floatingBarComponent.show();
			_floatingViewInterface.show();
			_helpScreen.hide();
		} else if (_currentView == SEARCH_VIEW) {
			_taskViewInterface.hide();
			_descriptionComponent.hide();
			_detailComponent.hide();
			_searchViewInterface.hide();
			_floatingViewInterface.hide();
			_floatingBarComponent.show();
			_searchViewInterface.show();
			_helpScreen.hide();
		}
	}

	/**
	 * Hide all views other then the PrimaryUserInterface.
	 */
	public void hide() {
		_taskViewInterface.hide();
		_descriptionComponent.hide();
		_floatingBarComponent.hide();
		_detailComponent.hide();
		_floatingViewInterface.hide();
		_searchViewInterface.hide();
		_helpScreen.hide();
	}

	/**
	 * De-initialize the components and kills the floating thread.
	 */
	public void destroy() {
		_taskViewInterface.destoryStage();
		_descriptionComponent.destoryStage();
		_detailComponent.destoryStage();
		_floatingBarComponent.destoryStage();
		_floatingViewInterface.destoryStage();
		_searchViewInterface.destoryStage();
		_helpScreen.destory();
		killFloatingThread();
	}

	/**
	 * Updates the TaskViewInterface, DetailComponent, DescriptionComponet
	 * according to the selected value;
	 * 
	 * @param value
	 */
	public void updateComponents(int value) {
		if (_currentView == TASK_VIEW || _currentView == EXPANDED_VIEW) {
			_taskViewInterface.update(value);
			TaskEntity selectedTask = _taskViewInterface.setItemSelected(value);
			_detailComponent.buildComponent(selectedTask);
			translateComponentsY(_taskViewInterface.getTranslationY());
			updateDescriptionComponent();
		} else if (_currentView == ASSOCIATE_VIEW) {
			_detailComponent.update(value);
		} else if (_currentView == FLOATING_VIEW) {
			_floatingViewInterface.update(value);
			_floatingViewInterface.setSelected(value);
		} else if (_currentView == SEARCH_VIEW) {
			_searchViewInterface.update(value);
			_searchViewInterface.setSelected(value);
		}
	}

	/**
	 * Updates the description panel to reflect the updates on the task view.
	 */
	public void updateDescriptionComponent() {
		if (_currentView == TASK_VIEW) {
			_taskViewInterface.rebuildDescriptionLabelsForWeek();
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
		} else if (_currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), EXPANDED_VIEW);
		}
	}

	/**
	 * Translates the views base on the user controls.
	 * 
	 * @param value
	 */
	public void translateComponentsY(double value) {
		_descriptionComponent.updateTranslateY(value);
		_taskViewInterface.updateTranslateY(value);
	}

	/**
	 * Changes the view according by the value.
	 * 
	 * @param value
	 */
	public void changeView(int value) {
		_helpScreen.hide();
		int view = _currentView + value;
		switch (view) {
		case TASK_VIEW: {
			setPreviousView();
			_currentView = view;
			_taskViewInterface.setView(_currentView);
			_detailComponent.setView(_currentView);
			updateComponents(0);
			startExpandAnimation(1);
			break;
		}
		case EXPANDED_VIEW: {
			setPreviousView();
			_currentView = view;
			_taskViewInterface.setView(_currentView);
			_detailComponent.setView(_currentView);
			updateComponents(0);
			startExpandAnimation(-1);
			break;
		}
		case ASSOCIATE_VIEW: {
			setPreviousView();
			if (_currentView == FLOATING_VIEW) {
				showMainView(ASSOCIATE_VIEW);
			} else {
				_currentView = view;
				_taskViewInterface.setView(_currentView);
				_detailComponent.setView(_currentView);
				updateComponents(0);
			}
			break;
		}
		case FLOATING_VIEW: {
			showFloatingView(0);
			break;
		}
		case SEARCH_VIEW: {
			showSearchView();
			break;
		}
		default:
			break;
		}
	}

	public void setPreviousView() {
		if (_currentView != FLOATING_VIEW && _currentView != SEARCH_VIEW) {
			_previousView = _currentView;
		}
	}

	/**
	 * Switches the view to the task view and brings up the help screen. If help
	 * screen is already shown, hides the help screen.
	 */
	public void showHelpView() {
		boolean isShown = _helpScreen.toggleHelpView();
		if (isShown) {
			showMainView(-1);
			_helpScreen.show();
		}
	}

	public void updateHelpView(int value) {
		_helpScreen.update(value);
	}

	public void showSearchView() {
		if (_currentView != FLOATING_VIEW && _currentView != SEARCH_VIEW) {
			_previousView = _currentView;
		}
		_currentView = SEARCH_VIEW;
		_logicFace.switchView(TaskManager.DISPLAY_SEARCH);
		ArrayList<TaskEntity> searchList = _logicFace.getWorkingList();
		_searchViewInterface.buildContent(searchList);
		show();
	}

	public void showFloatingView(int index) {
		if (_currentView != FLOATING_VIEW && _currentView != SEARCH_VIEW) {
			_previousView = _currentView;
		}
		_currentView = FLOATING_VIEW;
		_logicFace.switchView(TaskManager.DISPLAY_FLOATING);
		ArrayList<TaskEntity> floatingList = _logicFace.getWorkingList();
		_floatingViewInterface.buildContent(floatingList, index);
		show();
	}

	public void showMainView(int view) {
		_logicFace.switchView(TaskManager.DISPLAY_MAIN);
		if (view == -1) {
			if (_currentView == FLOATING_VIEW || _currentView == SEARCH_VIEW) {
				_currentView = _previousView;
				_taskViewInterface.setView(_currentView);
				_detailComponent.setView(_currentView);
				reBuildFrontView(-1);
			}
		} else {
			_currentView = ASSOCIATE_VIEW;
			_taskViewInterface.setView(_currentView);
			_detailComponent.setView(_currentView);
			reBuildFrontView(-1);
		}
		show();
	}

	/**
	 * Rebuilds the content of TaskViewUserInterface, DetailComponent,
	 * DescriptionComponent after a command is executed.
	 * 
	 * @param index
	 *            - if index is less then zero. rebuild views base on last, else
	 *            build with the index as selected.
	 */
	public void reBuildFrontView(int index) {
		int selelcted = 0;
		if (index < 0) {
			selelcted = _taskViewInterface.getSelectIndex();
			if (!(selelcted < _logicFace.getWorkingList().size() && selelcted > -1)) {
				selelcted = 0;
			}
		} else {
			selelcted = index;
		}
		_taskViewInterface.buildComponent(_logicFace.getWorkingList(), selelcted);
		_taskViewInterface.update(0);
		TaskEntity selectedTask = _taskViewInterface.setItemSelected(0);
		_detailComponent.buildComponent(selectedTask);
		if (_currentView == ASSOCIATE_VIEW) {
			_detailComponent.update(0);
		}
		translateComponentsY(_taskViewInterface.getTranslationY());
		updateDescriptionComponent();
	}

	/**
	 * Starts the service to animate the current view to the selected view.
	 * 
	 * @param direction
	 *            - direction -1 indicates ExpandedView to TaskView, direction 1
	 *            indicates TaskView to ExpandedView.
	 */
	public void startExpandAnimation(int direction) {
		TaskViewAnimation.getInstance(this, direction).start();
	}

	/**
	 * Increase the sizes of all components in the TaskView.
	 * 
	 * @return - true only when animation is done.
	 */
	public boolean isAtExpanedView() {
		boolean isDoneTranslating = _taskViewInterface.isAtDetailedView(1);
		_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), EXPANDED_VIEW);
		translateComponentsY(_taskViewInterface.getTranslationY());
		return isDoneTranslating;
	}

	/**
	 * Decrease the sizes of all components in the TaskView.
	 * 
	 * @return - true only when animation is done.
	 */
	public boolean isAtTaskView() {
		boolean isDoneTranslating = _taskViewInterface.isAtTaskView(-1);
		_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
		translateComponentsY(_taskViewInterface.getTranslationY());
		return isDoneTranslating;
	}

	/**
	 * Starts the thread to load a new floating task into the
	 * FloatingBarUserInterface.
	 */
	private void startFloatingThread() {
		if (_floatingThread == null) {
			if (_logicFace.getRandomFloating() != null) {
				_floatingBarComponent.addTask(_logicFace.getRandomFloating().getName());
				_floatingThread = new FloatingBarAnimationThread(this);
				_floatingThread.start();
			}
		}
	}

	/**
	 * Kills the thread that loads a new floating task into the
	 * FloatingBarUserInterface.
	 */
	public void killFloatingThread() {
		if (_floatingThread != null) {
			_floatingBarComponent.clearFloatingBar();
			_floatingThread.cancel();
			_floatingThread = null;
		}
	}

	/**
	 * Gets a random floating tasks and adds it to the FloatingBarUserInterface.
	 */
	public void addRandomTaskToDisplay() {
		TaskEntity task = _logicFace.getRandomFloating();
		if (task != null) {
			_floatingBarComponent.addTask(task.getName());
			if (_floatingThread == null) {
				startFloatingThread();
			}
		} else {
			if (_floatingThread != null) {
				killFloatingThread();
			}
		}
	}

	/**
	 * Translate the component in FloatingBarAnimation according to the
	 * percentageDone.
	 * 
	 * @param percentageDone
	 * @return - true only if animation is done.
	 */
	public boolean updateFloatingBar(double percentageDone) {
		boolean isDoneAnimating = _floatingBarComponent.animateView(percentageDone);
		return isDoneAnimating;
	}

	/**
	 * Add the task into the list.
	 * 
	 * @param task
	 * @param rawInput
	 * @param shouldUpdateView
	 *            (false only when recovering lost commands and testing)
	 * @return ResultSet
	 */
	public ResultSet addTask(TaskEntity task, String rawInput, boolean shouldUpdateView) {
		ResultSet resultSet = _logicFace.addTask(task, buildRawCommand(rawInput));
		if (resultSet.isSuccess()) {
			if (shouldUpdateView) {
				updateChangesToViews(resultSet.getIndex());
			}
		}

		return resultSet;
	}

	public ResultSet deleteTask(String id, String rawInput, boolean shouldUpdateView) {
		ResultSet resultSet = _logicFace.delete(id, buildRawCommand(rawInput));
		if (resultSet != null) {
			if (resultSet.isSuccess()) {
				if (shouldUpdateView) {
					updateChangesToViews(resultSet.getIndex());
				}
			}
			return resultSet;
		}
		return null;
	}

	/**
	 * Gets the task base on the id.
	 * 
	 * @param id
	 * @return TaskEntity
	 */
	public TaskEntity getTaskByID(int id) {
		ArrayList<TaskEntity> tasks = _logicFace.getWorkingList();
		if (id < tasks.size()) {
			return tasks.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Unused.
	 * 
	 * @param taskToCheck
	 * @return
	 */
	public int getTaskID(TaskEntity taskToCheck) {
		int index = -1;
		ArrayList<TaskEntity> tasks = _logicFace.getWorkingList();
		for (int i = 0; i < tasks.size(); i++) {
			TaskEntity taskOnList = tasks.get(i);
			Calendar toCheckDate = taskToCheck.getDueDate();
			toCheckDate.clear(Calendar.MILLISECOND);
			Calendar onListDate = taskOnList.getDueDate();
			onListDate.clear(Calendar.MILLISECOND);
			if (toCheckDate.compareTo(onListDate) == 0) {
				if (taskToCheck.getName().equals(taskOnList.getName())) {
					index = i;
				}
			}
		}
		return index;
	}

	/**
	 * Calls the logic component to modify the task.
	 * 
	 * @param idToModify
	 * @param task
	 * @param rawInput
	 * @param shouldUpdateView
	 * @return ResultSet
	 */
	public ResultSet modifyTask(int idToModify, TaskEntity task, String rawInput, boolean shouldUpdateView) {
		ResultSet resultSet = _logicFace.modify(idToModify, task, buildRawCommand(rawInput));
		if (resultSet.isSuccess()) {
			if (shouldUpdateView) {
				updateChangesToViews(resultSet.getIndex());
			}
		}
		return resultSet;
	}

	/**
	 * Starts the scrolling animation to auto scroll to the selected index.
	 * 
	 * @param indexToJump
	 * @return true only if index is valid
	 */
	public boolean jumpToIndex(String indexToJump) {
		int selected = _taskViewInterface.getSelectIndex();
		if (selected != -1) {
			_scorllAnimation = ScrollTaskAnimation.getInstance(selected, TaskUtils.convertStringToInteger(indexToJump),
					this);
			_scorllAnimation.start();
			return true;
		} else {
			return false;
		}
	}

	public ResultSet executeSearch(String stringToSearch, String rawString, boolean shouldUpdateView) {
		ResultSet resultSet = _logicFace.searchString(stringToSearch, buildRawCommand(rawString));
		if (resultSet.getSearchCount() > 0) {
			if (shouldUpdateView) {
				showSearchView();
			}
		}
		return resultSet;
	}

	public ResultSet markAsCompleted(String indexZZ, String rawString, boolean shouldUpdateView) {
		int indexInt = TaskUtils.convertStringToInteger(indexZZ);
		if (indexInt == -1) {
			return null;
		}
		ResultSet resultSet = _logicFace.markAsDone(indexInt, buildRawCommand(rawString));
		if (resultSet.isSuccess()) {
			if (shouldUpdateView) {
				updateChangesToViews(resultSet.getIndex());
			}
		}
		return resultSet;
	}

	public void stopScrollingAnimation() {
		if (_scorllAnimation != null) {
			if (_scorllAnimation.isRunning()) {
				_scorllAnimation.cancel();
			}
		}
		_scorllAnimation = null;
	}

	/**
	 * Calls the logic component to link the two task together.
	 * 
	 * @param indexZZ1
	 * @param indexZZ2
	 * @param rawString
	 * @param shouldUpdateView
	 * @return ResultSet
	 */
	public ResultSet link(String indexZZ1, String indexZZ2, String rawString, boolean shouldUpdateView) {
		int index1 = TaskUtils.convertStringToInteger(indexZZ1);
		int index2 = TaskUtils.convertStringToInteger(indexZZ2);
		if (index1 != -1 && index2 != -1) {
			if (index1 < _logicFace.getWorkingList().size() && index2 < _logicFace.getWorkingList().size()) {
				_logicFace.getWorkingList().get(index1);
				_logicFace.getWorkingList().get(index2);
				ResultSet resultSet = _logicFace.link(_logicFace.getWorkingList().get(index1),
						_logicFace.getWorkingList().get(index2), buildRawCommand(rawString));
				if (resultSet.isSuccess()) {
					if (shouldUpdateView) {
						updateChangesToViews(resultSet.getIndex());
					}
				}
				return resultSet;
			} else {
				ResultSet rs = new ResultSet();
				rs.setFail();
				rs.setIndex(-1);
				return rs;
			}
		}
		return null;
	}

	/**
	 * Updates the UserInterface Components to reflect the changes upon command
	 * execution.
	 * 
	 * @param index
	 *            - determines if the command executed is in the selected view.
	 *            - if index is -1, the task current targeted task is at another
	 *            view.
	 */
	public void updateChangesToViews(int index) {
		if (_currentView == TASK_VIEW || _currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
			if (index == SUCCESSFULLY_ADDED_DIFF) {
				startFloatingThread();
			}
			reBuildFrontView(index);
		} else if (_currentView == FLOATING_VIEW) {
			ArrayList<TaskEntity> floatingList = _logicFace.getWorkingList();
			if (floatingList == null || floatingList.size() == 0) {
				killFloatingThread();
			} else {
				startFloatingThread();
			}
			_floatingViewInterface.buildContent(floatingList, index);
		} else if (_currentView == SEARCH_VIEW) {
			showSearchView();
		}
	}

	/**
	 * Builds the String command base on the index of the task.
	 * 
	 * @param indexToModify
	 * @return command.
	 */
	public String getTaskToEditString(int indexToModify) {
		TaskEntity toPopulate = getTaskByID(indexToModify);
		if (toPopulate != null) {
			String toSet = " " + toPopulate.getName();
			if ((toPopulate.getDescription() != null) && (toPopulate.getDescription().trim().length() > 0)) {
				toSet += " : " + toPopulate.getDescription();
			}
			if (toPopulate.getStartDate() != null) {
				Calendar c = toPopulate.getStartDate();
				int day = c.get(Calendar.DATE);
				int month = c.get(Calendar.MONTH) + 1;
				int year = c.get(Calendar.YEAR);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int min = c.get(Calendar.MINUTE);
				String hourStr = ParserCommons.padTime(hour);
				String minStr = ParserCommons.padTime(min);
				toSet += " " + day + "-" + month + "-" + year + " " + hourStr + minStr + "hrs to ";
			}
			if (toPopulate.getDueDate() != null) {
				Calendar c = toPopulate.getDueDate();
				int day = c.get(Calendar.DATE);
				int month = c.get(Calendar.MONTH) + 1;
				int year = c.get(Calendar.YEAR);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int min = c.get(Calendar.MINUTE);
				String hourStr = ParserCommons.padTime(hour);
				String minStr = ParserCommons.padTime(min);
				toSet += " " + day + "-" + month + "-" + year + " " + hourStr + minStr + "hrs ";
			}
			if (toPopulate.getProjectHead() != null) {
				toSet += " @" + toPopulate.getProjectHead().getName();
			}
			return toSet;
		}
		return null;
	}

	public void saveStuff() {
		_logicFace.closeTaskManager();
	}

	/**
	 * Append the index of the view in-front of the raw command.
	 * 
	 * @param rawWithView
	 * @return actual raw command.
	 */
	public String buildRawCommand(String raw) {
		String full = Integer.toString(_currentView).concat(" ").concat(raw);
		return full;
	}

	/**
	 * Breaks the String into the view index and the actual raw command.
	 * 
	 * @param rawWithView
	 * @return actual raw command.
	 */
	public String deStructToRawCommand(String rawWithView) {
		if (rawWithView.split(" ").length > 0) {
			int index = rawWithView.indexOf(" ");
			if (index != -1) {
				return rawWithView.substring(index + 1);
			}
		}
		return null;
	}

	/**
	 * Breaks the String into the view index and the actual raw command.
	 * 
	 * @param rawWithView
	 * @return index of view.
	 */
	public String deStructToView(String rawWithView) {
		if (rawWithView.split(" ").length > 1) {
			String[] spilt = rawWithView.split(" ");
			return spilt[0];
		}
		return null;
	}

	/**
	 * Sets TaskManger view, serves only for recovering commands.
	 * 
	 * @param view
	 */
	private void setManagerView(int view) {
		if (view == TASK_VIEW) {
			_logicFace.switchView(TaskManager.DISPLAY_MAIN);
		} else if (view == EXPANDED_VIEW) {
			_logicFace.switchView(TaskManager.DISPLAY_MAIN);
		} else if (view == ASSOCIATE_VIEW) {
			_logicFace.switchView(TaskManager.DISPLAY_MAIN);
		} else if (view == SEARCH_VIEW) {
			_logicFace.switchView(TaskManager.DISPLAY_SEARCH);
		} else if (view == FLOATING_VIEW) {
			_logicFace.switchView(TaskManager.DISPLAY_FLOATING);
		}
	}

	/**
	 * Gets the list of commands that has been saves and has not been executed.
	 * Execute those commands.
	 */
	public void recoverLostCommands() {
		Queue<String> qCommands = _logicFace.getBackedupCommands();
		while (!qCommands.isEmpty()) {
			String rawCommandWithView = qCommands.poll();
			runCommands(rawCommandWithView);
		}
		_logicFace.switchView(TaskManager.DISPLAY_MAIN);
	}

	/**
	 * Process the rawCommand and execute the command. This method is only
	 * called during an undo command or during recovery.
	 * 
	 * @param rawCommandWithView
	 * @return the view the command was previously executed in.
	 */
	private int runCommands(String rawCommandWithView) {
		String rawCommand = deStructToRawCommand(rawCommandWithView);
		String view = deStructToView(rawCommandWithView);
		int viewInt = TaskUtils.convertStringToInteger(view);
		if (viewInt != -1) {
			setManagerView(viewInt);
		}
		if (rawCommand != null) {
			InputParser parser = new InputParser(rawCommand);
			COMMAND cmd = parser.getCommand();
			switch (cmd) {
			case ADD: {
				ArrayList<TaskEntity> tasks = parser.getTask();
				if (tasks.size() == 1) {
					addTask(tasks.get(0), rawCommand, false);
				}
				break;
			}
			case DELETE: {
				String id = parser.getID();
				deleteTask(id, rawCommand, false);
				break;
			}
			case EDIT: {
				int id = TaskUtils.convertStringToInteger(parser.getID());
				parser.removeId();
				ArrayList<TaskEntity> tasks = parser.getTask();
				if (tasks.size() == 1) {
					modifyTask(id, tasks.get(0), rawCommand, false);
				}
				break;
			}
			case DONE: {
				String id = parser.getID();
				markAsCompleted(id, rawCommand, false);
				break;
			}
			case SEARCH: {
				String searchStirng = parser.getSearchString();
				executeSearch(searchStirng, rawCommand, false);
				_logicFace.switchView(TaskManager.DISPLAY_SEARCH);
				break;
			}
			case LINK: {
				Pair<String, String> ids = parser.getLinkID();
				link(ids.getFirst(), ids.getSecond(), rawCommand, false);
				break;
			}
			default:
				break;
			}
		}
		return viewInt;
	}

	public ResultSet changeSaveDir(String dirPath) {
		ResultSet resultSet = _logicFace.changeSaveDir(dirPath);
		if (resultSet.isSuccess()) {
			_helpScreen.changeFilePath(_logicFace.getLoadFromFilePath());
		}
		return resultSet;
	}

	/**
	 * Gets the list of commands to run on the file start state.
	 * 
	 * @return ResultSet
	 */
	public ResultSet undoLastCommand() {
		int managerView = _logicFace.getCurrentManagerView();
		ArrayList<String> commandsToRun = _logicFace.getCommandsToRun();
		ResultSet resultSet = new ResultSet();
		if (commandsToRun == null) {
			resultSet.setFail();
			return resultSet;
		}
		resultSet.setSuccess();
		if (commandsToRun.size() == 0) {
			updateChangesToViews(-1);
			return resultSet;
		}

		for (int i = 0; i < commandsToRun.size(); i++) {
			runCommands(commandsToRun.get(i));
		}
		_logicFace.switchView(managerView);
		updateChangesToViews(-1);
		_logicFace.undoComplete();
		return resultSet;
	}

	/**
	 * Change the theme of all components.
	 * 
	 * @param styleSheet
	 * @return ResultSet
	 */
	public ResultSet changeTheme(String styleSheet) {
		_taskViewInterface.changeTheme(styleSheet);
		_descriptionComponent.changeTheme(styleSheet);
		_detailComponent.changeTheme(styleSheet);
		_floatingBarComponent.changeTheme(styleSheet);
		_floatingViewInterface.changeTheme(styleSheet);
		_searchViewInterface.changeTheme(styleSheet);
		_helpScreen.changeTheme(styleSheet);
		ResultSet resultSet = _logicFace.changeTheme(styleSheet);
		return resultSet;
	}

	public String loadTheme() {
		return _logicFace.loadTheme();
	}

	/**
	 * Calls the logic component to load the new file. Kills all threads and
	 * rebuild interface if loads returns true.
	 * 
	 * @param loadFrom
	 * @return ResultSet
	 */
	public ResultSet processLoadFrom(String loadFrom) {
		ResultSet resultSet = _logicFace.loadFrom(loadFrom);
		if (resultSet != null) {
			if (resultSet.isSuccess()) {
				_helpScreen.changeFilePath(_logicFace.getLoadFromFilePath());
				setManagerView(TASK_VIEW);
				_currentView = TASK_VIEW;
				killFloatingThread();
				int id = _logicFace.getNextTimeListId();
				updateChangesToViews(id);
				showMainView(-1);
				startFloatingThread();
			}
		}
		return resultSet;
	}

	/**
	 * Process the enter command.
	 * 
	 * @return true only if selection is valid.
	 */
	public boolean processEnter() {
		if (_currentView == SEARCH_VIEW) {
			TaskEntity task = _searchViewInterface.processEnter();
			if (task != null) {
				if (!task.isFloating()) {
					setManagerView(TASK_VIEW);
					_currentView = TASK_VIEW;
					ArrayList<TaskEntity> tasks = _logicFace.getWorkingList();

					for (int i = 0; i < tasks.size(); i++) {
						if (task.getId() == tasks.get(i).getId()) {
							updateChangesToViews(i);
							showMainView(-1);
							break;
						}
					}
					return true;
				} else {
					setManagerView(FLOATING_VIEW);
					_currentView = FLOATING_VIEW;
					ArrayList<TaskEntity> tasks = _logicFace.getWorkingList();
					for (int i = 0; i < tasks.size(); i++) {
						if (task.getId() == tasks.get(i).getId()) {
							showFloatingView(i);
							break;
						}
					}
					return true;
				}
			}
		} else if (_currentView == ASSOCIATE_VIEW) {
			TaskEntity task = _detailComponent.processEnter();
			if (task != null) {
				setManagerView(TASK_VIEW);
				ArrayList<TaskEntity> tasks = _logicFace.getWorkingList();

				for (int i = 0; i < tasks.size(); i++) {
					if (task.getId() == tasks.get(i).getId()) {
						updateChangesToViews(i);
						break;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the file given has loaded properly.
	 * 
	 * @return ResultSet
	 */
	public ResultSet isFileLoadedProper() {
		if (!_isLoaded) {
			ResultSet rs = new ResultSet();
			rs.setFail();
			rs.setStatus(ResultSet.STATUS_BAD);
			return rs;
		}
		return null;
	}
}
