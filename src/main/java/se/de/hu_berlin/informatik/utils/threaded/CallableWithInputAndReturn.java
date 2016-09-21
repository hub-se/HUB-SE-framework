/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Callable;

/**
 * An abstract class that implements the {@link Callable} and the 
 * {@link IMultiplexerInput} interface. The user has to
 * make sure that an input item is set before trying to process it.
 * 
 * @author Simon Heiden
 * 
 * @see Callable
 */
public abstract class CallableWithInputAndReturn<A,B> extends DisruptorFCFSEventHandler<A> implements Callable<Boolean>, IMultiplexerInput<B> {

	/**
	 * The input object.
	 */
	private A input = null;
	
	/**
	 * The output object.
	 */
	private B output = null;
	
	private boolean hasNewOutput = false;
	private final Object lock = new Object();
	private IMultiplexer<B> multiplexer = null;

	/**
	 * Creates a new {@link CallableWithInputAndReturn} object with the given path.
	 * @param input
	 * an input path
	 */
	public CallableWithInputAndReturn(A input) {
		super();
		this.input = input;
	}
	
	/**
	 * Creates a new {@link CallableWithInputAndReturn} object with no paths set.
	 */
	public CallableWithInputAndReturn() {
		super();
	}
	
	/**
	 * @param input
	 * an input path
	 */
	public void setInput(A input) {
		this.input = input;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		setOutputAndNotifyMultiplexer(processInput(input));
		return true;
	}

	/**
	 * Processes a single item of type A and returns an item of type B (or {@code null}).
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * an item of type B
	 */
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
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setMultiplexer(se.de.hu_berlin.informatik.utils.threaded.NToOneMultiplexer)
	 */
	@Override
	public void setMultiplexer(IMultiplexer<B> multiplexer) {
		if (multiplexer == null) {
			throw new IllegalStateException("No multiplexer given (null).");
		}
		this.multiplexer = multiplexer;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getLock()
	 */
	@Override
	public Object getLock() {
		return lock;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getOutput()
	 */
	@Override
	public B getOutput() {
		return output;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutput(java.lang.Object)
	 */
	@Override
	public boolean setOutput(B item) {
		output = item;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#outputItemIsValid()
	 */
	@Override
	public boolean outputItemIsValid() {
		return hasNewOutput;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutputItemValid()
	 */
	@Override
	public void setOutputItemValid() {
		hasNewOutput = true;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutputItemInvalid()
	 */
	@Override
	public void setOutputItemInvalid() {
		hasNewOutput = false;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getMultiplexer()
	 */
	@Override
	public IMultiplexer<B> getMultiplexer() {
		return multiplexer;
	}
	
}
