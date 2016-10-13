package se.de.hu_berlin.informatik.utils.tracking;

public class SimpleTracker implements TrackingStrategy {

	private int count = 0;
	
	@Override
	public void track() {
		writeTrackMsg(++count);
	}

	@Override
	public void track(String msg) {
		writeTrackMsg(++count, msg);
	}
	
	@Override
	public void reset() {
		count = 0;
	}

}
