/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.io.File;
import java.io.IOException;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Starts the given Java Class in a new process.
 * 
 * @author Simon Heiden
 */
public class ExecuteMainClassInNewJVMModule extends AModule<String[],Integer> {

	private File executionDir;
	private String clazz;
	private String cp;
	private String[] properties;
	
	private String javaHome = null;
	
	public ExecuteMainClassInNewJVMModule(File executionDir, String clazz, String cp, String... properties) {
		this(null, executionDir, clazz, cp, properties);
	}
	
	public ExecuteMainClassInNewJVMModule(String javaHome, File executionDir, String clazz, String cp, String... properties) {
		super(true);
		this.executionDir = executionDir;
		this.clazz = clazz;
		this.cp = cp;
		this.properties = properties;
		
		this.javaHome = javaHome;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Integer processItem(String[] args) {
		return run(args);
	}

	private int run(String... args) {
		String tool = "java";
		if (javaHome != null) {
			tool = javaHome + File.separator + "bin" + File.separator + "java";
		}
		String[] fullArgs = {tool, "-server", "-cp", cp};
		String[] clazzWrapper = { clazz };
		fullArgs = Misc.joinArrays(fullArgs, properties);
		fullArgs = Misc.joinArrays(fullArgs, clazzWrapper);
		fullArgs = Misc.joinArrays(fullArgs, args);

        ProcessBuilder pb = new ProcessBuilder(fullArgs);
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
