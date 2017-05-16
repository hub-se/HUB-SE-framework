/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors;

import se.de.hu_berlin.informatik.utils.processors.sockets.ConsumingProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.eh.EHWithInput;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;

/**
 * An interface that provides basic functionalities of a processor that consumes
 * items of type {@code A}, processes them and does NOT produce any output 
 * (that the processor would know or care of, at least).
 * 
 * <p> Overrides certain methods from the {@link Processor} interface that should not
 * be called, namely: {@link #processItem(Object)}, {@link #processItem(Object, ProcessorSocket)}
 * and {@link #getSocket()}. These methods will throw exceptions when called nonetheless.
 * 
 * <p> The method {@link #setSocket(ProcessorSocket)} will do nothing when called. This is 
 * intended behaviour.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessor<A> extends Processor<A,Object>, ConsumingProcessorSocketGenerator<A> {
	
	/**
	 * Per default, calls {@link #consumeItem(Object, ProcessorSocket)} on the given item
	 * and the result of {@link #getSocket()}.
	 * @param item
	 * the item to consume
	 */
	@Override
	default void _consume_(A item) {
		//in some cases, calling getSocket() does not deliver satisfying results when 
		//called inside some class, so we do it here and it seems to work...
		//it's more complicated, though...
		//problems arise when trying to get the socket from within the processing
		//method. Even when getting a new instance of the processor which has its socket set,
		//getSocket() seemingly still points to the method in the original instance...
		consumeItem(item, getSocket());
	}
	
	default void consumeItem(A item, ProcessorSocket<A, Object> socket) throws UnsupportedOperationException {
		consumeItem(item);
	}
	
	default void consumeItem(A item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("No processing method set for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Throws an exception, as it should not be used by a {@link ConsumingProcessor}.
	 * @param item
	 * the item to be processed
	 * @param socket
	 * the executing socket instance
	 * @return
	 * the processed item
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default Object processItem(A item, ProcessorSocket<A, Object> socket) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " tries to process item with output.");
	}
	
	/**
	 * Throws an exception, as it should not be used by a {@link ConsumingProcessor}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default Object processItem(A item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " tries to process item with output.");
	}
	
	/**
	 * Creates a new {@link AbstractDisruptorEventHandler} from this transmitter that 
	 * inherits the ConsumingProcessor's functionality.
	 * @return
	 * an AbstractDisruptorEventHandler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@SuppressWarnings("unchecked")
	@Override
	default public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,Object>> E newEHInstance() throws UnsupportedOperationException {
		EHWithInput<A> eh = new EHWithInput<A>(newProcessorInstance());
		return (E) eh;
	}
	
	/**
	 * Returns a new Processor instance with the same functionality as this
	 * Processor. Is used by {@link #newModuleInstance()},
	 * {@link #newPipeInstance()} and {@link #newEHInstance()}.
	 * <p>
	 * Per default, this creates a new {@link AbstractConsumingProcessor} that inherits
	 * the methods {@link #consumeItem(Object, ProcessorSocket)},
	 * {@link #getResultFromCollectedItems()} and {@link #finalShutdown()} from
	 * this Processor. It will, however, NOT generate separate instances of any
	 * declared global fields, for example. Note that
	 * {@link #consumeItem(Object)} gets called by
	 * {@link #consumeItem(Object, ProcessorSocket)}, such that it will get called even
	 * if it is not directly inherited.
	 * <p>
	 * If a new instance should be given their own global fields or some other
	 * functionality that is not met by the default implementation, this method
	 * has to be overridden to provide the desired functionality.
	 * @return a new Processor instance
	 */
	@Override
	default public ConsumingProcessor<A> newProcessorInstance() {
		return new AbstractConsumingProcessor<A>() {
			@Override
			public void resetAndInit() {
				ConsumingProcessor.this.resetAndInit();
			}
			@Override
			public void consumeItem(A item, ProcessorSocket<A, Object> socket) {
				ConsumingProcessor.this.consumeItem(item, socket);
			}
			@Override
			public Object getResultFromCollectedItems() {
				return ConsumingProcessor.this.getResultFromCollectedItems();
			}
			@Override
			public boolean finalShutdown() {
				return ConsumingProcessor.this.finalShutdown();
			}
		};
	}
	
}
