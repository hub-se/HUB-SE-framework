/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import se.de.hu_berlin.informatik.utils.miscellaneous.IOutputPathGenerator;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.PipeLinker;

/**
 * An abstract class that implements the {@link Callable} interface and
 * is enriched with fields for input objects and output paths. The user has to
 * make sure that input and/or output paths are set before use, if needed.
 * 
 * @author Simon Heiden
 * 
 * @param <A>
 * the type of the input objects
 * @param <T>
 * the type of the return function of the call() method
 * 
 * @see Callable
 */
public abstract class CallableWithPaths<A,T> extends DisruptorEventHandler<A> implements Callable<T> {

	/**
	 * The input object.
	 */
	private A input = null;
	
	/**
	 * The path to an output file.
	 */
	private Path output = null;
	
	private PipeLinker pipeCallback = null;
	
	/**
	 * Creates a new {@link CallableWithPaths} object with no paths set.
	 * @param outputPathGenerator
	 * a generator to automatically create output paths
	 */
	public CallableWithPaths(IOutputPathGenerator<Path> outputPathGenerator) {
		super();
		output = outputPathGenerator.getNewOutputPath();
	}
	
	/**
	 * Creates a new {@link CallableWithPaths} object with no paths set and no
	 * output path generator attached.
	 */
	public CallableWithPaths() {
		super();
	}
	
	/**
	 * Creates a new {@link CallableWithPaths} object with no paths set.
	 * @param outputPathGenerator
	 * a generator to automatically create output paths
	 * @param pipeCallback
	 * a callback object that may for example be used to submit items to
	 */
	public CallableWithPaths(IOutputPathGenerator<Path> outputPathGenerator, PipeLinker pipeCallback) {
		this(pipeCallback);
		output = outputPathGenerator.getNewOutputPath();
	}
	
	/**
	 * Creates a new {@link CallableWithPaths} object with no paths set and no
	 * output path generator attached.
	 * @param pipeCallback
	 * a callback object that may for example be used to submit items to
	 */
	public CallableWithPaths(PipeLinker pipeCallback) {
		super();
		this.pipeCallback = pipeCallback;
	}
	
	/**
	 * @param input
	 * an input object
	 * @return
	 * this object to enable chaining
	 */
	public CallableWithPaths<A,T> setInput(A input) {
		this.input = input;
		return this;
	}

	/**
	 * @return 
	 * the input object
	 */
	public A getInput() {
		return input;
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
	
	/**
	 * @return 
	 * the PipeLinker callback object, or null it isn't set
	 */
	public PipeLinker getCallback() {
		if (pipeCallback != null) {
			return pipeCallback;
		} else {
			Log.err(this, "No PipeLinker callback available.");
			return null;
		}
	}

	@Override
	public void processEvent(A input) throws Exception {
		this.input = input;
		call();
	}
	
}
