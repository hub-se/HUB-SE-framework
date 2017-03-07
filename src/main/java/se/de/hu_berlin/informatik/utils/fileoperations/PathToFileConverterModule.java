/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.File;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;

/**
 * Simple module that converts {@link Path} to {@link File} objects.
 * 
 * @author Simon Heiden
 */
public class PathToFileConverterModule extends AbstractProcessor<Path,File> {
	
	public PathToFileConverterModule() {
		super();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public File processItem(Path path) {
		return path.toFile();
	}

}
