package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.Semaphore;

public class ThreadLimit implements IThreadLimit {

	final private Semaphore threads;
	
	public ThreadLimit(int threads, boolean fair) {
		this.threads = new Semaphore(threads, fair);
	}
	
	public ThreadLimit(int threads) {
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
