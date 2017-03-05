package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractTransmitter<A, B> extends AbstractConsumer<A> implements Transmitter<A, B> {

	private AbstractPipe<A, B> pipeView = null;
	private AbstractModule<A, B> moduleView = null;
	private EHWithInputAndReturn<A, B> ehView = null;
	
//	@Override
//	public void produce(B item) throws UnsupportedOperationException {
//		throw new UnsupportedOperationException("Producing items not implemented for " + this.getClass().getSimpleName() + ".");
//	}

	@Override
	public <C> Consumer<C> linkTo(Consumer<C> consumer) throws IllegalArgumentException, IllegalStateException {
		throw new IllegalStateException("Can not link basic transmitter " + this.getClass().getSimpleName() + ".");
	}

	@Override
	public AbstractPipe<A, B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = newPipeInstance();
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A, B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = newModuleInstance();
		}
		return moduleView;
	}

	@Override
	public EHWithInputAndReturn<A, B> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = newEHInstance();
		}
		return ehView;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking() {
		super.enableTracking();
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(int stepWidth) {
		super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> disableTracking() {
		super.disableTracking();
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(TrackingStrategy tracker) {
		super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(boolean useProgressBar) {
		super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		super.enableTracking(useProgressBar, stepWidth);
		return this;
	}
	
}
