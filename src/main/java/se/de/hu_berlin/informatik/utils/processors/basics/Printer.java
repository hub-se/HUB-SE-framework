/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Printer module that simply prints a processed item to the system console.
 * 
 * @author Simon Heiden
 */
public class Printer<A> extends AbstractProcessor<A, A> {

	/**
	 * Creates a new {@link Printer} object.
	 */
	public Printer() {
		super();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public A processItem(A item) {
		Log.out(this, item.toString());
		return item;
	}

}
