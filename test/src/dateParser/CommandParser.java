package dateParser;

public class CommandParser {

	public enum COMMAND {
		ADD, EDIT, DELETE, EXIT, INVALID;
	};

	public CommandParser() {

	}

	/**
	 * takes in a string and retuns the related command
	 * @param input
	 * @return COMMAND enum
	 */
	public COMMAND getCommand(String input) {
		String inputCmd = ParserCommons.getFirstWord(input);
		COMMAND returnVal = COMMAND.INVALID;
		if (inputCmd.equalsIgnoreCase("add")) {
			returnVal = COMMAND.ADD;
		} else if (inputCmd.equalsIgnoreCase("edit")) {
			returnVal = COMMAND.EDIT;
		} else if (inputCmd.equalsIgnoreCase("delete")) {
			returnVal = COMMAND.DELETE;
		} else if (inputCmd.equalsIgnoreCase("exit")) {
			returnVal = COMMAND.EXIT;
		}
		return returnVal;
	}

	/**
	 * takes the first word and adds XML to it
	 * @param input
	 * @return xml of input
	 */
	public String xmlFirstWord(String input) {
		String inputFirstWord = ParserCommons.getFirstWord(input);
		String returnStrVal = input;
		returnStrVal = input.replace(inputFirstWord, "<cmd>" + inputFirstWord + "</cmd>");
		return returnStrVal;
	}
}
