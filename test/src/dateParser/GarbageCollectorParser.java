package dateParser;

import java.util.Scanner;

public class GarbageCollectorParser {
	public GarbageCollectorParser(){
		
	}
	
	public String xmlAllOthers(String input){
		String toReplace = XMLParser.removeAllAttributes(input);
		Scanner sc = new Scanner( toReplace);
		while(sc.hasNext()){
			String temp = sc.next();
			input.replace(temp, "<others>"+temp+"</others>");
		}
		return input;
	}
}
