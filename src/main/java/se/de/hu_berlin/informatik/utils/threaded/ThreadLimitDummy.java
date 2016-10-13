package se.de.hu_berlin.informatik.utils.threaded;

final public class ThreadLimitDummy implements ThreadLimit {

	final private static ThreadLimit INSTANCE = new ThreadLimitDummy();
	
	public static ThreadLimit getInstance() {
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
