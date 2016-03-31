package userInterface;

import java.util.ArrayList;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.Node;

public class CommandBar {

	private static final int FEEDBACK_STATUS_NORMAL = 0;
	private static final int FEEDBACK_STATUS_ERROR = 1;
	private static final int FEEDBACK_STATUS_CONFLICT = 2;

	private static final String MESSAGE_FAILURE_INVALID = "you have entered an invalid command";
	private static final String MESSAGE_SUCCESS_ADD_TYPE1 = "Successfully added %1$s to task list.";
	private static final String MESSAGE_SUCCESS_ADD_TYPE2 = "Successfully added %1$s to floating task list.";
	private static final String MESSAGE_SUCCESS_ADD_TYPE3 = "Successfully added %1$s to list.";
	private static final String MESSAGE_FAILURE_ADD = "Fail to add.";
	private static final String MESSAGE_SUCCESS_DELETE = "Successfully deleted %1$s.";
	private static final String MESSAGE_FAILURE_DELETE_TYPE_1 = "Fail to delete %1$s.";
	private static final String MESSAGE_FAILURE_DELETE_TYPE_2 = "Task id required to delete.";
	private static final String MESSAGE_SUCCESS_EDIT = "Successfully edited %1$s.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE1 = "Fail to edit %1$s.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE2 = "Fail to retrieve task with %1$s.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE3 = "Task id required to edit.";
	private static final String MESSAGE_SUCCESS_MARK = "Successfully mark %1$s as completed.";
	private static final String MESSAGE_FAILURE_MARK_TYPE_1 = "Fail to mark %1$s as completed.";
	private static final String MESSAGE_FAILURE_MARK_TYPE_2 = "Task id required to mark a task done.";
	private static final String MESSAGE_SUCCESS_SEARCH_TYPE_1 = "Search compelete with %1$s results.";
	private static final String MESSAGE_SUCCESS_SEARCH_TYPE_2 = "No results found.";
	private static final String MESSAGE_FAILURE_SEARCH_TYPE1 = "No results found.";
	private static final String MESSAGE_FAILURE_SEARCH_TYPE2 = "Search failed.";
	private static final String MESSAGE_FAILURE_JUMP_TYPE_1 = "No index to jump to.";
	private static final String MESSAGE_FAILURE_JUMP_TYPE_2 = "Task id required to jump to.";
	private static final String MESSAGE_SUCCESS_LINK = "Linked successfully.";
	private static final String MESSAGE_FAILURE_LINK_TYPE_1 = "Failed to link.";
	private static final String MESSAGE_FAILURE_LINK_TYPE_2 = "Failed to link.";
	private static final String MESSAGE_SUCCESS_UNDO = "successfully undo.";
	private static final String MESSAGE_FAILURE_UNDO_TYPE_1 = "Unable to undo.";
	private static final String MESSAGE_SUCCESS_SAVEDIR ="successfully changed save directory.";
	private static final String MESSAGE_FAILURE_SAVEDIR ="Fail to change save directory.";

	private static final int GAP_SIZE = 0;
	private static final double FEEDBACK_HEIGHT = 20;
	private static final int MAIN_PANE_LEFT_RIGHT_MARGIN = 0;
	private static final int TEXT_FIELD_WIDTH = 10;

	// font
	static final int FONT_SIZE_FEEDBACK = 12;
	private static final Font FONT_FEEDBACK = new Font(PrimaryUserInterface.DEFAULT_FONT, FONT_SIZE_FEEDBACK);

	private double _prefHeight;
	private double _prefWidth;
	private double _mainPaneHeight;
	private double _commandLabelHeight;

	private VBox _mainStructure;
	private Label _feedbackLabel;

	private GridPane _mainPane;
	private TextField _textField;
	private int _numberOfItems = 0;
	private int _selected;
	private ArrayList<Label> labels = new ArrayList<Label>();
	private ArrayList<String> preCommands = new ArrayList<String>();
	private int _commandSelector;

