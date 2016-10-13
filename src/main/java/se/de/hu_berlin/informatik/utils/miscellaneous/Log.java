/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides methods for creating error messages and other output, etc. 
 * 
 * @author Simon Heiden
 */
final public class Log {
	
	//intended to store already created loggers
	final private static Map<String,Logger> LOGGER_CACHE;
			
	static {
		System.setProperty("log4j.configurationFactory", LogConfigurationFactory.class.getCanonicalName());
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		LOGGER_CACHE = new ConcurrentHashMap<>();
	}
	
	//suppress default constructor (class should not be instantiated)
	private Log() {
		throw new AssertionError();
	}
	
	private static Logger getLogger(final Object id) {
		if (id == null) {
			return LogManager.getRootLogger();
		}
		String identifier;
		if (id instanceof String) {
			identifier = (String)id; 
		} else if (id instanceof Class<?>) {
			identifier = ((Class<?>)id).getName(); 
		} else {
			identifier = id.getClass().getName();
		}
		final Logger logger = LOGGER_CACHE.get(identifier);
		if (logger == null) {
			return LOGGER_CACHE.computeIfAbsent(identifier, k -> LogManager.getLogger(k));
		} else {
			return logger;
		}
		
	}
	
	/**
	 * Prints the given error message and exits the application with status code {@code 1}.
	 * @param id
	 * the object to use for generating an identifier
	 * @param e
	 * a caught exception from which will be printed a stack trace
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void abort(final Object id, final Exception e, final String message, final Object... args) {
		printfErrorMessage(id, message, args);
		printException(id, e);
		printAbort(id);
		exitWithError();
	}
	
	/**
	 * Prints the given error message and exits the application with status code {@code 1}.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void abort(final Object id, final String message, final Object... args) {
		printfErrorMessage(id, message, args);
		printAbort(id);
		exitWithError();
	}
	
	/**
	 * Prints the given error message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param e
	 * a caught exception from which will be printed a stack trace
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void err(final Object id, final Throwable e, final String message, final Object... args) {
		printfErrorMessage(id, message, args);
		printException(id, e);
	}
	
	/**
	 * Prints the given throwable.
	 * @param id
	 * the object to use for generating an identifier
	 * @param e
	 * a caught exception from which will be printed a stack trace
	 */
	public static void err(final Object id, final Throwable e) {
		printException(id, e);
	}
	
	/**
	 * Prints an exception.
	 * @param id
	 * the object to use for generating an identifier
	 * @param e
	 * the exception to be printed
	 */
	private static void printException(final Object id, final Throwable e) {
		getLogger(id).catching(Level.ERROR, e);
	}
	
	/**
	 * Prints an abort message.
	 * @param id
	 * the object to use for generating an identifier
	 */
	private static void printAbort(final Object id) {
		getLogger(id).fatal("aborting...");
	}
	
	/**
	 * Exits the application with status code 1.
	 */
	private static void exitWithError(){
		System.exit(1);
	}
	
	/**
	 * Prints the given warning message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * a warning message
	 * @param args
	 * some arguments for the warning message, as in {@code System.out.printf(...)}, for example
	 */
	public static void warn(final Object id, final String message, final Object... args) {
		printfWarnMessage(id, message, args);
	}
	
	/**
	 * Prints the given error message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	public static void err(final Object id, final String message, final Object... args) {
		printfErrorMessage(id, message, args);
	}
	
	/**
	 * Helper method that prints the given error message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfErrorMessage(final Object id, final String message, final Object... args) {
		getLogger(id).printf(Level.ERROR, message, args);
	}
	
	/**
	 * Helper method that prints the given error message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * an error message
	 * @param args
	 * some arguments for the error message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfWarnMessage(final Object id, final String message, final Object... args) {
		getLogger(id).printf(Level.WARN, message, args);
	}
	
	/**
	 * Prints the given message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * a message
	 * @param args
	 * some arguments for the message, as in {@code System.out.printf(...)}, for example
	 */
	public static void out(final Object id, final String message, final Object... args) {
		printfMessage(id, message, args);
	}
	
	/**
	 * Helper method that prints the given message.
	 * @param id
	 * the object to use for generating an identifier
	 * @param message
	 * a message
	 * @param args
	 * some arguments for the message, as in {@code System.out.printf(...)}, for example
	 */
	private static void printfMessage(final Object id, final String message, final Object... args) {
		getLogger(id).printf(Level.INFO, message, args);
	}
	
	
}
