/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessor<A> extends ConsumingProcessorUserGenerator<A> {

	/**
	 * Processes an item of type {@code A}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 */
	public void consume(A item);
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return true;
	}
	
	/**
	 * Creates a new pipe object from this transmitter that has the transmitter's 
	 * functionality.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	default public AbstractPipe<A,?> newPipeInstance() throws UnsupportedOperationException {
		AbstractPipe<A,Object> pipe = new AbstractPipe<A,Object>(true);
		pipe.setProcessor(newProcessorInstance());
		return pipe;
		
	}

	/**
	 * Creates a new module object from this transmitter that has the transmitter's 
	 * functionality.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	default public AbstractModule<A,?> newModuleInstance() throws UnsupportedOperationException {
		AbstractModule<A,Object> module = new AbstractModule<A,Object>(true);
		module.setProcessor(newProcessorInstance());
		return module;
	}

	/**
	 * Creates a new event handler from this transmitter that has the transmitter's 
	 * functionality.
	 * @return
	 * an event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	default public DisruptorFCFSEventHandler<A> newEHInstance() throws UnsupportedOperationException {
		DisruptorFCFSEventHandler<A> eh = new DisruptorFCFSEventHandler<A>() {
			@Override
			public void resetAndInit() {
				//do nothing
			}
		};
		eh.setProcessor(newProcessorInstance());
		return eh;
	}
	
	default public ConsumingProcessor<A> newProcessorInstance() {
		return new AbstractConsumingProcessor<A>() {
			@Override
			public void consume(A item) {
				ConsumingProcessor.this.consume(item);
			}
		};
	}
	
}
