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
		if (list.size() == 0) {
			//will produce an error message
			return null;
		}
		for (int i = 0; i < list.size()-1; ++i) {
			submitProcessedItem(list.get(i));
		}
		return list.get(list.size()-1);
	}

}
