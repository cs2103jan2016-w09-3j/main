package dateParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class InformationParser {
	private String title;
	private String description;

	public InformationParser() {

	}

	public String xmlTitleAndDesc(String input) {
		getInformation(input);
		input = input.replace(title, "<title>" + title + "</title>");
		input = input.replace(description, "<desc>"+ description + "</desc>");
		return input;
	}

	private void getInformation(String input) {
		input = removeOtherAttributes(input);
		String[] inputs = input.split(":");
		setTitle(inputs[0].trim());
		setDescription(inputs[1].trim());
	}

	private String removeOtherAttributes(String input) {
		try {
			Document tempXMLDoc = XMLParser.loadXMLFromString("<XML>" + input + "</XML>");
			input = input.replace("<title>", "");
			input = input.replace("</title>", "");
			NodeList titles = tempXMLDoc.getElementsByTagName(XMLParser.TITLE_TAG);
			for (int i = 0; i < titles.getLength(); i++) {
				input = input.replace(titles.item(i).getTextContent(), "");
			}

			input = input.replace("<desc>", "");
			input = input.replace("</desc>", "");
			NodeList descs = tempXMLDoc.getElementsByTagName(XMLParser.DESC_TAG);
			for (int i = 0; i < descs.getLength(); i++) {
				input = input.replace(descs.item(i).getTextContent(), "");
			}

			input = input.replace("<dates>", "");
			input = input.replace("</dates>", "");
			NodeList dates = tempXMLDoc.getElementsByTagName(XMLParser.DATE_TAG);
			for (int i = 0; i < dates.getLength(); i++) {
				input = input.replace(dates.item(i).getTextContent(), "");
			}

			input = input.replace("<cmd>", "");
			input = input.replace("</cmd>", "");
			NodeList commands = tempXMLDoc.getElementsByTagName(XMLParser.CMD_TAG);
			for (int i = 0; i < commands.getLength(); i++) {
				input = input.replace(commands.item(i).getTextContent(), "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static void main(String args[]) {
		String input = "<cmd>add</cmd> <dates>[Wed Mar 02 22:32:30 SGT 2016]</dates> basketball with niggas:at suntec <dates>[Sun Mar 06 22:32:30 SGT 2016]</dates>";
		InformationParser tempInfoParser = new InformationParser();
		System.out.println(tempInfoParser.xmlTitleAndDesc(input));
	}
}
