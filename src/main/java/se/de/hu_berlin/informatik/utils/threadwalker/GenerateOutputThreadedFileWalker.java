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
 * @see AThreadedFileWalker
 * @see CallableWithPaths
 */
public class GenerateOutputThreadedFileWalker extends AThreadedFileWalker {
	
	Class<? extends CallableWithPaths<Path,?>> call;
	Class<?>[] typeArgs;
	Object[] clazzConstructorArguments;
	String extension;
	
	IOutputPathGenerator<Path> generator;
	boolean generateOutputPaths = false;
	
	/**
	 * Initializes a {@link GenerateOutputThreadedFileWalker} object with the given parameters. 
	 * Will fail if the output directory already exists. Automatically generates output paths.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
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
	public GenerateOutputThreadedFileWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, Path outputdir, String extension, int maxThreadCount,
			boolean overwrite, Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
		super(ignoreRootDir, searchDirectories, searchFiles, pattern, maxThreadCount);
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
	 * Initializes a {@link GenerateOutputThreadedFileWalker} object with the given parameters. 
	 * Will not create any output directories and will not set any output paths while visiting a file.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
	 * @param maxThreadCount
	 * sets the maximum thread count of the underlying {@link java.util.concurrent.ExecutorService}
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class
	 */
	public GenerateOutputThreadedFileWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int maxThreadCount, Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
		this(ignoreRootDir, searchDirectories, searchFiles, pattern, null, null, maxThreadCount, false, callableClass, clazzConstructorArguments);
	}
	
	/**
	 * Initializes a {@link GenerateOutputThreadedFileWalker} object with the given parameters. 
	 * Will fail if the output directory already exists. Automatically generates output paths.
	 * @param executor
	 * an executor service that shall be used
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
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
	public GenerateOutputThreadedFileWalker(ExecutorService executor, boolean ignoreRootDir,
			boolean searchDirectories, boolean searchFiles, String pattern, Path outputdir, String extension,
			boolean overwrite, Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
		super(executor, ignoreRootDir, searchDirectories, searchFiles, pattern);
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
	 * Initializes a {@link GenerateOutputThreadedFileWalker} object with the given parameters. 
	 * Will not create any output directories and will not set any output paths while visiting a file.
	 * @param executor
	 * an executor service that shall be used
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class
	 */
	public GenerateOutputThreadedFileWalker(ExecutorService executor, boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
		this(executor, ignoreRootDir, searchDirectories, searchFiles, pattern, null, null, false, callableClass, clazzConstructorArguments);
	}
	

	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		System.out.println("\tsubmitting task for: " + file);
		try {
			CallableWithPaths<Path,?> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setInput(fileOrDir);
			if (generateOutputPaths()) {
				o.setOutputPath(getOutputPathGenerator().getNewOutputPath(fileOrDir, extension));
			}
			getExecutor().submit(o);
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
