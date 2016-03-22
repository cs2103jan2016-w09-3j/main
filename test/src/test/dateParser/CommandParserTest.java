
package test.dateParser;

import static org.junit.Assert.*;
import org.junit.*;
import dateParser.CommandParser;

public class CommandParserTest {
	CommandParser cp = new CommandParser();
	@Test
	public void testGetCommand(){
		assertTrue(CommandParser.COMMAND.ADD.equals(cp.getCommand("add something something")));
		assertTrue(CommandParser.COMMAND.DELETE.equals(cp.getCommand("delete something something")));
		assertTrue(CommandParser.COMMAND.EDIT.equals(cp.getCommand("edit something something")));
		assertTrue(CommandParser.COMMAND.EXIT.equals(cp.getCommand("exit something something")));
		assertTrue(CommandParser.COMMAND.INVALID.equals(cp.getCommand("munchkin something something")));
	}
	
	@Test
	public void testAddXML(){
		assertEquals("<cmd>add</cmd> basketball",cp.xmlFirstWord("add basketball"));
	}
}
