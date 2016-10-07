/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

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
	
	/**
	 * Processes a single item of type A and returns a boolean value.
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * true if successful, false otherwise
	 */
	abstract public boolean processInput(A input);

	@Override
	public void processEvent(A input) throws Exception {
		resetAndInit();
		processInput(input);
	}
	
	/**
	 * Should be used to reset or to initialize fields. Gets called before processing each event.
	 */
	abstract public void resetAndInit(); 
}
