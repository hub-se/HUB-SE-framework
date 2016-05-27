package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import se.de.hu_berlin.informatik.utils.fileoperations.FileToByteArrayModule;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

public class ZipFileWrapper {
	
	private ZipFile zipFile;
	private String destPath;
	
	public ZipFileWrapper(ZipFile zipFile) {
		super();
		this.zipFile = zipFile;
		destPath = zipFile.getFile().getParent() == null ? "" : zipFile.getFile().getParent();
	}
	
	
	public byte[] get(int index) {
		//extract the zip file contents to the zip file's parent folder
		String filename = index + ".bin";
		
		try {
			zipFile.extractFile(filename, destPath);
		} catch (ZipException e) {
			Misc.abort((Object)null, e, "Unable to get zipped file '%s', or could not write to '%s'.", filename, destPath);
		}

		//parse the file containing the identifiers
		Path filePath = Paths.get(destPath, filename);
		byte[] result = new FileToByteArrayModule().submitAndStart(filePath).getResult();
		Misc.delete(filePath);
		
		return result;
	}

}
