package userInterface;

import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DetailComponent implements ViewInterface {

	static final int COMPONENT_WIDTH = 300;
	static final int COMPONENT_LEFT_MARGIN = 2;
	private static final int COMPONENT_INNER_MARGIN = 30;

	private static final int CALENDAR_VEW = 0;
	private static final int TASK_VIEW = 1;
	private static final int EXPANDED_VIEW = 2;
	private static final int TOTAL_VIEWS = 3;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private int _itemMaxWidth;
	private VBox[] _mainVbox;
	private Scene[] _scenes;
	private int _currentSelectView;

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
			_mainVbox[i].setId("cssRoot");
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
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth, int windowHeight) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_itemMaxWidth = _stageWidth - COMPONENT_INNER_MARGIN - COMPONENT_INNER_MARGIN;
		_stage.setScene(_scenes[_currentSelectView]);
	}

	public void buildComponent(TaskEntity task) {
		if (_currentSelectView == TASK_VIEW) {
			buildUIForTaskView(task);
		} else if (_currentSelectView == EXPANDED_VIEW) {

		}
	}

	public void setView(int view) {
		_currentSelectView = view;
		_stage.setScene(_scenes[_currentSelectView]);
	}
	
	public void buildUIForTaskView(TaskEntity task){
		_mainVbox[TASK_VIEW].getChildren().clear();
		VBox childToAdd = buildItem(task);
		_mainVbox[TASK_VIEW].getChildren().add(childToAdd);
		VBox.setMargin(childToAdd, new Insets(COMPONENT_INNER_MARGIN));
	}

	public VBox buildItem(TaskEntity task) {
		VBox itemMain = new VBox();
		itemMain.setMinWidth(_itemMaxWidth);
		Label title = new Label(task.getName());
		title.setMinWidth(_itemMaxWidth);
		Label date = new Label(task.getDateCreated().toString());
		Label description = new Label(task.getDescription());
		itemMain.getChildren().add(title);
		itemMain.getChildren().add(date);
		itemMain.getChildren().add(description);
		return itemMain;
	}

	public void update(int value) {
		// TODO Auto-generated method stub

	}

	public void updateTranslateY(double posY) {
		// TODO Auto-generated method stub

	}

	public void show() {
		_stage.show();
	}

	public void hide() {
	}

	public void destoryStage() {
		_stage.close();
	}

}
