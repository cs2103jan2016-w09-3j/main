package userInterface;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FloatingBarViewUserInterface implements ViewInterface {

	static final int COMPONENT_HEIGHT = 50;
	static final int COMPONENT_TOP_MARGIN = 50;
	static final int COMPONENT_BOTTOM_MARGIN = 2;
	private static final int POSITION_ZERO = 0;

	static final int FONT_SIZE_TITLE_LABEL = 20;
	static final int FONT_SIZE_TASK = 16;
	private static final Font FONT_LABEL_TITLE = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_TITLE_LABEL);
	private static final Font FONT_LABEL_TASK = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_TASK);
	
	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;
	private HBox _mainHBox;

	public FloatingBarViewUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
	}

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		if (fixedSize) {
			_stageWidth = (int) screenBounds.getWidth();
			_stageHeight = COMPONENT_HEIGHT;
			_windowPosX = POSITION_ZERO;
			_windowPosY = POSITION_ZERO;
		} else {
			_stageWidth = (int) (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE);
			_stageHeight = COMPONENT_HEIGHT;
			_windowPosX = (int) (screenBounds.getWidth()
					- (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2;
			_windowPosY = COMPONENT_TOP_MARGIN;
		}
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth, int windowHeight) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_mainHBox = new HBox();
		_mainHBox.setPrefHeight(200);
		_mainHBox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		_mainHBox.setId("cssRoot");

		Scene scene = new Scene(_mainHBox, windowWidth, windowHeight, Color.TRANSPARENT);
		scene.setFill(Color.TRANSPARENT);
		_stage.setScene(scene);
		build();
	}

	public void build() {
		GridPane gp = new GridPane();
		gp.setId("cssFloatingTask");
		gp.setMinWidth(_stageWidth);
		Label floatTitleLabel = new Label("Floating task of the day");
		floatTitleLabel.setMinHeight(_stageHeight);
		floatTitleLabel.setMinWidth(250);
		floatTitleLabel.setId("cssFloatingTitleLabel");
		floatTitleLabel.setAlignment(Pos.CENTER);
		floatTitleLabel.setFont(FONT_LABEL_TITLE);
		gp.add(floatTitleLabel, 0, 0);
		
		Label floatTaskLabel = new Label("Run 10KM and feel good about it!");
		floatTaskLabel.setMinHeight(_stageHeight);
		floatTaskLabel.setAlignment(Pos.CENTER_LEFT);
		floatTaskLabel.setFont(FONT_LABEL_TASK);
		floatTaskLabel.setId("cssFloatingTaskLabel");
		gp.add(floatTaskLabel, 1, 0);
		_mainHBox.getChildren().add(gp);
	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public void update(int value) {

	}

	public void updateTranslateY(double posY) {

	}

	public void destoryStage() {
		_stage.close();
	}

}
