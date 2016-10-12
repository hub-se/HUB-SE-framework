/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * Pipe that sequences a given input List.
 * 
 * @author Simon Heiden
 *
 */
public class ListSequencerPipe<A extends List<B>,B> extends AbstractPipe<A,B> {

	public ListSequencerPipe() {
		super(true);
	}

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
