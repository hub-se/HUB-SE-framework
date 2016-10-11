package se.de.hu_berlin.informatik.utils.threaded;

public interface IThreadLimit {
	
	public void acquireSlot();
	
	public void releaseSlot();
	
}
