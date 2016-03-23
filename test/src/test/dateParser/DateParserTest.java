package test.dateParser;
import dateParser.DateParser;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class DateParserTest {
	DateParser dp = new DateParser();
	List<Date> dates = dp.parseToList("01/02/2016 1500hrs");
	Date testDate = dates.get(0);
	@Test
	public void testParserToList() {
		Calendar c = Calendar.getInstance();
		c.set(2016, 01, 01, 15, 00, 00);
		c.clear(Calendar.MILLISECOND);
		assertEquals(0, c.getTime().compareTo(testDate));
		
	}
	@Test
	public void testXMLDate(){
		assertEquals("<dates>tmr</dates> blah blah ",dp.xmlDate("tmr blah blah"));
		assertEquals("",dp.xmlDate(""));
	}
	
	

}
