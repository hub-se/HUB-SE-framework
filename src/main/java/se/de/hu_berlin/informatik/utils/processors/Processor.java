/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors;

import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocketGenerator;
import se.de.hu_berlin.informatik.utils.processors.sockets.eh.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.processors.sockets.module.Module;
import se.de.hu_berlin.informatik.utils.processors.sockets.pipe.Pipe;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;

/**
 * An interface that provides basic functionalities of a processor that consumes
 * items of type {@code A}, processes them and produces items of type {@code B}.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 * @param <B>
 * is the type of the output object
 */
public interface Processor<A, B> extends ProcessorSocketGenerator<A, B> {

	/**
	 * Convenience method that calls {@link #asModule()} on this Processor
	 * and then submits the given item.
	 * @param item
	 * the item to process
	 * @return
	 * this Processor as a {@link Module}, for chaining
	 */
	default public Module<A,B> submit(Object item) {
		return asModule().submit(item);
	}
	
	/**
	 * This method should mainly get called by classes that use the processor.
	 * <p>
	 * Per default, this method calls {@link #resetAndInit()}, then
	 * {@link ProcessorSocket#track()} and then {@link #_consume_(Object)} on
	 * the given item.
	 * @param item
	 * the item to consume
	 */
	default void resetTrackAndConsume(A item) {
		resetAndInit();
		getSocket().track();
		_consume_(item);
	}

	/**
	 * Per default, calls {@link #processItem(Object, ProcessorSocket)} on the
	 * given item and the result of {@link #getSocket()}. Then,
	 * {@link Producer#produce(Object)} is called on the result.
	 * @param item
	 * the item to consume
	 */
	default void _consume_(A item) {
		// in some cases, calling getSocket() does not deliver satisfying
		// results when
		// called inside some class, so we do it here and it seems to work...
		// it's more complicated, though...
		// problems arise when trying to get the socket from within the
		// processing
		// method. Even when getting a new instance of the processor which has
		// its socket set,
		// getSocket() seemingly still points to the method in the original
		// instance...
		getSocket().produce(processItem(item, getSocket()));
	}

	/**
	 * Processes an item of type {@code A} and produces an item of type
	 * {@code B}. In the default case, this method simply calls
	 * {@link #processItem(Object)}.
	 * @param item
	 * the item to be processed
	 * @param socket
	 * the executing socket instance
	 * @return the processed item
	 */
	default public B processItem(A item, ProcessorSocket<A, B> socket) {
		return processItem(item);
	}

