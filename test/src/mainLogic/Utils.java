package mainLogic;

import java.util.Calendar;
import java.util.Comparator;

import entity.TaskEntity;

class Utils {

    /**
     * Converts an integer into its base 36, 0~Z equivalent. Supports any length
     * of characters need to represent its equivalent
     * 
     * @param index - positive integer, throws an error if its negative
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
     * Converts a base 36 number represented by a string into a base 10 int value
     * 
     * @param base36 - String representation of a base36 number
     * @return - positive integer
     */
    public static int convertBase36ToDec(String base36) {
        int decNumber = 0;
        //digitValue increases by 36x per character in the string
        int digitWeight = 1;
        
        while (base36.length() > 0) {
            //Get the current last character of the base36 string
            char lastChar = base36.charAt(base36.length() - 1);
            
            int lastCharAsciiValue = (int) lastChar;
            int characterValue = 0;

            // Identify if the lastChar is an alphabet or number and then cast
            // it to its dec number value of 0~35
            if (lastCharAsciiValue >= 65 && lastCharAsciiValue <= 90) {
                characterValue = lastCharAsciiValue - 55;
            } else if (lastCharAsciiValue >= 48 && lastCharAsciiValue <= 57){
                characterValue = lastCharAsciiValue - 48;
            }
            
            //Add the appropriate value of that digit to the final value
            decNumber += characterValue * digitWeight;
            digitWeight *= 36;
            
            base36 = base36.substring(0, base36.length() - 1);
        }
        return decNumber;
    }
    
    /**
     * 
     * Checks if the 2 tasks passed in are of the same date
     * 
     * @param firstTask
     * @param secondTask
     * @return True - If the dates are the same
     *         False - If either the dates are different, or if either task is
     *         floating
     */
    public static boolean checkSameDate(TaskEntity firstTask, TaskEntity secondTask) {
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
}
