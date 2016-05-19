/**
 * 
 */
package se.de.hu_berlin.informatik.utils.threadwalker;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * {@link SimpleFileVisitor} extension that is enriched with a blocking {@link ExecutorService}.
 * 
 * @author Simon Heiden
 */
public abstract class AThreadedFileWalker extends SimpleFileVisitor<Path> {
	
	final private PathMatcher matcher;

	final private ExecutorServiceProvider executor;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	
	private boolean isFirst = true;
	
	/**
	 * Creates an {@link AThreadedFileWalker} object with the given parameters.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 * @param keepAliveTime
	 * when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
	 * @param unit the time unit for the keepAliveTime argument
	 */
	public AThreadedFileWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
		super();
		this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		//create an executor service
		this.executor = new ExecutorServiceProvider(corePoolSize, maximumPoolSize, keepAliveTime, unit);
		
		this.searchDirectories = searchDirectories;
		this.searchFiles = searchFiles;
		
		if (!ignoreRootDir) {
			isFirst = false;
		}
	}
	
	/**
	 * Creates an {@link AThreadedFileWalker} object with the given parameters,
	 * {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
	 * @param corePoolSize
	 * the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
	 * @param maximumPoolSize
	 * the maximum number of threads to allow in the pool
	 */
	public AThreadedFileWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int corePoolSize, int maximumPoolSize) {
		this(ignoreRootDir, searchDirectories, searchFiles, pattern, corePoolSize, maximumPoolSize, 1L, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates an {@link AThreadedFileWalker} object with the given parameters,
	 * {@code corePoolSize=1}, {@code keepAliveTime=1L} and {@code unit=TimeUnit.SECONDS}.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchDirectories 
	 * whether files shall be included in the search
	 * @param searchFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * holds a global pattern against which the visited files (more specific: their file names) should be matched
	 * @param poolSize
	 * the number of threads to run in the pool
	 */
	public AThreadedFileWalker(boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern, int poolSize) {
		this(ignoreRootDir, searchDirectories, searchFiles, pattern, poolSize, poolSize, 1L, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates an {@link AThreadedFileWalker} object with the given parameters.
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
	 */
	public AThreadedFileWalker(ExecutorService executor, boolean ignoreRootDir, boolean searchDirectories, boolean searchFiles,
			String pattern) {
		super();
		this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		//create an executor service
		this.executor = new ExecutorServiceProvider(executor);
		
		this.searchDirectories = searchDirectories;
		this.searchFiles = searchFiles;
		
		if (!ignoreRootDir) {
			isFirst = false;
		}
	}

	/**
	 * @return
	 * the executor service
	 */
	public ExecutorService getExecutorService() {
		return executor.getExecutorService();
	}

	/**
	 * @return
	 * the executor service provider
	 */
	public ExecutorServiceProvider getExecutorServiceProvider() {
		return executor;
	}
	
	/**
	 * @return 
	 * the path matcher
	 */
	protected PathMatcher getMatcher() {
		return matcher;
	}
	
	/**
	 * Convenience method to be used in actual implementations of this class
	 * @param path
	 * Path to be matched against the global pattern.
	 * @return
	 * true if path is not null and matches the global pattern.
	 */
	protected boolean match(Path path) {
		return path != null && matcher.matches(path);
	}
	
	
	abstract public void processMatchedFileOrDir(Path fileOrDir);
	
	/* (non-Javadoc)
	 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {		
		if (attrs.isDirectory()) {
			if (searchDirectories) {
				if (matcher == null) {
					processMatchedFileOrDir(file);
				} else if (match(file.toAbsolutePath())) {
					processMatchedFileOrDir(file);
//					Misc.out(file.toString());
				}
			}
		} else {
			if (searchFiles) {
				if (matcher == null) {
					processMatchedFileOrDir(file);
				} else if (match(file.toAbsolutePath())) {
					processMatchedFileOrDir(file);
//					Misc.out(file.toString());
				}
			}
		}
		return CONTINUE;
	}

	/* (non-Javadoc)
	 * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (!isFirst) {
			if (searchDirectories) {
				if (matcher == null) {
					processMatchedFileOrDir(dir);
				} else if (match(dir.toAbsolutePath())) {
					processMatchedFileOrDir(dir);
//					Misc.out(dir.toString());
				}
			}
		} else {
			isFirst = false;
		}
		return CONTINUE;
	}
}
