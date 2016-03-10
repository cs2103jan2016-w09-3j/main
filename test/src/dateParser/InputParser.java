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
	DateNLP dateParser;
	CommandParser cmdParser;
	InformationParser infoParser;

	public InputParser(String input) {
		this.input = input;
		dateParser = new DateNLP();
		cmdParser = new CommandParser();
		infoParser = new InformationParser();
	}

	//DO NOT CHANGE ORDER!!
	public void addXML() {
		addXMLDate();
		addXMLCmd();
		addXMLTitleDesc();
	}

	public void addXMLTitleDesc() {
		input = infoParser.xmlTitleAndDesc(input);
	}

	public void addXMLDate() {
		input = dateParser.xmlDate(input);
	}

	public void addXMLCmd() {
		input = cmdParser.xmlFirstWord(input);
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	public COMMAND getCommand(){
		return cmdParser.getCommand(input);
	}
	
	public static String getLastWord(String input){
		String returnVal = null;
		String [] seperated = input.split(" ");
		if (seperated.length>0){
			returnVal = seperated[seperated.length-1];
		}
		return returnVal;
	}

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
			System.out.println(InputParser.getLastWord(input));
			InputParser parser = new InputParser(input);
			parser.addXML();
			System.out.println(parser.getInput());
		}
	}
}
