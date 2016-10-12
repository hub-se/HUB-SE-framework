package se.de.hu_berlin.informatik.utils.tracking;

public class TrackerDummy implements ITrackingStrategy {

	private static ITrackingStrategy INSTANCE = new TrackerDummy();
	
	public static ITrackingStrategy getInstance() {
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
	public void track(String msg) {
		//do nothing
	}
	
	@Override
	public void reset() {
		//do nothing
	}

	@Override
	public void writeTrackMsg(int count) {
		//do nothing
	}

	@Override
	public void writeTrackMsg(int count, String msg) {
		//do nothing
	}

	@Override
	public String generateTruncatedMessage(String msg, int length) {
		return msg;
	}

}
