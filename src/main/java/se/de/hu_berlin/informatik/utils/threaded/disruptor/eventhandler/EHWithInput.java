/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import java.util.concurrent.Callable;
import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.tm.ConsumingProcessor;
import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.Module;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.Pipe;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUser;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUser;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

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
public class EHWithInput<A> extends DisruptorFCFSEventHandler<A> implements ConsumingProcessorUser<A>, ConsumingProcessorUserGenerator<A>, Trackable, OptionCarrier {

	private Processor<A,Object> processor;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param processor
	 * the processor to use
	 */
	public EHWithInput(ConsumingProcessor<A> processor) {
		super();
		setProcessor(processor);
	}
	
	@Override
	public void processEvent(A input) throws Exception {
		initAndConsume(input);
	}

	@Override
	public Processor<A,Object> getProcessor() {
		return processor;
	}

	@Override
	public void setProcessor(Processor<A,Object> consumer) {
		this.processor = consumer;
	}
	
	@Override
	public Pipe<A, Object> asPipe() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@Override
	public Module<A, Object> asModule() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends DisruptorFCFSEventHandler<A> & ProcessorUser<A,Object>> E asEH() throws UnsupportedOperationException {
		return (E) this;
	}

	@Override
	public void resetAndInit() {
		//do nothing (gets reset at another place)
	}
	
}
