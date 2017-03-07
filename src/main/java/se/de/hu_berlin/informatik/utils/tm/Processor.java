/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
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
	
	public Producer<B> getProducer() throws IllegalStateException;
	
	default public void manualOutput(B item) {
		getProducer().produce(item);
	}
	
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
	default public AbstractPipe<A, B> newPipeInstance() throws UnsupportedOperationException {
		AbstractPipe<A, B> pipe = new AbstractPipe<A,B>(newProcessorInstance(), true);
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
		AbstractModule<A, B> module = new AbstractModule<A, B>(newProcessorInstance());
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
		EHWithInputAndReturn<A,B> eh = new EHWithInputAndReturn<A,B>(newProcessorInstance());
		return eh;
	}
	
	default public Processor<A,B> newProcessorInstance() {
		return new AbstractProcessor<A,B>() {
			@Override
			public B processItem(A item) {
				return Processor.this.processItem(item);
			}
			@Override
			public void resetAndInit() {
				Processor.this.resetAndInit();
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