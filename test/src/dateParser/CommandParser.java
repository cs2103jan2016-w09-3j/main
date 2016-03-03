package dateParser;

public class CommandParser {
	private static final int FIRST_WORD = 0;
	
	public enum COMMAND {
		ADD
	};
	
	public CommandParser() {

	}

	private String getFirstWord(String input) {
		String returnStrVal = input.trim().split("\\s+")[FIRST_WORD];
		return returnStrVal;
	}

	public String xmlFirstWord(String input) {
		String inputFirstWord = getFirstWord(input);
		String returnStrVal = input.replace(inputFirstWord, "<cmd>" + inputFirstWord + "</cmd>");
		return returnStrVal;
	}
}
