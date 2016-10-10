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
public class Log {
	
	//intended to store already created loggers
	private static Map<String,Logger> loggerCache;
			
	static {
		System.setProperty("log4j.configurationFactory", LogConfigurationFactory.class.getCanonicalName());
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		loggerCache = new ConcurrentHashMap<>();
	}
	
	//suppress default constructor (class should not be instantiated)
	private Log() {
		throw new AssertionError();
	}
	
	private static Logger getLogger(Object id) {
		if (id == null) {
			return LogManager.getRootLogger();
		}
		String identifier = null;
		if (id instanceof String) {
			identifier = (String)id; 
		} else if (id instanceof Class<?>) {
			identifier = ((Class<?>)id).getName(); 
		} else {
			identifier = id.getClass().getName();
		}
		Logger logger = loggerCache.get(identifier);
		if (logger == null) {
			return loggerCache.computeIfAbsent(identifier, k -> LogManager.getLogger(k));
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
	public static void abort(Object id, Exception e, String message, Object... args) {
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
	public static void abort(Object id, String message, Object... args) {
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
	public static void err(Object id, Throwable e, String message, Object... args) {
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
	public static void err(Object id, Throwable e) {
		printException(id, e);
	}
	
	/**
	 * Prints an exception.
	 * @param id
	 * the object to use for generating an identifier
	 * @param e
	 * the exception to be printed
	 */
	private static void printException(Object id, Throwable e) {
		getLogger(id).catching(Level.ERROR, e);
	}
	
	/**
	 * Prints an abort message.
	 * @param id
	 * the object to use for generating an identifier
	 */
	private static void printAbort(Object id) {
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
	public static void warn(Object id, String message, Object... args) {
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
	public static void err(Object id, String message, Object... args) {
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
	private static void printfErrorMessage(Object id, String message, Object... args) {
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
	private static void printfWarnMessage(Object id, String message, Object... args) {
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
	public static void out(Object id, String message, Object... args) {
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
	private static void printfMessage(Object id, String message, Object... args) {
		getLogger(id).printf(Level.INFO, message, args);
	}
	
	
}
