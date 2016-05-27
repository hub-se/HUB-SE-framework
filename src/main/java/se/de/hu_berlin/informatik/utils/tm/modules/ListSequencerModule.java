/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Module that sequences a given input List.
 * 
 * @author Simon Heiden
 */
public class ListSequencerModule<A extends List<B>,B> extends AModule<A,B> {

	/**
	 * Creates a new {@link ListSequencerModule} object.
	 */
	public ListSequencerModule() {
		super(true);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(A list) {
		if (list.size() == 0) {
			//will produce an error if submitted to a linked module
			return null;
		}
		for (int i = 0; i < list.size()-1; ++i) {
			getLinkedModule().submit(list.get(i));
		}
		return list.get(list.size()-1);
	}

}