	/**
	 * Processes an item of type {@code A} and produces an item of type
	 * {@code B}. In the default case, this method gets called by
	 * {@link #processItem(Object, ProcessorSocket)} and can be used if manually
	 * producing processed items is not necessary.
	 * @param item
	 * the item to be processed
	 * @return the processed item
	 * @throws UnsupportedOperationException
	 * if not implemented
	 */
	default public B processItem(A item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"No processing method set for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Sets a {@link ProcessorSocket} to be used by this Processor.
	 * @param socket
	 * the socket
	 */
	public void setSocket(ProcessorSocket<A, B> socket);

	/**
	 * @return the socket that was set for this Processor.
	 * @throws IllegalStateException
	 * if no socket was set
	 */
	public ProcessorSocket<A, B> getSocket() throws IllegalStateException;

	/*
	 * this does not work as intended in certain cases (especially when creating
	 * event handlers)!
	 */
	// default public void manualOutput(B item) {
	// getProducer().produce(item);
	// }

	/**
	 * Does nothing per default. Is intended to be called before consuming each
	 * item by classes that use this processor. Should be overridden by
	 * implementing classes if, e.g., global variables have to be resetted after
	 * processing an item (though, using global variables can easily lead to
	 * errors when using processors in parallel...).
	 */
	default public void resetAndInit() {
		// does nothing per default
	}

	/**
	 * Should be overwritten by implementing classes that may collect input
	 * items without immediately processing or returning results. This method
	 * should process possibly remaining collected items and/or return the
	 * result (or null if there is no result).
	 * @return the result of unprocessed collected items
	 */
	default public B getResultFromCollectedItems() {
		return null;
	}

	/**
	 * Should cut all loose ends. May not be called automatically. Per default,
	 * simply returns true and does nothing else.
	 * @return true if successful
	 */
	default public boolean finalShutdown() {
		return true;
	}

	/**
	 * Creates a new {@link Pipe} from this Processor that inherits this
	 * Processor's functionality.
	 * @return a Pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	default public Pipe<A, B> newPipeInstance() throws UnsupportedOperationException {
		Pipe<A, B> pipe = new Pipe<A, B>(newProcessorInstance(), true);
		return pipe;

	}

	/**
	 * Creates a new {@link Module} from this Processor that inherits this
	 * Processor's functionality.
	 * @return a Module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	default public Module<A, B> newModuleInstance() throws UnsupportedOperationException {
		Module<A, B> module = new Module<A, B>(newProcessorInstance());
		return module;
	}

	/**
	 * Creates a new {@link AbstractDisruptorEventHandler} from this transmitter
	 * that inherits the Processor's functionality.
	 * @return an AbstractDisruptorEventHandler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@SuppressWarnings("unchecked")
	@Override
	default public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A, B>> E newEHInstance()
			throws UnsupportedOperationException {
		EHWithInputAndReturn<A, B> eh = new EHWithInputAndReturn<A, B>(newProcessorInstance());
		return (E) eh;
	}

	/**
	 * Returns a new Processor instance with the same functionality as this
	 * Processor. Is used by {@link #newModuleInstance()},
	 * {@link #newPipeInstance()} and {@link #newEHInstance()}.
	 * <p>
	 * Per default, this creates a new {@link AbstractProcessor} that inherits
	 * the methods {@link #processItem(Object, ProcessorSocket)},
	 * {@link #getResultFromCollectedItems()} and {@link #finalShutdown()} from
	 * this Processor. It will, however, NOT generate separate instances of any
	 * declared global fields, for example. Note that
	 * {@link #processItem(Object)} gets called by
	 * {@link #processItem(Object, ProcessorSocket)}, such that it will get called even
	 * if it is not directly inherited.
	 * <p>
	 * If a new instance should be given their own global fields or some other
	 * functionality that is not met by the default implementation, this method
	 * has to be overridden to provide the desired functionality.
	 * @return a new Processor instance
	 */
	default public Processor<A, B> newProcessorInstance() {
		return new AbstractProcessor<A, B>() {

			@Override
			public B processItem(A item, ProcessorSocket<A, B> socket) {
				return Processor.this.processItem(item, socket);
			}

			@Override
			public void resetAndInit() {
				Processor.this.resetAndInit();
			}

			@Override
			public B getResultFromCollectedItems() {
				return Processor.this.getResultFromCollectedItems();
			}

			@Override
			public boolean finalShutdown() {
				return Processor.this.finalShutdown();
			}
		};
	}

	// @Override
	// default public Processor<A,B> enableTracking() {
	// Trackable.super.enableTracking();
	// return this;
	// }
	//
	// @Override
	// default public Processor<A,B> enableTracking(int stepWidth) {
	// Trackable.super.enableTracking(stepWidth);
	// return this;
	// }
	//
	// @Override
	// default public Processor<A,B> disableTracking() {
	// Trackable.super.disableTracking();
	// return this;
	// }
	//
	// @Override
	// default public Processor<A,B> enableTracking(TrackingStrategy tracker) {
	// Trackable.super.enableTracking(tracker);
	// return this;
	// }
	//
	// @Override
	// default public Processor<A,B> enableTracking(boolean useProgressBar) {
	// Trackable.super.enableTracking(useProgressBar);
	// return this;
	// }
	//
	// @Override
	// default public Processor<A,B> enableTracking(boolean useProgressBar, int
	// stepWidth) {
	// Trackable.super.enableTracking(useProgressBar, stepWidth);
	// return this;
	// }
}
