package se.de.hu_berlin.informatik.utils.miscellaneous;

final public class SystemUtils {
	
	//suppress default constructor (class should not be instantiated)
	private SystemUtils() {
		throw new AssertionError();
	}

	public static long getFreeMem() {
		return Runtime.getRuntime().freeMemory();
	}
	
	public static long getMaxMem() {
		return Runtime.getRuntime().maxMemory();
	}
	
	public static long getTotalMem() {
		return Runtime.getRuntime().totalMemory();
	}
	
	public static long getTotalFreeMem() {
		final Runtime runtime = Runtime.getRuntime();

		final long maxMemory = runtime.maxMemory();
		final long allocatedMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();
		
		return freeMemory + (maxMemory - allocatedMemory);
	}
}
