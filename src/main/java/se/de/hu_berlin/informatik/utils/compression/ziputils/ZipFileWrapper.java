package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.files.processors.FileToByteArrayReader;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

public class ZipFileWrapper {
	
	final private ZipFile zipFile;
	final private String destPath;
	
	public ZipFileWrapper(final ZipFile zipFile) {
		super();
		this.zipFile = zipFile;
		destPath = zipFile.getFile().getParent() == null ? "" : zipFile.getFile().getParent();
	}
	
	
	public byte[] uncheckedGet(final int index) throws ZipException {
		//extract the zip file contents to the zip file's parent folder
		final String filename = index + ".bin";
		
		//may throw exception if file does not exist
		zipFile.extractFile(filename, destPath);

		//parse the file containing the identifiers
		final Path filePath = Paths.get(destPath, filename);
		final byte[] result = new FileToByteArrayReader().asModule().submit(filePath).getResult();
		FileUtils.delete(filePath);
		
		return result;
	}
	
	public byte[] get(final int index) {
		try {
			return uncheckedGet(index);
		} catch (ZipException e) {
			Log.abort(this, e, "Unable to get zipped file '%s', or could not write to '%s'.", index + ".bin", destPath);
		}
		return new byte[0];
	}

	@Override
	public String toString() {
		return zipFile.getFile().toString();
	}

}
