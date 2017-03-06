package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.Processor;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractProcessorUser<A, B> extends AbstractConsumingProcessorUser<A> implements ProcessorUser<A, B>, ProcessorUserGenerator<A, B> {

	private Processor<A, B> processor;
	
	private AbstractPipe<A,B> pipeView;
	private AbstractModule<A,B> moduleView;
	private EHWithInputAndReturn<A,B> ehView;

	@Override
	public AbstractPipe<A,B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = newPipeInstance();
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A,B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = newModuleInstance();
		}
		return moduleView;
	}

	@Override
	public EHWithInputAndReturn<A,B> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = newEHInstance();
		}
		return ehView;
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
	

//	@Override
//	public AbstractPipe<A, B> asPipe() throws UnsupportedOperationException {
//		if (pipeView == null) {
//			pipeView = newPipeInstance();
//		}
//		return pipeView;
//	}
//
//	@Override
//	public AbstractModule<A, B> asModule() throws UnsupportedOperationException {
//		if (moduleView == null) {
//			moduleView = newModuleInstance();
//		}
//		return moduleView;
//	}
//
//	@Override
//	public EHWithInputAndReturn<A, B> asEH() throws UnsupportedOperationException {
//		if (ehView == null) {
//			ehView = newEHInstance();
//		}
//		return ehView;
//	}

	@Override
	public AbstractProcessorUser<A,B> enableTracking() {
		super.enableTracking();
		return this;
	}

	@Override
	public AbstractProcessorUser<A,B> enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractProcessorUser<A,B> disableTracking() {
		super.disableTracking();
		return this;
	}

	@Override
	public AbstractProcessorUser<A,B> enableTracking(TrackingStrategy tracker) {
		super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractProcessorUser<A,B> enableTracking(boolean useProgressBar) {
		super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractProcessorUser<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		super.enableTracking(useProgressBar, stepWidth);
		return this;
	}
	
}
