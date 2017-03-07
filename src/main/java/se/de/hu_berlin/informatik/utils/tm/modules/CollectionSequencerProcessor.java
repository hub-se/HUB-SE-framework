/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.util.Collection;
import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.tm.Producer;

/**
 * Module that sequences a given input List.
 * 
 * @author Simon Heiden
 */
public class CollectionSequencerProcessor<B> extends AbstractProcessor<Collection<B>,B> {

	@Override
	public B processItem(Collection<B> list, Producer<B> producer) {
		if (list.size() == 0) {
			//will produce an error if submitted to a linked module
			return null;
		}
		for (B item : list) {
			producer.produce(item);
		}
		return null;
	}

}
