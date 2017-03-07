/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessor<A> extends Processor<A,Object>, ConsumingProcessorUserGenerator<A> {
	
	@Override
	public void consume(A item);

	@Override
	default Object processItem(A item, Producer<Object> producer) throws UnsupportedOperationException {
		throw new IllegalStateException(this.getClass().getSimpleName() + " tries to process item with output.");
	}
	
	@Override
	default Object processItem(A item) throws UnsupportedOperationException {
		throw new IllegalStateException(this.getClass().getSimpleName() + " tries to process item with output.");
	}

	@Override
	default void setProducer(Producer<Object> producer) {
		//do nothing
	}

	@Override
	default Producer<Object> getProducer() throws IllegalStateException {
		throw new IllegalStateException(this.getClass().getSimpleName() + " tries to access a producer but should not produce output.");
	}
	
}
