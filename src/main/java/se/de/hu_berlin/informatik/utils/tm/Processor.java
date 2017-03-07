/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.Module;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.Pipe;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUser;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorUserGenerator;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

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
public interface Processor<A,B> extends ProcessorUserGenerator<A,B>, Trackable, OptionCarrier {
	
	default void trackAndConsume(A item) {
		track();
		consume(item);
	}
	
	default void consume(A item) {
		//in some cases, calling getProducer() does not deliver satisfying results when 
		//called inside some class, so we do it here and it seems to work...
		//it's more complicated, though...
		//problems arise when trying to get the producer from within the processing
		//method. Even when getting a new instance of the processor which has its producer set,
		//getProducer seemingly still points to the method in the original instance...
		getProducer().produce(processItem(item, getProducer()));
	}

	/**
	 * Processes an item of type {@code A} and produces an item of type {@code B}.
	 * In the default case, this method simply calls {@link #processItem(Object)}.
	 * @param item
	 * the item to be processed
	 * @param producer
	 * the producer to send processed items to (needed for manually producing items)
	 * @return
	 * the processed item
	 */
	default public B processItem(A item, Producer<B> producer) {
		return processItem(item);
	}
	
	/**
	 * Processes an item of type {@code A} and produces an item of type {@code B}.
	 * In the default case, this method gets called by {@link #processItem(Object, Producer)}
	 * and may be used if manually producing processed items is not necessary.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 * @throws UnsupportedOperationException
	 * if not implemented
	 */
	default public B processItem(A item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("No processing method set for " + this.getClass().getSimpleName() + ".");
	}
	
	public void setProducer(Producer<B> producer);
	
	public Producer<B> getProducer() throws IllegalStateException;
	
	/* this does not work as intended in certain cases (especially when creating event handlers)! */
//	default public void manualOutput(B item) {
//		getProducer().produce(item);
//	}
	
	default public void resetAndInit() {
		//does nothing per default
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
	default public Pipe<A, B> newPipeInstance() throws UnsupportedOperationException {
		Pipe<A, B> pipe = new Pipe<A,B>(newProcessorInstance(), true);
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
	default public Module<A, B> newModuleInstance() throws UnsupportedOperationException {
		Module<A, B> module = new Module<A, B>(newProcessorInstance());
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
	@SuppressWarnings("unchecked")
	@Override
	default public <E extends DisruptorFCFSEventHandler<A> & ProcessorUser<A,B>> E newEHInstance() throws UnsupportedOperationException {
		EHWithInputAndReturn<A,B> eh = new EHWithInputAndReturn<A,B>(newProcessorInstance());
		return (E) eh;
	}
	
	default public Processor<A,B> newProcessorInstance() {
		return new AbstractProcessor<A,B>() {
			@Override
			public B processItem(A item, Producer<B> producer) {
				return Processor.this.processItem(item, producer);
			}
			@Override
			public void resetAndInit() {
				Processor.this.resetAndInit();
			}
			@Override
			public B processItem(A item) {
				return Processor.this.processItem(item);
			}
		};
	}
	
	@Override
	default public Processor<A,B> enableTracking() {
		Trackable.super.enableTracking();
		return this;
	}

	@Override
	default public Processor<A,B> enableTracking(int stepWidth) {
		Trackable.super.enableTracking(stepWidth);
		return this;
	}

	@Override
	default public Processor<A,B> disableTracking() {
		Trackable.super.disableTracking();
		return this;
	}

	@Override
	default public Processor<A,B> enableTracking(TrackingStrategy tracker) {
		Trackable.super.enableTracking(tracker);
		return this;
	}

	@Override
	default public Processor<A,B> enableTracking(boolean useProgressBar) {
		Trackable.super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	default public Processor<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		Trackable.super.enableTracking(useProgressBar, stepWidth);
		return this;
	}
}
