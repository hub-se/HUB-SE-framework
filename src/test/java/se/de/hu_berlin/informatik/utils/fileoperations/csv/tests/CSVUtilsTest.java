/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations.csv.tests;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.files.csv.CSVUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.TestSettings;

/**
 * @author SimHigh
 *
 */
public class CSVUtilsTest extends TestSettings {

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

	private static <T extends Comparable<T>> boolean arrayListsAreEqual(List<T[]> buckets1, List<T[]> buckets2) {
		if (buckets1.size() != buckets2.size()) {
			return false;
		}
		Iterator<T[]> it1 = buckets1.iterator();
		Iterator<T[]> it2 = buckets2.iterator();
		while (it1.hasNext()) {
			T[] array1 = it1.next();
			T[] array2 = it2.next();
			if (array1.length != array2.length) {
				return false;
			}
			for (int i = 0; i < array1.length; ++i) {
				if (array1[i].compareTo(array2[i]) != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.utils.files.csv.CSVUtils#toCsvFile(java.util.List, java.nio.file.Path)}.
	 */
	@Test
	public void testToCsvFile() throws Exception {
		List<String[]> arrayList = new ArrayList<>();
		arrayList.add(new String[] {"1", "2", "3", "8"});
		arrayList.add(new String[] {"4", "5", "6", ""});
		
		Path output = Paths.get(getStdTestDir(), "test.csv");
		CSVUtils.toCsvFile(arrayList, false, output);
		
		List<String[]> arrayList2 = CSVUtils.readCSVFileToListOfStringArrays(output, false);
		
		assertTrue(arrayListsAreEqual(arrayList, arrayList2));
		
		List<String[]> arrayList3 = new ArrayList<>();
		arrayList3.add(new String[] {"1", "4"});
		arrayList3.add(new String[] {"2", "5"});
		arrayList3.add(new String[] {"3", "6"});
		arrayList3.add(new String[] {"8", ""});
		
		CSVUtils.toCsvFile(arrayList, true, output);
		
		List<String[]> arrayList4 = CSVUtils.readCSVFileToListOfStringArrays(output, false);
		
		assertTrue(arrayListsAreEqual(arrayList3, arrayList4));
		
		List<String[]> arrayList5 = CSVUtils.readCSVFileToListOfStringArrays(output, true);
		
		assertTrue(arrayListsAreEqual(arrayList5, arrayList));
	}
	
	/**
	 * Test method for {@link se.de.hu_berlin.informatik.utils.files.csv.CSVUtils#toCsvFile(java.util.List, java.nio.file.Path)}.
	 */
	@Test
	public void testToCsvFile2() throws Exception {
		List<String[]> arrayList = new ArrayList<>();
		arrayList.add(new String[] {"1", "2"});
		arrayList.add(new String[] {"4", "5"});
		arrayList.add(new String[] {"6", "7"});
		arrayList.add(new String[] {"8", "9"});
		
		Path output = Paths.get(getStdTestDir(), "test2.csv");
		CSVUtils.toCsvFile(arrayList, false, output);
		
		List<String[]> arrayList2 = CSVUtils.readCSVFileToListOfStringArrays(output, false);
		
		assertTrue(arrayListsAreEqual(arrayList, arrayList2));
		
		List<String[]> arrayList3 = new ArrayList<>();
		arrayList3.add(new String[] {"1", "4", "6", "8"});
		arrayList3.add(new String[] {"2", "5", "7", "9"});
		
		CSVUtils.toCsvFile(arrayList, true, output);
		
		List<String[]> arrayList4 = CSVUtils.readCSVFileToListOfStringArrays(output, false);
		
		assertTrue(arrayListsAreEqual(arrayList3, arrayList4));
		
		List<String[]> arrayList5 = CSVUtils.readCSVFileToListOfStringArrays(output, true);
		
		assertTrue(arrayListsAreEqual(arrayList5, arrayList));
	}
	
	

}
