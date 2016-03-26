package userInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import dateParser.CommandParser;
import dateParser.CommandParser.COMMAND;
import dateParser.InputParser;
import dateParser.Pair;
import dateParser.XMLParser;
import entity.TaskEntity;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.scene.Node;
import javafx.application.Platform;
import org.jsoup.Jsoup;;

public class CommandBar {

	private static final int GAP_SIZE = 2;
	private double _prefWidth;
	private double _prefHeight = 30;

	private GridPane _mainPane;
	private TextField _textField;
	private int _numberOfItems = 0;

	private ArrayList<String> _allSessionCmds = new ArrayList<String>();
	private String fullInput = "";

	public CommandBar(double prefWidth) {
		this._prefWidth = prefWidth;
		initializeMainPane();
		initializeTextBox();
		_mainPane.add(_textField, _numberOfItems++, 0);
	}

	public void initializeHTMLEditor() {
		_textField = new TextField();
		_textField.setId("testUserInput");
		_textField.setPrefWidth(10);
		_textField.setBorder(null);
	}

	public void initializeMainPane() {
		_mainPane = new GridPane();
		_mainPane.setMaxHeight(_prefHeight);
		_mainPane.setStyle("-fx-background-color: #FFFFFF;");
		_mainPane.setAlignment(Pos.CENTER_LEFT);
		_mainPane.setHgap(GAP_SIZE);
	}

	public void initializeTextBox() {
		_textField = new TextField();
		_textField.setId("mainUserInput");
		_textField.setAlignment(Pos.CENTER_LEFT);
		_textField.setMinWidth(30);
		_textField.setPrefHeight(_prefHeight);
		_textField.setBorder(null);
	}

	public void concatToFullString() {
		String input = _textField.getText();
		fullInput = fullInput.concat(input);
		_textField.setText("");
	}

	public void clearFullString() {
		fullInput = "";
	}

	public void deleteKey() {
		if (fullInput.length() > 0) {
			fullInput = fullInput.substring(0, fullInput.length() - 1);
		}
		onKeyReleased();
	}

	public void release() {
		String input = _textField.getText();
		if (!input.equals("")) {
			onKeyReleased();
		}
	}

	public void onKeyReleased() {
		concatToFullString();
		ArrayList<Node> itemsToAdd = new ArrayList<Node>();
		InputParser parser = new InputParser(fullInput);
		try {
			parser.addXML();
			ArrayList<Pair<String, ArrayList<String>>> items = XMLParser.xmlToArrayList(parser.getInput());
			for (int i = 0; i < items.size(); i++) {
				Label label = buildItem(items.get(i));
				if (label != null) {
					itemsToAdd.add(label);
				}
			}
		} catch (Exception e) {
		}
		itemsToAdd.add(_textField);
		addItemsToBar(itemsToAdd);
	}

	private void addItemsToBar(ArrayList<Node> itemsToAdd) {
		_mainPane.getChildren().clear();
		_numberOfItems = 0;
		for (int i = 0; i < itemsToAdd.size(); i++) {
			_mainPane.add(itemsToAdd.get(i), _numberOfItems++, 0);
		}
	}

	public Label buildItem(Pair<String, ArrayList<String>> item) {
		String type = item.getFirst();
		if (type.equals(XMLParser.CMD_TAG)) {
			return buildCommandDisplay(item.getSecond());
		} else if (type.equals(XMLParser.TITLE_TAG)) {
			return buildTitleLabel(item.getSecond());
		} else if (type.equals(XMLParser.DESC_TAG)) {
			return buildDescLabel(item.getSecond());
		} else if (type.equals(XMLParser.ID_TAG)) {
			return buildIDLabel(item.getSecond());
		} else if (type.equals(XMLParser.DATE_TAG)) {
			return buildDateLabel(item.getSecond());
		}
		return null;
	}

	public Label buildLabelSkeleton() {
		Label label = new Label();
		label.setMinHeight(_prefHeight);
		label.setAlignment(Pos.BASELINE_RIGHT);
		return label;
	}

	private Label buildDateLabel(ArrayList<String> dates) {
		Label label = buildLabelSkeleton();
		String text = "";
		for (int i = 0; i < dates.size(); i++) {
			text = text.concat(dates.get(i));
			if (i < dates.size() - 1) {
				text.concat(" ");
			}
		}
		label.setText(text);
		label.setId("cssCommandDate");
		return label;
	}

	public Label buildIDLabel(ArrayList<String> text) {
		Label label = buildLabelSkeleton();
		label.setText(text.get(0));
		label.setId("cssCommandID");
		return label;
	}

	public Label buildDescLabel(ArrayList<String> text) {
		Label label = buildLabelSkeleton();
		label.setText(text.get(0));
		label.setId("cssCommandDescription");
		return label;
	}

	public Label buildTitleLabel(ArrayList<String> text) {
		Label label = buildLabelSkeleton();
		label.setText(text.get(0));
		label.setId("cssCommandTitle");
		return label;
	}

	public Label buildCommandDisplay(ArrayList<String> text) {
		CommandParser cp = new CommandParser();
		if (text.size() == 1) {
			String commandString = text.get(0);
			Label label = buildLabelSkeleton();
			label.setText(commandString);
			if (cp.getCommand(commandString).equals(COMMAND.ADD)) {
				label.setId("cssCommandBarAdd");
			} else if (cp.getCommand(commandString).equals(COMMAND.DELETE)) {
				label.setId("cssCommandBarDelete");
			} else if (cp.getCommand(commandString).equals(COMMAND.EDIT)) {
				label.setId("cssCommandBarEdit");
			} else if (cp.getCommand(commandString).equals(COMMAND.INVALID)) {
				label.setId("cssCommandBarInvalid");
			}
			return label;
		}
		return null;
	}

	public void addLabelToDisplay(Label label) {
		if (label != null) {
			_mainPane.add(label, _numberOfItems++, 0);
		}
	}

	public COMMAND onEnter() {
		onKeyReleased();
		InputParser parser = new InputParser(fullInput);
		COMMAND cmd = parser.getCommand();
		return cmd;
	}

	public ArrayList<TaskEntity> getTasks() {
		InputParser parser = new InputParser(fullInput);
		return parser.getTask();
	}

	public String getId() {
		String returnVal = null;
		InputParser parser = new InputParser(fullInput);
		returnVal = parser.getID();
		return returnVal;
	}

	public void setTextFieldHandler(EventHandler<KeyEvent> mainEventHandler,
			EventHandler<KeyEvent> secondaryEventHandler) {
		_textField.setOnKeyPressed(mainEventHandler);
		_textField.setOnKeyReleased(secondaryEventHandler);
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

}
