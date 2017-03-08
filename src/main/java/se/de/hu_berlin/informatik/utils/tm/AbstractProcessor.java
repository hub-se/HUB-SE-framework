package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.Module;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.Pipe;
import se.de.hu_berlin.informatik.utils.tm.user.ProcessorSocket;

/**
 * A basic implementation of a {@link Processor} that implementing classes should
 * extend by implementing {@link #processItem(Object)} or {@link #processItem(Object, Producer)}.
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
public abstract class AbstractProcessor<A,B> extends BasicComponent implements Processor<A,B> { 

	private Pipe<A,B> pipeView;
	private Module<A,B> moduleView;
	private EHWithInputAndReturn<A,B> ehView;
	private Producer<B> producer;

	/**
	 * Convenience method that calls {@link #asModule()} on this Processor
	 * and then submits the given item.
	 * @param item
	 * the item to process
	 * @return
	 * this Processor as a {@link Module}, for chaining
	 */
	public Module<A,B> submit(Object item) {
		return asModule().submit(item);
	}
	
	@Override
	public Pipe<A,B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = new Pipe<>(this, true);
		}
		return pipeView;
	}

	@Override
	public Module<A,B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = new Module<>(this);
		}
		return moduleView;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends AbstractDisruptorEventHandler<A> & ProcessorSocket<A,B>> E asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = new EHWithInputAndReturn<>(this);
		}
		return (E) ehView;
	}

	@Override
	public void setProducer(Producer<B> producer) {
		if (producer == null) {
			throw new IllegalStateException("No producer given (null) for " + this.getClass() + ".");
		}
		this.producer = producer;
	}

	@Override
	public Producer<B> getProducer() {
		if (producer == null) {
			throw new IllegalStateException("No producer set for " + this.getClass() + ".");
		} else {
			return producer;
		}
	}

}
