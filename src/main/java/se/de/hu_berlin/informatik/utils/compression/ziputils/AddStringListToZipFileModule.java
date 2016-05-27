/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import se.de.hu_berlin.informatik.utils.fileoperations.StringListToFileWriterModule;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Adds lists of char sequences (strings) to a zip file.
 * 
 * @author Simon Heiden
 * 
 * @param A
 * the type of iterable char sequence 
 */
public class AddStringListToZipFileModule<A extends Iterable<? extends CharSequence>> extends AModule<A,A> {

	private ZipFile zipFile;
	private ZipParameters parameters;
	
	public AddStringListToZipFileModule(Path zipFilePath, boolean deleteExisting) {
		//if this module needs an input item
		super(true);
		if (deleteExisting) {
			Misc.delete(zipFilePath);
		}
		try {
			zipFile = new ZipFile(zipFilePath.toString());
		} catch (ZipException e) {
			Misc.abort(this, e, "Could not initialize zip file '%s'.", zipFilePath);
		}
		
		parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
	}
	
	public AddStringListToZipFileModule(Path zipFilePath) {
		this(zipFilePath, false);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public A processItem(A list) {
		Path temp = null;
		try {
			// creates a temporary file
			//File f = File.createTempFile("tmp", ".txt", new File(zipFile.getFile().getParent() == null ? "" : zipFile.getFile().getParent()));
			// this sets the name of the file for this entry in the zip file
//			parameters.setFileNameInZip(zipFile.getFileHeaders().size() + ".txt");
			
			temp = Paths.get(zipFile.getFileHeaders().size() + ".txt");
			// save the given data to the temporary file (not perfect, but well...)
			new StringListToFileWriterModule<List<String>>(temp, true)
			.submitAndStart(list);

			// Creates a new entry in the zip file and adds the content to the zip file
			zipFile.addFile(temp.toFile(), parameters);
		} catch (ZipException e) {
			Misc.abort(this, e, "Zip file '%s' does not exist.", zipFile.getFile());
		} finally {
			Misc.delete(temp);
		}
		return null;
	}

}
