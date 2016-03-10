package dateParser;

public class ParserCommons {
	private static final int FIRST_WORD = 0;
	
	public static String getLastWord(String input) {
		String returnVal = null;
		String[] seperated = input.split(" ");
		if (seperated.length > 0) {
			returnVal = seperated[seperated.length - 1];
		}
		return returnVal;
	}

	public static String getFirstWord(String input) {
		String returnStrVal = input.trim().split("\\s+")[FIRST_WORD];
		return returnStrVal;
	}
}
