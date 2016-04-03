package dateParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.InputMapUIResource;

public class ReverseParser {
	private Map<Long,String> dictionary = new HashMap<Long,String> ();
	private Map<Integer,String> dayWeekStr =new HashMap<Integer,String>();
	public ReverseParser(){
		try {
			FileReader fr = new FileReader("../test/src/reverseParserDict.txt");
			BufferedReader br = new BufferedReader(fr);
			String line = new String();
			while(br.ready()){
				line = br.readLine();
				Scanner sc = new Scanner(line);
				sc.useDelimiter(",");
				Long index = Long.parseLong(sc.next());
				String value = sc.next();
				//System.out.println(index+" "+value);
				dictionary.put(index, value);
			}
			dayWeekStr.put(Calendar.SUNDAY, "Sunday");
			dayWeekStr.put(Calendar.MONDAY, "Monday");
			dayWeekStr.put(Calendar.TUESDAY, "Tuesday");
			dayWeekStr.put(Calendar.WEDNESDAY, "Wednesday");
			dayWeekStr.put(Calendar.THURSDAY, "Thursday");
			dayWeekStr.put(Calendar.FRIDAY, "Friday");
			dayWeekStr.put(Calendar.SATURDAY, "Saturday");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String reParse(Calendar input){
		String output = null;
		Calendar curr = Calendar.getInstance();
		curr.set(Calendar.HOUR_OF_DAY, 0);
		curr.clear(Calendar.MINUTE);
		curr.clear(Calendar.SECOND);
		curr.clear(Calendar.MILLISECOND);
		input.set(Calendar.HOUR_OF_DAY, 0);
		input.clear(Calendar.MINUTE);
		input.clear(Calendar.SECOND);
		input.clear(Calendar.MILLISECOND);
		long mills = input.getTimeInMillis()-curr.getTimeInMillis();
		long days =(long) Math.floor(mills/86400000);
		output = dictionary.get(days);
		int dayOfWeek = input.get(Calendar.DAY_OF_WEEK);
		if ((days>1)&&(days<7)){
			output = "Upcoming " + dayWeekStr.get(dayOfWeek);
		}
		if ((days>-7)&&(days<-1)){
			output = "Previous " + dayWeekStr.get(dayOfWeek);
		}
		return output;
	}
	
	public static void main(String args[]){
		Calendar c = Calendar.getInstance();
		c.set(2016, 2, 23,0,0,0);

		ReverseParser rp = new ReverseParser();
		System.out.println(rp.reParse(c));
		
	}
	
}
