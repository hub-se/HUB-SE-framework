/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipeframework;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * Simple wrapper class for a {@link LinkedBlockingQueue} that
 * implements the {@link IProvider} interface. Submitting and 
 * getting items to/from the provider blocks until the corresponding 
 * action is possible.
 * 
 * <br><br> If the provider is marked as done (shut down) and the 
 * underlying queue is empty, waiting pipes will retrieve a null object
 * as a signal that execution has finished. This also means, that
 * submitted null objects will be ignored and will not be further
 * processed.
 * 
 * @author Simon Heiden
 *
 * @param <A>
 * type of the items that the provider may hold
 * 
 * @see IProvider
 */
public class SynchronizedProvider<A> implements IProvider<A, APipe<?,?>> {

	final private BlockingQueue<A> queue;
	private boolean done = false;
	
	private boolean hasElement = false;
	
	/**
	 * Creates a blocking provider with the given size.
	 * @param size
	 * the size of the underlying queue
	 */
	public SynchronizedProvider(int size) {
		this.queue = new LinkedBlockingQueue<A>(size);
	}
	
	/**
	 * Submits an item to the provider.
	 * @param item
	 * the item to be submitted
	 */
	@SuppressWarnings("unchecked")
	public synchronized void submit(Object item) {
		try {
			while (!queue.offer((A)item)) {
				//wait until there is free space available
				try {
					wait();
				} catch (InterruptedException e) {
					//do nothing
				}
			}
			hasElement = true;
			notifyAll();
		} catch (ClassCastException e) {
			Misc.abort(this, e, "Type mismatch while submitting item.");
		} catch (NullPointerException e) {
			Misc.err(this, "Submitted item was null.");
		}
	}
	
	/**
	 * Gets an item from the provider. Returns null if all execution
	 * has finished (on shutdown).
	 * @return
	 * the item
	 */
	public synchronized A get() {
		A item;
		while ((item = queue.poll()) == null) {
			hasElement = false;
			while (!hasElement && !isProviderDone()) {
				//wait for an element to be available
				try {
					wait();
				} catch (InterruptedException e) {
					//do nothing
				}				
			}
			if (isProviderDone()) {
				return null;
			}
		}
		//notify any waiting submitters that there is free space again
		notifyAll();
		return item;
	}

	/**
	 * @return
	 * if the provider is marked as done and also empty
	 */
	public boolean isProviderDone() {
		return done && isEmpty();
	}
	
	/**
	 * Marks the provider as done and notifies possibly
	 * waiting threads that we are possibly done.
	 */
	public synchronized void setProviderDone() {
		done = true;
		notifyAll();
	}
	
	/**
	 * Marks the provider as "not done".
	 */
	public synchronized void setProviderWorking() {
		done = false;
	}
	
	/**
	 * @return
	 * if the provider contains no items.
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	/**
	 * Waits for the complete shutdown of this pipe.
	 */
	public synchronized void waitForShutdown() {
		while (!isProviderDone()) {
			try {
				wait();
			} catch (InterruptedException e1) {
				// do nothing
			}
		}
	}
}
