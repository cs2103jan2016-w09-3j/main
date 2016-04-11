/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *          This class build and manage the search panel where it shows the
 *          search results.
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

public class SearchUserInterface implements ViewInterface {

    private static SearchUserInterface _myInstance;

    private Stage _stage;
    private int _stageWidth;
    private int _stageHeight;
    private int _windowPosX;
    private int _windowPosY;

    private static final String CSS_LABEL = "cssLabelsSearchView";

    // Font
    static final int FONT_SIZE_LABEL = 20;
    static final int FONT_SIZE_LABEL_DATE = 10;
    static final int FONT_SIZE_TASK = 12;
    static final int FONT_SIZE_INDEX = 8;
    private static final Font FONT_LABEL = new Font(PrimaryUserInterface.FONT_TITLE_LABLES, FONT_SIZE_LABEL);
    private static final Font FONT_TASK = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_TASK);

    static final int LABEL_TITLE_HEIGHT = 30;
    static final int LABEL_TASK_HEIGHT = 25;
    private static final int THRESHOLD = 20;

    private String _styleSheet;

    private StackPane _mainVbox;
    private VBox _secondaryVbox;

    // variables to control items in floatingView.
    private int _startIndex = -1;
    private int _endIndex = -1;
    private int _selectedIndex = -1;

    private ArrayList<TaskEntity> _searchList;
    private ArrayList<HBox> _searchBoxes = new ArrayList<HBox>();

    /**
     * Create an instance of SearchUserInterface.
     * 
     * @param primaryStage
     * @param screenBounds
     * @param isFixedSize
     * @param styleSheet
     * @param mouseEvent
     * @return Instance of SearchUserInterface only if there isn't an instance
     *         already.
     */
    public static SearchUserInterface getInstance(Stage primaryStage, Rectangle2D screenBounds,
            boolean isFixedSize, String styleSheet, EventHandler<MouseEvent> mouseEvent) {
        if (_myInstance == null) {
            if (primaryStage == null || screenBounds == null) {
                return null;
            }
            _myInstance = new SearchUserInterface(primaryStage, screenBounds, isFixedSize, styleSheet,
                    mouseEvent);
            return _myInstance;
        }
        return null;
    }

    private SearchUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean isFixedSize,
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
        _mainVbox.setId("cssRootSearchView");

        Scene scene = new Scene(_mainVbox, stageWidth, stageHeight);
        scene.getStylesheets().add(_styleSheet);
        scene.setFill(Color.TRANSPARENT);
        scene.setOnMousePressed(mouseEvent);
        _stage.setScene(scene);
    }

    /**
     * Builds the main skeleton structure, no items added to the structure yet.
     */
    private void buildComponent() {
        _mainVbox.getChildren().clear();
        _secondaryVbox = new VBox();
        _secondaryVbox.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
        _secondaryVbox.setMaxHeight(_stageHeight - LABEL_TITLE_HEIGHT);
        _secondaryVbox.setId("cssFloatingViewSecondaryBox");

        _mainVbox.getChildren().add(_secondaryVbox);
        HBox labelTitle = buildTilteLabel();
        _mainVbox.getChildren().add(labelTitle);
        StackPane.setAlignment(labelTitle, Pos.TOP_LEFT);
        StackPane.setAlignment(_secondaryVbox, Pos.TOP_LEFT);
    }

    /**
     * Check if there are items in the search list and build component
     * accordingly.
     * 
     * @param searchList
     */
    public void buildContent(ArrayList<TaskEntity> searchList) {
        _searchList = searchList;
        _searchBoxes = new ArrayList<HBox>();
        // when there are no floating task yet
        if (_searchList == null || _searchList.size() == 0) {
            buildHelpWithSearch();
        } else {
            buildSearchList(_searchList);
        }
    }

    /**
     * Builds the individual items in the search list and add them to the
     * skeleton component.
     * 
     * @param searchList
     */
    private void buildSearchList(ArrayList<TaskEntity> searchList) {
        _secondaryVbox.getChildren().clear();
        _selectedIndex = 0;
        _startIndex = 0;
        if (searchList.size() < THRESHOLD * 2) {
            _endIndex = searchList.size() - 1;
        } else {
            _endIndex = THRESHOLD * 2;
        }

        for (int i = _startIndex; i <= _endIndex; i++) {
            HBox item = buildIndividualSearchItem(searchList.get(i), i);
            _secondaryVbox.getChildren().add(item);
            _searchBoxes.add(item);
        }
        setSelected(0);
    }

    /**
     * Build a help message whent there are no search results.
     */
    private void buildHelpWithSearch() {
        _secondaryVbox.getChildren().clear();
        Label helpLabel = new Label("Start searching by typing search in the command bar");
        helpLabel.getStyleClass().add(CSS_LABEL);
        helpLabel.setMinWidth(_stageWidth);
        helpLabel.setMinHeight(_stageHeight - LABEL_TITLE_HEIGHT);
        helpLabel.setAlignment(Pos.CENTER);
        _secondaryVbox.getChildren().add(helpLabel);
    }

    /**
     * Builds the individual item base on the input TaskEntity.
     * 
     * @param task
     * @param index
     * @return HBox
     */
    private HBox buildIndividualSearchItem(TaskEntity task, int index) {
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

        if (task.getAssociationState() == TaskEntity.PROJECT_HEAD) {
            top.getChildren().add(StarPane.createStar(LABEL_TASK_HEIGHT));
        }

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

    public HBox buildTilteLabel() {
        HBox titleLableBox = new HBox();
        titleLableBox.setId("cssSearchTitle");
        titleLableBox.setMinWidth(_stageWidth);
        titleLableBox.setMinHeight(LABEL_TITLE_HEIGHT);
        titleLableBox.setMaxHeight(LABEL_TITLE_HEIGHT);

        Label searchTitle = new Label("Search View");
        searchTitle.getStyleClass().add(CSS_LABEL);
        searchTitle.setMinWidth(_stageWidth);
        searchTitle.setFont(FONT_LABEL);
        searchTitle.setMinHeight(LABEL_TITLE_HEIGHT);
        searchTitle.setMaxHeight(LABEL_TITLE_HEIGHT);
        HBox.setMargin(searchTitle, new Insets(0, 0, 0, 30));

        titleLableBox.getChildren().add(searchTitle);
        return titleLableBox;
    }

    /**
     * Updates the selector index by the amount of value. Items are added and
     * removed to maintain THRESHOLD.
     */
    public void update(int value) {
        if (value > 0)// ctrl down
        {
            if (_endIndex + 1 < _searchList.size()) {
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

    /**
     * Adds a new search item in the beginning of the list.
     */
    private void addFirstItem() {
        _startIndex--;
        HBox item = buildIndividualSearchItem(_searchList.get(_startIndex), _startIndex);
        _searchBoxes.add(0, item);
        _secondaryVbox.getChildren().add(0, item);
    }

    /**
     * Removes the last component in the search list.
     */
    private void removeLastTask() {
        _endIndex--;
        HBox itemToRemove = _searchBoxes.remove(_searchBoxes.size() - 1);
        _secondaryVbox.getChildren().remove(itemToRemove);
    }

    /**
     * Adds a new search item in the end of the list.
     */
    private void addLastItem() {
        _endIndex++;
        HBox item = buildIndividualSearchItem(_searchList.get(_endIndex), _endIndex);
        _searchBoxes.add(item);
        _secondaryVbox.getChildren().add(item);
    }

    /**
     * Removes the first component in the search list.
     */
    private void removeFirstTask() {
        _startIndex++;
        HBox item = _searchBoxes.remove(0);
        _secondaryVbox.getChildren().remove(item);
    }

    /**
     * Sets the selected item base on value.
     * 
     * @param value
     */
    public void setSelected(int value) {
        int temp = _selectedIndex + value;
        if (isBetweenRange(temp)) {
            HBox prevItem = _searchBoxes.get(_selectedIndex - _startIndex);
            prevItem.setId("");
            _selectedIndex = temp;
            HBox item = _searchBoxes.get(_selectedIndex - _startIndex);
            item.setId("cssSearchSelected");
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
            posY += _searchBoxes.get(i).getMinHeight();
        }
        return posY;
    }

    public void translateY(double itemHeight) {
        int entireAreaHeight = _stageHeight - LABEL_TITLE_HEIGHT;
        double posY = LABEL_TITLE_HEIGHT;
        if (itemHeight < entireAreaHeight) {

        } else {
            posY -= (itemHeight - entireAreaHeight);
        }
        _secondaryVbox.setTranslateY(posY);
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

    public void show() {
        _stage.show();
    }

    public void hide() {
        _stage.hide();
    }

    public void updateTranslateY(double posY) {
    }

    public void changeTheme(String styleSheet) {
        _stage.getScene().getStylesheets().clear();
        _styleSheet = styleSheet;
        _stage.getScene().getStylesheets().add(styleSheet);
    }

    /**
     * Gets the task that is current selected.
     * 
     * @return TaskEntity
     */
    public TaskEntity processEnter() {
        if (_selectedIndex > -1 && _selectedIndex < _searchList.size()) {
            return _searchList.get(_selectedIndex);
        }
        return null;
    }

    public void destoryStage() {
        _myInstance = null;
        _stage.close();
    }

}
