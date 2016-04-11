//@@author A0125415N
package test.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import parser.CommandParser;

public class JUnitCommandParser {
	CommandParser cp = new CommandParser();
	@Test
	public void testCommandParser_GetAddCommand(){
		assertTrue(CommandParser.COMMAND.ADD.equals(cp.getCommand("add something something")));
	}
	
	@Test
	public void testCommandParser_GetDeleteCommand(){
		assertTrue(CommandParser.COMMAND.DELETE.equals(cp.getCommand("delete something something")));
	}
	
	@Test
	public void testCommandParser_GetEditCommand(){
		assertTrue(CommandParser.COMMAND.EDIT.equals(cp.getCommand("edit something something")));
	}
	
	@Test
	public void testCommandParser_GetExitCommand(){
		assertTrue(CommandParser.COMMAND.EXIT.equals(cp.getCommand("exit something something")));
	}
	
	@Test
	public void testCommandParser_GetMainCommand(){
		assertTrue(CommandParser.COMMAND.MAIN.equals(cp.getCommand("main something something")));
	}
	
	@Test
	public void testCommandParser_GetHideCommand(){
		assertTrue(CommandParser.COMMAND.HIDE.equals(cp.getCommand("hide something something")));
	}
	
	@Test
	public void testCommandParser_GetShowCommand(){
		assertTrue(CommandParser.COMMAND.SHOW.equals(cp.getCommand("show something something")));
	}
	
	@Test
	public void testCommandParser_GetFloatCommand(){
		assertTrue(CommandParser.COMMAND.FLOAT.equals(cp.getCommand("float something something")));
	}
	
	@Test
	public void testCommandParser_GetSearchCommand(){
		assertTrue(CommandParser.COMMAND.SEARCH.equals(cp.getCommand("search something something")));
	}
	
	@Test
	public void testCommandParser_GetJumpCommand(){
		assertTrue(CommandParser.COMMAND.JUMP.equals(cp.getCommand("jump something something")));
	}
	
	@Test
	public void testCommandParser_GetLinkCommand(){
		assertTrue(CommandParser.COMMAND.LINK.equals(cp.getCommand("link something something")));
	}
	
	@Test
	public void testCommandParser_GetDoneCommand(){
		assertTrue(CommandParser.COMMAND.DONE.equals(cp.getCommand("done something something")));
	}
	
	@Test
	public void testCommandParser_GetSaveToCommand(){
		assertTrue(CommandParser.COMMAND.SAVETO.equals(cp.getCommand("saveto something something")));
	}
	
	@Test
	public void testCommandParser_GetLoadFromCommand(){
		assertTrue(CommandParser.COMMAND.LOADFROM.equals(cp.getCommand("loadfrom something something")));
	}
	
	@Test
	public void testCommandParser_GetUndoCommand(){
		assertTrue(CommandParser.COMMAND.UNDO.equals(cp.getCommand("undo something something")));
	}

	@Test
	public void testCommandParser_GetThemeCommand(){
		assertTrue(CommandParser.COMMAND.THEME.equals(cp.getCommand("theme something something")));
	}
	
	@Test
	public void testCommandParser_GetInvalidCommand(){
		assertTrue(CommandParser.COMMAND.INVALID.equals(cp.getCommand("munchkin something something")));
		assertTrue(CommandParser.COMMAND.INVALID.equals(cp.getCommand("")));
	}
	
	@Test
	public void testCommandParser_AddXMLFirstWord(){
		assertEquals("<CmDxMl>add</CmDxMl> basketball",cp.xmlFirstWord("add basketball"));
	}
	
	@Test
	public void testCommandParser_AddXMLFirstWord_NoInput(){
		assertEquals("",cp.xmlFirstWord(""));
	}
}
