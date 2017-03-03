package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.EHWithInputAndReturn;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractTransmitter<A, B> implements Transmitter<A, B>, Trackable {

	private AbstractPipe<A, B> pipeView = null;
	private AbstractModule<A, B> moduleView = null;
	private EHWithInputAndReturn<A, B> ehView = null;
	private OptionParser options;
	
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	private boolean onlyForced;
	
	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public Transmitter<A, B> setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
	}

	@Override
	public <C, D> Transmitter<C, D> linkTo(Transmitter<C, D> transmitter)
			throws IllegalArgumentException, IllegalStateException {
		throw new IllegalStateException("Can not link basic transmitter.");
	}

	@Override
	public AbstractPipe<A, B> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = new AbstractPipe<A,B>(true) {
				@Override
				public B processItem(A item) {
					return AbstractTransmitter.this.processItem(item);
				}
			};
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A, B> asModule() throws UnsupportedOperationException {
		if (moduleView == null) {
			moduleView = new AbstractModule<A, B>(true) {
				@Override
				public B processItem(A item) {
					return AbstractTransmitter.this.processItem(item);
				}
			};
		}
		return moduleView;
	}

	@Override
	public EHWithInputAndReturn<A, B> asEH() throws UnsupportedOperationException {
		if (ehView == null) {
			ehView = new EHWithInputAndReturn<A,B>() {
				@Override
				public B processInput(A input) {
					return AbstractTransmitter.this.processItem(input);
				}
				@Override
				public void resetAndInit() {
					//do nothing
				}
			};
		}
		return ehView;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking() {
		Trackable.super.enableTracking();
		return this;
	}

	@Override
	public AbstractTransmitter<A, B> enableTracking(int stepWidth) {
		Trackable.super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> disableTracking() {
		Trackable.super.disableTracking();
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(TrackingStrategy tracker) {
		Trackable.super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(boolean useProgressBar) {
		Trackable.super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractTransmitter<A,B> enableTracking(boolean useProgressBar, int stepWidth) {
		Trackable.super.enableTracking(useProgressBar, stepWidth);
		return this;
	}
	
	@Override
	public TrackingStrategy getTracker() {
		return tracker;
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		this.tracker = tracker;
	}
	
	@Override
	public boolean onlyForced() {
		return onlyForced ;
	}

	@Override
	public void allowOnlyForcedTracks() {
		onlyForced = true;
	}
}
