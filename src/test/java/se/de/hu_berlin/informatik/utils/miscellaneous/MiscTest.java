/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Simon
 *
 */
public class MiscTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.utils.miscellaneous.Misc#addToArrayAndReturnResult(T[], T[])}.
	 */
	@Test
	public void testAddToArrayAndReturnResult() throws Exception {
		String[] test = { "test", "test2", "test3" };
		
		test = Misc.addToArrayAndReturnResult(test, "added", "added2");
		assertEquals(test.length, 5);
		
		String[] goal = { "test", "test2", "test3", "added", "added2" };
		assertArrayEquals(test, goal);
		
		test = Misc.addToArrayAndReturnResult(test);
		assertArrayEquals(test, goal);
	}

}
