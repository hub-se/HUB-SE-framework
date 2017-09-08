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
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Pair;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class AddNamedByteArrayToZipFileProcessor extends AbstractProcessor<Pair<String, byte[]>, byte[]> {

	private ZipFile zipFile;
	private ZipParameters parameters;
	
	public AddNamedByteArrayToZipFileProcessor(Path zipFilePath, boolean deleteExisting) {
		//if this module needs an input item
		super();
		if (deleteExisting) {
			FileUtils.delete(zipFilePath);
		}
		
		if (zipFilePath.getParent() != null) {
			zipFilePath.getParent().toFile().mkdirs();
		}
		
		try {
			zipFile = new ZipFile(zipFilePath.toString());
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
	
	public AddNamedByteArrayToZipFileProcessor(Path zipFilePath) {
		this(zipFilePath, false);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(Pair<String, byte[]> arrayWithFileName) {
		try {
			// this sets the name of the file for this entry in the zip file, starting from '0.bin'
			parameters.setFileNameInZip(arrayWithFileName.first());

			InputStream is = new ByteArrayInputStream(arrayWithFileName.second());

			// Creates a new entry in the zip file and adds the content to the zip file
			zipFile.addStream(is, parameters);
		} catch (ZipException e) {
			Log.abort(this, e, "Zip file '%s' does not exist.", zipFile.getFile());
		}
		return arrayWithFileName.second();
	}

}
