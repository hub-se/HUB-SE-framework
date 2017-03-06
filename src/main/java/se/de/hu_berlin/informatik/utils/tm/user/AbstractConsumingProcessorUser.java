package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.tm.ConsumingProcessor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractConsumingProcessorUser<A> extends AbstractComponent implements ConsumingProcessorUser<A>, ConsumingProcessorUserGenerator<A> {

	private ConsumingProcessor<A> processor;

	private AbstractPipe<A,?> pipeView;
	private AbstractModule<A,?> moduleView;
	private DisruptorFCFSEventHandler<A> ehView;

	@Override
	public AbstractPipe<A,?> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = newPipeInstance();
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A,?> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = newModuleInstance();
		}
		return moduleView;
	}

	@Override
	public DisruptorFCFSEventHandler<A> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = newEHInstance();
		}
		return ehView;
	}
	
	@Override
	public ConsumingProcessor<A> getProcessor() throws IllegalStateException {
		if (processor == null) {
			throw new IllegalStateException("No processor set for " + this.getClass().getSimpleName() + ".");
		} else {
			return processor;
		}
	}

	@Override
	public void setProcessor(ConsumingProcessor<A> consumer) {
		this.processor = consumer;
	}
	
	public void trackAndConsume(A item) {
		track();
		consume(item);
	}
	
//	@Override
//	public AbstractPipe<A, ?> asPipe() throws UnsupportedOperationException {
//		if (pipeView == null) {
//			pipeView = newPipeInstance();
//		}
//		return pipeView;
//	}
//
//	@Override
//	public AbstractModule<A, ?> asModule() throws UnsupportedOperationException {
//		if (moduleView == null) {
//			moduleView = newModuleInstance();
//		}
//		return moduleView;
//	}
//
//	@Override
//	public DisruptorFCFSEventHandler<A> asEH() throws UnsupportedOperationException {
//		if (ehView == null) {
//			ehView = newEHInstance();
//		}
//		return ehView;
//	}

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
