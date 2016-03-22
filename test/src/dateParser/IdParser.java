package dateParser;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class IdParser {
	private String id;
	
	public IdParser(){
		id = null;
	}
	
	public String getID(String input){
		if(hasIDKey(input)){
			String[] words = input.split(" ");
			for(int i=0;i<words.length; i++){
				String toCheck = words[i].substring(0,2);
				if(toCheck.equals("ID")){
					System.out.println("TEST");
					id = words[i].substring(2,words[i].length());
					System.out.println(id);
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
