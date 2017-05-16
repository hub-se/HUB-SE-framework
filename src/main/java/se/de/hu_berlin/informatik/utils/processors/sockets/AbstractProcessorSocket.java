package se.de.hu_berlin.informatik.utils.processors.sockets;

import se.de.hu_berlin.informatik.utils.processors.BasicComponent;
import se.de.hu_berlin.informatik.utils.processors.Processor;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

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
public abstract class AbstractProcessorSocket<A, B> extends BasicComponent implements ProcessorSocket<A, B>, ProcessorSocketGenerator<A, B> {

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
	
	@Override
	public AbstractProcessorSocket<A, B> enableTracking() {
		super.enableTracking();
		return this;
	}

	@Override
	public AbstractProcessorSocket<A, B> enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractProcessorSocket<A, B> disableTracking() {
		super.disableTracking();
		return this;
	}
	
	@Override
	public AbstractProcessorSocket<A, B> allowOnlyForcedTracks() {
		super.allowOnlyForcedTracks();
		return this;
	}

	@Override
	public AbstractProcessorSocket<A, B> enableTracking(TrackingStrategy tracker) {
		super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractProcessorSocket<A, B> enableTracking(boolean useProgressBar) {
		super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractProcessorSocket<A, B> enableTracking(boolean useProgressBar, int stepWidth) {
		super.enableTracking(useProgressBar, stepWidth);
		return this;
	}

}
