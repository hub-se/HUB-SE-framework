package se.de.hu_berlin.informatik.utils.miscellaneous;

public interface FromString<T> {

	public T getFromString(String identifier) throws IllegalArgumentException;
	
}
