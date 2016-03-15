package userInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import entity.TaskEntity;
import fileStorage.StorageInterface;
import mainLogic.TaskManager;
import mainLogic.Utils;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class UserInterfaceController {

	// view indicators
	final static int CALENDAR_VIEW = 0;
	final static int TASK_VIEW = 1;
	final static int DETAILED_VIEW = 2;

	private Stage _parentStage;
	private TaskViewUserInterface _taskViewInterface;
	private DescriptionComponent _descriptionComponent;
	private DetailComponent _detailComponent;
	private FloatingBarViewUserInterface _floatingBarComponent;
	private Rectangle2D _screenBounds;
	private boolean _fixedSize;

	// variables for animation and changing views;
	private int _currentView = TASK_VIEW;
	private Thread _threadToAnimate;

	// main logic class to interact
	private TaskManager _taskManager;

	public UserInterfaceController(Stage primaryStage) {
		_parentStage = primaryStage;
		_taskManager = new TaskManager();
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
		_descriptionComponent = new DescriptionComponent(_parentStage, _screenBounds, _fixedSize);
		_floatingBarComponent = new FloatingBarViewUserInterface(_parentStage, _screenBounds, _fixedSize);
		_detailComponent = new DetailComponent(_parentStage, _screenBounds, _fixedSize);
		_taskManager.generateFakeData();// replace when integrate with angie
		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), _taskManager.getNextTimeListId());
		updateUI(0);
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
		} else if (_currentView == DETAILED_VIEW) {
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
		TaskEntity selectedTask= _taskViewInterface.setItemSelected(value);
		_detailComponent.buildComponent(selectedTask);
		translateComponentsY(_taskViewInterface.getTranslationY());
		updateDescriptionComponent();
	}

	public void updateDescriptionComponent() {
		if (_currentView == TASK_VIEW) {
			_taskViewInterface.rebuildDescriptionLabelsForWeek();
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
		} else if (_currentView == DETAILED_VIEW) {
			_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(), DETAILED_VIEW);
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
			startThreadToAnimate();
			break;
		}
		case DETAILED_VIEW: {
			_currentView = view;
			_taskViewInterface.setView(_currentView);
			startThreadToAnimate();
			break;
		}
		default:
			break;
		}
	}

	public boolean animateView() {
		boolean temp = false;
		if (_currentView == TASK_VIEW || _currentView == DETAILED_VIEW) {
			if (_currentView == DETAILED_VIEW) {
				temp = _taskViewInterface.isAtDetailedView(1);
				_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForDay(),
						DETAILED_VIEW);
			} else {
				temp = _taskViewInterface.isAtTaskView(-1);
				_descriptionComponent.buildComponent(_taskViewInterface.rebuildDescriptionLabelsForWeek(), TASK_VIEW);
			}
			translateComponentsY(_taskViewInterface.getTranslationY());
		}
		return temp;
	}

	public void startThreadToAnimate() {
		TaskViewDescriptionAnimation animation = new TaskViewDescriptionAnimation(this);
		_threadToAnimate = new Thread(animation);
		_threadToAnimate.start();
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
		/*
		 * int date = task.getDueDate().get(Calendar.DATE); Random r = new
		 * Random(); date += r.nextInt(10); date += 5;
		 * task.getDueDate().set(Calendar.DATE, date);
		 * task.getDueDate().set(Calendar.MONTH,
		 * task.getDueDate().get(Calendar.MONTH) + 2);
		 */
		System.out.println(task.getDueDate());

		int insertedTo = _taskManager.add(task);
		int selected = _taskViewInterface.getSelectIndex();

		if (selected == -1) {
			selected = 0;
		} else if (insertedTo <= selected) {
			selected++;
		}

		_taskViewInterface.buildComponent(_taskManager.getWorkingList(), selected);
		updateUI(0);

		ScrollTaskAnimation sAnimation = new ScrollTaskAnimation(selected, insertedTo, this);
		Thread t = new Thread(sAnimation);
		t.start();
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
		ScrollTaskAnimation sAnimation = new ScrollTaskAnimation(selected, Utils.convertBase36ToDec(indexToJump), this);
		Thread t = new Thread(sAnimation);
		t.start();
	}

}
