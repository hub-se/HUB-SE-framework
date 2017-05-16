/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.sockets.eh;

import se.de.hu_berlin.informatik.utils.processors.ConsumingProcessor;
import se.de.hu_berlin.informatik.utils.processors.Processor;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.module.Module;
import se.de.hu_berlin.informatik.utils.processors.sockets.pipe.Pipe;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;

/**
 * A {@link ConsumingProcessorSocket} implementation that provides a simple API for 
 * a disruptor event handler that processes a single input object at a time.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 */
public class EHWithInput<A> extends DisruptorFCFSEventHandler<A> implements ConsumingProcessorSocket<A>, ConsumingProcessorSocketGenerator<A> {

	private Processor<A,Object> processor;
	
	/**
	 * Creates a new module with the given parameter.
	 * @param processor
	 * the processor to use
	 */
	public EHWithInput(ConsumingProcessor<A> processor) {
		super();
		insert(processor);
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
	public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,Object>> E asEH() throws UnsupportedOperationException {
		return (E) this;
	}

	@Override
	public void resetAndInit() {
		//do nothing (gets reset at another place)
	}

	@Override
	public Pipe<A, Object> asPipe(ClassLoader classLoader) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("not supported");
	}
	
}
