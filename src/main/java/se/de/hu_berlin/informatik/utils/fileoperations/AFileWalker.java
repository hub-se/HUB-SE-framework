/**
 * 
 */
package se.de.hu_berlin.informatik.utils.fileoperations;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import se.de.hu_berlin.informatik.utils.miscellaneous.IBuilder;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;
import se.de.hu_berlin.informatik.utils.tracking.TrackingStrategy;
import se.de.hu_berlin.informatik.utils.tracking.TrackerDummy;

/**
 * Extendable {@link FileVisitor} implementation.
 * 
 * @author Simon Heiden
 * 
 * @see FileVisitor
 */
public abstract class AFileWalker implements FileVisitor<Path>, Trackable {
	
	final private PathMatcher matcher;
	final private boolean searchDirectories;
	final private boolean searchFiles;
	final private boolean skipAfterFind;
	
	final private boolean relative;
	private Path relativeStartingPath;
	
	private int matchCount = 0;
	
	private boolean isFirst;
	private TrackingStrategy tracker = TrackerDummy.getInstance();
	
	protected AFileWalker(Builder builder) {
		matcher = builder.matcher;
		searchDirectories = builder.searchDirectories;
		searchFiles = builder.searchFiles;
		skipAfterFind = builder.skipAfterFind;
		isFirst = builder.isFirst;
		relative = builder.relative;
		
		if (searchDirectories == false && searchFiles == false) {
			throw new IllegalStateException("Define whether files or directories shall be searched.");
		}
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
	
	public int getNumberOfMatches() {
		return matchCount;
	}
	
	abstract public void processMatchedFileOrDir(Path fileOrDir);
	
	/* (non-Javadoc)
	 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		if (relativeStartingPath == null) {
			relativeStartingPath = file.getParent();
		}
		if (attrs.isDirectory()) {
			if (searchDirectories) {
				if (matcher == null) {
					track();
					if (relative) {
						file = relativeStartingPath.relativize(file);
					}
					processMatchedFileOrDir(file);
					++matchCount;
				} else if (match(file.toAbsolutePath())) {
					track();
					if (relative) {
						file = relativeStartingPath.relativize(file);
					}
					processMatchedFileOrDir(file);
					++matchCount;
//					Misc.out(file.toString());
					if (skipAfterFind) {
						return FileVisitResult.SKIP_SUBTREE;
					}
				}
			}
		} else {
			if (searchFiles) {
				if (matcher == null) {
					track();
					if (relative) {
						file = relativeStartingPath.relativize(file);
					}
					processMatchedFileOrDir(file);
					++matchCount;
				} else if (match(file.toAbsolutePath())) {
					track();
					if (relative) {
						file = relativeStartingPath.relativize(file);
					}
					processMatchedFileOrDir(file);
					++matchCount;
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
		if (relativeStartingPath == null) {
			relativeStartingPath = dir;
		}
		if (!isFirst) {
			if (searchDirectories) {
				if (matcher == null) {
					track();
					if (relative) {
						dir = relativeStartingPath.relativize(dir);
					}
					processMatchedFileOrDir(dir);
					++matchCount;
				} else if (match(dir.toAbsolutePath())) {
					track();
					if (relative) {
						dir = relativeStartingPath.relativize(dir);
					}
					processMatchedFileOrDir(dir);
					++matchCount;
//					Misc.out(dir.toString());
					if (skipAfterFind) {
						return FileVisitResult.SKIP_SUBTREE;
					}
				}
			}
		} else {
			isFirst = false;
		}
		return CONTINUE;
	}
	

    /**
     * Invoked for a file that could not be visited.
     *
     * <p> Unless overridden, this method re-throws the I/O exception that prevented
     * the file from being visited.
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
        throws IOException
    {
        Objects.requireNonNull(file);
        throw exc;
    }

    /**
     * Invoked for a directory after entries in the directory, and all of their
     * descendants, have been visited.
     *
     * <p> Unless overridden, this method returns {@link FileVisitResult#CONTINUE
     * CONTINUE} if the directory iteration completes without an I/O exception;
     * otherwise this method re-throws the I/O exception that caused the iteration
     * of the directory to terminate prematurely.
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
        throws IOException
    {
        Objects.requireNonNull(dir);
        if (exc != null)
            throw exc;
        return FileVisitResult.CONTINUE;
    }
    
    @Override
	public TrackingStrategy getTracker() {
		return tracker;
	}

	@Override
	public void setTracker(TrackingStrategy tracker) {
		this.tracker = tracker;
	}

	/**
	 * @author SimHigh
	 *
	 */
	/**
	 * @author SimHigh
	 *
	 */
	public static abstract class Builder implements IBuilder<AFileWalker> {
		
		private final PathMatcher matcher;
		
		private boolean searchDirectories = false;
		private boolean searchFiles = false;
		private boolean skipAfterFind = false;
		private boolean isFirst = true;
		
		private boolean relative = false;
		
		/**
		 * Creates an {@link Builder} object with the given parameters.
		 * @param pattern
		 * holds a global pattern against which the visited files (more specific: their file names) should be matched
		 */
		public Builder(String pattern) {
			super();
			if (pattern == null) {
				this.matcher = null;
			} else {
				this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
			}
		}
		
		/**
		 * Enables searching for files.
		 * @return
		 * this
		 */
		public Builder searchForFiles() {
			this.searchFiles = true;
			return this;
		}
		
		
		/**
		 * Puts out paths relative to the input path.
		 * @return
		 * this
		 */
		public Builder relative() {
			this.relative = true;
			return this;
		}
		
		/**
		 * Enables searching for directories.
		 * @return
		 * this
		 */
		public Builder searchForDirectories() {
			this.searchDirectories = true;
			return this;
		}
		
		/**
		 * Skips recursion to subtree after found items.
		 * @return
		 * this
		 */
		public Builder skipSubTreeAfterMatch() {
			this.skipAfterFind = true;
			return this;
		}
		
		/**
		 * Includes the root directory in the search.
		 * @return
		 * this
		 */
		public Builder includeRootDir() {
			isFirst = false;
			return this;
		}
    	
    }
    
}
