package se.de.hu_berlin.informatik.utils.tracking;

public class SimpleTracker implements ITrackingStrategy {

	private int count = 0;
	
	@Override
	public void track() {
		writeTrackMsg(++count);
	}

}
