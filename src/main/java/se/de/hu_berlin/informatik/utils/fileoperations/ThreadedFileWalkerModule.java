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
import se.de.hu_berlin.informatik.utils.threaded.AThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.threaded.CallableWithPaths;
import se.de.hu_berlin.informatik.utils.threaded.IDisruptorEventHandlerFactory;
import se.de.hu_berlin.informatik.utils.threaded.ThreadedFileWalker;
import se.de.hu_berlin.informatik.utils.threaded.ThreadedFileWalker.Builder;
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

	final private String pattern;
	final private int threadCount;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	private boolean includeRootDir = false;
	
	private boolean skipAfterFind = false;
	private IDisruptorEventHandlerFactory<Path> callableFactory;
	
	/**
	 * Creates a new {@link ThreadedFileWalkerModule} object with the given parameters. 
	 * @param pattern
	 * the pattern that describes the files that should be processed by this file walker
	 * @param threadCount
	 * the number of threads that shall be run in parallel
	 */
	public ThreadedFileWalkerModule(String pattern, int threadCount) {
		super(true, false);
		this.pattern = pattern;
		this.threadCount = threadCount;
	}
	
	/**
	 * Sets the factory.
	 * @param callableFactory
	 * a factory that provides instances of callable classes 
	 * @return
	 * this
	 */
	public ThreadedFileWalkerModule call(IDisruptorEventHandlerFactory<Path> callableFactory) {
		this.callableFactory = callableFactory;
		return this;
	}
	
	/**
	 * Enables searching for files.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerModule searchForFiles() {
		this.searchFiles = true;
		return this;
	}
	
	/**
	 * Enables searching for directories.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerModule searchForDirectories() {
		this.searchDirectories = true;
		return this;
	}
	
	/**
	 * Skips recursion to subtree after found items.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerModule skipSubTreeAfterMatch() {
		this.skipAfterFind = true;
		return this;
	}
	
	/**
	 * Includes the root directory in the search.
	 * @return
	 * this
	 */
	public ThreadedFileWalkerModule includeRootDir() {
		this.includeRootDir = false;
		return this;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	public Boolean processItem(Path input) {
		//declare a threaded FileWalker
		Builder builder = new Builder(pattern, threadCount);
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
		builder.call(callableFactory);
		
		AThreadedFileWalker walker = builder.build();
		delegateTrackingTo(walker);
		
		//traverse the file tree
		try {
			Files.walkFileTree(input, Collections.emptySet(), Integer.MAX_VALUE, walker);
		} catch (IOException e) {
			Log.abort(this, e, "IOException thrown.");
		}
		
		//we are done! Shutdown is necessary!
		walker.getDisruptorProvider().shutdown();
		return true;
	}

}
