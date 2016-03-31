package userInterface;

import java.util.ArrayList;
import entity.DescriptionLabel;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class DescriptionComponent implements ViewInterface {

	static final int CONPONENT_WIDTH = 50;
	static final int CONPONENT_RIGHT_MARGIN = 0;
	private static final int POSITION_ZERO = 0;

	private final double LABEL_SIZE_LARGE = 200;
	private final double LABEL_SIZE_MEDIUM = 100;
	private final double LABEL_SIZE_SMALL = 24;

	private Stage _stage;
	private int _stageWidth;
	private int _stageHeight;
	private int _windowPosX;
	private int _windowPosY;

	private GridPane _mainVbox;
	private double _translationY = 0;
	private int _currentView;

	public DescriptionComponent(Stage parentStage, Rectangle2D screenBounds, boolean fixedSize) {
		initializeVaribles(screenBounds, fixedSize);
		initializeStage(parentStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
	}

	public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
		if (fixedSize) {
			_stageWidth = CONPONENT_WIDTH;
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = POSITION_ZERO;
			_windowPosY = (int) screenBounds.getHeight() - _stageHeight - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
		} else {
			_stageWidth = CONPONENT_WIDTH;
			_stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
					- PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_HEIGHT - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
					- FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
			_windowPosX = (int) (screenBounds.getWidth()
					- (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2;
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

		StackPane mainPanel = new StackPane();
		mainPanel.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
		mainPanel.setId("cssRootDescriptionViewMainBackground");;
		mainPanel.setPrefSize(_stageWidth, _stageHeight);
		mainPanel.setAlignment(Pos.TOP_RIGHT);
		
		_mainVbox = new GridPane();
		_mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);

		mainPanel.getChildren().add(_mainVbox);
		
		Scene scene = new Scene(mainPanel, windowWidth, windowHeight, Color.TRANSPARENT);
		_stage.setScene(scene);
	}

	public void buildComponent(ArrayList<DescriptionLabel> descriptionLabels, int view) {
		_currentView = view;
		_mainVbox.getChildren().clear();
		double totalBuildedHeight = 0;
		if (descriptionLabels != null) {
			for (int i = 0; i < descriptionLabels.size(); i++) {
				_mainVbox.add(buildIndividualLabel(descriptionLabels.get(i), totalBuildedHeight), 0, i);
				totalBuildedHeight += descriptionLabels.get(i).getHeight();
			}
		}
	}

	public StackPane buildIndividualLabel(DescriptionLabel dLabel, double totalBuildedHeight) {
		StackPane s = new StackPane();
		s.setMinHeight(dLabel.getHeight());
		s.setMinWidth(CONPONENT_WIDTH);
		VBox labelBox = buildLabel(dLabel, totalBuildedHeight);
		s.getChildren().add(buildGradientRec(CONPONENT_WIDTH, dLabel.getHeight(), dLabel.isSelected()));
		s.getChildren().add(labelBox);
		return s;
	}

	public VBox buildLabel(DescriptionLabel dLabel, double totalBuildedHeight) {
		VBox vbox = new VBox();
		vbox.setMinHeight(dLabel.getHeight());
		vbox.setMinWidth(CONPONENT_WIDTH);
		double posYStart = _translationY + totalBuildedHeight;
		double posYEnd = posYStart + dLabel.getHeight();
		createLabelBaseOnHeight(posYStart, posYEnd, vbox, dLabel);
		return vbox;
	}

	public Rectangle buildGradientRec(double width, double height, boolean isSelected) {
		Stop[] stops;
		if (isSelected) {
			stops = new Stop[] {new Stop(0, new Color(1, 0.7, 0.5, 0)),new Stop(0.1, new Color(1, 0.7, 0.5, 0.90)), new Stop(1, new Color(1, 0.7, 0.5, 0.95))};
		} else {
			stops = new Stop[] { new Stop(0, new Color(1,1,1,0)),new Stop(0.1, new Color(1,1,1,0.9)), new Stop(1, new Color(1, 1, 1, 0.9)) };
		}
		LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		Rectangle rec = new Rectangle(0, 0, width, height);
		rec.setFill(lg1);
		return rec;
	}

	public void createLabelBaseOnHeight(double posYStart, double posYEnd, VBox vbox, DescriptionLabel dLabel) {
		Label main = new Label();
		Label extra = new Label();
		if ((posYStart >= 0 && posYStart < _stageHeight) || (posYEnd <= _stageHeight && posYEnd > 0)) {
			// partially or fully inside screen
			if (posYStart >= 0 && posYEnd <= _stageHeight) {
				// fully in screen
				main.setMinHeight(posYEnd - posYStart);
				vbox.getChildren().add(buildLabelBaseOnHeight(main, dLabel, posYEnd - posYStart));
			} else if (posYStart < 0) {
				// tail in screen only
				extra.setMinHeight(-posYStart);
				vbox.getChildren().add(extra);
				main.setMinHeight(posYEnd);
				vbox.getChildren().add(buildLabelBaseOnHeight(main, dLabel, posYEnd));
			} else if (posYStart >= 0) {
				// head in screen only
				main.setMinHeight(_stageHeight - posYStart);
				vbox.getChildren().add(buildLabelBaseOnHeight(main, dLabel, _stageHeight - posYStart));
				extra.setMinHeight(posYEnd - _stageHeight);
				vbox.getChildren().add(extra);
			}
		} else if (posYStart <= 0 && posYEnd >= _stageHeight) {
			// body in screen, head or tail or head and tail not in screen
			Label tempLabel = new Label();
			tempLabel.setMinHeight(-posYStart);
			vbox.getChildren().add(tempLabel);
			main.setMinHeight(_stageHeight);
			vbox.getChildren().add(buildLabelBaseOnHeight(main, dLabel, _stageHeight));
			extra.setMinHeight(posYEnd - _stageHeight);
			vbox.getChildren().add(extra);
		}
	}

	public Label buildLabelBaseOnHeight(Label label, DescriptionLabel dLabel, double height) {
		if (_currentView == UserInterfaceController.TASK_VIEW) {
			return setLabelForTaskView(label, dLabel, height);
		} else {
			return setLabelForDetailedView(label, dLabel, height);
		}
	}

	private Label setLabelForDetailedView(Label label, DescriptionLabel dLabel, double height) {
		if (height > LABEL_SIZE_MEDIUM) {
			label.setMinHeight(CONPONENT_WIDTH);
			label.setMinWidth(height);
			label.setRotate(270);
			double translationX = -(height / 2) + CONPONENT_WIDTH / 2;
			double translationY = (height / 2) - CONPONENT_WIDTH / 2;
			label.setTranslateX(translationX);
			label.setTranslateY(translationY);
			if (height > LABEL_SIZE_LARGE) {
				label.setText(dLabel.getFullDayLabel());
			} else {
				label.setText(dLabel.getMediumDayLabel());
			}
		} else if (height <= LABEL_SIZE_MEDIUM) {
			label.setText(dLabel.getSmallDayLabel());
			label.setMinWidth(CONPONENT_WIDTH);
		}
		label.setAlignment(Pos.CENTER);
		return label;
	}

	private Label setLabelForTaskView(Label label, DescriptionLabel dLabel, double height) {
		if (height > LABEL_SIZE_MEDIUM) {
			label.setMinHeight(CONPONENT_WIDTH);
			label.setMinWidth(height);
			label.setRotate(270);
			double translationX = -(height / 2) + CONPONENT_WIDTH / 2;
			double translationY = (height / 2) - CONPONENT_WIDTH / 2;
			label.setTranslateX(translationX);
			label.setTranslateY(translationY);
			if (height > LABEL_SIZE_LARGE) {
				label.setText(dLabel.getFullWeekLabel());
			} else {
				label.setText(dLabel.getMediumWeekLabel());
			}
		} else if (height <= LABEL_SIZE_MEDIUM) {
			label.setText(dLabel.getSmallWeekLabel());
			label.setMinWidth(CONPONENT_WIDTH);
		}
		label.setAlignment(Pos.CENTER);
		return label;
	}

	public void updateTranslateY(double value) {
		_translationY = value;
		_mainVbox.setTranslateY(value);
	}

	public void show() {
		_stage.show();
	}

	public void hide() {
		_stage.hide();
	}

	public GridPane getMainVBox() {
		return _mainVbox;
	}

	public void update(int value) {
	}

	public void destoryStage() {
		_stage.close();
	}

}
