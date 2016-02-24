
package userInterface;

import java.util.ArrayList;
import java.util.Calendar;

import entity.DescriptionLabel;
import entity.Task;
import javafx.geometry.Insets;
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

public class TaskViewUserInterface implements ViewInterface {

    static final int LABEL_HEIGHT = 35;
    static final int ITEM_HEIGHT = 30;
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
                    - DescriptionComponent.CONPONENT_RIGHT_MARGIN - DetailComponent.CONPONENT_WIDTH
                    - DetailComponent.CONPONENT_LEFT_MARGIN;
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
                    - DescriptionComponent.CONPONENT_WIDTH - DescriptionComponent.CONPONENT_RIGHT_MARGIN
                    - DetailComponent.CONPONENT_WIDTH - DetailComponent.CONPONENT_LEFT_MARGIN;
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
        int workingIndex = 1; // getWorkingIndex(); when qy implements
        int startIndex = 0;
        int endIndex = workingList.size() - 1;
        if (workingIndex - THRESHOLD > startIndex) {
            startIndex = workingIndex - THRESHOLD;
        }
        if (workingIndex + THRESHOLD < endIndex) {
            endIndex = workingIndex + THRESHOLD;
        }

        VBox weekBox = createWeekParent();
        VBox topBox = createDayParent(workingList.get(startIndex));
        HBox item = buildIndividualTask(workingList.get(startIndex));
        topBox.getChildren().add(item);
        topBox.setMinHeight(topBox.getMinHeight() + ITEM_HEIGHT);
        transLationY += LABEL_HEIGHT;
        if (startIndex < workingIndex) {
            transLationY += ITEM_HEIGHT;
        }
        for (int i = startIndex + 1; i <= endIndex; i++) {
            if (!isSameWeek(workingList.get(i - 1), workingList.get(i))) {
                weekBox.getChildren().add(topBox);
                weekBox.setMinHeight(weekBox.getMinHeight() + topBox.getMinHeight());
                _mainVbox.getChildren().add(weekBox);
                weekBox = createWeekParent();
                topBox = createDayParent(workingList.get(i));
                if (i < workingIndex) {
                    transLationY += LABEL_HEIGHT;
                }
            } else {
                if (!isSameDay(workingList.get(i - 1), workingList.get(i))) {
                    weekBox.getChildren().add(topBox);
                    weekBox.setMinHeight(weekBox.getMinHeight() + topBox.getMinHeight());
                    topBox = createDayParent(workingList.get(i));
                    if (i < workingIndex) {
                        transLationY += LABEL_HEIGHT;
                    }
                }
            }
            topBox.getChildren().add(buildIndividualTask(workingList.get(i)));
            topBox.setMinHeight(topBox.getMinHeight() + ITEM_HEIGHT);
            if (i < workingIndex) {
                transLationY += ITEM_HEIGHT;
            }

        }
        weekBox.getChildren().add(topBox);
        weekBox.setMinHeight(weekBox.getMinHeight() + topBox.getMinHeight());
        _mainVbox.getChildren().add(weekBox);

