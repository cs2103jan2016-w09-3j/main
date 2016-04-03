package test.userInterface;

import static org.junit.Assert.*;

import org.junit.Test;

import userInterface.TaskViewUserInterface;

public class JUnitUserInterfaceTaskView {

	@Test
	public void testInitilize() {
		TaskViewUserInterface t1 = TaskViewUserInterface.getInstance(null, null, false,null,null);
		assertEquals(t1, null);
		TaskViewUserInterface t2 = TaskViewUserInterface.getInstance(null, null, false,null,null);
		assertEquals(t2, null);
	}
}
