package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

import se.de.hu_berlin.informatik.utils.processors.basics.ExecuteCommandInSystemEnvironmentAndReturnOutput;
import se.de.hu_berlin.informatik.utils.processors.basics.ExecuteCommandInSystemEnvironment;

final public class SystemUtils {
	
	//suppress default constructor (class should not be instantiated)
	private SystemUtils() {
		throw new AssertionError();
	}

	public static long getFreeMem() {
		return Runtime.getRuntime().freeMemory();
	}
	
	public static long getMaxMem() {
		return Runtime.getRuntime().maxMemory();
	}
	
	public static long getTotalMem() {
		return Runtime.getRuntime().totalMemory();
	}
	
	public static long getTotalFreeMem() {
		final Runtime runtime = Runtime.getRuntime();

		final long maxMemory = runtime.maxMemory();
		final long allocatedMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();
		
		return freeMemory + (maxMemory - allocatedMemory);
	}
	
	/**
	 * Executes a given command in the system's environment, while additionally using the given Java environment,
	 * Will abort the program in case of an error in the executed process. If any of the given Java related
	 * Strings is null, then the command will be executed with the system's default Java environment.
	 * @param executionDir
	 * an execution directory in which the command shall be executed
	 * @param javaBinDir
	 * path to Java binaries
	 * @param javaHomeDir
	 * path to Java home directory
	 * @param javaJREDir
	 * path to a Java JRE directory
	 * @param commandArgs
	 * the command to execute, given as an array
	 */
	public static void executeCommandInJavaEnvironment(File executionDir, 
			String javaBinDir, String javaHomeDir, String javaJREDir, String... commandArgs) {
		int executionResult = -1;
		if (javaBinDir == null || javaHomeDir == null || javaJREDir == null) {
			executionResult = new ExecuteCommandInSystemEnvironment(executionDir)
					.asModule()
					.submit(commandArgs)
					.getResult();
		} else {
			executionResult = new ExecuteCommandInSystemEnvironment(executionDir, javaBinDir)
					.setEnvVariable("JAVA_HOME", javaHomeDir)
					.setEnvVariable("JRE_HOME", javaJREDir)
					.asModule()
					.submit(commandArgs)
					.getResult();
		}
		
		if (executionResult != 0) {
			Log.abort(SystemUtils.class, "Error while executing command: " + Misc.arrayToString(commandArgs, " ", "", ""));
		}
	}
	
	/**
	 * Executes a given command in the system's environment, while additionally using the given Java environment,
	 * Will abort the program in case of an error in the executed process. The command will be executed with 
	 * the system's default Java environment.
	 * @param executionDir
	 * an execution directory in which the command shall be executed
	 * @param commandArgs
	 * the command to execute, given as an array
	 */
	public static void executeCommand(File executionDir, String... commandArgs) {
		executeCommandInJavaEnvironment(executionDir, null, null, null, commandArgs);
	}
	
	/**
	 * Executes a given command in the system's environment, while additionally using a the given Java environment. 
	 * Returns either the process' output to standard out or to error out. If any of the given Java related
	 * Strings is null, then the command will be executed with the system's default Java environment.
	 * @param executionDir
	 * an execution directory in which the command shall be executed
	 * @param returnErrorOutput
	 * whether to output the error output channel instead of standeard out
	 * @param javaBinDir
	 * path to Java binaries
	 * @param javaHomeDir
	 * path to Java home directory
	 * @param javaJREDir
	 * path to a Java JRE directory
	 * @param commandArgs
	 * the command to execute, given as an array
	 * @return
	 * the process' output to standard out or to error out
	 */
	public static String executeCommandWithOutputInJavaEnvironment(File executionDir, boolean returnErrorOutput, 
			String javaBinDir, String javaHomeDir, String javaJREDir, String... commandArgs) {
		if (javaBinDir == null || javaHomeDir == null || javaJREDir == null) {
			return new ExecuteCommandInSystemEnvironmentAndReturnOutput(executionDir, returnErrorOutput)
					.asModule()
					.submit(commandArgs)
					.getResult();
		} else {
			return new ExecuteCommandInSystemEnvironmentAndReturnOutput(executionDir, returnErrorOutput, javaBinDir)
					.setEnvVariable("JAVA_HOME", javaHomeDir)
					.setEnvVariable("JRE_HOME", javaJREDir)
					.asModule()
					.submit(commandArgs)
					.getResult();
		}
	}
	
	/**
	 * Executes a given command in the system's environment, while additionally using a the given Java environment. 
	 * Returns either the process' output to standard out or to error out. The command will be executed with 
	 * the system's default Java environment.
	 * @param executionDir
	 * an execution directory in which the command shall be executed
	 * @param returnErrorOutput
	 * whether to output the error output channel instead of standeard out
	 * @param commandArgs
	 * the command to execute, given as an array
	 * @return
	 * the process' output to standard out or to error out
	 */
	public static String executeCommandWithOutput(File executionDir, boolean returnErrorOutput, 
			String... commandArgs) {
		return executeCommandWithOutputInJavaEnvironment(executionDir, returnErrorOutput, 
				null, null, null, commandArgs);
	}
	