        _startIndex = startIndex;
        _endIndex = endIndex;
        _selectedIndex = workingIndex;

    }

    public VBox createWeekParent() {
        VBox vbox = new VBox();
        vbox.setId("cssTaskViewWeek");
        vbox.setMinWidth(_stageWidth);
        vbox.setMinHeight(0);
        return vbox;
    }

    public VBox createDayParent(Task task) {
        VBox vbox = new VBox();
        vbox.setMinHeight(LABEL_HEIGHT);
        // vbox.setId(calculateVboxCssStyle(task.getDueDate()));
        Label dateLabel = new Label(getStringOfDate(task.getDueDate()));
        dateLabel.setMinHeight(TaskViewUserInterface.LABEL_HEIGHT);
        dateLabel.setFont(new Font(PrimaryUserInterface.DEFAULT_FONT, TaskViewUserInterface.LABEL_FONT_SIZE));
        vbox.getChildren().add(dateLabel);
        VBox.setMargin(dateLabel, new Insets(0, 0, 0, 20));
        return vbox;
    }

    public String calculateVboxCssStyle(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY :
                return "cssTaskViewSunday";
            case Calendar.SATURDAY :
                return "cssTaskViewSaturday";
            case Calendar.MONDAY :
                return "cssTaskViewMonday";
            case Calendar.TUESDAY :
                return "cssTaskViewTuesday";
            case Calendar.WEDNESDAY :
                return "cssTaskViewWednesday";
            case Calendar.THURSDAY :
                return "cssTaskViewThursday";
            case Calendar.FRIDAY :
                return "cssTaskViewFriday";
        }
        return null;
    }

    // this method will call ten method.
    public String getStringOfDate(Calendar c) {
        Integer.toString(c.get(Calendar.WEEK_OF_MONTH));
        return c.getTime().toString();
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
        grid.setId("");
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
                    removeFirstTask();
                }
                addLastItem();
            }
        } else if (value < 0) {
            if (_startIndex > 0) {
                if (_endIndex - _selectedIndex >= THRESHOLD) {
                    removeLastTask();
                }
                addFirstItem();
            }
        }
    }

    public void removeFirstTask() {
        GridPane gp = _gridPanes.get(0);
        VBox gpDayParent = (VBox) gp.getParent().getParent();
        VBox gpWeekParent = (VBox) gpDayParent.getParent();
        if (gpDayParent.getChildren().size() == 2) {
            gpWeekParent.getChildren().remove(gpDayParent);
            gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - gpDayParent.getMinHeight());
            if (gpWeekParent.getChildren().size() == 0) {
                _mainVbox.getChildren().remove(gpWeekParent);
            }
            transLationY = transLationY - LABEL_HEIGHT - ITEM_HEIGHT;
        } else {
            gpDayParent.getChildren().remove(1);
            gpDayParent.setMinHeight(gpDayParent.getMinHeight() - ITEM_HEIGHT);
            gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - ITEM_HEIGHT);
            transLationY = transLationY - ITEM_HEIGHT;
        }
        _gridPanes.remove(0);
        _startIndex += 1;
    }

    public void removeLastTask() {
        GridPane gp = _gridPanes.get(_gridPanes.size() - 1);
        VBox gpDayParent = (VBox) gp.getParent().getParent();
        VBox gpWeekParent = (VBox) gpDayParent.getParent();
        if (gpDayParent.getChildren().size() == 2) {
            gpWeekParent.getChildren().remove(gpDayParent);
            gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - gpDayParent.getMinHeight());
            if (gpWeekParent.getChildren().size() == 0) {
                _mainVbox.getChildren().remove(gpWeekParent);
            }
        } else {
            gpDayParent.getChildren().remove(gpDayParent.getChildren().size() - 1);
            gpDayParent.setMinHeight(gpDayParent.getMinHeight() - ITEM_HEIGHT);
            gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() - ITEM_HEIGHT);
        }
        _gridPanes.remove(_gridPanes.size() - 1);
        _endIndex -= 1;
    }

    public void addLastItem() {
        GridPane gp = _gridPanes.get(_gridPanes.size() - 1);
        VBox gpDayParent = (VBox) gp.getParent().getParent();
        VBox gpWeekParent = (VBox) gpDayParent.getParent();
        if (isSameDay(workingList.get(_endIndex), workingList.get(_endIndex + 1))) {
            gpDayParent.getChildren().add(buildIndividualTask(workingList.get(_endIndex + 1)));
            gpDayParent.setMinHeight(gpDayParent.getMinHeight() + ITEM_HEIGHT);
            gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() + ITEM_HEIGHT);
        } else {
            VBox weekParent = null;
            if (isSameWeek(workingList.get(_endIndex), workingList.get(_endIndex + 1))) {
                weekParent = (VBox) gp.getParent().getParent().getParent();
            } else {
                weekParent = createWeekParent();
                _mainVbox.getChildren().add(weekParent);
            }
            VBox vbox = createDayParent(workingList.get(_endIndex + 1));
            vbox.getChildren().add(buildIndividualTask(workingList.get(_endIndex + 1)));
            vbox.setMinHeight(vbox.getMinHeight() + ITEM_HEIGHT);
            weekParent.setMinHeight(weekParent.getMinHeight() + vbox.getMinHeight());
            weekParent.getChildren().add(vbox);
        }
        _endIndex = _endIndex + 1;
    }

    public void addFirstItem() {
        GridPane gp = _gridPanes.get(0);
        VBox gpDayParent = (VBox) gp.getParent().getParent();
        VBox gpWeekParent = (VBox) gpDayParent.getParent();
        if (isSameDay(workingList.get(_startIndex), workingList.get(_startIndex - 1))) {
            gpDayParent.getChildren().add(1, buildIndividualTask(workingList.get(_startIndex - 1)));
            gpDayParent.setMinHeight(gpDayParent.getMinHeight() + ITEM_HEIGHT);
            gpWeekParent.setMinHeight(gpWeekParent.getMinHeight() + ITEM_HEIGHT);
        } else {
            VBox weekBox = null;
            if (isSameWeek(workingList.get(_startIndex), workingList.get(_startIndex - 1))) {
                weekBox = (VBox) gpDayParent.getParent();
            } else {
                weekBox = createWeekParent();
                _mainVbox.getChildren().add(0, weekBox);
            }
            VBox vbox = createDayParent(workingList.get(_startIndex - 1));
            vbox.getChildren().add(buildIndividualTask(workingList.get(_startIndex - 1)));
            vbox.setMinHeight(vbox.getMinHeight() + ITEM_HEIGHT);
            weekBox.getChildren().add(0, vbox);
            weekBox.setMinHeight(weekBox.getMinHeight() + vbox.getMinHeight());
            transLationY += LABEL_HEIGHT;
        }
        transLationY += ITEM_HEIGHT;
        _gridPanes.add(0, _gridPanes.remove(_gridPanes.size() - 1));
        _startIndex = _startIndex - 1;
    }

    public boolean setItemSelected(int value) {
        int index = value + _selectedIndex;
        if (isBetweenStartEnd(index)) {
            _gridPanes.get(_selectedIndex - _startIndex).setId("");
            _gridPanes.get(index - _startIndex).setId("cssTaskViewSelectedTask");
            updateTranslationY(value, index, _selectedIndex);
            _selectedIndex = index;
            return true;
        }
        return false;
    }

    public void updateTranslationY(int direction, int selectedIndex, int previousSelected) {
        if (direction > 0) {
            if (!isSameDay(workingList.get(selectedIndex), workingList.get(previousSelected))) {
                transLationY = transLationY + LABEL_HEIGHT;
            }
            transLationY += ITEM_HEIGHT;
        } else {
            if (!isSameDay(workingList.get(selectedIndex), workingList.get(previousSelected))) {
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

    public static Label createLabelForDay(Task task) {
        return new Label(task.getDueDate().toString());
    }

    // get from logic side
    public static boolean isSameDay(Task task1, Task task2) {
        if (task1 == null) { // new day
            return false;
        }
        if (task1.getDueDate().get(Calendar.YEAR) == task2.getDueDate().get(Calendar.YEAR)) {
            if (task1.getDueDate().get(Calendar.MONTH) == task2.getDueDate().get(Calendar.MONTH)) {
                if (task1.getDueDate().get(Calendar.DATE) == task2.getDueDate().get(Calendar.DATE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSameWeek(Task task1, Task task2) {
        if (task1 == null) {
            return false;
        }
        if (task1.getDueDate().get(Calendar.YEAR) == task2.getDueDate().get(Calendar.YEAR)) {

            // display by year week //remove after decided
            boolean byYearWeek = true;
            if (byYearWeek) {
                if (task1.getDueDate().get(Calendar.WEEK_OF_YEAR) == task2.getDueDate()
                        .get(Calendar.WEEK_OF_YEAR)) {
                    return true;
                }
            } else {
                if (task1.getDueDate().get(Calendar.MONTH) == task2.getDueDate().get(Calendar.MONTH)) {
                    if (task1.getDueDate().get(Calendar.WEEK_OF_MONTH) == task2.getDueDate()
                            .get(Calendar.WEEK_OF_MONTH)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<DescriptionLabel> rebuildDescriptionLabels() {
        ArrayList<DescriptionLabel> descriptionLables = new ArrayList<DescriptionLabel>();
        HBox hbox = (HBox) getSelectedGridPane().getParent();
        int countOfItems = 0;
        for (int i = 0; i < _mainVbox.getChildren().size(); i++) {
            VBox weekBox = (VBox) _mainVbox.getChildren().get(i);
            Task firstTaskInWeek = workingList.get(_startIndex + countOfItems);
            int numberOfTaskInWeek = countNumberOfTaskInWeek(weekBox);
            int indexForLastTaskInWeek = countOfItems + numberOfTaskInWeek - 1;
            Task lastTaskInWeek = null;
            if (indexForLastTaskInWeek < workingList.size()) {
                lastTaskInWeek = workingList.get(_startIndex + indexForLastTaskInWeek);
                countOfItems += numberOfTaskInWeek;
            }
            DescriptionLabel dLabel = new DescriptionLabel(firstTaskInWeek, lastTaskInWeek);
            dLabel.setHeight(weekBox.getMinHeight());
            if (weekBox.getChildren().contains(hbox.getParent())) {
                dLabel.setSelected();
            }
            descriptionLables.add(dLabel);
        }
        return descriptionLables;
    }

    public int countNumberOfTaskInWeek(VBox weekBox) {
        int noOfTask = 0;
        for (int i = 0; i < weekBox.getChildren().size(); i++) {
            VBox dayBox = (VBox) weekBox.getChildren().get(i);
            noOfTask += dayBox.getChildren().size() - 1;// -1 cause of label for
                                                        // day
        }
        return noOfTask;
    }

    public int getSelectedGridPaneIndex(VBox vbox) {
        VBox parentDay = (VBox) vbox.getChildren().get(0);
        HBox parentHbox = (HBox) parentDay.getChildren().get(1);
        GridPane gp = (GridPane) parentHbox.getChildren().get(0);
        String id = gp.getId();
        return Integer.parseInt(id.substring(4));
    }

    public GridPane getSelectedGridPane() {
        GridPane gridPane = _gridPanes.get(_selectedIndex - _startIndex);
        return gridPane;
    }

    public void destoryStage() {
        _stage.close();
    }
}
