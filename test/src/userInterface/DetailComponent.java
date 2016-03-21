package userInterface;

import java.util.Calendar;
import java.util.Random;

import entity.TaskEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DetailComponent implements ViewInterface {

	static final int COMPONENT_WIDTH = 300;
	static final int COMPONENT_LEFT_MARGIN = 2;

	private static final int CALENDAR_VEW = 0;
	private static final int TASK_VIEW = 1;
	private static final int EXPANDED_VIEW = 2;
	private static final int ASSOCIATE_VIEW = 3;
	private static final int TOTAL_VIEWS = 3;

	private static final int SPACING_SIZE = 30;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private VBox[] _mainVbox;
	private Scene[] _scenes;
	private int _currentSelectView;
	private int _selectedIndex;

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
		_stage.setScene(_scenes[_currentSelectView]);
	}

	public void buildComponent(TaskEntity task) {
		if (_currentSelectView == TASK_VIEW) {
			buildUIForTaskView(task);
		} else if (_currentSelectView == EXPANDED_VIEW) {
			_selectedIndex = 0;
			buildUIForExpandedView(task);
		}
	}

	public void setView(int view) {
		_currentSelectView = view;
		if (_currentSelectView == ASSOCIATE_VIEW) {
			_stage.setScene(_scenes[EXPANDED_VIEW]);
		} else {
			_stage.setScene(_scenes[_currentSelectView]);
		}
	}

	public void buildUIForTaskView(TaskEntity task) {
		_mainVbox[TASK_VIEW].getChildren().clear();
		VBox childToAdd = buildTask(task);
		_mainVbox[TASK_VIEW].getChildren().add(childToAdd);
	}

	public void buildUIForExpandedView(TaskEntity task) {
		System.out.println("buildExpanded");
		_mainVbox[EXPANDED_VIEW].getChildren().clear();
		VBox childToAdd = buildAssociationList(task);
		_mainVbox[EXPANDED_VIEW].getChildren().add(childToAdd);
	}

	public VBox buildAssociationList(TaskEntity task) {
		VBox box = new VBox();
		box.setSpacing(SPACING_SIZE);
		box.setMinWidth(_stageWidth);
		box.setId("cssDetailAssociationListBox");

		// add itself first
		box.getChildren().add(buildTask(task));

		// change when qy done. add associates
		for (int i = 1; i < 2; i++) {
			Random r = new Random();
			int t = r.nextInt(1);
			String p = "what ";
			for (int k = 0; k < t; k++) {
				p += p;
			}
			task.setDescription(p);
			box.getChildren().add(buildTask(task));
		}
		return box;
	}

	/**
	 * This method is used when the current view is in the ASSOCIATE_VIEW. it
	 * sets the selected item to the index.
	 * 
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		VBox parent = (VBox) _mainVbox[EXPANDED_VIEW].getChildren().get(0);
		if (parent.getChildren().size() > index && index > -1) {
			if (_selectedIndex != -1) {
				VBox prev = (VBox) parent.getChildren().get(_selectedIndex);
				prev.setId("cssExpandedViewVBox");
			}

			VBox curr = (VBox) parent.getChildren().get(index);
			curr.setId("cssExpandedViewVBoxSelected");
			_selectedIndex = index;

			if (parent.getHeight() > _stageHeight) {
				double sizeOnTop = 0;
				for (int i = 0; i < index; i++) {
					VBox tempVBox = (VBox) parent.getChildren().get(i);
					sizeOnTop += tempVBox.getHeight() + SPACING_SIZE;
				}
				parent.setTranslateY(-sizeOnTop);
			}
		}
	}

	public VBox buildTask(TaskEntity task) {
		VBox itemMain = new VBox();
		itemMain.setSpacing(10);
		itemMain.setMinWidth(_stageWidth);
		itemMain.setId("cssExpandedViewVBox");

		Label titleLabel = new Label(task.getName());
		titleLabel.setId("cssExpandedViewLabelTitle");
		titleLabel.setMinWidth(_stageWidth);
		titleLabel.setAlignment(Pos.CENTER);
		itemMain.getChildren().add(titleLabel);

		HBox dateBox = new HBox();
		Label dateTitleLabel = new Label("Due date : ");
		dateBox.getChildren().add(dateTitleLabel);
		Label dateLabel = new Label(getDate(task.getDueDate()));
		dateBox.getChildren().add(dateLabel);
		VBox.setMargin(dateBox, new Insets(0, 20, 0, 20));
		itemMain.getChildren().add(dateBox);

		Text description = new Text(task.getDescription());
		description.setWrappingWidth(_stageWidth - 40);
		description.setTextAlignment(TextAlignment.JUSTIFY);
		itemMain.getChildren().add(description);
		VBox.setMargin(description, new Insets(0, 20, 20, 20));

		return itemMain;
	}

	public String getDate(Calendar cal) {
		String date = "";
		date += cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " "
				+ cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
		return date;

	}

	public void update(int value) {
		int index = value + _selectedIndex;
		setSelectedIndex(index);
	}

	public void updateTranslateY(double posY) {
		// TODO Auto-generated method stub

	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public void destoryStage() {
		_stage.close();
	}

}
