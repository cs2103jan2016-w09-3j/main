/**
 * @author Chan Yuan Shan
 * @@author A0125514N
 * 
 *  This class handles the building of the command bar and the feedback messgaes.
 */
package userInterface;

import java.util.ArrayList;
import dateParser.CommandParser;
import dateParser.CommandParser.COMMAND;
import dateParser.InputParser;
import dateParser.Pair;
import dateParser.XMLParser;
import entity.ResultSet;
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

	// Feedback status
	private static final int FEEDBACK_STATUS_ERROR = 0;
	private static final int FEEDBACK_STATUS_NORMAL = 1;
	private static final int FEEDBACK_STATUS_CONFLICT = 2;
	private static final int FEEDBACK_STATUS_PAST = 3;
	private static final int FEEDBACK_STATUS_CONFLICT_PAST = 4;

	// Feedback messages
	private static final String MESSAGE_FAILURE_INVALID = "you have entered an invalid command";

	private static final String MESSAGE_SUCCESS_ADD_TYPE_1 = "Successfully added %1$s to list.";
	private static final String MESSAGE_SUCCESS_ADD_TYPE_2 = "Successfully added %1$s to task list.";
	private static final String MESSAGE_SUCCESS_ADD_TYPE_3 = "Successfully added %1$s to floating task list.";
	private static final String MESSAGE_FAILURE_ADD_TYPE_1 = "Fail to add.";
	private static final String MESSAGE_FAILURE_ADD_TYPE_2 = "Fail to add task, task requires a name.";
	private static final String MESSAGE_FAILURE_ADD_TYPE_3 = "Fail to add task, Due date is before start date.";

	private static final String MESSAGE_SUCCESS_DELETE = "Successfully deleted %1$s.";
	private static final String MESSAGE_FAILURE_DELETE_TYPE_1 = "Task id required to delete.";
	private static final String MESSAGE_FAILURE_DELETE_TYPE_2 = "Fail to delete %1$s.";

	private static final String MESSAGE_SUCCESS_EDIT = "Successfully edited %1$s.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE_1 = "Fail to edit %1$s.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE_2 = "Fail to retrieve task.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE_3 = "Fail to edit task, task name is required.";
	private static final String MESSAGE_FAILURE_EDIT_TYPE_4 = "Fail to add task, Due date is before start date.";

	private static final String MESSAGE_SUCCESS_MARK = "Successfully mark %1$s as completed.";
	private static final String MESSAGE_FAILURE_MARK_TYPE_1 = "Fail to mark %1$s as completed.";
	private static final String MESSAGE_FAILURE_MARK_TYPE_2 = "Invalid task ID";

	private static final String MESSAGE_SUCCESS_SEARCH_TYPE_1 = "Search compelete with %1$s results.";
	private static final String MESSAGE_SUCCESS_SEARCH_TYPE_2 = "No results found.";
	private static final String MESSAGE_FAILURE_SEARCH_TYPE_1 = "No results found.";

	private static final String MESSAGE_FAILURE_JUMP_TYPE_1 = "No index to jump to.";
	private static final String MESSAGE_FAILURE_JUMP_TYPE_2 = "Task id required to jump to.";

	private static final String MESSAGE_SUCCESS_LINK = "Linked successfully.";
	private static final String MESSAGE_FAILURE_LINK_TYPE_1 = "Failed to link, IDs are not within range.";
	private static final String MESSAGE_FAILURE_LINK_TYPE_2 = "Failed to link.";

	private static final String MESSAGE_SUCCESS_UNDO_TYPE_1 = "successfully undo.";
	private static final String MESSAGE_FAILURE_UNDO_TYPE_1 = "There are no commands to undo.";

	private static final String MESSAGE_SUCCESS_SAVETO = "Saved to new file successfully.";
	private static final String MESSAGE_FAILURE_SAVETO = "Unable to create new directory and file. Please check for clashes or duplicate files.";

	private static final String MESSAGE_SUCCESS_THEME = "Successfully changed theme.";
	private static final String MESSAGE_FAILURE_THEME = "Invalid theme. Choose a theme from this list -> %1$s.";

	private static final String MESSAGE_CONFLICT = "Conflict detected.";
	private static final String MESSAGE_PAST = "Task has past deadline.";
	private static final String MESSAGE_CONFLICT_PAST = "Conflict detected and task has past deadline.";

	private static final String MESSAGE_SUCCESS_LOADFROM = "Loaded from %1$s file successfully.";
	private static final String MESSAGE_FAILURE_LOADFROM_TYPE_1 = "Unable to find %1$s. Please check that the file is available for reading.";
	private static final String MESSAGE_FAILURE_LOADFROM_TYPE_2 = "Json corrupted";
	private static final String MESSAGE_FAILURE_LOADFROM_TYPE_3 = "File not in json format.";

	// UserInterface values
	private static final int GAP_SIZE = 0;
	private static final double FEEDBACK_HEIGHT = 20;
	private static final int MAIN_PANE_LEFT_RIGHT_MARGIN = 0;
	private static final int TEXT_FIELD_WIDTH = 10;
	private static final int TWO = 2;
	private static final int ZERO = 0;
	private static final int NOTHING_SELECTED = -1;

	private static final String CSS_LABEL = "cssLabelsCommandBar";

	// Font
	static final int FONT_SIZE_FEEDBACK = 12;
	private static final Font FONT_FEEDBACK = new Font(PrimaryUserInterface.FONT_DEFAULT, FONT_SIZE_FEEDBACK);

	private static CommandBar _myInstance;
	private double _prefHeight;
	private double _prefWidth;
	private double _mainPaneHeight;
	private double _commandLabelHeight;

	private VBox _mainStructure;
	private Label _feedbackLabel;

	private GridPane _mainPane;
	private TextField _textField;
	private int _numberOfItems = ZERO;
	private int _selected;
	private ArrayList<Label> labels = new ArrayList<Label>();
	private ArrayList<String> preCommands = new ArrayList<String>();
	private int _commandSelector;

	private ArrayList<String> _allSessionCmds = new ArrayList<String>();
	private String fullInput = "";
	private int _feedBackCounter;

	/**
	 * Create an instance of the CommandBar only if there isn't an instance
	 * already.
	 * 
	 * @param _commandBarWidth
	 * @param commandBarHeigth
	 * @return CommandBar
	 */
	public static CommandBar getInstance(double _commandBarWidth, int commandBarHeigth) {
		if (_myInstance == null) {
			_myInstance = new CommandBar(_commandBarWidth, commandBarHeigth);
			return _myInstance;
		}
		return null;
	}

	/**
	 * Builds the CommandBar and all the main components.
	 * 
	 * @param preWidth
	 * @param preHeight
	 */
	private CommandBar(double preWidth, double preHeight) {
		_prefHeight = preHeight;
		_prefWidth = preWidth;
		_mainPaneHeight = _prefHeight - FEEDBACK_HEIGHT;
		_commandLabelHeight = _mainPaneHeight / TWO;
		_selected = NOTHING_SELECTED;
		initilizeMainStructure();
		initilizeFeedbackBar();
		initializeMainPane();
		initializeTextBox();
		_mainPane.add(_textField, _numberOfItems++, ZERO);
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
		_feedbackLabel.getStyleClass().add(CSS_LABEL);
		_feedbackLabel.setMinWidth(_prefWidth);
		_feedbackLabel.setMaxHeight(FEEDBACK_HEIGHT);
		_feedbackLabel.setMinHeight(FEEDBACK_HEIGHT);
		_feedbackLabel.setFont(FONT_FEEDBACK);
		_feedbackLabel.setAlignment(Pos.CENTER_LEFT);
		_feedbackLabel.setId("cssCommandBarfeedback_normal");
		_feedbackLabel.setOpacity(ZERO);
		_mainStructure.getChildren().add(_feedbackLabel);
	}

	public void initializeMainPane() {
		_mainPane = new GridPane();
		_mainPane.setId("cssCommandBarContentZone");
		_mainPane.setMinHeight(_mainPaneHeight);
		_mainPane.setMaxHeight(_mainPaneHeight);
		_mainPane.setAlignment(Pos.CENTER_LEFT);
		_mainPane.setHgap(GAP_SIZE);
		VBox.setMargin(_mainPane, new Insets(ZERO, MAIN_PANE_LEFT_RIGHT_MARGIN, ZERO, MAIN_PANE_LEFT_RIGHT_MARGIN));
		_mainStructure.getChildren().add(_mainPane);
	}

	public void initializeTextBox() {
		_textField = new TextField();
		_textField.setId("cssCommandMainUserInput");
		_textField.setMaxWidth(TEXT_FIELD_WIDTH);
		_textField.setAlignment(Pos.CENTER_LEFT);
		_textField.setPrefHeight(_commandLabelHeight);
		_textField.setPadding(new Insets(ZERO, ZERO, ZERO, ZERO));
		_textField.setBorder(null);
	}

	/**
	 * Gets the input from the user and concatenate to the full String.
	 */
	public void concatToFullString() {
		String input = _textField.getText();
		if (!input.equals("")) {
			if (_selected == NOTHING_SELECTED) {
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

	/**
	 * Deletes a character in the fullString base on where the selector is.
	 */
	public void deleteKey() {
		if (_selected != NOTHING_SELECTED) {
			String front = getFrontString();
			String current = currentString();
			String back = getBackString();
			current = current.substring(0, current.length() - 1);
			if (current.length() == ZERO) {
				_selected--;
			}
			fullInput = rebuildString(front, current, back);
			onKeyReleased();
		} else {
			if (fullInput.length() > ZERO) {
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

	/**
	 * Gets the user input and concatenate to the fullString and run through the
	 * parser. Rebuilds the command bar base on the parser.
	 */
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

	/**
	 * Creates the new labels and place them inside the commandBar.
	 * 
	 * @param itemsToAdd
	 */
	private void addItemsToBar(ArrayList<Node> itemsToAdd) {
		_mainPane.getChildren().clear();
		labels.clear();
		_numberOfItems = 0;
		if (itemsToAdd.size() == 0) {
			_selected = NOTHING_SELECTED;
		}
		if (_selected == NOTHING_SELECTED) {
			itemsToAdd.add(_textField);
		} else {
			itemsToAdd.add(_selected + 1, _textField);
		}

		for (int i = 0; i < itemsToAdd.size(); i++) {
			_mainPane.add(itemsToAdd.get(i), _numberOfItems++, 1);
			if (itemsToAdd.get(i) instanceof Label) {
				Label l = (Label) itemsToAdd.get(i);
				GridPane.setMargin(l, new Insets(ZERO, ZERO, ZERO, TWO));
				labels.add(l);
			}
		}
	}

	/**
	 * Build the individual label base on the XML tag.
	 * 
	 * @param item
	 * @return Label
	 */
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

	/**
	 * Builds a basic structure that all labels share.
	 * 
	 * @return
	 */
	public Label buildLabelSkeleton() {
		Label label = new Label();
		label.setMinHeight(_commandLabelHeight);
		label.setAlignment(Pos.BASELINE_RIGHT);
		return label;
	}

	private Label buildNormalLabel(ArrayList<String> other) {
		Label label = buildLabelSkeleton();
		label.setText(other.get(0));
		label.setId("cssCommandOther");
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
			case SAVETO: {
				label.setId("cssCommandSaveDir");
				break;
			}
			case UNDO: {
				label.setId("cssCommandUndo");
				break;
			}
			case THEME: {
				label.setId("cssCommandTheme");
				break;
			}
			case LOADFROM: {
				label.setId("cssCommandLoadFrom");
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

	/**
	 * Gets the tasks that the parser has build from the user input.
	 * 
	 * @return lists of TaskEntity
	 */
	public ArrayList<TaskEntity> getTasks() {
		InputParser parser = new InputParser(fullInput);
		return parser.getTask();
	}

	/**
	 * Gets the partial input that the parser has identified.
	 * 
	 * @return list of TaskEntity
	 */
	public ArrayList<TaskEntity> getTasksPartialInput() {
		InputParser parser = new InputParser(fullInput);
		parser.removeId();
		return parser.getTask();
	}

	/**
	 * Gets the ID that the parser has identified.
	 * 
	 * @return id
	 */
	public String getId() {
		String returnVal = null;
		InputParser parser = new InputParser(fullInput);
		returnVal = parser.getID();
		return returnVal;
	}

	/**
	 * Gets the String that the parser has identified as the search String.
	 * 
	 * @return search string
	 */
	public String getSearchStr() {
		InputParser parser = new InputParser(fullInput);
		return parser.getSearchString();
	}

	/**
	 * Gets the ids of the two task that needs to be link which the parser has
	 * identified.
	 * 
	 * @return Pair
	 */
	public Pair<String, String> getLinkId() {
		Pair<String, String> returnVal = null;
		InputParser parser = new InputParser(fullInput);
		returnVal = parser.getLinkID();
		return returnVal;
	}

	/**
	 * Sets the main textField handler to the given eventHandler.
	 * 
	 * @param mainEventHandler
	 * @param secondaryEventHandler
	 */
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
	 * Change the selector and underline the label that is being selected.
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

	/**
	 * Resets the commandBar to get ready for new command.
	 */
	public void reset() {
		_selected = -1;
		setFullInput("");
		ArrayList<Node> temp = new ArrayList<Node>();
		addItemsToBar(temp);
	}

	/**
	 * Shows the feedback message.
	 * 
	 * @param cmdType
	 * @param resultSet
	 * @param msg
	 */
	public void showFeedBackMessage(COMMAND cmdType, ResultSet resultSet, String msg) {
		switch (cmdType) {
		case INVALID: {
			setFeedBackMessage(MESSAGE_FAILURE_INVALID);
			setFeedBackColor(FEEDBACK_STATUS_ERROR);
			break;
		}
		case ADD: {
			if (resultSet != null) {
				String feedBackMsg = "";
				if (resultSet.isSuccess()) {
					if (resultSet.getIndex() > -1) {
						feedBackMsg = feedBackMsg.concat(String.format(MESSAGE_SUCCESS_ADD_TYPE_1, msg));
					} else {
						if (resultSet.getView() == ResultSet.ASSOCIATE_VIEW
								|| resultSet.getView() == ResultSet.EXPANDED_VIEW
								|| resultSet.getView() == ResultSet.TASK_VIEW) {
							feedBackMsg = feedBackMsg.concat(String.format(MESSAGE_SUCCESS_ADD_TYPE_2, msg));
						} else if (resultSet.getView() == ResultSet.FLOATING_VIEW) {
							feedBackMsg = feedBackMsg.concat(String.format(MESSAGE_SUCCESS_ADD_TYPE_3, msg));
						}
					}

					String feedback = processFeedBackColor(resultSet.getStatus());
					if (feedback != null) {
						feedBackMsg = feedBackMsg.concat(" ").concat(feedback);
					}
					setFeedBackMessage(feedBackMsg);
				} else {
					if (resultSet.getStatus() == ResultSet.STATUS_INVALID_NAME) {
						setFeedBackMessage(MESSAGE_FAILURE_ADD_TYPE_2);
					} else if (resultSet.getStatus() == ResultSet.STATUS_INVALID_DATE) {
						setFeedBackMessage(MESSAGE_FAILURE_ADD_TYPE_3);
					} else {
						setFeedBackMessage(MESSAGE_FAILURE_ADD_TYPE_1);
					}
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			}
			break;
		}
		case DELETE: {
			if (resultSet == null) {
				setFeedBackMessage(MESSAGE_FAILURE_DELETE_TYPE_1);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			} else {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_DELETE, msg));
					processFeedBackColor(resultSet.getStatus());
				} else {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_DELETE_TYPE_2, msg));
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			}
			break;
		}
		case EDIT: {
			if (resultSet == null) {
				setFeedBackMessage(MESSAGE_FAILURE_EDIT_TYPE_2);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			} else {
				if (resultSet.isSuccess()) {
					String feedback = processFeedBackColor(resultSet.getStatus());
					String feedBackMsg = String.format(MESSAGE_SUCCESS_EDIT, msg);
					if (feedback != null) {
						feedBackMsg = feedBackMsg.concat(" ").concat(feedback);
					}
					setFeedBackMessage(feedBackMsg);
				} else {

					if (resultSet.getStatus() == ResultSet.STATUS_INVALID_NAME) {
						setFeedBackMessage(MESSAGE_FAILURE_EDIT_TYPE_3);
					} else if (resultSet.getStatus() == ResultSet.STATUS_INVALID_DATE) {
						setFeedBackMessage(MESSAGE_FAILURE_EDIT_TYPE_4);
					} else {
						setFeedBackMessage(String.format(MESSAGE_FAILURE_EDIT_TYPE_1, msg));
					}
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			}
			break;
		}
		case DONE: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_MARK, msg));
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_MARK_TYPE_1, msg));
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			} else {
				setFeedBackMessage(MESSAGE_FAILURE_MARK_TYPE_2);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case SEARCH: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					if (resultSet.getSearchCount() > 0) {
						setFeedBackMessage(String.format(MESSAGE_SUCCESS_SEARCH_TYPE_1, resultSet.getSearchCount()));
					} else {
						setFeedBackMessage(MESSAGE_SUCCESS_SEARCH_TYPE_2);
						setFeedBackColor(FEEDBACK_STATUS_ERROR);
					}
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					setFeedBackMessage(MESSAGE_FAILURE_SEARCH_TYPE_1);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			}
			break;
		}
		case JUMP: {
			if (!resultSet.isSuccess()) {
				if (resultSet.getIndex() == PrimaryUserInterface.TYPE_1) {
					setFeedBackMessage(MESSAGE_FAILURE_JUMP_TYPE_1);
				} else {
					setFeedBackMessage(MESSAGE_FAILURE_JUMP_TYPE_2);
				}
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case LINK: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(MESSAGE_SUCCESS_LINK);
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					if (resultSet.getIndex() == -1) {
						setFeedBackMessage(MESSAGE_FAILURE_LINK_TYPE_1);
						setFeedBackColor(FEEDBACK_STATUS_ERROR);
					} else {
						setFeedBackMessage(MESSAGE_FAILURE_LINK_TYPE_2);
						setFeedBackColor(FEEDBACK_STATUS_ERROR);
					}
				}
			}
			break;
		}

		case UNDO: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(MESSAGE_SUCCESS_UNDO_TYPE_1);
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					setFeedBackMessage(MESSAGE_FAILURE_UNDO_TYPE_1);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			} else {
				setFeedBackMessage(MESSAGE_FAILURE_UNDO_TYPE_1);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case SAVETO: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(MESSAGE_SUCCESS_SAVETO);
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					setFeedBackMessage(MESSAGE_FAILURE_SAVETO);
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			} else {
				setFeedBackMessage(MESSAGE_FAILURE_SAVETO);
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case LOADFROM: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(String.format(MESSAGE_SUCCESS_LOADFROM, msg));
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					if (resultSet.getStatus() == ResultSet.STATUS_JSON_ERROR) {
						setFeedBackMessage(MESSAGE_FAILURE_LOADFROM_TYPE_3);
					} else if (resultSet.getStatus() == ResultSet.STATUS_BAD) {
						setFeedBackMessage(MESSAGE_FAILURE_LOADFROM_TYPE_2);
					} else {
						setFeedBackMessage(String.format(MESSAGE_FAILURE_LOADFROM_TYPE_1, msg));
					}
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			} else {
				setFeedBackMessage(String.format(MESSAGE_FAILURE_LOADFROM_TYPE_1, msg));
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
			break;
		}
		case THEME: {
			if (resultSet != null) {
				if (resultSet.isSuccess()) {
					setFeedBackMessage(MESSAGE_SUCCESS_THEME);
					setFeedBackColor(FEEDBACK_STATUS_NORMAL);
				} else {
					setFeedBackMessage(String.format(MESSAGE_FAILURE_THEME, msg));
					setFeedBackColor(FEEDBACK_STATUS_ERROR);
				}
			} else {
				setFeedBackMessage(String.format(MESSAGE_FAILURE_THEME, msg));
				setFeedBackColor(FEEDBACK_STATUS_ERROR);
			}
		}
		default:
			break;
		}

	}

	/**
	 * Process the feedback color base on the status.
	 * 
	 * @param status
	 * @return condition (conflict, past, conflict and past)
	 */
	public String processFeedBackColor(int status) {
		if (status == ResultSet.STATUS_GOOD) {
			setFeedBackColor(FEEDBACK_STATUS_NORMAL);
			return null;
		} else if (status == ResultSet.STATUS_CONFLICT) {
			setFeedBackColor(FEEDBACK_STATUS_CONFLICT);
			return MESSAGE_CONFLICT;
		} else if (status == ResultSet.STATUS_PAST) {
			setFeedBackColor(FEEDBACK_STATUS_PAST);
			return MESSAGE_PAST;
		} else if (status == ResultSet.STATUS_CONFLICT_AND_PAST) {
			setFeedBackColor(FEEDBACK_STATUS_CONFLICT_PAST);
			return MESSAGE_CONFLICT_PAST;
		}
		return null;
	}

	/**
	 * Gets the previous command that was executed, builds the labels and place
	 * them in the commandbar.
	 */
	public void getPrevCommand() {
		int index = _commandSelector - 1;
		if (index < preCommands.size() && index > -1) {
			String preCommand = preCommands.get(index);
			setFullInput(preCommand);
			onKeyReleased();
			_commandSelector = index;
		}
	}

	/**
	 * Gets the next command that was executed, builds the labels and place them
	 * in the commandbar.
	 */
	public void getNextCommand() {
		int index = _commandSelector + 1;
		if (index < preCommands.size() && index > -1) {
			String preCommand = preCommands.get(index);
			setFullInput(preCommand);
			onKeyReleased();
			_commandSelector = index;
		}
	}

	/**
	 * Sets the feed back area color base on the feedBackStatus.
	 * 
	 * @param feedBackStatus
	 */
	public void setFeedBackColor(int feedBackStatus) {
		switch (feedBackStatus) {
		case FEEDBACK_STATUS_NORMAL: {
			_feedbackLabel.setId("cssCommandBarfeedback_normal");
			break;
		}
		case FEEDBACK_STATUS_CONFLICT: {
			_feedbackLabel.setId("cssCommandBarfeedback_conflict");
			break;
		}
		case FEEDBACK_STATUS_PAST: {
			_feedbackLabel.setId("cssCommandBarfeedback_past");
			break;
		}
		case FEEDBACK_STATUS_CONFLICT_PAST: {
			_feedbackLabel.setId("cssCommandBarfeedback_conflict_past");
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
		CommandBarAnimation.start(this);
	}

	/**
	 * Reset the feedback to the default color and opacity.
	 * 
	 * @param feedCounter
	 */
	public void resetFeedBack(int feedCounter) {
		_feedBackCounter = feedCounter;
		_feedbackLabel.setOpacity(1.0);
	}

	/**
	 * Reduces the opacity of the feedBack component.
	 * 
	 * @param _percentageDone
	 * @param count
	 * 
	 * @return true only if opacity is 0s
	 */
	public boolean updateCommandStatus(double _percentageDone, int count) {
		if (_feedBackCounter == count) {
			double opacity = _feedbackLabel.getOpacity();
			if (opacity < 0) {
				_feedbackLabel.setOpacity(0);
				return true;
			} else {
				_feedbackLabel.setOpacity((1 - _percentageDone));
				return false;
			}
		} else {
			return true;
		}
	}

}
