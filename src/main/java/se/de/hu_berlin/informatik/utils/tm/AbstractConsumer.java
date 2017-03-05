package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.DisruptorFCFSEventHandler;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AbstractModule;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.AbstractPipe;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractConsumer<A> implements Consumer<A>, Trackable {

	private AbstractPipe<A, ?> pipeView = null;
	private AbstractModule<A, ?> moduleView = null;
	private DisruptorFCFSEventHandler<A> ehView = null;
	
	private OptionParser options;
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	private boolean onlyForced;
	
	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public Consumer<A> setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
	}

	@Override
	public AbstractPipe<A, ?> asPipe() throws UnsupportedOperationException {
		if (pipeView == null) {
			pipeView = newPipeInstance();
		}
		return pipeView;
	}

	@Override
	public AbstractModule<A, ?> asModule() throws UnsupportedOperationException {
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
	public AbstractConsumer<A> enableTracking() {
		Trackable.super.enableTracking();
		return this;
	}

	@Override
	public AbstractConsumer<A> enableTracking(int stepWidth) {
		Trackable.super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractConsumer<A> disableTracking() {
		Trackable.super.disableTracking();
		return this;
	}

	@Override
	public AbstractConsumer<A> enableTracking(TrackingStrategy tracker) {
		Trackable.super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractConsumer<A> enableTracking(boolean useProgressBar) {
		Trackable.super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractConsumer<A> enableTracking(boolean useProgressBar, int stepWidth) {
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
