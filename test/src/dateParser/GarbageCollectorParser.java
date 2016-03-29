package dateParser;

import java.util.ArrayList;
import java.util.Scanner;

public class GarbageCollectorParser {
	private ArrayList<String> hashes= new ArrayList<String>();
	public GarbageCollectorParser(){
	}
	
	public String xmlHash(String input){
		String toReplace = XMLParser.removeAllAttributes(input);
		hashes = new ArrayList<String>();
		//System.out.println("GC"+toReplace);
		Scanner sc = new Scanner(toReplace);
		while(sc.hasNext()){
			String temp = sc.next();
			//System.out.println("GC2"+input);
			if((temp.length()!=1)&&(!temp.contains("ID"))){
				if(temp.charAt(0)=='#'){
					hashes.add(temp);
					input = input.replace(temp, "<"+XMLParser.HASH_TAG+">"+temp+"</"+XMLParser.HASH_TAG+">");
				}	
			}
			//System.out.println("test2");
		}
		
		return input;
	}
	
	public ArrayList<String> getHashes(){
		return hashes;
	}
	
	public String xmlAllOthers(String input){
		//System.out.println(input);
		String toReplace = XMLParser.removeAllAttributes(input);
		//System.out.println("GC"+toReplace);
		Scanner sc = new Scanner(toReplace);
		while(sc.hasNext()){
			String temp = sc.next();
			//System.out.println("GC2"+input);
			if((temp.length()!=1)&&(!temp.contains("ID"))){
					input = input.replace(temp, "<"+XMLParser.OTHERS_TAG+">"+temp+"</"+XMLParser.OTHERS_TAG+">");
					
			}
			//System.out.println("test2");
		}
		String lastWord = ParserCommons.getLastWord(input);
		String firstWord = ParserCommons.getFirstWord(XMLParser.removeAllTags(input));
		//System.out.println(firstWord+" , "+lastWord);
		if((lastWord.length()==1)&&(!firstWord.equals(lastWord))&&(!lastWord.contains("ID"))){
			//System.out.println("test");
			input = input.substring(0, input.length()-2)+"<"+XMLParser.OTHERS_TAG+">"+lastWord+"</"+XMLParser.OTHERS_TAG+">";
		}
		return input;
	}
}
