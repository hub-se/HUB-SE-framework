/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import java.util.function.Consumer;

import se.de.hu_berlin.informatik.utils.tm.user.ProcessorSocket;

/**
 * An interface that provides basic functionalities of a producer.
 * 
 * @author Simon Heiden
 *
 * @param <B>
 * is the type of the output object
 */
public interface Producer<B> {
	
	/**
	 * Produces the given item of type {@code B}.
	 * @param item
	 * the processed item
	 */
	public void produce(B item);
	
	/**
	 * Links a matching {@link ProcessorSocket} to the output of this producer.
	 * @param <C>
	 * the input type of the consumer to be linked to
	 * @param socket
	 * the ProcessorSocket to be linked to
	 * @return
	 * the socket to be linked to, for chaining
	 * @throws IllegalArgumentException
	 * if the input type C of the given socket does not match the output type B of this Producer
	 * @throws IllegalStateException
	 * if the components can't be linked due to other reasons
	 * @throws UnsupportedOperationException
	 * if linking is not implemented
	 */
	default <C> ProcessorSocket<C,?> linkTo(ProcessorSocket<C,?> socket) throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not implemented for " + this.getClass().getSimpleName() + ".");
	}
	
	/**
	 * Links a matching {@link Consumer} to the output of this producer.
	 * @param <C>
	 * the input type of the Consumer to be linked to
	 * @param consumer
	 * the Consumer to be linked to
	 * @return
	 * the Consumer to be linked to
	 * @throws IllegalArgumentException
	 * if the input type C of the given Consumer does not match the output type B of this Producer
	 * @throws IllegalStateException
	 * if the components can't be linked due to other reasons
	 * @throws UnsupportedOperationException
	 * if linking is not implemented
	 */
	default <C> Consumer<C> linkTo(Consumer<C> consumer) throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not implemented for " + this.getClass().getSimpleName() + ".");
	}

}
