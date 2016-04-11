// @@author A0125415N
package parser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParserCommons {
    private static final int FIRST_WORD = 0;

    /**
     * returns the last word of a given string
     * 
     * @param input
     * @return String last word
     */
    public static String getLastWord(String input) {
        String returnVal = null;
        String[] seperated = input.split(" ");
        if (seperated.length > 0) {
            returnVal = seperated[seperated.length - 1];
        }
        return returnVal;
    }

    /**
     * For File reading, Converts a Date.toString() value and returns a Calendar
     * object
     * 
     * @param input
     * @return Calendar
     */
    public static Calendar getDate(String input) {
        Calendar c = Calendar.getInstance();
        DateParser dp = new DateParser();
        List<Date> d = dp.parseToList(input);
        c.clear(Calendar.MILLISECOND);
        c.setTime(d.get(0));
        return c;
    }

    /**
     * pad an input if its less than 2 digits, used for hours and mins
     * 
     * @param int hour (or min)
     * @return padded hour (or min)
     */
    public static String padTime(int field) {
        String returnVal = "00";
        if (field != 0) {
            if (field < 10) {
                returnVal = "0" + field;
            } else {
                returnVal = Integer.toString(field);
            }
        }
        return returnVal;
    }

    /**
     * creates a date time in format xth month year xxxxhrs
     * 
     * @param Calendar c
     * @return a string in date followed by time
     */
    public static String detailedDateTime(Calendar c) {
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        ReverseParser rp = new ReverseParser();
        String returnVal = rp.detailedReParse(c);
        returnVal += " " + padTime(hour);
        returnVal += padTime(min);
        returnVal += "hrs";
        return returnVal;
    }

    /**
     * returns the first word of a given String
     * 
     * @param input
     * @return String, first word
     */
    public static String getFirstWord(String input) {
        String returnStrVal = input.trim().split("\\s+")[FIRST_WORD];
        return returnStrVal;
    }
}
