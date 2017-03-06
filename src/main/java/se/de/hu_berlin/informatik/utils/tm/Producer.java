/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUser;

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
	 * Links a matching consumer to the output of this producer.
	 * @param <C>
	 * the input type of the consumer to be linked to
	 * @param consumer
	 * the consumer to be linked to
	 * @return
	 * the consumer to be linked to
	 * @throws IllegalArgumentException
	 * if the input type C of the given consumer does not match the output type B of this producer
	 * @throws IllegalStateException
	 * if the components can't be linked due to other reasons
	 * @throws UnsupportedOperationException
	 * if linking is not implemented
	 */
	default <C> ProcessorUser<C,?> linkTo(ProcessorUser<C,?> consumer) throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
		throw new UnsupportedOperationException("Linking not implemented for " + this.getClass().getSimpleName() + ".");
	}

}
