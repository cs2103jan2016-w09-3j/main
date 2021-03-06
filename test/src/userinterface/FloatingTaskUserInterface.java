/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *          This class build and manage the floating panel where it shows the
 *          floating task.
 */
package userinterface;

import java.util.ArrayList;
import entity.TaskEntity;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FloatingTaskUserInterface implements ViewInterface {

    private static FloatingTaskUserInterface _myInstance;

    private String _styleSheet;

    private Stage _stage;
    private int _stageWidth;
    private int _stageHeight;
    private int _windowPosX;
    private int _windowPosY;

    private StackPane _mainVbox;
    private VBox _secondaryVbox;

    private static final String CSS_LABEL = "cssLabelsFloatingTaskInterface";

    // font
    static final int FONT_SIZE_LABEL = 20;
    static final int FONT_SIZE_LABEL_DATE = 10;
    static final int FONT_SIZE_TASK = 12;
    static final int FONT_SIZE_INDEX = 8;
    private static final Font FONT_LABEL = new Font(PrimaryUserInterface.FONT_TITLE_LABLES, FONT_SIZE_LABEL);
    private static final Font FONT_TASK = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_TASK);

    static final int LABEL_TITLE_HEIGHT = 30;
    static final int LABEL_TASK_HEIGHT = 25;
    private static final int THRESHOLD = 20;

    // variables to control items in floatingView.
    private int _startIndex = -1;
    private int _endIndex = -1;
    private int _selectedIndex = -1;

    private ArrayList<TaskEntity> _floatingList;
    private ArrayList<HBox> _floatingBoxes = new ArrayList<HBox>();

    /**
     * Create an instance of FloatingTaskUserInterface.
     * 
     * @param primaryStage
     * @param screenBounds
     * @param isFixedSize
     * @param styleSheet
     * @param mouseEvent
     * @return Instance of FloatingTaskUserInterface only if there isn't an
     *         instance already.
     */
    public static FloatingTaskUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds,
            boolean isFixedSize, String styleSheet, EventHandler<MouseEvent> mouseEvent) {
        if (_myInstance == null) {
            if (primaryStage == null || screenBounds == null) {
                return null;
            }
            _myInstance = new FloatingTaskUserInterface(primaryStage, screenBounds, isFixedSize, styleSheet,
                    mouseEvent);
            return _myInstance;
        }
        return null;
    }

    private FloatingTaskUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean isFixedSize,
            String styleSheet, EventHandler<MouseEvent> mouseEvent) {
        _styleSheet = styleSheet;
        initializeVaribles(screenBounds, isFixedSize);
        initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight, mouseEvent);
        buildComponent();
    }

    /**
     * Initialize view dimensions and position.
     */
    public void initializeVaribles(Rectangle2D screenBounds, boolean isFixedSize) {
        if (isFixedSize) {
            _stageWidth = (int) screenBounds.getWidth();
            _stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
                    - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_HEIGHT
                    - FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
            _windowPosX = DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN;
            _windowPosY = (int) screenBounds.getHeight() - _stageHeight
                    - PrimaryUserInterface.COMMAND_BAR_HEIGTH - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
        } else {
            _stageWidth = (int) (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE);
            _stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
                    - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_HEIGHT
                    - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
            _windowPosX = (int) (screenBounds.getWidth()
                    - (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2;
            _windowPosY = (int) screenBounds.getHeight() - _stageHeight
                    - PrimaryUserInterface.COMMAND_BAR_HEIGTH - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
        }
    }

    /**
     * Initialize the stage and the components in the stage.
     */
    public void initializeStage(Window owner, int applicationX, int applicationY, int stageWidth,
            int stageHeight, EventHandler<MouseEvent> mouseEvent) {
        _stage = new Stage();
        _stage.initOwner(owner);
        _stage.initStyle(StageStyle.TRANSPARENT);
        _stage.setX(applicationX);
        _stage.setY(applicationY);

        _mainVbox = new StackPane();
        _mainVbox.setPrefSize(stageWidth, stageHeight);
        _mainVbox.setId("cssRootFloatingTaskView");

        Scene scene = new Scene(_mainVbox, stageWidth, stageHeight);
        scene.getStylesheets().add(_styleSheet);
        scene.setFill(Color.TRANSPARENT);
        scene.setOnMousePressed(mouseEvent);
        _stage.setScene(scene);
    }

    /**
     * Updates the selector index by the amount of value. Items are added and
     * removed to maintain THRESHOLD.
     */
    public void update(int value) {
        if (value > 0)// ctrl down
        {
            if (_endIndex + 1 < _floatingList.size()) {
                if (_selectedIndex - _startIndex >= THRESHOLD) {
                    removeFirstTask();
                    addLastItem();
                }
            }
        } else if (value < 0) {
            if (_startIndex > 0) {
                if (_endIndex - _selectedIndex >= THRESHOLD) {
                    removeLastTask();
                    addFirstItem();
                }
            }
        }
    }

    private void addFirstItem() {
        _startIndex--;
        HBox item = buildIndividualFloating(_floatingList.get(_startIndex), _startIndex);
        _floatingBoxes.add(0, item);
        _secondaryVbox.getChildren().add(0, item);
    }

    private void removeLastTask() {
        _endIndex--;
        HBox itemToRemove = _floatingBoxes.remove(_floatingBoxes.size() - 1);
        _secondaryVbox.getChildren().remove(itemToRemove);
    }

    private void addLastItem() {
        _endIndex++;
        HBox item = buildIndividualFloating(_floatingList.get(_endIndex), _endIndex);
        _floatingBoxes.add(item);
        _secondaryVbox.getChildren().add(item);
    }

    private void removeFirstTask() {
        _startIndex++;
        HBox item = _floatingBoxes.remove(0);
        _secondaryVbox.getChildren().remove(item);
    }

    public void updateTranslateY(double posY) {
    }

    public void show() {
        _stage.show();
    }

    public void hide() {
        _stage.hide();
    }

    /**
     * Build the main structure of the component.
     */
    public void buildComponent() {

        _mainVbox.getChildren().clear();
        _secondaryVbox = new VBox();
        _secondaryVbox.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
        _secondaryVbox.setMaxHeight(_stageHeight - LABEL_TITLE_HEIGHT);

        _mainVbox.getChildren().add(_secondaryVbox);
        HBox labelTitle = buildTilteLabel();
        _mainVbox.getChildren().add(labelTitle);
        StackPane.setAlignment(labelTitle, Pos.TOP_LEFT);
        StackPane.setAlignment(_secondaryVbox, Pos.TOP_LEFT);
    }

    /**
     * Check if there are any floating task and build the components base on
     * floatingList size.
     * 
     * @param floatingList
     * @param index
     */
    public void buildContent(ArrayList<TaskEntity> floatingList, int index) {
        _floatingList = floatingList;
        _floatingBoxes = new ArrayList<HBox>();
        // when there are no floating task yet
        if (_floatingList == null || _floatingList.size() == 0) {
            buildHelpWithFloating();
        } else {
            buildFloatingList(_floatingList, index);
        }
    }

    public HBox buildTilteLabel() {
        HBox titleLableBox = new HBox();
        titleLableBox.setId("cssFloatingTaskViewTitle");
        titleLableBox.setMinWidth(_stageWidth);
        titleLableBox.setMinHeight(LABEL_TITLE_HEIGHT);
        titleLableBox.setMaxHeight(LABEL_TITLE_HEIGHT);

        Label floatingTitle = new Label("Floating View");
        floatingTitle.getStyleClass().add(CSS_LABEL);
        floatingTitle.setMinWidth(_stageWidth);
        floatingTitle.setFont(FONT_LABEL);
        floatingTitle.setMinHeight(LABEL_TITLE_HEIGHT);
        floatingTitle.setMaxHeight(LABEL_TITLE_HEIGHT);
        HBox.setMargin(floatingTitle, new Insets(0, 0, 0, 30));

        titleLableBox.getChildren().add(floatingTitle);
        return titleLableBox;
    }

    /**
     * Builds the component when there are no floating task.
     */
    public void buildHelpWithFloating() {
        _secondaryVbox.getChildren().clear();
        Label helpLabel = new Label("You do not have any floating task yet.");
        helpLabel.getStyleClass().add(CSS_LABEL);
        helpLabel.setMinWidth(_stageWidth);
        helpLabel.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
        helpLabel.setAlignment(Pos.CENTER);
        _secondaryVbox.getChildren().add(helpLabel);
    }

    public void buildFloatingList(ArrayList<TaskEntity> floatingList, int index) {
        _secondaryVbox.getChildren().clear();
        if (index < floatingList.size() && index > -1) {
            _selectedIndex = index;
        } else {
            _selectedIndex = 0;
        }
        _startIndex = 0;

        if (floatingList.size() < THRESHOLD * 2) {
            _endIndex = floatingList.size() - 1;
        } else {
            _endIndex = THRESHOLD * 2;
        }

        for (int i = _startIndex; i <= _endIndex; i++) {
            HBox item = buildIndividualFloating(floatingList.get(i), i);
            _secondaryVbox.getChildren().add(item);
            _floatingBoxes.add(item);
        }
        setSelected(0);
    }

    /**
     * Build the individual component for each floating task.
     * 
     * @param task
     * @param index
     * @return HBox
     */
    public HBox buildIndividualFloating(TaskEntity task, int index) {
        HBox parentBox = new HBox();
        VBox parentBoxChild = new VBox();
        parentBoxChild.setMinWidth(_stageWidth);
        parentBoxChild.setMaxWidth(_stageWidth);

        HBox top = new HBox();
        top.setMinWidth(_stageWidth);
        top.setMaxWidth(_stageWidth);

        Label indexLabel = new Label("ID" + Integer.toString(index));
        indexLabel.getStyleClass().add(CSS_LABEL);
        indexLabel.setMinHeight(LABEL_TASK_HEIGHT);
        indexLabel.setMinWidth(60);
        indexLabel.setAlignment(Pos.CENTER);
        indexLabel.setFont(Font.font(PrimaryUserInterface.FONT_DEFAULT, FontWeight.BOLD, FONT_SIZE_TASK));
        top.getChildren().add(indexLabel);

        Label timeLabel = new Label();
        timeLabel.setText(task.getTime());
        timeLabel.getStyleClass().add(CSS_LABEL);
        timeLabel.setMinHeight(LABEL_TASK_HEIGHT);
        timeLabel.setAlignment(Pos.CENTER);
        timeLabel.setFont(FONT_TASK);
        HBox.setMargin(timeLabel, new Insets(0, 10, 0, 0));
        top.getChildren().add(timeLabel);

        Label nameLabel = new Label();
        nameLabel.getStyleClass().add(CSS_LABEL);
        nameLabel.setText(task.getName());
        nameLabel.setMinHeight(LABEL_TASK_HEIGHT);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setFont(FONT_TASK);
        HBox.setMargin(nameLabel, new Insets(0, 10, 0, 0));
        top.getChildren().add(nameLabel);
        top.setMinHeight(LABEL_TASK_HEIGHT);

        HBox mid = new HBox();
        Label indexPlaceHolder = new Label();
        indexPlaceHolder.getStyleClass().add(CSS_LABEL);
        indexPlaceHolder.setMinWidth(50);
        mid.getChildren().add(indexPlaceHolder);

        Text description = new Text();
        description.getStyleClass().add(CSS_LABEL);
        description.setText(task.getDescription());
        description.setWrappingWidth(_stageWidth - 50);
        mid.getChildren().add(description);
        mid.setMinHeight(description.getBoundsInLocal().getHeight() + 10);

        HBox btm = new HBox();
        Label indexPlaceHolder2 = new Label();
        indexPlaceHolder2.getStyleClass().add(CSS_LABEL);
        indexPlaceHolder2.setMinWidth(50);
        btm.getChildren().add(indexPlaceHolder2);

        Text hashtags = new Text();
        hashtags.getStyleClass().add(CSS_LABEL);
        hashtags.setText(task.getHashtags());
        hashtags.setWrappingWidth(_stageWidth - 50);
        btm.getChildren().add(hashtags);
        btm.setMinHeight(hashtags.getBoundsInLocal().getHeight() + 10);

        parentBoxChild.getChildren().add(top);
        parentBoxChild.getChildren().add(mid);
        parentBoxChild.getChildren().add(btm);
        parentBoxChild.setMinHeight(top.getMinHeight() + mid.getMinHeight() + btm.getMinHeight());
        parentBox.getChildren().add(parentBoxChild);
        parentBox.setMinHeight(parentBoxChild.getMinHeight());
        return parentBox;
    }

    /**
     * Sets the selected item base on value.
     * 
     * @param value
     */
    public void setSelected(int value) {
        int temp = _selectedIndex + value;
        if (isBetweenRange(temp)) {
            HBox prevItem = _floatingBoxes.get(_selectedIndex - _startIndex);
            prevItem.setId("");
            _selectedIndex = temp;
            HBox item = _floatingBoxes.get(_selectedIndex - _startIndex);
            item.setId("cssFloatingTaskViewSelected");
            translateY(calculateTopHeight(_selectedIndex - _startIndex));
        }
    }

    /**
     * Calculates the height above the selected component.
     * 
     * @param index
     * @return height
     */
    public double calculateTopHeight(int index) {
        double posY = 0;
        for (int i = 0; i <= index; i++) {
            posY += _floatingBoxes.get(i).getMinHeight();
        }
        return posY;
    }

    public void translateY(double itemTopHeight) {
        double posY = -LABEL_TITLE_HEIGHT;
        int entireAreaHeight = _stageHeight - LABEL_TITLE_HEIGHT;
        if (itemTopHeight + LABEL_TASK_HEIGHT > entireAreaHeight) {
            posY += itemTopHeight + LABEL_TASK_HEIGHT - entireAreaHeight;
        } else if (itemTopHeight < entireAreaHeight) {

        }
        _secondaryVbox.setTranslateY(-posY);
    }

    /**
     * Check if the index is between _startIndex and _endIndex.
     * 
     * @param index
     * @return true only if index is bewteen the range
     */
    public boolean isBetweenRange(int index) {
        if (index >= _startIndex && index <= _endIndex) {
            return true;
        }
        return false;
    }

    public StackPane getMainLayoutComponent() {
        return _mainVbox;
    }

    public void changeTheme(String styleSheet) {
        _stage.getScene().getStylesheets().clear();
        _styleSheet = styleSheet;
        _stage.getScene().getStylesheets().add(styleSheet);
    }

    public void destoryStage() {
        _myInstance = null;
        _stage.close();
    }
}
