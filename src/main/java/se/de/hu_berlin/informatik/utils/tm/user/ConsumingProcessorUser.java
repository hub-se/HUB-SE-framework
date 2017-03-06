/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.ConsumingProcessor;

/**
 * An interface that provides basic functionalities of a consumer.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessorUser<A> extends ConsumingProcessorUserGenerator<A> {
	
	public void setProcessor(ConsumingProcessor<A> consumer);
	
	public ConsumingProcessor<A> getProcessor() throws IllegalStateException;
	
	/**
	 * Processes an item of type {@code A}.
	 * @param item
	 * the item to be processed
	 */
	default void consume(A item) {
		getProcessor().consume(item);
	}
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return getProcessor().finalShutdown();
	}
	
//	/**
//	 * Creates a pipe object from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a pipe, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	default public AbstractPipe<A,?> asPipe() throws UnsupportedOperationException {
//		return getProcessor().asPipe();
//	}
//	
//	/**
//	 * Creates a module object from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a module, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	default public AbstractModule<A,?> asModule() throws UnsupportedOperationException {
//		return getProcessor().asModule();
//	}
//	
//	/**
//	 * Creates an event handler from this transmitter that has the transmitter's 
//	 * functionality. Has to return a reference to the same object if called
//	 * multiple times.
//	 * @return
//	 * a new event handler, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	default public DisruptorFCFSEventHandler<A> asEH() throws UnsupportedOperationException {
//		return getProcessor().asEH();
//	}
//
//
//	/**
//	 * Creates a new pipe object from this transmitter that has the transmitter's 
//	 * functionality.
//	 * @return
//	 * a pipe, if possible
//	 * @throws UnsupportedOperationException
//	 * if not possible
//	 */
//	default public AbstractPipe<A, ?> newPipeInstance() throws UnsupportedOperationException {
//		AbstractPipe<A, Object> pipe = new AbstractPipe<A,Object>(true);
//		pipe.setProcessor(this);
//		return pipe;
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
//	default public AbstractModule<A, ?> newModuleInstance() throws UnsupportedOperationException {
//		AbstractModule<A, Object> module = new AbstractModule<A,Object>(true);
//		module.setProcessor(this);
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
//	default public DisruptorFCFSEventHandler<A> newEHInstance() throws UnsupportedOperationException {
//		return new DisruptorFCFSEventHandler<A>() {
//			@Override
//			public void resetAndInit() {
//				//do nothing
//			}
//			@Override
//			public void consume(A item) {
//				Consumer.this.consume(item);
//			}
//		};
//	}

}
