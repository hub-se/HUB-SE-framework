package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Semaphore;

public class SemaphoreThreadLimit implements ThreadLimit {

	final private Semaphore threads;
	
	public SemaphoreThreadLimit(int threads, boolean fair) {
		this.threads = new Semaphore(threads, fair);
	}
	
	public SemaphoreThreadLimit(int threads) {
		this.threads = new Semaphore(threads);
	}
	
	@Override
	public void acquireSlot() {
		threads.acquireUninterruptibly();
	}
	
	@Override
	public void releaseSlot() {
		threads.release();
	}
	
}
