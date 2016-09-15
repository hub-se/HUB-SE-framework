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
		printException(o, e);
		printAbort(o);
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
		printAbort(o);
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
	public static void err(Object o, Throwable e, String message, Object... args) {
		printfErrorMessage(o, message, args);
		printException(o, e);
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
				identifier = ((Class<?>)o).getName();
			} catch(Exception e) {
				identifier = o.getClass().getName();
			}
		}
		Logger logger = identifier == null ? LogManager.getRootLogger() : LogManager.getLogger(identifier);
		logger.printf(Level.ERROR, message, args);
	}
	
	/**
	 * Prints an exception to the console.
	 * @param o
	 * is some instantiated object or a class (or null). The class name is used in the produced error message.
	 * @param e
	 * the exception to be printed
	 */
	private static void printException(Object o, Throwable e) {
		String identifier = null;
		if (o != null) {
			try {
				identifier = ((Class<?>)o).getName();
			} catch(Exception x) {
				identifier = o.getClass().getName();
			}
		}
		Logger logger = identifier == null ? LogManager.getRootLogger() : LogManager.getLogger(identifier);
		logger.catching(Level.ERROR, e);
//		System.err.printf("Exception message: %s%n", e.getMessage());
//		System.err.println();
//		e.printStackTrace();
	}
	
	/**
	 * Prints an abort message to {@code System.err}.
	 * @param o
	 * is some instantiated object or a class (or null). The class name is used in the produced message.
	 */
	private static void printAbort(Object o) {
		String identifier = null;
		if (o != null) {
			try {
				identifier = ((Class<?>)o).getName();
			} catch(Exception e) {
				identifier = o.getClass().getName();
			}
		}
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
				identifier = ((Class<?>)o).getName();
			} catch(Exception e) {
				identifier = o.getClass().getName();
			}
		}
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
