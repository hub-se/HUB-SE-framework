/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;

/**
 * Encodes sequences of integers into compressed sequences of integers, depending on the maximum
 * values of the input integers.
 * 
 * @author Simon Heiden
 */
public class IntSequenceToCompressedByteArrayModule extends AbstractModule<List<Integer>,byte[] > {

	private List<Byte> result;
	
	private byte neededBits;
	private int sequenceLength;
	private int lastByteIndex = 0;
	private byte remainingFreeBits = 0;
	private byte bitsLeft = 0;
	private int totalSequences = 0;
	
	public IntSequenceToCompressedByteArrayModule(int maxValue, int sequenceLength) {
		//if this module needs an input item
		super(true);
		result = new ArrayList<>();
		
		//compute the number of bits needed to represent integers with the given maximum value
		neededBits = ceilLog2(maxValue);

		this.sequenceLength = sequenceLength;
		//add a header that contains information needed for decoding
		addHeader(neededBits, sequenceLength);
	}
	
	
	private void addHeader(byte neededBits, int sequenceLength) {
		// header should be 9 bytes:
		// | number of bits used for one element (1 byte) | sequence length (4 bytes) | total number of sequences (4 bytes) |
		
		result.add(neededBits);
		
		ByteBuffer b = ByteBuffer.allocate(4);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putInt(sequenceLength);

		for (int i = 0; i < 4; ++i) {
			result.add(b.array()[i]);
		}
		
		//stores the number of sequences in the end (gets replaced)
		for (int i = 0; i < 4; ++i) {
			result.add((byte) 0);
		}
		lastByteIndex = 8;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public byte[] processItem(List<Integer> intSequence) {
		if (intSequence.size() != sequenceLength) {
			Log.abort(this, "given sequence is of length %d, but should be %d.", intSequence.size(), sequenceLength);
		}
		++totalSequences;
		for (Integer element : intSequence) {
			//reset the bits left to write
			bitsLeft = neededBits;
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

		return null;
	}

	@Override
	public byte[] getResultFromCollectedItems() {
		ByteBuffer b = ByteBuffer.allocate(4);
		//b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		b.putInt(totalSequences);

		//set the total number of sequences stored
		for (int i = 0; i < 4; ++i) {
			result.set(i+5, b.array()[i]);
		}
		
		byte[] temp = new byte[result.size()];
		for (int i = 0; i < temp.length; ++i) {
			temp[i] = result.get(i);
		}
		return temp;
	}

	private void addNewByteToList() {
		result.add((byte) 0);
		++lastByteIndex;
		remainingFreeBits = 8;
	}
	
	private int keepLastNBits(int element, byte n) {
		return element & (int)Math.pow(2, n)-1;
	}

	private static byte ceilLog2(int n) {
	    if(n <= 0) throw new IllegalArgumentException();
	    return (byte) (32 - Integer.numberOfLeadingZeros(n));
	}
}
