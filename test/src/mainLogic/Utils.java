package mainLogic;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Queue;

import entity.TaskEntity;

public class Utils {

	/**
	 * Converts an integer into its base 36, 0~Z equivalent. Supports any length
	 * of characters need to represent its equivalent
	 * 
	 * @param index
	 *            - positive integer, throws an error if its negative
	 * @return - returns a string of the base 36 value
	 */
	public static String convertDecToBase36(int index) {

		if (index < 0) {
			throw new Error("Converted number should be non-negative!");
		}

		String base36 = "";

		// 36 characters from 0~Z, break the int value down into each of their
		// base 36 equivalent number
		while (index / 36 >= 1) {
			// Supposed to be cast into integer to drop off its decimal value
			int currentDigit = index % 36;
			index = index / 36;

			if (currentDigit < 10) {
				base36 = Integer.toString(currentDigit) + base36;
			} else if (currentDigit < 36) {
				// Match the values 10~35 to match A~Z in the ASCII table
				base36 = (char) (currentDigit + 55) + base36;
			}
		}

		// Append last character
		if (index < 10) {
			base36 = Integer.toString(index) + base36;
		} else if (index < 36) {
			// Match the values 10~35 to match A~Z in the ASCII table
			base36 = (char) (index + 55) + base36;
		}

		return base36;
	}

	/**
	 * Converts a string to integer, NumberFormatException returns -1
	 * 
	 * @param str
	 * @return - int
	 */
	public static int convertStringToInteger(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return -2;
		}
	}

	/**
	 * Converts a base 36 number represented by a string into a base 10 int
	 * value. Function will strip ALL spaces in the string
	 * 
	 * @param base36
	 *            - String representation of a base36 number
	 * @return - positive integer -1 if unexpected input is given
	 */
	public static int convertBase36ToDec(String base36) {
		int decNumber = 0;
		// digitValue increases by 36x per character in the string
		int digitWeight = 1;

		// Additionalo check if string passed in is just spaces
		boolean hasValue = false;

		assert base36 != null : "Null string passed into convertBase36ToDec!";
		if (base36 == null) {
			return -1;
		}

		while (base36.length() > 0) {
			// Get the current last character of the base36 string
			char lastChar = base36.charAt(base36.length() - 1);

			int lastCharAsciiValue = (int) lastChar;
			int characterValue = 0;

			// Identify if the lastChar is an alphabet or number and then cast
			// it to its dec number value of 0~35
			if (checkIfAsciiUppercase(lastCharAsciiValue)) {
				characterValue = lastCharAsciiValue - 55;
			} else if (checkIfAsciiLowercase(lastCharAsciiValue)) {
				characterValue = lastCharAsciiValue - 87;
			} else if (checkIfAsciiNumber(lastCharAsciiValue)) {
				characterValue = lastCharAsciiValue - 48;
			} else if (checkIfAsciiSpaceChar(lastCharAsciiValue)) {
				return -1;
			}

			// Add the appropriate value of that digit to the final value
			if (checkIfAsciiSpaceChar(lastCharAsciiValue)) {
				decNumber += characterValue * digitWeight;
				digitWeight *= 36;
				hasValue = true;
			}
			base36 = base36.substring(0, base36.length() - 1);
		}

		// Return error message of -1 if there are only spaces or no characters
		// in the string
		if (hasValue == false) {
			return -1;
		}

		return decNumber;
	}

	private static boolean checkIfAsciiSpaceChar(int lastCharAsciiValue) {
		return lastCharAsciiValue != 32;
	}

	private static boolean checkIfAsciiNumber(int lastCharAsciiValue) {
		return lastCharAsciiValue >= 48 && lastCharAsciiValue <= 57;
	}

	private static boolean checkIfAsciiLowercase(int lastCharAsciiValue) {
		return lastCharAsciiValue >= 97 && lastCharAsciiValue <= 122;
	}

	private static boolean checkIfAsciiUppercase(int lastCharAsciiValue) {
		return lastCharAsciiValue >= 65 && lastCharAsciiValue <= 90;
	}

	/**
	 * 
	 * Checks if the 2 tasks passed in are of the same date
	 * 
	 * @param firstTask
	 *            - first task to be compared
	 * @param secondTask
	 *            - second task to be compared
	 * @return True - If the dates are the same False - If either the dates are
	 *         different, or if either task is floating
	 */
	public static boolean checkSameDate(TaskEntity firstTask, TaskEntity secondTask) {
		// Floating tasks cannot be compared, check if either tasks is floating
		Calendar firstDate;
		if (firstTask.isFloating()) {
			return false;
		} else {
			firstDate = firstTask.getDueDate();
		}

		Calendar secondDate;
		if (secondTask.isFloating()) {
			return false;
		} else {
			secondDate = secondTask.getDueDate();
		}

		return checkSameDate(firstDate, secondDate);
	}

	public static boolean checkSameDate(Calendar firstDate, Calendar secondDate) {
		if (firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
				&& firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH)
				&& firstDate.get(Calendar.DATE) == secondDate.get(Calendar.DATE)) {
			return true;
		}
		return false;
	}

	/**
	 * Used to generate a calendar object with the passed in parameters
	 * 
	 * @param day
	 *            - Used to set DAY_OF_MONTH field in calendar
	 * @param month
	 *            - Used to set MONTH field in calendar
	 * @param year
	 *            - Used to set YEAR field in the calendar
	 * 
	 * @return Calendar object with the passed in fields
	 */
	public static Calendar createDate(int day, int month, int year) {
		Calendar newDate = Calendar.getInstance();
		newDate.clear();
		newDate.set(year, month, day);
		return newDate;
	}

	/**
	 * Used to generate a calendar object with the passed in parameters
	 * 
	 * @param day
	 *            - Used to set DAY_OF_MONTH field in calendar
	 * @param month
	 *            - Used to set MONTH field in calendar
	 * @param year
	 *            - Used to set YEAR field in the calendar
	 * @param hour
	 *            - Used to set HOUR_OF_DAY field in the calendar
	 * @param minutes
	 *            - Used to set MINUTES field in the calendar
	 * @return
	 */
	public static Calendar createDate(int day, int month, int year, int hour, int minutes) {
		Calendar newDate = Calendar.getInstance();
		newDate.clear();
		newDate.set(year, month, day, hour, minutes);
		return newDate;
	}
}
