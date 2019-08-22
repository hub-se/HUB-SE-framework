/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.nio.file.Path;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class AddByteArrayToZipFileProcessor extends AbstractProcessor<byte[],byte[]> {

	private int fileCounter = -1;
	private ZipFileWrapper zipFile;
	
	public AddByteArrayToZipFileProcessor(Path zipFilePath, boolean deleteExisting) {
		//if this module needs an input item
		super();
		if (deleteExisting) {
			FileUtils.delete(zipFilePath);
		}
		
		if (zipFilePath.getParent() != null) {
			zipFilePath.getParent().toFile().mkdirs();
		}
		
		this.zipFile = ZipFileWrapper.getZipFileWrapper(zipFilePath);
	}
	
	public AddByteArrayToZipFileProcessor(Path zipFilePath) {
		this(zipFilePath, false);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(byte[] array) {
		// this sets the name of the file for this entry in the zip file, starting from '0.bin'
		// and creates a new entry in the zip file and adds the content to the zip file
		zipFile.addArray(array, ++fileCounter + ".bin");
		return array;
	}

}
