package com.engineering.printer;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DocumentTest {

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
	public void testLoadFile() {
		File f = new File("./project.properties");
		Document.loadFile(f);
		assertTrue(Document.data != null);
	}

	@Test
	public void testAddToHistory() {
		Document.addToHistory("1.txt");
		assertTrue(Document.history.size() == 1);
	}

	@Test
	public void testGetHistory() {
		Document.addToHistory("1.txt");
		int size = Document.getHistory().size();
		assertTrue(size == 1);
	}

}
