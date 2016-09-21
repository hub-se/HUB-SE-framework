/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import se.de.hu_berlin.informatik.utils.miscellaneous.IOutputPathGenerator;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

/**
 * An abstract class that implements the {@link Callable} interface and
 * is enriched with fields for input objects and output paths. The user has to
 * make sure that input and/or output paths are set before use, if needed.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * 
 * @see Callable
 */
public abstract class CallableWithInput<A> extends DisruptorFCFSEventHandler<A> implements Callable<Boolean> {

	/**
	 * The input object.
	 */
	private A input = null;
	
	/**
	 * The path to an output file.
	 */
	private Path output = null;
	
	/**
	 * Creates a new {@link CallableWithInput} object with no paths set.
	 * @param outputPathGenerator
	 * a generator to automatically create output paths
	 */
	public CallableWithInput(IOutputPathGenerator<Path> outputPathGenerator) {
		super();
		output = outputPathGenerator.getNewOutputPath();
	}
	
	/**
	 * Creates a new {@link CallableWithInput} object with no paths set and no
	 * output path generator attached.
	 */
	public CallableWithInput() {
		super();
	}
	
	/**
	 * Processes a single item of type A and returns a boolean value.
	 * Has to be instantiated by implementing classes.
	 * @param input
	 * the input item
	 * @return
	 * true if successful, false otherwise
	 */
	abstract public boolean processInput(A input);
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		return processInput(input);
	}
	
	/**
	 * @param input
	 * an input object
	 * @return
	 * this object to enable chaining
	 */
	public CallableWithInput<A> setInput(A input) {
		this.input = input;
		return this;
	}

	/**
	 * @return 
	 * the output path
	 */
	public Path getOutputPath() {
		if (output != null) {
			return output;
		} else {
			Log.err(this, "No output path available.");
			return null;
		}
	}

	@Override
	public void processEvent(A input) throws Exception {
		resetAndInit();
		this.input = input;
		call();
	}
	
	/**
	 * Should be used to reset or to initialize fields. Gets called before processing each event.
	 */
	abstract public void resetAndInit(); 
}
