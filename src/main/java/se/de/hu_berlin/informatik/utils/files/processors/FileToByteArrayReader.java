/**
 * 
 */
package se.de.hu_berlin.informatik.utils.files.processors;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * A file reader module that reads a file and returns a byte array with the file's contents.
 * 
 * @author Simon Heiden
 */
public class FileToByteArrayReader extends AbstractProcessor<Path, byte[]> {
	
	/**
	 * Creates a new {@link FileToByteArrayReader} with the given parameters.
	 */
	public FileToByteArrayReader() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(Path input) {
		File inputFile = input.toFile();
		byte[] data = new byte[(int) inputFile.length()];
		try (FileInputStream fis = new FileInputStream(inputFile)) {
			fis.read(data, 0, data.length);
		} catch (Exception e) {
			Log.abort(this, e, "Can not read file '%s'", input);
		}
		return data;
	}

}
