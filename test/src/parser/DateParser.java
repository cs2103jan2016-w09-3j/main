//@@author a0125415n
package parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import com.joestelmach.natty.*;

public class DateParser {

	private static final char DOUBLEQUOTE = '\"';
	private static final char SINGLEQUOTE = '\'';
	private static final int CONVERSION_FROM_GREGORIAN_CAL = 1;
	private static final char CHAR_ZERO = '0';
	private static final String SPACE_DELIM = " ";
	private static final char FWD_SLASH = '/';
	private static final char DASH = '-';
	private static final char DOT = '.';

	private final String DATE_REGEX_2DAY_2MONTH = "\\d{2}[\\/-]\\d{2}(\\/|-|)(?:\\d{4}|\\d{2}|\\d{0})";
	private final String DATE_REGEX_1DAY_2MONTH = "\\d{1}[\\/-]\\d{2}(\\/|-|)(?:\\d{4}|\\d{2}|\\d{0})";
	private final String DATE_REGEX_2DAY_1MONTH = "\\d{2}[\\/-]\\d{1}(\\/|-|)(?:\\d{4}|\\d{2}|\\d{0})";
	private final String DATE_REGEX_1DAY_1MONTH = "\\d{1}[\\/-]\\d{1}(\\/|-|)(?:\\d{4}|\\d{2}|\\d{0})";

	private final int START_DAY_INDEX = 0;
	private final int END_DAY_INDEX = 2;
	private final int START_MONTH_INDEX = 3;
	private final int END_MONTH_INDEX = 5;
	private final int START_YEAR_INDEX = 6;

	private PrintStream printStreamOriginal = System.err;
	private Parser nattyParser = new Parser();

	public DateParser() {
		// init the parser so that there's no lag later
		hideErr();
		nattyParser.parse("today");
		showErr();
	}

	/**
	 * @param input
	 * @return List of all possible dates
	 */
	public List<Date> parseToList(String inputDate) {
		ArrayList<Integer> locationQuote = new ArrayList<Integer>();
		for (int i = 0; i < inputDate.length(); i++) {
			char temp = inputDate.charAt(i);
			if (temp == DOUBLEQUOTE) {
				locationQuote.add(i);
			}
		}
		for (int i = 0; i < locationQuote.size(); i = i + 2) {
			if (i + 1 < locationQuote.size()) {
				inputDate = inputDate.substring(0, locationQuote.get(i))
						+ inputDate.substring(locationQuote.get(i + 1));
			}
		}
		inputDate = convertFormalDates(inputDate);
		List<Date> returnDateList = new ArrayList<Date>();
		hideErr();
		List<DateGroup> dateGroups = nattyParser.parse(inputDate);
		showErr();
		for (int i = 0; i < dateGroups.size(); i++) {
			List<Date> dates = dateGroups.get(i).getDates();
			for (int j = 0; j < dates.size(); j++) {
				if(dates.size() == 1){
					Calendar toCheck = Calendar.getInstance();
					toCheck.setTime(dates.get(j));
					toCheck.clear(Calendar.SECOND);
					toCheck.clear(Calendar.MILLISECOND);
					Calendar curr = Calendar.getInstance();
					curr.set(toCheck.get(Calendar.YEAR), toCheck.get(Calendar.MONTH), toCheck.get(Calendar.DATE));
					curr.clear(Calendar.SECOND);
					curr.clear(Calendar.MILLISECOND);
					if (curr.equals(toCheck)) {
						toCheck.set(Calendar.HOUR_OF_DAY, 0);
						toCheck.set(Calendar.MINUTE, 0);
						returnDateList.add(toCheck.getTime());
						toCheck.set(Calendar.HOUR_OF_DAY, 23);
						toCheck.set(Calendar.MINUTE, 59);
						returnDateList.add(toCheck.getTime());
					} else {
						returnDateList.add(dates.get(j));
					}
				}else{
					Calendar addDate = Calendar.getInstance();
					addDate.setTime(dates.get(j));
					addDate.clear(Calendar.SECOND);
					addDate.clear(Calendar.MILLISECOND);
					returnDateList.add(addDate.getTime());
				}
			}
		}

		return returnDateList;
	}

	/**takes in a string with date and adds XML to the date portions
	 * 
	 * @param input String with date in it
	 * @return String of xmlDate
	 */
	public String xmlDate(String input) {
		hideErr();
		input = convertFormalDates(input);
		String workingStr = "";

		workingStr += input;
		String returnVal = new String(input);
		ArrayList<Integer> locationQuote = new ArrayList<Integer>();
		for (int i = 0; i < workingStr.length(); i++) {
			char temp = workingStr.charAt(i);
			if ((temp == SINGLEQUOTE)||(temp==DOUBLEQUOTE)) {
				locationQuote.add(i);
			}
		}

		for (int i = 0; i < locationQuote.size(); i = i + 2) {
			if (i + 1 < locationQuote.size()) {
				workingStr = workingStr.substring(0, locationQuote.get(i))
						+ workingStr.substring(locationQuote.get(i + 1)+1, workingStr.length() - 1);
			} else {
				workingStr = workingStr.substring(0, locationQuote.get(i));
			}
		}

		workingStr = workingStr.replace(SINGLEQUOTE, ' ');
		workingStr = workingStr.replace(DOUBLEQUOTE, ' ');

		List<DateGroup> dateGroups = nattyParser.parse(workingStr);
		for (int i = 0; i < dateGroups.size(); i++) {
			List<Date> dates = dateGroups.get(i).getDates();
			returnVal = returnVal.replace(dateGroups.get(i).getText(), "<" + XMLParser.DATE_TAG + ">"
					+ convertFormalDates(dateGroups.get(i).getText()) + "</" + XMLParser.DATE_TAG + ">");
		}
		// System.out.println("test" +returnVal);
		showErr();
		// returnVal = returnVal.replace('\'', ' ');
		return returnVal;
	}

