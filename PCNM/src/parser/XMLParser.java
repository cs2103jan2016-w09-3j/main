// @@author A0125415N
package parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser {
    public static final String DATE_TAG = "DaTeSxMl";
    public static final String CMD_TAG = "CmDxMl";
    public static final String TITLE_TAG = "TiTlExMl";
    public static final String DESC_TAG = "DeScXmL";
    public static final String OTHERS_TAG = "OtHeRxMl";
    public static final String ID_TAG = "IdXmL";
    public static final String HASH_TAG = "HaShXmL";

    /**
     * creates a document from a string of xml
     * 
     * @param xml
     * @return
     * @throws Exception
     */
    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    /**
     * creates a map from a string of xml
     * 
     * @param xml
     * @return map with the xml tag as the key and the date in an array list
     * @throws Exception
     */
    public static Map<String, ArrayList<String>> loadMapFromXML(String xml) throws Exception {
        xml = "<xml>" + xml + "</xml>";
        // System.out.println(xml);
        Map<String, ArrayList<String>> returnVal = new HashMap<String, ArrayList<String>>();
        Document xmlDoc = loadXMLFromString(xml);
        NodeList children = xmlDoc.getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String nodeName = child.getNodeName();
            if (!nodeName.equals("#text")) {
                ArrayList<String> value;
                if (returnVal.containsKey(nodeName)) {
                    value = returnVal.get(nodeName);
                    value.add(child.getTextContent());
                } else {
                    value = new ArrayList<String>();
                    value.add(child.getTextContent());
                    returnVal.put(nodeName, value);
                }
                // System.out.println(nodeName);
            }
        }
        return returnVal;
    }

    /**
     * removes all text in a string between xml tags
     * 
     * @param input
     * @return string without xml
     */
    public static String removeAllAttributes(String input) {
        String temp = new String(input);
        try {
            Document tempXMLDoc = XMLParser.loadXMLFromString("<XML>" + input + "</XML>");
            input = removeAttribute(input, tempXMLDoc, TITLE_TAG);
            input = removeAttribute(input, tempXMLDoc, CMD_TAG);
            input = removeAttribute(input, tempXMLDoc, DESC_TAG);
            input = removeAttribute(input, tempXMLDoc, DATE_TAG);
            input = removeAttribute(input, tempXMLDoc, ID_TAG);
            input = removeAttribute(input, tempXMLDoc, OTHERS_TAG);
            input = removeAttribute(input, tempXMLDoc, HASH_TAG);
        } catch (Exception e) {
            e.printStackTrace();
            return temp;
        }
        return input;
    }

    /**
     * removes a specific xml tag and its contents from a string
     * 
     * @param input
     * @param tag
     * @param value
     * @return String
     */
    private static String removeOneAttribute(String input, String tag, String value) {
        input = input.replace("<" + tag + ">" + value + "</" + tag + ">", "");
        return input;
    }

    /**
     * removes a specific xml tag and its contents from a document
     * 
     * @param input
     * @param tempXMLDoc
     * @param tag
     * @return String
     */
    private static String removeAttribute(String input, Document tempXMLDoc, String tag) {
        NodeList titles = tempXMLDoc.getElementsByTagName(tag);
        for (int i = 0; i < titles.getLength(); i++) {
            input = input.replace("<" + tag + ">" + titles.item(i).getTextContent() + "</" + tag + ">", "");
        }
        return input;
    }

    /**
     * remove all tags without removing the contents
     * 
     * @param input
     * @return String
     */
    public static String removeAllTags(String input) {
        input = removeTags(input, CMD_TAG);
        input = removeTags(input, TITLE_TAG);
        input = removeTags(input, DESC_TAG);
        input = removeTags(input, DATE_TAG);
        input = removeTags(input, ID_TAG);
        input = removeTags(input, OTHERS_TAG);
        input = removeTags(input, HASH_TAG);
        return input;
    }

    /**
     * removes specific tags from string
     * 
     * @param input
     * @param tag
     * @return
     */
    private static String removeTags(String input, String tag) {
        input = input.replace("<" + tag + ">", "");
        input = input.replace("</" + tag + ">", "");
        return input;
    }

    /**
     * creates a list of tags and their contents, retaining order
     * 
     * @param input
     * @return ArrayList of Pairs, containing the tag followed by the contents
     */
    public static ArrayList<Pair<String, ArrayList<String>>> xmlToArrayList(String input) {
        ArrayList<Pair<String, ArrayList<String>>> tagStringPair = new ArrayList<Pair<String, ArrayList<String>>>();
        String prev = "";
        while (!input.trim().isEmpty()) {
            String tag = findNextTag(input);
            String nextIn = findNextInput(tag, input);
            if (tag != "") {
                ArrayList<String> values = new ArrayList<String>();
                values.add(nextIn);
                tagStringPair.add(new Pair(tag, values));
                input = removeOneAttribute(input, tag, nextIn);
                // System.out.println("test@"+tag+values.get(0));

            }

            if (prev == input) {
                break;
            }
            if (tag.trim().equals("")) {
                break;
            }
            prev = input;
        }

        return tagStringPair;
    }

    /**
     * Get the next tag in the string
     * 
     * @param input
     * @return String
     */
    private static String findNextTag(String input) {
        String tag = "";
        boolean isInTag = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!isInTag) {
                if (c == '<') {
                    isInTag = true;
                }
            } else {
                if (c == '>') {
                    isInTag = false;
                    break;
                }
                tag += c;
            }
        }
        return tag;
    }

    /**
     * Finds the next part of the input given tag
     * 
     * @param tag
     * @param input
     * @returnString with input
     */
    private static String findNextInput(String tag, String input) {
        input = input.trim();
        // System.out.println(tag+","+ input);
        boolean isInTag = false;
        boolean isClosingTag = false;
        String temp = "";
        String closingTag = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isClosingTag) {
                closingTag += c;
            } else {
                temp += c;
            }
            if (isInTag) {
                if (c == '<') {
                    isClosingTag = true;
                    closingTag += c;
                } else if (closingTag.equals("</" + tag + ">")) {
                    isInTag = false;
                    isClosingTag = false;
                    temp = temp.substring(0, temp.length() - 1);
                    break;
                }
            } else {
                if (temp.equals("<" + tag + ">")) {
                    isInTag = true;
                    temp = "";
                }
            }
        }
        return temp;
    }
}
