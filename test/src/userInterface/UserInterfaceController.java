package userInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.TaskEntity;
import mainLogic.TaskManager;
import mainLogic.Utils;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;

public class UserInterfaceController {

	// view indicators
	final static int CALENDAR_VIEW = 0;
	final static int TASK_VIEW = 1;
	final static int EXPANDED_VIEW = 2;

	private Stage _parentStage;
	private TaskViewUserInterface _taskViewInterface;
	private DescriptionComponent _descriptionComponent;
	private DetailComponent _detailComponent;
	private FloatingBarViewUserInterface _floatingBarComponent;
	private Rectangle2D _screenBounds;
	private boolean _fixedSize;

	// variables for animation and changing views;
	private int _currentView = TASK_VIEW;
	private TaskViewDescriptionAnimation _expandAnimation;
	private ScrollTaskAnimation _scorllAnimation;

	// main logic class to interact
	private TaskManager _taskManager;

	private static Logger logger = Logger.getLogger("UserInterfaceController");

	public UserInterfaceController(Stage primaryStage) {
		try {
			Handler fh = new FileHandler("uiinterfaceLog.log");
			logger.addHandler(fh);
			logger.setLevel(Level.FINEST);
		} catch (IOException e) {
			System.out.println(e);
		}
		logger.log(Level.INFO, "UserInterfaceController Init");
		_parentStage = primaryStage;
		_taskManager = TaskManager.getInstance();
	}

	public void initializeInterface(Rectangle2D screenBounds, boolean fixedSize) {
		this._screenBounds = screenBounds;
		this._fixedSize = fixedSize;
		initializeTaskView();
		show();
	}

	/**
	 * Initialize floatingBar Component, TaskViewUserInterface,
	 * DescriptionComponent, DetailsComponent
	 */
	public void initializeTaskView() {
		_taskViewInterface = new TaskViewUserInterface(_parentStage, _screenBounds, _fixedSize);
		initilizeFloatingBar();
		_descriptionComponent = new DescriptionComponent(_parentStage, _screenBounds, _fixedSize);
		_detailComponent = new DetailComponent(_parentStage, _screenBounds, _fixedSize);
		_taskManager.generateFakeData();// replace when integrate with angie
		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), _taskManager.getNextTimeListId());
		updateUI(0);
	}

	public void initilizeFloatingBar() {
		_floatingBarComponent = new FloatingBarViewUserInterface(_parentStage, _screenBounds, _fixedSize);
		_floatingBarComponent.addTask("floating task");
		FloatingTaskAnimationThread r = new FloatingTaskAnimationThread(this);
		r.start();
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
		} else if (_currentView == EXPANDED_VIEW) {
			_taskViewInterface.show();
			_descriptionComponent.show();
			_floatingBarComponent.show();
			_detailComponent.show();
		}
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

	public void updateUI(int value) {
		_taskViewInterface.update(value);
		TaskEntity selectedTask = _taskViewInterface.setItemSelected(value);
		_detailComponent.buildComponent(selectedTask);
		translateComponentsY(_taskViewInterface.getTranslationY());
		updateDescriptionComponent();
	}

	public void updateDescriptionComponent() {
		if (_currentView == TASK_VIEW) {
			_taskViewInterface.rebuildDescriptionLabelsForWeek();
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
		} else if (_currentView == EXPANDED_VIEW) {
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), EXPANDED_VIEW);
		}
	}

	public void translateComponentsY(double value) {
		_taskViewInterface.updateTranslateY(value);
		_descriptionComponent.updateTranslateY(value);
	}

	public void changeView(int value) {
		int view = _currentView + value;
		switch (view) {
		case CALENDAR_VIEW: {
			_currentView = view;
			break;
		}
		case TASK_VIEW: {
			_currentView = view;
			_taskViewInterface.setView(_currentView);
			_detailComponent.setView(_currentView);
			updateUI(0);
			startThreadToAnimate();
			break;
		}
		case EXPANDED_VIEW: {
			_currentView = view;
			_taskViewInterface.setView(_currentView);
			_detailComponent.setView(_currentView);
			updateUI(0);
			startThreadToAnimate();
			break;
		}
		default:
			break;
		}
	}

	public boolean animateView() {
		boolean temp = false;
		if (_currentView == TASK_VIEW || _currentView == EXPANDED_VIEW) {
			if (_currentView == EXPANDED_VIEW) {
				temp = _taskViewInterface.isAtDetailedView(1);
				_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(),
						EXPANDED_VIEW);
			} else {
				temp = _taskViewInterface.isAtTaskView(-1);
				_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
			}
			translateComponentsY(_taskViewInterface.getTranslationY());
		}
		return temp;
	}

	/**
	 * This method will start the service to animate the current view to the selected view.
	 */
	public void startThreadToAnimate() {
		if (_expandAnimation != null) {
			if (_expandAnimation.isRunning()) {
				_expandAnimation.cancel();
			}
		}
		_expandAnimation = new TaskViewDescriptionAnimation(this);
		_expandAnimation.start();
	}

	public boolean updateFloatingBar(double percentageDone) {
		boolean isDoneAnimating = _floatingBarComponent.animateView(percentageDone);
		return isDoneAnimating;
	}
	
	public void addRandomTaskToDisplay() {
		TaskEntity task = _taskManager.getRandomFloating();
		// mod here after qy add random task
		_floatingBarComponent.addTask("new task");
	}

	/**
	 * method for debugging purposes only.
	 * 
	 */
	public void move(int value) {
		if (value > 0) {
			double t = _taskViewInterface.getMainLayoutComponent().getTranslateY() + 50;
			_taskViewInterface.updateTranslateY(t);
			_descriptionComponent.updateTranslateY(t);
		} else {
			double t = _taskViewInterface.getMainLayoutComponent().getTranslateY() - 50;
			_taskViewInterface.updateTranslateY(t);
			_descriptionComponent.updateTranslateY(t);
		}
	}

	public void addTask(TaskEntity task) {
		int insertedTo = _taskManager.add(task);
		int selected = _taskViewInterface.getSelectIndex();

		if (selected == -1) {
			selected = 0;
		} else if (insertedTo <= selected) {
			selected++;
		}

		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), selected);
		updateUI(0);

		_scorllAnimation = new ScrollTaskAnimation(selected, insertedTo, this);
		_scorllAnimation.start();
	}

	public void addBatchTask(ArrayList<TaskEntity> task) {

		int insertedTo = _taskManager.add(task);
		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), insertedTo);
		updateUI(0);
	}

	public boolean deleteTask(int idToDelete) {
		boolean isDeleted = _taskManager.delete(idToDelete);
		if (isDeleted) {
			int index = idToDelete;
			index--;
			if (index < 0) {
				index = 0;
			}
			_taskViewInterface.buildComponent(_taskManager.getWorkingList(), index);
			updateUI(0);
			return true;
		}
		return false;
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
		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), index);
		updateUI(0);
		return true;
	}

	public void jumpToIndex(String indexToJump) {
		int selected = _taskViewInterface.getSelectIndex();
		_scorllAnimation = new ScrollTaskAnimation(selected, Utils.convertBase36ToDec(indexToJump), this);
		_scorllAnimation.start();
	}

}
