/**
 * @author qy
 * @@author A0125493A
 * 
 *          Common utilities from logic for use through the program
 */
package logic;

import java.util.Calendar;

import entity.TaskEntity;

public class TaskUtils {

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

            base36 = appendChar(currentDigit, base36);
        }

        // Append last character
        base36 = appendChar(index, base36);

        return base36;
    }

    /**
     * Appends the next character to the end of the number. For
     * convertDecToBase36
     * 
     * @param index - value of next digit to append
     * @param base36 - currently compiled string of base 36 number
     * @return new String with next appended character
     */
    private static String appendChar(int index, String base36) {
        if (index < 10) {
            base36 = Integer.toString(index) + base36;
        } else if (index < 36) {
            // Match the values 10~35 to match A~Z in the ASCII table
            base36 = (char) (index + 55) + base36;
        }
        return base36;
    }

    /**
     * Checks if the name of a task is a valid name (Non-empty)
     * 
     * @param taskToCheck
     * @return true if the name is not null,just whitespace characters or empty
     *         false otherwise
     */
    public static boolean checkValidName(TaskEntity taskToCheck) {
        if (taskToCheck.getName() == null) {
            return false;
        } else if (taskToCheck.getName().trim() == "") {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Converts a string representing a number to integer
     * 
     * @param str - String to convert
     * @return - converted integer in int, returns -2 if there is a number
     *         format exception
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
     * @param base36 - String representation of a base36 number
     * @return - positive integer -1 if unexpected input is given
     */
    public static int convertBase36ToDec(String base36) {
        int decNumber = 0;
        // digitValue increases by 36x per character in the string
        int digitWeight = 1;

        // Additional check if string passed in is just spaces
        boolean isWithValue = false;

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
            characterValue = castCharacterValue(lastCharAsciiValue, characterValue);
            if (characterValue == -1) {
                return -1;
            }

            // Add the appropriate value of that digit to the final value
            if (checkIfAsciiSpaceChar(lastCharAsciiValue)) {
                decNumber += characterValue * digitWeight;
                digitWeight *= 36;
                isWithValue = true;
            }
            base36 = base36.substring(0, base36.length() - 1);
        }

        // Return error message of -1 if there are only spaces or no characters
        // in the string
        if (isWithValue == false) {
            return -1;
        }

        return decNumber;
    }

    private static int castCharacterValue(int lastCharAsciiValue, int characterValue) {
        if (checkIfAsciiUppercase(lastCharAsciiValue)) {
            characterValue = lastCharAsciiValue - 55;
        } else if (checkIfAsciiLowercase(lastCharAsciiValue)) {
            characterValue = lastCharAsciiValue - 87;
        } else if (checkIfAsciiNumber(lastCharAsciiValue)) {
            characterValue = lastCharAsciiValue - 48;
        } else if (checkIfAsciiSpaceChar(lastCharAsciiValue)) {
            characterValue = -1;
        }
        return characterValue;
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
     * @param firstTask - first task to be compared
     * @param secondTask - second task to be compared
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

    /**
     * Checks if 2 Calendar dates passed in are in the same day
     * 
     * @param firstDate
     * @param secondDate
     * @return True if it is, false otherwise
     */
    public static boolean checkSameDate(Calendar firstDate, Calendar secondDate) {
        if (firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
                && firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH)
                && firstDate.get(Calendar.DATE) == secondDate.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    /**
     * Clears the seconds and milliseconds off a calendar object
     * 
     * @param timeToTrim
     */
    public static void clearSeconds(Calendar timeToTrim) {
        timeToTrim.set(Calendar.SECOND, 0);
        timeToTrim.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Checks if 2 tasks are clashing
     * 
     * @param task1
     * @param task2
     * @return True if clashing
     *         False if not clashing or one of the variables to be checked is
     *         not set
     */
    public static boolean isClashing(TaskEntity task1, TaskEntity task2) {
        if (task1.getStartDate() == null) {
            if (task2.getStartDate() == null) {
                return TaskUtils.compareTwoDueDates(task1, task2);
            } else {
                return TaskUtils.checkDueDateInRange(task1, task2);
            }
        } else {
            if (task2.getStartDate() == null) {
                return TaskUtils.checkDueDateInRange(task2, task1);
            } else {
                return TaskUtils.checkOverlappingDuration(task1, task2);
            }
        }
    }

    /**
     * Checks if the durations of 2 tasks are overlapping
     * 
     * @param task1 - tasks to be compared
     * @param task2 - tasks to be compared
     * @return true if the range from start-end time of both tasks intersects
     *         false otherwise
     */
    public static boolean checkOverlappingDuration(TaskEntity task1, TaskEntity task2) {
        assert task1.getDueDate() != null && task1.getStartDate() != null && task2.getDueDate() != null
                && task2.getStartDate() != null : "One of the variables being compared is null in checkOverlappingDuration";

        if (task1.getDueDate() == null || task1.getStartDate() == null || task2.getDueDate() == null
                || task2.getStartDate() == null) {
            return false;
        } else if (task1.isFullDay() || task2.isFullDay()) {
            return false;
        } else {
            return checkOverlapping(task1, task2);
        }
    }

    /**
     * Checks if either startDate or dueDate of task1 is within the range of
     * startDate and dueDate of task2. Function strips the seconds and
     * milliseconds of all dates compared
     * 
     * @param task1
     * @param task2
     * @return True if either dates is within range
     *         false otherwise
     */
    private static boolean checkOverlapping(TaskEntity task1, TaskEntity task2) {
        clearSeconds(task1.getDueDate());
        clearSeconds(task1.getStartDate());
        clearSeconds(task2.getDueDate());
        clearSeconds(task2.getStartDate());

        if (task1.getDueDate().compareTo(task2.getDueDate()) <= 0
                && task1.getDueDate().compareTo(task2.getStartDate()) >= 0) {
            return true;
        } else if (task1.getStartDate().compareTo(task2.getDueDate()) <= 0
                && task1.getStartDate().compareTo(task2.getStartDate()) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a due date of the first task is within the range specified by
     * the startDate and dueDate of the second task
     * 
     * @param dueDate - Task with due date to be checked
     * @param range - Task with the start and end time
     * @return True if the due date is within the 2 timing
     *         False if the due date is within the
     */
    public static boolean checkDueDateInRange(TaskEntity dueDate, TaskEntity range) {
        assert range.getDueDate() != null && range.getDueDate() != null && range
                .getStartDate() != null : "One of the variables being compared is null in checkDueDateInRange";

        if (range.getDueDate() == null || range.getDueDate() == null || range.getStartDate() == null) {
            return false;
        } else if (dueDate.isFullDay() || range.isFullDay()) {
            return false;
        } else {
            return checkWithinRange(dueDate, range);
        }
    }

    /**
     * Checks if dueDate of "dueDate" variable is within the range specified by
     * startDate and dueDate of "range" variable. Function strips the seconds
     * and milliseconds of all dates compared
     * 
     * @param dueDate
     * @param range
     * @return true if it is within the range
     *         false otherwise
     */
    private static boolean checkWithinRange(TaskEntity dueDate, TaskEntity range) {
        clearSeconds(dueDate.getDueDate());
        clearSeconds(range.getDueDate());
        clearSeconds(range.getStartDate());

        if (dueDate.getDueDate().compareTo(range.getDueDate()) <= 0
                && dueDate.getDueDate().compareTo(range.getStartDate()) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares if the 2 due dates of the task is the same (Task order dont
     * matter)
     * 
     * @param task1 - First task to be compared
     * @param task2 - Second task to be compared
     * @return True if the same
     *         False if not the same, or fail to check
     */
    public static boolean compareTwoDueDates(TaskEntity task1, TaskEntity task2) {
        assert task1.getDueDate() != null && task2
                .getDueDate() != null : "Comparing floating task or corrupted non-floating task set to null for clashing";

        if (task1.getDueDate() == null || task2.getDueDate() == null) {
            return false;
        } else if (task1.isFullDay() || task2.isFullDay()) {
            return false;
        } else {
            return checkSameDueDate(task1, task2);
        }
    }

    /**
     * Checks if the dueDates of both tasks are the same. Function strips the
     * seconds and milliseconds of all dates compared
     * 
     * @param task1
     * @param task2
     * @return True if the dueDates are the same
     *         false otherwise
     */
    private static boolean checkSameDueDate(TaskEntity task1, TaskEntity task2) {
        clearSeconds(task1.getDueDate());
        clearSeconds(task2.getDueDate());
        if (task1.getDueDate().compareTo(task2.getDueDate()) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Used to generate a calendar object with the passed in time
     * 
     * @param day - Used to set DAY_OF_MONTH field in calendar
     * @param month - Used to set MONTH field in calendar
     * @param year - Used to set YEAR field in the calendar
     * @return Calendar object with the passed in fields
     */
    public static Calendar createDate(int day, int month, int year) {
        Calendar newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(year, month, day);
        return newDate;
    }

    /**
     * Used to generate a calendar object with the passed in time
     * 
     * @param day - Used to set DAY_OF_MONTH field in calendar
     * @param month - Used to set MONTH field in calendar
     * @param year - Used to set YEAR field in the calendar
     * @param hour - Used to set HOUR_OF_DAY field in the calendar
     * @param minutes - Used to set MINUTES field in the calendar
     * @return Calendar object with the passed in fields
     */
    public static Calendar createDate(int day, int month, int year, int hour, int minutes) {
        Calendar newDate = Calendar.getInstance();
        newDate.clear();
        newDate.set(year, month, day, hour, minutes);
        return newDate;
    }

    /**
     * Checks if a date is before the current time
     * 
     * @param timeToCheck
     * @return true is it is before, false otherwise
     */
    public static boolean isDateBeforeNow(Calendar timeToCheck) {
        Calendar currentTime = Calendar.getInstance();

        if (currentTime.compareTo(timeToCheck) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the firstDate is on a day before the secondDate in the same
     * year
     * 
     * @param firstDate
     * @param secondDate
     * @return True if the first date is on a day before the secondDate in the
     *         same year,false otherwise
     */
    public static boolean dayBefore(Calendar firstDate, Calendar secondDate) {
        if (firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)) {
            if (firstDate.get(Calendar.MONTH) < secondDate.get(Calendar.MONTH)) {
                return true;
            } else if (firstDate.get(Calendar.MONTH) > secondDate.get(Calendar.MONTH)) {
                return false;
            } else if (firstDate.get(Calendar.DAY_OF_MONTH) < secondDate.get(Calendar.DAY_OF_MONTH)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if the firstDate is set at a time before the secondDate on the
     * same day
     * 
     * @param firstDate
     * @param secondDate
     * @return True if it is
     *         False otherwise
     */
    public static boolean timeBefore(Calendar firstDate, Calendar secondDate) {
        if (checkSameDate(firstDate, secondDate)) {
            if (firstDate.get(Calendar.HOUR_OF_DAY) < secondDate.get(Calendar.HOUR_OF_DAY)) {
                return true;
            } else if (firstDate.get(Calendar.HOUR_OF_DAY) > secondDate.get(Calendar.HOUR_OF_DAY)) {
                return false;
            } else if (firstDate.get(Calendar.MINUTE) < secondDate.get(Calendar.MINUTE)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Function to ensure that the dueDate of a task is not before the startTime
     * of it
     * 
     * @param taskToBeProcessed
     * @return same task passed in, but with date correction
     */
    public static TaskEntity calculateSecondDate(TaskEntity taskToBeProcessed) {
        Calendar firstDate = taskToBeProcessed.getStartDate();
        Calendar secondDate = taskToBeProcessed.getDueDate();

        if (firstDate == null || secondDate == null) {
            return taskToBeProcessed;
        }

        if (firstDate.compareTo(secondDate) < 0) {
            return taskToBeProcessed;
        }

        if (firstDate.get(Calendar.YEAR) < secondDate.get(Calendar.YEAR)) {
            return taskToBeProcessed;
        } else if (firstDate.get(Calendar.YEAR) > secondDate.get(Calendar.YEAR)) {
            secondDate.set(Calendar.YEAR, firstDate.get(Calendar.YEAR));
            taskToBeProcessed.setDate(firstDate, secondDate, taskToBeProcessed.isFullDay());
        }

        return processSameYearTask(taskToBeProcessed, firstDate, secondDate);
    }

    /**
     * Processes the 2 dates of a task that is on the same year. Causes any
     * start time before end time to be after logically
     * 
     * @param taskToBeProcessed
     * @param firstDate
     * @param secondDate
     * @return Same task with edited dates to make sense
     */
    private static TaskEntity processSameYearTask(TaskEntity taskToBeProcessed, Calendar firstDate,
            Calendar secondDate) {
        if (dayBefore(secondDate, firstDate)) {
            Calendar newTime = secondDate;
            newTime.set(Calendar.YEAR, secondDate.get(Calendar.YEAR) + 1);
            taskToBeProcessed.setDate(firstDate, newTime, taskToBeProcessed.isFullDay());
            return taskToBeProcessed;
        } else if (timeBefore(secondDate, firstDate) && !taskToBeProcessed.isFullDay()) {
            Calendar newTime = secondDate;
            newTime.set(Calendar.DAY_OF_MONTH, newTime.get(Calendar.DAY_OF_MONTH) + 1);
            taskToBeProcessed.setDate(firstDate, newTime, taskToBeProcessed.isFullDay());
            return taskToBeProcessed;
        } else {
            // Same date and hence, remove start time
            taskToBeProcessed.setDate(secondDate, taskToBeProcessed.isFullDay());
            return taskToBeProcessed;
        }
    }
}
