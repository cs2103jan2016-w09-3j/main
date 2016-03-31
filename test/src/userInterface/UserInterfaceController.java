package userInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Inflater;

import dateParser.InputParser;
import dateParser.Pair;
import dateParser.CommandParser.COMMAND;
import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.TaskManagerInterface;
import mainLogic.Utils;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;

public class UserInterfaceController {

	// Singleton
	private static int numberOfInstance = 0;

	// view indicators
	final static int CALENDAR_VIEW = 0;
	final static int TASK_VIEW = 1;
	final static int EXPANDED_VIEW = 2;
	final static int ASSOCIATE_VIEW = 3;
	final static int FLOATING_VIEW = 4;
	final static int SEARCH_VIEW = 5;
	private int _previousView = -1;

	// Return values
	private static final int FAIL_TO_EXECUTE = -2;
	private static final int SUCCESSFULLY_ADDED_DIFF = -1;
	private static final int SUCCESSFULLY_ADDED = 1;

	private Stage _parentStage;
	private TaskViewUserInterface _taskViewInterface;
	private DescriptionComponent _descriptionComponent;
	private DetailComponent _detailComponent;
	private FloatingBarViewUserInterface _floatingBarComponent;
	private FloatingTaskUserInterface _floatingViewInterface;
	private SearchUserInterface _searchViewInterface;

	private HelpScreenUserInterface _helpScreen;

	private Rectangle2D _screenBounds;
	private boolean _fixedSize;

	// variables for animation and changing views;
	private int _currentView = TASK_VIEW;
	private ScrollTaskAnimation _scorllAnimation;
	private FloatingBarAnimationThread _floatingThread;

	// main logic class to interact
	private UserInterfaceExecuter _logicFace;

	// Debug purpose
	private static Logger logger = Logger.getLogger("UserInterfaceController");

	public static UserInterfaceController getInstance(Stage primaryStage) {
		if (numberOfInstance == 0) {
			numberOfInstance++;
			return new UserInterfaceController(primaryStage);
		} else {
			return null;
		}
	}

	private UserInterfaceController(Stage primaryStage) {
		try {
			Handler handler = new FileHandler("uiinterfaceLog.log");
			logger.addHandler(handler);
			logger.setLevel(Level.FINEST);
		} catch (IOException e) {
			System.out.println(e);
		}
		logger.log(Level.INFO, "UserInterfaceController Init");

		_parentStage = primaryStage;
		_logicFace = new UserInterfaceExecuter();
		recoverLostCommands();
	}

	public void initializeInterface(Rectangle2D screenBounds, boolean fixedSize) {
		this._screenBounds = screenBounds;
		this._fixedSize = fixedSize;
		initializeViews();
		show();
	}

	/**
	 * Initialize floatingBar Component, TaskViewUserInterface,
	 * DescriptionComponent, DetailsComponent
	 */
	public void initializeViews() {
		initializeHelpScreen();
		initializeFloatingBar();
		initializeFloatingView();
		initializeSearchView();
		initializeTaskView();
		_descriptionComponent = new DescriptionComponent(_parentStage, _screenBounds, _fixedSize);
		_detailComponent = new DetailComponent(_parentStage, _screenBounds, _fixedSize);
		updateComponents(0);
	}

