//@@author a0125415n
package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.InputMapUIResource;

public class ReverseParser {
	private Map<Integer, String> dictionary = new HashMap<Integer, String>();
	private Map<Integer, String> dayWeekStr = new HashMap<Integer, String>();

	public ReverseParser() {
		dictionary.put(0,"Today");
		dictionary.put(-1,"Yesterday");
		dictionary.put(1,"Tomorrow");
		dayWeekStr.put(Calendar.SUNDAY, "Sunday");
		dayWeekStr.put(Calendar.MONDAY, "Monday");
		dayWeekStr.put(Calendar.TUESDAY, "Tuesday");
		dayWeekStr.put(Calendar.WEDNESDAY, "Wednesday");
		dayWeekStr.put(Calendar.THURSDAY, "Thursday");
		dayWeekStr.put(Calendar.FRIDAY, "Friday");
		dayWeekStr.put(Calendar.SATURDAY, "Saturday");
		
	}

	/**
	 * returns a string in the format "upcoming/previous" day_of_week DDth MMMMM YYYY
	 * @param input
	 * @return
	 */
	public String reParse(Calendar input) {
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
		long mills = input.getTimeInMillis() - curr.getTimeInMillis();
		int days = (int) Math.floor(mills / 86400000);
		output = dictionary.get(days);
		int dayOfWeek = input.get(Calendar.DAY_OF_WEEK);

		if ((days > 1) && (days < 7)) {
			output = "Upcoming " + dayWeekStr.get(dayOfWeek);
		}
		if ((days > -7) && (days < -1)) {
			output = "Previous " + dayWeekStr.get(dayOfWeek);
		}

		String date = getFormattedDate(input.getTime());
		if (output == null) {
			output = date;
		} else {
			output += " " + date;
		}
		return output;
	}
	
	/**
	 * Re-parsing dates for detailed view
	 * @param input
	 * @return
	 */
	public String detailedReParse(Calendar input) {
		String output = null;
		int dayOfWeek = input.get(Calendar.DAY_OF_WEEK);

		output = dayWeekStr.get(dayOfWeek);

		String date = getFormattedDate(input.getTime());
		if (output == null) {
			output = date;
		} else {
			output += " " + date;
		}
		return output;
	}

	/**
	 * adds the "th" for the date
	 * @param date
	 * @return
	 */
	private String getFormattedDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 2nd of march 2015
		int day = cal.get(Calendar.DATE);

		if (!((day > 10) && (day < 19)))
			switch (day % 10) {
			case 1:
				return new SimpleDateFormat("d'st' MMMM yyyy").format(date);
			case 2:
				return new SimpleDateFormat("d'nd' MMMM yyyy").format(date);
			case 3:
				return new SimpleDateFormat("d'rd' MMMM yyyy").format(date);
			default:
				return new SimpleDateFormat("d'th' MMMM yyyy").format(date);
			}
		return new SimpleDateFormat("d'th' MMMM yyyy").format(date);
	}

	public static void main(String args[]) {
		Calendar c = Calendar.getInstance();
		c.set(2016, 2, 23, 0, 0, 0);

		ReverseParser rp = new ReverseParser();
		System.out.println(rp.reParse(c));

	}

}
