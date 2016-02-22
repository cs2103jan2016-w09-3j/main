package userInterface;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DetailComponent implements ViewInterface {
    
    static final int CONPONENT_WIDTH = 100;
    static final int CONPONENT_LEFT_MARGIN = 5;

    private Stage _stage;
    private int _stageWidth;
    private int _stageHeight;
    private int _windowPosX;
    private int _windowPosY;

    private VBox _mainVbox;

    public DetailComponent(Stage parentStage, Rectangle2D screenBounds, boolean fixedSize) {
        initializeVaribles(screenBounds, fixedSize);
        initializeStage(parentStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
    }

    public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
        if (fixedSize) {
            _stageWidth = CONPONENT_WIDTH;
            _stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_HEIGHT
                    - FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
            _windowPosX = (int) (screenBounds.getWidth() - CONPONENT_WIDTH);
            _windowPosY = (int) screenBounds.getHeight() - _stageHeight
                    - PrimaryUserInterface.COMMAND_BAR_HEIGTH - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
        } else {
            _stageWidth = CONPONENT_WIDTH;
            _stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
                    - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_HEIGHT
                    - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
            _windowPosX = (int) ((screenBounds.getWidth()
                    - (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2
                    + (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)
                    - CONPONENT_WIDTH);
            _windowPosY = (int) screenBounds.getHeight() - _stageHeight
                    - PrimaryUserInterface.COMMAND_BAR_HEIGTH - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
        }
    }

    public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth,
            int windowHeight) {
        _stage = new Stage();
        _stage.initOwner(owner);
        _stage.initStyle(StageStyle.UNDECORATED);
        _stage.setX(applicationX);
        _stage.setY(applicationY);

        _mainVbox = new VBox();
        _mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
        _stage.setScene(new Scene(_mainVbox, windowWidth, windowHeight));
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

}
