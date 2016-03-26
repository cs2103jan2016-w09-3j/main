package dateParser;

import java.util.Scanner;

public class GarbageCollectorParser {
	public GarbageCollectorParser(){
		
	}
	
	public String xmlAllOthers(String input){
		//System.out.println(input);
		String toReplace = XMLParser.removeAllAttributes(input);
		//System.out.println("GC"+toReplace);
		Scanner sc = new Scanner(toReplace);
		while(sc.hasNext()){
			String temp = sc.next();
			//System.out.println("GC2"+input);
			if(temp.length()!=1){
				input = input.replace(temp, "<others>"+temp+"</others>");
			}
			//System.out.println("test2");
		}
		String lastWord = ParserCommons.getLastWord(input);
		if(lastWord.length()==1){
			//System.out.println("test");
			input = input.substring(0, input.length()-2)+"<others>"+lastWord+"</others>";
		}
		return input;
	}
}
