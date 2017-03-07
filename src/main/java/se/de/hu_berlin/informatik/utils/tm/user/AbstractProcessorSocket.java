package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.Processor;

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
