package dateParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import dateParser.CommandParser.COMMAND;
import entity.TaskEntity;
import junit.framework.Assert;

public class InputParser {
	private final int EXPECT_CMD = 0;
	private final int EXPECT_INFO = 1;
	private String input;
	private DateParser dateParser;
	private CommandParser cmdParser;
	private InformationParser infoParser;
	private IdParser idParser;
	private GarbageCollectorParser gcParser;
	
	//private static Logger logger = Logger.getLogger("InputParser");

	/**
	 * Intializes parser and creates individual command parsers
	 * @param input String to be parsed
	 */
	public InputParser(String input) {
		/**
		try{
			Handler fh = new FileHandler("inputParser.log");
			logger.addHandler(fh);
			logger.setLevel(Level.FINEST);
		}catch(IOException e){
			
		}
		logger.log(Level.INFO,"InputParser init");
		
		**/
		input = XMLParser.removeAllTags(input);
		setInput(input);
		dateParser = new DateParser();
		cmdParser = new CommandParser();
		infoParser = new InformationParser();
		idParser = new IdParser();
		gcParser = new GarbageCollectorParser();
	}


	/**
	 * changes input to XML form
	 */
	public void addXML() {
		//DO NOT CHANGE ORDER!!
		//logger.log(Level.INFO,"Add xml to input");
		assert (input!=null) : "Input is null";

		input = XMLParser.removeAllTags(input);
		/*
		Scanner sc = new Scanner(input);
		String temp = "";
		String returnStr = "";
		int type = 0;
		while(sc.hasNext()){
			temp+=sc.next();
			if (type == EXPECT_CMD){
				returnStr += cmdParser.xmlFirstWord(temp);
				temp = "";
				type++;
			}else if (type == EXPECT_INFO){
				// handle ID
				if(!dateParser.hasDate(temp))
				{
					returnStr += infoParser.xmlTitleAndDesc(temp);
				}else{
					
				}
			}
		}
	*/

		COMMAND given =cmdParser.getCommand(input);
		System.out.println("before"+input);
		addXMLDate();//problem with quotes lie here!
		System.out.println("after date"+input);
		addXMLCmd();
		System.out.println("after cmd"+input);
		if(given != COMMAND.SEARCH){
			addXMLID();
			System.out.println("after ID"+input);
			addXMLTitleDesc();
			System.out.println("after title"+input);
		}
		addXMLGarbage();
		System.out.println("after"+input);
		
	}
	
	public void removeId(){
		input = XMLParser.removeAllTags(input);
		addXMLID();
		input = XMLParser.removeAllAttributes(input);
		
	}
	
	private void addXMLGarbage(){
		input = gcParser.xmlAllOthers(input);
	}
	
	private void addXMLID() {
		input = idParser.xmlID(input);
	}

	public String getSearchString(){
		input = XMLParser.removeAllTags(input);
		addXMLCmd();
		input = XMLParser.removeAllAttributes(input);
		return input.trim();
	}
	/**
	 * adds xml to title
	 */
	private void addXMLTitleDesc() {
		input = infoParser.xmlTitleAndDesc(input);
	}

	/**
	 * adds xml to date
	 */
	private void addXMLDate() {
		input = dateParser.xmlDate(input);
	}

	/**
	 * adds xml to cmd(first word)
	 */
	private void addXMLCmd() {
		input = cmdParser.xmlFirstWord(input);
	}

	/**
	 * getter for input
	 * @return input
	 */
	public String getInput() {
		return input;
	}

	/**
	 * setter for input
	 * @param input
	 */
	public void setInput(String input) {
		this.input = input;		
	}
	
	/**
	 * Parses the input and returns an enum COMMAND
	 * @return respective COMMAND 
	 */
	public COMMAND getCommand(){
		COMMAND returnEnum = cmdParser.getCommand(input);
		return returnEnum;
	}
	
	/**
	 * generates a list of tasks from the input, with no time
	 * @return ArrayList of tasks
	 */
	public ArrayList<TaskEntity> getTask(){
		input = XMLParser.removeAllTags(input);
		input = input.replace("edit", "");
		ArrayList<TaskEntity> tasks  = new ArrayList<TaskEntity>();
		if(input.trim().equals("")){
			//logger.log(Level.WARNING, "Input is empty", new IllegalArgumentException("input is empty"));
		}else{
			List<Date> dates = dateParser.parseToList(input);
			addXMLDate();
			addXMLCmd();
			infoParser.setInformation(this.input);
			String name = infoParser.getTitle();
			String desc = infoParser.getDescription();
			if(dates.size() == 0){
				TaskEntity toAdd = new TaskEntity(name, null, false, desc);
				toAdd.setFloating(true);
				tasks.add(toAdd);
			}
			else{
				for(int i=0; i<dates.size(); i++){
					Calendar c = Calendar.getInstance();
					c.setTime(dates.get(i));
					c.clear(Calendar.SECOND);
					c.clear(Calendar.MILLISECOND);
					TaskEntity toAdd = new TaskEntity(name, c, false, desc);
					tasks.add(toAdd);
				}
			}
		}
		return tasks;
	}
	
	public String getID(){
		return idParser.getID(XMLParser.removeAllTags(input));
	}
	
	public Pair<String,String> getLinkID(){
		return idParser.getLinkID(XMLParser.removeAllTags(input));
	}
	
	public static void main(String args[]) {
		while (true) {
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			//System.out.println(ParserCommons.getLastWord(input));
			InputParser parser = new InputParser(input);
			parser.addXML();
			parser.getTask();
			System.out.println(parser.getInput());
		}
	}
}
