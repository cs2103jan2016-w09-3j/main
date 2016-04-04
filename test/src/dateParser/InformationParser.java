//@@author a0125415n
package dateParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class InformationParser {
	private String title;
	private String description;

	public InformationParser() {
		
	}
	/**
	 * Takes a String input and returns input in XML
	 * @param input, a String with with Date and Command already parsed 
	 * @return String in xml form
	 */
	public String xmlTitleAndDesc(String input) {
		String working = XMLParser.removeAllAttributes(input);
		boolean success = setInformation(working);
		if (success){
			if ((title != null)&&(title.length()!=1)){
				input = input.replace(title, "<"+XMLParser.TITLE_TAG+">" + title + "</"+XMLParser.TITLE_TAG+">");
			}
			if ((description != null)&&(description.length()!=1)) {
				input = input.replace(description, "<"+XMLParser.DESC_TAG+">" + description + "</"+XMLParser.DESC_TAG+">");
			}
		}
		input = input.replace(":", "<"+XMLParser.OTHERS_TAG+">:</"+XMLParser.OTHERS_TAG+">");
		return input;
	}

	/**
	 * Automatically takes input(non-XML) and returns the success status
	 * @param input (non-XML)
	 * @return success
	 */
	public boolean setInformation(String input) {
		input = XMLParser.removeAllAttributes(input);
		boolean success = false;
		//System.out.println(input);
		if (!input.trim().equalsIgnoreCase(":"))
		{
			String[] inputs = input.split(":");
			if (inputs.length == 2) {
				if(!inputs[0].trim().isEmpty())
				{
					if(!inputs[0].contains("ID")){
						setTitle(inputs[0].trim());
					}
				}
				//System.out.println(inputs[1]);
				if(!inputs[1].trim().isEmpty())
				{
					if(!inputs[1].contains("ID")){
						setDescription(inputs[1].trim());
					}
				}
			} else {
				if (!inputs[0].trim().isEmpty()){
					for(int i=0; i<inputs.length; i++){
						if(!inputs[i].contains("ID")){
							setTitle(inputs[i].trim());
						}
					}
				}
			}
			success = true;
		}
		return success;
	}

	/**
	 * getter for title
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * setter for title
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * getter for description
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setter for description
	 * @param description
	 */
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
