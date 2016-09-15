/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import se.de.hu_berlin.informatik.utils.tracking.Trackable;

import static java.nio.file.FileVisitResult.*;

import java.io.IOException;

/**
 * FileVisitor implementation that searches for and returns files
 * whose file paths match the given pattern.
 * 
 * @author Simon Heiden
 * 
 * @see FileVisitor
 */
public class SearchFileOrDirWalker extends Trackable implements FileVisitor<Path> {
	
	final private PathMatcher matcher;
	final private List<Path> matchedPaths;
	final private boolean searchDirectories;
	final private boolean searchFiles;
	final private boolean skipAfterFind;
	
	private boolean isFirst = true;

	private SearchFileOrDirWalker(Builder builder) {
		matcher = builder.matcher;
		searchDirectories = builder.searchDirectories;
		searchFiles = builder.searchFiles;
		skipAfterFind = builder.skipAfterFind;
		isFirst = builder.isFirst;
		
		if (searchDirectories == false && searchFiles == false) {
			throw new IllegalStateException("Define whether files or directories shall be searched.");
		}
		
		this.matchedPaths = new ArrayList<>();
	}
	
	/**
	 * Convenience method to match the given path against the pattern.
	 * @param path
	 * path to be matched against the pattern.
	 * @return
	 * true if path is not null and matches the pattern.
	 */
	private boolean match(Path path) {
		return path != null && matcher.matches(path);
	}
	
	/**
	 * @return
	 * a list of matching files that can be obtained after using the file walker
	 */
	public List<Path> getResult() {
		return matchedPaths;
	}
	
	/* (non-Javadoc)
	 * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {		
		if (attrs.isDirectory()) {
			if (searchDirectories) {
				if (matcher == null) {
					track();
					matchedPaths.add(file);
				} else if (match(file.toAbsolutePath())) {
					track();
					matchedPaths.add(file);
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
					matchedPaths.add(file);
				} else if (match(file.toAbsolutePath())) {
					track();
					matchedPaths.add(file);
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
					track();
					matchedPaths.add(dir);
				} else if (match(dir.toAbsolutePath())) {
					track();
					matchedPaths.add(dir);
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
    
    
    public static class Builder implements se.de.hu_berlin.informatik.utils.miscellaneous.IBuilder<SearchFileOrDirWalker> {
		
		private PathMatcher matcher;
		
		private boolean searchDirectories = false;
		private boolean searchFiles = false;
		private boolean skipAfterFind = false;
		private boolean isFirst = true;
		
		public Builder() {
			super();
		}
		
		/**
		 * Sets a search pattern.
		 * @param pattern
		 * holds a global pattern against which the visited files (more specific: their file names) should be matched
		 * @return
		 * this
		 */
		public Builder pattern(String pattern) {
			this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
			return this;
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

		@Override
		public SearchFileOrDirWalker build() {
			return new SearchFileOrDirWalker(this);
		}
    	
    }
	
}
