package se.de.hu_berlin.informatik.utils.threaded;

public interface ThreadLimit {
	
	public void acquireSlot();
	
	public void releaseSlot();
	
}
