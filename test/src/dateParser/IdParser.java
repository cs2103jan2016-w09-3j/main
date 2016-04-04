//@@author a0125415n
package dateParser;

import java.util.Scanner;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class IdParser {
	private String id;
	private String id2;
	private final String TWO_ID_REGEX = "[I][D][0-9a-zA-Z]+[-][I][D][0-9a-zA-Z]+";

	public IdParser() {
		id = null;
		id2 = null;
	}

	public String getID(String input) {
		if (hasIDKey(input)) {
			Scanner sc = new Scanner(input);
			while (sc.hasNext()) {
				String test = sc.next();
				if (hasIDKey(test)) {
					if (test.matches(TWO_ID_REGEX)) {
						setID1And2(test);
					} else {
						id = test.substring(2, test.length());
					}
				}
			}
		}
		return id;
	}

	private void setID1And2(String test) {
		String[] temp = test.split("-");
		id=  temp[0].substring(2, temp[0].length());
		id2=  temp[1].substring(2, temp[1].length());
	}

	public Pair<String,String> getLinkID(String input){
		getID(input);
		return new Pair<String,String>(id,id2);
	}
	public String xmlID(String input) {
		getID(input);
		if (id2 != null) {
			input = input.replace("ID" + id+"-ID"+id2, "<"+XMLParser.ID_TAG+">ID" + id +"-ID"+id2+ "</"+XMLParser.ID_TAG+">");
		} else if (id != null) {
			input = input.replace("ID" + id, "<"+XMLParser.ID_TAG+">ID" + id + "</"+XMLParser.ID_TAG+">");
		}

		return input;
	}

	private boolean hasIDKey(String input) {
		if (input.contains("ID")) {
			return true;
		} else {
			return false;
		}
	}
}
