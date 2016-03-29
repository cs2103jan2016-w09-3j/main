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
	private Rectangle2D _screenBounds;
	private boolean _fixedSize;

	// variables for animation and changing views;
	private int _currentView = TASK_VIEW;
	private ScrollTaskAnimation _scorllAnimation;
	private FloatingBarAnimationThread _floatingThread;

	// main logic class to interact
	private TaskManagerInterface _taskManager;

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
		_taskManager = new TaskManagerInterface();
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
		// _taskManager.generateFakeData();// replace when integrate with angie
		initializeFloatingBar();
		initializeFloatingView();
		initializeSearchView();
		initializeTaskView();
		_descriptionComponent = new DescriptionComponent(_parentStage, _screenBounds, _fixedSize);
		_detailComponent = new DetailComponent(_parentStage, _screenBounds, _fixedSize);
		updateComponents(0);
	}

	private void initializeTaskView() {
		_taskViewInterface = TaskViewUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize);
		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), _taskManager.getNextTimeListId());
	}

	private void initializeFloatingView() {
		_floatingViewInterface = FloatingTaskUserInterface.getInstance(_parentStage, _screenBounds, _fixedSize);
	}

	private void initializeFloatingBar() {
		_floatingBarComponent = new FloatingBarViewUserInterface(_parentStage, _screenBounds, _fixedSize);
		TaskEntity floatingTask = _taskManager.getRandomFloating();
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
		_taskViewInterface.updateTranslateY(value);
		_descriptionComponent.updateTranslateY(value);
	}

	/**
	 * Change the view to when Ctrl left/right is entered. Ctrl + left -
	 * increment the _currentView by 1. Ctrl _right - decrement the _currentView
	 * by 1.
	 * 
	 * @param value
	 */
	public void changeView(int value) {
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

	public void showSearchView() {
		if (_currentView != FLOATING_VIEW && _currentView != SEARCH_VIEW) {
			_previousView = _currentView;
		}
		_currentView = SEARCH_VIEW;
		_taskManager.switchView(TaskManager.DISPLAY_SEARCH);
		ArrayList<TaskEntity> searchList = _taskManager.getWorkingList();
		_searchViewInterface.buildContent(searchList);
		show();
	}

	public void showFloatingView() {
		if (_currentView != FLOATING_VIEW && _currentView != SEARCH_VIEW) {
			_previousView = _currentView;
		}
		_currentView = FLOATING_VIEW;
		_taskManager.switchView(TaskManager.DISPLAY_FLOATING);
		ArrayList<TaskEntity> floatingList = _taskManager.getWorkingList();
		_floatingViewInterface.buildContent(floatingList);
		show();
	}

	public void showMainView(int view) {
		_taskManager.switchView(TaskManager.DISPLAY_MAIN);
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
			if (!(selelcted < _taskManager.getWorkingList().size() && selelcted > -1)) {
				selelcted = 0;
			}
		} else {
			selelcted = index;
		}
		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), selelcted);
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
			if (_taskManager.getRandomFloating() != null) {
				_floatingBarComponent.addTask(_taskManager.getRandomFloating().getName());
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
		TaskEntity task = _taskManager.getRandomFloating();
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
		_taskManager.backupCommand(rawInput);
		int insertedTo = _taskManager.add(task);
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
		_taskManager.backupCommand(rawInput);
		int insertedTo = _taskManager.add(task);
		if (insertedTo == -1) {
			return -2;
		} else {
			if (toUpdateView) {
				updateChangesToViews(insertedTo);
			}
			return 1;
		}
	}

	public int deleteTask(String id) {
		int result = _taskManager.delete(id);
		if (result == -2) {
			return result;
		}
		if (result > -2) {
			updateChangesToViews(result);
		}
		return result;
	}

	public TaskEntity getTaskByID(int ID) {
		ArrayList<TaskEntity> tasks = _taskManager.getWorkingList();
		if (ID < tasks.size()) {
			return tasks.get(ID);
		} else {
			return null;
		}
	}

	public int getTaskID(TaskEntity taskToCheck) {
		int index = -1;
		ArrayList<TaskEntity> tasks = _taskManager.getWorkingList();
		for (int i = 0; i < tasks.size(); i++) {
			TaskEntity taskOnList = tasks.get(i);
			System.out.println("test0");
			System.out.println(taskOnList.getDueDate().getTime());
			Calendar toCheckDate = taskToCheck.getDueDate();
			toCheckDate.clear(Calendar.MILLISECOND);

			Calendar onListDate = taskOnList.getDueDate();
			onListDate.clear(Calendar.MILLISECOND);

			if (toCheckDate.compareTo(onListDate) == 0) {
				System.out.println("test1");
				if (taskToCheck.getName().equals(taskOnList.getName())) {
					System.out.println("test2");
					index = i;
				}
			}
		}
		return index;
	}

	public boolean modifyTask(int idToModify, TaskEntity task) {
		int index = _taskManager.modify(idToModify, task);
		if (index < 0) {
			return false;
		}
		updateChangesToViews(index);
		return true;
	}

	public boolean jumpToIndex(String indexToJump) {
		int selected = _taskViewInterface.getSelectIndex();
		if (selected != -1) {
			_scorllAnimation = ScrollTaskAnimation.getInstance(selected, Utils.convertBase36ToDec(indexToJump), this);
			_scorllAnimation.start();
			return true;
		} else {
			return false;
		}
	}

	public int executeSearch(String stringToSearch) {
		int status = _taskManager.searchString(stringToSearch);
		if (status > -1) {
			showSearchView();
			return status;
		}
		return status;
	}

	public boolean markAsCompleted(String indexZZ) {
		int indexInt = Utils.convertBase36ToDec(indexZZ);
		if (indexInt == -1) {
			return false;
		}
		int index = _taskManager.markAsDone(indexInt);
		if (index > -1) {
			updateChangesToViews(index);
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

	public boolean link(String indexZZ1, String indexZZ2) {
		int index1 = Utils.convertBase36ToDec(indexZZ1);
		int index2 = Utils.convertBase36ToDec(indexZZ2);
		if (index1 != -1 && index2 != -1) {
			if (index1 < _taskManager.getWorkingList().size() && index2 < _taskManager.getWorkingList().size()) {
				_taskManager.getWorkingList().get(index1);
				_taskManager.getWorkingList().get(index2);
				boolean success = _taskManager.link(_taskManager.getWorkingList().get(index1),
						_taskManager.getWorkingList().get(index2));
				if (success) {
					updateChangesToViews(0);
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
			ArrayList<TaskEntity> floatingList = _taskManager.getWorkingList();
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

	public void saveStuff() {
		_taskManager.closeTaskManager();
	}

	public void recoverLostCommands() {
		Queue<String> qCommands = _taskManager.getBackedupCommands();
		while (!qCommands.isEmpty()) {
			String rawCommand = qCommands.poll();
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

					break;
				}
				}
			}
		}

	}

}
