/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.Multiplexer;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.MultiplexerInput;
import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUser;

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
public abstract class EHWithInputAndReturn<A,B> extends DisruptorFCFSEventHandler<A> implements ProcessorUser<A,B>, MultiplexerInput<B> {

	/**
	 * The output object.
	 */
	private B output = null;
	
	private AtomicBoolean hasNewOutput = new AtomicBoolean(false);
	private final Object lock = new Object();
	private Multiplexer<B> multiplexer = null;
	
	private Processor<A, B> processor;

	@Override
	public Processor<A, B> getProcessor() {
		return processor;
	}

	@Override
	public void setProcessor(Processor<A, B> consumer) {
		this.processor = consumer;
	}
	
	@Override
	public void produce(B item) {
		trySettingNewOutputAndValidate(item);
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
	public AbstractPipe<A, B> asPipe() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public AbstractModule<A, B> asModule() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public EHWithInputAndReturn<A, B> asEH() throws UnsupportedOperationException {
		return this;
	}
	
}
