/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * Provides miscellaneous methods that are useful for various applications. 
 * 
 * @author Simon Heiden
 */
public class Misc {
	
	//suppress default constructor (class should not be instantiated)
	private Misc() {
		throw new AssertionError();
	}

	/**
	 * Generates an array of a generic type that is only known at runtime.
	 * @param clazz
	 * the type of the items in the array
	 * @param arrayLength
	 * the length of the array
	 * @return
	 * the created array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createGenericArray(Class<T> clazz, int arrayLength) {
        return (T[]) Array.newInstance(clazz, arrayLength);
    }
	
	/**
	 * searches for a method with the given name in the given class.
	 * @param target
	 * class in which to search for the method
	 * @param name
	 * identifier of the method to be searched for 
	 * @return
	 * the method or null if no match was found
	 */
	public static Method getMethod(Class<?> target, String name) {
		Method[] mts = target.getDeclaredMethods();

		for (Method m : mts) {
			//String st = m.getName();
			// System.out.println(st + " - " + m);

			if (m.getName().compareTo(name) == 0) {
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Replaces all white spaces (including tabs, new lines, etc.) in a given 
	 * String with a replacement String.
	 * @param aString
	 * a String in which to replace white spaces
	 * @param replaceString
	 * the String to replace the white spaces with
	 * @return
	 * the result String
	 */
	public static String replaceWhitespacesInString(String aString, String replaceString) {
		return replaceNewLinesInString(aString, replaceString)
				.replace(" ", replaceString)
				.replace("\t", replaceString);
	}
	
	/**
	 * Replaces all new lines, carriage returns and form feeds in a given 
	 * String with a replacement String.
	 * @param aString
	 * a String in which to replace white spaces
	 * @param replaceString
	 * the String to replace the white spaces with
	 * @return
	 * the result String
	 */
	public static String replaceNewLinesInString(String aString, String replaceString) {
		return aString
				.replace("\n", replaceString)
				.replace("\r", replaceString)
				.replace("\f", replaceString);
	}
	
	/**
	 * Returns a String representation of the given array
	 * with ',' as separation element and enclosed in rectangular brackets.
	 * @param array
	 * an array
	 * @return
	 * a String representation of the given array
	 * @param <T>
	 * the type of the array
	 */
	public static <T> String arrayToString(T[] array) {
		return arrayToString(array, ",", "[", "]");
	}
	
	/**
	 * Returns a String representation of the given array.
	 * @param array
	 * an array
	 * @param sepElement
	 * a separation element that separates the different elements of
	 * the array in the returned String representation
	 * @param start
	 * a String that marks the begin of the array
	 * @param end
	 * a String that marks the end of the array
	 * @return
	 * a String representation of the given array
	 * @param <T>
	 * the type of the array
	 */
	public static <T> String arrayToString(T[] array, String sepElement, String start, String end) {
		StringBuilder builder = new StringBuilder();
		builder.append(start);
		boolean isFirst = true;
		for (T element : array) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(sepElement);
			}
			builder.append(element);
		}
		builder.append(end);
		
		return builder.toString();
	}
	
	/**
	 * Joins two arrays of type {@code T} and returns the concatenated arrays.
	 * @param a
	 * the first array
	 * @param b
	 * the second array
	 * @return
	 * the concatenation of the two given arrays
	 * @param <T>
	 * the type of the arrays
	 */
	public static <T> T[] joinArrays(T[] a, T[] b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		Class<?> type = a.getClass().getComponentType();
		@SuppressWarnings("unchecked")
		T[] joinedArray = (T[]) Array.newInstance(type, a.length + b.length);
		System.arraycopy(a, 0, joinedArray, 0, a.length);
		System.arraycopy(b, 0, joinedArray, a.length, b.length);
		return joinedArray;
	}
	
	/**
	 * Adds the given item to the end of the given 
	 * array of type {@code T}.
	 * @param a
	 * the first array
	 * @param items
	 * item to append to the array
	 * @return
	 * the array with the given item appended
	 * @param <T>
	 * the type of the arrays
	 */
	@SafeVarargs
	public static <T> T[] addToArrayAndReturnResult(T[] a, T... items) {
		if (items == null || items.length == 0) {
			return a;
		}
		if (a == null) {
			Class<?> type = items[0].getClass();
			@SuppressWarnings("unchecked")
			T[] array = (T[]) Array.newInstance(type, items.length);
			System.arraycopy(items, 0, array, 0, items.length);
			return array;
		}
		Class<?> type = a.getClass().getComponentType();
		@SuppressWarnings("unchecked")
		T[] joinedArray = (T[]) Array.newInstance(type, a.length + items.length);
		System.arraycopy(a, 0, joinedArray, 0, a.length);
		System.arraycopy(items, 0, joinedArray, a.length, items.length);
		return joinedArray;
	}
	
	/**
	 * Blocks further execution until the given thread is dead.
	 * @param thread
	 * the thread to wait on
	 */
	public static void waitOnThread(Thread thread) {
		while (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				//do nothing
			}
		}
	}
	
	/**
	 * Converts a wrapper object array to its corresponding 
	 * simple type array.
	 * @param oBytes
	 * the wrapper object array
	 * @return
	 * the corresponding simple type array
	 */
	public static byte[] toPrimitives(Byte[] oBytes)
	{
	    byte[] bytes = new byte[oBytes.length];

	    for(int i = 0; i < oBytes.length; i++) {
	        bytes[i] = oBytes[i];
	    }

	    return bytes;
	}
	
	/**
	 * Converts a wrapper object array to its corresponding 
	 * simple type array.
	 * @param oIntegers
	 * the wrapper object array
	 * @return
	 * the corresponding simple type array
	 */
	public static int[] toPrimitives(Integer[] oIntegers)
	{
	    int[] integers = new int[oIntegers.length];

	    for(int i = 0; i < oIntegers.length; i++) {
	        integers[i] = oIntegers[i];
	    }

	    return integers;
	}

	
	
}
