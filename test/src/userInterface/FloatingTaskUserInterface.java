//@@author A0125514N
package userInterface;

import java.util.ArrayList;
import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mainLogic.Utils;

public class FloatingTaskUserInterface implements ViewInterface {

	private static FloatingTaskUserInterface _myInstance;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private StackPane _mainVbox;
	private VBox _secondaryVbox;

	private static final String CSS_LABEL = "cssLabelsFloatingTaskInterface";
	
	// font
	static final int FONT_SIZE_LABEL = 20;
	static final int FONT_SIZE_LABEL_DATE = 10;
	static final int FONT_SIZE_TASK = 12;
	static final int FONT_SIZE_INDEX = 8;
	private static final Font FONT_LABEL = new Font(PrimaryUserInterface.FONT_TITLE_LABLES, FONT_SIZE_LABEL);
	private static final Font FONT_TASK = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_TASK);

	static final int LABEL_TITLE_HEIGHT = 35;
	static final int LABEL_TASK_HEIGHT = 30;
	private static final int THRESHOLD = 20;

	// variables to control items in floatingView.
	private int _startIndex = -1;
	private int _endIndex = -1;
	private int _selectedIndex = -1;
	private int _individualItemWidth = -1;
	private double transLationY = 0;

	private ArrayList<TaskEntity> _floatingList;
	private ArrayList<HBox> _floatingBoxes = new ArrayList<HBox>();

	public static FloatingTaskUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds,
			boolean fixedSize) {
		if (_myInstance == null) {
			if (primaryStage == null || screenBounds == null) {
				return null;
			}
			_myInstance = new FloatingTaskUserInterface(primaryStage, screenBounds, fixedSize);
			return _myInstance;
		}
		return null;
	}

	private FloatingTaskUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
		buildComponent();
	}

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		if (fixedSize) {
			_stageWidth = (int) screenBounds.getWidth();
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN;
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		} else {
			_stageWidth = (int) (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE);
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = (int) (screenBounds.getWidth()
					- (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2;
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		}
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int stageWidth, int stageHeight) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_mainVbox = new StackPane();
		_mainVbox.setPrefSize(stageWidth, stageHeight);
		_mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		_mainVbox.setId("cssRootFloatingTaskView");

		Scene s = new Scene(_mainVbox, stageWidth, stageHeight);
		s.setFill(Color.TRANSPARENT);
		_stage.setScene(s);
	}

	public void update(int value) {
		if (value > 0)// ctrl down
		{
			if (_endIndex + 1 < _floatingList.size()) {
				if (_selectedIndex - _startIndex >= THRESHOLD) {
					removeFirstTask();
					addLastItem();
				}
			}
		} else if (value < 0) {
			if (_startIndex > 0) {
				if (_endIndex - _selectedIndex >= THRESHOLD) {
					removeLastTask();
					addFirstItem();
				}
			}
		}
	}

	private void addFirstItem() {
		_startIndex--;
		HBox item = buildIndividualFloating(_floatingList.get(_startIndex), _startIndex);
		_floatingBoxes.add(0, item);
		_secondaryVbox.getChildren().add(0, item);
	}

	private void removeLastTask() {
		_endIndex--;
		HBox itemToRemove = _floatingBoxes.remove(_floatingBoxes.size() - 1);
		_secondaryVbox.getChildren().remove(itemToRemove);
	}

	private void addLastItem() {
		_endIndex++;
		HBox item = buildIndividualFloating(_floatingList.get(_endIndex), _endIndex);
		_floatingBoxes.add(item);
		_secondaryVbox.getChildren().add(item);
	}

	private void removeFirstTask() {
		_startIndex++;
		HBox item = _floatingBoxes.remove(0);
		_secondaryVbox.getChildren().remove(item);
	}

	public void updateTranslateY(double posY) {
	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public void buildComponent() {

		_mainVbox.getChildren().clear();
		_secondaryVbox = new VBox();
		_secondaryVbox.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		_secondaryVbox.setMaxHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		
		_mainVbox.getChildren().add(_secondaryVbox);
		HBox labelTitle = buildTilteLabel();
		_mainVbox.getChildren().add(labelTitle);
		StackPane.setAlignment(labelTitle, Pos.TOP_LEFT);
		StackPane.setAlignment(_secondaryVbox, Pos.TOP_LEFT);
	}

	public void buildContent(ArrayList<TaskEntity> floatingList) {
		_floatingList = floatingList;
		_floatingBoxes = new ArrayList<HBox>();
		// when there are no floating task yet
		if (_floatingList == null || _floatingList.size() == 0) {
			buildHelpWithFloating();
		} else {
			buildFloatingList(_floatingList);
		}
	}

	public HBox buildTilteLabel() {
		HBox titleLableBox = new HBox();
		titleLableBox.setId("cssFloatingTaskViewTitle");
		titleLableBox.setMinWidth(_stageWidth);
		titleLableBox.setMinHeight(LABEL_TITLE_HEIGHT);
		titleLableBox.setMaxHeight(LABEL_TITLE_HEIGHT);

		Label floatingTitle = new Label("Floating View");
		floatingTitle.getStyleClass().add(CSS_LABEL);
		floatingTitle.setMinWidth(_stageWidth);
		floatingTitle.setFont(FONT_LABEL);
		floatingTitle.setMinHeight(LABEL_TITLE_HEIGHT);
		floatingTitle.setMaxHeight(LABEL_TITLE_HEIGHT);
		HBox.setMargin(floatingTitle, new Insets(0, 0, 0, 30));

		titleLableBox.getChildren().add(floatingTitle);
		return titleLableBox;
	}

	public void buildHelpWithFloating() {
		_secondaryVbox.getChildren().clear();
		Label helpLabel = new Label("You do not have any floating task yet.");
		helpLabel.getStyleClass().add(CSS_LABEL);
		helpLabel.setMinWidth(_stageWidth);
		helpLabel.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		helpLabel.setAlignment(Pos.CENTER);
		_secondaryVbox.getChildren().add(helpLabel);
	}

	public void buildFloatingList(ArrayList<TaskEntity> floatingList) {
		_secondaryVbox.getChildren().clear();
		_selectedIndex = 0;
		_startIndex = 0;
		if (floatingList.size() < THRESHOLD * 2) {
			_endIndex = floatingList.size() - 1;
		} else {
			_endIndex = THRESHOLD * 2;
		}

		for (int i = _startIndex; i <= _endIndex; i++) {
			HBox item = buildIndividualFloating(floatingList.get(i), i);
			_secondaryVbox.getChildren().add(item);
			_floatingBoxes.add(item);
		}
		setSelected(0);
	}

	public HBox buildIndividualFloating(TaskEntity task, int index) {
		HBox floatingParent = new HBox();
		floatingParent.toBack();
		floatingParent.setMinHeight(LABEL_TASK_HEIGHT);
		floatingParent.setMaxHeight(LABEL_TASK_HEIGHT);
		floatingParent.setMinWidth(_stageWidth);

		Label indexLabel = new Label("ID"+Integer.toString(index));
		indexLabel.getStyleClass().add(CSS_LABEL);
		indexLabel.setMinHeight(LABEL_TASK_HEIGHT);
		indexLabel.setMinWidth(50);
		indexLabel.setAlignment(Pos.CENTER);
		indexLabel.setFont(Font.font(PrimaryUserInterface.FONT_DEFAULT, FontWeight.BOLD, FONT_SIZE_TASK));
		floatingParent.getChildren().add(indexLabel);

		Label descriptionLabel = new Label();
		descriptionLabel.getStyleClass().add(CSS_LABEL);
		descriptionLabel.setText(task.getName());
		descriptionLabel.setMinHeight(LABEL_TASK_HEIGHT);
		descriptionLabel.setAlignment(Pos.CENTER);
		descriptionLabel.setFont(FONT_TASK);
		floatingParent.getChildren().add(descriptionLabel);

		return floatingParent;
	}

	public void setSelected(int value) {
		int temp = _selectedIndex + value;
		if (isBetweenRange(temp)) {
			HBox prevItem = _floatingBoxes.get(_selectedIndex - _startIndex);
			prevItem.setId("");
			_selectedIndex = temp;
			HBox item = _floatingBoxes.get(_selectedIndex - _startIndex);
			item.setId("cssFloatingTaskViewSelected");
			translateY(getTopHeight(_selectedIndex - _startIndex));
		}
	}

	public double getTopHeight(int index) {
		double sizeTop = index * LABEL_TASK_HEIGHT;
		return sizeTop;
	}

	public void translateY(double itemTopHeight) {
		double posY = -LABEL_TITLE_HEIGHT;
		int entireAreaHeight = _stageHeight - LABEL_TITLE_HEIGHT;
		if (itemTopHeight + LABEL_TASK_HEIGHT > entireAreaHeight) {
			posY += itemTopHeight + LABEL_TASK_HEIGHT - entireAreaHeight;
		} else if (itemTopHeight < entireAreaHeight) {
			
		}
		_secondaryVbox.setTranslateY(-posY);
	}

	public boolean isBetweenRange(int index) {
		if (index >= _startIndex && index <= _endIndex) {
			return true;
		}
		return false;
	}

	public StackPane getMainLayoutComponent() {
		return _mainVbox;
	}
}
