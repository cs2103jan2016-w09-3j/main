//@@author A0125514N
package userInterface;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class HelpScreenUserInterface implements ViewInterface {

	private static HelpScreenUserInterface _myInstance;

	private static final String HELP_DESCRIPTION_FLOATING_BAR_TITLE = "Floating Panel";
	private static final String HELP_DESCRIPTION_FLOATING_BAR = "This panel shows you a task that does not has a date assigned to it. It changes every 10 seconds.";

	private static final String HELP_DESCRIPTION_MAIN_TITLE = "Welcome to PCNM help manual!";
	private static final String HELP_DESCRIPTION_MAIN = "Press the left and right arrow keys to scroll through the help and F1 to close the help screen";

	private static final String HELP_DESCRIPTION_DESCRIPTION_COMPONENT_TITLE = "Description Panel";
	private static final String HELP_DESCRIPTION_DESCRIPTION_COMPONENT = "This panel shows you range of the date of the tasks in the week.";

	private static final String HELP_DESCRIPTION_TASK_VIEW_TITLE = "Main View";
	private static final String HELP_DESCRIPTION_TASK_VIEW = "This panel shows you your task in chronological order.";

	private static final String HELP_DESCRIPTION_DETAIL_COMPONENT_TITLE = "Details Panel";
	private static final String HELP_DESCRIPTION_DETAIL_COMPONENT = "This panel shows you more description of the selected task.";

	private static final String[][] CHEAT_SHEET_SHORT_CUTS = { { "F1", "Brings up the help manual!" },
			{ "F2", "Switch the application to the compact view" },
			{ "F3", "Switch the application to the full view." }, { "Up/Down", "Look up previous command entered." },
			{ "Ctrl + Up/Down", "Scroll through your task in the selected view." },
			{ "Ctrl + Left/Right", "Change view from the main view to other views." }, { "", "" } };
	private static final String[][] CHEAT_SHEET_COMMANDS = {
			{ "ADD", "Add a task into your task list. Task with out dates will be considered floating task." },
			{ "DELETE", "Delete a task with the specific id." }, { "EDIT", "Edit your task base on the id." },
			{ "LINK",
					"Link 2 task together to create associations between the tasks. \neg. Link ID1-ID5, id5 will be the project head of the link." },
			{ "SEARCH", "Search for any task base on the input." },
			{ "HIDE", "Switch the applcaition to compact view." }, { "SHOW", "Switch the applciaiton to full view." },
			{ "FLOAT", "Change view to the floating view." }, { "MAIN", "Change view to the main view." },
			{ "THEME", "Use to select Theme." },
			{ "SAVETO", "Change the directory of where the saved file will be stored." },
			{ "LOADFROM", "Load an existing file." } };
	private static final String HELP_DESCRIPTION_COMMAND_BAR_TITLE = "Command Bar";
	private static final String HELP_DESCRIPTION_COMMAND_BAR = "This is where u type your input commands.";

	// font
	private static final int FONT_SIZE_LABEL_TITLE = 18;
	private static final Font FONT_LABEL_TITLE = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_LABEL_TITLE);
	private static final int FONT_SIZE_LABEL = 14;
	private static final Font FONT_LABEL = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_LABEL);

	private static final int MARGIN = 10;
	private static final int MAX_ITEMS = 6;

	private static final int DESCRIPTION_BOX_WIDTH = 300;
	private static final int DESCRIPTION_BOX_SMALL_WIDTH = 200;
	private static final int DESCRIPTION_BOX_HEIGHT = 150;
	private static final int LINE_SIZE = 2;
	private static final int LINE_LENGTH = 20;

	private String _styleSheet;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;
	private boolean _isFixed;
	private Rectangle2D _screenBounds;
	private double _taskViewWidth;

	private StackPane _mainPanel;
	private Pane[] _items = new Pane[MAX_ITEMS];

	private int _selector = 0;

	public static HelpScreenUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize,
			String styleSheet, EventHandler<MouseEvent> mouseEvent) {
		if (_myInstance == null) {
			if (primaryStage == null || screenBounds == null) {
				return null;
			}
			_myInstance = new HelpScreenUserInterface(primaryStage, screenBounds, fixedSize, styleSheet, mouseEvent);
			return _myInstance;
		}
		return null;
	}

	public HelpScreenUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize, String styleSheet,
			EventHandler<MouseEvent> mouseEvent) {
		_styleSheet = styleSheet;
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight, mouseEvent);
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

	public void initializeStage(Window owner, int applicationX, int applicationY, int windowWidth, int windowHeight,
			EventHandler<MouseEvent> mouseEvent) {
		_stage = new Stage();
		_stage.initOwner(owner);
		_stage.initStyle(StageStyle.TRANSPARENT);
		_stage.setX(applicationX);
		_stage.setY(applicationY);

		_mainPanel = new StackPane();
		_mainPanel.setId("cssHelpScreenRoot");
		_mainPanel.setPrefSize(windowWidth, windowHeight);
		_mainPanel.setAlignment(Pos.TOP_CENTER);

		Scene scene = new Scene(_mainPanel, windowWidth, windowHeight);
		scene.getStylesheets().add(_styleSheet);
		scene.setFill(Color.TRANSPARENT);
		scene.setOnMousePressed(mouseEvent);

		_stage.setScene(scene);
		buildComponent();
	}

	public void buildComponent() {
		_items[0] = buildMainHelp();
		_items[1] = buildfloatingBarHelp();
		_items[2] = buildDescriptionHelp();
		_items[3] = buildTaskViewHelp();
		_items[4] = buildDetailComponentHelp();
		_items[5] = buildCommandBarHelp();
		_mainPanel.getChildren().add(_items[0]);
	}

	public VBox buildMainHelp() {
		double minWidth = _stageWidth * 0.8;
		VBox main = new VBox();
		main.setAlignment(Pos.CENTER);

		VBox descriptionBox = new VBox();
		descriptionBox.setMaxWidth(minWidth);
		descriptionBox.setMinWidth(minWidth);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label title = new Label(HELP_DESCRIPTION_MAIN_TITLE);
		title.setFont(FONT_LABEL_TITLE);
		title.setId("cssHelpTitle");
		title.setMinWidth(minWidth - MARGIN * 2);
		title.setAlignment(Pos.CENTER);
		descriptionBox.getChildren().add(title);

		Label descLabel = new Label(HELP_DESCRIPTION_MAIN);
		descLabel.setMinWidth(minWidth - MARGIN * 2);
		descLabel.setFont(FONT_LABEL);
		descLabel.setAlignment(Pos.CENTER);
		descLabel.setWrapText(true);
		VBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		VBox.setMargin(title, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		Label titleCheatSheet = new Label("Cheat Sheet");
		titleCheatSheet.setFont(FONT_LABEL_TITLE);
		titleCheatSheet.setId("cssHelpTitle");
		titleCheatSheet.setMinWidth(minWidth - MARGIN * 2);
		titleCheatSheet.setAlignment(Pos.CENTER);
		VBox.setMargin(titleCheatSheet, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(titleCheatSheet);

		GridPane gp = new GridPane();
		Label shortcutsTitle = new Label("Short-cut Keys");
		shortcutsTitle.setMinWidth(120);
		Label shortcutDesriptionTitle = new Label("Description");
		gp.add(shortcutsTitle, 0, 0);
		gp.add(shortcutDesriptionTitle, 1, 0);
		int rowCount = 1;
		for (int i = 0; i < CHEAT_SHEET_SHORT_CUTS.length; i++) {
			Label shortcuts = new Label(CHEAT_SHEET_SHORT_CUTS[i][0]);
			shortcuts.setFont(FONT_LABEL);
			gp.add(shortcuts, 0, i + 1);
			Label shortcutsDescription = new Label(CHEAT_SHEET_SHORT_CUTS[i][1]);
			shortcutsDescription.setFont(FONT_LABEL);
			gp.add(shortcutsDescription, 1, i + 1);
			rowCount = i;
		}
		rowCount++;

		Label commandsTitle = new Label("Commands");
		GridPane.setMargin(commandsTitle, new Insets(MARGIN, 0, 0, 0));
		Label commandsDesriptionTitle = new Label("Description");
		GridPane.setMargin(commandsDesriptionTitle, new Insets(MARGIN, 0, 0, 0));
		gp.add(commandsTitle, 0, rowCount);
		gp.add(commandsDesriptionTitle, 1, rowCount);
		rowCount++;

		for (int i = 0; i < CHEAT_SHEET_COMMANDS.length; i++) {
			Label shortcuts = new Label(CHEAT_SHEET_COMMANDS[i][0]);
			shortcuts.setFont(FONT_LABEL);
			gp.add(shortcuts, 0, rowCount);
			Label shortcutsDescription = new Label(CHEAT_SHEET_COMMANDS[i][1]);
			shortcutsDescription.setFont(FONT_LABEL);
			gp.add(shortcutsDescription, 1, rowCount);
			rowCount++;
		}

		VBox.setMargin(gp, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(gp);

		main.getChildren().add(descriptionBox);
		return main;
	}

	public HBox buildDetailComponentHelp() {
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
		link.setMinHeight(LINE_SIZE);
		link.setMaxHeight(LINE_SIZE);
		link.setMinWidth(LINE_LENGTH);
		link.setId("cssHelpComponentLinker");

		VBox descriptionBox = new VBox();
		descriptionBox.setMaxWidth(200);
		descriptionBox.setMaxHeight(150);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label title = new Label(HELP_DESCRIPTION_DETAIL_COMPONENT_TITLE);
		title.setFont(FONT_LABEL_TITLE);
		title.setId("cssHelpTitle");
		descriptionBox.getChildren().add(title);

		Label descLabel = new Label(HELP_DESCRIPTION_DETAIL_COMPONENT);
		descLabel.setFont(FONT_LABEL);
		descLabel.setWrapText(true);
		VBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		VBox.setMargin(title, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(descriptionBox);
		main.getChildren().add(link);
		main.getChildren().add(hbox);
		main.setTranslateX(_taskViewWidth + DescriptionComponent.CONPONENT_WIDTH
				+ DescriptionComponent.CONPONENT_RIGHT_MARGIN - 220);
		main.setTranslateY(FloatingBarViewUserInterface.COMPONENT_HEIGHT);
		return main;
	}

	public HBox buildTaskViewHelp() {
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

		VBox descriptionBox = new VBox();
		descriptionBox.setMaxWidth(DESCRIPTION_BOX_SMALL_WIDTH);
		descriptionBox.setMaxHeight(DESCRIPTION_BOX_HEIGHT);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label title = new Label(HELP_DESCRIPTION_TASK_VIEW_TITLE);
		title.setFont(FONT_LABEL_TITLE);
		title.setId("cssHelpTitle");
		descriptionBox.getChildren().add(title);

		Label descLabel = new Label(HELP_DESCRIPTION_TASK_VIEW);
		descLabel.setWrapText(true);
		descLabel.setFont(FONT_LABEL);
		VBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		VBox.setMargin(title, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(hbox);
		main.getChildren().add(link);
		main.getChildren().add(descriptionBox);
		main.setTranslateX(DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN);
		main.setTranslateY(FloatingBarViewUserInterface.COMPONENT_HEIGHT);
		return main;
	}

	public HBox buildDescriptionHelp() {
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
		link.setMinHeight(LINE_SIZE);
		link.setMaxHeight(LINE_SIZE);
		link.setMinWidth(LINE_LENGTH);
		link.setId("cssHelpComponentLinker");

		VBox descriptionBox = new VBox();
		descriptionBox.setMaxWidth(DESCRIPTION_BOX_WIDTH);
		descriptionBox.setMaxHeight(DESCRIPTION_BOX_HEIGHT);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label title = new Label(HELP_DESCRIPTION_DESCRIPTION_COMPONENT_TITLE);
		title.setFont(FONT_LABEL_TITLE);
		title.setId("cssHelpTitle");
		descriptionBox.getChildren().add(title);

		Label descLabel = new Label(HELP_DESCRIPTION_DESCRIPTION_COMPONENT);
		descLabel.setFont(FONT_LABEL);
		descLabel.setWrapText(true);
		VBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		VBox.setMargin(title, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(hbox);
		main.getChildren().add(link);
		main.getChildren().add(descriptionBox);
		main.setTranslateY(FloatingBarViewUserInterface.COMPONENT_HEIGHT);
		return main;
	}

	public VBox buildfloatingBarHelp() {
		VBox main = new VBox();
		main.setAlignment(Pos.TOP_CENTER);

		HBox hbox = new HBox();
		hbox.setPrefSize(_stageWidth, FloatingBarViewUserInterface.COMPONENT_HEIGHT);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setPrefHeight(LINE_LENGTH);
		link.setMaxWidth(LINE_SIZE);
		link.setId("cssHelpComponentLinker");

		VBox descriptionBox = new VBox();
		descriptionBox.setMaxWidth(DESCRIPTION_BOX_WIDTH);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label title = new Label(HELP_DESCRIPTION_FLOATING_BAR_TITLE);
		title.setFont(FONT_LABEL_TITLE);
		title.setId("cssHelpTitle");
		descriptionBox.getChildren().add(title);

		Label descLabel = new Label(HELP_DESCRIPTION_FLOATING_BAR);
		descLabel.setFont(FONT_LABEL);
		descLabel.setWrapText(true);
		VBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		VBox.setMargin(title, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(hbox);
		main.getChildren().add(link);
		main.getChildren().add(descriptionBox);
		return main;
	}

	public VBox buildCommandBarHelp() {
		VBox main = new VBox();
		main.setAlignment(Pos.TOP_CENTER);

		HBox hbox = new HBox();
		hbox.setPrefSize(_stageWidth, PrimaryUserInterface.COMMAND_BAR_HEIGTH);
		hbox.setId("cssHelpComponentHighLighter");

		HBox link = new HBox();
		link.setPrefHeight(LINE_LENGTH);
		link.setMaxWidth(LINE_SIZE);
		link.setId("cssHelpComponentLinker");

		VBox descriptionBox = new VBox();
		descriptionBox.setMaxWidth(DESCRIPTION_BOX_WIDTH);
		descriptionBox.setMaxHeight(DESCRIPTION_BOX_HEIGHT);
		descriptionBox.setMinHeight(DESCRIPTION_BOX_HEIGHT);
		descriptionBox.setId("cssHelpComponentDescriptionBox");

		Label title = new Label(HELP_DESCRIPTION_COMMAND_BAR_TITLE);
		title.setFont(FONT_LABEL_TITLE);
		title.setId("cssHelpTitle");
		descriptionBox.getChildren().add(title);

		Label descLabel = new Label(HELP_DESCRIPTION_COMMAND_BAR);
		descLabel.setFont(FONT_LABEL);
		descLabel.setWrapText(true);
		VBox.setMargin(descLabel, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		VBox.setMargin(title, new Insets(MARGIN, MARGIN, MARGIN, MARGIN));
		descriptionBox.getChildren().add(descLabel);

		main.getChildren().add(descriptionBox);
		main.getChildren().add(link);
		main.getChildren().add(hbox);
		main.setTranslateY(
				_stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH - DESCRIPTION_BOX_HEIGHT - LINE_LENGTH);

		return main;
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
		_mainPanel.getChildren().clear();
		_mainPanel.getChildren().add(_items[_selector]);
	}

	public void updateTranslateY(double posY) {

	}

	public void show() {
		if (_stage.isShowing()) {
			hide();
		} else {
			_stage.show();
			updateHelpPrompt();
		}
	}

	public void hide() {
		_stage.hide();
		_selector = 0;
		updateHelpPrompt();
	}

	public void changeTheme(String styleSheet) {
		_stage.getScene().getStylesheets().clear();
		_styleSheet = styleSheet;
		_stage.getScene().getStylesheets().add(styleSheet);
	}

}
