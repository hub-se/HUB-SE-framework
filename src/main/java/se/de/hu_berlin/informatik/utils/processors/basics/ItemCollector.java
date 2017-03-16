/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Pipe that collects items that pass through in a List.
 * The items may be retrieved with a public method. When items
 * are collected, the pipe's collection will be resetted.
 * 
 * @author Simon Heiden
 *
 */
public class ItemCollector<A> extends AbstractProcessor<A,A> {
	
	final private List<A> collectedItems;
	
	public ItemCollector() {
		super();
		collectedItems = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public A processItem(A item) {
		synchronized (collectedItems) {
			collectedItems.add(item);
		}
		
		return item;
	}

	public List<A> getCollectedItems() {
		List<A> temp;
		synchronized (collectedItems) {
			temp = new ArrayList<>(collectedItems);
			collectedItems.clear();
		}
		return temp;
	}

}
