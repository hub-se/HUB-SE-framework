/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Pipe that sequences a given input List.
 * 
 * @author Simon Heiden
 *
 */
public class ListSequencerPipe<A extends List<B>,B> extends APipe<A,B> {

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(A list) {
		for (B element : list) {
			submitProcessedItem(element);
		}
		return null;
	}

}
