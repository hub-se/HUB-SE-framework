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
	
	private PathMatcher matcher = null;
	
	private List<Path> matchedPaths;
	
	private boolean searchDirectories = false;
	private boolean searchFiles = false;
	
	private boolean skipAfterFind = false;
	
	private boolean isFirst = true;

	/**
	 * Creates a new {@link SearchFileOrDirWalker} object with the given pattern.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * pattern to match the file paths against, for example "**&#47;*.{txt}"
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 */
	public SearchFileOrDirWalker(boolean ignoreRootDir, boolean searchForDirectories, boolean searchForFiles, 
			String pattern, boolean skipAfterFind) {
		this(ignoreRootDir, searchForDirectories, searchForFiles, skipAfterFind);
		this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object that returns ALL visited
	 * files.
	 * @param ignoreRootDir
	 * whether the root directory should be ignored
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 */
	public SearchFileOrDirWalker(boolean ignoreRootDir, boolean searchForDirectories, boolean searchForFiles, 
			boolean skipAfterFind) {
		super();
		if (!searchForDirectories && !searchForFiles) {
			Log.abort(this, "Neither searching for files nor for directories.");
		}
		this.searchDirectories = searchForDirectories;
		this.searchFiles = searchForFiles;
		this.skipAfterFind = skipAfterFind;
		
		this.matchedPaths = new ArrayList<>();
		
		if (!ignoreRootDir) {
			isFirst = false;
		}
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object with the given pattern. Ignores
	 * the root directory.
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param pattern
	 * pattern to match the file names against, for example "*.{txt}"
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 */
	public SearchFileOrDirWalker(boolean searchForDirectories, boolean searchForFiles, 
			String pattern, boolean skipAfterFind) {
		this(true, searchForDirectories, searchForFiles, pattern, skipAfterFind);
	}
	
	/**
	 * Creates a new {@link SearchFileOrDirWalker} object that returns ALL visited
	 * files. Ignores the root directory.
	 * @param searchForDirectories 
	 * whether files shall be included in the search
	 * @param searchForFiles 
	 * whether directories shall be included in the search
	 * @param skipAfterFind
	 * whether to skip subtree elements after a match (will only affect matching directories)
	 */
	public SearchFileOrDirWalker(boolean searchForDirectories, boolean searchForFiles, 
			boolean skipAfterFind) {
		this(true, searchForDirectories, searchForFiles, skipAfterFind);
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
	
}
