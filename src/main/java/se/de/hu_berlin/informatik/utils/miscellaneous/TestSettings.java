/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;
import java.nio.file.Paths;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * Helper Class that defines various standard directories and methods
 * to be used by JUnit test cases. Just extend this class to be able to
 * use the included static variables and methods.
 * 
 * @author SimHigh
 */
public abstract class TestSettings {

	private static String stdResourcesDir = "src" + File.separator + "test" + File.separator + "resources";
	private static String stdTestDir = "target" + File.separator + "testoutput";
	
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
		Misc.delete(Paths.get(getStdTestDir()));
	}
}
