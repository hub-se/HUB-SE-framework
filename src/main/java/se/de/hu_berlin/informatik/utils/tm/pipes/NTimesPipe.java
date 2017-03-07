/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.tm.Producer;

/**
 * Pipe that outputs every input item n times.
 * 
 * @author Simon Heiden
 *
 */
public class NTimesPipe extends AbstractProcessor<Object,Object> {

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

	@Override
	public Object processItem(Object item, Producer<Object> producer) {
		for (int i = 0; i < n-1; ++i) {
			producer.produce(item);
		}
		return item;
	}

}
