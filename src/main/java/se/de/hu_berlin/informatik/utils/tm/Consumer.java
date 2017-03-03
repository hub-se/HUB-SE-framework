/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInput;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * An interface that provides basic functionalities of a consumer.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface Consumer<A> extends OptionCarrier {
	
	/**
	 * Processes an item of type {@code A}.
	 * @param item
	 * the item to be processed
	 */
	public void consume(A item);
	
//	/**
//	 * Creates a pipe object from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a pipe, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	public AbstractPipe<A,?> asPipe() throws UnsupportedOperationException;
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
//	public AbstractModule<A,?> asModule() throws UnsupportedOperationException;
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
//	public EHWithInput<A> asEH() throws UnsupportedOperationException;

}
