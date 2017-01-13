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
public class ListSequencerPipe<B> extends AbstractPipe<List<B>,B> {

	public ListSequencerPipe() {
		super(true);
	}

	@Override
	public B processItem(List<B> list) {
		for (B element : list) {
			submitProcessedItem(element);
		}
		return null;
	}

}
