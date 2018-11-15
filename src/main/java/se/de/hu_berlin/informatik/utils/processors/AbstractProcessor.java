package se.de.hu_berlin.informatik.utils.processors;

import se.de.hu_berlin.informatik.utils.processors.sockets.ProcessorSocket;
import se.de.hu_berlin.informatik.utils.processors.sockets.eh.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.processors.sockets.module.Module;
import se.de.hu_berlin.informatik.utils.processors.sockets.pipe.Pipe;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;

/**
 * A basic implementation of a {@link Processor} that implementing classes should
 * extend by implementing {@link #processItem(Object)} or {@link #processItem(Object, ProcessorSocket)}.
 * 
 * <p> Optionally, the methods {@link #getResultFromCollectedItems()}, {@link #resetAndInit()}, 
 * {@link #finalShutdown()} and {@link #newProcessorInstance()} can be overridden, if necessary.
 * 
 * <p> Other methods are generally not intended (and not safe) to be overridden by implementing
 * classes and overriding them may cause unintended behaviour when the Processor is used.
 * 
 * <p> For further details about the methods, take a look at the given comments.
 * 
 * @author Simon
 *
 * @param <A>
 * the type of input objects
 * @param <B>
 * the type of output objects
 */
public abstract class AbstractProcessor<A,B> implements Processor<A,B> { 

	private Pipe<A,B> pipeView;
	private Module<A,B> moduleView;
	private EHWithInputAndReturn<A,B> ehView;
	private ProcessorSocket<A, B> socket;
	private ClassLoader classLoader;
	
	public AbstractProcessor(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	
	public AbstractProcessor() {
		this(null);
	}
	
//	/**
//	 * Convenience method that calls {@link #asModule()} on this Processor
//	 * and then submits the given item.
//	 * @param item
//	 * the item to process
//	 * @return
//	 * this Processor as a {@link Module}, for chaining
//	 */
//	public Module<A,B> submit(Object item) {
//		return asModule().submit(item);
//	}
	
	@Override
	public Pipe<A,B> asPipe() {
		return asPipe(8, this.classLoader);
	}
	
	@Override
	public Pipe<A,B> asPipe(int bufferSize) {
		return asPipe(bufferSize, this.classLoader);
	}
	
	@Override
	public Pipe<A,B> asPipe(int bufferSize, ClassLoader classLoader) {
		if (pipeView == null) {
			pipeView = new Pipe<>(this, bufferSize, true, classLoader);
		}
		return pipeView;
	}

	@Override
	public Module<A,B> asModule() {
		if (moduleView == null) {
			moduleView = new Module<>(this);
		}
		return moduleView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,B>> E asEH() {
		if (ehView == null) {
			ehView = new EHWithInputAndReturn<>(this);
		}
		return (E) ehView;
	}

	@Override
	public void setSocket(ProcessorSocket<A, B> socket) {
		if (socket == null) {
			throw new IllegalStateException("No socket given (null) for " + this.getClass() + ".");
		}
		this.socket = socket;
	}

	@Override
	public ProcessorSocket<A, B> getSocket() {
		if (socket == null) {
			throw new IllegalStateException("No socket set for " + this.getClass() + ".");
		} else {
			return socket;
		}
	}
	
}
