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
 * be called, namely: {@link #processItem(Object)}, {@link #processItem(Object, Producer)}
 * and {@link #getProducer()}. These methods will throw exceptions when called nonetheless.
 * 
 * <p> The method {@link #setProducer(Producer)} will do nothing when called. This is 
 * intended behaviour.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessor<A> extends Processor<A,Object>, ConsumingProcessorSocketGenerator<A> {
	
	/**
	 * Should provide the main functionality of this ConsumingProcessor.
	 * @param item
	 * the item to consume
	 */
	@Override
	public void consume(A item);

	/**
	 * Throws an exception, as it should not be used by a {@link ConsumingProcessor}.
	 * @param item
	 * the item to be processed
	 * @param producer
	 * the Producer to send processed items to (needed for manually producing items)
	 * @return
	 * the processed item
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default Object processItem(A item, Producer<Object> producer) throws UnsupportedOperationException {
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
	 * Does nothing, as it is not useful for a {@link ConsumingProcessor}.
	 * @param producer
	 * the producer
	 */
	@Override
	default void setProducer(Producer<Object> producer) {
		//do nothing
	}

	/**
	 * Throws an exception, as it should not be used by a {@link ConsumingProcessor}.
	 * @return
	 * the Producer that was set for this Processor.
	 * @throws UnsupportedOperationException
	 * when called
	 */
	@Override
	default Producer<Object> getProducer() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " tries to access a producer but should not produce output.");
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
	 * Returns a new ConsumingProcessor instance with the same functionality as this
	 * ConsumingProcessor. Is used by {@link #newModuleInstance()}, {@link #newPipeInstance()} and {@link #newEHInstance()}.
	 * <p> Per default, this creates a new {@link AbstractConsumingProcessor} that
	 * inherits the methods {@link #consume(Object)}, {@link #getResultFromCollectedItems()} and 
	 * {@link #finalShutdown()} from this ConsumingProcessor. 
	 * It will, however, NOT generate separate instances of any declared global fields, for example.
	 * <p> If a new instance should be given their own global fields or some other functionality that
	 * is not met by the default implementation, this method has to be overridden
	 * to provide the desired functionality.
	 * @return
	 * a new ConsumingProcessor instance
	 */
	@Override
	default public ConsumingProcessor<A> newProcessorInstance() {
		return new AbstractConsumingProcessor<A>() {
			@Override
			public void resetAndInit() {
				ConsumingProcessor.this.resetAndInit();
			}
			@Override
			public void consume(A item) {
				ConsumingProcessor.this.consume(item);
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
