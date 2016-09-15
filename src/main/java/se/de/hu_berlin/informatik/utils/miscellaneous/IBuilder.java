package se.de.hu_berlin.informatik.utils.miscellaneous;

/**
 * Interface for a builder of some kind of object.
 * 
 * @author Simon
 *
 * @param <T>
 * the type of the object to build
 */
public interface IBuilder<T> {

	public T build();
	
}
