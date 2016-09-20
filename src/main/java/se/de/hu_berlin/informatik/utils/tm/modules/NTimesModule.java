/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Module that submits the input item n times to a linked module.
 * 
 * @author Simon Heiden
 */
public class NTimesModule extends AModule<Object,Object> {

	private int n;
	
	/**
	 * Creates a new {@link NTimesModule} object with the given parameter.
	 * @param n
	 * the number of times that the item should be submitted to the linked module
	 */
	public NTimesModule(int n) {
		super(true);
		this.n = n;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Object processItem(Object item) {
		for (int i = 0; i < n-1; ++i) {
			getLinkedModule().submit(item);
		}
		return item;
	}

}