	private void initializeHelpScreen() {
		_helpScreen = HelpScreenUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize);
	}

	private void initializeTaskView() {
		_taskViewInterface = TaskViewUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize);
		_taskViewInterface.buildComponent(_logicFace.getWorkingList(), _logicFace.getNextTimeListId());
	}

	private void initializeFloatingView() {
		_floatingViewInterface = FloatingTaskUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize);
	}

	private void initializeFloatingBar() {
		_floatingBarComponent = new FloatingBarViewUserInterface(_parentStage, _screenBounds, _fixedSize);
		TaskEntity floatingTask = _logicFace.getRandomFloating();
		if (floatingTask != null) {
			startFloatingThread();
		}
	}

	private void initializeSearchView() {
		_searchViewInterface = SearchUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize);
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
		} else if (_currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
			_taskViewInterface.show();
			_descriptionComponent.show();
			_floatingBarComponent.show();
			_detailComponent.show();

			_floatingViewInterface.hide();
			_searchViewInterface.hide();
		} else if (_currentView == FLOATING_VIEW) {
			_taskViewInterface.hide();
			_descriptionComponent.hide();
			_detailComponent.hide();
			_searchViewInterface.hide();

			_floatingBarComponent.show();
			_floatingViewInterface.show();
		} else if (_currentView == SEARCH_VIEW) {
			_taskViewInterface.hide();
			_descriptionComponent.hide();
			_detailComponent.hide();
			_searchViewInterface.hide();
			_floatingViewInterface.hide();

			_floatingBarComponent.show();
			_searchViewInterface.show();
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
	}

	/**
	 * unused method for now
	 */
	public void destory() {
		_taskViewInterface.destoryStage();
		_descriptionComponent.destoryStage();
		_floatingBarComponent.destoryStage();
		_detailComponent.destoryStage();
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

	public void updateDescriptionComponent() {
		if (_currentView == TASK_VIEW) {
			_taskViewInterface.rebuildDescriptionLabelsForWeek();
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
		} else if (_currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), EXPANDED_VIEW);
		}
	}

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
		_helpScreen.show();
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

	public boolean animateToExpanedView() {
		boolean isDoneTranslating = _taskViewInterface.isAtDetailedView(1);
		_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), EXPANDED_VIEW);
		translateComponentsY(_taskViewInterface.getTranslationY());
		return isDoneTranslating;
	}

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
	public int addTask(TaskEntity task, String rawInput, boolean toUpdateView) {
		if (toUpdateView) {
			if (_currentView == SEARCH_VIEW || _currentView == FLOATING_VIEW) {
				showMainView(-1);
			}
		}

		int insertedTo = _logicFace.addTask(task, buildRawCommand(rawInput));
		if (toUpdateView) {
			if (insertedTo > -2) {
				updateChangesToViews(insertedTo);
			}
		}

		if (insertedTo == SUCCESSFULLY_ADDED_DIFF) {
			if (_currentView == FLOATING_VIEW) {
				return SUCCESSFULLY_ADDED;
			} else if (_currentView == TASK_VIEW || _currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
				return SUCCESSFULLY_ADDED_DIFF;
			} else if (_currentView == SEARCH_VIEW) {
				return -5;
			}
		} else if (insertedTo > SUCCESSFULLY_ADDED_DIFF) {

			if (_currentView == FLOATING_VIEW) {
				return SUCCESSFULLY_ADDED_DIFF;
			} else if (_currentView == TASK_VIEW || _currentView == EXPANDED_VIEW || _currentView == ASSOCIATE_VIEW) {
				return SUCCESSFULLY_ADDED;
			}
		}
		return FAIL_TO_EXECUTE;
	}

	public int addBatchTask(ArrayList<TaskEntity> task, String rawInput, boolean toUpdateView) {
		int insertedTo = _logicFace.addBatch(task, buildRawCommand(rawInput));
		if (insertedTo == -1) {
			return -2;
		} else {
			if (toUpdateView) {
				updateChangesToViews(insertedTo);
			}
			return 1;
		}
	}

	public int deleteTask(String id, String rawInput, boolean toUpdateView) {
		int result = _logicFace.delete(id, buildRawCommand(rawInput));
		if (result == -2) {
			return result;
		}
		if (result > -2) {
			if (toUpdateView) {
				updateChangesToViews(result);
			}
		}
		return result;
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

	public boolean modifyTask(int idToModify, TaskEntity task, String rawInput, boolean toUpdateView) {
		int index = _logicFace.modify(idToModify, task, buildRawCommand(rawInput));
		if (index < 0) {
			return false;
		}
		if (toUpdateView) {
			updateChangesToViews(index);
		}
		return true;
	}

	public boolean jumpToIndex(String indexToJump) {
		int selected = _taskViewInterface.getSelectIndex();
		if (selected != -1) {
			_scorllAnimation = ScrollTaskAnimation.getInstance(selected, Utils.convertStringToInteger(indexToJump),
					this);
			_scorllAnimation.start();
			return true;
		} else {
			return false;
		}
	}

	public int executeSearch(String stringToSearch, String rawString, boolean toUpdateView) {
		int status = _logicFace.searchString(stringToSearch, buildRawCommand(rawString));
		if (status > -1) {
			if (toUpdateView) {
				showSearchView();
			}
			return status;
		}
		return status;
	}

	public boolean markAsCompleted(String indexZZ, String rawString, boolean toUpdateview) {
		int indexInt = Utils.convertStringToInteger(indexZZ);
		if (indexInt == -1) {
			return false;
		}
		int index = _logicFace.markAsDone(indexInt, buildRawCommand(rawString));
		if (index > -2) {
			if (toUpdateview) {
				updateChangesToViews(index);
			}
			return true;
		}
		return false;
	}

	public void stopScrollingAnimation() {
		if (_scorllAnimation != null) {
			if (_scorllAnimation.isRunning()) {
				_scorllAnimation.cancel();
			}
		}
		_scorllAnimation = null;
	}

	public boolean link(String indexZZ1, String indexZZ2, String rawString, boolean toUpdateView) {
		int index1 = Utils.convertStringToInteger(indexZZ1);
		int index2 = Utils.convertStringToInteger(indexZZ2);
		if (index1 != -1 && index2 != -1) {
			if (index1 < _logicFace.getWorkingList().size() && index2 < _logicFace.getWorkingList().size()) {
				_logicFace.getWorkingList().get(index1);
				_logicFace.getWorkingList().get(index2);
				boolean success = _logicFace.link(_logicFace.getWorkingList().get(index1),
						_logicFace.getWorkingList().get(index2), buildRawCommand(rawString));
				if (success) {
					if (toUpdateView) {
						updateChangesToViews(0);
					}
					return true;
				}
			}
		}
		return false;
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

	public String getTaskToEidtString(int indexToModify) {
		TaskEntity toPopulate = getTaskByID(indexToModify);
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
			String rawCommand = deStructToRawCommand(rawCommandWithView);
			String view = deStructToView(rawCommandWithView);
			int viewInt = Utils.convertStringToInteger(view);
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
					int id = Utils.convertStringToInteger(parser.getID());
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
		}
		_logicFace.switchView(TaskManager.DISPLAY_MAIN);
	}

	public boolean changeSaveDir(String dirPath) {
		return _logicFace.changeSaveDir(dirPath);
	}

}
