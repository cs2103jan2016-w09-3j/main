package dateParser;

import java.util.Scanner;

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
	
	public static String getLastWord(String input){
		String returnVal = null;
		String [] seperated = input.split(" ");
		if (seperated.length>0){
			returnVal = seperated[seperated.length-1];
		}
		return returnVal;
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
