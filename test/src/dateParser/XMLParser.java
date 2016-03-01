package dateParser;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLParser {
	public static final String DATE_TAG = "dates";
	public static final String CMD_TAG = "cmd";
	public static final String TITLE_TAG = "title";
	public static final String DESC_TAG = "desc";
	public static Document loadXMLFromString(String xml) throws Exception
	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}
	
	public static void main(String args[]){
		String temp = "<a> some stuff </a>";
		try {
			Document tempDoc = loadXMLFromString(temp);
			System.out.println(tempDoc.getElementsByTagName("a").item(0).getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
