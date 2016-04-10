//@@author a0125415n
package test.dateParser;
import dateParser.DateParser;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class DateParserTest {
	DateParser dp = new DateParser();
	
	@Test
	public void testDateParser_testParseToList_AbsoluteDate() {
		List<Date> dates = dp.parseToList("01/02/2016 1500hrs");
		Date testDate = dates.get(0);
		//test data
		Calendar c = Calendar.getInstance();
		c.set(2016, 01, 01, 15, 00, 00);
		c.clear(Calendar.MILLISECOND);
		
		assertEquals(0, c.getTime().compareTo(testDate));	
	}
	
	@Test
	public void testDateParser_testParseToList_relativeDate() {
		List<Date> dates = dp.parseToList("today");
		Date testDate = dates.get(0);
		//test data
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		
		assertEquals(0, c.getTime().compareTo(testDate));	
	}
	
	@Test
	public void testDateParser_testParseToList_noDate() {
		List<Date> dates = dp.parseToList("");
		assertTrue(dates.isEmpty());	
	}
	
	@Test
	public void testDateParser_XMLDate(){
		assertEquals("<DaTeSxMl>tmr </DaTeSxMl> blah blah",dp.xmlDate("tmr blah blah"));
	}
	
	@Test
	public void testDateParser_XMLDate_NoInput(){
		assertEquals("",dp.xmlDate(""));
	}
}
