/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides miscellaneous methods that are useful for various applications. 
 * 
 * @author Simon Heiden
 */
final public class Misc {
	
	//suppress default constructor (class should not be instantiated)
	private Misc() {
		throw new AssertionError();
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
	public static Method getMethod(
			final Class<?> target, final String name) {
		final Method[] mts = target.getDeclaredMethods();

		for (final Method m : mts) {
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
	public static String replaceWhitespacesInString(
			final String aString, final String replaceString) {
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
	public static String replaceNewLinesInString(
			final String aString, final String replaceString) {
		return aString
				.replace("\n", replaceString)
				.replace("\r", replaceString)
				.replace("\f", replaceString);
	}
	
	/**
	 * Returns a String representation of the given list
	 * with ',' as separation element and enclosed in parantheses.
	 * @param list
	 * a list
	 * @return
	 * a String representation of the given list
	 * @param <T>
	 * the type of the list elements
	 */
	public static <T> String listToString(final List<T> list) {
		return listToString(list, ", ", "( ", " )");
	}
	
	/**
	 * Returns a String representation of the given list.
	 * @param list
	 * a list
	 * @param sepElement
	 * a separation element that separates the different elements of
	 * the list in the returned String representation
	 * @param start
	 * a String that marks the begin of the list
	 * @param end
	 * a String that marks the end of the list
	 * @return
	 * a String representation of the given list
	 * @param <T>
	 * the type of the list elements
	 */
	public static <T> String listToString(
			final List<T> list, final String sepElement, 
			final String start, final String end) {
		final StringBuilder builder = new StringBuilder();
		builder.append(start);
		boolean isFirst = true;
		for (final T element : list) {
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
	 * Returns a String representation of the given array
	 * with ',' as separation element and enclosed in rectangular brackets.
	 * @param array
	 * an array
	 * @return
	 * a String representation of the given array
	 * @param <T>
	 * the type of the array elements
	 */
	public static <T> String arrayToString(final T[] array) {
		return arrayToString(array, ", ", "[ ", " ]");
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
	 * the type of the array elements
	 */
	public static <T> String arrayToString(
			final T[] array, final String sepElement, 
			final String start, final String end) {
		final StringBuilder builder = new StringBuilder();
		builder.append(start);
		boolean isFirst = true;
		for (final T element : array) {
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
	 * Returns a String representation of the given array
	 * within the specified indices {@code from} and {@code to}.
	 * @param array
	 * an array
	 * @param sepElement
	 * a separation element that separates the different elements of
	 * the array in the returned String representation
	 * @param start
	 * a String that marks the begin of the array
	 * @param end
	 * a String that marks the end of the array
	 * @param from
	 * the start index
	 * @param to
	 * the end index
	 * @return
	 * a String representation of the given array from index {@code from}
	 * to index {@code from-1}
	 * @param <T>
	 * the type of the array elements
	 */
	public static <T> String arrayToString(T[] array, String sepElement, 
			final String start, final String end, 
			int from, int to) {
		if (from < 0) {
			from = 0;
		}
		if (to > array.length) {
			to = array.length;
		}
		final StringBuilder builder = new StringBuilder();
		builder.append(start);
		boolean isFirst = true;
		for (int i = from; i < to; ++i) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(sepElement);
			}
			builder.append(array[i]);
		}
		builder.append(end);
		
		return builder.toString();
	}
	
	/**
	 * Returns a String representation of the elements in the given enum class
	 * with ',' as separation element and enclosed in rectangular brackets.
	 * @param enumClass
	 * an enum class
	 * @return
	 * a String representation of the given enum class
	 * @param <T>
	 * the type of the enum class
	 */
	public static <T extends Enum<T>> String enumToString(final Class<T> enumClass) {
		return enumToString(enumClass, ", ", "[ ", " ]");
	}
	
	/**
	 * Returns a String representation of the elements in the given enum class.
	 * @param enumClass
	 * an enum class
	 * @param sepElement
	 * a separation element that separates the different elements of
	 * the enum class in the returned String representation
	 * @param start
	 * a String that marks the begin of the enum class
	 * @param end
	 * a String that marks the end of the enum class
	 * @return
	 * a String representation of the given enum class
	 * @param <T>
	 * the type of the enum class
	 */
	public static <T extends Enum<T>> String enumToString(
			final Class<T> enumClass, final String sepElement, 
			final String start, final String end) {
		final StringBuilder builder = new StringBuilder();
		builder.append(start);
		boolean isFirst = true;
		EnumSet<T> set = EnumSet.allOf(enumClass);
		for (final T element : set) {
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
	 * Obtains the enum from the given class that has an identical {@link #toString()}
	 * value as the given String. Will ignore upper and lower case.
	 * @param enumClass
	 * the enum class
	 * @param request
	 * the String for which to retrieve an enum for
	 * @return
	 * the respective enum if successful; null otherwise
	 * @param <T>
	 * the type of the enum class
	 */
	public static <T extends Enum<T>> T getEnumFromToString(Class<T> enumClass, String request) {
		if (request == null) {
			return null;
		}
		request = request.toLowerCase(Locale.getDefault());
		EnumSet<T> set = EnumSet.allOf(enumClass);
		for (final T element : set) {
			if (element.toString().toLowerCase(Locale.getDefault()).equals(request)) {
				return element;
			}
		}
		return null;
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
	public static <T> T[] joinArrays(final T[] a, final T[] b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		final Class<?> type = a.getClass().getComponentType();
		@SuppressWarnings("unchecked")
		final T[] joinedArray = createGenericArray((Class<T>)type, a.length + b.length);
		System.arraycopy(a, 0, joinedArray, 0, a.length);
		System.arraycopy(b, 0, joinedArray, a.length, b.length);
		return joinedArray;
	}
	
	/**
	 * Adds the given items to the end of the given 
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
	public static <T> T[] addToArrayAndReturnResult(final T[] a, final T... items) {
		return joinArrays(a, items);
	}
	
	/**
	 * Creates an array of type {@code T} with the given items.
	 * @param items
	 * item to append to the array
	 * @return
	 * the array with the given items
	 * @param <T>
	 * the type of the arrays
	 */
	@SafeVarargs
	public static <T> T[] createArrayFromItems(T... items) {
		return items;
	}
	
	/**
	 * Generates an array of a generic type that is only known at runtime.
	 * @param clazz
	 * the type of the items in the array
	 * @param arrayLength
	 * the length of the array
	 * @return
	 * the created array
	 * @param <T>
	 * the type of the items in the array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createGenericArray(final Class<T> clazz, final int arrayLength) {
        return (T[]) Array.newInstance(clazz, arrayLength);
    }
	
	/**
	 * Blocks further execution until the given thread is dead.
	 * @param thread
	 * the thread to wait on
	 */
	public static void waitOnThread(final Thread thread) {
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
	public static byte[] toPrimitives(final Byte[] oBytes)
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

	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByKey(/*Collections.reverseOrder()*/))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	public static <K, V extends Comparable<? super V>> List<V> sortByValueToValueList(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Comparator.comparing(Map.Entry::getValue))
	              .map(Map.Entry::getValue)
	              .collect(Collectors.toList());
	}
	
	public static <K, V extends Comparable<? super V>> List<K> sortByValueToKeyList(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Comparator.comparing(Map.Entry::getValue))
	              .map(Map.Entry::getKey)
	              .collect(Collectors.toList());
	}
	
	public static <K extends Comparable<? super K>, V> List<V> sortByKeyToValueList(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Comparator.comparing(Map.Entry::getKey))
	              .map(Map.Entry::getValue)
	              .collect(Collectors.toList());
	}
	
	public static <K extends Comparable<? super K>, V> List<K> sortByKeyToKeyList(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Comparator.comparing(Map.Entry::getKey))
	              .map(Map.Entry::getKey)
	              .collect(Collectors.toList());
	}
	
	/**
	 * Bitwise-OR each ordinal value together to encode the given EnumSet as a single integer.
	 * @param set
	 * the set to encode
	 * @return
	 * the encoded enum set as an integer
	 * @param <E>
	 * the enum type
	 */
	public static <E extends Enum<E>> int encode(EnumSet<E> set) {
		int result = 0;
		for (E val : set) {
			result |= (1 << val.ordinal());
		}
		return result;
	}

	/**
	 * Get an EnumSet of the given class from the given integer, assuming the integer
	 * is the result of calling {@link #encode(EnumSet)} on an EnumSet of the given class.
	 * If the Enum has changed in between encoding and decoding, this method will
	 * not return correct results.
	 * @param encoded
	 * the integer
	 * @param enumKlazz
	 * the associated Enum class
	 * @return
	 * the respective decoded EnumSet
	 * @param <E>
	 * the enum type
	 */
	public static <E extends Enum<E>> EnumSet<E> decode(int encoded, Class<E> enumKlazz) {
		E[] values = enumKlazz.getEnumConstants();
        EnumSet<E> result = EnumSet.noneOf(enumKlazz);
        while (encoded != 0) {
            int ordinal = Integer.numberOfTrailingZeros(encoded);
            encoded ^= Integer.lowestOneBit(encoded);
            result.add(values[ordinal]);
        }
        return result;
	}
	
}
