package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tm.ConsumingProcessor;

public abstract class AbstractConsumingProcessorSocket<A> extends AbstractProcessorSocket<A, Object> implements ConsumingProcessorSocket<A>, ConsumingProcessorSocketGenerator<A> {

	public AbstractConsumingProcessorSocket(ConsumingProcessor<A> processor) {
		super(processor);
	}

}
