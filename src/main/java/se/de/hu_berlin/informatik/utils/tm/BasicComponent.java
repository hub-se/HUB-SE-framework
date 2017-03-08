package se.de.hu_berlin.informatik.utils.tm;

import se.de.hu_berlin.informatik.utils.optionparser.OptionCarrier;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;

/**
 * A simple Component that implements {@link Trackable} and {@link OptionCarrier}.
 * 
 * @author Simon
 */
public class BasicComponent implements Trackable, OptionCarrier {

	private OptionParser options;
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	private boolean onlyForced;
	
	@Override
	public OptionParser getOptions() {
		return options;
	}

	@Override
	public BasicComponent setOptions(OptionParser options) {
		this.options = options;
		return this;
	}
	
	@Override
	public boolean hasOptions() {
		return options != null;
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
