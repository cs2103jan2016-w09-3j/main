package test.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mainLogic.Utils;

public class JUnitUtils {

	@Test
	public void testConvertDecToBase36() {
		String r0 = Utils.convertDecToBase36(0);
		String r1 = Utils.convertDecToBase36(100);
		String r2 = Utils.convertDecToBase36(1000);
		String r3 = Utils.convertDecToBase36(10000);
		String r4 = Utils.convertDecToBase36(100000);

		assertEquals(r0, "0");
		assertEquals(r1, "2S");
		assertEquals(r2, "RS");
		assertEquals(r3, "7PS");
		assertEquals(r4, "255S");
	}

	@Test
	public void convertBase36ToDec() {
		int r1 = Utils.convertStringToInteger("AF");
		int r2 = Utils.convertStringToInteger("asd");
		int r3 = Utils.convertStringToInteger("");
		int r4 = Utils.convertStringToInteger("-");

		assertEquals(r1, 375);
		assertEquals(r2, 13981);
		assertEquals(r3, -1);
		assertEquals(r4, -1);
	}
}
