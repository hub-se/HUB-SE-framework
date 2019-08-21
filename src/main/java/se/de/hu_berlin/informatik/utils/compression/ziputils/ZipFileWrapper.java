package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

public class ZipFileWrapper {
	
	final private Path zipFilePath;
	Map<String, String> env;
	
	public ZipFileWrapper(final Path zipFilePath) {
		super();
		this.zipFilePath = zipFilePath;
		env = new HashMap<>(); 
		env.put("create", "true");
	}
	
	public byte[] get(String fileName, boolean logError) {
		try (ZipFile zipFile = new ZipFile(zipFilePath.toString())) {
			if (zipFile.getEntry(fileName) == null) {
				if (logError) {
					Log.err(this, "Unable to get zipped file '%s'.", fileName);
				}
				return null;
			}
		} catch (IOException e) {
			if (logError) {
				Log.err(this, "Unable to get zipped file '%s'", fileName);
			}
			return null;
		}
		
		try {
			return uncheckedGet(fileName);
		} catch (ZipException e) {
			if (logError) {
				Log.err(this, e, "Error in zipped file '%s'", fileName);
			}
			return null;
		}
	}
	
	public boolean exists(String fileName) throws ZipException {
		try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
			ZipEntry entry = zipFile.getEntry(fileName);
			return entry != null;
		} catch (IOException e) {
			throw new ZipException("Reading file '" + fileName + "' failed!");
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
		
		try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
			ZipEntry entry = zipFile.getEntry(fileName);
			if (entry == null) {
				throw new ZipException("File '" + fileName + "' does not exist in zip file'" + zipFilePath.toString() + "'!");
			}
			return getBytesFromInputStream(zipFile.getInputStream(entry));
		} catch (IOException e) {
			throw new ZipException("Reading input stream from file '" + fileName + "' failed!");
		}
	}

	public byte[] uncheckedGet(ZipEntry fileHeader) throws ZipException {
		try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
			return getBytesFromInputStream(zipFile.getInputStream(fileHeader));
		} catch (IOException e) {
			throw new ZipException("Reading input stream from file '" + fileHeader.getName() + "' failed!");
		}
	}

//	public ZipFile getOrCreateZipFile() {
//		ZipFile zipFile = null;
//		try {
//			zipFile = new ZipFile(zipFilePath.toString());
////			if (!zipFile.isValidZipFile()) {
////				Log.abort(this, "File '%s' is no valid zip file.", zipFilePath);
////			}
//		} catch (IOException e) {
//			Log.abort(this, "Could not initialize zip file '%s'.", zipFilePath);
//		}
//		return zipFile;
//	}
	
//	public ZipOutputStream getOutputStream() {
//		try {
//			return new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public void addArray(byte[] array, String fileName) {
		try (InputStream in = new ByteArrayInputStream(array)) {
			addStream(in, fileName);
		} catch (IOException e) {
			Log.abort(this, e, "Could not create input stream from byte array.");
		}
	}
	
	public void addStream(InputStream in, String fileName) throws IOException {
		try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zipFilePath.toUri()), env)) {
		    Path nf = fs.getPath(fileName);
		    try (SeekableByteChannel channel = Files.newByteChannel(nf, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
		        int n;
		        // heap resident byte buffer
		        ByteBuffer directBuf = ByteBuffer.allocate(4096);
		        while (0 <= (n = in.read(directBuf.array()))) {
		        	// uses the byte buffer's backing array to read from the input stream;
		        	// this indirect use of a byte array is necessary, since we 
		        	// need a byte buffer to write to the file channel object...
		        	directBuf.position(0);
		        	directBuf.limit(n);
					channel.write(directBuf);		        	
		        }
		    }
		}
	}
	
//	public InputStream uncheckedGetAsStream(ZipEntry fileHeader) throws IOException {
//		ZipFile zipFile = null;
//		try {
//			zipFile = new ZipFile(zipFilePath.toString());
//			return zipFile.getInputStream(fileHeader);
//		} catch (IOException e) {
//			throw new ZipException("Reading input stream from file '" + fileHeader.getName() + "' failed!");
//		} finally {
//			if (zipFile != null) {
//				zipFile.close();
//			}
//		}
//	}
//	
//	public InputStream uncheckedGetAsStream(String filename) throws IOException {
//		ZipFile zipFile = null;
//		try {
//			zipFile = new ZipFile(zipFilePath.toString());
//			return zipFile.getInputStream(zipFile.getEntry(filename));
//		} catch (IOException e) {
//			throw new ZipException("Reading input stream from file '" + filename + "' failed!");
//		} finally {
//			if (zipFile != null) {
//				zipFile.close();
//			}
//		}
//	}
	
	public List<String> getFileHeadersContainingString(String pattern) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath.toString());
			List<String> matchingHeaders = new ArrayList<>();
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getName()));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (zipEntry.getName().contains(pattern)) {
					matchingHeaders.add(zipEntry.getName());
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();

			// sort the files alphabetically
			Collections.sort(matchingHeaders, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			
			return matchingHeaders;
		} catch (IOException e) {
			throw new ZipException("Getting zip file contents containing pattern '" + pattern + "' failed!");
		} finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}

	private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			byte[] buffer = new byte[0xFFFF];
			for (int len = inputStream.read(buffer); len != -1; len = inputStream.read(buffer)) { 
				os.write(buffer, 0, len);
			}
			return os.toByteArray();
		} finally {
			if (inputStream != null) {
				inputStream.close();
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
		try (ZipFile zipFile = new ZipFile(zipFilePath.toString());) {
			return zipFile.getName();
		} catch (IOException e) {
			return "IOException calling ZipFileWrapper.toString()...";
		}
	}

//	public ZipInputStream getInputStream(String fileName) {
//		try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
//			ZipEntry entry = zipFile.getEntry(fileName);
//			zipFile.getInputStream(entry);
//			return entry != null;
//		} catch (IOException e) {
//			throw new ZipException("Reading input stream from file '" + fileName + "' failed!");
//		}
//	}

}
