/**
 * 
 */
package se.de.hu_berlin.informatik.utils.processors.sockets;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.processors.Processor;
import se.de.hu_berlin.informatik.utils.processors.Producer;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

/**
 * An interface that provides basic functionalities of transmitters that can
 * hold a {@link Processor} and can be linked together, such that an input
 * item of type {@code A} can be submitted to the transmitter, gets processed
 * by the inserted {@link Processor}, and then the produced item of type {@code B}
 * gets submitted to the linked transmitter which repeats the process, using its 
 * own given Processor.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input objects
 * @param <B>
 * is the type of the output objects
 */
public interface ProcessorSocket<A,B> extends Producer<B>, ProcessorSocketGenerator<A, B>, Trackable, OptionCarrier {
	
	/**
	 * Inserts the given {@link Processor} into the socket. Calling this method
	 * in implementing classes should be the usual (and intended) way to produce a
	 * usable {@link ProcessorSocket}.
	 * <p> Per default, this method calls {@link #setProcessor(Processor)} on the
	 * given Processor to register it for this socket. Then, {@link Processor#setSocket(ProcessorSocket)}
	 * gets called to register this socket as the {@link Producer} for the given Processor.
	 * @param processor
	 * the Processor to insert
	 */
	default public void insert(Processor<A, B> processor) {
		setProcessor(processor);
		processor.setSocket(this);
	}
	
	/**
	 * Registers the given Processor for this socket.
	 * @param processor
	 * the Processor to register
	 */
	public void setProcessor(Processor<A, B> processor);
	
	/**
	 * @return
	 * the registered Processor, if any
	 * @throws IllegalStateException
	 * if no Processor was registered
	 */
	public Processor<A, B> getProcessor() throws IllegalStateException;

	/**
	 * Calls {@link Processor#resetTrackAndConsume(Object)} on the given item,
	 * using the registered Processor.
	 * @param item
	 * the item to process
	 */
	default void initAndConsume(A item) {
		getProcessor().resetTrackAndConsume(item);
	}
	
	/**
	 * Calls {@link Processor#getResultFromCollectedItems()}, using the
	 * registered Processor.
	 * @return
	 * the result of unprocessed collected items
	 */
	default public B getResultFromCollectedItems(){
		return getProcessor().getResultFromCollectedItems();
	}
	
	/**
	 * Calls {@link Processor#finalShutdown()}, using the
	 * registered Processor.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return getProcessor().finalShutdown();
	}
	
}
