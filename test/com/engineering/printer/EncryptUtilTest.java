package com.engineering.printer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EncryptUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncryptPassword() {
		String pw = "123456";
		String encryptPw = EncryptUtil.encryptPassword(pw);
		System.out.println(encryptPw);
		assertEquals("ԏԌԍԊԋԈ",encryptPw);
	}

	@Test
	public void testDecryptPassword() {
		String pw = "ԏԌԍԊԋԈ";
		String decryptPw = EncryptUtil.decryptPassword(pw);
		System.out.println(decryptPw);
		assertEquals("123456",decryptPw);
	}

}
