package se.de.hu_berlin.informatik.utils.threaded;

public class ThreadLimitDummy implements IThreadLimit {

	private static IThreadLimit INSTANCE = new ThreadLimitDummy();
	
	public static IThreadLimit getInstance() {
		return INSTANCE;
	}
	
	private ThreadLimitDummy() {
		//avoid instantiation, since immutable
	}
	
	@Override
	public void acquireSlot() {
		//do nothing
	}
	
	@Override
	public void releaseSlot() {
		//do nothing
	}
	
}
