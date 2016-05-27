/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

/**
 * {@link AThreadedFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see AThreadedFileWalker
 * @see CallableWithPaths
 */
public class ThreadedFileWalker extends AThreadedFileWalker {
	
	Class<? extends CallableWithPaths<Path,?>> call;
	Class<?>[] typeArgs;
	Object[] clazzConstructorArguments;
	
	/**
	 * Initializes a {@link ThreadedFileWalker} object with the given parameters. 
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
	 * @param threadCount
	 * sets the thread count of the underlying {@link java.util.concurrent.ExecutorService}
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public ThreadedFileWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int threadCount,
			Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
		super(ignoreRootDir, searchDirectories, searchFiles, pattern, threadCount);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
	}
	
	/**
	 * Initializes a {@link ThreadedFileWalker} object with the given parameters. 
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
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 */
	public ThreadedFileWalker(ExecutorService executor, boolean ignoreRootDir,
			boolean searchDirectories, boolean searchFiles, String pattern,
			Class<? extends CallableWithPaths<Path,?>> callableClass, Object... clazzConstructorArguments) {
		super(executor, ignoreRootDir, searchDirectories, searchFiles, pattern);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
	}

	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		System.out.println("\tsubmitting task for: " + file);
		try {
			CallableWithPaths<Path,?> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setInput(fileOrDir);
			getExecutorService().submit(o);
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
	
}
