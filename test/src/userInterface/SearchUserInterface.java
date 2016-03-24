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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class SearchUserInterface implements ViewInterface {

	private static SearchUserInterface _myInstance;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

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
	private static final int THRESHOLD = 20;

	private StackPane _mainVbox;
	private VBox _secondaryVbox;

	// variables to control items in floatingView.
	private int _startIndex = -1;
	private int _endIndex = -1;
	private int _selectedIndex = -1;
	private double transLationY = 0;

	private ArrayList<TaskEntity> _searchList;
	private ArrayList<HBox> _searchBoxes = new ArrayList<HBox>();

	public static SearchUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
		if (_myInstance == null) {
			if (primaryStage == null || screenBounds == null) {
				return null;
			}
			_myInstance = new SearchUserInterface(primaryStage, screenBounds, fixedSize);
			return _myInstance;
		}
		return null;
	}

	private SearchUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
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
		_mainVbox.setId("cssRoot");

		Scene s = new Scene(_mainVbox, stageWidth, stageHeight);
		s.setFill(Color.TRANSPARENT);
		_stage.setScene(s);
	}

	private void buildComponent() {
		_mainVbox.getChildren().clear();
		_secondaryVbox = new VBox();
		_secondaryVbox.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		_secondaryVbox.setMaxHeight(_stageHeight - LABEL_TITLE_HEIGHT);
		_secondaryVbox.setId("cssFloatingViewSecondaryBox");

		_mainVbox.getChildren().add(_secondaryVbox);
		HBox labelTitle = buildTilteLabel();
		_mainVbox.getChildren().add(labelTitle);
		StackPane.setAlignment(labelTitle, Pos.TOP_LEFT);
		StackPane.setAlignment(_secondaryVbox, Pos.TOP_LEFT);
	}

	public HBox buildTilteLabel() {
		HBox titleLableBox = new HBox();
		titleLableBox.setId("cssSearchTitle");
		titleLableBox.setMinWidth(_stageWidth);
		titleLableBox.setMinHeight(LABEL_TITLE_HEIGHT);
		titleLableBox.setMaxHeight(LABEL_TITLE_HEIGHT);

		Label searchTitle = new Label("Search View");
		searchTitle.setMinWidth(_stageWidth);
		searchTitle.setFont(FONT_LABEL);
		searchTitle.setMinHeight(LABEL_TITLE_HEIGHT);
		searchTitle.setMaxHeight(LABEL_TITLE_HEIGHT);
		HBox.setMargin(searchTitle, new Insets(0, 0, 0, 30));

		titleLableBox.getChildren().add(searchTitle);
		return titleLableBox;
	}

	public void update(int value) {
		if (value > 0)// ctrl down
		{
			if (_endIndex + 1 < _searchList.size()) {
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
		HBox item = buildIndividualSearchItem(_searchList.get(_startIndex), _startIndex);
		_searchBoxes.add(0, item);
		_secondaryVbox.getChildren().add(0, item);
	}

	private void removeLastTask() {
		_endIndex--;
		HBox itemToRemove = _searchBoxes.remove(_searchBoxes.size() - 1);
		_secondaryVbox.getChildren().remove(itemToRemove);
	}

	private void addLastItem() {
		_endIndex++;
		HBox item = buildIndividualSearchItem(_searchList.get(_endIndex), _endIndex);
		_searchBoxes.add(item);
		_secondaryVbox.getChildren().add(item);
	}

	private void removeFirstTask() {
		_startIndex++;
		HBox item = _searchBoxes.remove(0);
		_secondaryVbox.getChildren().remove(item);
	}

	private HBox buildIndividualSearchItem(TaskEntity taskEntity, int index) {
		
		return null;
	}

	public void updateTranslateY(double posY) {

	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

}
