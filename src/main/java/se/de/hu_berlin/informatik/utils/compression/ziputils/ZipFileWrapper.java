package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

public class ZipFileWrapper {
	
	final private Path zipFilePath;
	
	public ZipFileWrapper(final Path zipFilePath) {
		super();
		this.zipFilePath = zipFilePath;
	}
	
	public byte[] get(String fileName, boolean logError) {
		ZipFile zipFile = getZipFile();
		try {
			if (zipFile.getFileHeader(fileName) == null) {
				if (logError) {
					Log.err(this, "Unable to get zipped file '%s'.", fileName);
				}
				return null;
			}
			return uncheckedGet(fileName);
		} catch (ZipException e) {
			if (logError) {
				Log.err(this, "Unable to get zipped file '%s'", fileName);
			}
			return null;
		}
	}
	
	public boolean exists(String fileName) {
		ZipFile zipFile = getZipFile();
		try {
			return zipFile.getFileHeader(fileName) != null;
		} catch (ZipException e) {
			Log.err(this, "Unable to get zipped file '%s'", fileName);
			return false;
		}
	}
	
	public Path getzipFilePath() {
		return zipFilePath.toAbsolutePath();
	}
	
	public byte[] uncheckedGet(String fileName) throws ZipException {
//		//extract the file in the zip file to a unique file
//		//may throw exception if file does not exist
//		String newFileName = zipFile.getFile().getName() + "_" + fileName;
//		zipFile.extractFile(fileName, destPath.toString(), null, newFileName);
//
//		//parse the file containing the identifiers
//		final Path filePath = Paths.get(destPath, newFileName);
//		final byte[] result = new FileToByteArrayReader().asModule().submit(filePath).getResult();
//		FileUtils.delete(filePath);
//		
//		return result;
		
		ZipFile zipFile = getZipFile();
		return uncheckedGet(zipFile.getFileHeader(fileName));
	}

	public byte[] uncheckedGet(FileHeader fileHeader) throws ZipException {
		ZipFile zipFile = getZipFile();
		try {
			return getBytesFromInputStream(zipFile.getInputStream(fileHeader));
		} catch (IOException e) {
			throw new ZipException("Reading input stream from file '" + fileHeader.getFileName() + "' failed!");
		}
	}

	private ZipFile getZipFile() {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath.toString());
			if (!zipFile.isValidZipFile()) {
				Log.abort(this, "File '%s' is no valid zip file.", zipFilePath);
			}
		} catch (ZipException e) {
			Log.abort(this, "Could not initialize zip file '%s' for reading.", zipFilePath);
		}
		return zipFile;
	}
	
	public ZipInputStream uncheckedGetAsStream(FileHeader fileHeader) throws ZipException {
		ZipFile zipFile = getZipFile();
		try {
			return zipFile.getInputStream(fileHeader);
		} catch (ZipException e) {
			throw new ZipException("Reading input stream from file '" + fileHeader.getFileName() + "' failed!");
		}
	}
	
	public ZipInputStream uncheckedGetAsStream(String filename) throws ZipException {
		ZipFile zipFile = getZipFile();
		try {
			return zipFile.getInputStream(zipFile.getFileHeader(filename));
		} catch (ZipException e) {
			throw new ZipException("Reading input stream from file '" + filename + "' failed!");
		}
	}
	
	public List<FileHeader> getFileHeadersContainingString(String pattern) throws ZipException {
		ZipFile zipFile = getZipFile();
		List<FileHeader> matchingHeaders = new ArrayList<>();
		for (Object headerO : zipFile.getFileHeaders()) {
			if (headerO != null) {
				FileHeader header = (FileHeader) headerO;
				if (header.getFileName().contains(pattern)) {
					matchingHeaders.add(header);
				}
			}
		}
		
		// sort the files alphabetically
		Collections.sort(matchingHeaders, new Comparator<FileHeader>() {

			@Override
			public int compare(FileHeader o1, FileHeader o2) {
				return o1.getFileName().compareTo(o2.getFileName());
			}
		});
		
		return matchingHeaders;
	}

	private static byte[] getBytesFromInputStream(ZipInputStream is) throws IOException {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			byte[] buffer = new byte[0xFFFF];
			for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
				os.write(buffer, 0, len);
			}
			return os.toByteArray();
		} finally {
			if (is != null) {
				is.close();
			}
		}
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
				Log.err(this, "Unable to get zipped file '%s'.", index + ".bin");
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
		ZipFile zipFile = getZipFile();
		return zipFile.getFile().toString();
	}

}
