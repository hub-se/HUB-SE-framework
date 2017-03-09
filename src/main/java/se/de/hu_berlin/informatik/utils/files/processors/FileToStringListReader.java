/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.nio.file.Path;
import java.util.List;

import se.de.hu_berlin.informatik.utils.files.processors.stringprocessor.StringsToListProcessor;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * A file reader module that reads a file and returns the file's contents as a list of Strings.
 * 
 * @author Simon Heiden
 */
public class FileToStringListReader extends AbstractProcessor<Path, List<String>> {
	
	/**
	 * Creates a new {@link FileToStringListReader} with the given parameters.
	 */
	public FileToStringListReader() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	public List<String> processItem(Path input) {
		return new FileLineProcessor<List<String>>(new StringsToListProcessor(), true)
				.submit(input)
				.getResult();
	}

}
