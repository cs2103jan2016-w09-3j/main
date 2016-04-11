//@@author a0125415n
package test.parser;
import static org.junit.Assert.*;
import org.junit.Test;

import parser.CommandParser;
import parser.IdParser;
import parser.Pair;

public class IdParserTest {
	IdParser id = new IdParser();
	
	@Test
	public void testIDParser_GetID(){
		String toTest = id.getID("ID123");
		String expected = "123";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testIDParser_GetID_noInput(){
		String toTest = id.getID("asfd");
		String expected = null;
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testIDParser_GetLinkID(){
		Pair<String,String> toTest = id.getLinkID("ID123-ID234");
		Pair<String,String> expected = new Pair("123","234");
		assertEquals(toTest.getFirst(),expected.getFirst());
		assertEquals(toTest.getSecond(),expected.getSecond());
	}
	
	@Test
	public void testIDParser_GetLinkID_invalidInput(){
		Pair<String,String> toTest = id.getLinkID("123-234");
		Pair<String,String> expected = new Pair(null,null);
		assertEquals(toTest.getFirst(),expected.getFirst());
		assertEquals(toTest.getSecond(),expected.getSecond());
	}
	
	@Test
	public void testIDParser_xmlID(){
		String toTest = id.xmlID("ID1234");
		String expected = "<IdXmL>ID1234</IdXmL>";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testIDParser_xmlID_invalidInput(){
		String toTest = id.xmlID("1234");
		String expected = "1234";
		assertEquals(toTest,expected);
	}
}
