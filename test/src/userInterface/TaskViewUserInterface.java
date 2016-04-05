//@@author A0125514N
package userInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dateParser.ReverseParser;
import entity.DescriptionLabel;
import entity.TaskEntity;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class TaskViewUserInterface implements ViewInterface {

	private static TaskViewUserInterface _myInstance;

	private static final int GAP_SIZE = 10;
	private static final int THRESHOLD = 50;

	static final int TASK_VIEW_LABEL_HEIGHT = 50;
	static final int TASK_VIEW_ITEM_HEIGHT = 30;
	static final int DETAILED_VIEW_ITEM_HEIGHT = 40;
	static final int SELECTOR_POSITION_Y = TASK_VIEW_LABEL_HEIGHT + TASK_VIEW_ITEM_HEIGHT * 2;

	// font
	static final int FONT_SIZE_LABEL_DATE = 24;
	static final int FONT_SIZE_TASK = 12;
	static final int FONT_SIZE_INDEX = 8;
	private static final Font FONT_LABEL = new Font(PrimaryUserInterface.FONT_TITLE_LABLES, FONT_SIZE_LABEL_DATE);
	private static final Font FONT_TASK = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_TASK);

	private static final String CSS_LABEL = "cssLabels";

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	// variables to control items in taskView.
	private int _startIndex = -1;
	private int _endIndex = -1;
	private int _selectedIndex = -1;
	private int _individualItemWidth = -1;
	private double transLationY = 0;

	private int _view = UserInterfaceController.TASK_VIEW;

	private VBox _mainVbox; // main parent for items.
	private int _itemIndexCounter = 0;
	private String _styleSheet;

	private ReverseParser _reverseParser = new ReverseParser();

	// container to store current gridPanes builded for easy reference.
	private ArrayList<GridPane> _gridPanes = new ArrayList<GridPane>();
	private ArrayList<TaskEntity> workingList;

	public static TaskViewUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize,
			String styleSheet, EventHandler<MouseEvent> mouseEvent) {
		if (_myInstance == null) {
			if (primaryStage == null || screenBounds == null) {
				return null;
			}
			_myInstance = new TaskViewUserInterface(primaryStage, screenBounds, fixedSize, styleSheet, mouseEvent);
			return _myInstance;
		}
		return null;
	}

	private TaskViewUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize, String styleSheet,
			EventHandler<MouseEvent> mouseEvent) {
		_styleSheet = styleSheet;
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight, mouseEvent);
	}

	/**
	 * Initialize view dimensions and position base.
	 */
	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		if (fixedSize) {
			_stageWidth = (int) screenBounds.getWidth() - DescriptionComponent.CONPONENT_WIDTH
					- DescriptionComponent.CONPONENT_RIGHT_MARGIN - DetailComponent.COMPONENT_WIDTH
					- DetailComponent.COMPONENT_LEFT_MARGIN;
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN;
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		} else {
			_stageWidth = (int) (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)
					- DescriptionComponent.CONPONENT_WIDTH - DescriptionComponent.CONPONENT_RIGHT_MARGIN
					- DetailComponent.COMPONENT_WIDTH - DetailComponent.COMPONENT_LEFT_MARGIN;
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = (int) (screenBounds.getWidth()
					- (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2
					+ DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN;
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		}
		_individualItemWidth = _stageWidth;
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int stageWidth, int stageHeight,
			EventHandler<MouseEvent> mouseEvent) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		StackPane mainPanel = new StackPane();
		mainPanel.setPrefSize(stageWidth, stageHeight);
		mainPanel.setId("cssTaskViewMainBackground");
		mainPanel.setAlignment(Pos.TOP_LEFT);

		_mainVbox = new VBox();
		mainPanel.getChildren().add(_mainVbox);

		Scene scene = new Scene(mainPanel, stageWidth, stageHeight);
		scene.getStylesheets().add(_styleSheet);
		scene.setFill(Color.TRANSPARENT);
		_stage.setScene(scene);

		scene.setOnMousePressed(mouseEvent);

	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public VBox getMainLayoutComponent() {
		return _mainVbox;
	}

	public int get_stageWidth() {
		return _stageWidth;
	}

	public int get_stageHeight() {
		return _stageHeight;
	}

	/**
	 * Builds the items inside taskView base on the workingList and the
	 * workingIndex.
	 * 
	 * @param workingList
	 *            - this is the list of all task that can be shown in the
	 *            taskView.
	 * @param workingIndex
	 *            - this is the selected index of the task.
	 */
	public void buildComponent(ArrayList<TaskEntity> workingList, int workingIndex) {

		_mainVbox.getChildren().clear();
		_gridPanes = new ArrayList<GridPane>();

		this.workingList = workingList;
		if (workingList.size() > 0) {

			assert (workingIndex < workingList.size());
			assert (workingIndex > -1);

			setIndexs(workingIndex);

			_itemIndexCounter = _startIndex;
			int countItemSameWeek = 0;
			ArrayList<TaskEntity> itemsInSameWeek = new ArrayList<TaskEntity>();
			for (int i = _startIndex; i < _endIndex + 1; i++) {
				if (countItemSameWeek == 0) {
					countItemSameWeek++;
					itemsInSameWeek.add(workingList.get(i));
				} else {
					if (isSameWeek(workingList.get(i), itemsInSameWeek.get(0))) {
						itemsInSameWeek.add(workingList.get(i));
					} else {
						_mainVbox.getChildren().add(creatWeek(itemsInSameWeek));
						countItemSameWeek = 1;
						itemsInSameWeek = new ArrayList<TaskEntity>();
						itemsInSameWeek.add(workingList.get(i));
					}
				}
			}
			if (itemsInSameWeek.size() > 0) {
				_mainVbox.getChildren().add(creatWeek(itemsInSameWeek));
			}
			_selectedIndex = workingIndex;
		}
	}

	/**
	 * Sets _startIndex and _endIndex values base on the workingIndex,
	 * workingList and THRESHOLD.
	 * 
	 * @param workingIndex
	 */
	private void setIndexs(int workingIndex) {
		_startIndex = 0;
		_endIndex = workingList.size() - 1;
		if (workingIndex - THRESHOLD > _startIndex) {
			_startIndex = workingIndex - THRESHOLD;
		}
		if (workingIndex + THRESHOLD < _endIndex) {
			_endIndex = workingIndex + THRESHOLD;
		}
	}

	/**
	 * Add child into the parent, and increase parent height base on child
	 * height.
	 * 
	 * @param parent
	 * @param child
	 */
	private void childToParent(Pane parent, Pane child) {
		double pHeight = parent.getMinHeight();
		parent.setMinHeight(pHeight + child.getMinHeight());
		parent.getChildren().add(child);
	}

	/**
	 * Creates VBox for the items, items must belong to same Week.
	 * 
	 * @param items
	 * @return VBox
	 */
	private VBox creatWeek(ArrayList<TaskEntity> items) {
		VBox weekParent = createWeekParent();
		int countItemSameDay = 0;
		ArrayList<TaskEntity> itemsSameDay = new ArrayList<TaskEntity>();
		for (int i = 0; i < items.size(); i++) {
			if (countItemSameDay == 0) {
				countItemSameDay++;
				itemsSameDay.add(items.get(i));
			} else {
				if (isSameDay(items.get(i), itemsSameDay.get(0))) {
					itemsSameDay.add(items.get(i));
				} else {
					childToParent(weekParent, createDayForWeek(itemsSameDay));
					countItemSameDay = 1;
					itemsSameDay = new ArrayList<TaskEntity>();
					itemsSameDay.add(items.get(i));
				}
			}
		}
		if (itemsSameDay.size() > 0) {
			childToParent(weekParent, createDayForWeek(itemsSameDay));
		}
		return weekParent;
	}

	/**
	 * Creates VBox for the items, items must belong to same day.
	 * 
	 * @param items
	 * @return VBox
	 */
	private VBox createDayForWeek(ArrayList<TaskEntity> items) {
		VBox dayParent = createDayParent(items.get(0));
		for (int i = 0; i < items.size(); i++) {
			childToParent(dayParent, buildIndividualTask(items.get(i), _itemIndexCounter++));
		}
		return dayParent;
	}

	/**
	 * Creates the warper for week.
	 * 
	 * @return VBox.
	 */
	private VBox createWeekParent() {
		VBox vbox = new VBox();
		vbox.setId("cssTaskViewWeekUnSelected");
		vbox.setMinWidth(_stageWidth);
		vbox.setMinHeight(0);
		return vbox;
	}

	/**
	 * Creates the warper for day with the label.
	 * 
	 * @return VBox.
	 */
	private VBox createDayParent(TaskEntity taskEntity) {
		VBox vbox = new VBox();
		vbox.setMinHeight(TASK_VIEW_LABEL_HEIGHT + 2);
		HBox hbox = new HBox();
		hbox.setMinHeight(TASK_VIEW_LABEL_HEIGHT); // setMax
		hbox.setId("cssTaskViewDayLabel");

		Label dateNLPLabel = new Label();
		dateNLPLabel.getStyleClass().add(CSS_LABEL);
		dateNLPLabel.setMinHeight(TaskViewUserInterface.TASK_VIEW_LABEL_HEIGHT);
		dateNLPLabel.setFont(FONT_LABEL);
		dateNLPLabel.setAlignment(Pos.BOTTOM_CENTER);

		String labelText = getStringOfDate(taskEntity.getDueDate());
		if (labelText != null) {
			dateNLPLabel.setText(labelText);
			hbox.getChildren().add(dateNLPLabel);
		} else {
			SimpleDateFormat daySdf = new SimpleDateFormat("d");
			SimpleDateFormat sdf = new SimpleDateFormat("MMMMM yyyy");
			dateNLPLabel.setText(daySdf.format(taskEntity.getDueDate().getTime()) + " "
					+ sdf.format(taskEntity.getDueDate().getTime()));
			hbox.getChildren().add(dateNLPLabel);
		}

		vbox.getChildren().add(hbox);
		VBox.setMargin(hbox, new Insets(0, 20, 0, 20));
		return vbox;
	}

	private String getStringOfDate(Calendar c) {
		return _reverseParser.reParse((Calendar) c.clone());
	}

	/**
	 * Creates UI for the taskEntity.
	 * 
	 * @return VBox.
	 */
	private HBox buildIndividualTask(TaskEntity taskEntity, int index) {
		HBox hbox = new HBox();
		GridPane gridPane = createGridPaneForTask(taskEntity, index);
		_gridPanes.add(gridPane);
		hbox.getChildren().add(gridPane);
		hbox.setMinHeight(gridPane.getMinHeight());
		hbox.setMaxHeight(gridPane.getMinHeight());
		return hbox;
	}

	private GridPane createGridPaneForTask(TaskEntity taskEntity, int index) {
		GridPane grid = new GridPane();
		grid.setId("cssTaskViewUnSelectedTask");
		grid.setHgap(GAP_SIZE);
		grid.setMinWidth(_individualItemWidth);

		Label indexLabel = new Label("ID" + Integer.toString(index));
		indexLabel.getStyleClass().add(CSS_LABEL);
		indexLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		indexLabel.setFont(Font.font(PrimaryUserInterface.FONT_DEFAULT, FontWeight.BOLD, FONT_SIZE_TASK));
		indexLabel.setMinWidth(40);
		indexLabel.setAlignment(Pos.CENTER_RIGHT);
		grid.add(indexLabel, 0, 0);

		HBox topBox = new HBox();
		Label timeLabel = new Label();
		timeLabel.getStyleClass().add(CSS_LABEL);
		timeLabel.setText(taskEntity.getTime());
		timeLabel.setMinWidth(80);
		timeLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		timeLabel.setFont(FONT_TASK);
		timeLabel.setAlignment(Pos.CENTER_RIGHT);
		HBox.setMargin(timeLabel, new Insets(0, 5, 0, 0));
		topBox.getChildren().add(timeLabel);

		if (taskEntity.getAssociationState() == TaskEntity.PROJECT_HEAD) {
			topBox.getChildren().add(createStar(TASK_VIEW_ITEM_HEIGHT));
		}
		Label titleLabel = new Label(taskEntity.getName());
		titleLabel.getStyleClass().add(CSS_LABEL);
		titleLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		titleLabel.setFont(FONT_TASK);
		HBox.setMargin(titleLabel, new Insets(0, 10, 0, 0));
		topBox.getChildren().add(titleLabel);
		grid.add(topBox, 1, 0);

		HBox midBox = new HBox();
		midBox.setMinHeight(0);

		String text = taskEntity.getDescription();
		Label descriptionLabel2 = new Label(text);
		descriptionLabel2.getStyleClass().add(CSS_LABEL);
		descriptionLabel2.setMinHeight(0);
		descriptionLabel2.setMaxWidth(_individualItemWidth - 80);
		descriptionLabel2.setWrapText(true);
		descriptionLabel2.setFont(FONT_TASK);

		Text t = new Text(text);
		t.setWrappingWidth(_individualItemWidth - 80);

		midBox.getChildren().add(descriptionLabel2);
		grid.add(midBox, 1, 1);

		Label hashTagLabel = new Label(taskEntity.getHashtags());
		hashTagLabel.getStyleClass().add(CSS_LABEL);
		hashTagLabel.setMinHeight(0);
		hashTagLabel.setMaxWidth(_individualItemWidth - 80);
		hashTagLabel.setFont(FONT_TASK);
		hashTagLabel.setWrapText(true);
		grid.add(hashTagLabel, 1, 2);

		Text t2 = new Text(taskEntity.getHashtags());
		t2.setWrappingWidth(_individualItemWidth - 80);

		if (_view == UserInterfaceController.TASK_VIEW) {
			grid.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		} else {
			grid.setMinHeight(
					DETAILED_VIEW_ITEM_HEIGHT + t.getBoundsInLocal().getHeight() + t2.getBoundsInLocal().getHeight());
		}
		grid.setMaxHeight(
				DETAILED_VIEW_ITEM_HEIGHT + t.getBoundsInLocal().getHeight() + t2.getBoundsInLocal().getHeight());

		return grid;
	}

	private StackPane createStar(double size) {
		StackPane stackPane = new StackPane();
		stackPane.setMinHeight(size);
		stackPane.setMaxHeight(size);
		stackPane.setMinWidth(size);
		stackPane.setMaxWidth(size);
		stackPane.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(buildStar(0.5 * (size / 2)));
		return stackPane;
	}

	public Polygon buildStar(double size) {
		int arms = 5;
		double rOuter = 1 * size;
		double rInner = 0.5 * size;
		double angle = Math.PI / arms;
		int c = 0;
		Double[] starCoor = new Double[20];
		for (int i = 0; i < arms * 2; i++) {
			double r = (i & 1) == 0 ? rOuter : rInner;
			double x = Math.cos(i * angle) * r;
			double y = Math.sin(i * angle) * r;
			starCoor[c++] = x;
			starCoor[c++] = y;
		}
		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(starCoor);
		polygon.setFill(Color.WHITE);
		polygon.setStroke(Color.BLACK);
		return polygon;
	}

	/**
	 * Check if the index is between the _startIndex and _endIndex, inclusive.
	 * 
	 * @param index.
	 * @return boolean.
	 */
	private boolean isBetweenStartEnd(int index) {
		if (index >= _startIndex && index <= _endIndex) {
			return true;
		}
		return false;
	}

	/**
	 * This method updates the selector index by the amount of value. Items are
	 * added and removed to maintain THRESHOLD.
	 * 
	 */
	public void update(int value) {
		for (int i = 0; i < Math.abs(value); i++) {
			if (value > 0)// ctrl down
			{
				if (_endIndex + 1 < workingList.size()) {
					if (_selectedIndex - _startIndex >= THRESHOLD) {
						removeFirstTask();
					}
					addLastItem();
				} else {
					break;
				}
			} else if (value < 0) {
				if (_startIndex > 0) {
					if (_endIndex - _selectedIndex >= THRESHOLD) {
						removeLastTask();
					}
					addFirstItem();
				} else {
					break;
				}
			}
		}
	}

	private void removeFirstTask() {
		try {
			GridPane gp = _gridPanes.get(0);
			VBox gpDayParent = (VBox) gp.getParent().getParent();
			VBox gpWeekParent = (VBox) gpDayParent.getParent();
			if (gpDayParent.getChildren().size() == 2) {
				gpWeekParent.getChildren().remove(gpDayParent);
				gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - gpDayParent.getMinHeight());
				if (gpWeekParent.getChildren().size() == 0) {
					_mainVbox.getChildren().remove(gpWeekParent);
				}
				transLationY = transLationY - gpDayParent.getMinHeight();

			} else {
				HBox itemToRemove = (HBox) gpDayParent.getChildren().remove(1);
				gpDayParent.setMinHeight(gpDayParent.getMinHeight() - itemToRemove.getMinHeight());
				gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - itemToRemove.getMinHeight());
				transLationY = transLationY - itemToRemove.getMinHeight();
			}
			_gridPanes.remove(0);
			_startIndex += 1;
		} catch (IndexOutOfBoundsException e) {

		}
	}

	private void removeLastTask() {
		try {
			GridPane gp = _gridPanes.get(_gridPanes.size() - 1);
			VBox gpDayParent = (VBox) gp.getParent().getParent();
			VBox gpWeekParent = (VBox) gpDayParent.getParent();
			if (gpDayParent.getChildren().size() == 2) {
				gpWeekParent.getChildren().remove(gpDayParent);
				gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - gpDayParent.getMinHeight());
				if (gpWeekParent.getChildren().size() == 0) {
					_mainVbox.getChildren().remove(gpWeekParent);
				}
			} else {
				HBox itemToRemove = (HBox) gpDayParent.getChildren().remove(gpDayParent.getChildren().size() - 1);
				gpDayParent.setMinHeight(gpDayParent.getMinHeight() - itemToRemove.getMinHeight());
				gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - itemToRemove.getMinHeight());
			}
			_gridPanes.remove(_gridPanes.size() - 1);
			_endIndex -= 1;
		} catch (IndexOutOfBoundsException e) {

		}
	}

	private void addLastItem() {
		GridPane gp = _gridPanes.get(_gridPanes.size() - 1);
		VBox gpDayParent = (VBox) gp.getParent().getParent();
		VBox gpWeekParent = (VBox) gpDayParent.getParent();
		if (isSameDay(workingList.get(_endIndex), workingList.get(_endIndex + 1))) {
			HBox itemToAdd = buildIndividualTask(workingList.get(_endIndex + 1), (_endIndex + 1));
			gpDayParent.getChildren().add(itemToAdd);
			gpDayParent.setMinHeight(gpDayParent.getMinHeight() + itemToAdd.getMinHeight());
			gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() + itemToAdd.getMinHeight());
		} else {
			VBox weekParent = null;
			if (isSameWeek(workingList.get(_endIndex), workingList.get(_endIndex + 1))) {
				weekParent = gpWeekParent;
			} else {
				weekParent = createWeekParent();
				_mainVbox.getChildren().add(weekParent);
			}
			VBox vbox = createDayParent(workingList.get(_endIndex + 1));
			HBox itemToAdd = buildIndividualTask(workingList.get(_endIndex + 1), (_endIndex + 1));
			vbox.getChildren().add(itemToAdd);
			vbox.setMinHeight(vbox.getMinHeight() + itemToAdd.getMinHeight());
			weekParent.setMinHeight(weekParent.getMinHeight() + vbox.getMinHeight());
			weekParent.getChildren().add(vbox);
		}
		_endIndex = _endIndex + 1;
	}

	private void addFirstItem() {
		GridPane gp = _gridPanes.get(0);
		VBox gpDayParent = (VBox) gp.getParent().getParent();
		VBox gpWeekParent = (VBox) gpDayParent.getParent();
		if (isSameDay(workingList.get(_startIndex), workingList.get(_startIndex - 1))) {
			HBox itemToAdd = buildIndividualTask(workingList.get(_startIndex - 1), (_startIndex - 1));
			gpDayParent.getChildren().add(1, itemToAdd);
			gpDayParent.setMinHeight(gpDayParent.getMinHeight() + itemToAdd.getMinHeight());
			gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() + itemToAdd.getMinHeight());
			transLationY += itemToAdd.getMinHeight();
		} else {
			VBox weekBox = null;
			if (isSameWeek(workingList.get(_startIndex), workingList.get(_startIndex - 1))) {
				weekBox = (VBox) gpDayParent.getParent();
			} else {
				weekBox = createWeekParent();
				_mainVbox.getChildren().add(0, weekBox);
			}
			VBox vbox = createDayParent(workingList.get(_startIndex - 1));
			HBox itemToAdd = buildIndividualTask(workingList.get(_startIndex - 1), (_startIndex - 1));
			vbox.getChildren().add(itemToAdd);
			vbox.setMinHeight(vbox.getMinHeight() + itemToAdd.getMinHeight());
			weekBox.getChildren().add(0, vbox);
			weekBox.setMinHeight(weekBox.getMinHeight() + vbox.getMinHeight());
			transLationY += vbox.getMinHeight();
		}
		_gridPanes.add(0, _gridPanes.remove(_gridPanes.size() - 1));
		_startIndex = _startIndex - 1;
	}

	public TaskEntity setItemSelected(int value) {
		int index = value + _selectedIndex;
		if (_gridPanes.size() > 0) {
			if (index > -1 && index < workingList.size()) {
				if (isBetweenStartEnd(index)) {
					if (_selectedIndex > -1) {
						GridPane gpPrevious = _gridPanes.get(_selectedIndex - _startIndex);
						setItemAsDeSelected(gpPrevious);
					}
					GridPane selectedGp = _gridPanes.get(index - _startIndex);
					setItemAsSelected(selectedGp);
					_selectedIndex = index;
					updateTranslationY();
				}
			} else {
				if (value > 0) {
					GridPane gpPrevious = _gridPanes.get(_selectedIndex - _startIndex);
					setItemAsDeSelected(gpPrevious);
					GridPane selectedGp = _gridPanes.get(_gridPanes.size() - 1);
					setItemAsSelected(selectedGp);
					_selectedIndex = _endIndex;
				} else if (value < 0) {
					GridPane gpPrevious = _gridPanes.get(_selectedIndex - _startIndex);
					setItemAsDeSelected(gpPrevious);
					GridPane selectedGp = _gridPanes.get(0);
					setItemAsSelected(selectedGp);
					_selectedIndex = 0;
				}
				updateTranslationY();
			}
			return workingList.get(_selectedIndex);
		}
		return null;
	}

	public void setItemAsSelected(GridPane selectedGp) {
		selectedGp.setId("cssTaskViewSelectedTask");
		VBox selectedParent = (VBox) selectedGp.getParent().getParent();
		selectedParent.setId("cssTaskViewDayBoxSelected");
		VBox weekParentSelected = (VBox) selectedParent.getParent();
		weekParentSelected.setId("cssTaskViewWeekSelected");
	}

	public void setItemAsDeSelected(GridPane gpPrevious) {
		gpPrevious.setId("cssTaskViewUnSelectedTask");
		VBox previousParent = (VBox) gpPrevious.getParent().getParent();
		previousParent.setId("cssTaskViewDayBoxUnSelected");
		VBox weekParent = (VBox) previousParent.getParent();
		weekParent.setId("cssTaskViewWeekUnSelected");
	}

	// set transLationY to be desired tranlationY, which is current selected
	// item position + threshold
	public void updateTranslationY() {
		if ((_selectedIndex - _startIndex) >= _gridPanes.size()) {
			return;
		}
		GridPane gp = _gridPanes.get(_selectedIndex - _startIndex);
		HBox gpParent = (HBox) gp.getParent();
		VBox dayParent = (VBox) gpParent.getParent();
		VBox weekParent = (VBox) dayParent.getParent();
		double heightAboveItem = 0;
		for (int i = 0; i < _mainVbox.getChildren().indexOf(weekParent); i++) {
			VBox weekBox = (VBox) _mainVbox.getChildren().get(i);
			heightAboveItem += weekBox.getMinHeight();
		}
		for (int i = 0; i < weekParent.getChildren().indexOf(dayParent); i++) {
			VBox dayBox = (VBox) weekParent.getChildren().get(i);
			heightAboveItem += dayBox.getMinHeight();
		}
		for (int i = 0; i < dayParent.getChildren().indexOf(gpParent); i++) {
			if (dayParent.getChildren().get(i) instanceof Label) {
				heightAboveItem += TASK_VIEW_LABEL_HEIGHT;
			} else {
				heightAboveItem += ((HBox) dayParent.getChildren().get(i)).getMinHeight();
			}
		}
		transLationY = heightAboveItem;
	}

	public double getTranslationY() {
		double totalHeight = 0;
		for (int i = 0; i < _mainVbox.getChildren().size(); i++) {
			VBox weekBox = (VBox) _mainVbox.getChildren().get(i);
			totalHeight += weekBox.getMinHeight();
		}
		if (totalHeight > _stageHeight) {
			totalHeight -= _stageHeight;
			double itemPosY = -transLationY + SELECTOR_POSITION_Y;
			if (itemPosY > 0) {
				return 0;
			}
			if (itemPosY < -totalHeight) {
				return -totalHeight;
			}
			return itemPosY;
		}

		return 0;
	}

	public void updateTranslateY(double itemPosY) {
		_mainVbox.setTranslateY(itemPosY);
	}

	// get from logic side
	public static boolean isSameDay(TaskEntity task1, TaskEntity task2) {
		if (task1 == null) { // new day
			return false;
		}
		if (task1.getDueDate().get(Calendar.YEAR) == task2.getDueDate().get(Calendar.YEAR)) {
			if (task1.getDueDate().get(Calendar.MONTH) == task2.getDueDate().get(Calendar.MONTH)) {
				if (task1.getDueDate().get(Calendar.DATE) == task2.getDueDate().get(Calendar.DATE)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isSameWeek(TaskEntity task1, TaskEntity task2) {
		if (task1 == null) {
			return false;
		}
		if (task1.getDueDate().get(Calendar.YEAR) == task2.getDueDate().get(Calendar.YEAR)) {

			// display by year week //remove after decided
			boolean byYearWeek = true;
			if (byYearWeek) {
				if (task1.getDueDate().get(Calendar.WEEK_OF_YEAR) == task2.getDueDate().get(Calendar.WEEK_OF_YEAR)) {
					return true;
				}
			} else {
				if (task1.getDueDate().get(Calendar.MONTH) == task2.getDueDate().get(Calendar.MONTH)) {
					if (task1.getDueDate().get(Calendar.WEEK_OF_MONTH) == task2.getDueDate()
							.get(Calendar.WEEK_OF_MONTH)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public ArrayList<DescriptionLabel> rebuildDescriptionLabelsForWeek() {
		ArrayList<DescriptionLabel> descriptionLables = new ArrayList<DescriptionLabel>();
		GridPane gp = getSelectedGridPane();
		if (gp == null) { // no selected gridPane
			return null;
		}
		HBox hbox = (HBox) gp.getParent();
		int countOfItems = 0;
		for (int i = 0; i < _mainVbox.getChildren().size(); i++) {
			VBox weekBox = (VBox) _mainVbox.getChildren().get(i);
			TaskEntity firstTaskInWeek = workingList.get(_startIndex + countOfItems);
			int numberOfTaskInWeek = countNumberOfTaskInWeek(weekBox);
			int indexForLastTaskInWeek = countOfItems + numberOfTaskInWeek - 1;
			TaskEntity lastTaskInWeek = null;
			if (indexForLastTaskInWeek < workingList.size()) {
				lastTaskInWeek = workingList.get(_startIndex + indexForLastTaskInWeek);
				countOfItems += numberOfTaskInWeek;
			}
			DescriptionLabel dLabel = new DescriptionLabel(firstTaskInWeek, lastTaskInWeek);
			dLabel.setHeight(weekBox.getMinHeight());
			if (weekBox.getChildren().contains(hbox.getParent())) {
				dLabel.setSelected();
			}
			descriptionLables.add(dLabel);
		}
		return descriptionLables;
	}

	public ArrayList<DescriptionLabel> rebuildDescriptionLabelsForDay() {
		ArrayList<DescriptionLabel> descriptionLables = new ArrayList<DescriptionLabel>();
		GridPane gp = getSelectedGridPane();
		if (gp == null) {
			return null;
		}
		HBox hbox = (HBox) gp.getParent();
		int countOfItems = 0;
		for (int i = 0; i < _mainVbox.getChildren().size(); i++) {
			VBox weekBox = (VBox) _mainVbox.getChildren().get(i);
			for (int k = 0; k < weekBox.getChildren().size(); k++) {
				VBox dayParent = (VBox) weekBox.getChildren().get(k);
				TaskEntity firstTaskInWeek = workingList.get(_startIndex + countOfItems);
				int numberOfTaskInDay = countNumberOfTaskInDay(dayParent);
				countOfItems += numberOfTaskInDay;
				DescriptionLabel dLabel = new DescriptionLabel(firstTaskInWeek);
				dLabel.setHeight(dayParent.getMinHeight());
				if (dayParent.getChildren().contains(hbox)) {
					dLabel.setSelected();
				}
				descriptionLables.add(dLabel);
			}
		}
		return descriptionLables;
	}

	public int countNumberOfTaskInWeek(VBox weekBox) {
		int noOfTask = 0;
		for (int i = 0; i < weekBox.getChildren().size(); i++) {
			VBox dayBox = (VBox) weekBox.getChildren().get(i);
			noOfTask += countNumberOfTaskInDay(dayBox);
		}
		return noOfTask;
	}

	public int countNumberOfTaskInDay(VBox dayBox) {
		return dayBox.getChildren().size() - 1;
	}

	public int getSelectedGridPaneIndex(VBox vbox) {
		VBox parentDay = (VBox) vbox.getChildren().get(0);
		HBox parentHbox = (HBox) parentDay.getChildren().get(1);
		GridPane gp = (GridPane) parentHbox.getChildren().get(0);
		String id = gp.getId();
		return Integer.parseInt(id.substring(4));
	}

	public GridPane getSelectedGridPane() {
		if (_gridPanes.size() <= (_selectedIndex - _startIndex)) {
			return null;
		}
		GridPane gridPane = _gridPanes.get(_selectedIndex - _startIndex);
		return gridPane;
	}

	public void destoryStage() {
		_stage.close();
	}

	public void setView(int view) {
		_view = view;
	}

	public boolean isAtDetailedView(double value) {
		double totalHeight = 0;
		int index = _selectedIndex - _startIndex;
		int counterForDoneItems = 0;
		for (int i = 0; i < _gridPanes.size(); i++) {
			GridPane gp = _gridPanes.get(i);
			HBox gpParent = (HBox) gp.getParent();
			VBox dayParent = (VBox) gpParent.getParent();
			VBox weekParent = (VBox) dayParent.getParent();
			if (gp.getMinHeight() < gp.getMaxHeight()) {
				if (gp.getMaxHeight() - gp.getMinHeight() < value) {
					value = gp.getMaxHeight() - gp.getMinHeight();
				}
				if (i < index) {
					totalHeight += value;
				}
				gp.setMinHeight(gp.getMinHeight() + value);
				gpParent.setMinHeight(gpParent.getMinHeight() + value);
				dayParent.setMinHeight(dayParent.getMinHeight() + value);
				weekParent.setMinHeight(weekParent.getMinHeight() + value);
			} else {
				counterForDoneItems++;
			}
		}
		transLationY += totalHeight;
		if (counterForDoneItems >= _gridPanes.size()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAtTaskView(double value) {
		double totalHeight = 0;
		int counterForDoneItems = 0;
		int index = _selectedIndex - _startIndex;
		for (int i = 0; i < _gridPanes.size(); i++) {
			GridPane gp = _gridPanes.get(i);
			HBox gpParent = (HBox) gp.getParent();
			VBox dayParent = (VBox) gpParent.getParent();
			VBox weekParent = (VBox) dayParent.getParent();
			if (gp.getMinHeight() > TASK_VIEW_ITEM_HEIGHT) {
				if (gp.getMinHeight() - TASK_VIEW_ITEM_HEIGHT < value) {
					value = gp.getMinHeight() - TASK_VIEW_ITEM_HEIGHT;
				}
				if (i < index) {
					totalHeight += value;
				}
				gp.setMinHeight(gp.getMinHeight() + value);
				gpParent.setMinHeight(gpParent.getMinHeight() + value);
				gpParent.setMaxHeight(gpParent.getMinHeight());
				dayParent.setMinHeight(dayParent.getMinHeight() + value);
				weekParent.setMinHeight(weekParent.getMinHeight() + value);
			} else {
				counterForDoneItems++;
			}
		}
		transLationY += totalHeight;
		if (counterForDoneItems >= _gridPanes.size()) {
			return true;
		} else {
			return false;
		}
	}

	public int getSelectIndex() {
		return _selectedIndex;
	}

	public void setSelectedIndex(int index) {
		_selectedIndex = index;
	}

	public void changeTheme(String styleSheet) {
		_styleSheet = styleSheet;
		_stage.getScene().getStylesheets().clear();
		_stage.getScene().getStylesheets().add(_styleSheet);
	}

}
