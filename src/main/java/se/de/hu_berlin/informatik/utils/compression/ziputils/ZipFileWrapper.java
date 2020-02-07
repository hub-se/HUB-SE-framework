package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
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
				readWriteLock.lock();
				try {
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} finally {
					readWriteLock.unlock();
				}
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
	
	private ZipOutputStream getOutputStream() {
		if (outputStream == null) {
			// new file or previously closed
			try {
				if (zipFilePath.toAbsolutePath().toFile().exists()) {
					// the zip file already exists... need to rewrite zip archive...
					try {
						File source = zipFilePath.toFile();
						File tmpZip = File.createTempFile(source.getName(), null);
						tmpZip.delete();
						if(!source.renameTo(tmpZip)) {
							throw new Exception("Could not make temp file (" + source.getName() + ")");
						}
						byte[] buffer = new byte[1024];
						ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
						ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));

						for (ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
							out.putNextEntry(ze);
							for (int read = zin.read(buffer); read > -1; read = zin.read(buffer)) {
								out.write(buffer, 0, read);
							}
							out.closeEntry();
						}

						zin.close();
						tmpZip.delete();
						outputStream = out;
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					// new zip file!
					outputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return outputStream;
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
			ZipOutputStream zos = getOutputStream();
			try {
				ZipEntry entry = new ZipEntry(fileName);
				zos.putNextEntry(entry);
				int n;
				byte[] buffer = new byte[4096];
				while (0 <= (n = in.read(buffer))) {
					zos.write(buffer, 0, n);		        	
				}
				zos.flush();
			} finally {
				zos.closeEntry();
			}
		} finally {
			readWriteLock.unlock();
		}
		
//		URI uri = URI.create("jar:" + zipFilePath.toUri());
////		if (FileSystems.getFileSystem(uri).isOpen()){ return FileSystems.getFileSystem(uri); } return FileSystems.newFileSystem(uri, env);
//		
//		FileSystem fs = null;
//		try {
//			while (fs == null || !fs.isOpen()) {
//				try {
//					fs = FileSystems.newFileSystem(uri, env);
//				} catch (FileSystemAlreadyExistsException e) {
//					if (fs != null) {
//						fs.close();
//					}
//					System.out.println("0");
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e2) {
//						// do nothing
//					}
//					
//					try {
//						
//						fs = FileSystems.getFileSystem(uri);
//					} finally {
//						while (fs != null && fs.isOpen()) {
//							try {
//								fs.close();
//							} catch (Exception e2) {
//								System.out.println("1");
//								try {
//									Thread.sleep(50);
//								} catch (InterruptedException e3) {
//									// do nothing
//								}
//							}
//						}
//					}
//					
//				}
//			}
//		    Path nf = fs.getPath(fileName);
//		    try (SeekableByteChannel channel = Files.newByteChannel(nf, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
//		        int n;
//		        // heap resident byte buffer
//		        ByteBuffer directBuf = ByteBuffer.allocate(4096);
//		        while (0 <= (n = in.read(directBuf.array()))) {
//		        	// uses the byte buffer's backing array to read from the input stream;
//		        	// this indirect use of a byte array is necessary, since we 
//		        	// need a byte buffer to write to the file channel object...
//		        	directBuf.position(0);
//		        	directBuf.limit(n);
//					channel.write(directBuf);		        	
//		        }
//		    }
//		} finally {
//			while (fs != null && fs.isOpen()) {
//				try {
//					fs.close();
//				} catch (Exception e) {
//					System.out.println("2: " + e.getMessage());
//					e.printStackTrace();
//					
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e2) {
//						// do nothing
//					}
//				}
//			}
//			
//		}
//		
////		try {
////			while (FileSystems.getFileSystem(uri).isOpen()) {
////				try {
////					Thread.sleep(50);
////				} catch (InterruptedException e) {
////					// do nothing
////				}
////			}
////		} catch (FileSystemNotFoundException e) {
////			// may happen if closed
////		}
//		return fs;
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
		readWriteLock.lock();
		try {
			closeOpenOutputStream();
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
		} finally {
			readWriteLock.unlock();
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
