/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;

/**
 * Starts the given command as a new process.
 * 
 * @author Simon Heiden
 */
public class ExecuteCommandInSystemEnvironmentModule extends AbstractProcessor<String[],Integer> {

	private File executionDir;
	private String[] paths = null;
	private Map<String,String> environmentVariables;
	
	/**
	 * Starts the given command as a new process.
	 * @param executionDir
	 * the directory to execute the command in (or null if the current directory should be used)
	 * @param paths
	 * paths to add at the start of the PATH environment variable, if any
	 */
	public ExecuteCommandInSystemEnvironmentModule(File executionDir, String... paths) {
		super();
		this.executionDir = executionDir;
		this.paths = paths;
		environmentVariables = new HashMap<>();
	}
	
	/**
	 * Sets a new execution directory.
	 * @param executionDir
	 * the new execution directory for future started processes
	 * @return
	 * this module for method chaining
	 */
	public ExecuteCommandInSystemEnvironmentModule setExecutionDir(File executionDir) {
		this.executionDir = executionDir;
		return this;
	}
	
	/**
	 * Sets an environment variable,
	 * @param variable
	 * the environment variable to be set
	 * @param value
	 * the desired value of the environment variable
	 * @return
	 * this module for method chaining
	 */
	public ExecuteCommandInSystemEnvironmentModule setEnvVariable(String variable, String value) {
		environmentVariables.put(variable, value);
		return this;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Integer processItem(String[] commands) {
		return run(commands);
	}

	private int run(String[] commands) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(executionDir);
        pb.inheritIO();
        
        if (paths != null && pb.environment().containsKey("PATH")) {
        	String newPath = Misc.arrayToString(paths, File.pathSeparator, "", "") + File.pathSeparator + pb.environment().get("PATH");
        	pb.environment().put("PATH", newPath);
//        	Misc.out(this, "PATH: " + pb.environment().get("PATH"));
        }
        for (Entry<String,String> entry : environmentVariables.entrySet()) {
        	pb.environment().put(entry.getKey(), entry.getValue());
        }
        
        Process p = null;
		try {
			p = pb.start();
		} catch (IOException e) {
			Log.err(this, e, "IOException thrown.");
			return 1;
		}
//        InputStreamConsumer consumer = new InputStreamConsumer(p.getInputStream(), System.out);
//        consumer.start();
//        InputStreamConsumer errconsumer = new InputStreamConsumer(p.getErrorStream(), System.err);
//        errconsumer.start();

        //obtain result and wait for the process to finish execution
        int result = 1;
        boolean isFirst = true;
        while (p.isAlive() || isFirst) {
        	isFirst = false;
        	try {
        		result = p.waitFor();
        	} catch (InterruptedException e) {
        	}
        }

//        while (consumer.isAlive()) {
//        	try {
//        		consumer.join();
//        	} catch (InterruptedException e) {
//        	}
//        }
//        while (errconsumer.isAlive()) {
//        	try {
//        		errconsumer.join();
//        	} catch (InterruptedException e) {
//        	}
//        }

        return result;
    }	
}
