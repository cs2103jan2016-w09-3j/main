package userInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import entity.TaskEntity;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import mainLogic.TaskManager;

public class FloatingTaskUserInterface implements ViewInterface {

	private static FloatingTaskUserInterface _myInstance;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private Stage _parentStage;
	private Rectangle2D _screenBounds;
	private boolean _fixedSize;
	private VBox _mainVbox;
	private double _individualItemWidth;

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
		_individualItemWidth = _stageWidth;
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
	}

	public void updateTranslateY(double posY) {
	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public void buildComponent(ArrayList<TaskEntity> floatingList) {
		// when there are no floating task yet
		if (floatingList == null || floatingList.size() < 0) {
			buildHelpWithFloating();
		} else {

		}
	}

	public void buildHelpWithFloating() {
		
	}

	public void buildFloatingList(ArrayList<TaskEntity> floatingList) {

	}

}
