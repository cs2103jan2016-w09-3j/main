package dateParser;

public class CommandParser {
	private static final int FIRST_WORD = 0;

	public enum COMMAND {
		ADD, EDIT, DELETE, EXIT, INVALID;
	};

	public CommandParser() {

	}

	private String getFirstWord(String input) {
		String returnStrVal = input.trim().split("\\s+")[FIRST_WORD];
		return returnStrVal;
	}

	public COMMAND getCommand(String input) {
		String inputCmd = getFirstWord(input);
		COMMAND returnVal = COMMAND.INVALID;
		if (inputCmd.equalsIgnoreCase("add")) {
			returnVal = COMMAND.ADD;
		}else if (inputCmd.equalsIgnoreCase("edit")) {
			returnVal = COMMAND.EDIT;
		}else if (inputCmd.equalsIgnoreCase("delete")) {
			returnVal = COMMAND.DELETE;
		}else if (inputCmd.equalsIgnoreCase("exit")) {
			returnVal = COMMAND.EXIT;
		}
		return returnVal;
	}

	public String xmlFirstWord(String input) {
		String inputFirstWord = getFirstWord(input);
		String returnStrVal = input;
		returnStrVal = input.replace(inputFirstWord, "<cmd>" + inputFirstWord + "</cmd>");
		return returnStrVal;
	}
}
