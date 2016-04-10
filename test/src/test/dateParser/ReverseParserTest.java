//@@author a0125415n
package test.dateParser;

import dateParser.CommandParser;
import dateParser.ReverseParser;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class ReverseParserTest {
	ReverseParser rp = new ReverseParser();
	@Test
	public void testReverseParser_reParse_1stJan(){
		Calendar c = Calendar.getInstance();
		c.set(2016, 0, 1, 0, 0, 0);
		String toTest = rp.reParse(c);
		String expected ="1st January 2016";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testReverseParser_reParse_2ndFeb(){
		Calendar c = Calendar.getInstance();
		c.set(2016, 1, 2, 0, 0, 0);
		String toTest = rp.reParse(c);
		String expected ="2nd February 2016";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testReverseParser_reParse_3rdMar(){
		Calendar c = Calendar.getInstance();
		c.set(2016, 2, 3, 0, 0, 0);
		String toTest = rp.reParse(c);
		String expected ="3rd March 2016";
		assertEquals(toTest,expected);
	}
	
	@Test
	public void testReverseParser_reParse_4thApril(){
		Calendar c = Calendar.getInstance();
		c.set(2015, 3, 4, 0, 0, 0);
		String toTest = rp.reParse(c);
		String expected ="4th April 2015";
		assertEquals(toTest,expected);
	}
}
