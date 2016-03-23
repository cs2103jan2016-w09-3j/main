package dateParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser {
	public static final String DATE_TAG = "dates";
	public static final String CMD_TAG = "cmd";
	public static final String TITLE_TAG = "title";
	public static final String DESC_TAG = "desc";
	public static final String ID_TAG = "ID";
	
	public static Document loadXMLFromString(String xml) throws Exception{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}
	
	public static Map<String, ArrayList<String>> loadMapFromXML (String xml) throws Exception{
		xml = "<xml>"+xml +"</xml>";
		System.out.println(xml);
		Map<String,ArrayList<String>> returnVal = new HashMap<String,ArrayList<String>>();
		Document xmlDoc = loadXMLFromString(xml);
		NodeList children = xmlDoc.getChildNodes().item(0).getChildNodes();
		for(int i=0; i< children.getLength(); i++ ){
			Node child = children.item(i);
			String nodeName = child.getNodeName();
			if(!nodeName.equals("#text")){
				ArrayList<String> value;
				if(returnVal.containsKey(nodeName)){
					value = returnVal.get(nodeName);
					value.add(child.getTextContent());
				}else{
					value = new ArrayList<String>();
					value.add(child.getTextContent());
					returnVal.put(nodeName, value);
				}
			//System.out.println(nodeName);
			}
		}
		return returnVal;
	}
	
	public static String removeAllAttributes(String input) {
		try {
			Document tempXMLDoc = XMLParser.loadXMLFromString("<XML>" + input + "</XML>");
			input = removeAttribute(input, tempXMLDoc,TITLE_TAG);
			input = removeAttribute(input, tempXMLDoc,CMD_TAG);
			input = removeAttribute(input, tempXMLDoc,DESC_TAG);
			input = removeAttribute(input, tempXMLDoc,DATE_TAG);
			input = removeAttribute(input, tempXMLDoc,ID_TAG);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}

	private static String removeAttribute(String input, Document tempXMLDoc, String tag) {
		input = input.replace("<"+tag+">", "");
		input = input.replace("</"+tag+">", "");
		NodeList titles = tempXMLDoc.getElementsByTagName(tag);
		for (int i = 0; i < titles.getLength(); i++) {
			input = input.replace(titles.item(i).getTextContent(), "");
		}
		return input;
	}
	
	public static String removeAllTags(String input) {
		input = removeTags(input, CMD_TAG);
		input = removeTags(input, TITLE_TAG);
		input = removeTags(input, DESC_TAG);
		input = removeTags(input, DATE_TAG);
		input = removeTags(input, ID_TAG);
		return input;
	}
	
	private static String removeTags(String input, String tag) {
		input = input.replace("<"+tag+">", "");
		input = input.replace("</"+tag+">", "");
		return input;
	}
	
	public static void main(String args[]){
		String temp = "<cmd>add</cmd> <dates>nigra</dates> <title>basketball with friends</title> <dates>[Fri Mar 04 22:25:13 SGT 2016]</dates> ";
		try {
			//Document tempDoc = loadXMLFromString(temp);
			//System.out.println(tempDoc.getElementsByTagName("a").item(0).getTextContent());
			Map<String,ArrayList<String>> m = loadMapFromXML(temp);
			System.out.println(m.get("dates"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
