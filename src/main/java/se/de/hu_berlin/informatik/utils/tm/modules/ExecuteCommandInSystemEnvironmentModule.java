/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.io.File;
import java.io.IOException;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Starts the given command as a new process.
 * 
 * @author Simon Heiden
 */
public class ExecuteCommandInSystemEnvironmentModule extends AModule<String[],Integer> {

	private File executionDir;
	private String[] commands;
	
	/**
	 * Starts the given command as a new process.
	 * @param executionDir
	 * the directory to execute the command in (or null if the current directory should be used)
	 * @param commands
	 * the commands to execute
	 */
	public ExecuteCommandInSystemEnvironmentModule(File executionDir, String... commands) {
		super(true);
		this.executionDir = executionDir;
		this.commands = commands;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Integer processItem(String[] args) {
		return run(args);
	}

	private int run(String... args) {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(executionDir);
        pb.inheritIO();
        Process p = null;
		try {
			p = pb.start();
		} catch (IOException e) {
			Misc.err(this, e, "IOException thrown.");
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