	private ArrayList<String> _allSessionCmds = new ArrayList<String>();
	private String fullInput = "";

	private CommandBarAnimation _animation;

	public CommandBar(double preHeight, double preWidth) {
		this._prefHeight = preHeight;
		this._prefWidth = preWidth;
		_mainPaneHeight = _prefHeight - FEEDBACK_HEIGHT;
		_commandLabelHeight = _mainPaneHeight / 2;
		_selected = -1;
		initilizeMainStructure();
		initilizeFeedbackBar();
		initializeMainPane();
		initializeTextBox();
		_mainPane.add(_textField, _numberOfItems++, 0);
	}

	public void initilizeMainStructure() {
		_mainStructure = new VBox();
		_mainStructure.setMaxHeight(_prefHeight);
		_mainStructure.setMaxWidth(_prefWidth);
		_mainStructure.setMinHeight(_prefHeight);
		_mainStructure.setMinWidth(_prefWidth);
		_mainStructure.setAlignment(Pos.BOTTOM_LEFT);
		_mainStructure.setId("cssCommandBarMainStructure");
	}

	private void initilizeFeedbackBar() {
		_feedbackLabel = new Label();
		_feedbackLabel.setMinWidth(_prefWidth);
		_feedbackLabel.setMaxHeight(FEEDBACK_HEIGHT);
		_feedbackLabel.setMinHeight(FEEDBACK_HEIGHT);
		_feedbackLabel.setFont(FONT_FEEDBACK);
		_feedbackLabel.setAlignment(Pos.CENTER_RIGHT);
		_feedbackLabel.setId("cssCommandBarfeedback_normal");
		_feedbackLabel.setOpacity(0.0);
		_mainStructure.getChildren().add(_feedbackLabel);
	}

	public void initializeMainPane() {
		_mainPane = new GridPane();
		_mainPane.setId("cssCommandBarContentZone");
		_mainPane.setMinHeight(_mainPaneHeight);
		_mainPane.setMaxHeight(_mainPaneHeight);
		_mainPane.setAlignment(Pos.CENTER_LEFT);
		_mainPane.setHgap(GAP_SIZE);
		VBox.setMargin(_mainPane, new Insets(0, MAIN_PANE_LEFT_RIGHT_MARGIN, 0, MAIN_PANE_LEFT_RIGHT_MARGIN));
		_mainStructure.getChildren().add(_mainPane);
	}

	public void initializeTextBox() {
		_textField = new TextField();
		_textField.setId("cssCommandMainUserInput");
		_textField.setMaxWidth(TEXT_FIELD_WIDTH);
		_textField.setAlignment(Pos.CENTER_LEFT);
		_textField.setPrefHeight(_commandLabelHeight);
		_textField.setPadding(new Insets(0, 0, 0, 0));
		_textField.setBorder(null);
	}

	public void concatToFullString() {
		String input = _textField.getText();
		if (!input.equals("")) {
			if (_selected == -1) {
				fullInput = fullInput.concat(input);
			} else {
				String front = getFrontString();
				String current = currentString();
				String back = getBackString();
				current = current.concat(input);
				fullInput = rebuildString(front, current, back);
			}
			_textField.setText("");
		}
	}

	public void clearFullString() {
		fullInput = "";
	}

	public void deleteKey() {
		if (_selected != -1) {
			String front = getFrontString();
			String current = currentString();
			String back = getBackString();
			current = current.substring(0, current.length() - 1);
			if (current.length() == 0) {
				_selected--;
			}
			fullInput = rebuildString(front, current, back);
			onKeyReleased();
		} else {
			if (fullInput.length() > 0) {
				fullInput = fullInput.substring(0, fullInput.length() - 1);
			}
			onKeyReleased();
		}
	}

	private String rebuildString(String front, String current, String back) {
		String full = "";
		if (!front.equals("")) {
			full = full.concat(front);
		}
		if (full.equals("")) {
			full = full.concat(current);
		} else {
			full = full.concat(" ").concat(current);
		}
		if (full.equals("")) {
			full = full.concat(back);
		} else {
			full = full.concat(" ").concat(back);
		}
		return full;
	}

