/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import se.de.hu_berlin.informatik.utils.miscellaneous.ClassPathParser;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.AbstractProcessor;

/**
 * Starts the given Java Class in a new process.
 * 
 * @author Simon Heiden
 */
public class ExecuteMainClassInNewJVMModule extends AbstractProcessor<String[],Integer> {

	private File executionDir;
	private Class<?> clazz;
	private String cp;
	private String[] properties;
	private Map<String,String> environmentVariables;
	
	private String javaHome = null;
	
	/**
	 * Starts the given class as a new process with the standard JVM and the current class path.
	 * @param clazz
	 * the name of the Java class to execute. Must contain a main method
	 * @param executionDir
	 * the directory to execute the new process in (or null if the current directory should be used)
	 * @param properties
	 * other properties to give to the JVM
	 */
	public ExecuteMainClassInNewJVMModule(Class<?> clazz, File executionDir, String... properties) {
		this(null, clazz, null, executionDir, properties);
	}
	
	/**
	 * Starts the given class as a new process with the standard JVM.
	 * @param clazz
	 * the name of the Java class to execute. Must contain a main method
	 * @param cp
	 * the class path to use
	 * @param executionDir
	 * the directory to execute the new process in (or null if the current directory should be used)
	 * @param properties
	 * other properties to give to the JVM
	 */
	public ExecuteMainClassInNewJVMModule(Class<?> clazz, String cp, File executionDir, String... properties) {
		this(null, clazz, cp, executionDir, properties);
	}
	
	/**
	 * Starts the given class as a new process.
	 * @param javaHome
	 * a path to a Java installation directory (or null if the standard Java installation should be used)
	 * @param clazz
	 * the name of the Java class to execute. Must contain a main method
	 * @param cp
	 * the class path to use
	 * @param executionDir
	 * the directory to execute the new process in (or null if the current directory should be used)
	 * @param properties
	 * other properties to give to the JVM
	 */
	public ExecuteMainClassInNewJVMModule(String javaHome,  
			Class<?> clazz, String cp, File executionDir, String... properties) {
		super();
		this.executionDir = executionDir;
		this.clazz = clazz;
		if (cp != null) {
			this.cp = cp;
		} else {
			this.cp = new ClassPathParser()
					.parseSystemClasspath()
					.getClasspath();
		}
		this.properties = properties;
		
		this.javaHome = javaHome;
		
		this.environmentVariables = new HashMap<>();
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
		String[] clazzWrapper = new String[1];
		if (clazz.getEnclosingClass() == null) {
			clazzWrapper[0] = clazz.getCanonicalName();
		} else {
			String[] clazzItems = clazz.getCanonicalName().split("\\.");
			StringBuilder builder = new StringBuilder();
			boolean isFirst = true;
			for (int i = 0; i < clazzItems.length - 1; ++i) {
				if (isFirst) {
					isFirst = false;
				} else {
					builder.append('.');
				}
				builder.append(clazzItems[i]);
			}
			builder.append('$');
			builder.append(clazzItems[clazzItems.length-1]);
			clazzWrapper[0] = builder.toString();
		}
		fullArgs = Misc.joinArrays(fullArgs, properties);
		fullArgs = Misc.joinArrays(fullArgs, clazzWrapper);
		fullArgs = Misc.joinArrays(fullArgs, args);

        ProcessBuilder pb = new ProcessBuilder(fullArgs);
        pb.directory(executionDir);
        pb.inheritIO();
        
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
        
        p.destroyForcibly();

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
	
	/**
	 * Sets an environment variable,
	 * @param variable
	 * the environment variable to be set
	 * @param value
	 * the desired value of the environment variable
	 * @return
	 * this module for method chaining
	 */
	public ExecuteMainClassInNewJVMModule setEnvVariable(String variable, String value) {
		environmentVariables.put(variable, value);
		return this;
	}
}
