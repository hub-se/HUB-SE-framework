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
public interface ConsumingProcessorUser<A> extends ProcessorUser<A,Object>, ConsumingProcessorUserGenerator<A> {

	@Override
	default public void produce(Object item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Producing items not possible for " + this.getClass().getSimpleName() + ".");
	}

	@Override
	default public <C> ProcessorUser<C, ?> linkTo(ProcessorUser<C, ?> consumer)
			throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not possible for " + this.getClass().getSimpleName() + ".");
	}
	
}
