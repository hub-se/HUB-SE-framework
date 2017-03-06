/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessor<A> extends Processor<A,Object>, ConsumingProcessorUserGenerator<A> {

	/**
	 * Processes an item of type {@code A}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 */
	@Override
	default void consume(A item) {
		processItem(item);
	}

	@Override
	default void setProducer(Producer<Object> producer) {
		//do nothing
	}

	@Override
	default Producer<Object> getProducer() throws IllegalStateException {
		throw new IllegalStateException("No producer set for " + this.getClass().getSimpleName() + ".");
	}
	
//	default public Processor<A, Object> asProcessor() {
//		return new AbstractProcessor<A, Object>() {
//			@Override
//			public Object processItem(A item) {
//				ConsumingProcessor.this.consume(item);
//				return null;
//			}
//		};
//	}
	
//	/**
//	 * Creates a new pipe object from this transmitter that has the transmitter's 
//	 * functionality.
//	 * @return
//	 * a pipe, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	@Override
//	default public AbstractPipe<A,?> newPipeInstance() throws UnsupportedOperationException {
//		AbstractPipe<A,Object> pipe = new AbstractPipe<A,Object>(newProcessorInstance());
//		return pipe;
//		
//	}
//
//	/**
//	 * Creates a new module object from this transmitter that has the transmitter's 
//	 * functionality.
//	 * @return
//	 * a module, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	@Override
//	default public AbstractModule<A,?> newModuleInstance() throws UnsupportedOperationException {
//		AbstractModule<A,Object> module = new AbstractModule<A,Object>(newProcessorInstance());
//		return module;
//	}
//
//	/**
//	 * Creates a new event handler from this transmitter that has the transmitter's 
//	 * functionality.
//	 * @return
//	 * an event handler, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	@Override
//	default public DisruptorFCFSEventHandler<A> newEHInstance() throws UnsupportedOperationException {
//		DisruptorFCFSEventHandler<A> eh = new DisruptorFCFSEventHandler<A>() {
//			@Override
//			public void resetAndInit() {
//				//do nothing
//			}
//		};
//		eh.setProcessor(newProcessorInstance());
//		return eh;
//	}
//	
//	default public ConsumingProcessor<A> newProcessorInstance() {
//		return new AbstractConsumingProcessor<A>() {
//			@Override
//			public void consume(A item) {
//				ConsumingProcessor.this.consume(item);
//			}
//		};
//	}
	
}
