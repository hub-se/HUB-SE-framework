/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

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
public interface Transmitter<A,B> extends Consumer<A>, Producer<B> {
	
	/**
	 * Consumes an item of type {@code A}, processes it and produces an item of type {@code B}.
	 * @param item
	 * the item to be processed
	 */
	@Override
	default public void consume(A item) {
		produce(processItem(item));
	}
	
	/**
	 * Processes an item of type {@code A} and produces an item of type {@code B}.
	 * @param item
	 * the item to be processed
	 * @return
	 * the processed item
	 */
	public B processItem(A item);

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
	 * Creates a pipe object from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException;
	
	/**
	 * Creates a module object from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	public AbstractModule<A,B> asModule() throws UnsupportedOperationException;
	
	/**
	 * Creates an event handler from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a new event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	@Override
	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException;


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
		return new AbstractPipe<A,B>(true) {
			@Override
			public B processItem(A item) {
				return Transmitter.this.processItem(item);
			}
		};
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
		return new AbstractModule<A, B>(true) {
			@Override
			public B processItem(A item) {
				return Transmitter.this.processItem(item);
			}
		};
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
		return new EHWithInputAndReturn<A,B>() {
			@Override
			public B processItem(A input) {
				return Transmitter.this.processItem(input);
			}
			@Override
			public void resetAndInit() {
				//do nothing
			}
		};
	}
	
}
