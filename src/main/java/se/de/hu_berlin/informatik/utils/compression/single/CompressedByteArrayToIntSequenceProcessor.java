/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.single;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Decodes...
 * 
 * @author Simon Heiden
 */
public class CompressedByteArrayToIntSequenceProcessor extends AbstractProcessor<byte[],List<Integer>> {
	
	private byte usedBits;
	private int sequenceLength;
	private int arrayPos;
	
	public CompressedByteArrayToIntSequenceProcessor() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public List<Integer> processItem(byte[] array) {
		readHeader(array);
		byte currentByte = 0;
		int currentInt = 0;
		byte remainingBits = 0;
		byte bitsLeft = 0;

		List<Integer> result = new ArrayList<Integer>(sequenceLength);
		int intCounter = 0;
		
		if (sequenceLength == 0) {
			return result;
		}

		//get all the encoded integers
		while (arrayPos < array.length) {
			//for each number, the number of bits to get is equal
			bitsLeft = usedBits;
			//if no bits remain to get from the current byte, then get the next one from the array
			if (remainingBits == 0) {
				currentByte = array[arrayPos];
				remainingBits = 8;
			}
			
			//as long as bits are still needed, get them from the array
			while (bitsLeft > 0) {
				if (bitsLeft > remainingBits) {
					currentInt = (currentInt << remainingBits) | (currentByte & 0xFF ) >>> (8 - remainingBits);
					bitsLeft -= remainingBits;
//					remainingBits = 0;
					currentByte = array[++arrayPos];
					remainingBits = 8;
				} else { //bitsLeft <= remainingBits
					currentInt = (currentInt << bitsLeft) | (currentByte & 0xFF ) >>> (8 - bitsLeft);
					currentByte = (byte) (currentByte << bitsLeft);
					remainingBits -= bitsLeft;
					bitsLeft = 0;
				}
			}

			//add the next integer to the current sequence
			result.add(currentInt);
			++intCounter;
			//if the sequence ends here, reset the counter
			if (intCounter >= sequenceLength) {
				return result;
			}

			//reset the current integer to all zeroes
			currentInt = 0;
			
			//if no bits remain in the current byte, then update the array position for the next step
			if (remainingBits == 0) {
				++arrayPos;
			}
		}
		
		// could not get full sequence...
		Log.err(this, "Unable to get full integer sequence from byte array (too short).");
		return null;
	}

	private void readHeader(byte[] array) {
		// header should be 5 bytes:
		// | number of bits used for one element (1 byte) | sequence length (4 bytes) |
		
		usedBits = array[0];
		
		byte[] smallArray = { array[1], array[2], array[3], array[4] };
		ByteBuffer b = ByteBuffer.wrap(smallArray);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		sequenceLength = b.getInt();
		
		arrayPos = 5;
	}
	
	
}
