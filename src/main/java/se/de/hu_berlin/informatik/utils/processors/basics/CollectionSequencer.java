/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.basics;

import java.util.Collection;

import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;
import se.de.hu_berlin.informatik.utils.processors.Producer;

/**
 * Module that sequences a given input List.
 * 
 * @author Simon Heiden
 */
public class CollectionSequencer<B> extends AbstractProcessor<Collection<B>,B> {

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
