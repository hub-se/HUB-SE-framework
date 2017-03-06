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

}
