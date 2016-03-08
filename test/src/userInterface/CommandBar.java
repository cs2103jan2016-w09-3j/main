package userInterface;

import java.util.ArrayList;
import java.util.Calendar;

import dateParser.CommandParser.COMMAND;
import dateParser.InputParser;
import dateParser.XMLParser;
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
	private String _textInField = new String();
	private ArrayList<String> _allSessionCmds = new ArrayList<String>();
	public String get_textInField() {
		return _textInField;
	}

	public void set_textInField(String _textInField) {
		this._textInField = _textInField;
	}

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

	public void onSpace(String input) {
		_textInField = XMLParser.removeAllTags(input);
		System.out.println(_textInField);
		InputParser parser = new InputParser(_textInField);
		parser.addXML();

		String textToShow = parser.getInput();
		_textField.setText(textToShow);
		_textField.positionCaret(textToShow.length() - 1);
	}

	public COMMAND onEnter(String input) {
		onSpace(input);
		InputParser parser = new InputParser(_textInField);
		COMMAND cmd = parser.getCommand(_textInField);
		return cmd;
	}

	public ArrayList<TaskEntity> getTasks(String input) {
		InputParser parser = new InputParser(input);
		return parser.getTask(input);
	}

	public void setTextFieldHandler(EventHandler<KeyEvent> mainEventHandler) {
		_textField.setOnKeyPressed(mainEventHandler);
	}

	public TaskEntity executeLine(String userInput) {

		Calendar c = Calendar.getInstance();
		TaskEntity t = new TaskEntity("name of task", c, false, userInput);
		return t; // return null if not valid command.
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

	public ArrayList<String> get_allSessionCmds() {
		return _allSessionCmds;
	}

	public void set_allSessionCmds(ArrayList<String> _allSessionCmds) {
		this._allSessionCmds = _allSessionCmds;
	}
	
	public void addSessionCmds(String input) {
		this._allSessionCmds.add(input);
	}

}
