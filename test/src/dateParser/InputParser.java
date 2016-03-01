package dateParser;

import java.util.Scanner;

public class InputParser {
	private String input;
	DateNLP dateParser;
	CommandParser cmdParser;

	public InputParser(String input) {
		this.input = input;
		dateParser = new DateNLP();
		cmdParser = new CommandParser();
	}
	
	public void addXML(){
		addXMLCmd();
		addXMLDate();
	}
	
	public void addXMLDate(){
		input = dateParser.XMLDate(input);
	}
	
	public void addXMLCmd(){
		input = cmdParser.xmlFirstWord(input);
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public static void main(String args[]) {
		while(true){
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			InputParser parser = new InputParser(input);
			parser.addXML();
			System.out.println(parser.getInput());
		}
	}
}
