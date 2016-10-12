/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;

/**
 * Printer module that simply prints a processed item to the system console.
 * 
 * @author Simon Heiden
 */
public class PrinterModule<A> extends AbstractModule<A, A> {

	/**
	 * Creates a new {@link PrinterModule} object.
	 */
	public PrinterModule() {
		super(true);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public A processItem(A item) {
		Log.out(this, item.toString());
		return item;
	}

}