	/**
	 * Adds the given paths dynamically to the class path via reflection.
	 * Calls the method {@link #addToClassPath(ClassLoader, URL...)}.
	 * @param paths
	 * the paths to add
	 * @param classLoader
	 * the class loader to add the paths to
	 * @throws IllegalArgumentException
	 * if one of the given paths is not valid or if adding one of the paths failed
	 */
	public static void addToClassPath(ClassLoader classLoader, File... paths) throws IllegalArgumentException {
		URL[] urls = new URL[paths.length];
		for (int i = 0; i < paths.length; ++i) {
			try {
				urls[i] = paths[i].toURI().toURL();
			} catch (MalformedURLException muex) {
				throw new IllegalArgumentException("Invalid path: " + paths[i], muex);
			}
		}
		addToClassPath(classLoader, (URL[])urls);
	}

	/**
	 * Adds the given path dynamically to the class path via reflection.
	 * Calls the method {@link #addToClassPath(ClassLoader, URL)}.
	 * @param path
	 * the path to add
	 * @param classLoader
	 * the class loader to add the path to
	 * @throws IllegalArgumentException
	 * if the given path is not valid or if adding the path failed
	 */
	public static void addToClassPath(ClassLoader classLoader, File path) throws IllegalArgumentException {
		try {
			addToClassPath(classLoader, path.toURI().toURL());
		} catch (MalformedURLException muex) {
			throw new IllegalArgumentException("Invalid path: " + path, muex);
		}
	}

	/**
	 * Adds the given URLs dynamically to the class path via reflection.
	 * @param urls
	 * the URLs to add
	 * @param classLoader
	 * the class loader to add the URLs to
	 * @throws IllegalArgumentException
	 * if adding the given URLs failed
	 */
	public static void addToClassPath(ClassLoader classLoader, URL... urls) throws IllegalArgumentException {
		try {
			Class<?> clazz = URLClassLoader.class;
			Method m = clazz.getDeclaredMethod("addURL", new Class[]{URL.class});
			m.setAccessible(true);
			for (URL url : urls) {
				m.invoke(classLoader, new Object[]{url});
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Adding URLs failed: " + Misc.arrayToString(urls), ex);
		}
	}
	
	/**
	 * Adds the given URL dynamically to the class path via reflection.
	 * @param url
	 * the URL to add
	 * @param classLoader
	 * the class loader to add the URL to
	 * @throws IllegalArgumentException
	 * if adding the given URLs failed
	 * @throws IllegalArgumentException
	 * if adding the given URL failed
	 */
	public static void addToClassPath(ClassLoader classLoader, URL url) throws IllegalArgumentException {
		try {
			Class<?> clazz = URLClassLoader.class;
			Method m = clazz.getDeclaredMethod("addURL", new Class[]{URL.class});
			m.setAccessible(true);
			m.invoke(classLoader, new Object[]{url});
		} catch (Exception ex) {
			throw new IllegalArgumentException("Adding URL failed: " + url, ex);
		}
	}
	
	/**
	 * Adds the given paths dynamically to the class path via reflection, using
	 * the system class loader. Calls {@link #addToClassPath(ClassLoader, File...)}.
	 * @param paths
	 * the paths to add
	 * @throws IllegalArgumentException
	 * if one of the given paths is not valid or if adding one of the paths failed
	 */
	public static void addToClassPath(File... paths) throws IllegalArgumentException {
		addToClassPath(getSystemClassLoader(), (File[])paths);
	}
	
	/**
	 * Adds the given path dynamically to the class path via reflection, using
	 * the system class loader. Calls {@link #addToClassPath(ClassLoader, File)}.
	 * @param path
	 * the path to add
	 * @throws IllegalArgumentException
	 * if the given path is not valid or if adding the path failed
	 */
	public static void addToClassPath(File path) throws IllegalArgumentException {
		addToClassPath(getSystemClassLoader(), path);
	}

	/**
	 * Adds the given URLs dynamically to the class path via reflection, using
	 * the system class loader. Calls {@link #addToClassPath(ClassLoader, URL...)}.
	 * @param urls
	 * the URLs to add
	 * @throws IllegalArgumentException
	 * if adding the given URLs failed
	 */
	public static void addToClassPath(URL... urls) throws IllegalArgumentException {
		addToClassPath(getSystemClassLoader(), (URL[])urls);
	}
	
	/**
	 * Adds the given URL dynamically to the class path via reflection, using
	 * the system class loader. Calls {@link #addToClassPath(ClassLoader, URL)}.
	 * @param url
	 * the URL to add
	 * @throws IllegalArgumentException
	 * if adding the given URL failed
	 */
	public static void addToClassPath(URL url) throws IllegalArgumentException {
		addToClassPath(getSystemClassLoader(), url);
	}

	/**
	 * @return
	 * the system class loader
	 */
	public static ClassLoader getSystemClassLoader() {
		if (System.getSecurityManager() == null) {
			return ClassLoader.getSystemClassLoader();
		} else {
			return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
				@Override
				public ClassLoader run() {
					return ClassLoader.getSystemClassLoader();
				}
			});
		}
	}

}