	public String getFrontString() {
		String front = "";
		for (int i = 0; i < _selected; i++) {
			front = front.concat(labels.get(i).getText());
			if (i + 1 < _selected) {
				front = front.concat(" ");
			}
		}
		return front;
	}

	public String currentString() {
		return labels.get(_selected).getText();
	}

	public String getBackString() {
		String back = "";
		for (int i = _selected + 1; i < labels.size(); i++) {
			back = back.concat(labels.get(i).getText());
			if (i + 1 < labels.size()) {
				back = back.concat(" ");
			}
		}
		return back;
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
			System.out.println(parser.getInput());
			ArrayList<Pair<String, ArrayList<String>>> items = XMLParser.xmlToArrayList(parser.getInput());
			System.out.println(items.size());
			for (int i = 0; i < items.size(); i++) {
				Label label = buildItem(items.get(i));
				if (label != null) {
					itemsToAdd.add(label);
				}
			}
			addItemsToBar(itemsToAdd);
		} catch (Exception e) {
		}
	}

	private void addItemsToCommandBar(ArrayList<Label> itemsToAdd) {
		ArrayList<Node> temp = new ArrayList<Node>();
		for (Label i : itemsToAdd) {
			temp.add(i);
		}
		addItemsToBar(temp);
	}

	private void addItemsToBar(ArrayList<Node> itemsToAdd) {
		_mainPane.getChildren().clear();
		labels.clear();
		_numberOfItems = 0;
		if (_selected == -1) {
			itemsToAdd.add(_textField);
		} else {
			itemsToAdd.add(_selected + 1, _textField);
		}

		for (int i = 0; i < itemsToAdd.size(); i++) {
			_mainPane.add(itemsToAdd.get(i), _numberOfItems++, 1);
			if (itemsToAdd.get(i) instanceof Label) {
				labels.add((Label) itemsToAdd.get(i));
			}
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
		} else if (type.equals(XMLParser.OTHERS_TAG)) {
			return buildNormalLabel(item.getSecond());
		} else if (type.equals(XMLParser.HASH_TAG)) {
			return buildHashTagLabel(item.getSecond());
		}
		return null;
	}

	public Label buildLabelSkeleton() {
		Label label = new Label();
		label.setMinHeight(_commandLabelHeight);
		label.setAlignment(Pos.BASELINE_RIGHT);
		return label;
	}

	private Label buildNormalLabel(ArrayList<String> other) {
		Label label = buildLabelSkeleton();
		label.setText(other.get(0));
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

	public Label buildHashTagLabel(ArrayList<String> text) {
		Label label = buildLabelSkeleton();
		label.setText(text.get(0));
		label.setId("cssCommandHashTag");
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
			label.setText(commandString.toUpperCase());
			COMMAND cmd = cp.getCommand(commandString);
			switch (cmd) {
			case ADD: {
				label.setId("cssCommandBarAdd");
				break;
			}
			case DELETE: {
				label.setId("cssCommandBarDelete");
				break;
			}
			case EDIT: {
				label.setId("cssCommandBarEdit");
				break;
			}
			case INVALID: {
				label.setId("cssCommandBarInvalid");
				break;
			}
			case JUMP: {
				label.setId("cssCommandJump");
				break;
			}
			case DONE: {
				label.setId("cssCommandBarDone");
				break;
			}
			case SEARCH: {
				label.setId("cssCommandSearch");
				break;
			}
			case LINK: {
				label.setId("cssCommandBarLink");
				break;
			}
			case EXIT: {
				label.setId("cssCommandExit");
				break;
			}
			case FLOAT: {
				label.setId("cssCommandFloat");
				break;
			}
			case MAIN: {
				label.setId("cssCommandMain");
				break;
			}
			case HIDE: {
				label.setId("cssCommandHide");
				break;
			}
			case SHOW: {
				label.setId("cssCommandShow");
				break;
			}
			default:
				break;
			}
			return label;
		}
		return null;
	}

	public COMMAND onEnter() {
		onKeyReleased();
		preCommands.add(fullInput);
		_commandSelector = preCommands.size();
		InputParser parser = new InputParser(fullInput);
		COMMAND cmd = parser.getCommand();
		return cmd;
	}

	public ArrayList<TaskEntity> getTasks() {
		InputParser parser = new InputParser(fullInput);
		return parser.getTask();
	}

	public ArrayList<TaskEntity> getTasksPartialInput() {
		InputParser parser = new InputParser(fullInput);
		parser.removeId();
		return parser.getTask();
	}

	public String getId() {
		String returnVal = null;
		InputParser parser = new InputParser(fullInput);
		returnVal = parser.getID();
		return returnVal;
	}

	public String getSearchStr() {
		InputParser parser = new InputParser(fullInput);
		return parser.getSearchString();
	}

	public Pair<String, String> getLinkId() {
		Pair<String, String> returnVal = null;
		InputParser parser = new InputParser(fullInput);
		returnVal = parser.getLinkID();
		return returnVal;
	}

	public void setTextFieldHandler(EventHandler<KeyEvent> mainEventHandler,
			EventHandler<KeyEvent> secondaryEventHandler) {
		_textField.setOnKeyPressed(mainEventHandler);
		_textField.setOnKeyReleased(secondaryEventHandler);
	}

	public void focus() {
		_mainPane.requestFocus();
		_textField.requestFocus();
	}

	public TextField getTextField() {
		return _textField;
	}

	public VBox getCommandBar() {
		return _mainStructure;
	}

	public ArrayList<String> get_allSessionCmds() {
		return _allSessionCmds;
	}

	public String getFullInput() {
		return fullInput;
	}

	public void setFullInput(String toSet) {
		fullInput = toSet;
	}

	public void addToFullInput(String toSet) {
		fullInput = fullInput.trim().concat(" ").concat(toSet.trim());
	}

	/**
	 * method is called when "TAB" is executed, increase the selector index by
	 * 1, if index is over total no of labels, jumps to -1.
	 */
	public void changeSelector() {
		if (labels.size() > 0) {
			int tempSelector = _selected;
			if (_selected + 1 < labels.size() - 1) {
				tempSelector++;
			} else {
				tempSelector = -1;
			}
			// change in selector detected
			if (tempSelector != _selected && _selected != -1) {
				labels.get(_selected).setUnderline(false);
			}
			if (tempSelector != -1) {
				labels.get(tempSelector).setUnderline(true);
			}
			_selected = tempSelector;
		}
		addItemsToCommandBar(labels);
	}

	public void setFeedBackMessage(String feedback) {
		_feedbackLabel.setText(feedback);
	}

	public void reset() {
		_selected = -1;
		setFullInput("");
		ArrayList<Node> temp = new ArrayList<Node>();
		addItemsToBar(temp);
	}

	public void showFeedBackMessage(COMMAND cmdType, boolean condition, int type, String msg) {
		switch (cmdType) {
		case INVALID: {
			setFeedBackMessage(MESSAGE_FAILURE_INVALID);
			setFeedBackColor(FEEDBACK_STATUS_ERROR);
			break;
		}
		case ADD: {
			if (condition) {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_ADD_TYPE1, msg));
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else if (type == PrimaryUserInterface.TYPE_2) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_ADD_TYPE2, msg));
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else if (type == PrimaryUserInterface.TYPE_3) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_ADD_TYPE3, msg));
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				}
			} else {
				setFeedBackMessage(MESSAGE_FAILURE_ADD);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case DELETE: {
			if (condition) {
				setFeedBackMessage(String.format(MESSAGE_SUCCESS_DELETE, msg));
				setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			} else {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_DELETE_TYPE_1, msg));
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				} else if (type == PrimaryUserInterface.TYPE_2) {
					setFeedBackMessage(MESSAGE_FAILURE_DELETE_TYPE_2);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			}
			break;
		}
		case EDIT: {
			if (condition) {
				setFeedBackMessage(String.format(MESSAGE_SUCCESS_EDIT, msg));
				setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			} else {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_EDIT_TYPE1, msg));
				} else if (type == PrimaryUserInterface.TYPE_2) {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_EDIT_TYPE2, msg));
				} else if (type == PrimaryUserInterface.TYPE_3) {
					setFeedBackMessage(MESSAGE_FAILURE_EDIT_TYPE3);
				}
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case DONE: {
			if (condition) {
				setFeedBackMessage(String.format(MESSAGE_SUCCESS_MARK, msg));
				setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			} else {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_MARK_TYPE_1, msg));
				} else {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_MARK_TYPE_2, msg));
				}
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case SEARCH: {
			if (condition) {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_SEARCH_TYPE_1, msg));
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					setFeedBackMessage(MESSAGE_SUCCESS_SEARCH_TYPE_2);
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				}
			} else {
				setFeedBackMessage(String.format(MESSAGE_FAILURE_SEARCH_TYPE1, msg));
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case JUMP: {
			if (!condition) {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(MESSAGE_FAILURE_JUMP_TYPE_1);
				} else {
					setFeedBackMessage(MESSAGE_FAILURE_JUMP_TYPE_2);
				}
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case LINK: {
			if (!condition) {
				if (type == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(MESSAGE_FAILURE_LINK_TYPE_1);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				} else {
					setFeedBackMessage(MESSAGE_FAILURE_LINK_TYPE_2);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			} else {
				setFeedBackMessage(MESSAGE_SUCCESS_LINK);
				setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			}
			break;
		}
		/*case UNDO: {
			if (!condition) {
					setFeedBackMessage(MESSAGE_FAILURE_UNDO_TYPE_1);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
			} else {
				setFeedBackMessage(MESSAGE_SUCCESS_UNDO);
				setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			}
			break;
		}
		case SAVEDIR :{
			if(!condition)
			{
				setFeedBackMessage(MESSAGE_FAILURE_SAVEDIR);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}else
			{
			setFeedBackMessage(MESSAGE_SUCCESS_SAVEDIR);
				setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			}
		}
		*/
		default:
			break;
		}

	}

	public void getPrevCommand() {
		int index = _commandSelector - 1;
		if (index < preCommands.size() && index > -1) {
			String preCommand = preCommands.get(index);
			setFullInput(preCommand);
			onKeyReleased();
			_commandSelector = index;
		}
	}

	public void getNextCommand() {
		int index = _commandSelector + 1;
		if (index < preCommands.size() && index > -1) {
			String preCommand = preCommands.get(index);
			setFullInput(preCommand);
			onKeyReleased();
			_commandSelector = index;
		}
	}

	public void setFeedBackColor(int feedBackStatus) {
		if (_animation != null) {
			if (_animation.isRunning()) {
				_animation.cancel();
			}
			_animation = null;
		}
		_feedbackLabel.setOpacity(1.0);
		switch (feedBackStatus) {
		case FEEDBACK_STATUS_NORMAL: {
			_feedbackLabel.setId("cssCommandBarfeedback_normal");
			break;
		}
		case FEEDBACK_STATUS_CONFLICT: {
			_feedbackLabel.setId("cssCommandBarfeedback_conflict");
			break;
		}
		case FEEDBACK_STATUS_ERROR: {
			_feedbackLabel.setId("cssCommandBarfeedback_error");
			break;
		}
		default: {
			_feedbackLabel.setId("cssCommandBarfeedback_normal");
			break;
		}
		}
		_animation = new CommandBarAnimation(this);
		_animation.start();
	}

	public boolean updateCommandStatus(double _percentageDone) {
		double opacity = _feedbackLabel.getOpacity();
		if (opacity < 0) {
			_feedbackLabel.setOpacity(0);
			return true;
		} else {
			_feedbackLabel.setOpacity((1 - _percentageDone));
			return false;
		}
	}

}
