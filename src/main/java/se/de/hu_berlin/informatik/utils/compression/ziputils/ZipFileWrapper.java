package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

public class ZipFileWrapper {
	
	final private Path zipFilePath;
	private ZipOutputStream outputStream;
	private ReentrantLock readWriteLock = new ReentrantLock();
	
//	Map<String, String> env;
	
	final private static Map<String,ZipFileWrapper> zipFileCache = new ConcurrentHashMap<>();
	
	private ZipFileWrapper(final Path zipFilePath) {
		super();
		this.zipFilePath = zipFilePath;
//		env = new HashMap<>(); 
//		env.put("create", "true");
		addShutDownHook();
	}
	
	public static synchronized ZipFileWrapper getZipFileWrapper(final Path zipFilePath) {
		return zipFileCache.computeIfAbsent(zipFilePath.toAbsolutePath().toString(), k -> {
			return new ZipFileWrapper(zipFilePath);
		});
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				closeOpenOutputStream();
//				for (Entry<String, ZipOutputStream> entry : openOutputStreams.entrySet()) {
//					if (entry.getValue() != null) {
//						try {
//							entry.getValue().close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
			}
		});
	}

	public byte[] get(String fileName, boolean logError) {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
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
		} finally {
			readWriteLock.unlock();
		}
	}
	
	public boolean exists(String fileName) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				ZipEntry entry = zipFile.getEntry(fileName);
				return entry != null;
			} catch (IOException e) {
				throw new ZipException("Reading file '" + fileName + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
		}
	}
	
	public long getEntrySize(String fileName) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				ZipEntry entry = zipFile.getEntry(fileName);
				return entry.getSize();
			} catch (IOException e) {
				throw new ZipException("Reading file '" + fileName + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
		}
	}
	
	public Path getzipFilePath() {
		return zipFilePath.toAbsolutePath();
	}
	
	public byte[] uncheckedGet(String fileName) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				ZipEntry entry = zipFile.getEntry(fileName);
				if (entry == null) {
					throw new ZipException("File '" + fileName + "' does not exist in zip file'" + zipFilePath.toString() + "'!");
				}
				return getBytesFromInputStream(zipFile.getInputStream(entry));
			} catch (IOException e) {
				throw new ZipException("Reading input stream from file '" + fileName + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
		}
	}

	public byte[] uncheckedGet(ZipEntry fileHeader) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				return getBytesFromInputStream(zipFile.getInputStream(fileHeader));
			} catch (IOException e) {
				throw new ZipException("Reading input stream from file '" + fileHeader.getName() + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
		}
	}
	
	public byte[] uncheckedGet(String fileName, long start, int byteCount) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				ZipEntry entry = zipFile.getEntry(fileName);
				if (entry == null) {
					throw new ZipException("File '" + fileName + "' does not exist in zip file'" + zipFilePath.toString() + "'!");
				}
				return getBytesFromInputStream(zipFile.getInputStream(entry), start, byteCount);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ZipException("Reading input stream from file '" + this.zipFilePath + "/" + fileName + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
		}
	}

	public byte[] uncheckedGet(ZipEntry fileHeader, long start, int byteCount) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				return getBytesFromInputStream(zipFile.getInputStream(fileHeader), start, byteCount);
			} catch (IOException e) {
				throw new ZipException("Reading input stream from file '" + fileHeader.getName() + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
		}
	}
	
	public List<byte[]> uncheckedGet(String fileName, List<Integer> chunkLengths) throws ZipException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			try (ZipFile zipFile = new ZipFile(zipFilePath.toString()) ){
				ZipEntry entry = zipFile.getEntry(fileName);
				if (entry == null) {
					throw new ZipException("File '" + fileName + "' does not exist in zip file'" + zipFilePath.toString() + "'!");
				}
				return getBytesFromInputStream(zipFile.getInputStream(entry), chunkLengths);
			} catch (IOException e) {
				throw new ZipException("Reading input stream from file '" + fileName + "' failed!");
			}
		} finally {
			readWriteLock.unlock();
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
	

	public void close() {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
		} finally {
			readWriteLock.unlock();
		}
	}
	
	private void closeOpenOutputStream() {
		if (outputStream != null) {
			System.out.println("Closed output stream for zip file '" + zipFilePath + "'.");
			try {
				outputStream.close();
				outputStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private ZipOutputStream getOutputStream(Collection<String> excludeFiles) {
		if (excludeFiles != null) {
			closeOpenOutputStream();
		}
		if (outputStream == null) {
			// new file or previously closed
			try {
				if (zipFilePath.toAbsolutePath().toFile().exists()) {
					// the zip file already exists... need to rewrite zip archive...
					try {
						File source = zipFilePath.toFile();
						File tmpZip = File.createTempFile(source.getName(), null);
						tmpZip.delete();
						Files.move(zipFilePath, tmpZip.toPath());
						byte[] buffer = new byte[4096];
						ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
						ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));

						if (excludeFiles == null) {
							for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
								out.putNextEntry(ze);
								int read;
								while ((read = zin.read(buffer)) > 0) {
									out.write(buffer, 0, read);
								}
								out.closeEntry();
							}
						} else {
							for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
								if (excludeFiles.contains(ze.getName())) {
									// skip entries to exclude
//									System.err.println("removing " + ze.getName());
									continue;
								}
								out.putNextEntry(ze);
								int read;
								while ((read = zin.read(buffer)) > 0) {
									out.write(buffer, 0, read);
								}
								out.closeEntry();
							}
						}

						zin.close();
						tmpZip.delete();
						outputStream = out;
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					// new zip file!
					zipFilePath.getParent().toFile().mkdirs();
					outputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return outputStream;
	}
	
	public void removeEntries(Collection<String> files) {
		getOutputStream(files);
	}
	
	public void addArray(byte[] array, String fileName) {
		try (InputStream in = new ByteArrayInputStream(array)) {
			addStream(in, fileName);
		} catch (IOException e) {
			Log.abort(this, e, "Could not create input stream from byte array.");
		}
	}
	
	public void addStream(InputStream in, String fileName) throws IOException {
		readWriteLock.lock();
		try {
			ZipOutputStream zos = getOutputStream(null);
			try {
				ZipEntry entry = new ZipEntry(fileName);
				zos.putNextEntry(entry);
				int n;
				byte[] buffer = new byte[4096];
				while ((n = in.read(buffer)) > 0) {
//					System.err.print(Arrays.toString(buffer));
					zos.write(buffer, 0, n);		        	
				}
				zos.flush();
			} catch (ZipException e) {
				// trying to add duplicate entry? -> remove the existing one
				zos = getOutputStream(Collections.singletonList(fileName));
				ZipEntry entry = new ZipEntry(fileName);
				zos.putNextEntry(entry);
				int n;
				byte[] buffer = new byte[4096];
				while ((n = in.read(buffer)) > 0) {
//					System.err.print(Arrays.toString(buffer));
					zos.write(buffer, 0, n);		        	
				}
				zos.flush();
			} finally {
				in.close();
				zos.closeEntry();
			}
		} finally {
			readWriteLock.unlock();
		}
	}
	
	public List<String> getFileHeadersContainingString(String pattern) throws IOException {
		return getFileHeadersSatisfyingCheck(k -> k.contains(pattern));
	}
	
	public List<String> getFileHeadersStartingWithString(String pattern) throws IOException {
		return getFileHeadersSatisfyingCheck(k -> k.startsWith(pattern));
	}
	
	public List<String> getFileHeadersSatisfyingCheck(Predicate<String> check) throws IOException {
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(zipFilePath.toString());
				List<String> matchingHeaders = new ArrayList<>();
				ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.getName()));
				ZipEntry zipEntry;
				while ((zipEntry = zis.getNextEntry()) != null) {
//					System.err.println(zipEntry.getName());
					if (check.test(zipEntry.getName())) {
						matchingHeaders.add(zipEntry.getName());
					}
					zis.closeEntry();
				}
				zis.close();

//				// sort the files alphabetically
//				Collections.sort(matchingHeaders, new Comparator<String>() {
//
//					@Override
//					public int compare(String o1, String o2) {
//						return o1.compareTo(o2);
//					}
//				});

				return matchingHeaders;
			} catch (IOException e) {
				e.printStackTrace();
				throw new ZipException("Getting zip file contents failed: " + zipFilePath);
			} finally {
				if (zipFile != null) {
					zipFile.close();
				}
			}
		} finally {
			readWriteLock.unlock();
		}
	}

	private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		try {
			byte[] buffer = new byte[4096];
			int len;
			while ((len = inputStream.read(buffer)) > 0) { 
				os.write(buffer, 0, len);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return os.toByteArray();
	}
	
	private static byte[] getBytesFromInputStream(InputStream inputStream, long start, int byteCount) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		try {
			long skip = inputStream.skip(start);
			if (skip != start) {
				return null;
			}
			byte[] buffer = new byte[4096];
			int len;
			while ((len = inputStream.read(buffer)) > 0) { 
				if (len < byteCount) {
					os.write(buffer, 0, len);
					byteCount -= len;
				} else {
					os.write(buffer, 0, byteCount);
					byteCount = 0;
					break;
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		byte[] byteArray = os.toByteArray();
//		System.err.println(byteArray.length + ", " + Arrays.toString(byteArray));
		return byteArray;
	}
	
	private List<byte[]> getBytesFromInputStream(InputStream inputStream, List<Integer> chunkLengths) throws IOException {
		List<byte[]> chunks = new ArrayList<>(chunkLengths.size());
		
		int bytesToRead = 0;
		Iterator<Integer> iterator = chunkLengths.iterator();
		if (iterator.hasNext()) {
			bytesToRead = iterator.next();
		} else {
			return Collections.emptyList();
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		try {
			byte[] buffer = new byte[4096];
			int len;
			while ((len = inputStream.read(buffer)) > 0) {
				int processed = 0;
				while (bytesToRead <= len - processed) {
					// enough bytes left to read in the current buffer
					os.write(buffer, processed, bytesToRead);
					processed += bytesToRead;
					// add the next chunk to the output list and reset the output stream
					chunks.add(os.toByteArray());
					os.reset();
					// get next chunk length
					if (iterator.hasNext()) {
						bytesToRead = iterator.next();
					} else {
						if (len - processed > 0) {
							Log.warn(this, "Didn't process entire file entry!");
						}
						return chunks;
					}
				}
				if (len - processed > 0) {
					// process the remaining input buffer;
					// bytesToRead is greater than remaining buffer size
					os.write(buffer, processed, len - processed);
					bytesToRead -= len - processed;
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		if (iterator.hasNext()) {
			Log.err(this, "Not all chunks could be retrieved (file entry too short)!");
		}
//		System.err.println(byteArray.length + ", " + Arrays.toString(byteArray));
		return chunks;
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
		return zipFilePath.toAbsolutePath().toString();
//		try (ZipFile zipFile = new ZipFile(zipFilePath.toString());) {
//			return zipFile.getName();
//		} catch (IOException e) {
//			return "IOException calling ZipFileWrapper.toString()...";
//		}
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
