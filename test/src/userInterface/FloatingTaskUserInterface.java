package userInterface;

import java.util.ArrayList;
import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

	private VBox _mainVbox;
	private VBox _secondaryVbox;

	// font
	static final int FONT_SIZE_LABEL = 16;
	static final int FONT_SIZE_LABEL_DATE = 10;
	static final int FONT_SIZE_TASK = 12;
	static final int FONT_SIZE_INDEX = 8;
	private static final Font FONT_LABEL = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_LABEL);
	private static final Font FONT_TASK = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_TASK);
	private static final Font FONT_INDEX = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_INDEX);
	private static final Font FONT_LABEL_DATE = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_LABEL_DATE);

	static final int LABEL_TITLE_HEIGHT = 35;
	static final int LABEL_TASK_HEIGHT = 30;
	private static final int THRESHOLD = 50;

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
			_stageWidth = (int) screenBounds.getWidth() - DetailComponent.COMPONENT_WIDTH
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
					- DetailComponent.COMPONENT_WIDTH - DetailComponent.COMPONENT_LEFT_MARGIN;
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

		_mainVbox = new VBox();
		_mainVbox.setPrefSize(stageWidth, stageHeight);
		_mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		_mainVbox.setId("cssRoot");

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

	private void addFirstItem() {
		_startIndex = _startIndex + 1;
		HBox item = buildIndividualFloating(_floatingList.get(_startIndex), _startIndex);
		_floatingBoxes.add(0, item);
		_secondaryVbox.getChildren().add(0, item);
	}

	private void removeLastTask() {
		HBox itemToRemove = _floatingBoxes.remove(_floatingBoxes.size() - 1);
		_secondaryVbox.getChildren().remove(itemToRemove);
	}

	private void addLastItem() {
		_endIndex++;
		HBox item = buildIndividualFloating(_floatingList.get(_endIndex), _endIndex);
		_secondaryVbox.getChildren().add(item);
	}

	private void removeFirstTask() {
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
		_mainVbox.getChildren().add(buildTilteLabel());

		_secondaryVbox = new VBox();
		_secondaryVbox.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		_secondaryVbox.setMaxHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		_secondaryVbox.setId("cssFloatingViewSecondaryBox");
		_mainVbox.getChildren().add(_secondaryVbox);
	}

	public void buildContent(ArrayList<TaskEntity> floatingList) {
		_floatingList = floatingList;
		_floatingBoxes = new ArrayList<HBox>();
		_selectedIndex = 0;
		// when there are no floating task yet
		if (_floatingList == null || _floatingList.size() == 0) {
			buildHelpWithFloating();
		} else {
			buildFloatingList(_floatingList);
		}
	}

	public HBox buildTilteLabel() {
		HBox titleLableBox = new HBox();
		titleLableBox.setId("cssFloatingTitle");
		titleLableBox.setMinWidth(_stageWidth);
		titleLableBox.setMinHeight(LABEL_TITLE_HEIGHT);
		titleLableBox.setMaxHeight(LABEL_TITLE_HEIGHT);

		Label floatingTitle = new Label("Floating View");
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
		Label temp = new Label("You do not have any floating task yet.");
		temp.setMinWidth(_stageWidth);
		temp.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		temp.setAlignment(Pos.CENTER);
		_secondaryVbox.getChildren().add(temp);
	}

	public void buildFloatingList(ArrayList<TaskEntity> floatingList) {
		_secondaryVbox.getChildren().clear();
		_startIndex = 0;
		if (floatingList.size() < THRESHOLD) {
			_endIndex = floatingList.size() - 1;
		} else {
			_endIndex = THRESHOLD;
		}

		for (int i = _startIndex; i <= _endIndex; i++) {
			HBox item = buildIndividualFloating(floatingList.get(i), i);
			_secondaryVbox.getChildren().add(item);
			_floatingBoxes.add(item);
		}
	}

	public HBox buildIndividualFloating(TaskEntity task, int index) {
		HBox floatingParent = new HBox();
		floatingParent.setMinHeight(LABEL_TASK_HEIGHT);
		floatingParent.setMaxHeight(LABEL_TASK_HEIGHT);
		floatingParent.setMinWidth(_stageWidth);

		Label indexLabel = new Label(Utils.convertDecToBase36(index));
		indexLabel.setMinHeight(LABEL_TASK_HEIGHT);
		indexLabel.setMinWidth(50);
		indexLabel.setAlignment(Pos.CENTER);
		indexLabel.setFont(FONT_INDEX);
		floatingParent.getChildren().add(indexLabel);

		Label descriptionLabel = new Label();
		descriptionLabel.setText(task.getName());
		descriptionLabel.setMinHeight(LABEL_TASK_HEIGHT);
		descriptionLabel.setAlignment(Pos.CENTER);
		descriptionLabel.setFont(FONT_TASK);
		floatingParent.getChildren().add(descriptionLabel);

		return floatingParent;
	}
}
