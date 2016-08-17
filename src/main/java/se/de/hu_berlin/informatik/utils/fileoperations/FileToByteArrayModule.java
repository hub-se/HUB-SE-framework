/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * A file reader module that reads a file and returns a byte array with the file's contents.
 * 
 * @author Simon Heiden
 */
public class FileToByteArrayModule extends AModule<Path, byte[]> {
	
	/**
	 * Creates a new {@link FileToByteArrayModule} with the given parameters.
	 */
	public FileToByteArrayModule() {
		super(true);
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.ITransmitter#processItem(java.lang.Object)
	 */
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
