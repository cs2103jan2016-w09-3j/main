//@@author A0125415N
package parser;

import java.util.ArrayList;
import java.util.Scanner;

public class GarbageCollectorParser {
	private static final String ID_CONST = "ID";
	private static final char HEXCHAR = '#';
	private ArrayList<String> hashes= new ArrayList<String>();
	public GarbageCollectorParser(){
	}
	
	/**
	 * adds xml to anything that has hashtags
	 * @param String input
	 * @return input with xml around any word with has in front
	 */
	public String xmlHash(String input){
		String toReplace = XMLParser.removeAllAttributes(input);
		hashes = new ArrayList<String>();
		Scanner sc = new Scanner(toReplace);
		while(sc.hasNext()){
			String temp = sc.next();
			if((temp.length()!=1)&&(!temp.contains(ID_CONST))){
				if(temp.charAt(0)==HEXCHAR){
					hashes.add(temp);
					input = input.replace(temp, "<"+XMLParser.HASH_TAG+">"+temp+"</"+XMLParser.HASH_TAG+">");
				}	
			}
		}
		return input;
	}
	
	/**
	 * get the hashes in the string, precondition: run xmlHash first
	 * @return ArrayList of hashes in string
	 */
	public ArrayList<String> getHashes(){
		return hashes;
	}
	
	/**
	 * add xml to anything that hasn't already got xml.
	 * @param String input
	 * @return input with xml on any other not yet defined fields
	 */
	public String xmlAllOthers(String input){
		String toReplace = XMLParser.removeAllAttributes(input);
		Scanner sc = new Scanner(toReplace);
		while(sc.hasNext()){
			String temp = sc.next();
			if((temp.length()!=1)&&(!temp.contains(ID_CONST))){
					input = input.replace(temp, "<"+XMLParser.OTHERS_TAG+">"+temp+"</"+XMLParser.OTHERS_TAG+">");
					
			}
		}
		String lastWord = ParserCommons.getLastWord(input);
		String firstWord = ParserCommons.getFirstWord(XMLParser.removeAllTags(input));
		if((lastWord.length()==1)&&(!firstWord.equals(lastWord))&&(!lastWord.contains(ID_CONST))){
			input = input.substring(0, input.length()-2)+"<"+XMLParser.OTHERS_TAG+">"+lastWord+"</"+XMLParser.OTHERS_TAG+">";
		}
		return input;
	}
}
