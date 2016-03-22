package test.userInterface;

import static org.junit.Assert.*;

import org.junit.Test;

import userInterface.UserInterfaceController;

public class JUnitUserInterfaceController {

	@Test
	public void testSingleton() {
		UserInterfaceController ui1 = UserInterfaceController.getInstance(null);
		UserInterfaceController ui2 = UserInterfaceController.getInstance(null);
		assertEquals(ui2,null);
	}

	
}
