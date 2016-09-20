/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import se.de.hu_berlin.informatik.utils.fileoperations.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class AddByteArrayToZipFileModule extends AModule<byte[],byte[]> {

	private ZipFile zipFile;
	private ZipParameters parameters;
	private int fileCounter = -1;
	
	public AddByteArrayToZipFileModule(Path zipFilePath, boolean deleteExisting) {
		//if this module needs an input item
		super(true);
		if (deleteExisting) {
			FileUtils.delete(zipFilePath);
		}
		
		if (zipFilePath.getParent() != null) {
			zipFilePath.getParent().toFile().mkdirs();
		}
		
		try {
			zipFile = new ZipFile(zipFilePath.toString());
			if (zipFile.getFile().exists()) {
				fileCounter = zipFile.getFileHeaders().size()-1;
			}
		} catch (ZipException e) {
			Log.abort(this, e, "Could not initialize zip file '%s'.", zipFilePath);
		}
		
		parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
		
		// we set this flag to true. If this flag is true, Zip4j identifies that
		// the data will not be from a file but directly from a stream
		parameters.setSourceExternalStream(true);
	}
	
	public AddByteArrayToZipFileModule(Path zipFilePath) {
		this(zipFilePath, false);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public byte[] processItem(byte[] array) {
		try {
			// this sets the name of the file for this entry in the zip file, starting from '0.bin'
			parameters.setFileNameInZip(++fileCounter + ".bin");

			InputStream is = new ByteArrayInputStream(array);

			// Creates a new entry in the zip file and adds the content to the zip file
			zipFile.addStream(is, parameters);
		} catch (ZipException e) {
			Log.abort(this, e, "Zip file '%s' does not exist.", zipFile.getFile());
		}
		return array;
	}

}
