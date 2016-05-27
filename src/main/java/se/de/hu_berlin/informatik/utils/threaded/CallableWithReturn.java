/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * An abstract class that implements the {@link Callable} interface and
 * is enriched with fields for input and output paths. The user has to
 * make sure that input and/or output paths are set before use if needed.
 * 
 * @author Simon Heiden
 * 
 * 
 * @see Callable
 */
public abstract class CallableWithReturn<B> implements Callable<Boolean> {

	/**
	 * The path to an input file.
	 */
	private Path input = null;
	
	APipe<?, B> pipe = null;
	
	/**
	 * Creates a new {@link CallableWithReturn} object with the given path.
	 * @param input
	 * an input path
	 */
	public CallableWithReturn(Path input) {
		super();
		this.input = input;
	}
	
	/**
	 * Creates a new {@link CallableWithReturn} object with no paths set.
	 */
	public CallableWithReturn() {
		super();
	}
	
	/**
	 * @param input
	 * an input path
	 */
	public void setInputPath(Path input) {
		this.input = input;
	}

	/**
	 * @return 
	 * the input path
	 */
	public Path getInputPath() {
		return input;
	}

	public void setPipe(APipe<?, B> pipe) {
		this.pipe = pipe;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		System.out.print(".");
		pipe.submitProcessedItem(processInput(input));
		return true;
	}

	abstract public B processInput(Path input);

}