	/**Convert all formal dates from SG to US
	 * 
	 * @param input
	 * @return String formal date in a US format
	 */
	private String convertFormalDates(String input) {
		String convertedFormalDates = new String();
		Scanner sc = new Scanner(input);
		sc.useDelimiter(SPACE_DELIM);
		while (sc.hasNext()) {
			String temp = sc.next();
			convertedFormalDates += convertSGFormalDateToUS(temp) + SPACE_DELIM;
		}
		return convertedFormalDates;
	}

	/**convert a single date from SG to US
	 * 
	 * @param sgDate
	 *            a String in US dates
	 * @return a String in US dates
	 */
	private String convertSGFormalDateToUS(String sgDate) {
		String usDate = sgDate;
		// .replace(FWD_SLASH, DASH);
		// usDate = sgDate.replace(DOT, DASH);
		usDate = addZero(usDate);

		if (usDate.matches(DATE_REGEX_2DAY_2MONTH)) {
			if (usDate.contains("-")) {
				usDate = swapDayMonth(usDate, DASH);
			} else if (usDate.contains("/")) {
				usDate = swapDayMonth(usDate, FWD_SLASH);
			}
		}
		return usDate;

	}

	/**
	 * takes in a formal date and if it does not follow DD/MM/YYYY, it will add
	 * the required zeros
	 * 
	 * @param date
	 * @return String date with format DD/MM/YYYY
	 */
	private String addZero(String date) {
		boolean addToDay = false;
		boolean addToMonth = false;
		if (date.matches(DATE_REGEX_1DAY_1MONTH)) {
			addToDay = true;
			addToMonth = true;
		} else if (date.matches(DATE_REGEX_1DAY_2MONTH)) {
			addToDay = true;
		} else if (date.matches(DATE_REGEX_2DAY_1MONTH)) {
			addToMonth = true;
		}

		if (addToDay) {
			date = CHAR_ZERO + date;
		}
		if (addToMonth) {
			if (date.contains("-")) {
				date = date.substring(START_DAY_INDEX, END_DAY_INDEX) + DASH + CHAR_ZERO
						+ date.substring(START_MONTH_INDEX);
			} else if (date.contains("/")) {
				date = date.substring(START_DAY_INDEX, END_DAY_INDEX) + FWD_SLASH + CHAR_ZERO
						+ date.substring(START_MONTH_INDEX);
			}
		}
		return date;
	}

	/**
	 * Swaps day and month of a date in string with format DD/MM/YYYY
	 * 
	 * @param input
	 * @param seperator
	 *            Character which is used to seperate day month and year
	 * @return the input with the day and month swapped
	 */
	private String swapDayMonth(String input, char seperator) {
		String usDate;
		String day;
		String month;
		String year = new String();
		day = input.substring(START_DAY_INDEX, END_DAY_INDEX);
		month = input.substring(START_MONTH_INDEX, END_MONTH_INDEX);
		if (START_YEAR_INDEX < input.length()) {
			year = input.substring(START_YEAR_INDEX, input.length());
		} else {
			// year = getYear(day, month);
		}
		if (year.length() > 0) {
			usDate = month + seperator + day + seperator + year;
		} else {
			usDate = month + seperator + day;
		}
		return usDate;
	}

	/**
	 * Checks the date and sets the year to next year if it the date has already
	 * passed
	 * 
	 * @param dayStr
	 * @param monthStr
	 * @return String year
	 */
	private String getYear(String dayStr, String monthStr) {
		int currYear = Calendar.getInstance().get(Calendar.YEAR);
		int currDay = Calendar.getInstance().get(Calendar.DATE);
		int currMonth = Calendar.getInstance().get(Calendar.MONTH) + CONVERSION_FROM_GREGORIAN_CAL;
		int inputMonth = Integer.parseInt(monthStr);
		int inputDay = Integer.parseInt(dayStr);
		/*
		 * if (currMonth > inputMonth) { currYear++; } else if ((currMonth ==
		 * inputMonth) && (currDay > inputDay)) { currYear++; }
		 */
		return Integer.toString(currYear);
	}

	/**
	 * Hide error messages
	 */
	private void hideErr() {
		System.setErr(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		}));
	}

	/**
	 * shows error messages
	 */
	private void showErr() {
		System.setErr(printStreamOriginal);
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		DateParser tempNLP = new DateParser();

		boolean running = true;
		while (running) {
			String tempDate = sc.nextLine();
			if (tempDate.equalsIgnoreCase("exit")) {
				running = false;
			} else {
				tempNLP.hideErr();
				List<Date> dates = tempNLP.parseToList(tempDate);
				tempNLP.showErr();
				// System.out.println("test2"+dates);

				tempNLP.hideErr();
				System.out.println(dates);
				tempNLP.showErr();
			}
		}

	}
}
