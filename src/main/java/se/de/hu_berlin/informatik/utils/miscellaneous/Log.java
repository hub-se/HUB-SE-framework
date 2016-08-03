/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

/**
 * Provides methods for creating error messages and other output, etc. 
 * 
 * @author Simon Heiden
 */
public class Log {
	
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
	
//	/**
//	 * Prints the given message to {@code System.err} and exits the application with status code {@code 1}.
//	 * @param message
//	 * an error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void abort(String message, Object... args) {
//		printfErrorMessage(null, message, args);
//		printAbort();
//		exitWithError();
//	}
	
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
	
//	/**
//	 * Prints the given message to {@code System.err}.
//	 * @param message
//	 * an error message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void err(String message, Object... args) {
//		printfErrorMessage(null, message, args);
//	}
	
	/**
	 * Helper method that prints the given message to {@code System.err}.
	 * @param o
	 * is some instantiated object or a class (or null). The class name is used in the produced error message.
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfErrorMessage(Object o, String message, Object... args) {
		System.out.flush();
		String identifier = null;
		if (o != null) {
			try {
				identifier = ((Class<?>)o).getSimpleName();
			} catch(Exception e) {
				identifier = o.getClass().getSimpleName();
			}
		}
		if (identifier != null) {
			System.err.printf("[" + identifier + "] " + message + "%n", args);
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
	
//	/**
//	 * Prints the given message to {@code System.out}.
//	 * @param message
//	 * a message
//	 * @param args
//	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
//	 */
//	public static void out(String message, Object... args) {
//		printfMessage(null, message, args);
//	}
	
	/**
	 * Helper method that prints the given message to {@code System.out}.
	 * @param o
	 * is some instantiated object or a class (or null). The class name is used in the produced message.
	 * @param message
	 * a message
	 * @param args
	 * some arguments for the message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfMessage(Object o, String message, Object... args) {
		String identifier = null;
		if (o != null) {
			try {
				identifier = ((Class<?>)o).getSimpleName();
			} catch(Exception e) {
				identifier = o.getClass().getSimpleName();
			}
		}
		if (identifier != null) {
			System.out.printf("[" + identifier + "] " + message + "%n", args);
		} else {
			System.out.printf(message + "%n", args);
		}
	}
	
	
}
