package se.de.hu_berlin.informatik.utils.threaded;


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
	 */
	public static <T> void translate(Event<T> event, long sequence, T item) {
		event.set(item);
	}

}
