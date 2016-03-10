package dateParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import dateParser.CommandParser.COMMAND;
import entity.TaskEntity;

public class InputParser {
	private String input;
	private DateParser dateParser;
	private CommandParser cmdParser;
	private InformationParser infoParser;

	/**
	 * Intializes parser and creates individual command parsers
	 * @param input String to be parsed
	 */
	public InputParser(String input) {
		this.input = input;
		dateParser = new DateParser();
		cmdParser = new CommandParser();
		infoParser = new InformationParser();
	}


	/**
	 * changes input to XML form
	 */
	public void addXML() {
		//DO NOT CHANGE ORDER!!
		addXMLDate();
		addXMLCmd();
		addXMLTitleDesc();
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
		return cmdParser.getCommand(input);
	}
	
	/**
	 * generates a list of tasks from the input, with no time
	 * @return ArrayList of tasks
	 */
	public ArrayList<TaskEntity> getTask(){
		input = XMLParser.removeAllTags(input);
		this.input = input;
		ArrayList<TaskEntity> tasks  = new ArrayList<TaskEntity>();
		List<Date> dates = dateParser.parseToList(input);
		addXMLDate();
		addXMLCmd();
		infoParser.setInformation(this.input);
		String name = infoParser.getTitle();
		String desc = infoParser.getDescription();
		for(int i=0; i<dates.size(); i++){
			Calendar c = Calendar.getInstance();
			dates.get(i).setHours(0);
			dates.get(i).setMinutes(0);
			dates.get(i).setSeconds(0);
			c.setTime(dates.get(i));
			TaskEntity toAdd = new TaskEntity(name, c, false, desc);
			tasks.add(toAdd);
		}
		return tasks;
	}
	
	public static void main(String args[]) {
		while (true) {
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			System.out.println(ParserCommons.getLastWord(input));
			InputParser parser = new InputParser(input);
			parser.addXML();
			System.out.println(parser.getInput());
		}
	}
}
