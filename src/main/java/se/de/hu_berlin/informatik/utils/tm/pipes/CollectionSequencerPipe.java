/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.util.Collection;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * Pipe that sequences a given input Collection.
 * 
 * @author Simon Heiden
 *
 */
public class CollectionSequencerPipe<B> extends AbstractPipe<Collection<B>,B> {

	public CollectionSequencerPipe() {
		super(true);
	}

	@Override
	public B processItem(Collection<B> list) {
		for (B element : list) {
			submitProcessedItem(element);
		}
		return null;
	}

}
