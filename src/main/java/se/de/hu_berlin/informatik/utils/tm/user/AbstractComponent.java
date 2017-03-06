package se.de.hu_berlin.informatik.utils.tm.user;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

public abstract class AbstractComponent implements Trackable, OptionCarrier {

	private OptionParser options;
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	private boolean onlyForced;
	
	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public AbstractComponent setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
	}

	@Override
	public AbstractComponent enableTracking() {
		Trackable.super.enableTracking();
		return this;
	}

	@Override
	public AbstractComponent enableTracking(int stepWidth) {
		Trackable.super.enableTracking(stepWidth);
		return this;
	}

	@Override
	public AbstractComponent disableTracking() {
		Trackable.super.disableTracking();
		return this;
	}

	@Override
	public AbstractComponent enableTracking(TrackingStrategy tracker) {
		Trackable.super.enableTracking(tracker);
		return this;
	}

	@Override
	public AbstractComponent enableTracking(boolean useProgressBar) {
		Trackable.super.enableTracking(useProgressBar);
		return this;
	}

	@Override
	public AbstractComponent enableTracking(boolean useProgressBar, int stepWidth) {
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
