/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threadwalker;

import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.IOutputPathGenerator;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.miscellaneous.OutputPathGenerator;

/**
 * {@link AThreadedFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see AThreadedListProcessor
 * @see CallableWithPaths
 *
 * @param <A>
 * type of objects in the list to process
 */
public class GenerateOutputThreadedListProcessor<A> extends AThreadedListProcessor {
	
	Class<? extends CallableWithPaths<A,?>> call;
	Class<?>[] typeArgs;
	Object[] clazzConstructorArguments;
	String extension;
	
	IOutputPathGenerator<Path> generator;
	boolean generateOutputPaths = false;
	
	/**
	 * Initializes a {@link GenerateOutputThreadedListProcessor} object with the given parameters. 
	 * Will fail if the output directory already exists. Automatically generates output paths.
	 * @param outputdir
	 * holds the path to the output directory
	 * @param extension
	 * holds an extension for the files to create (e.g. ".{txt}")
	 * @param maxThreadCount
	 * sets the maximum thread count of the underlying {@link java.util.concurrent.ExecutorService}
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public GenerateOutputThreadedListProcessor(Path outputdir, String extension, int maxThreadCount,
			boolean overwrite, Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		super(maxThreadCount);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
		this.extension = extension;
		
		if (outputdir != null) {
			this.generateOutputPaths = true;
			this.generator = new OutputPathGenerator(outputdir, overwrite);
		}
	}
	
	/**
	 * Initializes a {@link GenerateOutputThreadedListProcessor} object with the given parameters. 
	 * Will not create any output directories and will not set any output paths while processing an object.
	 * @param maxThreadCount
	 * sets the maximum thread count of the underlying {@link java.util.concurrent.ExecutorService}
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class
	 */
	public GenerateOutputThreadedListProcessor(int maxThreadCount, 
			Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		this(null, null, maxThreadCount, false, callableClass, clazzConstructorArguments);
	}
	
	/**
	 * Initializes a {@link GenerateOutputThreadedListProcessor} object with the given parameters. 
	 * Will fail if the output directory already exists. Automatically generates output paths.
	 * @param executor
	 * an executor service that shall be used
	 * @param outputdir
	 * holds the path to the output directory
	 * @param extension
	 * holds an extension for the files to create (e.g. ".{txt}")
	 * @param overwrite
	 * determines if files and directories should be overwritten
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public GenerateOutputThreadedListProcessor(ExecutorService executor, Path outputdir, String extension,
			boolean overwrite, Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		super(executor);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
		this.extension = extension;
		
		if (outputdir != null) {
			this.generateOutputPaths = true;
			this.generator = new OutputPathGenerator(outputdir, overwrite);
		}
	}
	
	/**
	 * Initializes a {@link GenerateOutputThreadedListProcessor} object with the given parameters. 
	 * Will not create any output directories and will not set any output paths while visiting a file.
	 * @param executor
	 * an executor service that shall be used
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class
	 */
	public GenerateOutputThreadedListProcessor(ExecutorService executor,
			Class<? extends CallableWithPaths<A,?>> callableClass, Object... clazzConstructorArguments) {
		this(executor, null, null, false, callableClass, clazzConstructorArguments);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void processElement(Object inputObject) {
//		System.out.println("\tsubmitting task for: " + file);
		try {
			CallableWithPaths<A,?> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setInput((A) inputObject);
			if (generateOutputPaths()) {
				o.setOutputPath(getOutputPathGenerator().getNewOutputPath(extension));
			}
			getExecutor().submit(o);
		} catch (ClassCastException e) {
			Misc.abort(this, e, "Input type mismatch!");
		} catch (InstantiationException e) {
			Misc.err(this, e, "Cannot instantiate object %s.", call.getSimpleName());
		} catch (IllegalAccessException e) {
			Misc.err(this, e, "Illegal access to object %s.", call.getSimpleName());
		} catch (IllegalArgumentException e) {
			Misc.abort(this, e, "Illegal argument to object %s.", call.getSimpleName());
		} catch (InvocationTargetException e) {
			Misc.err(this, e, "Invocation target exception on object %s.", call.getSimpleName());
		} catch (NoSuchMethodException e) {
			Misc.abort(this, e, "No such method exception on object %s.", call.getSimpleName());
		} catch (SecurityException e) {
			Misc.err(this, e, "Security exception on object %s.", call.getSimpleName());
		}
	}

	/**
	 * @return
	 * the output path generator
	 */
	public IOutputPathGenerator<Path> getOutputPathGenerator() {
		return generator;
	}
	
	/**
	 * @return
	 * if output paths shall be automatically generated
	 */
	public boolean generateOutputPaths() {
		return generateOutputPaths;
	}
	
}
