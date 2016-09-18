/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Callable;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * An abstract class that implements the {@link Callable} interface and
 * is enriched with fields for input and output paths. The user has to
 * make sure that input and/or output paths are set before use if needed.
 * 
 * @author Simon Heiden
 * 
 * 
 * @see Callable
 */
public abstract class CallableWithReturn<A,B> extends DisruptorEventHandler<A>  implements Callable<Boolean> {

	/**
	 * The input object.
	 */
	private A input = null;
	
	APipe<?, B> pipe = null;
	
	/**
	 * Creates a new {@link CallableWithReturn} object with the given path.
	 * @param input
	 * an input path
	 */
	public CallableWithReturn(A input) {
		super();
		this.input = input;
	}
	
	/**
	 * Creates a new {@link CallableWithReturn} object with no paths set.
	 */
	public CallableWithReturn() {
		super();
	}
	
	/**
	 * @param input
	 * an input path
	 */
	public void setInput(A input) {
		this.input = input;
	}

	/**
	 * @return 
	 * the input path
	 */
	public A getInput() {
		return input;
	}

	public void setPipe(APipe<?, B> pipe) {
		this.pipe = pipe;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		pipe.submitProcessedItem(processInput(input));
		return true;
	}

	abstract public B processInput(A input);

	@Override
	public void processEvent(A input) throws Exception {
		resetAndInit();
		this.input = input;
		call();
	}
	
	/**
	 * Should be used to reset or to initialize fields. Gets called before processing each event.
	 */
	abstract public void resetAndInit(); 
	
}
