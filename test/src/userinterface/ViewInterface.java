/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *         All user interface components controlled by the UserInterfaceController should implement this class.
 */
package userinterface;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

public interface ViewInterface {

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize);

	public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth, int windowHeight,
			EventHandler<MouseEvent> mouseEvent);

	public void update(int value);

	public void updateTranslateY(double posY);

	public void show();

	public void hide();

	public void changeTheme(String styleSheet);

}
