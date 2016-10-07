/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Callable;

/**
 * An abstract class that implements the {@link Callable} interface and
 * is enriched with a field for input objects. The user has to
 * make sure that input object is set before use.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * 
 * @see Callable
 */
public abstract class CallableEHWithInput<A> implements Callable<Boolean> {

	/**
	 * The input object.
	 */
	private A input = null;
	
	private final EHWithInput<A> eventHandler;
	
	public A getInput() {
		return input;
	}
	
	public CallableEHWithInput(EHWithInput<A> eventHandler) {
		super();
		this.eventHandler = eventHandler;
	}
	
	/**
	 * Processes a single item of type A and returns a boolean value.
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * true if successful, false otherwise
	 */
	abstract public boolean processInput(A input);
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		eventHandler.resetAndInit();
		return eventHandler.processInput(input);
	}
	
	/**
	 * @param input
	 * an input object
	 * @return
	 * this object to enable chaining
	 */
	public CallableEHWithInput<A> setInput(A input) {
		this.input = input;
		return this;
	}
	
	/**
	 * Should be used to reset or to initialize fields. Gets called before processing each event.
	 */
	abstract public void resetAndInit(); 
}
