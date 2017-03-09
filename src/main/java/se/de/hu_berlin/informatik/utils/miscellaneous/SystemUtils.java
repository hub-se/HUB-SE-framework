package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;

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
}
