package se.de.hu_berlin.informatik.utils.tracking;

final public class TrackerDummy implements TrackingStrategy {

	final private static TrackingStrategy INSTANCE = new TrackerDummy();
	
	public static TrackingStrategy getInstance() {
		return INSTANCE;
	}
	
	private TrackerDummy() {
		//avoid instantiation, since immutable
	}
	
	@Override
	public void track() {
		//do nothing
	}

	@Override
	public void track(final String msg) {
		//do nothing
	}
	
	@Override
	public void reset() {
		//do nothing
	}

	@Override
	public void writeTrackMsg(final int count) {
		//do nothing
	}

	@Override
	public void writeTrackMsg(final int count, final String msg) {
		//do nothing
	}

	@Override
	public String generateTruncatedMessage(final String msg, final int length) {
		return msg;
	}

}
