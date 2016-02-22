package userInterface;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FloatingBarViewUserInterface implements ViewInterface {

    static final int COMPONENT_HEIGHT = 50;
    static final int COMPONENT_TOP_MARGIN = 50;
    static final int COMPONENT_BOTTOM_MARGIN = 2;
    private static final int POSITION_ZERO = 0;

    private Stage _stage;
    private int _stageWidth;
    private int _stageHeight;
    private int _windowPosX;
    private int _windowPosY;

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

    public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth,
            int windowHeight) {
        _stage = new Stage();
        _stage.initOwner(owner);
        _stage.initStyle(StageStyle.UNDECORATED);
        _stage.setTitle("Display");
        _stage.setX(applicationX);
        _stage.setY(applicationY);

        HBox hbox = new HBox();
        hbox.setPrefHeight(200);

        _stage.setScene(new Scene(hbox, windowWidth, windowHeight, Color.LIGHTCYAN));
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

}
