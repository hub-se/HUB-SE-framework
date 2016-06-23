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
 * Provides miscellaneous methods that are useful in general like creating error messages, etc. 
 * 
 * @author Simon Heiden
 */
public class Misc {
	
//	/**
//	 * Failure types to print pre-defined error messages.
//	 */
//	public enum FailureType {
//		/** Some directory does not exist. Needs the directory as a string as an argument. **/
//		DIR_NOT_EXISTS,
//		/** Some file does not exist. Needs the file as a string as an argument. **/
//		FILE_NOT_EXISTS,
//		/** Some path does not exist. Needs the path as a string as an argument. **/
//		PATH_NOT_EXISTS,
//		/** Error while processing an object. Needs the processed object as a string as an argument. **/
//		PROCESSING_ERROR,
//		/** Error while parsing an object. Needs the parsed object as a string as an argument. **/
//		PARSING_ERROR,
//		/** Some exception was thrown and caught. Needs no arguments. **/
//		EXCEPTION_THROWN,
//		/** Some unknown error occurred. Needs no arguments. **/
//		UNKNOWN_ERROR
//		 }
//
//	/**
//	 * Helper method that prints a pre-defined message to {@code System.err}.
//	 * @param o
//	 * is some instantiated object. The class name is used in the produced error message.
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	private static void printfErrorMessage(Object o, FailureType failure, Object... args) {
//		switch (failure) {
//		case FILE_NOT_EXISTS:
//			printfErrorMessage(o, "File '%s' does not exist.", args);
//			break;
//		case DIR_NOT_EXISTS:
//			printfErrorMessage(o, "Directory '%s' does not exist.", args);
//			break;
//		case PATH_NOT_EXISTS:
//			printfErrorMessage(o, "Path '%s' does not exist.", args);
//			break;
//		case PROCESSING_ERROR:
//			printfErrorMessage(o, "Could not process '%s'.", args);
//			break;
//		case PARSING_ERROR:
//			printfErrorMessage(o, "Could not parse '%s'.", args);
//			break;
//		case EXCEPTION_THROWN:
//			printfErrorMessage(o, "Exception thrown.");
//			break;
//		case UNKNOWN_ERROR:
//			printfErrorMessage(o, "An error occurred.");
//			break;
//		}
//	}
//	
//	/**
//	 * Prints a pre-defined message to {@code System.err} and exits the application with 
//	 * status code {@code 1}.
//	 * @param o
//	 * is some instantiated object. The class name is used in the produced error message.
//	 * @param e
//	 * a caught exception from which will be printed a stack trace
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void abort(Object o, Exception e, FailureType failure, Object... args) {
//		printfErrorMessage(o, failure, args);
//		printAbort();
//		printException(e);
//		exitWithError();
//	}
//	
//	/**
//	 * Prints a pre-defined message to {@code System.err} and exits the application with 
//	 * status code {@code 1}.
//	 * @param o
//	 * is some instantiated object. The class name is used in the produced error message.
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void abort(Object o, FailureType failure, Object... args) {
//		printfErrorMessage(o, failure, args);
//		printAbort();
//		exitWithError();
//	}
//	
//	/**
//	 * Prints a pre-defined message to {@code System.err} and exits the application with 
//	 * status code {@code 1}.
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void abort(FailureType failure, Object... args) {
//		printfErrorMessage(null, failure, args);
//		printAbort();
//		exitWithError();
//	}
//	
//	/**
//	 * Prints a pre-defined message to {@code System.err}.
//	 * @param o
//	 * is some instantiated object. The class name is used in the produced error message.
//	 * @param e
//	 * a caught exception from which will be printed a stack trace
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void err(Object o, Exception e, FailureType failure, Object... args) {
//		printfErrorMessage(o, failure, args);
//		printException(e);
//	}
//	
//	/**
//	 * Prints a pre-defined message to {@code System.err}.
//	 * @param o
//	 * is some instantiated object. The class name is used in the produced error message.
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void err(Object o, FailureType failure, Object... args) {
//		printfErrorMessage(o, failure, args);
//	}
//	
//	/**
//	 * Prints a pre-defined message to {@code System.err}.
//	 * @param failure
//	 * a failure type to generate a pre-defined error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void err(FailureType failure, Object... args) {
//		printfErrorMessage(null, failure, args);
//	}
	
	/**
	 * Prints the given message to {@code System.err} and exits the application with status code {@code 1}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced error message.
	 * @param e
	 * a caught exception from which will be printed a stack trace
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void abort(Object o, Exception e, String message, Object... args) {
		printfErrorMessage(o, message, args);
		printAbort();
		printException(e);
		exitWithError();
	}
	
	/**
	 * Prints the given message to {@code System.err} and exits the application with status code {@code 1}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced error message.
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void abort(Object o, String message, Object... args) {
		printfErrorMessage(o, message, args);
		printAbort();
		exitWithError();
	}
	
	/**
	 * Prints the given message to {@code System.err} and exits the application with status code {@code 1}.
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void abort(String message, Object... args) {
		printfErrorMessage(null, message, args);
		printAbort();
		exitWithError();
	}
	
	/**
	 * Prints the given message to {@code System.err}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced error message.
	 * @param e
	 * a caught exception from which will be printed a stack trace
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void err(Object o, Exception e, String message, Object... args) {
		printfErrorMessage(o, message, args);
		printException(e);
	}
	
	/**
	 * Prints the given message to {@code System.err}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced error message.
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void err(Object o, String message, Object... args) {
		printfErrorMessage(o, message, args);
	}
	
	/**
	 * Prints the given message to {@code System.err}.
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void err(String message, Object... args) {
		printfErrorMessage(null, message, args);
	}
	
	/**
	 * Helper method that prints the given message to {@code System.err}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced error message.
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfErrorMessage(Object o, String message, Object... args) {
		System.out.flush();
		if (o != null) {
			System.err.printf(o.getClass().getSimpleName() + ": " + message + "%n", args);
		} else {
			System.err.printf(message + "%n", args);
		}
	}
	
	
	
	/**
	 * Prints an exception to the console.
	 * @param e
	 * the exception to be printed
	 */
	private static void printException(Exception e) {
		System.err.println();
		System.err.printf("Exception message: %s%n", e.getMessage());
		System.err.println();
		e.printStackTrace();
	}
	
	/**
	 * Prints an abort message to {@code System.err}.
	 */
	private static void printAbort() {
		System.err.println("aborting...");
	}
	
	/**
	 * Exits the application with status code 1.
	 */
	private static void exitWithError(){
		System.exit(1);
	}
	
	/**
	 * Prints the given message to {@code System.out}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced message.
	 * @param message
	 * a message
	 * @param args
	 * some arguments for the message, as in {@code System.out.printf(...)}, for example
	 */
	public static void out(Object o, String message, Object... args) {
		printfMessage(o, message, args);
	}
	
	/**
	 * Prints the given message to {@code System.out}.
	 * @param message
	 * a message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void out(String message, Object... args) {
		printfMessage(null, message, args);
	}
	
	/**
	 * Helper method that prints the given message to {@code System.out}.
	 * @param o
	 * is some instantiated object. The class name is used in the produced message.
	 * @param message
	 * a message
	 * @param args
	 * some arguments for the message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfMessage(Object o, String message, Object... args) {
		if (o != null) {
			System.out.printf("[" + o.getClass().getSimpleName() + "] " + message + "%n", args);
		} else {
			System.out.printf(message + "%n", args);
		}
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
				err("Could not delete " + fileOrDir.toString() + ".");
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
	private static void ensureParentDir(File file) {
	    File parent = file.getParentFile();
	    if (parent != null && !parent.exists())
	        parent.mkdirs();
	} 

	/**
	 * Writes a String to the provided file.
	 * @param st
	 * the string to write
	 * @param f
	 * the output file
	 * @throws IOException
	 * if the file does not exist or can not be opened or written to
	 */
	public static void writeString2File(String st, File f) throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)))) {
			writer.println(st);
		} catch (IOException e) {
			throw(e);
		}
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
	 * @param item
	 * item to append to the array
	 * @return
	 * the array with the given item appended
	 * @param <T>
	 * the type of the arrays
	 */
	public static <T> T[] addToArrayAndReturnResult(T[] a, T item) {
		if (a == null) {
			Class<?> type = item.getClass();
			@SuppressWarnings("unchecked")
			T[] array = (T[]) Array.newInstance(type, 1);
			array[0] = item;
			return array;
		}
		if (item == null) {
			return a;
		}
		Class<?> type = a.getClass().getComponentType();
		@SuppressWarnings("unchecked")
		T[] joinedArray = (T[]) Array.newInstance(type, a.length + 1);
		System.arraycopy(a, 0, joinedArray, 0, a.length);
		joinedArray[a.length] = item;
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
