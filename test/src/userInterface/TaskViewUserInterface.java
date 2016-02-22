
package userInterface;

import java.util.ArrayList;
import java.util.Calendar;

import entity.DescriptionLabel;
import entity.Task;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class TaskViewUserInterface implements ViewInterface {

    static final int LABEL_HEIGHT = 30;
    static final int ITEM_HEIGHT = 24;
    static final int LABEL_FONT_SIZE = 12;
    static final int TASK_FONT_SIZE = 12;

    private static final int GAP_SIZE = 10;
    private static final int THRESHOLD = 50;

    private Stage _stage;
    private int _stageWidth;
    private int _stageHeight;
    private int _windowPosX;
    private int _windowPosY;

    private int _startIndex = -1;
    private int _endIndex = -1;
    private int _selectedIndex = -1;
    private int _individualItemWidth = -1;
    private double transLationY = 0;

    private VBox _mainVbox; // main parent for items
    private ArrayList<GridPane> _gridPanes = new ArrayList<GridPane>();
    private ArrayList<Task> workingList;

    public TaskViewUserInterface(Stage primaryStage, Rectangle2D screenBounds, boolean fixedSize) {
        initializeVaribles(screenBounds, fixedSize);
        initializeStage(primaryStage, _windowPosX, _windowPosY, _stageWidth, _stageHeight);
    }

    public void initializeVaribles(Rectangle2D screenBounds, boolean fixedSize) {
        if (fixedSize) {
            _stageWidth = (int) screenBounds.getWidth() - DescriptionComponent.CONPONENT_WIDTH
                    - DescriptionComponent.CONPONENT_RIGHT_MARGIN;
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
            _stageWidth = (int) (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)
                    - DescriptionComponent.CONPONENT_WIDTH - DescriptionComponent.CONPONENT_RIGHT_MARGIN;
            _stageHeight = (int) (screenBounds.getHeight() - PrimaryUserInterface.COMMAND_BAR_HEIGTH
                    - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_HEIGHT
                    - FloatingBarViewUserInterface.COMPONENT_TOP_MARGIN
                    - FloatingBarViewUserInterface.COMPONENT_BOTTOM_MARGIN);
            _windowPosX = (int) (screenBounds.getWidth()
                    - (screenBounds.getWidth() * PrimaryUserInterface.PREFERED_WINDOW_SCALE)) / 2
                    + DescriptionComponent.CONPONENT_WIDTH + DescriptionComponent.CONPONENT_RIGHT_MARGIN;
            _windowPosY = (int) screenBounds.getHeight() - _stageHeight
                    - PrimaryUserInterface.COMMAND_BAR_HEIGTH - PrimaryUserInterface.COMMAND_BAR_TOP_MARGIN
                    - PrimaryUserInterface.COMMAND_BAR_BOTTOM_MARGIN;
        }
    }

    public void initializeStage(Window owner, int applicationX, int applicationY, int stageWidth,
            int stageHeight) {
        _stage = new Stage();
        _stage.initOwner(owner);
        _stage.initStyle(StageStyle.UNDECORATED);
        _stage.setX(applicationX);
        _stage.setY(applicationY);

        _mainVbox = new VBox();
        _mainVbox.setPrefSize(stageWidth, stageHeight);
        _mainVbox.getStylesheets().add(PrimaryUserInterface.STYLE_SHEET);
        _stage.setScene(new Scene(_mainVbox, stageWidth, stageHeight));
    }

    public void show() {
        _stage.show();
    }

    public void hide() {
        _stage.hide();
    }

    public VBox getMainLayoutComponent() {
        return _mainVbox;
    }

    public int get_stageWidth() {
        return _stageWidth;
    }

    public int get_stageHeight() {
        return _stageHeight;
    }

    public void buildComponent(ArrayList<Task> workingList) {
        this.workingList = workingList;
        _individualItemWidth = _stageWidth;
        int workingIndex = 30; // getWorkingIndex(); when qy implements 
        int startIndex = 0;
        int endIndex = workingList.size() - 1;
        if (workingIndex - THRESHOLD > startIndex) {
            startIndex = workingIndex - THRESHOLD;
        }
        if (workingIndex + THRESHOLD < endIndex) {
            endIndex = workingIndex + THRESHOLD;
        }
        VBox topBox = createDayParent(workingList.get(startIndex));
        HBox item = buildIndividualTask(workingList.get(startIndex));
        topBox.getChildren().add(item);
        topBox.setMinHeight(topBox.getMinHeight() + ITEM_HEIGHT);
        transLationY += LABEL_HEIGHT;
        if (startIndex < workingIndex) {
            transLationY += ITEM_HEIGHT;
        }

        for (int i = startIndex + 1; i <= endIndex; i++) {
            Label dateLabel = checkSameDay(workingList.get(i - 1), workingList.get(i));
            if (dateLabel != null) {
                _mainVbox.getChildren().add(topBox);
                topBox = createDayParent(workingList.get(i));
                if (i < workingIndex) {
                    transLationY += LABEL_HEIGHT;
                }

            }
            topBox.getChildren().add(buildIndividualTask(workingList.get(i)));
            topBox.setMinHeight(topBox.getMinHeight() + ITEM_HEIGHT);
            if (i < workingIndex) {
                transLationY += ITEM_HEIGHT;
            }
        }
        _mainVbox.getChildren().add(topBox);
        _startIndex = startIndex;
        _endIndex = endIndex;
        _selectedIndex = workingIndex;
    }

    public static VBox createDayParent(Task task) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setId("cssTaskViewDay");
        vbox.setMinHeight(LABEL_HEIGHT);
        Label dateLabel = new Label("task.getDueDate() and pass to ten to prase out Today/Tmr/watever");
        dateLabel.setMinHeight(TaskViewUserInterface.LABEL_HEIGHT);
        dateLabel.setFont(new Font(PrimaryUserInterface.DEFAULT_FONT, TaskViewUserInterface.LABEL_FONT_SIZE));
        vbox.getChildren().add(dateLabel);
        return vbox;
    }

    public HBox buildIndividualTask(Task task) {
        HBox hbox = new HBox();
        hbox.setMinHeight(TaskViewUserInterface.TASK_FONT_SIZE);
        GridPane gridPane = createGridPaneForTask(task);
        _gridPanes.add(gridPane);
        hbox.getChildren().add(gridPane);
        return hbox;
    }

    public GridPane createGridPaneForTask(Task task) {
        Font font = new Font(PrimaryUserInterface.DEFAULT_FONT, TaskViewUserInterface.TASK_FONT_SIZE);
        GridPane grid = new GridPane();
        grid.setStyle(null);
        grid.setId("grid" + task.getId());
        grid.setHgap(GAP_SIZE);
        grid.setMinWidth(_individualItemWidth);
        grid.setMinHeight(ITEM_HEIGHT);
        Label timeLabel = new Label("hi");
        timeLabel.setMinHeight(ITEM_HEIGHT);
        timeLabel.setFont(font);
        grid.add(timeLabel, 1, 0);
        Label descriptionLabel = new Label(task.getDescription());
        descriptionLabel.setMinHeight(ITEM_HEIGHT);
        descriptionLabel.setFont(font);
        grid.add(descriptionLabel, 2, 0);
        return grid;
    }

    // inclusive
    public boolean isBetweenStartEnd(int index) {
        if (index >= _startIndex && index <= _endIndex) {
            return true;
        }
        return false;
    }

    public void update(int value) {
        if (value > 0)// ctrl down
        {
            if (_endIndex + 1 < workingList.size()) {
                if (_selectedIndex - _startIndex >= THRESHOLD) {
                    String item = "#grid" + String.valueOf(_startIndex);
                    GridPane gp = (GridPane) _mainVbox.lookup(item);
                    VBox gpParent = (VBox) gp.getParent().getParent();
                    if (gpParent.getChildren().size() == 2) {
                        _mainVbox.getChildren().remove(gpParent);
                        transLationY = transLationY - LABEL_HEIGHT - ITEM_HEIGHT;
                    } else {
                        gpParent.getChildren().remove(1);
                        gpParent.setMinHeight(gpParent.getMinHeight() - ITEM_HEIGHT);
                        transLationY = transLationY - ITEM_HEIGHT;
                    }
                    _gridPanes.remove(0);
                    _startIndex += 1;
                }
                Label labelForTask = checkSameDay(workingList.get(_endIndex), workingList.get(_endIndex + 1));
                if (labelForTask == null) {
                    GridPane gp = _gridPanes.get(_gridPanes.size() - 1);
                    VBox gpParent = (VBox) gp.getParent().getParent();
                    gpParent.getChildren().add(buildIndividualTask(workingList.get(_endIndex + 1)));
                    gpParent.setMinHeight(gpParent.getMinHeight() + ITEM_HEIGHT);
                } else {
                    VBox vbox = createDayParent(workingList.get(_endIndex + 1));
                    vbox.getChildren().add(buildIndividualTask(workingList.get(_endIndex + 1)));
                    vbox.setMinHeight(vbox.getMinHeight() + ITEM_HEIGHT);
                    _mainVbox.getChildren().add(vbox);
                }
                _endIndex = _endIndex + 1;
            }
        } else if (value < 0) {
            if (_startIndex > 0) {
                if (_endIndex - _selectedIndex >= THRESHOLD) {
                    String item = "#grid" + String.valueOf(_endIndex);
                    GridPane gp = (GridPane) _mainVbox.lookup(item);
                    VBox gpParent = (VBox) gp.getParent().getParent();
                    if (gpParent.getChildren().size() == 2) {
                        _mainVbox.getChildren().remove(gpParent);
                    } else {
                        gpParent.getChildren().remove(gpParent.getChildren().size() - 1);
                        gpParent.setMinHeight(gpParent.getMinHeight() - ITEM_HEIGHT);
                    }
                    _gridPanes.remove(_gridPanes.size() - 1);
                    _endIndex -= 1;
                }
                Label labelForTask = checkSameDay(workingList.get(_startIndex),
                        workingList.get(_startIndex - 1));
                if (labelForTask == null) {
                    GridPane gp = _gridPanes.get(0);
                    VBox gpParent = (VBox) gp.getParent().getParent();
                    gpParent.getChildren().add(1, buildIndividualTask(workingList.get(_startIndex - 1)));
                    gpParent.setMinHeight(gpParent.getMinHeight() + ITEM_HEIGHT);
                } else {
                    VBox vbox = createDayParent(workingList.get(_startIndex - 1));
                    vbox.getChildren().add(buildIndividualTask(workingList.get(_startIndex - 1)));
                    vbox.setMinHeight(vbox.getMinHeight() + ITEM_HEIGHT);
                    _mainVbox.getChildren().add(0, vbox);
                    transLationY += LABEL_HEIGHT;
                }
                transLationY += ITEM_HEIGHT;
                _gridPanes.add(0, _gridPanes.remove(_gridPanes.size() - 1));
                _startIndex = _startIndex - 1;
            }
        }
    }

    public boolean setItemSelected(int value) {
        int index = value + _selectedIndex;
        if (isBetweenStartEnd(index)) {
            _gridPanes.get(_selectedIndex - _startIndex).setStyle(null);
            _gridPanes.get(index - _startIndex).setStyle("-fx-border-color:blue");
            updateTranslationY(value, index, _selectedIndex);
            _selectedIndex = index;
            return true;
        }
        return false;
    }

    public void updateTranslationY(int direction, int selectedIndex, int previousSelected) {
        if (direction > 0) {
            if (checkSameDay(workingList.get(selectedIndex), workingList.get(previousSelected)) != null) {
                transLationY = transLationY + LABEL_HEIGHT;
            }
            transLationY += ITEM_HEIGHT;
        } else {
            if (checkSameDay(workingList.get(selectedIndex), workingList.get(previousSelected)) != null) {
                transLationY = transLationY - LABEL_HEIGHT;
            }
            transLationY -= ITEM_HEIGHT;
        }
    }

    public double getTranslationY() {
        return -transLationY + TaskViewUserInterface.LABEL_HEIGHT + TaskViewUserInterface.ITEM_HEIGHT * 2;
    }

    public void updateTranslateY(double itemPosY) {
        _mainVbox.setTranslateY(itemPosY);
    }

    //get from logic side
    public static Label checkSameDay(Task task1, Task task2) {
        if (task1 == null) { // new day
            return new Label(task2.getDueDate().toString());
        } else {
            if (task1.getDueDate().get(Calendar.YEAR) == task2.getDueDate().get(Calendar.YEAR)) {
                if (task1.getDueDate().get(Calendar.MONTH) == task2.getDueDate().get(Calendar.MONTH)) {
                    if (task1.getDueDate().get(Calendar.DATE) == task2.getDueDate().get(Calendar.DATE)) {
                        return null;
                    }
                }
            }
        }
        return new Label(task2.getDueDate().toString());
    }

    public ArrayList<DescriptionLabel> rebuildDescriptionLabels() {
        ArrayList<DescriptionLabel> descriptionLables = new ArrayList<DescriptionLabel>();
        HBox hbox = (HBox) getSelectedGridPane().getParent();
        for (int i = 0; i < _mainVbox.getChildren().size(); i++) {
            VBox vbox = (VBox) _mainVbox.getChildren().get(i);
            int index = getSelectedGridPaneIndex(vbox);

            DescriptionLabel dLabel = new DescriptionLabel(workingList.get(index));
            dLabel.setHeight(vbox.getMinHeight());
            if (vbox == hbox.getParent()) {
                dLabel.setSelected();
            }
            descriptionLables.add(dLabel);
        }
        return descriptionLables;
    }

    public int getSelectedGridPaneIndex(VBox vbox) {
        HBox parentHbox = (HBox) vbox.getChildren().get(1);
        GridPane gp = (GridPane) parentHbox.getChildren().get(0);
        String id = gp.getId();
        return Integer.parseInt(id.substring(4));
    }

    public GridPane getSelectedGridPane() {
        String item = "#grid" + String.valueOf(_selectedIndex);
        GridPane gridPane = (GridPane) _mainVbox.lookup(item);
        return gridPane;
    }
}
