/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.pipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.Callable;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.AThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.threaded.CallableWithReturn;
import se.de.hu_berlin.informatik.utils.threaded.ProcessAndReturnThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.threaded.ProcessAndReturnThreadedFileWalker.Builder;
import se.de.hu_berlin.informatik.utils.tm.pipeframework.APipe;

/**
 * Starts a threaded file walker with a provided callable class on a submitted input path.
 * 
 * @author Simon Heiden
 * 
 * @see ProcessAndReturnThreadedFileWalker
 * @see AThreadedFileWalker
 * @see CallableWithReturn
 * @see Callable
 */
public class ThreadedFileWalkerPipe<B> extends APipe<Path,B> {

	final private String pattern;
	final private int threadCount;
	
	private Class<? extends CallableWithReturn<Path,B>> clazz = null;
	private Object[] clazzConstructorArguments = null;
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	private boolean includeRootDir = false;
	
	private boolean skipAfterFind = false;
	
	/**
	 * Creates a new {@link ThreadedFileWalkerPipe} object with the given parameters. 
	 * @param pattern
	 * the pattern that describes the files that should be processed by this file walker
	 * @param threadCount
	 * sets the number of threads to use
	 */
	public ThreadedFileWalkerPipe(String pattern, int threadCount) {
		super();
		this.pattern = pattern;
		this.threadCount = threadCount;
	}
	
	/**
	 * Sets the callable class with its contructor arguments.
	 * @param callableClass
	 * callable class to be called on every visited file
	 * @param clazzConstructorArguments
	 * arguments that shall be passed to the constructor of the callable class 
	 * @return
	 * this
	 */
	public ThreadedFileWalkerPipe<B> call(Class<? extends CallableWithReturn<Path,B>> callableClass, Object... clazzConstructorArguments) {
		this.clazz = callableClass;
		this.clazzConstructorArguments = clazzConstructorArguments;
		return this;
	}
	
	/**
	 * Enables searching for files.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerPipe<B> searchForFiles() {
		this.searchFiles = true;
		return this;
	}
	
	/**
	 * Enables searching for directories.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerPipe<B> searchForDirectories() {
		this.searchDirectories = true;
		return this;
	}
	
	/**
	 * Skips recursion to subtree after found items.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerPipe<B> skipSubTreeAfterMatch() {
		this.skipAfterFind = true;
		return this;
	}
	
	/**
	 * Includes the root directory in the search.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerPipe<B> includeRootDir() {
		this.includeRootDir = false;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(Path input) {
		//declare a threaded FileWalker
		Builder<B> builder = new Builder<>(pattern);
		if (includeRootDir) {
			builder.includeRootDir();
		}
		if (searchDirectories) {
			builder.searchForDirectories();
		}
		if (searchFiles) {
			builder.searchForFiles();
		}
		if (skipAfterFind) {
			builder.skipSubTreeAfterMatch();
		}
		builder.executor(threadCount);
		builder.call(clazz, clazzConstructorArguments);
		builder.pipe(this);
		
		AThreadedFileWalker walker = builder.build();
		delegateTrackingTo(walker);
		
		//traverse the file tree
		try {
			Files.walkFileTree(input, Collections.emptySet(), Integer.MAX_VALUE, walker);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}
		
		//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
		walker.getExecutorServiceProvider().shutdownAndWaitForTermination();

		return null;
	}

}
