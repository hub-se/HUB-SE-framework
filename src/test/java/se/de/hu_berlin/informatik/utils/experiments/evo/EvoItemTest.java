/**
 * 
 */
package se.de.hu_berlin.informatik.utils.experiments.evo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.experiments.evo.EvoItem.History;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author Simon
 *
 */
public class EvoItemTest extends TestSettings {

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
		deleteTestOutputs();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		deleteTestOutputs();
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.utils.experiments.evo.EvoItem.History#History(java.lang.Object)}.
	 */
	@Test
	public void testHistory() throws Exception {
		Object origin1 = new Object();
		Object origin2 = new Object();
		History<Object> history1 = new History<>(origin1);
		History<Object> history2 = new History<>(origin2);
		
		assertTrue(history1.equals(history1));
		assertFalse(history1.equals(history2));
		assertFalse(history2.equals(history1));
		
		history1.addMutationId(new EvoID(0,0));
		history1.addMutationId(new EvoID(0,0));
		history1.addMutationId(new EvoID(0,1));
		
		history2.addMutationId(new EvoID(0,0));
		history2.addMutationId(new EvoID(0,0));
		history2.addMutationId(new EvoID(0,1));
		
		assertTrue(history1.equals(history1));
		assertFalse(history1.equals(history2));
		assertFalse(history2.equals(history1));
		
		History<Object> history3 = history1.copy();
		assertTrue(history1.equals(history3));
		assertTrue(history3.equals(history1));
		
		history1.addMutationId(new EvoID(0,0));
		
		assertFalse(history1.equals(history3));
		assertFalse(history3.equals(history1));
		
		History<Object> childHistory = new History<>(history1, history2, new EvoID(0,0));
		
		assertTrue(childHistory.equals(childHistory));
		assertFalse(childHistory.equals(history1));
		assertFalse(childHistory.equals(history2));
		assertFalse(childHistory.equals(history3));
		
		History<Object> copiedChild = childHistory.copy();
		
		assertTrue(copiedChild.equals(childHistory));
		
		copiedChild.addMutationId(new EvoID(1,2));
		copiedChild.addMutationId(new EvoID(12,1));
		
		assertFalse(copiedChild.equals(childHistory));
	}
	
	
}
