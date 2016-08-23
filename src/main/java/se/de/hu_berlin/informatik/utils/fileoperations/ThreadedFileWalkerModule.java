/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.Callable;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.threaded.CallableWithPaths;
import se.de.hu_berlin.informatik.utils.threaded.ThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;

/**
 * Starts a threaded file walker with a provided callable class on a submitted input path.
 * 
 * @author Simon Heiden
 * 
 * @see ThreadedFileWalker
 * @see CallableWithPaths
 * @see Callable
 */
public class ThreadedFileWalkerModule extends AModule<Path,Boolean> {

	private String pattern;
	int threadCount;
	private Class<? extends CallableWithPaths<Path,?>> clazz;
	private Object[] clazzConstructorArguments;
	private boolean searchDirectories;
	private boolean searchFiles;
	private boolean ignoreRootDir;
	
	private boolean skipAfterFind = false;
	
	/**
	 * Creates a new {@link ThreadedFileWalkerModule} object with the given parameters. 
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
	public ThreadedFileWalkerModule(boolean ignoreRootDir, boolean searchForDirectories, boolean searchForFiles,
			String pattern, boolean skipAfterFind, int threadCount,
			Class<? extends CallableWithPaths<Path,?>> clazz, Object... clazzConstructorArguments) {
		super(true);
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
	public Boolean processItem(Path input) {
		//declare a threaded FileWalker
		ThreadedFileWalker walker = new ThreadedFileWalker(ignoreRootDir, searchDirectories, searchFiles, 
					pattern, skipAfterFind, threadCount, clazz, clazzConstructorArguments);
		delegateTrackingTo(walker);
		
		//traverse the file tree
		try {
			Files.walkFileTree(input, Collections.emptySet(), Integer.MAX_VALUE, walker);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}
		
		//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
		return walker.getExecutorServiceProvider().shutdownAndWaitForTermination();
	}

}
