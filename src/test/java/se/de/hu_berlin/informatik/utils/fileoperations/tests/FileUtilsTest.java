/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.de.hu_berlin.informatik.utils.fileoperations.FileUtils;

/**
 * @author SimHigh
 *
 */
public class FileUtilsTest {

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
	 * Test method for {@link se.de.hu_berlin.informatik.utils.fileoperations.FileUtils#getFileExtension(java.lang.String)}.
	 */
	@Test
	public void testGetFileExtensionString() throws Exception {
		Assert.assertEquals("java", FileUtils.getFileExtension("sample.java"));
        Assert.assertEquals("java", FileUtils.getFileExtension("path/to/sample.java"));
        Assert.assertEquals("java", FileUtils.getFileExtension("windows\\path\\to\\sample.java"));
        Assert.assertEquals("xml", FileUtils.getFileExtension("path/to/sample.xml"));
        Assert.assertEquals("", FileUtils.getFileExtension("path/to/sample"));
	}

}
