/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import java.util.concurrent.Callable;

/**
 * An abstract class that provides a simple API for a disruptor event handler
 * that processes a single input object at a time and produces no output.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * 
 * @see Callable
 */
public abstract class EHWithInput<A> extends DisruptorFCFSEventHandler<A> {
	
	@Override
	public void processEvent(A input) throws Exception {
		processInput(input);
	}

	/**
	 * Processes a single item of type A.
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * true if successfull, false otherwise
	 */
	public abstract boolean processInput(A input);
	
}
