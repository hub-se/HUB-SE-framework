/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

/**
 * An interface that provides basic functionalities of a consumer.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * is the type of the input object
 */
public interface Consumer<A> extends OptionCarrier {
	
	/**
	 * Processes an item of type {@code A}.
	 * @param item
	 * the item to be processed
	 */
	public void consume(A item);
	
	/**
	 * Should cut all loose ends.
	 * @return
	 * true if successful
	 */
	default public boolean finalShutdown() {
		return true;
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
	public AbstractPipe<A,?> asPipe() throws UnsupportedOperationException;
	
	/**
	 * Creates a module object from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractModule<A,?> asModule() throws UnsupportedOperationException;
	
	/**
	 * Creates an event handler from this transmitter that has the transmitter's 
	 * functionality. Has to return a reference to the same object if called
	 * multiple times.
	 * @return
	 * a new event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public DisruptorFCFSEventHandler<A> asEH() throws UnsupportedOperationException;


	/**
	 * Creates a new pipe object from this transmitter that has the transmitter's 
	 * functionality.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public AbstractPipe<A, ?> newPipeInstance() throws UnsupportedOperationException {
		return new AbstractPipe<A,Object>(true) {
			@Override
			public Object processItem(A item) {
				Consumer.this.consume(item);
				return null;
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
	default public AbstractModule<A, ?> newModuleInstance() throws UnsupportedOperationException {
		return new AbstractModule<A, Object>(true) {
			@Override
			public Object processItem(A item) {
				Consumer.this.consume(item);
				return null;
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
	default public DisruptorFCFSEventHandler<A> newEHInstance() throws UnsupportedOperationException {
		return new DisruptorFCFSEventHandler<A>() {
			@Override
			public void resetAndInit() {
				//do nothing
			}
			@Override
			public void consume(A item) {
				Consumer.this.consume(item);
			}
		};
	}

}
