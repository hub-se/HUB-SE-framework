/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.File;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Simple module that converts {@link Path} to {@link File} objects.
 * 
 * @author Simon Heiden
 */
public class PathToFileConverter extends AbstractProcessor<Path,File> {
	
	public PathToFileConverter() {
		super();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public File processItem(Path path) {
		return path.toFile();
	}

}
