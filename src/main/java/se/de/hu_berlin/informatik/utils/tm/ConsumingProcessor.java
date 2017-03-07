/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInput;
import se.de.hu_berlin.informatik.utils.tm.user.ConsumingProcessorUserGenerator;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUser;

/**
 * An interface that provides basic functionalities of transmitters that can be linked together.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface ConsumingProcessor<A> extends Processor<A,Object>, ConsumingProcessorUserGenerator<A> {
	
	@Override
	public void consume(A item);

	@Override
	default Object processItem(A item, Producer<Object> producer) throws UnsupportedOperationException {
		throw new IllegalStateException(this.getClass().getSimpleName() + " tries to process item with output.");
	}
	
	@Override
	default Object processItem(A item) throws UnsupportedOperationException {
		throw new IllegalStateException(this.getClass().getSimpleName() + " tries to process item with output.");
	}

	@Override
	default void setProducer(Producer<Object> producer) {
		//do nothing
	}

	@Override
	default Producer<Object> getProducer() throws IllegalStateException {
		throw new IllegalStateException(this.getClass().getSimpleName() + " tries to access a producer but should not produce output.");
	}
	
	/**
	 * Creates a new event handler from this transmitter that has the transmitter's 
	 * functionality.
	 * @return
	 * an event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@SuppressWarnings("unchecked")
	@Override
	default public <E extends DisruptorFCFSEventHandler<A> & ProcessorUser<A,Object>> E newEHInstance() throws UnsupportedOperationException {
		EHWithInput<A> eh = new EHWithInput<A>(newProcessorInstance());
		return (E) eh;
	}
	
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
		};
	}
	
}
