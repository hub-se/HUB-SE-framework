/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.user;

/**
 * An interface that provides basic functionalities of a consumer.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessorSocket<A> extends ProcessorSocket<A,Object>, ConsumingProcessorSocketGenerator<A> {

	@Override
	default public void produce(Object item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Producing items not possible for " + this.getClass().getSimpleName() + ".");
	}

	@Override
	default public <C> ProcessorSocket<C, ?> linkTo(ProcessorSocket<C, ?> consumer)
			throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not possible for " + this.getClass().getSimpleName() + ".");
	}
	
}
