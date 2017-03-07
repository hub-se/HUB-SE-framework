package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.Processor;

public abstract class AbstractProcessorUser<A, B> implements ProcessorUser<A, B>, ProcessorUserGenerator<A, B> {

	private Processor<A, B> processor;
	
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
