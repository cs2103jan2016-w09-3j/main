
package userInterface;

import java.util.ArrayList;
import java.util.Calendar;

import dateParser.ReverseParser;
import entity.DescriptionLabel;
import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mainLogic.Utils;

public class TaskViewUserInterface implements ViewInterface {

	static final int TASK_VIEW_LABEL_HEIGHT = 35;
	static final int TASK_VIEW_ITEM_HEIGHT = 30;
	static final int DETAILED_VIEW_ITEM_HEIGHT = 90;
	static final int LABEL_FONT_SIZE = 12;
	static final int TASK_FONT_SIZE = 12;
	static final int SELECTOR_POSITION_Y = TASK_VIEW_LABEL_HEIGHT + TASK_VIEW_ITEM_HEIGHT * 2;

	private static final int GAP_SIZE = 10;
	private static final int THRESHOLD = 50;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private int _startIndex = -1;
	private int _endIndex = -1;
	private int _selectedIndex = -1;
	private int _individualItemWidth = -1;
	private double transLationY = 0;

	private int _view = UserInterfaceController.TASK_VIEW;

	private VBox _mainVbox; // main parent for items
	private ArrayList<GridPane> _gridPanes = new ArrayList<GridPane>();
	private ArrayList<TaskEntity> workingList;

