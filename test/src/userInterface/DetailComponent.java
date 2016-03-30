package userInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DetailComponent implements ViewInterface {

	static final int COMPONENT_WIDTH = 300;
	static final int COMPONENT_LEFT_MARGIN = 2;

	private static final int CALENDAR_VEW = 0;
	private static final int TASK_VIEW = 1;
	private static final int EXPANDED_VIEW = 2;
	private static final int ASSOCIATE_VIEW = 3;
	private static final int TOTAL_VIEWS = 3;

	private static final int SPACING_SIZE = 10;
	private static final int ITEM_MARGIN = 4;

	private static final String LABEL_MESSAGE_NO_TASK = "This task has no associations.";
	private static final int LABEL_PROJECTHEAD_HEIGHT = 30;
	private static final int LABEL_TASK_HEIGHT = 25;
	private static final int LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS = 20;
	private static final int BOTTOM_MARGIN_INDIVIDUAL_ITEMS = 10;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private VBox[] _mainVbox;
	private Scene[] _scenes;
	private int _currentSelectView;
	private int _selectedIndex;
	private int _individualItemWidth;

	private boolean _haveAssociation;
	private TaskEntity _targetedTask;

	public DetailComponent(Stage parentStage, Rectangle2D screenBounds, boolean fixedSize) {
		_currentSelectView = TASK_VIEW;
		initializeVaribles(screenBounds, fixedSize);
		initializeScenes();
		initializeStage(parentStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
	}

	private void initializeScenes() {
		_scenes = new Scene[TOTAL_VIEWS];
		_mainVbox = new VBox[TOTAL_VIEWS];
		for (int i = 0; i < TOTAL_VIEWS; i++) {
			_mainVbox[i] = new VBox();
			_mainVbox[i].setMinSize(_stageWidth, _stageHeight);
			_mainVbox[i].getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
			_mainVbox[i].setId("cssDetailComponentRoot");
			_scenes[i] = new Scene(_mainVbox[i], _stageWidth, _stageHeight);
			_scenes[i].setFill(Color.TRANSPARENT);
		}
	}

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		if (fixedSize) {
			_stageWidth = COMPONENT_WIDTH;
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = (int) (screenBounds.getWidth() - COMPONENT_WIDTH);
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		} else {
			_stageWidth = COMPONENT_WIDTH;
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = (int) ((screenBounds.getWidth()
					- (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2
					+ (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE) - COMPONENT_WIDTH);
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		}
		_individualItemWidth = _stageWidth - ITEM_MARGIN;
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth, int windowHeight) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);
		_stage.setScene(_scenes[_currentSelectView]);
	}

	public void buildComponent(TaskEntity task) {
		if (_currentSelectView == TASK_VIEW) {
			buildUIForTaskView(task);
		} else if (_currentSelectView == EXPANDED_VIEW || _currentSelectView == ASSOCIATE_VIEW) {
			_selectedIndex = 0;
			buildUIForExpandedView(task);
		}
	}

	public void setView(int view) {
		_currentSelectView = view;
		if (_currentSelectView == ASSOCIATE_VIEW) {
			_stage.setScene(_scenes[EXPANDED_VIEW]);
		} else {
			_stage.setScene(_scenes[_currentSelectView]);
		}
	}

	public void buildUIForTaskView(TaskEntity task) {
		_mainVbox[TASK_VIEW].getChildren().clear();
		if (task != null) {
			VBox childToAdd = buildTaskForTaskView(task);
			_mainVbox[TASK_VIEW].getChildren().add(childToAdd);
		}
	}

	public void buildUIForExpandedView(TaskEntity task) {
		_targetedTask = task;
		_mainVbox[EXPANDED_VIEW].getChildren().clear();
		if (task != null) {
			VBox childToAdd = buildAssociationList(task);
			_mainVbox[EXPANDED_VIEW].getChildren().add(childToAdd);
		}
	}

	public VBox buildAssociationList(TaskEntity task) {
		VBox box = new VBox();
		box.setSpacing(SPACING_SIZE);
		box.setMinWidth(_stageWidth);
		box.setMaxHeight(0);
		box.setAlignment(Pos.CENTER);
		box.setId("cssDetailAssociationListBox");

		ArrayList<TaskEntity> association = task.getDisplayAssociations();

		if (association != null) {
			if (association.size() == 0) {
				box.getChildren().add(buildEmptyLabel());
				_haveAssociation = false;
			} else {
				VBox projectHeadToAdd = buildProjectHead(association.get(0));
				box.getChildren().add(projectHeadToAdd);
				for (int i = 1; i < association.size(); i++) {
					VBox taskToAdd = buildTaskCollapse(association.get(i));
					box.getChildren().add(taskToAdd);
				}
				_haveAssociation = true;
				_selectedIndex = task.getAssociationPosition();
			}
		}
		return box;
	}

	/**
	 * This method is used when the current view is in the ASSOCIATE_VIEW. it
	 * sets the selected item to the index.
	 * 
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		if (isValidIndex(index)) {
			VBox parent = (VBox) _mainVbox[EXPANDED_VIEW].getChildren().get(0);
			if (_selectedIndex != -1) {
				VBox prev = (VBox) parent.getChildren().get(_selectedIndex);
				prev.setId("cssExpandedViewVBox");
				if (_selectedIndex != 0) {
					removeDescription(prev);
				}
			}

			VBox curr = (VBox) parent.getChildren().get(index);
			curr.setId("cssExpandedViewVBoxSelected");
			if (index != 0) {
				addDescription(curr, index);
			}
			_selectedIndex = index;

			if (parent.getHeight() > _stageHeight) {
				double sizeOnTop = 0;
				for (int i = 0; i <= index; i++) {
					VBox tempVBox = (VBox) parent.getChildren().get(i);
					sizeOnTop += tempVBox.getHeight() + SPACING_SIZE;
				}

				double posY = 0;
				if (sizeOnTop > _stageHeight) {
					posY = sizeOnTop - _stageHeight;
				}
				parent.setTranslateY(-posY);
			}
		}
	}

	public boolean isValidIndex(int index) {
		if (!_haveAssociation) {
			return false;
		}
		if (index < 0) {
			return false;
		}
		if (_mainVbox[EXPANDED_VIEW].getChildren().size() < 0) {
			return false;
		}
		VBox child = (VBox) _mainVbox[EXPANDED_VIEW].getChildren().get(0);
		if (child.getChildren().size() > index) {
			return true;
		}
		return false;
	}

	public Label buildEmptyLabel() {
		Label label = new Label(LABEL_MESSAGE_NO_TASK);
		label.setMinWidth(_stageWidth);
		label.setId("cssDetailComponentEmptyTitle");
		label.setAlignment(Pos.CENTER);
		return label;
	}

	private void removeDescription(VBox curr) {
		int indexToRemove = curr.getChildren().size() - 1;
		curr.getChildren().remove(indexToRemove);
	}

	private void addDescription(VBox curr, int index) {
		if (_targetedTask.getDisplayAssociations().size() < index || index < 0) {
			return;
		}
		TaskEntity task = _targetedTask.getDisplayAssociations().get(index);
		Text description = new Text(task.getDescription());
		description.setWrappingWidth(_individualItemWidth - LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS * 2);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		VBox.setMargin(description, new Insets(0, LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS, BOTTOM_MARGIN_INDIVIDUAL_ITEMS,
				LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS));
		curr.getChildren().add(description);
	}

	public VBox buildTaskForTaskView(TaskEntity task) {
		VBox itemMain = new VBox();
		itemMain.setSpacing(10);
		itemMain.setMinWidth(_stageWidth);
		itemMain.setId("cssExpandedViewVBox");

		Label titleLabel = new Label(task.getName());
		titleLabel.setId("cssDetailComponentEmptyTitle");
		titleLabel.setMinWidth(_stageWidth);
		titleLabel.setMinHeight(LABEL_PROJECTHEAD_HEIGHT);
		titleLabel.setAlignment(Pos.CENTER);
		itemMain.getChildren().add(titleLabel);

		HBox dateBox = new HBox();
		Label dateTitleLabel = new Label("Due date : ");
		dateBox.getChildren().add(dateTitleLabel);
		Label dateLabel = new Label(getDate(task.getDueDate()));
		dateBox.getChildren().add(dateLabel);
		VBox.setMargin(dateBox, new Insets(0, 20, 0, 20));
		itemMain.getChildren().add(dateBox);

		Text description = new Text(task.getDescription());
		description.setWrappingWidth(_individualItemWidth - LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS * 2);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		itemMain.getChildren().add(description);
		VBox.setMargin(description, new Insets(0, LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS, BOTTOM_MARGIN_INDIVIDUAL_ITEMS,
				LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS));
		
		Text hashtag = new Text(task.getHashtags());
		hashtag.setWrappingWidth(_individualItemWidth - LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS * 2);
		hashtag.setTextAlignment(TextAlignment.JUSTIFY);
		itemMain.getChildren().add(hashtag);
		VBox.setMargin(hashtag, new Insets(0, LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS, BOTTOM_MARGIN_INDIVIDUAL_ITEMS,
				LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS));
		
		return itemMain;
	}

	public VBox buildProjectHead(TaskEntity task) {
		VBox itemMain = new VBox();
		itemMain.setSpacing(10);
		itemMain.setMinWidth(_stageWidth);
		itemMain.setId("cssExpandedViewVBox");

		Label titleLabel = new Label(task.getName());
		titleLabel.setId("cssDetailComponentEmptyTitle");
		titleLabel.setMinWidth(_stageWidth);
		titleLabel.setMinHeight(LABEL_PROJECTHEAD_HEIGHT);
		titleLabel.setAlignment(Pos.CENTER);
		itemMain.getChildren().add(titleLabel);
		itemMain.setMaxHeight(LABEL_PROJECTHEAD_HEIGHT);

		HBox dateBox = new HBox();
		Label dateTitleLabel = new Label("Due date : ");
		dateBox.getChildren().add(dateTitleLabel);
		Label dateLabel = new Label(getDate(task.getDueDate()));
		dateBox.getChildren().add(dateLabel);
		VBox.setMargin(dateBox, new Insets(0, 20, 0, 20));
		itemMain.getChildren().add(dateBox);
		itemMain.setMaxHeight(itemMain.getMaxHeight() + LABEL_TASK_HEIGHT);

		Text description = new Text(task.getDescription());
		description.setWrappingWidth(_individualItemWidth - LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS * 2);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		itemMain.getChildren().add(description);
		itemMain.setMaxHeight(itemMain.getMaxHeight() + description.getBoundsInLocal().getHeight());
		VBox.setMargin(description, new Insets(0, LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS, BOTTOM_MARGIN_INDIVIDUAL_ITEMS,
				LEFT_RIGHT_MARGIN_INDIVIDUAL_ITEMS));

		return itemMain;
	}

	public VBox buildTaskCollapse(TaskEntity task) {
		VBox itemMain = new VBox();
		itemMain.setMinWidth(_individualItemWidth);
		VBox.setMargin(itemMain, new Insets(0, 2, 0, 2));
		itemMain.setId("cssExpandedViewVBox");

		HBox dateBox = new HBox();
		dateBox.setId("cssDetailComponentTask");
		dateBox.setMinWidth(_individualItemWidth);
		dateBox.setMinHeight(LABEL_TASK_HEIGHT);
		dateBox.setAlignment(Pos.CENTER_LEFT);

		Label dateLabel = new Label(getDate(task.getDueDate()));
		dateBox.getChildren().add(dateLabel);
		dateBox.setMinHeight(LABEL_TASK_HEIGHT);

		Label titleLabel = new Label(task.getName());
		titleLabel.setMinHeight(LABEL_TASK_HEIGHT);
		HBox.setMargin(titleLabel, new Insets(0, 0, 0, 5));
		dateBox.getChildren().add(titleLabel);

		VBox.setMargin(dateBox, new Insets(0, 20, 0, 20));
		itemMain.getChildren().add(dateBox);

		Text description = new Text();
		itemMain.getChildren().add(description);
		return itemMain;
	}

	public VBox buildTask(TaskEntity task) {
		VBox itemMain = new VBox();
		itemMain.setSpacing(10);
		itemMain.setMinWidth(_individualItemWidth);
		VBox.setMargin(itemMain, new Insets(0, 2, 0, 2));
		itemMain.setId("cssExpandedViewVBox");

		Label titleLabel = new Label(task.getName());
		titleLabel.setId("cssDetailComponentEmptyTitle");
		titleLabel.setMinWidth(_individualItemWidth);
		titleLabel.setAlignment(Pos.CENTER);
		itemMain.getChildren().add(titleLabel);

		HBox dateBox = new HBox();
		Label dateTitleLabel = new Label("Due date : ");
		dateBox.getChildren().add(dateTitleLabel);
		Label dateLabel = new Label(getDate(task.getDueDate()));
		dateBox.getChildren().add(dateLabel);
		VBox.setMargin(dateBox, new Insets(0, 20, 0, 20));
		itemMain.getChildren().add(dateBox);

		Text description = new Text(task.getDescription());
		description.setWrappingWidth(_individualItemWidth);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		itemMain.getChildren().add(description);
		VBox.setMargin(description, new Insets(0, 20, 20, 20));

		return itemMain;
	}

	/**
	 * Append child into parent space and increase parent MaxSize.
	 * 
	 * @param child
	 * @param parent
	 */
	public void addChildToParent(VBox child, VBox parent) {
		parent.getChildren().add(child);
		parent.setPrefHeight(parent.getMaxHeight() + child.getMaxHeight());
		parent.setMinHeight(parent.getMaxHeight() + child.getMaxHeight());
		parent.setMaxHeight(parent.getMaxHeight() + child.getMaxHeight());
	}

	public String getDate(Calendar cal) {
		String date = "";
		date += cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR)
				+ " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
		return date;

	}

	public void update(int value) {
		int index = value + _selectedIndex;
		setSelectedIndex(index);
	}

	public void updateTranslateY(double posY) {
		// TODO Auto-generated method stub

	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public void destoryStage() {
		_stage.close();
	}

}
