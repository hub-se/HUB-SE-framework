/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression;

import java.nio.ByteBuffer;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Decodes...
 * 
 * @author Simon Heiden
 */
public class CompressedByteArrayToByteArrayModule extends AModule<byte[],byte[]> {
	
	private byte usedBits;
	private int sequenceLength;
	private int totalSequences;
	private int arrayPos;
	
	public CompressedByteArrayToByteArrayModule() {
		//if this module needs an input item
		super(true, true);
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(byte[] array) {
		readHeader(array);
		byte currentByte = 0;
		int currentInt = 0;
		byte remainingBits = 0;
		byte bitsLeft = 0;
		
		byte[] result = new byte[sequenceLength * totalSequences];
		int bytePos = -1;
		
		//get all the encoded integers
		while (bytePos+1 < result.length) {
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
			
			//add the next integer to the result list
			result[++bytePos] = (byte) currentInt;
			
			//reset the current integer to all zeroes
			currentInt = 0;
			
			//if no bits remain in the current byte, then update the array position for the next step
			if (remainingBits == 0) {
				++arrayPos;
			}
		}
		
		return result;
	}

	private void readHeader(byte[] array) {
		// header should be 9 bytes:
		// | number of bits used for one element (1 byte) | sequence length (4 bytes) | total number of sequences (4 bytes) |
		
		usedBits = array[0];
		if (usedBits > 8) {
			Log.abort(this, "More bits than 8 are being used. Can not store the numbers in a byte.");
		}
		
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
