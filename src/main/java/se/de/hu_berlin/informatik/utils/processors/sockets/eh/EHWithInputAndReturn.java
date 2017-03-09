/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.sockets.eh;

import java.util.concurrent.atomic.AtomicBoolean;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.processors.Processor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.module.Module;
import se.de.hu_berlin.informatik.utils.processors.sockets.pipe.Pipe;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.Multiplexer;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.MultiplexerInput;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

/**
 * A {@link ProcessorSocket} implementation that provides a simple API for 
 * a disruptor event handler that processes a single input object at a time 
 * and produces output objects that may be collected by a multiplexer thread.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * @param <B>
 * the type of the output objects
 */
public class EHWithInputAndReturn<A,B> extends DisruptorFCFSEventHandler<A> implements ProcessorSocket<A,B>, Trackable, OptionCarrier, MultiplexerInput<B> {

	/**
	 * The output object.
	 */
	private B output = null;
	
	private AtomicBoolean hasNewOutput = new AtomicBoolean(false);
	private final Object lock = new Object();
	private Multiplexer<B> multiplexer = null;
	
	private Processor<A, B> processor;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param processor
	 * the processor to use
	 */
	public EHWithInputAndReturn(Processor<A,B> processor) {
		super();
		insert(processor);
	}
	
	@Override
	public void processEvent(A input) throws Exception {
		initAndConsume(input);
	}

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
	public Pipe<A, B> asPipe() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Module<A, B> asModule() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,B>> E asEH() throws UnsupportedOperationException {
		return (E) this;
	}

	@Override
	public void resetAndInit() {
		//do nothing (gets reset at another place)
	}
	
}
