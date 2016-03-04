package userInterface;

import java.util.Calendar;

import entity.TaskEntity;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class CommandBar {

    private GridPane _mainPane;
    private TextField _textField;
    private int _numberOfItems = 0;

    public CommandBar() {
        initializeMainPane();
        initializeTextBox();
        _mainPane.add(_textField, _numberOfItems++, 0);
    }

    public void initializeMainPane() {
        _mainPane = new GridPane();
        _mainPane.setMaxHeight(30);
        _mainPane.setPadding(new Insets(4, 2, 0, 2));
        _mainPane.setStyle("-fx-background-color: #FFFFFF;");
        _mainPane.setAlignment(Pos.CENTER);
    }

    public void initializeTextBox() {
        _textField = new TextField();
        _textField.setId("mainUserInput");
        _textField.setPrefWidth(800.0);
        _textField.setBorder(null);
    }

    public TaskEntity executeLine(String userInput) {
        
        Calendar c = Calendar.getInstance();
        TaskEntity t = new TaskEntity("id123", c, false, userInput);
        
        return t; //return null if not valid command.
    }

    public void setTextFieldHandler(EventHandler<KeyEvent> mainEventHandler) {
        _textField.setOnKeyPressed(mainEventHandler);
    }

    public void focus() {
        _mainPane.requestFocus();
        _textField.requestFocus();
    }

    public TextField getTextField() {
        return _textField;
    }

    public GridPane getCommandBar() {
        return _mainPane;
    }

}
