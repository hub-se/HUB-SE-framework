package se.de.hu_berlin.informatik.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import se.de.hu_berlin.informatik.utils.fileoperations.ListToFileWriterModule;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.UserCommunicationUtils;

public class PropertyLoader {

	/**
	 * Loads properties from the property file.
	 * @param propertyFile
	 * the property file
	 * @param properties
	 * an enum containing the required properties
	 * @param <T>
	 * an Enum type that represents properties
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
			Log.err(PropertyLoader.class, "No property file exists: '" + propertyFile + "'.");
			if (UserCommunicationUtils.askUser("Generate a template configuration file in this location?")) {
				PropertyLoader.saveTemplateFile(properties, propertyFile.toPath());
			} else {
				Log.abort(PropertyLoader.class, "No configuration file created.");
			}
		}
		
		for (final T property : EnumSet.allOf(properties)) {
			String value = props.getProperty(property.getPropertyIdentifier(), null);
			if (value == null || value.equals("")) {
				Log.abort(PropertyLoader.class, "Property '" + property.getPropertyIdentifier() 
				+ "' not set in configuration file: '" + propertyFile + "'.");
			}
			if (value.equals(property.getPlaceHolder())) {
				Log.abort(PropertyLoader.class, "Property '" + property.getPropertyIdentifier() 
				+ "' is equal to the template place holder.");
			}
			property.setPropertyValue(value);
		}
		
		return props;
	}
	
	public static <T extends Enum<T> & PropertyTemplate> void saveTemplateFile(final Class<T> properties, Path output) {
		List<String> lines = new ArrayList<>();
		
		lines.add("Property file for " + properties.getName() + ". Template creation date: " + new Date());
		for (final T property : EnumSet.allOf(properties)) {
			lines.add("");
			for (String description : property.getHelpfulDescription()) {
				lines.add("# " + description);
			}
			lines.add(property.getPropertyIdentifier() + "=" + property.getPlaceHolder());
		}
		
		new ListToFileWriterModule<List<String>>(output, false)
		.submit(lines);
	}
	
}
