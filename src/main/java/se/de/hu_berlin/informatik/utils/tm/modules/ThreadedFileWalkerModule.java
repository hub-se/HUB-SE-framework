/**
 * 
 */
package se.de.hu_berlin.informatik.utils.tm.modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.Callable;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;
import se.de.hu_berlin.informatik.utils.threadwalker.ThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.tm.moduleframework.AModule;
import se.de.hu_berlin.informatik.utils.threadwalker.CallableWithPaths;

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
	
	/**
	 * Creates a new {@link ThreadedFileWalkerModule} object with the given parameters. 
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * the pattern that describes the files that should be processed by this file walker
	 * @param threadCount
	 * the number of threads that shall be run in parallel
	 * @param clazz
	 * a {@link CallableWithPaths} class which is called for every matching file
	 * @param clazzConstructorArguments
	 * arguments that might be needed in the constructor of the callable class
	 */
	public ThreadedFileWalkerModule(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int threadCount,
			Class<? extends CallableWithPaths<Path,?>> clazz, Object... clazzConstructorArguments) {
		super(true);
		this.pattern = pattern;
		this.threadCount = threadCount;
		this.clazz = clazz;
		this.clazzConstructorArguments = clazzConstructorArguments;
		
		this.searchDirectories = searchDirectories;
		this.searchFiles = searchFiles;
		this.ignoreRootDir = ignoreRootDir;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(Path input) {
		//declare a threaded FileWalker
		ThreadedFileWalker walker = new ThreadedFileWalker(ignoreRootDir, searchDirectories, searchFiles, 
					pattern, threadCount, clazz, clazzConstructorArguments);
		
		//traverse the file tree
		try {
			Files.walkFileTree(input, Collections.emptySet(), Integer.MAX_VALUE, walker);
		} catch (IOException e) {
			Misc.abort(this, e, "IOException thrown.");
		}
		
		//we are done! Shutdown of the executor service is necessary! (That means: No new task submissions!)
		return walker.getExecutorServiceProvider().shutdownAndWaitForTermination();
	}

}
