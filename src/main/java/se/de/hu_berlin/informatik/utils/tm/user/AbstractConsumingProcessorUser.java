package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractConsumingProcessorUser<A> extends AbstractProcessorUser<A, Object> implements ConsumingProcessorUser<A>, ConsumingProcessorUserGenerator<A> {

	@Override
	public AbstractConsumingProcessorUser<A> enableTracking() {
		super.enableTracking();
		return this;
	}

	@Override
	public AbstractConsumingProcessorUser<A> enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractConsumingProcessorUser<A> disableTracking() {
		super.disableTracking();
		return this;
	}

	@Override
	public AbstractConsumingProcessorUser<A> enableTracking(TrackingStrategy tracker) {
		super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractConsumingProcessorUser<A> enableTracking(boolean useProgressBar) {
		super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractConsumingProcessorUser<A> enableTracking(boolean useProgressBar, int stepWidth) {
		super.enableTracking(useProgressBar, stepWidth);
		return this;
	}
	
}
