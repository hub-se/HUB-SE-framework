/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.Producer;

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
public interface ProcessorUser<A,B> extends ConsumingProcessorUser<A>, Producer<B> {
	
	public void setProcessor(Processor<A, B> processor);
	
	public Processor<A, B> getProcessor() throws IllegalStateException;

	@Override
	default void consume(A item) {
		getProcessor().consume(item);
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
//	@Override
//	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException;
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
//	@Override
//	public AbstractModule<A,B> asModule() throws UnsupportedOperationException;
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
//	@Override
//	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException;
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
//	@Override
//	default public AbstractPipe<A, B> newPipeInstance() throws UnsupportedOperationException {
//		return getProcessor().newPipeInstance();
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
//	@Override
//	default public AbstractModule<A, ?> newModuleInstance() throws UnsupportedOperationException {
//		return getProcessor().newModuleInstance();
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
//	@Override
//	default public DisruptorFCFSEventHandler<A> newEHInstance() throws UnsupportedOperationException {
//		return getProcessor().newEHInstance();
//	}
	
}
