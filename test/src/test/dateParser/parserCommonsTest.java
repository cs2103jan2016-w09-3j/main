//@@author a0125415n
package test.dateParser;

import dateParser.ParserCommons;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class parserCommonsTest {
	@Test
	public void testParserCommons_getLastWord() {
		String toTest = ParserCommons.getLastWord("asdf ghjk jkl");
		String expected = "jkl";
		assertEquals(toTest,expected);
	}

	@Test
	public void testParserCommons_getLastWord_noInput() {
		String toTest = ParserCommons.getLastWord("");
		String expected = "";
		assertEquals(toTest,expected);
	}

	@Test
	public void testParserCommons_getDate() {
		Calendar toTest = ParserCommons.getDate("today");
		Calendar expected = Calendar.getInstance();
		expected.set(Calendar.HOUR_OF_DAY, 0);
		expected.set(Calendar.MINUTE, 0);
		expected.set(Calendar.SECOND, 0);
		expected.set(Calendar.MILLISECOND, 0);
		assertEquals(toTest,expected);
	}

	@Test
	public void testParserCommons_padTime() {
		String toTest = ParserCommons.padTime(0);
		String expected = "00";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testParserCommons_detailedDateTiem() {
		Calendar c = Calendar.getInstance();
		c.set(2016, 0, 1, 0,0, 0);
		String toTest = ParserCommons.detailedDateTime(c);
		String expected = "Friday 1st January 2016 0000hrs";
		assertEquals(toTest,expected);
	}

	@Test
	public void testParserCommons_getFirsttWord() {
		String toTest = ParserCommons.getFirstWord("asdf ghjk jkl");
		String expected = "asdf";
		assertEquals(toTest,expected);
	}

	@Test
	public void testParserCommons_getFirstWord_noInput() {
		String toTest = ParserCommons.getFirstWord("");
		String expected = "";
		assertEquals(toTest,expected);
	}
}
