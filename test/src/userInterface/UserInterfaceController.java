//@@author A0125514N
package userInterface;

import java.io.IOException;
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
	private boolean _fixedSize;

	// variables for animation and changing views;
	private int _currentView = TASK_VIEW;
	private ScrollTaskAnimation _scorllAnimation;
	private FloatingBarAnimationThread _floatingThread;

	// Main logic class to interact
	private UserInterfaceExecuter _logicFace;

	// Debug purpose
	private static Logger logger = Logger.getLogger("UserInterfaceController");

	/**
	 * Only 1 instance of UserInterfaceController can be initialize.
	 * 
	 * @param primaryStage
	 * @return instance of UserInterfaceController
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
	 * Initialize logic components and try to recover any lost data.
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
		recoverLostCommands();
		logger.log(Level.INFO, "Recovery done.");

	}

	public void initializeInterface(Rectangle2D screenBounds, boolean fixedSize, String styleSheet,
			EventHandler<MouseEvent> mouseEvent) {
		this._styleSheet = styleSheet;
		this._screenBounds = screenBounds;
		this._fixedSize = fixedSize;
		this._mouseEvent = mouseEvent;
		initializeViews();
		show();
	}

	/**
	 * Initialize floatingBar Component, TaskViewUserInterface,
	 * DescriptionComponent, DetailsComponent
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
		_helpScreen = HelpScreenUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize, _styleSheet,
				_mouseEvent);
	}

	private void initializeTaskView() {
		logger.log(Level.INFO, "initializing task view.");
		_taskViewInterface = TaskViewUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize, _styleSheet,
				_mouseEvent);
		_taskViewInterface.buildComponent(_logicFace.getWorkingList(), _logicFace.getNextTimeListId());
	}

	private void initializeFloatingView() {
		logger.log(Level.INFO, "initializing floating view.");
		_floatingViewInterface = FloatingTaskUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize,
				_styleSheet, _mouseEvent);
	}

	private void initializeFloatingBar() {
		logger.log(Level.INFO, "initializing floating bar component.");
		_floatingBarComponent = FloatingBarViewUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize, _styleSheet,
				_mouseEvent);
		TaskEntity floatingTask = _logicFace.getRandomFloating();
		if (floatingTask != null) {
			startFloatingThread();
		}
	}

	private void initializeSearchView() {
		logger.log(Level.INFO, "initializing search view.");
		_searchViewInterface = SearchUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize, _styleSheet,
				_mouseEvent);
	}

	private void initializeDetailComponent() {
		logger.log(Level.INFO, "initializing detail component.");
		_detailComponent = DetailComponent.getInstance(_parentStage, _screenBounds, _fixedSize, _styleSheet,
				_mouseEvent);
	}

	private void initializeDescriptionComponent() {
		logger.log(Level.INFO, "initializing description component.");
		_descriptionComponent = DescriptionComponent.getInstance(_parentStage, _screenBounds, _fixedSize, _styleSheet,
				_mouseEvent);
	}

	/**
	 * Show the various components depending on _currentView
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
	 * unused method for now
	 */
	public void destory() {
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
	 * This method update the taskViewInterface, DetailComponent,
	 * DescriptionComponet according to the selected value;
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
	 * Update the description panel to reflect the updates on the task view.
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
	 * translate the views base on the user controls.
	 * 
	 * @param value
	 */
	public void translateComponentsY(double value) {
		_descriptionComponent.updateTranslateY(value);
		_taskViewInterface.updateTranslateY(value);
	}

	/**
	 * Change the view to when Ctrl left/right is entered. Ctrl + left -
	 * increment the _currentView by 1. Ctrl _right - decrement the _currentView
	 * by 1.
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
			showFloatingView();
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

	public void showHelpView() {
		boolean isShown = _helpScreen.toggleHelpView();
		if(isShown){
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

	public void showFloatingView() {
		if (_currentView != FLOATING_VIEW && _currentView != SEARCH_VIEW) {
			_previousView = _currentView;
		}
		_currentView = FLOATING_VIEW;
		_logicFace.switchView(TaskManager.DISPLAY_FLOATING);
		ArrayList<TaskEntity> floatingList = _logicFace.getWorkingList();
		_floatingViewInterface.buildContent(floatingList);
		show();
	}

	public void showMainView(int view) {
		_logicFace.switchView(TaskManager.DISPLAY_MAIN);
		if (view == -1) {
			if (_currentView == FLOATING_VIEW || _currentView == SEARCH_VIEW) {
				_currentView = _previousView;
				_taskViewInterface.setView(_currentView);
				_detailComponent.setView(_currentView);
				reBuildFrontView(-5);
			}
		} else {
			_currentView = ASSOCIATE_VIEW;
			_taskViewInterface.setView(_currentView);
			_detailComponent.setView(_currentView);
			reBuildFrontView(-5);
		}
		show();
	}

	/**
	 * rebuilds task view, expanded view, associate view and their components
	 * after a command is executed.
	 * 
	 * @param index
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
	 * This method will start the service to animate the current view to the
	 * selected view.
	 */
	public void startExpandAnimation(int direction) {
		TaskViewDescriptionAnimation.getInstance(this, direction).start();
	}

	/**
	 * This method is only called by the animator thread to animation the view
	 * to the Expanded view.
	 * 
	 * @return - boolean true when animation is done.
	 */
	public boolean animateToExpanedView() {
		boolean isDoneTranslating = _taskViewInterface.isAtDetailedView(1);
		_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), EXPANDED_VIEW);
		translateComponentsY(_taskViewInterface.getTranslationY());
		return isDoneTranslating;
	}

	/**
	 * This method is only called by the animator thread to animation the view
	 * to the Main view.
	 * 
	 * @return - boolean true when animation is done.
	 */
	public boolean animateToTaskView() {
		boolean isDoneTranslating = _taskViewInterface.isAtTaskView(-1);
		_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
		translateComponentsY(_taskViewInterface.getTranslationY());
		return isDoneTranslating;
	}

	private void startFloatingThread() {
		if (_floatingThread == null) {
			if (_logicFace.getRandomFloating() != null) {
				_floatingBarComponent.addTask(_logicFace.getRandomFloating().getName());
				_floatingThread = new FloatingBarAnimationThread(this);
				_floatingThread.start();
			}
		}
	}

	public void killFloatingThread() {
		if (_floatingThread != null) {
			_floatingThread.cancel();
			_floatingThread = null;
		}
	}

	/**
	 * This method is called to add a random task into the floating bar. It
	 * starts the floating bar thread if it is not started.
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
	 * This method is used only by FloatingTaskAnimation to update the floating
	 * bar view.
	 * 
	 * @param percentageDone
	 * @return
	 */
	public boolean updateFloatingBar(double percentageDone) {
		boolean isDoneAnimating = _floatingBarComponent.animateView(percentageDone);
		return isDoneAnimating;
	}

	/**
	 * Add the task into the list.
	 * 
	 * @param task
	 * @param toUpdateView
	 *            (false only when recovering lost commands and testing)
	 * @return
	 */
	public ResultSet addTask(TaskEntity task, String rawInput, boolean toUpdateView) {
		ResultSet resultSet = _logicFace.addTask(task, buildRawCommand(rawInput));
		if (resultSet.isSuccess()) {
			if (toUpdateView) {
				updateChangesToViews(resultSet.getIndex());
			}
		}

		return resultSet;
	}

	public int addBatchTask(ArrayList<TaskEntity> task, String rawInput, boolean toUpdateView) {
		/*
		 * int insertedTo = _logicFace.addBatch(task,
		 * buildRawCommand(rawInput)); if (insertedTo == -1) { return -2; } else
		 * { if (toUpdateView) { updateChangesToViews(insertedTo); } return 1; }
		 */
		return -2;
	}

	public ResultSet deleteTask(String id, String rawInput, boolean toUpdateView) {
		ResultSet resultSet = _logicFace.delete(id, buildRawCommand(rawInput));
		if (resultSet != null) {
			if (resultSet.isSuccess()) {
				if (toUpdateView) {
					updateChangesToViews(resultSet.getIndex());
				}
			}
			return resultSet;
		}
		return null;
	}

	public TaskEntity getTaskByID(int id) {
		ArrayList<TaskEntity> tasks = _logicFace.getWorkingList();
		if (id < tasks.size()) {
			return tasks.get(id);
		} else {
			return null;
		}
	}

	/**
	 * unused already.
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

	public ResultSet modifyTask(int idToModify, TaskEntity task, String rawInput, boolean toUpdateView) {
		ResultSet resultSet = _logicFace.modify(idToModify, task, buildRawCommand(rawInput));
		if (resultSet.isSuccess()) {
			if (toUpdateView) {
				updateChangesToViews(resultSet.getIndex());
			}
		}
		return resultSet;
	}

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

	public ResultSet executeSearch(String stringToSearch, String rawString, boolean toUpdateView) {
		ResultSet resultSet = _logicFace.searchString(stringToSearch, buildRawCommand(rawString));
		if (resultSet.getSearchCount() > 0) {
			if (toUpdateView) {
				showSearchView();
			}
		}
		return resultSet;
	}

	public ResultSet markAsCompleted(String indexZZ, String rawString, boolean toUpdateview) {
		int indexInt = TaskUtils.convertStringToInteger(indexZZ);
		if (indexInt == -1) {
			return null;
		}
		ResultSet resultSet = _logicFace.markAsDone(indexInt, buildRawCommand(rawString));
		if (resultSet.isSuccess()) {
			if (toUpdateview) {
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

	public ResultSet link(String indexZZ1, String indexZZ2, String rawString, boolean toUpdateView) {
		int index1 = TaskUtils.convertStringToInteger(indexZZ1);
		int index2 = TaskUtils.convertStringToInteger(indexZZ2);
		if (index1 != -1 && index2 != -1) {
			if (index1 < _logicFace.getWorkingList().size() && index2 < _logicFace.getWorkingList().size()) {
				_logicFace.getWorkingList().get(index1);
				_logicFace.getWorkingList().get(index2);
				ResultSet resultSet = _logicFace.link(_logicFace.getWorkingList().get(index1),
						_logicFace.getWorkingList().get(index2), buildRawCommand(rawString));
				if (resultSet.isSuccess()) {
					if (toUpdateView) {
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
	 * This method is called upon a command execution to update the views.
	 * 
	 * @param index
	 *            - determines if the command executed is in the selected view.
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
				_floatingBarComponent.clearFloatingBar();
			} else {
				startFloatingThread();
			}
			_floatingViewInterface.buildContent(floatingList);
		} else if (_currentView == SEARCH_VIEW) {
			showSearchView();
		}
	}

	public String getTaskToEditString(int indexToModify) {
		TaskEntity toPopulate = getTaskByID(indexToModify);
		if (toPopulate != null) {
			String toSet = " " + toPopulate.getName();
			if ((toPopulate.getDescription() != null) && (toPopulate.getDescription().trim().length() > 0)) {
				// System.out.println("desc"+toPopulate.getDescription());
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

	public String buildRawCommand(String raw) {
		String full = Integer.toString(_currentView).concat(" ").concat(raw);
		return full;
	}

	public String deStructToRawCommand(String rawWithView) {
		if (rawWithView.split(" ").length > 0) {
			int index = rawWithView.indexOf(" ");
			if (index != -1) {
				return rawWithView.substring(index + 1);
			}
		}
		return null;
	}

	public String deStructToView(String rawWithView) {
		if (rawWithView.split(" ").length > 1) {
			String[] spilt = rawWithView.split(" ");
			return spilt[0];
		}
		return null;
	}

	/**
	 * Set TaskManger view, serves only for recovering commands.
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

	public void recoverLostCommands() {
		Queue<String> qCommands = _logicFace.getBackedupCommands();
		while (!qCommands.isEmpty()) {
			String rawCommandWithView = qCommands.poll();
			runCommands(rawCommandWithView);
		}
		_logicFace.switchView(TaskManager.DISPLAY_MAIN);
	}

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
				} else {
					addBatchTask(tasks, rawCommand, false);
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
		return _logicFace.changeSaveDir(dirPath);
	}

	public ResultSet undoLastCommand() {
		ArrayList<String> commandsToRun = _logicFace.getCommandsToRun();
		ResultSet resultSet = new ResultSet();
		if (commandsToRun == null) {
			resultSet.setFail();
			return resultSet;
		}
		resultSet.setSuccess();

		if (commandsToRun.size() == 0) {
			setView(TASK_VIEW);
			return resultSet;
		}
		int view = -1;
		for (int i = 0; i < commandsToRun.size(); i++) {
			view = runCommands(commandsToRun.get(i));
		}
		if (view != -1) {
			setView(view);
		}
		_logicFace.undoComplete();
		return resultSet;
	}

	private void setView(int view) {
		_currentView = view;
		updateChangesToViews(-1);
	}

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

	public ResultSet processLoadFrom(String loadFrom) {
		ResultSet resultSet = _logicFace.loadFrom(loadFrom);
		if (resultSet != null) {
			if (resultSet.isSuccess()) {
				setManagerView(TASK_VIEW);
				_currentView = TASK_VIEW;
				int id = _logicFace.getNextTimeListId();
				updateChangesToViews(id);
				showMainView(-1);
			}
		}
		return resultSet;
	}

	public boolean processEnter() {
		if (_currentView == SEARCH_VIEW) {
			TaskEntity task = _searchViewInterface.processEnter();
			if (task != null) {
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
			}
		}
		return false;
	}
}
