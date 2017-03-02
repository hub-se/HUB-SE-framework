/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.Multiplexer;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.MultiplexerInput;
import se.de.hu_berlin.informatik.utils.tm.Transmitter;
import se.de.hu_berlin.informatik.utils.tm.TransmitterProvider;

/**
 * An abstract class that provides a simple API for a disruptor event handler
 * that processes a single input object at a time and produces output objects
 * that may be collected by a multiplexer thread.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * @param <B>
 * the type of the output objects
 * 
 * @see Callable
 */
public abstract class EHWithInputAndReturn<A,B> extends DisruptorFCFSEventHandler<A> implements MultiplexerInput<B>, Transmitter<A,B>, TransmitterProvider<A,B> {

	/**
	 * The output object.
	 */
	private B output = null;
	
	private AtomicBoolean hasNewOutput = new AtomicBoolean(false);
	private final Object lock = new Object();
	private Multiplexer<B> multiplexer = null;
	
	private OptionParser options = null;

	@Override
	public void processEvent(A input) throws Exception {
		trySettingNewOutputAndValidate(processInput(input));
	}
	
	/**
	 * Processes a single item of type A and returns an item of type B (or {@code null}).
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * an item of type B
	 */
	public abstract B processInput(A input);
	
	public void manualOutput(B outputItem) {
		trySettingNewOutputAndValidate(outputItem);
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setMultiplexer(se.de.hu_berlin.informatik.utils.threaded.NToOneMultiplexer)
	 */
	@Override
	public void setMultiplexer(Multiplexer<B> multiplexer) {
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
		return hasNewOutput.get();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutputItemValid()
	 */
	@Override
	public void setOutputItemValid() {
		hasNewOutput.set(true);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#setOutputItemInvalid()
	 */
	@Override
	public void setOutputItemInvalid() {
		hasNewOutput.set(false);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threaded.IMultiplexerInput#getMultiplexer()
	 */
	@Override
	public Multiplexer<B> getMultiplexer() {
		return multiplexer;
	}

	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public EHWithInputAndReturn<A, B> setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
	}

	@Override
	public B processItem(A item) {
		return processInput(item);
	}

	@Override
	public <C, D> Transmitter<C, D> linkTo(Transmitter<C, D> transmitter)
			throws IllegalStateException {
		throw new IllegalStateException("Can not link event handler (not allowed).");
	}
	
}