	public TaskViewUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
	}

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		if (fixedSize) {
			_stageWidth = (int) screenBounds.getWidth() - DescriptionComponent.CONPONENT_WIDTH
					- DescriptionComponent.CONPONENT_RIGHT_MARGIN - DetailComponent.CONPONENT_WIDTH
					- DetailComponent.CONPONENT_LEFT_MARGIN;
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
					- DetailComponent.CONPONENT_WIDTH - DetailComponent.CONPONENT_LEFT_MARGIN;
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
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int stageWidth, int stageHeight) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.UNDECORATED);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_mainVbox = new VBox();
		_mainVbox.setPrefSize(stageWidth, stageHeight);
		_mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		_stage.setScene(new Scene(_mainVbox, stageWidth, stageHeight));
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

	public void buildComponent(ArrayList<TaskEntity> workingList, int workingIndex) {
		_mainVbox.getChildren().clear();
		_gridPanes = new ArrayList<GridPane>();

		this.workingList = workingList;
		_individualItemWidth = _stageWidth;

		if (workingList.size() > 0) {
			int startIndex = 0;
			int endIndex = workingList.size() - 1;
			if (workingIndex - THRESHOLD > startIndex) {
				startIndex = workingIndex - THRESHOLD;
			}
			if (workingIndex + THRESHOLD < endIndex) {
				endIndex = workingIndex + THRESHOLD;
			}

			VBox weekBox = createWeekParent();
			VBox topBox = createDayParent(workingList.get(startIndex));
			HBox item = buildIndividualTask(workingList.get(startIndex),startIndex);
			topBox.getChildren().add(item);
			topBox.setMinHeight(topBox.getMinHeight() + item.getMinHeight());
			for (int i = startIndex + 1; i <= endIndex; i++) {
				if (!isSameWeek(workingList.get(i - 1), workingList.get(i))) {
					weekBox.getChildren().add(topBox);
					weekBox.setMinHeight(weekBox.getMinHeight() + topBox.getMinHeight());
					_mainVbox.getChildren().add(weekBox);
					weekBox = createWeekParent();
					topBox = createDayParent(workingList.get(i));
				} else {
					if (!isSameDay(workingList.get(i - 1), workingList.get(i))) {
						weekBox.getChildren().add(topBox);
						weekBox.setMinHeight(weekBox.getMinHeight() + topBox.getMinHeight());
						topBox = createDayParent(workingList.get(i));
					}
				}
				HBox itemToAdd = buildIndividualTask(workingList.get(i),i);
				topBox.getChildren().add(itemToAdd);
				topBox.setMinHeight(topBox.getMinHeight() + itemToAdd.getMinHeight());
			}
			weekBox.getChildren().add(topBox);
			weekBox.setMinHeight(weekBox.getMinHeight() + topBox.getMinHeight());
			_mainVbox.getChildren().add(weekBox);
			_startIndex = startIndex;
			_endIndex = endIndex;
			_selectedIndex = workingIndex;
		}
	}

	public void jumpToIndex(int workingIndex) {

	}

	public VBox createWeekParent() {
		VBox vbox = new VBox();
		vbox.setId("cssTaskViewWeek");
		vbox.setMinWidth(_stageWidth);
		vbox.setMinHeight(0);
		return vbox;
	}

	public VBox createDayParent(TaskEntity taskEntity) {
		VBox vbox = new VBox();
		vbox.setMinHeight(TASK_VIEW_LABEL_HEIGHT);
		Label dateLabel = new Label(getStringOfDate(taskEntity.getDueDate()));
		dateLabel.setMinHeight(TaskViewUserInterface.TASK_VIEW_LABEL_HEIGHT);
		dateLabel.setFont(new Font(PrimaryUserInterface.DEFAULT_FONT, TaskViewUserInterface.LABEL_FONT_SIZE));
		vbox.getChildren().add(dateLabel);
		VBox.setMargin(dateLabel, new Insets(0, 0, 0, 20));
		return vbox;
	}

	public String calculateVboxCssStyle(Calendar calendar) {
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			return "cssTaskViewSunday";
		case Calendar.SATURDAY:
			return "cssTaskViewSaturday";
		case Calendar.MONDAY:
			return "cssTaskViewMonday";
		case Calendar.TUESDAY:
			return "cssTaskViewTuesday";
		case Calendar.WEDNESDAY:
			return "cssTaskViewWednesday";
		case Calendar.THURSDAY:
			return "cssTaskViewThursday";
		case Calendar.FRIDAY:
			return "cssTaskViewFriday";
		}
		return null;
	}

	public String getStringOfDate(Calendar c) {
		return ReverseParser.reParse(c);
	}

	public HBox buildIndividualTask(TaskEntity taskEntity, int index) {
		HBox hbox = new HBox();
		GridPane gridPane = createGridPaneForTask(taskEntity,index);
		hbox.setMinHeight(gridPane.getMinHeight());
		hbox.setMaxHeight(gridPane.getMinHeight());
		_gridPanes.add(gridPane);
		hbox.getChildren().add(gridPane);
		return hbox;
	}

	public GridPane createGridPaneForTask(TaskEntity taskEntity, int index) {
		Font font = new Font(PrimaryUserInterface.DEFAULT_FONT, TaskViewUserInterface.TASK_FONT_SIZE);
		GridPane grid = new GridPane();
		grid.setStyle(null);
		grid.setId("");
		grid.setHgap(GAP_SIZE);
		grid.setMinWidth(_individualItemWidth);

		if (_view == UserInterfaceController.TASK_VIEW) {
			grid.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		} else {
			grid.setMinHeight(DETAILED_VIEW_ITEM_HEIGHT);
		}
		grid.setMaxHeight(DETAILED_VIEW_ITEM_HEIGHT);

		Label indexLabel = new Label(Utils.convertDecToBase36(index));
		indexLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		indexLabel.setFont(font);
		grid.add(indexLabel, 0, 0);

		Label timeLabel = new Label();
		if (taskEntity.isFullDay()) {
			timeLabel.setText("Full Day Event");
		} else {
			timeLabel.setText(taskEntity.getTime());
		}
		timeLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		timeLabel.setFont(font);
		grid.add(timeLabel, 1, 0);

		Label titleLabel = new Label(taskEntity.getName());
		titleLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		titleLabel.setFont(font);
		grid.add(titleLabel, 2, 0);
		
		Label descriptionLabel = new Label(taskEntity.getDescription());
		descriptionLabel.setMinHeight(TASK_VIEW_ITEM_HEIGHT);
		descriptionLabel.setFont(font);
		grid.add(descriptionLabel, 3, 0);

		Label descriptionLabel2 = new Label(taskEntity.getDescription());
		descriptionLabel2.setMinHeight(0);
		descriptionLabel2.setFont(font);
		descriptionLabel2.setStyle("-fx-background-color:red");
		grid.add(descriptionLabel2, 3, 1);
		return grid;
	}

	// inclusive
	public boolean isBetweenStartEnd(int index) {
		if (index >= _startIndex && index <= _endIndex) {
			return true;
		}
		return false;
	}

	public void update(int value) {
		if (!isBetweenStartEnd(value + _selectedIndex)) {
			if (value > 0) {
				value = _gridPanes.size() - _selectedIndex - 1;
			} else if (value < 0) {
				value = _selectedIndex;
			}
		}
		for (int i = 0; i <  Math.abs(value); i++) {
			if (value > 0)// ctrl down
			{
				if (_endIndex + 1 < workingList.size()) {
					if (_selectedIndex - _startIndex >= THRESHOLD) {
						removeFirstTask();
					}
					addLastItem();
				}
			} else if (value < 0) {
				if (_startIndex > 0) {
					if (_endIndex - _selectedIndex >= THRESHOLD) {
						removeLastTask();
					}
					addFirstItem();
				}
			}
		}
	}

	public void removeFirstTask() {
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
	}

	public void removeLastTask() {
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
	}

	public void addLastItem() {
		GridPane gp = _gridPanes.get(_gridPanes.size() - 1);
		VBox gpDayParent = (VBox) gp.getParent().getParent();
		VBox gpWeekParent = (VBox) gpDayParent.getParent();
		if (isSameDay(workingList.get(_endIndex), workingList.get(_endIndex + 1))) {
			HBox itemToAdd = buildIndividualTask(workingList.get(_endIndex + 1),(_endIndex + 1));
			gpDayParent.getChildren().add(itemToAdd);
			gpDayParent.setMinHeight(gpDayParent.getMinHeight() + itemToAdd.getMinHeight());
			gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() + itemToAdd.getMinHeight());
		} else {
			VBox weekParent = null;
			if (isSameWeek(workingList.get(_endIndex), workingList.get(_endIndex + 1))) {
				weekParent = (VBox) gp.getParent().getParent().getParent();
			} else {
				weekParent = createWeekParent();
				_mainVbox.getChildren().add(weekParent);
			}
			VBox vbox = createDayParent(workingList.get(_endIndex + 1));
			HBox itemToAdd = buildIndividualTask(workingList.get(_endIndex + 1),(_endIndex + 1));
			vbox.getChildren().add(itemToAdd);
			vbox.setMinHeight(vbox.getMinHeight() + itemToAdd.getMinHeight());
			weekParent.setMinHeight(weekParent.getMinHeight() + vbox.getMinHeight());
			weekParent.getChildren().add(vbox);
		}
		_endIndex = _endIndex + 1;
	}

	public void addFirstItem() {
		GridPane gp = _gridPanes.get(0);
		VBox gpDayParent = (VBox) gp.getParent().getParent();
		VBox gpWeekParent = (VBox) gpDayParent.getParent();
		if (isSameDay(workingList.get(_startIndex), workingList.get(_startIndex - 1))) {
			HBox itemToAdd = buildIndividualTask(workingList.get(_startIndex - 1),(_startIndex - 1));
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
			HBox itemToAdd = buildIndividualTask(workingList.get(_startIndex - 1),(_startIndex - 1));
			vbox.getChildren().add(itemToAdd);
			vbox.setMinHeight(vbox.getMinHeight() + itemToAdd.getMinHeight());
			weekBox.getChildren().add(0, vbox);
			weekBox.setMinHeight(weekBox.getMinHeight() + vbox.getMinHeight());
			transLationY += vbox.getMinHeight();
		}
		_gridPanes.add(0, _gridPanes.remove(_gridPanes.size() - 1));
		_startIndex = _startIndex - 1;
	}

	public boolean setItemSelected(int value) {
		int index = value + _selectedIndex;
		if (isBetweenStartEnd(index)) {
			if (_gridPanes.size() > 0) {
				_gridPanes.get(_selectedIndex - _startIndex).setId(null);
				_gridPanes.get(index - _startIndex).setId("cssTaskViewSelectedTask");
				_selectedIndex = index;
				updateTranslationY();
			}
			return true;
		} else {
			if (value > 0) {
				if (_gridPanes.size() > 0) {
					_gridPanes.get(_selectedIndex - _startIndex).setId(null);
					_gridPanes.get(_gridPanes.size() - 1).setId("cssTaskViewSelectedTask");
					_selectedIndex = _gridPanes.size() - 1;
				}
			} else if (value < 0) {
				if (_gridPanes.size() > 0) {
					_gridPanes.get(_selectedIndex - _startIndex).setId(null);
					_gridPanes.get(0).setId("cssTaskViewSelectedTask");
					_selectedIndex = 0;
				}
			}
			updateTranslationY();
		}
		return false;
	}

	// set transLationY to be desired tranlationY, which is current selected
	// item position + threshold
	public void updateTranslationY() {
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

	public static Label createLabelForDay(TaskEntity taskEntity) {
		return new Label(taskEntity.getDueDate().toString());
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
		int noOfTask = 0;
		for (int i = 0; i < dayBox.getChildren().size(); i++) {
			if (dayBox.getChildren().get(i) instanceof HBox) {
				noOfTask++;
			}
		}
		return noOfTask;
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
}
