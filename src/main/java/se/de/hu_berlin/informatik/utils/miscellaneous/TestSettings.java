/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.nio.file.Paths;

import se.de.hu_berlin.informatik.utils.files.FileUtils;

/**
 * Helper Class that defines various standard directories and methods
 * to be used by JUnit test cases. Just extend this class to be able to
 * use the included static variables and methods.
 * 
 * @author SimHigh
 */
public abstract class TestSettings {

	private static String stdResourcesDir = Paths.get("src", "test", "resources").toAbsolutePath().toString();
	private static String stdTestDir = Paths.get("target", "testoutput").toAbsolutePath().toString();
	
	/**
	 * Gets the standard test resource directory.
	 * @return
	 * the standard test resource directory as a String
	 */
	public static String getStdResourcesDir() {
		return stdResourcesDir;
	}
	
	/**
	 * Sets the standard test resource directory.
	 * @param stdResourcesDir
	 * the standard test resource directory as a String
	 */
	public static void setStdResourcesDir(String stdResourcesDir) {
		TestSettings.stdResourcesDir = stdResourcesDir;
	}
	
	/**
	 * Gets the standard test output directory.
	 * @return
	 * the standard test output directory as a String
	 */
	public static String getStdTestDir() {
		return stdTestDir;
	}
	
	/**
	 * Sets the standard test output directory.
	 * @param stdTestDir
	 * the standard test output directory as a String
	 */
	public static void setStdTestDir(String stdTestDir) {
		TestSettings.stdTestDir = stdTestDir;
	}
	
	/**
	 * Deletes the complete test output directory.
	 */
	public static void deleteTestOutputs() {
		FileUtils.delete(Paths.get(getStdTestDir()));
	}
}
