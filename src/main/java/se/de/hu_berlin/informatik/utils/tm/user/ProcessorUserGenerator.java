package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;

public interface ProcessorUserGenerator<A,B> {

	/**
	 * Creates a pipe object from this component. Has to return a 
	 * reference to the same object if called multiple times.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException;
	
	/**
	 * Creates a module object from this component. Has to return a 
	 * reference to the same object if called multiple times.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractModule<A,B> asModule() throws UnsupportedOperationException;
	
	/**
	 * Creates an event handler from this component. Has to return a 
	 * reference to the same object if called multiple times.
	 * @return
	 * a new event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException;


	/**
	 * Creates a new pipe object from this component.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public AbstractPipe<A, B> newPipeInstance() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can not get new instance for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Creates a new module object from this component.
	 * @return
	 * a module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public AbstractModule<A, B> newModuleInstance() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can not get new instance for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Creates a new event handler from this component.
	 * @return
	 * an event handler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public EHWithInputAndReturn<A,B> newEHInstance() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can not get new instance for " + this.getClass().getSimpleName() + ".");
	}
	
}
