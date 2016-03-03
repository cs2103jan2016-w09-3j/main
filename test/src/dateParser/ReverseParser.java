package dateParser;
import java.util.Calendar;
import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

public class ReverseParser {
	public ReverseParser(){
		
	}
	
	public static String reParse(Calendar input){
		PrettyTime p = new PrettyTime();
		String output = p.format(input);
		return output;
	}
	
}
