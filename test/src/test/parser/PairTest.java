//@@author a0125415n
package test.parser;

import static org.junit.Assert.*;
import org.junit.Test;

import parser.Pair;

public class PairTest {
	
	@Test
	public void testPair_GetFirst() {
		Pair<String, String> p = new Pair<String, String>("a", "b");
		assertEquals(p.getFirst(), "a");
	}

	@Test
	public void testPair_GetSecond() {
		Pair<String, String> p = new Pair<String, String>("a", "b");
		assertEquals(p.getSecond(), "b");
	}
}
