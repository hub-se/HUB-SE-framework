package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.Processor;

/**
 * Basic implementation of {@link ProcessorSocket} and {@link ProcessorSocketGenerator}.
 * 
 * @author Simon
 *
 * @param <A>
 * is the type of the input objects
 * @param <B>
 * is the type of the output objects
 */
public abstract class AbstractProcessorSocket<A, B> implements ProcessorSocket<A, B>, ProcessorSocketGenerator<A, B> {

	private Processor<A, B> processor;
	
	public AbstractProcessorSocket(Processor<A, B> processor) {
		super();
		insert(processor);
	}

	@Override
	public Processor<A, B> getProcessor() throws IllegalStateException {
		if (processor == null) {
			throw new IllegalStateException("No processor set for " + this.getClass().getSimpleName() + ".");
		} else {
			return processor;
		}
	}

	@Override
	public void setProcessor(Processor<A, B> consumer) {
		this.processor = consumer;
	}
	
}
