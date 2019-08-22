/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.nio.file.Path;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Pair;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class AddNamedByteArrayToZipFileProcessor extends AbstractProcessor<Pair<String, byte[]>, byte[]> {

	private ZipFileWrapper zipFile;
	
	public AddNamedByteArrayToZipFileProcessor(Path zipFilePath, boolean deleteExisting) {
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
	
	public AddNamedByteArrayToZipFileProcessor(Path zipFilePath) {
		this(zipFilePath, false);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(Pair<String, byte[]> arrayWithFileName) {
		// this sets the name of the file for this entry in the zip file
		// and creates a new entry in the zip file and adds the content to the zip file
		zipFile.addArray(arrayWithFileName.second(), arrayWithFileName.first());
		return arrayWithFileName.second();
	}

}
