/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides miscellaneous methods that are useful for various applications. 
 * 
 * @author Simon Heiden
 */
public class Misc {
	
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
	 * Deletes the given file or directory (recursively).
	 * @param fileOrDir
	 * a file or a directory
	 * @return
	 * true if and only if the file or directory is successfully deleted; false otherwise
	 */
	public static boolean delete(File fileOrDir) {
		if (fileOrDir.isDirectory()) {
			try {
				for (File file : fileOrDir.listFiles()) {
					delete(file);
				}
			} catch(NullPointerException e) {
				Log.err(null, "Could not delete " + fileOrDir.toString() + ".");
			}
		}
		return fileOrDir.delete();
	}
	
	/**
	 * Deletes the given file or directory (recursively).
	 * @param fileOrDir
	 * a file or a directory
	 * @return
	 * true if and only if the file or directory is successfully deleted; false otherwise
	 */
	public static boolean delete(Path fileOrDir) {
		return delete(fileOrDir.toFile());
	}
	
	/**
	 * Copies a file or a directory recursively.
	 * @param source
	 * the source file or directory
	 * @param dest
	 * the destination file or directory
	 * @param options
	 * the copy options
	 * @throws IOException
	 * thrown in case of an error
	 */
	public static void copyFileOrDir(File source, File dest, CopyOption...  options) throws IOException {
	    if (source.isDirectory())
	        copyDir(source, dest, options);
	    else {
	        ensureParentDir(dest);
	        copyFile(source, dest, options);
	    }
	}

	/**
	 * Copies a directory. 
	 * @param source
	 * the source directory
	 * @param dest
	 * the destination directory
	 * @param options
	 * the copy options
	 * @throws IOException
	 * thrown in case of an error
	 */
	private static void copyDir(File source, File dest, CopyOption... options) throws IOException {
	    if (!dest.exists())
	        dest.mkdirs();
	    File[] contents = source.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
	            if (f.isDirectory())
	                copyDir(f, newFile, options);
	            else
	                copyFile(f, newFile, options);
	        }
	    }
	}

	/**
	 * Copies a file.
	 * @param source
	 * the source file
	 * @param dest
	 * the destination file
	 * @param options
	 * the copy options
	 * @throws IOException
	 * thrown in case of an error
	 */
	private static void copyFile(File source, File dest, CopyOption... options) throws IOException {
	    Files.copy(source.toPath(), dest.toPath(), options);
	}

	/**
	 * Ensure that the given file has a parent directory. Creates all
	 * directories on the way to the parent directory if they not exist.
	 * @param file
	 * the file
	 */
	public static void ensureParentDir(File file) {
	    File parent = file.getParentFile();
	    if (parent != null && !parent.exists())
	        parent.mkdirs();
	} 

	/**
	 * Writes a String to the provided file.
	 * @param string
	 * the string to write
	 * @param file
	 * the output file
	 * @throws IOException
	 * if the file does not exist or can not be opened or written to
	 */
	public static void writeString2File(String string, File file) throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			writer.println(string);
		} catch (IOException e) {
			throw(e);
		}
	}
	
	/**
	 * Appends a String to the provided file.
	 * @param string
	 * the string to append
	 * @param file
	 * the output file
	 * @throws IOException
	 * if the file does not exist or can not be opened or written to
	 */
	public static void appendString2File(String string, File file) throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			writer.println(string);
		} catch (IOException e) {
			throw(e);
		}
	}
	
	/**
	 * Reads a file and returns its contents as a String.
	 * @param path
	 * the path to the file to read
	 * @return
	 * the file's contents
	 * @throws IOException
	 * if the file does not exist or can not be opened
	 */
	public static String readFile2String(Path path) throws IOException {
		return new String(Files.readAllBytes(path));
	}
	
//	/**
//	 * Copies the given file to the given destination.
//	 * @param a
//	 * the file to copy
//	 * @param b
//	 * the output file
//	 * @throws IOException
//	 * if the input file could not be read or the output file could not be written
//	 */
//	public static void copyFile(File a, File b) throws IOException {
//		try (FileInputStream inStream = new FileInputStream(a); FileOutputStream outStream = new FileOutputStream(b);
//				FileChannel in = (inStream).getChannel(); FileChannel out = (outStream).getChannel()) {
//			out.transferFrom(in, 0, in.size());
//		} catch (IOException e) {
//			throw(e);
//		}
//	}
	
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
