/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.single;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import se.de.hu_berlin.informatik.utils.compression.ziputils.ZipFileWrapper;
import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Encodes submitted integers into compressed sequences of integers, depending on the maximum
 * values of the input integers.
 * 
 * @author Simon Heiden
 */
public class BufferedIntegersToCompressedByteArrayProcessor extends AbstractProcessor<Integer,byte[] > {

	// same buffer that is used in zip utils
	private static final int BUFFER_SIZE = 4096;

	PipedOutputStream out;
	
	private ZipFileWrapper zipFile;
		
	public static final int DELIMITER = 0;
	
	private byte[] result;
	
	private byte neededBits;
	private int lastByteIndex = -1;
	private byte remainingFreeBits = 0;

	private int maxValue;

	private boolean containsZero;

	private Thread zipFileListener;
	
	public BufferedIntegersToCompressedByteArrayProcessor(
			Path zipFilePath, String fileName, boolean deleteExisting, 
			int maxValue, boolean containsZero) throws IOException {
		super();
		if (deleteExisting) {
			FileUtils.delete(zipFilePath);
		}
		
		if (zipFilePath.getParent() != null) {
			zipFilePath.getParent().toFile().mkdirs();
		}
		
		zipFile = ZipFileWrapper.getZipFileWrapper(zipFilePath);

		PipedInputStream in = new PipedInputStream();
		out = new PipedOutputStream(in);
		
		zipFileListener = startZipFileListener(fileName, in);
		zipFileListener.start();
		
		this.containsZero = containsZero;
		this.maxValue = containsZero ? maxValue+1 : maxValue;
		result = new byte[BUFFER_SIZE];
		
		//compute the number of bits needed to represent integers with the given maximum value
		neededBits = ceilLog2(this.maxValue);

		//add a header that contains information needed for decoding
		addHeader(neededBits);
	}

	private Thread startZipFileListener(String fileName, PipedInputStream in) {
		return new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int tries = 0;
					boolean worked = false;
					while (!worked) {
						++tries;
						try {
							// Creates a new entry in the zip file and adds the content to the zip file
							zipFile.addStream(in, fileName);
							worked = true;
						} catch (IOException e) {
							if (tries < 5) {
								Log.warn(this, "Attempt %d - Error adding stream to zip file '%s'.", tries, zipFile.getzipFilePath());
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e1) {
									// do nothing
								}
							} else {
								Log.abort(this, e, "Zip file '%s' does not exist.", zipFile.getzipFilePath());
							}
						}
					}
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		});
	}
	
	
	private void addHeader(byte neededBits) throws IOException {
		// header should be 1 byte:
		// | number of bits used for one element (1 byte) |
		out.write(new byte[] {neededBits});
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(Integer next) {
		int element = containsZero ? next+1 : next;
		if (element == DELIMITER) {
			closeOutputStream();
			Log.abort(this, "Cannot store numbers identical to the delimiter (%d).", DELIMITER);
		}

		storeNextInteger(element);
		
		return null;
	}
	
	/**
	 * This will add an end marker at the current position.
	 * Setting a delimiter in the middle of a sequence might
	 * result in all successing elements being ignored when
	 * the resulting byte array gets decoded again.
	 * 
	 * Do NOT use this if you don't know what you're doing!
	 */
	public void addDelimiter() {
		storeNextInteger(DELIMITER);
	}

	private void storeNextInteger(int element) {
		if (element > maxValue) {
			Log.warn(this, "Trying to store '%d', but max value set to '%d'.", element, maxValue);
			if (ceilLog2(element) > neededBits) {
				closeOutputStream();
				Log.abort(this, "Can not store '%d' in %d bits.", element, neededBits);
			}
		}
		//reset the bits left to write
		byte bitsLeft = neededBits;
		//keep only relevant bits as defined by the given maximum value
		element = keepLastNBits(element, bitsLeft);
		//add bits until all bits of the given number are processed
		while (bitsLeft > 0) {
			//add a new byte if no space is left
			if (remainingFreeBits == 0) {
				addNewByteToList();
				//remainingFreeBits > 0 holds now!
			}
			//need to shift the bits differently if more bits are left to write than free bits are remaining in the last byte of the list
			if (bitsLeft > remainingFreeBits) {
				bitsLeft -= remainingFreeBits;
				result[lastByteIndex] = (byte) (result[lastByteIndex] | (element >>> bitsLeft));
				remainingFreeBits = 0;
				//set the first bits that are processed already to 0 and keep only the last n bits
				element = keepLastNBits(element, bitsLeft);
			} else { //bitsLeft <= remainingFreeBits
				result[lastByteIndex] = (byte) (result[lastByteIndex] | (element << (remainingFreeBits - bitsLeft)));
				remainingFreeBits -= bitsLeft;
				bitsLeft = 0;
			}
		}
	}

	private void closeOutputStream() {
		if (out != null) {
			// add two delimiters to indicate the absolute end
			storeNextInteger(DELIMITER);
			storeNextInteger(DELIMITER);
			try {
				if (lastByteIndex >= 0) {
					out.write(result, 0, lastByteIndex+1);
					lastByteIndex = -1;
				}
				result = null;

				out.flush();
				out.close();
			} catch (IOException e) {
				Log.abort(this, e, "Could not write to or close output stream.");
			} finally {
				while (zipFileListener.isAlive()) {
					try {
						zipFileListener.join();
					} catch (InterruptedException e) {
					}
				}
				out = null;
			}
		}
		
		// invalidate zip file after use!
		zipFile = null;
	}

	@Override
	public byte[] getResultFromCollectedItems() {
		closeOutputStream();
		return null;
	}

	private void addNewByteToList() {
		++lastByteIndex;
		remainingFreeBits = 8;
		writeBuferToStreamIfFull();
	}

	private void writeBuferToStreamIfFull() {
		if (lastByteIndex >= BUFFER_SIZE) {
			try {
				out.write(result);
			} catch (IOException e) {
				Log.abort(this, e, "Could not write to output stream.");
			}
			lastByteIndex = 0;
			result = new byte[BUFFER_SIZE];
		}
	}
	
	private int keepLastNBits(int element, byte n) {
		return element & (int)Math.pow(2, n)-1;
	}

	private static byte ceilLog2(int n) {
	    if (n < 0) {
	    	throw new IllegalArgumentException("Can not compute for n = " + n);
	    }
	    if (n == 0) {
	    	Log.warn(BufferedIntegersToCompressedByteArrayProcessor.class, "Maximum input number is zero.");
	    	return 1;
	    } else {
	    	return (byte) (32 - Integer.numberOfLeadingZeros(n));
	    }
	}
	
	@Override
	public boolean finalShutdown() {
		closeOutputStream();
		return super.finalShutdown();
	}
	
	@Override
	protected void finalize() throws Throwable {
		closeOutputStream();
		super.finalize();
	}
}
