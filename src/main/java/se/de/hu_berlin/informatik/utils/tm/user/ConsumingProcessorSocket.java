/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.user;

import java.util.function.Consumer;

import se.de.hu_berlin.informatik.utils.tm.ConsumingProcessor;

/**
 * An interface that provides basic functionalities of consumers that can
 * hold a {@link ConsumingProcessor}, such that an input
 * item of type {@code A} can be submitted to the consumer and gets processed
 * by the inserted {@link ConsumingProcessor}.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input objects
 */
public interface ConsumingProcessorSocket<A> extends ProcessorSocket<A,Object>, ConsumingProcessorSocketGenerator<A> {

	/**
	 * Throws an exception, as it should not be used in this context.
	 * @param item
	 * the processed item
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default public void produce(Object item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Producing items not possible for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Throws an exception, as it should not be used in this context.
	 * @param <C>
	 * the input type of the consumer to be linked to
	 * @param socket
	 * the ProcessorSocket to be linked to
	 * @return
	 * the socket to be linked to, for chaining
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default public <C> ProcessorSocket<C, ?> linkTo(ProcessorSocket<C, ?> socket) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not possible for " + this.getClass().getSimpleName() + ".");
	}
	
	/**
	 * Throws an exception, as it should not be used in this context.
	 * @param <C>
	 * the input type of the Consumer to be linked to
	 * @param consumer
	 * the Consumer to be linked to
	 * @return
	 * the Consumer to be linked to
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default <C> Consumer<C> linkTo(Consumer<C> consumer) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not possible for " + this.getClass().getSimpleName() + ".");
	}
	
}
