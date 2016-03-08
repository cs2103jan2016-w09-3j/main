package dateParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class InformationParser {
	private String title;
	private String description;

	public InformationParser() {

	}

	public String xmlTitleAndDesc(String input) {
		boolean success = setInformation(input);
		if (success){
			if (title != null){
				input = input.replace(title, "<title>" + title + "</title>");
			}
			if (description != null) {
				input = input.replace(description, "<desc>" + description + "</desc>");
			}
		}
		return input;
	}

	public boolean setInformation(String input) {
		input = XMLParser.removeAllAttributes(input);
		boolean success = false;
		System.out.println(input);
		if (!input.trim().equalsIgnoreCase(":"))
		{
			String[] inputs = input.split(":");
			if (inputs.length == 2) {
				setTitle(inputs[0].trim());
				System.out.println(inputs[1]);
				if(!inputs[1].trim().isEmpty())
				{
					setDescription(inputs[1].trim());
				}
			} else {
				if (!inputs[0].trim().isEmpty()){
					setTitle(inputs[0].trim());
				}
			}
			success = true;
		}
		return success;
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
		//String input = "<cmd>add</cmd> <dates>[Wed Mar 02 22:32:30 SGT 2016]</dates> basketball with niggas:at suntec <dates>[Sun Mar 06 22:32:30 SGT 2016]</dates>";
		String input = "<cmd>add</cmd>";
		InformationParser tempInfoParser = new InformationParser();
		System.out.println(tempInfoParser.xmlTitleAndDesc(input));
	}
}
