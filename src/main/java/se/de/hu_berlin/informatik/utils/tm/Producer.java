/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * An interface that provides basic functionalities of a producer.
 * 
 * @author Simon Heiden
 *
 * @param <B>
 * is the type of the output object
 */
public interface Producer<B> extends OptionCarrier {
	
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
	 */
	public <C> Consumer<C> linkTo(Consumer<C> consumer) throws IllegalArgumentException, IllegalStateException;
	
//	/**
//	 * Creates a pipe object from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a pipe, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	public AbstractPipe<?,B> asPipe() throws UnsupportedOperationException;
//	
//	/**
//	 * Creates a module object from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a module, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	public AbstractModule<?,B> asModule() throws UnsupportedOperationException;
//	
//	/**
//	 * Creates an event handler from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a new event handler, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	public EHWithInputAndReturn<?,B> asEH() throws UnsupportedOperationException;

}
