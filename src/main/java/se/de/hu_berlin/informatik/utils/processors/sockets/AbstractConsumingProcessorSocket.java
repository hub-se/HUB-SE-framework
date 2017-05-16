package se.de.hu_berlin.informatik.utils.processors.sockets;

import se.de.hu_berlin.informatik.utils.processors.BasicComponent;
import se.de.hu_berlin.informatik.utils.processors.ConsumingProcessor;
import se.de.hu_berlin.informatik.utils.processors.Processor;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractConsumingProcessorSocket<A> extends BasicComponent implements ConsumingProcessorSocket<A>, ConsumingProcessorSocketGenerator<A> {

	private Processor<A, Object> processor;
	
	public AbstractConsumingProcessorSocket(ConsumingProcessor<A> processor) {
		super();
		insert(processor);
	}

	@Override
	public Processor<A, Object> getProcessor() throws IllegalStateException {
		if (processor == null) {
			throw new IllegalStateException("No processor set for " + this.getClass().getSimpleName() + ".");
		} else {
			return processor;
		}
	}

	@Override
	public void setProcessor(Processor<A, Object> consumer) {
		this.processor = consumer;
	}

	@Override
	public AbstractConsumingProcessorSocket<A> enableTracking() {
		super.enableTracking();
		return this;
	}

	@Override
	public AbstractConsumingProcessorSocket<A> enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractConsumingProcessorSocket<A> disableTracking() {
		super.disableTracking();
		return this;
	}
	
	@Override
	public AbstractConsumingProcessorSocket<A> allowOnlyForcedTracks() {
		super.allowOnlyForcedTracks();
		return this;
	}

	@Override
	public AbstractConsumingProcessorSocket<A> enableTracking(TrackingStrategy tracker) {
		super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractConsumingProcessorSocket<A> enableTracking(boolean useProgressBar) {
		super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractConsumingProcessorSocket<A> enableTracking(boolean useProgressBar, int stepWidth) {
		super.enableTracking(useProgressBar, stepWidth);
		return this;
	}
	
}
