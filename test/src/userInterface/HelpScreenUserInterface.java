package userInterface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class HelpScreenUserInterface implements ViewInterface {

	private static HelpScreenUserInterface _myInstance;

	private static final String HELP_DESCRIPTION_FLOATING_BAR = "This is the floating bar, it randomly shows you a task that does not has a date assigned to it.";
	private static final String HELP_DESCRIPTION_MAIN = "Welcome to PCNM help manual! \n Press the left and right arrow keys to scroll through the help and F1 to close the help screen";
	private static final String HELP_DESCRIPTION_DESCRIPTION_COMPONENT = "This is the description view. It shows u the range of the week or the dates of the dates.";
	private static final String HELP_DESCRIPTION_TASK_VIEW = "This is the main view, it shows you your task in chronological order.";
	private static final String HELP_DESCRIPTION_DETAIL_COMPONENT = "This is the detail component, it shows you more description of the selected task.";
	private static final String HELP_DESCRIPTION_COMMAND_BAR = "This is the command bar where u type your input commands.";
	private static final int MARGIN = 10;
	private static final int MAX_ITEMS = 6;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;
	private boolean _isFixed;
	private Rectangle2D _screenBounds;
	private double _taskViewWidth;

	private StackPane _mainPanel;

	private int _selector = 0;

	public static HelpScreenUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
		if (_myInstance == null) {
			if (primaryStage == null || screenBounds == null) {
				return null;
			}
			_myInstance = new HelpScreenUserInterface(primaryStage, screenBounds, fixedSize);
			return _myInstance;
		}
		return null;
	}

	public HelpScreenUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
	}

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		_isFixed = fixedSize;
		_screenBounds = screenBounds;
		if (fixedSize) {
			_stageWidth = (int) screenBounds.getWidth();
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN);
			_windowPosX = 0;
			_windowPosY = 0;
		} else {
			_stageWidth = (int) (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE);
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN);
			_windowPosX = (int) (screenBounds.getWidth()
					- (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2;
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight
					- PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		}
	}

	public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth, int windowHeight) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_mainPanel = new StackPane();
		_mainPanel.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		_mainPanel.setId("cssHelpScreenRoot");
		_mainPanel.setPrefSize(windowWidth, windowHeight);
		_mainPanel.setAlignment(Pos.TOP_CENTER);

		Scene s = new Scene(_mainPanel, windowWidth, windowHeight);
		s.setFill(Color.TRANSPARENT);
		_stage.setScene(s);
		buildComponent();
	}

	public void buildComponent() {
		buildMainHelp();
	}

	public void buildMainHelp() {
		_mainPanel.getChildren().clear();
		VBox main = new VBox();
		main.setAlignment(Pos.CENTER);

		HBox descriptionBox = new HBox();
		descriptionBox.setMaxWidth(_stageWidth / 2);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label descLabel = new Label(HELP_DESCRIPTION_MAIN);
		descLabel.setWrapText(true);
		HBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(descriptionBox);
		_mainPanel.getChildren().add(main);
	}

	public void buildDetailComponentHelp() {
		_mainPanel.getChildren().clear();
		HBox main = new HBox();

		double componentHeight = 0;
		double componentWidth = 0;
		if (_isFixed) {
			componentWidth = DetailComponent.COMPONENT_WIDTH;
			componentHeight = (int) (_screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
		} else {
			componentWidth = DetailComponent.COMPONENT_WIDTH;
			componentHeight = (int) (_screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
		}
		main.setMaxHeight(componentHeight);
		main.setAlignment(Pos.CENTER_LEFT);

		HBox hbox = new HBox();
		hbox.setMinSize(componentWidth, componentHeight);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setMinHeight(2);
		link.setMaxHeight(2);
		link.setMinWidth(20);
		link.setId("cssHelpComponentLinker");

		HBox descriptionBox = new HBox();
		descriptionBox.setMaxWidth(200);
		descriptionBox.setMaxHeight(150);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label descLabel = new Label(HELP_DESCRIPTION_DETAIL_COMPONENT);
		descLabel.setWrapText(true);
		HBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(descriptionBox);
		main.getChildren().add(link);
		main.getChildren().add(hbox);
		_mainPanel.getChildren().add(main);
		main.setTranslateX(_taskViewWidth + DescriptionComponent.CONPONENT_WIDTH
				+ DescriptionComponent.CONPONENT_RIGHT_MARGIN - 220);
		main.setTranslateY(FloatingBarViewUserInterface.COMPONENT_HEIGHT);
	}

	public void buildTaskViewHelp() {
		_mainPanel.getChildren().clear();
		HBox main = new HBox();

		double componentHeight = 0;
		if (_isFixed) {
			_taskViewWidth = (int) _screenBounds.getWidth() - DescriptionComponent.CONPONENT_WIDTH
					- DescriptionComponent.CONPONENT_RIGHT_MARGIN - DetailComponent.COMPONENT_WIDTH
					- DetailComponent.COMPONENT_LEFT_MARGIN;
			componentHeight = (int) (_screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
		} else {
			_taskViewWidth = (int) (_screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)
					- DescriptionComponent.CONPONENT_WIDTH - DescriptionComponent.CONPONENT_RIGHT_MARGIN
					- DetailComponent.COMPONENT_WIDTH - DetailComponent.COMPONENT_LEFT_MARGIN;
			componentHeight = (int) (_screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
		}
		main.setMaxHeight(componentHeight);
		main.setAlignment(Pos.CENTER_LEFT);

		HBox hbox = new HBox();
		hbox.setMinSize(_taskViewWidth, componentHeight);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setMinHeight(2);
		link.setMaxHeight(2);
		link.setMinWidth(20);
		link.setId("cssHelpComponentLinker");

		HBox descriptionBox = new HBox();
		descriptionBox.setMaxWidth(200);
		descriptionBox.setMaxHeight(150);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label descLabel = new Label(HELP_DESCRIPTION_TASK_VIEW);
		descLabel.setWrapText(true);
		HBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(hbox);
		main.getChildren().add(link);
		main.getChildren().add(descriptionBox);
		_mainPanel.getChildren().add(main);
		main.setTranslateX(DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN);
		main.setTranslateY(FloatingBarViewUserInterface.COMPONENT_HEIGHT);
	}

	public void buildDescriptionHelp() {
		_mainPanel.getChildren().clear();
		HBox main = new HBox();

		double componentHeight = 0;
		if (_isFixed) {
			componentHeight = (int) (_screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
		} else {
			componentHeight = (int) (_screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
		}
		main.setMaxHeight(componentHeight);
		main.setAlignment(Pos.CENTER_LEFT);

		HBox hbox = new HBox();
		hbox.setMinSize(DescriptionComponent.CONPONENT_WIDTH, componentHeight);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setMinHeight(2);
		link.setMaxHeight(2);
		link.setMinWidth(20);
		link.setId("cssHelpComponentLinker");

		HBox descriptionBox = new HBox();
		descriptionBox.setMaxWidth(300);
		descriptionBox.setMaxHeight(150);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label descLabel = new Label(HELP_DESCRIPTION_DESCRIPTION_COMPONENT);
		descLabel.setWrapText(true);
		HBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(hbox);
		main.getChildren().add(link);
		main.getChildren().add(descriptionBox);
		main.setTranslateY(FloatingBarViewUserInterface.COMPONENT_HEIGHT);
		_mainPanel.getChildren().add(main);
	}

	public void buildfloatingBarHelp() {
		_mainPanel.getChildren().clear();
		VBox main = new VBox();
		main.setAlignment(Pos.TOP_CENTER);

		HBox hbox = new HBox();
		hbox.setPrefSize(_stageWidth, FloatingBarViewUserInterface.COMPONENT_HEIGHT);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setPrefHeight(30);
		link.setMaxWidth(2);
		link.setId("cssHelpComponentLinker");

		HBox descriptionBox = new HBox();
		descriptionBox.setMaxWidth(300);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label descLabel = new Label(HELP_DESCRIPTION_FLOATING_BAR);
		descLabel.setWrapText(true);
		HBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(hbox);
		main.getChildren().add(link);
		main.getChildren().add(descriptionBox);

		_mainPanel.getChildren().add(main);
	}

	public void buildCommandBarHelp() {
		_mainPanel.getChildren().clear();
		VBox main = new VBox();
		main.setAlignment(Pos.TOP_CENTER);

		HBox hbox = new HBox();
		hbox.setPrefSize(_stageWidth, PrimaryUserInterface.COMMAND_BAR_HEIGTH);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setPrefHeight(30);
		link.setMaxWidth(2);
		link.setId("cssHelpComponentLinker");

		HBox descriptionBox = new HBox();
		descriptionBox.setMaxWidth(300);
		descriptionBox.setMaxHeight(150);
		descriptionBox.setMinHeight(150);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label descLabel = new Label(HELP_DESCRIPTION_COMMAND_BAR);
		descLabel.setWrapText(true);
		HBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(descriptionBox);
		main.getChildren().add(link);
		main.getChildren().add(hbox);
		main.setTranslateY(_stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH - 180);

		_mainPanel.getChildren().add(main);
	}

	public void update(int value) {
		if (_stage.isShowing()) {
			int index = _selector + value;
			if (index < 0) {
			} else if (index >= MAX_ITEMS) {
			} else {
				_selector = index;
			}
			updateHelpPrompt();
		}
	}

	private void updateHelpPrompt() {
		switch (_selector) {
		case 0: {
			buildMainHelp();
			break;
		}
		case 1: {
			buildCommandBarHelp();
			break;
		}
		case 2: {
			buildDescriptionHelp();
			break;
		}
		case 3: {
			buildTaskViewHelp();
			break;
		}
		case 4: {
			buildDetailComponentHelp();
			break;
		}
		case 5: {
			buildfloatingBarHelp();
			break;
		}
		}
	}

	public void updateTranslateY(double posY) {

	}

	public void show() {
		if (_stage.isShowing()) {
			hide();
		} else {
			_stage.show();
		}
	}

	public void hide() {
		_stage.hide();
		_selector = 0;
		buildMainHelp();
	}

}
