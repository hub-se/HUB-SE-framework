/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;

/**
 * Pipe that collects items from input lists until a minimum number
 * of items is collected. The last chunk of items may produce a list
 * that has less than the given number of items. It is returned 
 * nonetheless.
 * 
 * @author Simon Heiden
 *
 */
public class ListCollectorPipe<A> extends AbstractProcessor<List<A>,List<A>> {
	
	final private int minEntries;
	private List<A> collectedItems;
	
	public ListCollectorPipe(int minEntries) {
		super();
		this.minEntries = minEntries;
		collectedItems = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public List<A> processItem(List<A> list) {
		collectedItems.addAll(list);
		if (collectedItems.size() < minEntries) {
			//do nothing
			return null;
		}
		
		List<A> temp = collectedItems;
		collectedItems = new ArrayList<>();
		return temp;
	}

	@Override
	public List<A> getResultFromCollectedItems() {
		// get the list that contains the last few collected items, if any
		if (collectedItems.size() > 0) {
			return collectedItems;
		} else {
			return null;
		}
	}

}
