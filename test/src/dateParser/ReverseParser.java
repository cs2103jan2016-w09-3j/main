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
	private static Map<Long,String> dictionary = new HashMap<Long,String> ();
	
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
				System.out.println(index+" "+value);
				dictionary.put(index, value);
			}
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
		return output;
	}
	
	public static void main(String args[]){
		Calendar c = Calendar.getInstance();
		c.set(2016, 2, 23,0,0,0);

		ReverseParser rp = new ReverseParser();
		System.out.println(rp.reParse(c));
		
	}
	
}
