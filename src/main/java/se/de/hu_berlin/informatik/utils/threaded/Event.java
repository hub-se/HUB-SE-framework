package se.de.hu_berlin.informatik.utils.threaded;

import com.lmax.disruptor.EventFactory;


/**
 * A wrapper for elements to be processed by a pipe. This is
 * needed for the Disruptor framework to work...
 * 
 * @author Simon
 *
 * @param <T>
 * the type of elements
 */
public class Event<T> {
	
	private T value;

	public void set(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}

	public class LongEventFactory implements EventFactory<Event<T>> {
		public Event<T> newInstance() {
			return new Event<>();
		}
	}

	public static <T> void translate(Event<T> event, long sequence, T value) {
		event.set(value);
	}

}
