package dateParser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.joestelmach.natty.*;

public class DateNLP {
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

	PrintStream printStreamOriginal = System.err;
	private Parser nattyParser = new Parser();

	public DateNLP() {
		// init the parser so that there's no lag later
		hideErr();
		nattyParser.parse("today");
		showErr();
	}

	public List<Date> parseToList(String inputDate) {
		inputDate = convertFormalDates(inputDate);
		List<Date> returnDateList = new ArrayList<Date>();
		List<DateGroup> dateGroups = nattyParser.parse(inputDate);
		for (int i = 0; i < dateGroups.size(); i++) {
			List<Date> dates = dateGroups.get(i).getDates();
			for (int j = 0; j < dates.size(); j++) {
				returnDateList.add(dates.get(j));
			}
		}
		return returnDateList;
	}

	public String XMLDate(String input) {
		hideErr();
		String returnVal = input;
		input = convertFormalDates(input);
		List<DateGroup> dateGroups = nattyParser.parse(input);
		for (int i = 0; i < dateGroups.size(); i++) {
			List<Date> dates = dateGroups.get(i).getDates();
			returnVal = returnVal.replace(dateGroups.get(i).getText(), "<dates>" + dates + "</dates>");
		}
		showErr();
		return returnVal;
	}

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

	private String convertSGFormalDateToUS(String sgDate) {
		String usDate = sgDate.replace(FWD_SLASH, DASH);
		usDate = sgDate.replace(DOT, DASH);

		usDate = addZero(usDate);

		if (usDate.matches(DATE_REGEX_2DAY_2MONTH)) {
			usDate = swapDayMonth(usDate, DASH);
		}
		return usDate;

	}

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
			date = date.substring(START_DAY_INDEX, END_DAY_INDEX) + DASH + CHAR_ZERO
					+ date.substring(START_MONTH_INDEX);
		}
		return date;
	}

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
			year = getYear(day, month);
		}
		usDate = month + seperator + day + seperator + year;
		return usDate;
	}

	private String getYear(String dayStr, String monthStr) {
		int currYear = Calendar.getInstance().get(Calendar.YEAR);
		int currDay = Calendar.getInstance().get(Calendar.DATE);
		int currMonth = Calendar.getInstance().get(Calendar.MONTH) + CONVERSION_FROM_GREGORIAN_CAL;
		int inputMonth = Integer.parseInt(monthStr);
		int inputDay = Integer.parseInt(dayStr);
		if (currMonth > inputMonth) {
			currYear++;
		} else if ((currMonth == inputMonth) && (currDay > inputDay)) {
			currYear++;
		}

		return Integer.toString(currYear);
	}

	private void hideErr() {
		System.setErr(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		}));
	}

	private void showErr() {
		System.setErr(printStreamOriginal);
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		DateNLP tempNLP = new DateNLP();

		boolean running = true;
		while (running) {
			String tempDate = sc.nextLine();
			if (tempDate.equalsIgnoreCase("exit")) {
				running = false;
			} else {
				tempNLP.hideErr();
				List<Date> dates = tempNLP.parseToList(tempDate);
				tempNLP.showErr();

				System.out.println(dates);

				tempNLP.hideErr();
				System.out.println(tempNLP.XMLDate(tempDate));
				tempNLP.showErr();
			}
		}

	}
}
