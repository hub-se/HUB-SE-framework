/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUserGenerator;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 */
public interface Processor<A,B> extends ConsumingProcessor<A>, ProcessorUserGenerator<A,B> {
	
	@Override
	default void consume(A item) {
		getProducer().produce(processItem(item));
	}

	/**
	 * Processes an item of type {@code A} and produces an item of type {@code B}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 */
	public B processItem(A item);
	
	public void setProducer(Producer<B> producer);
	
	public Producer<B> getProducer();
	
	default public void manualOutput(B item) {
		getProducer().produce(item);
	}
	
	/**
	 * Should be overwritten by implementing transmitters that may collect
	 * input items without immediately processing them. This method should
	 * process possibly remaining collected items and/or return the result
	 * (or null if there is no result).
	 * @return
	 * the result of unprocessed collected items
	 */
	default public B getResultFromCollectedItems(){
		return null;
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
	default public AbstractPipe<A, B> newPipeInstance() throws UnsupportedOperationException {
		Processor<A,B> processor = newProcessorInstance();
		AbstractPipe<A, B> pipe = new AbstractPipe<A,B>(true);
		pipe.setProcessor(processor);
		processor.setProducer(pipe);
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
	default public AbstractModule<A, B> newModuleInstance() throws UnsupportedOperationException {
		Processor<A,B> processor = newProcessorInstance();
		AbstractModule<A, B> module = new AbstractModule<A, B>(true);
		module.setProcessor(processor);
		processor.setProducer(module);
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
	default public EHWithInputAndReturn<A, B> newEHInstance() throws UnsupportedOperationException {
		Processor<A,B> processor = newProcessorInstance();
		EHWithInputAndReturn<A,B> eh = new EHWithInputAndReturn<A,B>() {
			@Override
			public void resetAndInit() {
				//do nothing
			}
		};
		eh.setProcessor(processor);
		processor.setProducer(eh);
		return eh;
	}
	
	default public Processor<A,B> newProcessorInstance() {
		return new AbstractProcessor<A,B>() {
			@Override
			public B processItem(A item) {
				return Processor.this.processItem(item);
			}
		};
	}
	
}
