package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.files.processors.FileToByteArrayReader;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

public class ZipFileWrapper {
	
	final private ZipFile zipFile;
	final private String destPath;
	
	public ZipFileWrapper(final ZipFile zipFile) {
		super();
		this.zipFile = zipFile;
		destPath = zipFile.getFile().getParent() == null ? "" : zipFile.getFile().getParent();
	}
	
	public byte[] get(String fileName, boolean logError) {
		try {
			return uncheckedGet(fileName);
		} catch (ZipException e) {
			if (logError) {
				Log.err(this, "Unable to get zipped file '%s', or could not write to '%s'.", fileName, destPath);
			}
			return null;
		}
	}
	
	public byte[] uncheckedGet(String fileName) throws ZipException {
		//extract the file in the zip file to a unique file
		//may throw exception if file does not exist
		String newFileName = zipFile.getFile().getName() + "_" + fileName;
		zipFile.extractFile(fileName, destPath.toString(), null, newFileName);

		//parse the file containing the identifiers
		final Path filePath = Paths.get(destPath, newFileName);
		final byte[] result = new FileToByteArrayReader().asModule().submit(filePath).getResult();
		FileUtils.delete(filePath);
		
		return result;
	}
	
	public byte[] uncheckedGet(final int index) throws ZipException {
		final String filename = index + ".bin";
		return uncheckedGet(filename);
	}
	
	public byte[] get(final int index, boolean logError) {
		try {
			return uncheckedGet(index);
		} catch (ZipException e) {
			if (logError) {
				Log.err(this, "Unable to get zipped file '%s', or could not write to '%s'.", index + ".bin", destPath);
			}
			return null;
		}
	}
	
	public byte[] tryGetFromOneOf(String... fileNames) {
		byte[] result = null;
		for (String fileName : fileNames) { 
			result = this.get(fileName, false);
			if (result != null) {
				break;
			}
		}
		if (result == null) {
			Log.err(this, "Unable to load data from (one of) " + Misc.arrayToString(fileNames));
		}
		return result;
	}

	@Override
	public String toString() {
		return zipFile.getFile().toString();
	}

}
