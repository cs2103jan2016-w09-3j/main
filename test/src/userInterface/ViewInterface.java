package userInterface;

import javafx.geometry.Rectangle2D;
import javafx.stage.Window;

public interface ViewInterface {

    public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize);

    public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth,
            int windowHeight);

    public void update(int value);

    public void updateTranslateY(double posY);

    public void show();

    public void hide();

}
