package dateParser;

import java.util.Scanner;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class IdParser {
	private String id;
	
	public IdParser(){
		id = null;
	}
	
	public String getID(String input){
		if(hasIDKey(input)){
			Scanner sc = new Scanner(input);
			while (sc.hasNext()){
				String test = sc.next();
				if(hasIDKey(test)){
					id = test.substring(2,test.length());
				}
			}
		}
		return id;
	}
	
	public String xmlID(String input){
		getID(input);
		input = input.replace("ID"+id, "<ID>ID"+id+"</ID>");
		return input;
	}
	
	private boolean hasIDKey(String input){
		if(input.contains("ID")){
			return true;
		}else{
			return false;
		}
	}
}
