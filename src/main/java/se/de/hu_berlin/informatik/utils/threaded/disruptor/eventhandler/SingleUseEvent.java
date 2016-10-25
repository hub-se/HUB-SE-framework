package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

/**
 * A wrapper for elements to be processed by a disruptor...
 * 
 * @author Simon
 *
 * @param <T>
 * the type of elements
 */
public class SingleUseEvent<T> extends MultiUseEvent<T> {
	
	/**
	 * Gets the element contained in this event. Nullifies the reference to
	 * the contained element in the process, such that the element may be 
	 * collected by the GC afterwards.
	 * @return
	 * the contained element
	 */
	@Override
	public T get() {
		T temp = super.get();
		super.setWithoutValidation(null);
		return temp;
	}

}
