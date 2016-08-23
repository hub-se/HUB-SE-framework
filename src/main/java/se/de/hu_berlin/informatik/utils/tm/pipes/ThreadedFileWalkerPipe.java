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
import se.de.hu_berlin.informatik.utils.threaded.CallableWithPaths;
import se.de.hu_berlin.informatik.utils.threaded.CallableWithReturn;
import se.de.hu_berlin.informatik.utils.threaded.ProcessAndReturnThreadedFileWalker;
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

	private String pattern;
	int threadCount;
	private Class<? extends CallableWithReturn<B>> clazz;
	private Object[] clazzConstructorArguments;
	
	private boolean searchDirectories;
	private boolean searchFiles;
	private boolean ignoreRootDir;
	
	private boolean skipAfterFind = false;
	
	/**
	 * Creates a new {@link ThreadedFileWalkerPipe} object with the given parameters. 
	 * Doesn't create any output paths and assumes that no output is created during execution.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * the pattern that describes the files that should be processed by this file walker
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 * @param threadCount
	 * the number of threads that shall be run in parallel
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedFileWalkerPipe(boolean ignoreRootDir, boolean searchForDirectories, boolean searchForFiles, 
			String pattern,  boolean skipAfterFind, int threadCount, 
			Class<? extends CallableWithReturn<B>> clazz, Object... clazzConstructorArguments) {
		super();
		this.pattern = pattern;
		this.threadCount = threadCount;
		this.clazz = clazz;
		this.clazzConstructorArguments = clazzConstructorArguments;
		
		this.searchDirectories = searchForDirectories;
		this.searchFiles = searchForFiles;
		this.ignoreRootDir = ignoreRootDir;
		
		this.skipAfterFind = skipAfterFind;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public B processItem(Path input) {
		//declare a threaded FileWalker
		AThreadedFileWalker walker = new ProcessAndReturnThreadedFileWalker<B>(this, ignoreRootDir, 
				searchDirectories, searchFiles, pattern, skipAfterFind, threadCount, clazz, clazzConstructorArguments);
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
