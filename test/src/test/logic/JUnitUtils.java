/**
 * @author qy
 * @@author a0125493a
 * 
 *          Testing unit for TaskUtils's functionalities
 */

package test.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import entity.TaskEntity;
import mainLogic.TaskUtils;

public class JUnitUtils {

	@Test
	public void testConvertDecToBase36() {
		String r0 = TaskUtils.convertDecToBase36(0);
		String r1 = TaskUtils.convertDecToBase36(100);
		String r2 = TaskUtils.convertDecToBase36(1000);
		String r3 = TaskUtils.convertDecToBase36(10000);
		String r4 = TaskUtils.convertDecToBase36(100000);

		assertEquals(r0, "0");
		assertEquals(r1, "2S");
		assertEquals(r2, "RS");
		assertEquals(r3, "7PS");
		assertEquals(r4, "255S");
	}

	@Test
	public void convertBase36ToDec() {
		int r1 = TaskUtils.convertBase36ToDec("AF");
		int r2 = TaskUtils.convertBase36ToDec("asd");
		int r3 = TaskUtils.convertBase36ToDec("");
		int r4 = TaskUtils.convertBase36ToDec("-");

		assertEquals(r1, 375);
		assertEquals(r2, 13981);
		assertEquals(r3, -1);
		assertEquals(r4, -1);
	}
	
	@Test
    public void testDueDateInRange () {
        TaskEntity due2MinutesPast3Am = new TaskEntity("Due 3:02", null, TaskUtils.createDate(1, 1, 2016, 3, 2), false);
        TaskEntity due4Am = new TaskEntity("Due 4am", null, TaskUtils.createDate(1, 1, 2016, 4, 0), false);
        TaskEntity due5Am = new TaskEntity("Due 5am", null, TaskUtils.createDate(1, 1, 2016, 5, 0), false);
        TaskEntity due6Am = new TaskEntity("Due 6am", null, TaskUtils.createDate(1, 1, 2016, 6, 0), false);
        TaskEntity due4AmOtherDay = new TaskEntity("Due 4am another day", null, TaskUtils.createDate(2, 1, 2016, 6, 0), false);
        TaskEntity from0300to0500 = new TaskEntity("From 3:05 to 5:00", TaskUtils.createDate(1, 1, 2016, 3, 5), TaskUtils.createDate(1, 1, 2016, 5, 0), false);

        assertEquals(false, TaskUtils.checkDueDateInRange(due2MinutesPast3Am, from0300to0500 ));
        assertEquals(true, TaskUtils.checkDueDateInRange(due4Am, from0300to0500 ));
        assertEquals(true, TaskUtils.checkDueDateInRange(due5Am, from0300to0500 ));
        assertEquals(false, TaskUtils.checkDueDateInRange(due6Am, from0300to0500 ));
        assertEquals(false, TaskUtils.checkDueDateInRange(due4AmOtherDay, from0300to0500 ));
    }
	
	@Test
    public void testCheckOverlappingDuration () {
	    TaskEntity from0300to0500 = new TaskEntity("From 3:05 to 5:00", TaskUtils.createDate(1, 1, 2016, 3, 5), TaskUtils.createDate(1, 1, 2016, 5, 0), false);
	    TaskEntity from0652to1100 = new TaskEntity("From 6:52 to 11:00", TaskUtils.createDate(1, 1, 2016, 6, 52), TaskUtils.createDate(1, 1, 2016, 11, 0), false);
	    TaskEntity from0152to0428 = new TaskEntity("From 1:52 to 4:28", TaskUtils.createDate(1, 1, 2016, 1, 52), TaskUtils.createDate(1, 1, 2016, 4, 28), false);
	    TaskEntity from1010to2210 = new TaskEntity("From 10:10 to 22:10", TaskUtils.createDate(1, 1, 2016, 10, 10), TaskUtils.createDate(1, 1, 2016, 22, 10), false);
	    TaskEntity from0000to2359OtherDay = new TaskEntity("From 00:00 to 23:59 another day", TaskUtils.createDate(2, 1, 2016, 0, 0), TaskUtils.createDate(2, 1, 2016, 23, 39), false);

        assertEquals(false, TaskUtils.checkOverlappingDuration(from0300to0500, from0652to1100 ));
        assertEquals(false, TaskUtils.checkOverlappingDuration(from0152to0428, from0000to2359OtherDay ));
        assertEquals(true, TaskUtils.checkOverlappingDuration(from0652to1100, from1010to2210 ));
        assertEquals(true, TaskUtils.checkOverlappingDuration(from0300to0500, from0152to0428 ));
        assertEquals(false, TaskUtils.checkOverlappingDuration(from0652to1100, from0152to0428 ));
    }
}
