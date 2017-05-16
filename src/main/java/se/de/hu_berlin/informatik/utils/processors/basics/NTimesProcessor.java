/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;

/**
 * Pipe that outputs every input item n times.
 * 
 * @author Simon Heiden
 *
 */
public class NTimesProcessor extends AbstractProcessor<Object,Object> {

	private int n;
	
	/**
	 * Creates a new {@link NTimesProcessor} object with the given parameter.
	 * @param n
	 * the number of times that the item should be submitted to the linked pipe
	 */
	public NTimesProcessor(int n) {
		super();
		this.n = n;
	}

	@Override
	public Object processItem(Object item, ProcessorSocket<Object, Object> socket) {
		for (int i = 0; i < n-1; ++i) {
			socket.produce(item);
		}
		return item;
	}

}
