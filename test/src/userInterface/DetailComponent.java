package userInterface;

import java.util.ArrayList;

import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DetailComponent implements ViewInterface {

	static final int COMPONENT_WIDTH = 300;
	static final int COMPONENT_LEFT_MARGIN = 0;
	private static final int COMPONENT_INNER_MARGIN = 30;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private int _itemMaxWidth;

	private VBox _mainVbox;

	public DetailComponent(Stage parentStage, Rectangle2D screenBounds, boolean fixedSize) {
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(parentStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
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
		_stage.initStyle(StageStyle.UNDECORATED);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_itemMaxWidth = _stageWidth - COMPONENT_INNER_MARGIN - COMPONENT_INNER_MARGIN;

		_mainVbox = new VBox();
		_mainVbox.setId("cssDescriptionMainBox");
		_mainVbox.setMinSize(_stageWidth, _stageHeight);
		_mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		_stage.setScene(new Scene(_mainVbox, windowWidth, windowHeight));
	}

	public void buildComponent(TaskEntity task) {
		_mainVbox.getChildren().clear();
		VBox childToAdd = buildItem(task);
		_mainVbox.getChildren().add(childToAdd);
		_mainVbox.setMargin(childToAdd, new Insets(COMPONENT_INNER_MARGIN));
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
