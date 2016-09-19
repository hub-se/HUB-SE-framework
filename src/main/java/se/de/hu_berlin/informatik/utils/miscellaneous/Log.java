/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

/**
 * Provides methods for creating error messages and other output, etc. 
 * 
 * @author Simon Heiden
 */
public class Log {
			
	static {
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		ConfigurationFactory.setConfigurationFactory(new CustomConfigurationFactory());
	}
	
	//suppress default constructor (class should not be instantiated)
	private Log() {
		throw new AssertionError();
	}
	
	/**
	 * Returns an identifier for the given object. If the object is a String, then
	 * the String will simply be returned. If it is a Class object, then the class 
	 * name will be returned. In all other cases, the name of the class of the given
	 * object will be returned.
	 * @param id
	 * the object for which to return an identifier
	 * @return
	 * an identifier
	 */
	private static String getIdentifier(Object id) {
		if (id != null) {
			if (id instanceof String) {
				return (String)id; 
			} else {
				try {
					return ((Class<?>)id).getName();
				} catch(Exception e) {
					return id.getClass().getName();
				}
			}
		}
		return null;
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
		String identifier = getIdentifier(id);
		Logger logger = identifier == null ? LogManager.getRootLogger() : LogManager.getLogger(identifier);
		logger.printf(Level.ERROR, message, args);
	}
	
	/**
	 * Prints an exception.
	 * @param id
	 * the object to use for generating an identifier
	 * @param e
	 * the exception to be printed
	 */
	private static void printException(Object id, Throwable e) {
		String identifier = getIdentifier(id);
		Logger logger = identifier == null ? LogManager.getRootLogger() : LogManager.getLogger(identifier);
		logger.catching(Level.ERROR, e);
	}
	
	/**
	 * Prints an abort message.
	 * @param id
	 * the object to use for generating an identifier
	 */
	private static void printAbort(Object id) {
		String identifier = getIdentifier(id);
		Logger logger = identifier == null ? LogManager.getRootLogger() : LogManager.getLogger(identifier);
		logger.fatal("aborting...");
	}
	
	/**
	 * Exits the application with status code 1.
	 */
	private static void exitWithError(){
		System.exit(1);
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
		String identifier = getIdentifier(id);
		Logger logger = identifier == null ? LogManager.getRootLogger() : LogManager.getLogger(identifier);
		logger.printf(Level.INFO, message, args);
	}
	
	@Plugin(name = "CustomConfigurationFactory", category = ConfigurationFactory.CATEGORY)
	@Order(50)
	private static class CustomConfigurationFactory extends ConfigurationFactory {

	    private static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
	        builder.setConfigurationName(name);
	        builder.setStatusLevel(Level.ERROR);
	        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
	            addAttribute("level", Level.DEBUG));
	        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
	            addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
	        appenderBuilder.add(builder.newLayout("PatternLayout").
	            addAttribute("pattern", "%d{HH:mm:ss} %-5level [%c{1}] %msg%n"
	            		+ "%xEx{filters(org.junit,org.apache.maven,sun.reflect,java.lang.reflect)}"));
	        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY,
	            Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"));
	        builder.add(appenderBuilder);
	        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG).
	            add(builder.newAppenderRef("Stdout")).
	            addAttribute("additivity", false));
	        builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("Stdout")));
	        return builder.build();
	    }

	    @Override
	    public Configuration getConfiguration(ConfigurationSource source) {
	        return getConfiguration(source.toString(), null);
	    }

	    @Override
	    public Configuration getConfiguration(final String name, final URI configLocation) {
	        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
	        return createConfiguration(name, builder);
	    }

	    @Override
	    protected String[] getSupportedTypes() {
	        return new String[] {"*"};
	    }
	}
	
}
