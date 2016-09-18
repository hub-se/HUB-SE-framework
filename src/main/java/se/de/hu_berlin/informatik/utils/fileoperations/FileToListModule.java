/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.nio.file.Path;
import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.tm.modules.stringprocessor.StringsToListProcessor;

/**
 * A file reader module that reads a file and returns the file's contents as a list of Strings.
 * 
 * @author Simon Heiden
 */
public class FileToListModule extends AModule<Path, List<String>> {
	
	/**
	 * Creates a new {@link FileToListModule} with the given parameters.
	 */
	public FileToListModule() {
		super(true, true);
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	public List<String> processItem(Path input) {
		return new FileLineProcessorModule<List<String>>(new StringsToListProcessor(), true)
				.submit(input)
				.getResultFromCollectedItems();
	}

}
