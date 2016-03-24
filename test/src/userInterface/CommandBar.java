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
import javafx.scene.web.HTMLEditor;
import javafx.scene.Node;
import javafx.application.Platform;
import org.jsoup.Jsoup;;

public class CommandBar {
	private GridPane _mainPane;
	//private TextField _textField;
	// private TextFlow _textField;
	private int _numberOfItems = 0;
	HTMLEditor _textField;

	private ArrayList<String> _allSessionCmds = new ArrayList<String>();

	public CommandBar() {
		initializeMainPane();
		//initializeTextBox();
		initializeHTMLEditor();
		//_mainPane.add(_textField, _numberOfItems++, 0);
		_mainPane.add(_textField, _numberOfItems++, 0);
	}

	public void initializeHTMLEditor() {
		_textField = new HTMLEditor();
		_textField.setId("testUserInput");
		_textField.setPrefWidth(800.0);
		hideHTMLEditorToolbars(_textField);
		_textField.setBorder(null);
	}
	


	@SuppressWarnings("restriction")
	public void hideHTMLEditorToolbars(final HTMLEditor editor)
	{
	    editor.setVisible(false);
	    Platform.runLater(new Runnable()
	    {
	        @Override
	        public void run()
	        {
	            Node[] nodes = editor.lookupAll(".tool-bar").toArray(new Node[0]);
	            for(Node node : nodes)
	            {
	                node.setVisible(false);
	                node.setManaged(false);
	            }
	            editor.setVisible(true);
	        }
	    });
	}



	public void initializeMainPane() {
		_mainPane = new GridPane();
		_mainPane.setMaxHeight(30);
		_mainPane.setPadding(new Insets(4, 2, 0, 2));
		_mainPane.setStyle("-fx-background-color: #FFFFFF;");
		_mainPane.setAlignment(Pos.CENTER);
	}

	/*
	public void initializeTextBox() {
		_textField = new TextField();
		_textField.setId("mainUserInput");
		_textField.setPrefWidth(800.0);
		_textField.setBorder(null);
	}
	*/
	public void onKeyReleased(String input) {
		// System.out.println(input);
		InputParser parser = new InputParser(XMLParser.removeAllTags(input));
		System.out.println(input);
		parser.addXML();
		String textToShow = parser.getInput();
		_textField.setHtmlText(textToShow);
		_mainPane.requestFocus();
		_textField.requestFocus();
		//_textField.setText(textToShow);
		//_textField.positionCaret(textToShow.length() - 1);
	}
	
	public static String removeHtml(String html) {
	    return Jsoup.parse(html).text();
	}

	public COMMAND onEnter(String input) {
		onKeyReleased(input);
		InputParser parser = new InputParser(XMLParser.removeAllTags(input));
		COMMAND cmd = parser.getCommand();
		return cmd;
	}

	public ArrayList<TaskEntity> getTasks(String input) {
		InputParser parser = new InputParser(XMLParser.removeAllTags(input));
		return parser.getTask();
	}

	public String getId(String input) {
		String returnVal = null;
		InputParser parser = new InputParser(XMLParser.removeAllTags(input));
		returnVal = parser.getID();
		return returnVal;
	}

	public void setTextFieldHandler(EventHandler<KeyEvent> mainEventHandler,
			EventHandler<KeyEvent> keyReleasedEventHandler) {
		_textField.setOnKeyPressed(mainEventHandler);
		_textField.setOnKeyReleased(keyReleasedEventHandler);
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
/*
	public TextField getTextField() {
		return _textField;
	}
*/
	public HTMLEditor getTextField() {
		return _textField;
	}
	
	public GridPane getCommandBar() {
		return _mainPane;
	}

	public ArrayList<String> get_allSessionCmds() {
		return _allSessionCmds;
	}

}
