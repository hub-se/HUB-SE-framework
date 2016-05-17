/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threadwalker;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import se.de.hu_berlin.informatik.utils.miscellaneous.IOutputPathGenerator;

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
public abstract class CallableWithPaths<A,T> implements Callable<T> {

	/**
	 * The input object.
	 */
	private A input = null;
	
	/**
	 * The path to an output file.
	 */
	private Path output = null;
	
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
	 * @param input
	 * an input object
	 */
	public void setInput(A input) {
		this.input = input;
	}

	/**
	 * @return 
	 * the input object
	 */
	public A getInput() {
		return input;
	}

//	/**
//	 * @param output
//	 * an output path
//	 */
//	public void setOutputPath(Path output) {
//		this.output = output;
//	}
	
	/**
	 * @return 
	 * the output path
	 */
	public Path getOutputPath() {
		return output;
	}

}
