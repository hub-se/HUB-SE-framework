/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Decodes...
 * 
 * @author Simon Heiden
 */
public class CompressedByteArrayToIntArraysProcessor extends AbstractProcessor<byte[],List<int[]>> {
	
	public static final int DELIMITER = 0;
	
	private byte usedBits;
	private int sequenceLength;
	private int totalSequences;
	private int arrayPos;

	private boolean containsZero;
	
	public CompressedByteArrayToIntArraysProcessor(boolean containsZero) {
		super();
		this.containsZero = containsZero;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public List<int[]> processItem(byte[] array) {
		readHeader(array);
		byte currentByte = 0;
		int currentInt = 0;
		byte remainingBits = 0;
		byte bitsLeft = 0;
		List<Integer> currentSequence = null;
		
		List<int[]> result = new ArrayList<>();
		int intCounter = 0;
		int sequenceCounter = 0;
		
		//get all the encoded integers
		while (arrayPos < array.length) {
			//for each number, the number of bits to get is equal
			bitsLeft = usedBits;
			//if no bits remain to get from the current byte, then get the next one from the array
			if (remainingBits == 0) {
				currentByte = array[arrayPos];
				remainingBits = 8;
			}
			
			//if intCounter is zero, then we are at the start of a new sequence
			if (intCounter == 0) {
				if (++sequenceCounter > totalSequences) {
					break;
				}
				if (currentSequence != null) {
					result.add(currentSequence.stream().mapToInt(i -> i).toArray());
				}
				currentSequence = new ArrayList<>();
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
			
			if (sequenceLength == 0) {
				if (currentInt == DELIMITER) {
					//reset the counter (start of new sequence)
					intCounter = 0;
				} else {
					//add the next integer to the current sequence
					currentSequence.add(containsZero ? currentInt-1 : currentInt);
					++intCounter;
				}
			} else {
				//add the next integer to the current sequence
				currentSequence.add(currentInt);
				++intCounter;
				//if the sequence ends here, reset the counter
				if (intCounter >= sequenceLength) {
					intCounter = 0;
				}
			}
			//reset the current integer to all zeroes
			currentInt = 0;
			
			//if no bits remain in the current byte, then update the array position for the next step
			if (remainingBits == 0) {
				++arrayPos;
			}
		}
		
		if (currentSequence != null) {
			result.add(currentSequence.stream().mapToInt(i -> i).toArray());
		}
		
		return result;
	}

	private void readHeader(byte[] array) {
		// header should be 9 bytes:
		// | number of bits used for one element (1 byte) | sequence length (4 bytes) - 0 for delimiter mode | total number of sequences (4 bytes) |
		
		usedBits = array[0];
		
		byte[] smallArray = { array[1], array[2], array[3], array[4] };
		ByteBuffer b = ByteBuffer.wrap(smallArray);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		sequenceLength = b.getInt();
		
		byte[] smallArray2 = { array[5], array[6], array[7], array[8] };
		b = ByteBuffer.wrap(smallArray2);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		totalSequences = b.getInt();
		
		arrayPos = 9;
	}
	
	
}
