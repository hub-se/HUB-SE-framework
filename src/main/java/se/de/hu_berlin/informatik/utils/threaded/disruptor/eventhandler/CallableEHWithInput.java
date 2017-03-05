/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

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
	
	private final DisruptorFCFSEventHandler<A> eventHandler;
	
	public CallableEHWithInput(DisruptorFCFSEventHandler<A> eventHandler) {
		super();
		this.eventHandler = eventHandler;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		eventHandler.resetAndInit();
		eventHandler.consume(input);
		return true;
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
	
}
