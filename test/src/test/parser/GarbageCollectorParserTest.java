//@@author: a0125415n
package test.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import parser.GarbageCollectorParser;

public class GarbageCollectorParserTest {
	GarbageCollectorParser gc = new GarbageCollectorParser();

	@Test
	public void TestGarbageCollector_xmlHash() {
		String toTest = gc.xmlHash("asdfgh #qwer");
		String expected = "asdfgh <HaShXmL>#qwer</HaShXmL>";
		assertEquals(toTest, expected);
	}

	@Test
	public void TestGarbageCollector_xmlHash_noInput() {
		String toTest = gc.xmlHash("asdfgh");
		String expected = "asdfgh";
		assertEquals(toTest, expected);
	}

	@Test
	public void TestGarbageCollector_getHash_noInput() {
		gc.xmlHash("#one #two #three four");
		ArrayList<String> toTest = gc.getHashes();
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("#one");
		expected.add("#two");
		expected.add("#three");
		assertEquals(toTest, expected);
	}

	@Test
	public void TestGarbageCollector_xmlAllOthers() {
		String toTest = gc.xmlAllOthers("asdf");
		String expected = "<OtHeRxMl>asdf</OtHeRxMl>";
		assertEquals(toTest, expected);
	}
	
	@Test
	public void TestGarbageCollector_xmlAllOthers_noInput() {
		String toTest = gc.xmlAllOthers("");
		String expected = "";
		assertEquals(toTest, expected);
	}
}
