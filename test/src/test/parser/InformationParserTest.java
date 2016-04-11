//@@author A0125415N
package test.parser;
import static org.junit.Assert.*;
import org.junit.Test;

import parser.CommandParser;
import parser.InformationParser;

public class InformationParserTest {
	InformationParser info = new InformationParser();
	
	@Test
	public void testInformationParser_XMLTitleDesc_bothTitleAndDesc(){
		String toTest = info.xmlTitleAndDesc("blah:something");
		String expected = "<TiTlExMl>blah</TiTlExMl><OtHeRxMl>:</OtHeRxMl><DeScXmL>something</DeScXmL>";
		assertEquals(toTest,expected);
	}

	@Test
	public void testInformationParser_XMLTitleDesc_onlyTitle(){
		String toTest = info.xmlTitleAndDesc("blah");
		String expected = "<TiTlExMl>blah</TiTlExMl>";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testInformationParser_XMLTitleDesc_noInput(){
		String toTest = info.xmlTitleAndDesc("");
		String expected = "";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testInformationParser_setInfomation(){
		boolean toTest = info.setInformation("title");
		assertTrue(toTest);
	}
	
	@Test
	public void testInformationParser_setInfomation_noInput(){
		boolean toTest = info.setInformation(":");
		assertFalse(toTest);
	}
		
	@Test
	public void testInformationParser_GetTitle(){
		info.xmlTitleAndDesc("blah:something");
		String toTest = info.getTitle();
		String expected = "blah";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testInformationParser_GetDesc(){
		info.xmlTitleAndDesc("blah:something");
		String toTest = info.getDescription();
		String expected = "something";
		assertEquals(toTest,expected);
	}
}
