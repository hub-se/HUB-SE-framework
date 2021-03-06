/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.XFileWrapper;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * A file writer module that gets an {@link XFileWrapper} and writes it to the hard 
 * drive with the associated output path. The given input is returned as it is
 * to the output in the end in case it has to be further processed.
 * 
 * @author Simon Heiden
 */
public class XFileWriter extends AbstractProcessor<XFileWrapper<Iterable<? extends CharSequence>>, XFileWrapper<Iterable<? extends CharSequence>>> {
	
	private boolean overwrite = false;
	/**
	 * Creates a new {@link XFileWriter} with the given parameters.
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 */
	public XFileWriter(boolean overwrite) {
		super();
		this.overwrite = overwrite;
	}
	
	/**
	 * Creates a new {@link XFileWriter} that overwrites existing files by default.
	 */
	public XFileWriter() {
		this(true);
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public XFileWrapper<Iterable<? extends CharSequence>> processItem(XFileWrapper<Iterable<? extends CharSequence>> item) {
		if (item.getOutputPath().toFile().isDirectory()) {
			Log.abort(this, "Path \"%s\" is a directory and should be a file.", item.getOutputPath().toString());
		}
		if (!overwrite && item.getOutputPath().toFile().exists()) {
			Log.abort(this, "File \"%s\" exists.", item.getOutputPath().toString());
		}
		try {
			Files.write(item.getOutputPath(), item.getLinesToWrite(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			Log.abort(this, e, "Cannot write file \"" + item.getOutputPath().toString() + "\".");
		}
		return item;
	}

}
