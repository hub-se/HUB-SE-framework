package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.Module;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.Pipe;

/**
 * An interface that provides the functionality of being capable to 
 * generate different implementations of {@link ProcessorSocket}, namely
 * {@link Module}, {@link Pipe} and {@link AbstractDisruptorEventHandler}
 * (in the form of {@link EHWithInputAndReturn}).
 * 
 * @author Simon
 *
 * @param <A>
 * is the type of the input objects
 * @param <B>
 * is the type of the output objects
 */
public interface ProcessorSocketGenerator<A,B> {

	/**
	 * Creates a {@link Pipe} from this object. Has to return a 
	 * reference to the same Pipe if called multiple times.
	 * @return
	 * a Pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public Pipe<A,B> asPipe() throws UnsupportedOperationException;
	
	/**
	 * Creates a {@link Module} from this object. Has to return a 
	 * reference to the same Module if called multiple times.
	 * @return
	 * a Module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public Module<A,B> asModule() throws UnsupportedOperationException;
	
	/**
	 * Creates an {@link AbstractDisruptorEventHandler} from this object
	 * that may be used as a {@link ProcessorSocket}. 
	 * Has to return a reference to the same AbstractDisruptorEventHandler 
	 * if called multiple times.
	 * @return
	 * a new AbstractDisruptorEventHandler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 * @param <E>
	 * the type of the returned event handler
	 */
	public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,B>> E asEH() throws UnsupportedOperationException;


	/**
	 * Creates a new {@link Pipe} from this component.
	 * @return
	 * a Pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public Pipe<A, B> newPipeInstance() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can not get new instance for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Creates a new {@link Module} from this component.
	 * @return
	 * a Module, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	default public Module<A, B> newModuleInstance() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can not get new instance for " + this.getClass().getSimpleName() + ".");
	}

	/**
	 * Creates a new {@link AbstractDisruptorEventHandler} from this component.
	 * @return
	 * an AbstractDisruptorEventHandler, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 * @param <E>
	 * the type of the returned event handler
	 */
	default public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,B>> E newEHInstance() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can not get new instance for " + this.getClass().getSimpleName() + ".");
	}
	
}
