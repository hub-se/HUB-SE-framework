/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Starts the given command as a new process and returns the output as a String.
 * 
 * @author Simon Heiden
 */
public class ExecuteCommandInSystemEnvironmentAndReturnOutputModule extends AModule<String[],String> {

	private File executionDir;
	private String[] paths = null;
	private Map<String,String> environmentVariables;
	boolean returnErrorOutput = false;
	
	/**
	 * Starts the given command as a new process.
	 * @param executionDir
	 * the directory to execute the command in (or null if the current directory should be used)
	 * @param returnErrorOutput
	 * whether the error output channel should be returned instead of the standard output
	 * @param paths
	 * paths to add at the start of the PATH environment variable, if any
	 */
	public ExecuteCommandInSystemEnvironmentAndReturnOutputModule(File executionDir, boolean returnErrorOutput, String... paths) {
		super(true);
		this.executionDir = executionDir;
		this.paths = paths;
		environmentVariables = new HashMap<>();
		this.returnErrorOutput = returnErrorOutput;
	}

	/**
	 * Sets a new execution directory.
	 * @param executionDir
	 * the new execution directory for future started processes
	 * @return
	 * this module for method chaining
	 */
	public ExecuteCommandInSystemEnvironmentAndReturnOutputModule setExecutionDir(File executionDir) {
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
	public ExecuteCommandInSystemEnvironmentAndReturnOutputModule setEnvVariable(String variable, String value) {
		environmentVariables.put(variable, value);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public String processItem(String[] commands) {
		return run(commands);
	}

	private String run(String[] commands) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(executionDir);
        
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
			Misc.err(this, e, "IOException thrown.");
			return null;
		}
//        InputStreamConsumer consumer = new InputStreamConsumer(p.getInputStream(), System.out);
//        consumer.start();
//        InputStreamConsumer errconsumer = new InputStreamConsumer(p.getErrorStream(), System.err);
//        errconsumer.start();

		BufferedReader reader = null;
		
		if (returnErrorOutput) {
			reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			pb.redirectInput(Redirect.INHERIT).redirectOutput(Redirect.INHERIT);
		} else {
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			pb.redirectInput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
		}
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			boolean isFirst = true;
			while ( (line = reader.readLine()) != null) {
				if (isFirst) {
					isFirst = false;
				} else {
					builder.append(System.getProperty("line.separator"));
				}
				builder.append(line);
			}
		} catch (IOException e1) {
			Misc.err(this, e1, "IOException thrown while trying to process process output.");
			return null;
		}
		String result = builder.toString();

        //obtain result and wait for the process to finish execution
        boolean isFirst = true;
        while (p.isAlive() || isFirst) {
        	isFirst = false;
        	try {
        		p.waitFor();
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
