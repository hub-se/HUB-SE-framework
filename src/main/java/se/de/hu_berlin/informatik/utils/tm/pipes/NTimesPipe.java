/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Pipe that outputs every input item n times.
 * 
 * @author Simon Heiden
 *
 */
public class NTimesPipe extends APipe<Object,Object> {

	private int n;
	
	/**
	 * Creates a new {@link NTimesPipe} object with the given parameter.
	 * @param n
	 * the number of times that the item should be submitted to the linked pipe
	 */
	public NTimesPipe(int n) {
		super();
		this.n = n;
	}

	/**
	 * Creates a new {@link NTimesPipe} object with the given parameters.
	 * @param n
	 * the number of times that the item should be submitted to the linked pipe
	 * @param pipeSize
	 * the size of the output pipe
	 */
	public NTimesPipe(int n, int pipeSize) {
		super(pipeSize);
		this.n = n;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Object processItem(Object item) {
		for (int i = 0; i < n-1; ++i) {
			submitProcessedItem(item);
		}
		return item;
	}

}
