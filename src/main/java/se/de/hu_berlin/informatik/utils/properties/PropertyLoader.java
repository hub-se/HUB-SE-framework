package se.de.hu_berlin.informatik.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

public class PropertyLoader {

	/**
	 * Loads properties from the property file.
	 * @param propertyFile
	 * the property file
	 * @param properties
	 * an enum containing the required properties
	 * @return
	 * a Properties object containing all loaded properties
	 */
	public static <T extends Enum<T> & PropertyTemplate> Properties loadProperties(File propertyFile, final Class<T> properties) {
//		File homeDir = new File(System.getProperty("user.home"));

		Properties props = new Properties();

		if (propertyFile.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(propertyFile);
				props.load(fis);
			} catch (FileNotFoundException e) {
				Log.abort(PropertyLoader.class, "No property file found: '" + propertyFile + "'.");
			} catch (IOException e) {
				Log.abort(PropertyLoader.class, "IOException while reading property file: '" + propertyFile + "'.");
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (IOException e) {
					// nothing to do
				}
			}
		} else {
			Log.abort(PropertyLoader.class, "No property file exists: '" + propertyFile + "'.");
		}
		
		for (final T property : EnumSet.allOf(properties)) {
			property.setPropertyValue(props.getProperty(property.getPropertyIdentifier(), null));
		}
		
		return props;
	}
	
//	public static void storeProperties(Properties props) {
//	// write the updated properties file to the file system
//	FileOutputStream fos = null;
//	File propertyFile = new File(PROP_FILE_NAME);
//	
//	try {
//		fos = new FileOutputStream(propertyFile);
//		props.store(fos, "property file for Defects4J benchmark experiments");
//	} catch (FileNotFoundException e) {
//		e.printStackTrace();
//	} catch (IOException e) {
//		e.printStackTrace();
//	} finally {
//		if (fos != null) {
//			try {
//				fos.close();
//			} catch (IOException e) {
//				// nothing to do
//			}
//		}
//	}
//}
	
}
