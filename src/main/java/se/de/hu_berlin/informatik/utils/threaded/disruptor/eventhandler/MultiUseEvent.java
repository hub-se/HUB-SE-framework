package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A wrapper for elements to be processed by a disruptor...
 * 
 * @author Simon
 *
 * @param <T>
 * the type of elements
 */
public class MultiUseEvent<T> implements Event<T> {
	
	private T item;
	
	private AtomicBoolean isFirstAccess = new AtomicBoolean(true);

	@Override
	public void set(T item) {
		this.item = item;
		isFirstAccess.set(true);
	}

	@Override
	public T get() {
		return item;
	}
	
	/**
	 * Tells whether this event hasn't been processed by any handler yet. This method
	 * is ensured to return true only once for every new element. If this method
	 * returns true to a handler, then this handler has to process this event.
	 * @return
	 * whether the wrapped element has not yet been accessed
	 */
	public boolean isFirstAccess() {
		if (isFirstAccess.get()) {
			return isFirstAccess.compareAndSet(true, false);
		} else {
			return false;
		}
	}

}
