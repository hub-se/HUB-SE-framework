/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threaded;

import java.nio.file.*;
import java.lang.reflect.InvocationTargetException;

import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * {@link AThreadedFileWalker} extension that takes a callable class 
 * and calls it on every file visited that matches the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see AThreadedFileWalker
 * @see CallableWithReturn
 */
public class ProcessAndReturnThreadedFileWalker<B> extends AThreadedFileWalker {
	
	private Class<? extends CallableWithReturn<B>> call;
	private Class<?>[] typeArgs;
	private Object[] clazzConstructorArguments;
	private APipe<?, B> pipe;

	
	/**
	 * Initializes a {@link ProcessAndReturnThreadedFileWalker} object with the given parameters.
	 * @param pipe
	 * the pipe object that is associated with this file walker
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
	public ProcessAndReturnThreadedFileWalker(APipe<?, B> pipe, boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int maxThreadCount,
			Class<? extends CallableWithReturn<B>> callableClass, Object... clazzConstructorArguments) {
		super(ignoreRootDir, searchDirectories, searchFiles, pattern, maxThreadCount);
		this.call = callableClass;
		this.typeArgs = call.getConstructors()[0].getParameterTypes();//TODO is that right?
		this.clazzConstructorArguments = clazzConstructorArguments;
		this.pipe = pipe;
	}
	
	
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.threadwalker.AThreadedFileWalker#processMatchedFileOrDir(java.nio.file.Path)
	 */
	@Override
	public void processMatchedFileOrDir(Path fileOrDir) {
//		Misc.out(this, "\tsubmitting task for: " + fileOrDir);
		try {
			CallableWithReturn<B> o = call.getConstructor(typeArgs).newInstance(clazzConstructorArguments);
			o.setPipe(pipe);
			o.setInputPath(fileOrDir);
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
