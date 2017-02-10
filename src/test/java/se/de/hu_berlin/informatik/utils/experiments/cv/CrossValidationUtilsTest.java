/**
 * 
 */
package se.de.hu_berlin.informatik.utils.experiments.cv;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.de.hu_berlin.informatik.utils.experiments.cv.CrossValidationUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author SimHigh
 *
 */
public class CrossValidationUtilsTest extends TestSettings {

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
		deleteTestOutputs();
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
		deleteTestOutputs();
	}

	private static <T extends Comparable<T>> boolean bucketsAreEqual(List<T>[] buckets1, List<T>[] buckets2) {
		if (buckets1.length != buckets2.length) {
			return false;
		}
		for (int i = 0; i < buckets1.length; ++i) {
			if (buckets1[i].size() != buckets2[i].size()) {
				return false;
			}
			Iterator<T> it1 = buckets1[i].iterator();
			Iterator<T> it2 = buckets2[i].iterator();
			while (it1.hasNext()) {
				if (it1.next().compareTo(it2.next()) != 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Test method for {@link se.de.hu_berlin.informatik.utils.experiments.cv.CrossValidationUtils#drawFromArrayIntoNBuckets(T[], int, long)}.
	 */
	@Test
	public void testDrawFromArrayIntoNBuckets() throws Exception {
		String[] array = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };
		
		List<String>[] buckets1 = CrossValidationUtils.drawFromArrayIntoNBuckets(array, 3, 123456789L);
		List<String>[] buckets2 = CrossValidationUtils.drawFromArrayIntoNBuckets(array, 3, 123456789L);
		
//		for (List<String> bucket : buckets) {
//			Log.out(this, bucket.toString());
//		}
		
		assertTrue(bucketsAreEqual(buckets1, buckets2));
		
		List<String>[] buckets3 = CrossValidationUtils.drawFromArrayIntoNBuckets(array, 3);
		
		assertFalse(bucketsAreEqual(buckets1, buckets3));
	}

}
