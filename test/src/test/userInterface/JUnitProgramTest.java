package test.userInterface;

import static org.junit.Assert.*;

import org.junit.Test;

import dateParser.InputParser;
import userInterface.CommandBar;
import dateParser.CommandParser.COMMAND;

public class JUnitProgramTest {

	@Test
	public void test() {
		runCommand("asd");
	}

	public void runCommand(String rawString) {
		InputParser parser = new InputParser(rawString);
		COMMAND cmd = parser.getCommand();
		if (cmd.equals(COMMAND.ADD)) {
		} 
		
	}
}
