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
public class Event<T> {
	
	private T item;
	
	private AtomicBoolean isFirstAccess = new AtomicBoolean(true);

	/**
	 * Sets this event's contained element to the given item.
	 * @param item
	 * the item that shall be handled by the disruptor
	 */
	public void set(T item) {
		this.item = item;
	}

	/**
	 * Gets the element contained in this event.
	 * @return
	 * the contained element
	 */
	public T get() {
		return item;
	}

	/**
	 * Translation method that sets the given event's contained element to the
	 * given item.
	 * @param event
	 * the event
	 * @param sequence
	 * the event's sequence
	 * @param item
	 * the item to wrap in the event
	 * @param <T>
	 * the type of the items
	 */
	public static <T> void translate(Event<T> event, long sequence, T item) {
		event.set(item);
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
