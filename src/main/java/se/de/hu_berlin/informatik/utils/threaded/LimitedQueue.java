/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of a blocking queue to be used by, for example,
 * a {@link java.util.concurrent.ExecutorService}. 
 * 
 * @author Simon Heiden
 */
public class LimitedQueue<E> extends LinkedBlockingQueue<E> 
{
	
	private static final long serialVersionUID = 1226149687909586427L;

	/**
	 * Creates a {@link LimitedQueue} with the given maximum size.
	 * @param maxSize
	 * is the maximum size of the queue.
	 */
	public LimitedQueue(int maxSize)
	{
		super(maxSize);
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.LinkedBlockingQueue#offer(java.lang.Object)
	 */
	@Override
	public boolean offer(E e)
	{
		// turn offer() and add() into blocking calls (unless interrupted)
		try {
			put(e);
			return true;
		} catch(InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
		return false;
	}

}
