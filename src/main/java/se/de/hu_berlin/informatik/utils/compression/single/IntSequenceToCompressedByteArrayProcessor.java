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
 * Encodes a sequence of integers into a compressed byte array, depending on the maximum
 * values of the input integers.
 * 
 * @author Simon Heiden
 */
public class IntSequenceToCompressedByteArrayProcessor extends AbstractProcessor<List<Integer>,byte[] > {
	
	private int addHeader(byte neededBits, int sequenceLength, List<Byte> result) {
		// header should be 5 bytes:
		// | number of bits used for one element (1 byte) | sequence length (4 bytes) |
		
		result.add(neededBits);
		
		ByteBuffer b = ByteBuffer.allocate(4);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putInt(sequenceLength);

		for (int i = 0; i < 4; ++i) {
			result.add(b.array()[i]);
		}
		
		return 4;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(List<Integer> intSequence) {
		int sequenceLength = intSequence.size();
		int maxValue = 0;
		for (int item : intSequence) {
			if (item < 0) {
				Log.err(this, "Can not store negative item '%d'.", item);
			}
			maxValue = Math.max(maxValue, item);
		}
		
		//compute the number of bits needed to represent integers with the given maximum value
		byte neededBits = ceilLog2(maxValue);
		
		List<Byte> result = new ArrayList<>(sequenceLength + 5);
		//add a header that contains information needed for decoding
		int lastByteIndex = addHeader(neededBits, sequenceLength, result);

		byte remainingFreeBits = 0;
		byte bitsLeft = 0;
		for (Integer element : intSequence) {
			if (element > maxValue) {
				Log.abort(this, "Trying to store '%d', but max value set to '%d'.", element.intValue(), maxValue);
			}
			//reset the bits left to write
			bitsLeft = neededBits;
			//keep only relevant bits as defined by the given maximum value
			element = keepLastNBits(element, bitsLeft);
			//add bits until all bits of the given number are processed
			while (bitsLeft > 0) {
				//add a new byte if no space is left
				if (remainingFreeBits == 0) {
					result.add((byte) 0);
					++lastByteIndex;
					remainingFreeBits = 8;
					//remainingFreeBits > 0 holds now!
				}
				//need to shift the bits differently if more bits are left to write than free bits are remaining in the last byte of the list
				if (bitsLeft > remainingFreeBits) {
					bitsLeft -= remainingFreeBits;
					result.set(lastByteIndex, (byte) (result.get(lastByteIndex) | (element >>> bitsLeft)) );
					remainingFreeBits = 0;
					//set the first bits that are processed already to 0 and keep only the last n bits
					element = keepLastNBits(element, bitsLeft);
				} else { //bitsLeft <= remainingFreeBits
					result.set(lastByteIndex, (byte) (result.get(lastByteIndex) | (element << (remainingFreeBits - bitsLeft))) );
					remainingFreeBits -= bitsLeft;
					bitsLeft = 0;
				}
			}
		}
		
		byte[] temp = new byte[result.size()];
		for (int i = 0; i < temp.length; ++i) {
			temp[i] = result.get(i);
		}
		return temp;
	}
	
	private int keepLastNBits(int element, byte n) {
		return element & (int)Math.pow(2, n)-1;
	}

	private static byte ceilLog2(int n) {
	    if (n < 0) {
	    	throw new IllegalArgumentException("Can not compute for n = " + n);
	    }
	    if (n == 0) {
	    	Log.warn(IntSequenceToCompressedByteArrayProcessor.class, "Maximum input number is zero.");
	    	return 1;
	    } else {
	    	return (byte) (32 - Integer.numberOfLeadingZeros(n));
	    }
	}
}
