package dateParser;

public class ParserCommons {
	public static String getLastWord(String input){
			String returnVal = null;
			String [] seperated = input.split(" ");
			if (seperated.length>0){
				returnVal = seperated[seperated.length-1];
			}
			return returnVal;
		}

}
