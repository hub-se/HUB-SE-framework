/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * Pipe that collects items that pass through in a List.
 * The items may be retrieved with a public method. When items
 * are collected, the pipe's collection will be resetted.
 * 
 * @author Simon Heiden
 *
 */
public class ElementCollectorPipe<A> extends AbstractPipe<A,A> {
	
	final private List<A> collectedItems;
	
	public ElementCollectorPipe() {
		super(true);
		collectedItems = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
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
