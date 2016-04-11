// @@author A0125415N
package test.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import parser.IdParser;
import parser.Pair;

public class IdParserTest {
    IdParser id = new IdParser();

    @Test
    public void testIDParser_GetID() {
        String toTest = id.getId("ID123");
        String expected = "123";
        assertEquals(toTest, expected);
    }

    @Test
    public void testIDParser_GetID_noInput() {
        String toTest = id.getId("asfd");
        String expected = null;
        assertEquals(toTest, expected);
    }

    @Test
    public void testIDParser_GetLinkID() {
        Pair<String, String> toTest = id.getLinkId("ID123-ID234");
        Pair<String, String> expected = new Pair("123", "234");
        assertEquals(toTest.getFirst(), expected.getFirst());
        assertEquals(toTest.getSecond(), expected.getSecond());
    }

    @Test
    public void testIDParser_GetLinkID_invalidInput() {
        Pair<String, String> toTest = id.getLinkId("123-234");
        Pair<String, String> expected = new Pair(null, null);
        assertEquals(toTest.getFirst(), expected.getFirst());
        assertEquals(toTest.getSecond(), expected.getSecond());
    }

    @Test
    public void testIDParser_xmlID() {
        String toTest = id.xmlID("ID1234");
        String expected = "<IdXmL>ID1234</IdXmL>";
        assertEquals(toTest, expected);
    }

    @Test
    public void testIDParser_xmlID_invalidInput() {
        String toTest = id.xmlID("1234");
        String expected = "1234";
        assertEquals(toTest, expected);
    }
}
