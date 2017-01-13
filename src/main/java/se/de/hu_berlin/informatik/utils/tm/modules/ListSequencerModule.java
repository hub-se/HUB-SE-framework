/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.List;

import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;

/**
 * Module that sequences a given input List.
 * 
 * @author Simon Heiden
 */
public class ListSequencerModule<B> extends AbstractModule<List<B>,B> {

	/**
	 * Creates a new {@link ListSequencerModule} object.
	 */
	public ListSequencerModule() {
		super(true);
	}

	@Override
	public B processItem(List<B> list) {
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
