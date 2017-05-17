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
	
	private int skip;
	private int max;

	public FileToStringListReader() {
		this(0, 0);
	}
	
	public FileToStringListReader(int skip) {
		this(skip, 0);
	}
	
	public FileToStringListReader(int skip, int max) {
		super();
		this.skip = skip;
		this.max = max;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public List<String> processItem(Path input) {
		return new FileLineProcessor<List<String>>(new StringsToListProcessor(), true)
				.skipFirstLines(skip)
				.readMaxLines(max)
				.submit(input)
				.getResult();
	}

}
